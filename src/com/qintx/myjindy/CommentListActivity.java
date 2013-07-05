package com.qintx.myjindy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.qintx.myjindy.constant.Constants;
import com.qintx.myjindy.http.HttpUtil;
import com.qintx.myjindy.utility.ProgressDlg;

public class CommentListActivity extends SherlockActivity {

    private String url = null;
    private WebView wvComments = null;
    private TextView tvTitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_list);
        url = this.getIntent().getStringExtra(Constants.INTENT_URL);
        tvTitle = (TextView)this.findViewById(R.id.title);
        tvTitle.setText(this.getIntent().getStringExtra(Constants.INTENT_ARTICLE_TITLE));
        wvComments = (WebView)this.findViewById(R.id.tvComments);

        // add back button
        this.getSupportActionBar().setHomeButtonEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        if (url != null) {
            new CommentsTask().execute(url);
        }
        super.onResume();
    }

    private class CommentsTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            ProgressDlg.showProgress(CommentListActivity.this, null, R.string.SID_LOADING);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            ProgressDlg.closeProgress();
            wvComments.loadDataWithBaseURL("about:blank", result, "text/html", "utf-8", null);
            super.onPostExecute(result);
        }

        @Override
        protected String doInBackground(String... url) {
            String content = HttpUtil.getContentAsString(url[0]);
            return filterContent(content);
        }

        private String filterContent(String content) {
            if (content == null || content.isEmpty()) {
                return null;
            }
            Document doc = Jsoup.parse(content);
            StringBuilder result = new StringBuilder();
            Elements titles = doc.select(".main .col-left .comment .title");
            for (int i = 0; i < titles.size(); i++) {
                Element titleElem = titles.get(i);
                String title = titleElem.text();
                Element commentElem = titleElem.nextElementSibling();
                String comment = commentElem.textNodes().get(0).text();
                result.append("<p>");
                result.append("<div style='" + Constants.COMMENT_TITLE_CSS + "'>" + title + "</div>");
                result.append("<div style='" + Constants.COMMENT_CONTENT_CSS + "'>" + comment + "</div></p>");
            }
            return result.toString();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
