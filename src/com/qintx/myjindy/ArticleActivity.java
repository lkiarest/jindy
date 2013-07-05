package com.qintx.myjindy;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.qintx.myjindy.constant.Constants;
import com.qintx.myjindy.content.entity.Article;
import com.qintx.myjindy.content.manager.ArticleManager;
import com.qintx.myjindy.utility.ProgressDlg;

import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

public class ArticleActivity extends SherlockActivity {

    private static final String TAG = "ArticleActivity";
    
    private String title;

    private WebView tvArticle;
    private Article articleEntity;

    private BroadcastReceiver articleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            boolean success = intent.getBooleanExtra(Constants.ACTION_RESULT, false);
            if (success) {
                // show article
                String url = intent.getStringExtra(Constants.INTENT_URL);
                if ((url == null) || url.isEmpty()) {
                    Toast.makeText(ArticleActivity.this, R.string.SID_EMPTY_CONTENT, Constants.TOAST_DURATION).show();
                    return;
                }
                Article article = ArticleManager.getInstance().getArticle(url);
                if (article != null) {
                    articleEntity = article;
                    tvArticle.loadDataWithBaseURL("about:blank", article.getContent(), "text/html", "utf-8", null);
                }
            } else {
                // show error message
                Toast.makeText(ArticleActivity.this, R.string.SID_EMPTY_CONTENT, Constants.TOAST_DURATION).show();
            }
            ProgressDlg.closeProgress();
        }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        // add back button
        this.getSupportActionBar().setHomeButtonEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        IntentFilter filter = new IntentFilter(Constants.ACTION_GET_ARTICLE_CONTENT_RESULT);
        this.registerReceiver(articleReceiver, filter);

        tvArticle = (WebView)this.findViewById(R.id.tvArticle);

        // get title
        String title = this.getIntent().getStringExtra(Constants.INTENT_ARTICLE_TITLE);
        setTitle(title);

        // get article content
        String url = this.getIntent().getStringExtra(Constants.INTENT_URL);
        requestContent(url);
    }

    private void requestContent(String url) {
        ProgressDlg.showProgress(this, null, R.string.SID_LOADING);
        Intent requestIntent = new Intent(Constants.ACTION_GET_ARTICLE_CONTENT);
        requestIntent.putExtra(Constants.INTENT_URL, url);
        startService(requestIntent);
    }

    private void setTitle(String title) {
        TextView tvTitle = (TextView)this.findViewById(R.id.tvTitle);
        tvTitle.setText(title);
        this.title = title;
    }

    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        menu.add(R.string.SID_LIST_COMMENT)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(R.string.SID_NEW_COMMENT)
        .setIcon(R.drawable.ic_compose)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        return super.onCreateOptionsMenu(menu);
    }

    private void showComments() {
        Intent intent = new Intent(this, CommentListActivity.class);
        intent.putExtra(Constants.INTENT_ARTICLE_TITLE, title);
        intent.putExtra(Constants.INTENT_URL, articleEntity.getCommentUrl());
        Log.d(TAG, "start comments activity with " + articleEntity.getCommentUrl() + ", " + title);
        this.startActivity(intent);
    }

    private void newComments() {
        Intent intent = new Intent(this, NewCommentActivity.class);
        intent.putExtra(Constants.INTENT_ARTICLE_TITLE, title);
        intent.putExtra(Constants.INTENT_URL, articleEntity.getCommentUrl());
        Log.d(TAG, "start comments eidt activity with " + articleEntity.getCommentUrl() + ", " + title);
        this.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else {
            String title = item.getTitle().toString();
            if (title.equals(this.getResources().getString(R.string.SID_LIST_COMMENT))) {
                showComments();
            } else if (title.equals(this.getResources().getString(R.string.SID_NEW_COMMENT))) {
                newComments();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(articleReceiver);
        super.onDestroy();
    }

}
