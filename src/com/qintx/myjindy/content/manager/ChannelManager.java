package com.qintx.myjindy.content.manager;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.content.Context;
import android.util.Log;

import com.qintx.myjindy.constant.Constants;
import com.qintx.myjindy.content.entity.Channel;
import com.qintx.myjindy.utility.Config;

public class ChannelManager {

    private static final String TAG = "ChannelManager";
    private Context mContext = null;
    private List<Channel> mChannels = null;

    public ChannelManager(Context context) {
        mContext = context;
        init();
    }

    public List<Channel> getChannels() {
        return mChannels;
    }

    public String getChannelUrl(int index) {
        if (mChannels == null || index <0 || index >= mChannels.size()) {
            return null;
        }
        return mChannels.get(index).getUrl();
    }

    private void init() {
        mChannels = loadChannels();
    }

    private List<Channel> loadChannels() {
        List<Channel> channels = new ArrayList<Channel>();
        Document doc = Config.loadXmlFile(mContext, Constants.CHANNEL_FILE);
        if (doc == null) {
            return null;
        }
        Elements elements = doc.select("channel");
        for (int i = 0; i < elements.size(); i++) {
            try {
                Channel channel = new Channel();
                channel.setName(elements.get(i).select("name").get(0).text());
                channel.setUrl(elements.get(i).select("url").get(0).text());
                channels.add(channel);
            } catch(Exception e) {
                Log.e(TAG, "get channel error:" + e);
                continue;
            }
        }
        return channels;
    }

}
