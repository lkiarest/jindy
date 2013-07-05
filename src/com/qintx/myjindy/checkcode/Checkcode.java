package com.qintx.myjindy.checkcode;

import java.io.IOException;
import java.io.InputStream;

import com.qintx.myjindy.constant.Constants;
import com.qintx.myjindy.http.HttpCon;
import com.qintx.myjindy.http.HttpUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.DisplayMetrics;
import android.util.Log;

public class Checkcode {

    private static final String TAG = "Checkcode";

    public static Bitmap getCheckcode(float density) {
        HttpCon con = new HttpCon();
        if (HttpUtil.getContentAsStream(Constants.LOGIN_PAGE_URL, con) != null) {
            con.release();
            InputStream is = HttpUtil.getContentAsStream(Constants.CHECK_CODE_URL, con);
            if (is != null) {
                //Rect outPadding = new Rect();
                //Options opts = new Options();
                Bitmap bitmap = BitmapFactory.decodeStream(is);

                try {
                    is.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                } finally {
                    con.release();
                }
                Matrix matrix = new Matrix();
                matrix.postScale(density, density);
                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
        }
        return null;
    }
}
