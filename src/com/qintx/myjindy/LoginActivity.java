package com.qintx.myjindy;

import com.qintx.myjindy.checkcode.Checkcode;
import com.qintx.myjindy.http.HttpUtil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;

public class LoginActivity extends Activity {
    
    private ImageView ivCheckcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ivCheckcode = (ImageView)findViewById(R.id.ivCheckcode);
        
        new GetCheckCode().execute();
    }

    private class GetCheckCode extends AsyncTask<Void, Bitmap, Bitmap> {

        @Override
        protected Bitmap doInBackground(Void... params) {
            return Checkcode.getCheckcode();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            ivCheckcode.setImageBitmap(result);
            super.onPostExecute(result);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

}
