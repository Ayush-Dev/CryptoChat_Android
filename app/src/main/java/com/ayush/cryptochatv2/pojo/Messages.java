package com.ayush.cryptochatv2.pojo;

public class Messages {

    private String owner, message, timestamp;

    public Messages(){}

    public Messages(String owner, String message) {
        this.owner = owner;
        this.message = message;
        this.timestamp = Long.toString(System.currentTimeMillis());
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getOwner() {
        return owner;
    }

    public String getMessage() {
        return message;
    }
}
