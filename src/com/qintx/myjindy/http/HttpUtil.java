package com.qintx.myjindy.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import android.util.Log;

import com.qintx.myjindy.constant.Constants;

public class HttpUtil {

    private static final String TAG = "HttpUtil";
    private static HttpClient client = null;
    // cookie support
    private static HttpCookie httpCookie = null;

    static {
        client = new HttpClient();
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        httpCookie = HttpCookie.getInstance();
    }

    public static byte[] getContentAsBytes(String url) {
        HttpMethodBase method = getHttpGet(url);
        return getContent(url, method, new ResponseConverter<byte[]>() {
            public byte[] convert(HttpMethodBase method) throws IllegalStateException, IOException {
                byte[] result =  method.getResponseBody();
                method.releaseConnection();
                return result;
            }
        });
    }

    public static String getContentAsString(String url) {
        HttpMethodBase method = getHttpGet(url);
        return getContent(url, method, new ResponseConverter<String>() {
            public String convert(HttpMethodBase method) throws IllegalStateException, IOException {
                String result = method.getResponseBodyAsString();
                method.releaseConnection();
                return result;
            }
        });
    }

    public static InputStream getContentAsStream(String url, HttpCon con) {
        HttpMethodBase method = getHttpGet(url);
        con.setMethod(method);
        return getContent(url, method, new ResponseConverter<InputStream>() {
            public InputStream convert(HttpMethodBase method) throws IllegalStateException, IOException {
                return method.getResponseBodyAsStream();
            }
        });
    }

    public static String post(String url, Map<String, String> headerMap, Map<String, String> paramMap) {
        HttpMethodBase method = getHttpPost(url);
        return post(url, method, new ResponseConverter<String>() {
            public String convert(HttpMethodBase method) throws IllegalStateException, IOException {
                String result = method.getResponseBodyAsString();
                method.releaseConnection();
                return result;
            }
        }, getParamFromMap(paramMap));
    }

    // set cookie
    private static void setCookies(String url) {
        Cookie[] cookies = client.getState().getCookies();
        String cookie = "";
        for (int i = 0; i < cookies.length; i++) {
            cookie += cookies[i].toString() + ";";
        }

        httpCookie.setCookie(getDomainFromUrl(url), cookie);
    }

    private static <T> T httpRequest(String url, HttpMethodBase method, ResponseConverter<T> converter) {
        try {
            int result = client.executeMethod(method);
            Log.d(TAG, "http request result is " + result + "--by " + url);

            if (result != HttpStatus.SC_OK) {
                return null;
            }
            //Header header = method.getResponseHeader("Set-Cookie");

            // set cookie
            setCookies(url);
            T requestResult =  converter.convert(method);

            return requestResult;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static <T> T getContent(String url, HttpMethodBase method, ResponseConverter<T> converter) {
        return httpRequest(url, method, converter);
    }

    public static <T> T post(String url, HttpMethodBase method, ResponseConverter<T> converter, NameValuePair[] params) {
        ((PostMethod)method).addParameters(params);
        return httpRequest(url, method, converter);
    }

    private static NameValuePair[] getParamFromMap(Map<String, String> paramMap) {
        if (paramMap == null) {
            return null;
        }
        Set<String> keys = paramMap.keySet();
        Iterator<String> itr = keys.iterator();
        NameValuePair[] params = new NameValuePair[keys.size()];
        int i = 0;
        while(itr.hasNext()) {
            String key = itr.next();
            params[i] = new NameValuePair(key, paramMap.get(key));
            i ++;
        }
        return params;
    }

    private static PostMethod getHttpPost(String url) {
        PostMethod method = new PostMethod(url);
        method.addRequestHeader("Cookie", httpCookie.getCookie(getDomainFromUrl(url)));
        method.addRequestHeader("Referer", Constants.INDEX_URL + "index.php?m=member&c=index&a=login&forward=http%3A%2F%2Fjindy.myjindy.net%2F&siteid=1");
        method.addRequestHeader("Host", Constants.HOST);
        method.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:21.0) Gecko/20100101 Firefox/21.0");
        method.addRequestHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
        method.addRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        //method.addRequestHeader("Accept-Encoding", "gzip, deflate");
        method.addRequestHeader("Connection", "keep-alive");
        Log.d(TAG, "post cookie : " + httpCookie.getCookie(getDomainFromUrl(url)));
        return method;
    }

    private static GetMethod getHttpGet(String url) {
        GetMethod method = new GetMethod(url);
        method.addRequestHeader("Cookie", httpCookie.getCookie(getDomainFromUrl(url)));
        method.addRequestHeader("Referer", Constants.INDEX_URL + "index.php?m=member&c=index&a=mini&forward=http%3A%2F%2Fjindy.myjindy.net");
        method.addRequestHeader("Host", Constants.HOST);
        method.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:21.0) Gecko/20100101 Firefox/21.0");
        method.addRequestHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
        //method.addRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        //method.addRequestHeader("Accept-Encoding", "gzip, deflate");
        method.addRequestHeader("Connection", "keep-alive");
        Log.d(TAG, "get cookie : " + httpCookie.getCookie(getDomainFromUrl(url)));
        return method;
    }

    private static String getDomainFromUrl(String url) {
        int start = url.indexOf("http://");
        start = start == 0 ? 7 : 0;
        int pos = url.indexOf("/", start);
        return url.substring(start, pos);
    }

    public HttpClient getHttpClient() {
        return client;
    }

}
