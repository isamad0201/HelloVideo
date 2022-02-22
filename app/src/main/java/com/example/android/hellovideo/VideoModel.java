package com.example.android.hellovideo;

import android.widget.MediaController;

import java.lang.reflect.Member;

public class VideoModel {

    private String videoUrl,videoId;
    private long likes;
//    private int views;

    public VideoModel(String videoUrl, String videoId, long likes) {
        this.videoUrl = videoUrl;
        this.videoId = videoId;
        this.likes = likes;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }


}
