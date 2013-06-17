package com.qintx.myjindy.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpUtil {

    private HttpClient client = null;

    public HttpUtil() {
        client = new DefaultHttpClient();
    }

    public byte[] getContentAsBytes(String url) {
        return getContent(url, new ResponseConverter<byte[]>() {
            public byte[] convert(HttpResponse response) throws IllegalStateException, IOException {
                return EntityUtils.toByteArray(response.getEntity());
            }
        });
    }

    public String getContentAsString(String url) {
        return getContent(url, new ResponseConverter<String>() {
            public String convert(HttpResponse response) throws IllegalStateException, IOException {
                String content = EntityUtils.toString(response.getEntity());
                return new String(content.getBytes("ISO-8859-1"), "UTF-8");// for Chinese words
            }
        });
    }

    public InputStream getContentAsStream(String url) {
        return getContent(url, new ResponseConverter<InputStream>() {
            public InputStream convert(HttpResponse response) throws IllegalStateException, IOException {
                return response.getEntity().getContent();
            }
        });
    }

    private <T> T getContent(String url, ResponseConverter<T> converter) {
        HttpGet method = getHttpGet(url);
        try {
            HttpResponse response = client.execute(method);
            if (response.getEntity() == null) {
                return null;
            }
            return converter.convert(response);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private HttpGet getHttpGet(String url) {
        HttpGet method = new HttpGet(url);
        method.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
        return method;
    }

    private class ResponseConverter<T> {
        public T convert(HttpResponse response) throws IllegalStateException, IOException {
            return null;
        }
    }

    public HttpClient getHttpClient() {
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
//    private FinalHttp fh;
//
//    public void ajaxPost(final String url, final HttpCallback callback) {
//        fh.post(url, new AjaxCallBack<String>() {
//
//            @Override
//            public void onFailure(Throwable t, String strMsg) {
//                if (callback != null) {
//                    callback.onCallback(false, url, strMsg);
//                }
//                super.onFailure(t, strMsg);
//            }
//
//            @Override
//            public void onSuccess(String t) {
//                if (callback != null) {
//                    callback.onCallback(true, url, t);
//                }
//                super.onSuccess(t);
//            }
//        });
//    }
//
//    public void ajaxGet(final String url, final HttpCallback callback) {
//        fh.get(url, new AjaxCallBack<String>() {
//
//            @Override
//            public void onFailure(Throwable t, String strMsg) {
//                if (callback != null) {
//                    callback.onCallback(false, url, strMsg);
//                }
//                super.onFailure(t, strMsg);
//            }
//
//            @Override
//            public void onSuccess(String t) {
//                if (callback != null) {
//                    callback.onCallback(true, url, t);
//                }
//
//                super.onSuccess(t);
//            }
//        });
//    }
//
//    public interface HttpCallback {
//        public void onCallback(boolean success, String url, String msg);
//    }
}
