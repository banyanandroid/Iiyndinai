package com.banyan.iiyndinai.activity;

/**
 * Created by User on 9/3/2016.
 */
public class Msg {
    String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msgs;
    }

    public void setMsg(String msgs) {
        this.msgs = msgs;
    }

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    String msgs;
    String serverTime;
    public Msg(String title, String msg, String serverTime) {

    }
}
