package com.example.android.hellovideo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {

    Activity context;


    private ArrayList<VideoModel> videos;
    private HashSet<String> likedVideos;

    public VideoAdapter(ArrayList<VideoModel> videos, HashSet<String> likedVideos, Activity context) {
        this.videos = videos;
        this.context = context;
        this.likedVideos = likedVideos;
    }

    public ArrayList<VideoModel> getVideos() {
        return videos;
    }

    public void setVideos(ArrayList<VideoModel> videos) {
        this.videos = videos;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_video_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.setData(videos.get(position));


        holder.uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.selectAndUpload(context);
            }
        });

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Auth.isLoggedIn() == true) {
                updateLikeStatus(holder, position);
                }
                else {
                    ShowDialogBox.showLoginDialogBox(context);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }


    private void updateLikeStatus (final MyViewHolder holder, int position) {
        long likes = Long.parseLong(holder.numberOfLikes.getText().toString());
        if (likedVideos.contains(videos.get(position).getVideoId()) != true) {
            holder.likeButton.setBackgroundResource(R.drawable.red_like_button);
            Database.updateLikes(true, videos.get(position).getVideoId());
            likedVideos.add(videos.get(position).getVideoId());
            likes++;
        }
        else {
            holder.likeButton.setBackgroundResource(R.drawable.like_button);
            Database.updateLikes(false, videos.get(position).getVideoId());
            likedVideos.remove(videos.get(position).getVideoId());
            likes--;
        }
        holder.numberOfLikes.setText(String.valueOf(likes));
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        ProgressBar progressBar;
        Button uploadButton, likeButton;
        TextView numberOfLikes;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.videoView);
            progressBar = itemView.findViewById(R.id.progressBar);
            uploadButton = itemView.findViewById(R.id.uploadButton);
            numberOfLikes = itemView.findViewById(R.id.numberOfLikes);
            likeButton = itemView.findViewById(R.id.likeButton);
        }

        void setData(VideoModel videoModel) {
            videoView.setVideoPath(videoModel.getVideoUrl());
            numberOfLikes.setText(String.valueOf(videoModel.getLikes()));
            if ( likedVideos.contains(videoModel.getVideoId()) ) {
                likeButton.setBackgroundResource(R.drawable.red_like_button);
            }
            else {
                likeButton.setBackgroundResource(R.drawable.like_button);
            }

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    progressBar.setVisibility(View.INVISIBLE);
                    mediaPlayer.start();
                }
            });

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.start();
                }
            });
        }
    }
}
