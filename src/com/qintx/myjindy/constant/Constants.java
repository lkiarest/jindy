package com.qintx.myjindy.constant;

public class Constants {

    public final static String CHANNEL_FILE = "channels.xml";

    public final static int TOAST_DURATION = 1000;

    public static final String ACTION_GET_ARTICLES = "com.qintx.myjindy.manager.ACTION_GET_ARTICLES";
    public static final String ACTION_GET_ARTICLE_CONTENT = "com.qintx.myjindy.manager.ACTION_GET_ARTICLE_CONTENT";
    public static final String ACTION_GET_ARTICLES_RESULT = "com.qintx.myjindy.manager.ACTION_GET_ARTICLES_RESULT";
    public static final String ACTION_GET_ARTICLE_CONTENT_RESULT = "com.qintx.myjindy.manager.ACTION_GET_ARTICLE_CONTENT_RESULT";
    public static final String ACTION_RESULT = "com.qintx.myjindy.manager.ACTION_RESULT";

    public final static String INTENT_CHANNEL_URL = "channelUrl";
    public final static String INTENT_ARTICLE_TITLE = "title";
    public final static String INTENT_ARTICLE_URL = "url";
    
    // request url
    public final static String CHECK_CODE_URL = "http://www.jindy.myjindy.net/api.php?op=checkcode&code_len=4&font_size=14&width=84&height=24&font_color=&background=";
    //private final static String CHECK_CODE_URL = "http://img.download.pchome.net/3q/d0/167091_800x600.jpg";
    public final static String LOGIN_PAGE_URL = "http://www.jindy.myjindy.net/index.php?m=member&c=index&a=login&forward=http%3A%2F%2Fwww.myjindy.net%2F&siteid=1";
    

}
