package com.example.android.hellovideo;

import android.widget.MediaController;

import java.lang.reflect.Member;

public class VideoModel {

    private String videoUrl,videoId;
    private String uploderId;
    private String uploderName;
    private long likes;
//    private int views;

    public VideoModel(String videoUrl, String videoId, long likes, String uploderName, String uploderId) {
        this.videoUrl = videoUrl;
        this.videoId = videoId;
        this.likes = likes;
        this.uploderName = uploderName;
        this.uploderId = uploderId;
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

    public String getUploderId() {
        return uploderId;
    }

    public void setUploderId(String uploderId) {
        this.uploderId = uploderId;
    }

    public String getUploderName() {
        return uploderName;
    }

    public void setUploderName(String uploderName) {
        this.uploderName = uploderName;
    }


}
