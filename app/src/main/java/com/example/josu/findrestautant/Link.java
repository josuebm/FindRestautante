package com.example.josu.findrestautant;

/**
 * Created by stage on 9/04/15.
 */
public class Link {

    private String title, url;

    public Link() {
    }

    public Link(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Links{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
