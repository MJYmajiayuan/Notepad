package com.android.notepad.login.model;

import java.io.Serializable;
import java.util.Date;

public class Note implements Serializable {
    private int id;
    private String content;
    private String time;
    private long timestamp;

    public Note() {
    }

    public Note (String content, String time, long timestamp) {
        this.content = content;
        this.time = time;
        this.timestamp = timestamp;
    }

    public Note(int id, String content, String time, long timestamp) {
        this.id = id;
        this.content = content;
        this.time = time;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
