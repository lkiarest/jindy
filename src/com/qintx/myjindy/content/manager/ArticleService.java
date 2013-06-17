package com.qintx.myjindy.content.manager;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.qintx.myjindy.constant.Constants;
import com.qintx.myjindy.content.entity.Article;
import com.qintx.myjindy.http.HttpUtil;
import com.qintx.myjindy.http.HttpUtil.HttpCallback;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ArticleService extends Service implements HttpCallback {

    private static final String TAG = "ArticleService";
    private ArticleManager mgr = ArticleManager.getInstance();

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        Log.d(TAG, "start command : " + action);
        if (Constants.ACTION_GET_ARTICLES.equals(action)) {
            String channelUrl = intent.getStringExtra(Constants.INTENT_CHANNEL_URL);
            Log.d(TAG, "channelUrl is  : " + channelUrl);
            if ((channelUrl != null) && (!channelUrl.isEmpty())) {
                requestArticles(channelUrl);
            }
        } else if (Constants.ACTION_GET_ARTICLE_CONTENT.equals(action)) {
            String articalUrl = intent.getStringExtra(Constants.INTENT_ARTICLE_URL);
            if ((articalUrl != null) && (!articalUrl.isEmpty())) {
                requestArticleContent(articalUrl);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void requestArticleContent(final String articleUrl) {
        HttpUtil client = new HttpUtil();
        client.ajaxGet(articleUrl, new ArticleCallback());
    }

    private void requestArticles(String channelUrl) {
        HttpUtil client = new HttpUtil();
        client.ajaxGet(channelUrl, this);
    }

    private List<Article> filterArticleList(String html) {
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select(".f14 a");
        if (elements == null) {
            return null;
        }

        List<Article> articles = new ArrayList<Article>();

        for (int i = 0; i < elements.size(); i++) {
            String link = elements.get(i).attr("href");
            String title = elements.get(i).text();
            Article article = new Article(title, link);
            articles.add(article);
        }

        return articles;
    }
    
    private String filterArticleContent(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        Document doc = Jsoup.parse(content);
        Elements elements = doc.select("#Article .content");
        if (elements == null) {
            return null;
        }

        return elements.get(0).html();
    }

    private void managerArticles(String url, String msg) {
        if (msg == null) {
            return;
        }
        List<Article> articles = filterArticleList(msg);
        if (articles == null) {
            return;
        }
        mgr.clearArticleMap(url);
        mgr.addArticleList(url, articles);
    }

    private void addArticle(String url, String msg) {
        Article article = new Article();
        article.setUrl(url);
        article.setContent(msg);
        mgr.addArticle(url, article);
    }

    @Override
    public void onCallback(boolean success, String url, String msg) {
        Intent intent = new Intent(Constants.ACTION_GET_ARTICLES_RESULT);
        if (success) {
            managerArticles(url, msg);
            intent.putExtra(Constants.ACTION_RESULT, true);
            intent.putExtra(Constants.INTENT_CHANNEL_URL, url);
            this.sendBroadcast(intent);
        } else {
            intent.putExtra(Constants.ACTION_RESULT, false);
            intent.putExtra(Constants.INTENT_CHANNEL_URL, url);
            this.sendBroadcast(intent);
        }
    }

    private class ArticleCallback implements HttpCallback {

        @Override
        public void onCallback(boolean success, String url, String msg) {
            Intent intent = new Intent(Constants.ACTION_GET_ARTICLE_CONTENT_RESULT);
            if (success) {
                addArticle(url, filterArticleContent(msg));
                intent.putExtra(Constants.ACTION_RESULT, true);
                intent.putExtra(Constants.INTENT_ARTICLE_URL, url);
                sendBroadcast(intent);
            } else {
                intent.putExtra(Constants.ACTION_RESULT, false);
                intent.putExtra(Constants.INTENT_ARTICLE_URL, url);
                sendBroadcast(intent);
            }
        }
    }
}
