package com.qintx.myjindy.http;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpUtil {

    private final static HttpClient client = new DefaultHttpClient();

    public static HttpClient getHttpClient() {
        return client;
    }
/*
    public static String get(String url) {
        HttpGet  method = new HttpGet(url);
        try {
            HttpResponse resp = client.execute(method);
            resp.getParams().g
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String getWithCookie(String url, String cookie) {
        
    }
*/
    private FinalHttp fh;

    public HttpUtil() {
        fh = new FinalHttp();
    }

    public void ajaxPost(final String url, final HttpCallback callback) {
        fh.post(url, new AjaxCallBack<String>() {

            @Override
            public void onFailure(Throwable t, String strMsg) {
                if (callback != null) {
                    callback.onCallback(false, url, strMsg);
                }
                super.onFailure(t, strMsg);
            }

            @Override
            public void onSuccess(String t) {
                if (callback != null) {
                    callback.onCallback(true, url, t);
                }
                super.onSuccess(t);
            }
        });
    }

    public void ajaxGet(final String url, final HttpCallback callback) {
        fh.get(url, new AjaxCallBack<String>() {

            @Override
            public void onFailure(Throwable t, String strMsg) {
                if (callback != null) {
                    callback.onCallback(false, url, strMsg);
                }
                super.onFailure(t, strMsg);
            }

            @Override
            public void onSuccess(String t) {
                if (callback != null) {
                    callback.onCallback(true, url, t);
                }

                super.onSuccess(t);
            }
        });
    }

    public interface HttpCallback {
        public void onCallback(boolean success, String url, String msg);
    }
}
