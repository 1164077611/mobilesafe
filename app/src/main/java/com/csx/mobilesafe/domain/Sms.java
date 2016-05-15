package com.csx.mobilesafe.domain;

/**
 * Created by csx on 2016/5/1.
 */
public class Sms {
    public static final String BODY="body";
    public static final String DATE="date";
    public static final String TYPE="type";
    public static final String ADDRESS="address";

    private String body;//短信内容
    private long date;//短信事件
    private int type;//短信种类，是发出的还是收到的
    private String address;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Sms{" +
                "body='" + body + '\'' +
                ", date=" + date +
                ", type=" + type +
                ", address='" + address + '\'' +
                '}';
    }
}
