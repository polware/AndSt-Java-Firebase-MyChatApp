package com.network.chattingapp.models;

public class InboxModel {
    public String message;
    public String from;

    public InboxModel() {}

    public InboxModel(String message, String from) {
        this.message = message;
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
