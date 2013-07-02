package com.qintx.myjindy.user;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.qintx.myjindy.R;
import com.qintx.myjindy.constant.Constants;
import com.qintx.myjindy.http.HttpUtil;

public class AccountManager {

    private static AccountManager instance;

    private UserInfo userInfo;

    private boolean bLogin = false;

    private static final String TAG = "AccountManager";

    private AccountManager() {
        
    }

    public boolean login(String name, String pwd, String checkcode) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", name);
        params.put("password", pwd);
        params.put("code", checkcode);
        params.put("dosubmit", "登录");
        params.put("forward", "http://jindy.myjindy.net/");

        String result = HttpUtil.post(Constants.LOGIN_URL, null, params);
        Log.d(TAG, "login result is : " + result);
        if ((result != null) && result.contains(Constants.LOGIN_SUCCESS_LABLE)) {
            bLogin = true;
        }
        return bLogin;
    }

    public boolean logoff() {
        String result = HttpUtil.getContentAsString(Constants.LOGOFF_URL);
        if ((result != null) && result.contains(Constants.LOGOFF_SUCCESS_LABLE)) {
            bLogin = false;
        }
        return bLogin;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public boolean isLogin() {
        return bLogin;
    }

    public static AccountManager getInstance() {
        if (instance == null) {
            instance = new AccountManager();
        }
        return instance;
    }

}
