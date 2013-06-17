package com.qintx.myjindy.utility;

import java.io.IOException;
import java.io.InputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.content.Context;

public class Config {

    public static Document loadXmlFile(Context context, String fileName) {
        InputStream in = null;
        Document doc = null;
        try {
            in = context.getAssets().open(fileName);
            doc = Jsoup.parse(in, "utf-8", "");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return doc;
    }
}
