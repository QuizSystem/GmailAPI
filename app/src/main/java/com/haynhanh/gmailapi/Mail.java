package com.haynhanh.gmailapi;

/**
 * Created by thieumao on 2/1/17.
 */

public class Mail {

    private String title;
    private String content;
    private String from;
    private String to;
    private String date;

    public Mail(String title, String content, String from, String to, String date) {
        this.title = title;
        this.content = content;
        this.from = from;
        this.to = to;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
