package com.qintx.myjindy.http;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static HttpCookie instance;

    // store url and cookie string
    private Map<String, String> cookieMap;

    private HttpCookie() {
        cookieMap = new HashMap<String, String>();
    }

    public String getCookie(String url) {
        return cookieMap.get(url);
    }

    public void setCookie(String url, String cookie) {
        cookieMap.put(url, cookie);
    }

    public static HttpCookie getInstance() {
        if (instance == null) {
            instance = new HttpCookie();
        }
        return instance;
    }
}
