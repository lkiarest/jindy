package com.qintx.myjindy.content.entity;

public class Channel {

    private String name;
    private String url;
    
    public Channel() {
        
    }

    public Channel(String name, String url) {
        super();
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
}
