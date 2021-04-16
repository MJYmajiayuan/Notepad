package com.android.notepad.login.model;

public class Tag {

    private int tagId;
    private String tagName;
    private int tagNum;

    public Tag(String tagName, int tagNum) {
        this.tagName = tagName;
        this.tagNum = tagNum;
    }

    public Tag(int tagId, String tagName, int tagNum) {
        this.tagId = tagId;
        this.tagName = tagName;
        this.tagNum = tagNum;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getTagNum() {
        return tagNum;
    }

    public void setTagNum(int tagNum) {
        this.tagNum = tagNum;
    }
}
