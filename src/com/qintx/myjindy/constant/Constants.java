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
    public final static String INTENT_URL = "url";

    // request url
    public final static String HOST = "www.jindy.myjindy.net";
    public final static String INDEX_URL = "http://" + HOST + "/";
    public final static String CHECK_CODE_URL = INDEX_URL + "api.php?op=checkcode&code_len=4&font_size=14&width=84&height=24&font_color=&background=";
    //private final static String CHECK_CODE_URL = "http://img.download.pchome.net/3q/d0/167091_800x600.jpg";
    public final static String LOGIN_PAGE_URL = INDEX_URL + "index.php?m=member&c=index&a=login&forward=http%3A%2F%2Fwww.myjindy.net%2F&siteid=1";
    public final static String LOGIN_URL = INDEX_URL + "index.php?m=member&c=index&a=login";
    public final static String LOGOFF_URL = INDEX_URL + "index.php?m=member&c=index&a=logout&forward=http%3A%2F%2Fwww.jindy.myjindy.net%2F&siteid=1";
    //post comment
    public final static String POST_COMMENT_URL = "http://www.jindy.myjindy.net/index.php?m=comment&c=index&a=post&commentid=";

    // request result
    public final static int SUCCESS_CODE = 200;
    public final static String LOGIN_SUCCESS_LABLE = "登录成功";
    public final static String LOGOFF_SUCCESS_LABLE = "退出成功";
    public final static String LOGIN_ERR1_LABEL = "验证码输入错误";
    public final static String LOGIN_ERR2_LABEL = "密码错误";

    //comment style
    public final static String COMMENT_TITLE_CSS = "font-size:12px;color:#333333;";
    public final static String COMMENT_CONTENT_CSS = "background:none repeat scroll 0 0 #F6F9FB;border:1px solid #C4D5DF;padding:8px 10px;";
}
