package com.qintx.myjindy;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.qintx.myjindy.checkcode.Checkcode;
import com.qintx.myjindy.constant.Constants;
import com.qintx.myjindy.user.AccountManager;

public class LoginActivity extends Activity implements OnClickListener {
    
    private static final String TAG = "LoginActivity";
    private static final int MSG_LOGIN_SUCCESS = 0;
    private static final int MSG_LOGIN_FAILED = 1;

    private ImageView ivCheckcode;

    private EditText etName;
    private EditText etPwd;
    private EditText etCheckcode;
    private Button btnLogin;

    private Handler loginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOGIN_SUCCESS:
                    Toast.makeText(LoginActivity.this, R.string.login_sucess, Constants.TOAST_DURATION).show();
                    finish();
                    break;
                case MSG_LOGIN_FAILED:
                    Toast.makeText(LoginActivity.this, R.string.login_failed, Constants.TOAST_DURATION).show();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etName = (EditText)findViewById(R.id.etName);
        etPwd = (EditText)findViewById(R.id.etPwd);
        etCheckcode = (EditText)findViewById(R.id.etCheckcode);
        ivCheckcode = (ImageView)findViewById(R.id.ivCheckcode);
        btnLogin = (Button)findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(this);
        ivCheckcode.setOnClickListener(this);

        getCheckcode();
    }
    
    private void getCheckcode() {
        new GetCheckCode().execute();
    }

    private class GetCheckCode extends AsyncTask<Void, Bitmap, Bitmap> {

        @Override
        protected void onPreExecute() {
            btnLogin.setEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            return Checkcode.getCheckcode();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                btnLogin.setEnabled(true);
            }
            ivCheckcode.setImageBitmap(result);
            super.onPostExecute(result);
        }

    }

    private void startLogin() {
        final String name = this.etName.getText().toString();
        final String pwd = this.etPwd.getText().toString();
        final String checkcode = this.etCheckcode.getText().toString();
        
        if (name == null || pwd == null || checkcode == null) {
            return;
        }
        Log.d(TAG, "start login with [" + name + ", " + pwd + "]," + checkcode);
        new Thread() {
            public void run() {
                boolean loginResult = AccountManager.getInstance().login(name, pwd, checkcode);
                if (loginResult) {
                    loginHandler.sendEmptyMessage(MSG_LOGIN_SUCCESS);
                } else {
                    loginHandler.sendEmptyMessage(MSG_LOGIN_FAILED);
                }
            }
        }.start();
        
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                startLogin();
                break;
            case R.id.ivCheckcode:
                getCheckcode();
            default:
        }
    }

}
