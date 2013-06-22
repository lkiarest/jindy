package com.qintx.myjindy.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpUtil {

    private static final String TAG = "HttpUtil";
    private HttpClient client = null;
    // cookie support
    private CookieStore  cookieStore = null;
    private HttpContext localContext = null;
    private HttpCookie httpCookie = null;

    public HttpUtil() {
        client = new DefaultHttpClient();
        httpCookie = HttpCookie.getInstance();
        cookieStore = new BasicCookieStore();
        localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
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
                BufferedHttpEntity bhe = new BufferedHttpEntity(response.getEntity());
                return bhe.getContent();
            }
        });
    }

    private void setCookies(String url) {
        if ((url == null) || url.isEmpty()) {
            return;
        }
        List<Cookie> cookies = cookieStore.getCookies();
        StringBuilder cookieStr = new StringBuilder();
        for (int i = 0; i < cookies.size(); i++) {
            Cookie cookie = cookies.get(i);
            cookieStr.append(cookie.getName() + "=" + cookie.getValue() + ";");
        }
        Log.d(TAG, cookieStr.toString());
        httpCookie.setCookie(getDomainFromUrl(url), cookieStr.toString());
    }

    private <T> T getContent(String url, ResponseConverter<T> converter) {
        HttpGet method = getHttpGet(url);
        try {
            HttpResponse response = client.execute(method, localContext);

            if (response.getEntity() == null) {
                return null;
            }

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Log.e(TAG, "response status : " + response.getStatusLine().getStatusCode() + "--" + url);
                return null;
            }

            // set cookie
            setCookies(url);

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
        method.setHeader("Cookie", httpCookie.getCookie(getDomainFromUrl(url)));
        return method;
    }

    private class ResponseConverter<T> {
        public T convert(HttpResponse response) throws IllegalStateException, IOException {
            return null;
        }
    }

    private String getDomainFromUrl(String url) {
        int pos = url.indexOf("/");
        return url.substring(0, pos);
    }

    public HttpClient getHttpClient() {
        return client;
    }

}
