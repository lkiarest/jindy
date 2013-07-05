package com.qintx.myjindy.content.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qintx.myjindy.content.entity.Article;

public class ArticleManager {

    private static ArticleManager instance;
    
    private Map<String, List<Article>> articleMap;
    
    private Map<String, Article> articleContent;
    
    private ArticleManager() {
        articleMap = new HashMap<String, List<Article>>();
        articleContent = new HashMap<String, Article>();
    }

    public void addArticle(String url, Article article) {
        articleContent.put(url, article);
    }

    public Article getArticle(String url) {
        return articleContent.get(url);
    }

    public void addArticleList(String channel, List<Article> articles) {
        List<Article> arts = articleMap.get(channel);
        if (arts == null) {
            articleMap.put(channel, articles);
        } else {
            arts.addAll(articles);
        }
    }

    public void clearArticleMap(String channel) {
        List<Article> arts = articleMap.get(channel);
        if (arts == null) {
            return;
        } else {
            arts.clear();
        }
    }

    public List<Article> getArticles(String channel) {
        return articleMap.get(channel);
    }

    public static ArticleManager getInstance() {
        if (instance == null) {
            synchronized(ArticleManager.class) {
                if (instance == null) {
                    instance = new ArticleManager();
                }
            }
        }
        return instance;
    }

}
