package com.qintx.myjindy;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.qintx.myjindy.constant.Constants;
import com.qintx.myjindy.http.HttpUtil;
import com.qintx.myjindy.utility.ProgressDlg;

public class NewCommentActivity extends SherlockActivity implements OnClickListener {

    private static final String TAG = "NewCommentActivity";

    private final static int MSG_POST_OK = 0;
    private final static int MSG_POST_ERR = 1;

    private String title = null;
    private TextView tvTitle = null;
    private String postUrl = null;
    private Button btnSubmit = null;
    private EditText etComment = null;

    private Handler postHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            ProgressDlg.closeProgress();
            switch(msg.what) {
                case MSG_POST_OK:
                    Toast.makeText(NewCommentActivity.this, R.string.SID_POST_OK, Constants.TOAST_DURATION).show();
                    finish();
                    break;
                case MSG_POST_ERR:
                    Toast.makeText(NewCommentActivity.this, R.string.SID_POST_FAILED, Constants.TOAST_DURATION).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_comment);

        // add back button
        this.getSupportActionBar().setHomeButtonEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvTitle = (TextView)this.findViewById(R.id.title);
        title = this.getIntent().getStringExtra(Constants.INTENT_ARTICLE_TITLE);
        tvTitle.setText(title);

        etComment = (EditText)this.findViewById(R.id.etComment);

        btnSubmit = (Button)this.findViewById(R.id.btnPost);
        btnSubmit.setOnClickListener(this);

        postUrl = getPostUrl();
    }

    private String getPostUrl() {
        String commentUrl = this.getIntent().getStringExtra(Constants.INTENT_URL);
        if (commentUrl == null) {
            return null;
        }
        int pos = commentUrl.indexOf("commentid=");
        if (pos > 0) {
            String temp = commentUrl.substring(pos);
            String[] vals = temp.split("=");
            if ((vals != null) && (vals.length == 2)) {
                String commendId = vals[1];
                return Constants.POST_COMMENT_URL + commendId;
            }
        }
        return null;
    }

    private void postComment() {
        String comment = etComment.getText().toString();
        if (comment == null || comment.trim().isEmpty()) {
            return;
        }
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("content", comment);
        paramMap.put("title", title);
        String result = HttpUtil.post(postUrl, null, paramMap);
        Log.d(TAG, "post comment result :" + result);
        if (result.contains("操作成功")) {
            postHandler.sendEmptyMessage(MSG_POST_OK);
        } else {
            postHandler.sendEmptyMessage(MSG_POST_ERR);
        }
    }

    private void doPost() {
        ProgressDlg.showProgress(this, null, R.string.SID_POSTING);
        new Thread() {
            public void run() {
                postComment();
            }
        }.start();
    }

    @Override
    public void onClick(View arg0) {
        doPost();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
