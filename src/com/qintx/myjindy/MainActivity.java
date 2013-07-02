package com.qintx.myjindy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.qintx.myjindy.constant.Constants;
import com.qintx.myjindy.content.entity.Article;
import com.qintx.myjindy.content.entity.Channel;
import com.qintx.myjindy.content.manager.ArticleManager;
import com.qintx.myjindy.content.manager.ChannelManager;
import com.qintx.myjindy.user.AccountManager;
import com.qintx.myjindy.utility.ProgressDlg;

public class MainActivity extends SherlockActivity implements TabListener, OnItemClickListener {

    private static final String TAG = "MainActivity";
    
    private static final int MSG_LOGOFF_SUCCESS = 0;
    private static final int MSG_LOGOFF_FAILED = 1;
    
    private ListView articleList = null;
    private ChannelManager chMgr = null;
    private ArticleManager artMgr = null;
    private String currentChannelUrl = null;
    
    private MenuItem loginMenu = null;

    private final BroadcastReceiver articleRecv = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ProgressDlg.closeProgress();
            boolean result = intent.getBooleanExtra(Constants.ACTION_RESULT, false);
            if (result) {
                String url = intent.getStringExtra(Constants.INTENT_CHANNEL_URL);
                if ((url != null) && url.equals(currentChannelUrl)) {
                    initArticleList(url, artMgr.getArticles(url));
                }
            } else {
                Toast.makeText(MainActivity.this, R.string.SID_NO_ARTICLE, Constants.TOAST_DURATION).show();
            }
        }
    };

    private final Handler logoffHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case MSG_LOGOFF_SUCCESS:
                    Toast.makeText(MainActivity.this, R.string.logoff_sucess, Constants.TOAST_DURATION).show();
                    break;
                case MSG_LOGOFF_FAILED:
                    Toast.makeText(MainActivity.this, R.string.logoff_failed, Constants.TOAST_DURATION).show();
                    break;
            }
            setLoginMenu();
            super.handleMessage(msg);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        chMgr = new ChannelManager(this);
        artMgr = ArticleManager.getInstance();
        registerBroadcastReceiver();
        articleList = (ListView)this.findViewById(R.id.articleList);
        initTabs();
//        new Thread() {
//            public void run() {
//                if (Checkcode.getCheckcode() != null) {
//                    Log.d(TAG, "check code got !");
//                }
//            }
//        }.start();
    }

    private void registerBroadcastReceiver() {
        IntentFilter articleFilter = new IntentFilter(Constants.ACTION_GET_ARTICLES_RESULT);
        this.registerReceiver(articleRecv, articleFilter);
    }

    private void unregisterBroadcastReceiver() {
        this.unregisterReceiver(articleRecv);
    }

    private void initTabs() {
        //ProgressDlg.showProgress(this, null, R.string.SID_LOADING);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        List<Channel> channels = chMgr.getChannels();
        if (channels == null) {
            Toast.makeText(this, R.string.SID_NO_CHANNEL, Constants.TOAST_DURATION).show();
            return;
        }
        int len = channels.size();
        for (int i = 0; i < len; i++) {
            Tab tab = this.getSupportActionBar().newTab();
            tab.setText(channels.get(i).getName());
            tab.setTabListener(this);
            this.getSupportActionBar().addTab(tab);
        }
       // ProgressDlg.closeProgress();
    }

    private void initArticleList(String url, List<Article> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        List<Map<String, String>> titles = new ArrayList<Map<String, String>>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, String> titleMap = new HashMap<String, String>();
            titleMap.put("title", list.get(i).getTitle());
            titleMap.put("url", list.get(i).getUrl());
            titles.add(titleMap);
        }

        SimpleAdapter adapter = new SimpleAdapter(this, titles, R.layout.articleitem, new String[] {"title"}, new int[] {R.id.title});
        articleList.setAdapter(adapter);
        articleList.setOnItemClickListener(this);
    }

    private void startArticleService(String url) {
        Intent articlesService = new Intent(Constants.ACTION_GET_ARTICLES);
        articlesService.putExtra(Constants.INTENT_CHANNEL_URL, url);
        Log.d(TAG, "start service to get articles");
        startService(articlesService);
    }

    private void stopArticleService() {
        Intent articlesService = new Intent(Constants.ACTION_GET_ARTICLES);
        this.stopService(articlesService);
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        ProgressDlg.showProgress(this, null, R.string.SID_LOADING);
        String url = chMgr.getChannelUrl(tab.getPosition());
        startArticleService(url);
        currentChannelUrl = url;
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // refresh button
        menu.add("Refresh")
        .setIcon(R.drawable.ic_refresh)
        .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        // sub menus
        SubMenu subMenu1 = menu.addSubMenu("Action Item");
        loginMenu = subMenu1.add("login");
        setLoginMenu();
        subMenu1.add("exit").setTitle(R.string.exit_app);

        MenuItem subMenu1Item = subMenu1.getItem();
        subMenu1Item.setIcon(R.drawable.ic_title_share_default);
        subMenu1Item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        return super.onCreateOptionsMenu(menu);
    }

    private void logoff() {
        new Thread() {
            public void run() {
                if (AccountManager.getInstance().logoff()) {
                    logoffHandler.sendEmptyMessage(MSG_LOGOFF_SUCCESS);
                } else {
                    logoffHandler.sendEmptyMessage(MSG_LOGOFF_FAILED);
                }
            }
        }.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals("Refresh")) {
            ProgressDlg.showProgress(this, null, R.string.SID_LOADING);
            startArticleService(currentChannelUrl);
        } else if (item.getTitle().equals(this.getResources().getString(R.string.title_activity_login))) {
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
        } else if (item.getTitle().equals(this.getResources().getString(R.string.title_activity_logoff))) {
            logoff();
        } else if (item.getTitle().equals(this.getResources().getString(R.string.exit_app))) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
        Map<String, String> articleMap = (Map<String, String>)adapter.getItemAtPosition(position);
        if (articleMap == null) {
            return;
        }
        String url = articleMap.get("url");
        String title = articleMap.get("title");
        showArticle(title, url);
    }

    private void showArticle(String title, String url) {
        Intent showIntent = new Intent(this, ArticleActivity.class);
        showIntent.putExtra(Constants.INTENT_ARTICLE_TITLE, title);
        showIntent.putExtra(Constants.INTENT_ARTICLE_URL, url);
        this.startActivity(showIntent);
    }

    private void setLoginMenu() {
        if (AccountManager.getInstance().isLogin()) {
            loginMenu.setTitle(R.string.title_activity_logoff);
        } else {
            loginMenu.setTitle(R.string.title_activity_login);
        }
    }

    @Override
    protected void onResume() {
        if (loginMenu != null) {
            setLoginMenu();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        stopArticleService();
        this.unregisterBroadcastReceiver();
        super.onDestroy();
    }

}
