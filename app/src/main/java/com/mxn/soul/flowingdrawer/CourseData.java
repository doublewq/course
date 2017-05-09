package com.mxn.soul.flowingdrawer;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by .liu on 17/04/2017.
 */

public class CourseData implements Serializable {

    @SerializedName("")
    private String title;
    @SerializedName("")
    private String content;
    @SerializedName("")
    private String info;
    @SerializedName("")
    private String author;

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

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
