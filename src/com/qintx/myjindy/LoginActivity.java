package com.qintx.myjindy;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.qintx.myjindy.checkcode.Checkcode;
import com.qintx.myjindy.constant.Constants;
import com.qintx.myjindy.user.AccountManager;
import com.qintx.myjindy.utility.ProgressDlg;

public class LoginActivity extends Activity implements OnClickListener {
    
    private static final String TAG = "LoginActivity";
    private static final int MSG_LOGIN_SUCCESS = 0;
    private static final int MSG_LOGIN_FAILED = 1;

    private ImageView ivCheckcode;

    private EditText etName;
    private EditText etPwd;
    private EditText etCheckcode;
    private Button btnLogin;
    private ProgressBar pgLoading;

    private Handler loginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ProgressDlg.closeProgress();
            switch (msg.what) {
                case MSG_LOGIN_SUCCESS:
                    Toast.makeText(LoginActivity.this, R.string.login_sucess, Constants.TOAST_DURATION).show();
                    finish();
                    break;
                case MSG_LOGIN_FAILED:
                    if (msg.arg1 == AccountManager.LOGIN_ERR_CHECKCODE) {
                        Toast.makeText(LoginActivity.this, Constants.LOGIN_ERR1_LABEL, Constants.TOAST_DURATION).show();
                    } else if (msg.arg1 == AccountManager.LOGIN_ERR_PWD) {
                        Toast.makeText(LoginActivity.this, Constants.LOGIN_ERR2_LABEL, Constants.TOAST_DURATION).show();
                    }
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

        pgLoading = (ProgressBar)findViewById(R.id.pgLoading);

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
            ivCheckcode.setVisibility(View.GONE);
            pgLoading.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            return Checkcode.getCheckcode(metrics.density);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                btnLogin.setEnabled(true);
            }
            ivCheckcode.setVisibility(View.VISIBLE);
            pgLoading.setVisibility(View.GONE);
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
        ProgressDlg.showProgress(this, null, R.string.SID_LOGGING);
        new Thread() {
            public void run() {
                int loginResult = AccountManager.getInstance().login(name, pwd, checkcode);
                if (loginResult == 0) {
                    loginHandler.sendEmptyMessage(MSG_LOGIN_SUCCESS);
                } else {
                    Message msg = Message.obtain();
                    msg.what = MSG_LOGIN_FAILED;
                    msg.arg1 = loginResult;
                    loginHandler.sendMessage(msg);
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
