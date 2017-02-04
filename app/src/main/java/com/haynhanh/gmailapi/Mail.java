package com.haynhanh.gmailapi;

/**
 * Created by thieumao on 2/1/17.
 */

public class Mail {

    private String subject;
    private String content;
    private String from;
    private String date;

    public Mail(String subject, String content, String from, String date) {
        this.subject = subject;
        this.content = content;
        this.from = from;
        this.date = date;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
