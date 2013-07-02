package com.qintx.myjindy.checkcode;

import java.io.IOException;
import java.io.InputStream;

import com.qintx.myjindy.constant.Constants;
import com.qintx.myjindy.http.HttpCon;
import com.qintx.myjindy.http.HttpUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Checkcode {

    private static final String TAG = "Checkcode";

    public static Bitmap getCheckcode() {
        HttpCon con = new HttpCon();
        if (HttpUtil.getContentAsStream(Constants.LOGIN_PAGE_URL, con) != null) {
            con.release();
            InputStream is = HttpUtil.getContentAsStream(Constants.CHECK_CODE_URL, con);
            if (is != null) {
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                try {
                    is.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                } finally {
                    con.release();
                }
                return bitmap;
            }
        }
        return null;
    }
}
