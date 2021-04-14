package com.android.notepad.login.model;

import java.io.Serializable;
import java.util.Date;

public class Note implements Serializable {
    private int id;
    private String content;
    private String time;
    private long timestamp;
    private String image;
    private String sound;

    public Note() {
    }

    public Note (String content, String time, long timestamp, String image, String sound) {
        this.content = content;
        this.time = time;
        this.timestamp = timestamp;
        this.image = image;
    }

    public Note(int id, String content, String time, long timestamp, String image, String sound) {
        this.id = id;
        this.content = content;
        this.time = time;
        this.timestamp = timestamp;
        this.image = image;
        this.sound = sound;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }
}
