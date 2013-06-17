package com.qintx.myjindy;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.qintx.myjindy.constant.Constants;
import com.qintx.myjindy.content.entity.Article;
import com.qintx.myjindy.content.manager.ArticleManager;
import com.qintx.myjindy.utility.ProgressDlg;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

public class ArticleActivity extends SherlockActivity {

    private WebView tvArticle;

    private BroadcastReceiver articleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            
            boolean success = intent.getBooleanExtra(Constants.ACTION_RESULT, false);
            if (success) {
                // show article
                String url = intent.getStringExtra(Constants.INTENT_ARTICLE_URL);
                if ((url == null) || url.isEmpty()) {
                    Toast.makeText(ArticleActivity.this, R.string.SID_EMPTY_CONTENT, Constants.TOAST_DURATION).show();
                    return;
                } 
                Article article = ArticleManager.getInstance().getArticle(url);
                if (article != null) {
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
        String url = this.getIntent().getStringExtra(Constants.INTENT_ARTICLE_URL);
        requestContent(url);
    }
    
    private void requestContent(String url) {
        ProgressDlg.showProgress(this, null, R.string.SID_LOADING);
        Intent requestIntent = new Intent(Constants.ACTION_GET_ARTICLE_CONTENT);
        requestIntent.putExtra(Constants.INTENT_ARTICLE_URL, url);
        startService(requestIntent);
    }

    private void setTitle(String title) {
        TextView tvTitle = (TextView)this.findViewById(R.id.tvTitle);
        tvTitle.setText(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            onBackPressed();
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        this.unregisterReceiver(articleReceiver);
        super.onDestroy();
    }

}
