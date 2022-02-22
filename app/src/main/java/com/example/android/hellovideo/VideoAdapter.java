package com.example.android.hellovideo;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {

    private static final int PICK_FILE = 1;
    Activity context;


    private ArrayList<VideoModel> videos;

    public VideoAdapter(ArrayList<VideoModel> videos, Activity context) {
        this.videos = videos;
        this.context = context;
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
                selectAndUpload(context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }
    public void selectAndUpload(Activity context) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        context.startActivityForResult(galleryIntent, PICK_FILE);

        // its onActivityResult() is in MainActivity
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        ProgressBar progressBar;
        Button uploadButton;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.videoView);
            progressBar = itemView.findViewById(R.id.progressBar);
            uploadButton = itemView.findViewById(R.id.uploadButton);
        }

        void setData(VideoModel videoModel) {
            videoView.setVideoPath(videoModel.getVideoUrl());

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
