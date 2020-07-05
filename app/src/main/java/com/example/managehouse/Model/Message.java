package com.example.managehouse.Model;

import org.json.JSONObject;

public class Message {

    private String title;
    private String[] body;
    private String data;
    private int status;

    public Message(String title, String[] body, int status, String data) {
        this.title = title;
        this.body = body;
        this.status = status;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getBody() {
        return body;
    }

    public void setBody(String[] body) {
        this.body = body;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
