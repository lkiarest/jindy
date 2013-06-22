package com.qintx.myjindy.user;

import java.io.IOException;
import java.io.InputStream;

import com.qintx.myjindy.http.HttpUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class AccountManager {

    private HttpUtil httpClient;
    private static final String TAG = "AccountManager";

    public AccountManager() {
        httpClient = new HttpUtil();
    }

}
