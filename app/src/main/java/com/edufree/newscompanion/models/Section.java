package com.edufree.newscompanion.models;

public class Section {

    private String id;
    private String webTitle;
    private String webUrl;

    public Section(String id, String webTitle, String webUrl) {
        this.id = id;
        this.webTitle = webTitle;
        this.webUrl = webUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public void setWebTitle(String webTitle) {
        this.webTitle = webTitle;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }
}
