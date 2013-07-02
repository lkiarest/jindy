package com.qintx.myjindy.http;

import org.apache.commons.httpclient.HttpMethodBase;

import android.util.Log;

public class HttpCon {
    private static final String TAG = "HttpCon";
    private HttpMethodBase method;

    public HttpCon() {
        
    }

    public void setMethod(HttpMethodBase method) {
        this.method = method;
    }

    public void release() {
        if (method == null) {
            Log.d(TAG, "close method failed, method is null !");
            return;
        }
        method.releaseConnection();
    }
}
