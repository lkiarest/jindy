package com.qintx.myjindy.user;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.qintx.myjindy.constant.Constants;
import com.qintx.myjindy.http.HttpUtil;

public class AccountManager {

    private static AccountManager instance;

    public static final int LOGIN_SUCCESS = 0;
    public static final int LOGIN_ERR_CHECKCODE = -1;
    public static final int LOGIN_ERR_PWD = -2;
    public static final int LOGIN_ERR_OTHER = -3;

    private UserInfo userInfo;

    private boolean bLogin = false;

    private static final String TAG = "AccountManager";

    private AccountManager() {
        
    }

    public int login(String name, String pwd, String checkcode) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", name);
        params.put("password", pwd);
        params.put("code", checkcode);
        params.put("dosubmit", "登录");
        params.put("forward", "http://jindy.myjindy.net/");

        String result = HttpUtil.post(Constants.LOGIN_URL, null, params);
        Log.d(TAG, "login result is : " + result);
        int retCode = LOGIN_SUCCESS;

        if (result == null) {
            return LOGIN_ERR_OTHER;
        }

        if (result.contains(Constants.LOGIN_SUCCESS_LABLE)) {
            bLogin = true;
        } else {
            bLogin = false;
            if (result.contains(Constants.LOGIN_ERR1_LABEL)) {
                retCode = LOGIN_ERR_CHECKCODE;
            } else if (result.contains(Constants.LOGIN_ERR2_LABEL)) {
                retCode = LOGIN_ERR_PWD;
            }
        }
        return retCode;
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
