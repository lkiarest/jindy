package com.qintx.myjindy.content.manager;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.qintx.myjindy.constant.Constants;
import com.qintx.myjindy.content.entity.Article;
import com.qintx.myjindy.http.HttpUtil;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ArticleService extends Service {

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
            final String channelUrl = intent.getStringExtra(Constants.INTENT_CHANNEL_URL);
            Log.d(TAG, "channelUrl is  : " + channelUrl);
            if ((channelUrl != null) && (!channelUrl.isEmpty())) {
                new Thread() {
                    public void run() {
                        requestArticles(channelUrl);
                    }
                }.start();
            }
        } else if (Constants.ACTION_GET_ARTICLE_CONTENT.equals(action)) {
            final String articalUrl = intent.getStringExtra(Constants.INTENT_ARTICLE_URL);
            if ((articalUrl != null) && (!articalUrl.isEmpty())) {
                new Thread() {
                    public void run() {
                        requestArticleContent(articalUrl);
                    }
                }.start();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void requestArticleContent(final String articleUrl) {
        HttpUtil client = new HttpUtil();
        String content = client.getContentAsString(articleUrl);
        Intent intent = new Intent(Constants.ACTION_GET_ARTICLE_CONTENT_RESULT);
        if (content != null) {
            addArticle(articleUrl, filterArticleContent(content));
            intent.putExtra(Constants.ACTION_RESULT, true);
            intent.putExtra(Constants.INTENT_ARTICLE_URL, articleUrl);
            sendBroadcast(intent);
        } else {
            intent.putExtra(Constants.ACTION_RESULT, false);
            intent.putExtra(Constants.INTENT_ARTICLE_URL, articleUrl);
            sendBroadcast(intent);
        }
    }

    private void requestArticles(String channelUrl) {
        HttpUtil client = new HttpUtil();
        String content = client.getContentAsString(channelUrl);
        Intent intent = new Intent(Constants.ACTION_GET_ARTICLES_RESULT);
        if (content != null) {
            managerArticles(channelUrl, content);
            intent.putExtra(Constants.ACTION_RESULT, true);
            intent.putExtra(Constants.INTENT_CHANNEL_URL, channelUrl);
            this.sendBroadcast(intent);
        } else {
            intent.putExtra(Constants.ACTION_RESULT, false);
            intent.putExtra(Constants.INTENT_CHANNEL_URL, channelUrl);
            this.sendBroadcast(intent);
        }
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
}
