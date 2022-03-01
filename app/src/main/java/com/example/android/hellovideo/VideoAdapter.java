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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyViewHolder> {

    Activity context;
    ArrayList<VideoModel> videos;
    boolean justVideo, inProfileVideo;

    public VideoAdapter(Activity context, ArrayList<VideoModel> videos, boolean justVideo, boolean inProfileVideo) {
        this.context = context;
        this.videos = videos;
        this.justVideo = justVideo;
        this.inProfileVideo = inProfileVideo;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_video_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        if(! justVideo)
            holder.setDataForVideo(videos.get(position));
        else
            holder.setDataForImage(videos.get(position), position);

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
        if (UserData.likedVideos.contains(videos.get(position).getVideoId()) != true) {
            holder.likeButton.setBackgroundResource(R.drawable.red_like_button);
            Database.updateLikes(true, videos.get(position).getVideoId());
            UserData.likedVideos.add(videos.get(position).getVideoId());
            likes++;
        }
        else {
            holder.likeButton.setBackgroundResource(R.drawable.like_button);
            Database.updateLikes(false, videos.get(position).getVideoId());
            UserData.likedVideos.remove(videos.get(position).getVideoId());
            likes--;
        }
        holder.numberOfLikes.setText(String.valueOf(likes));
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        VideoView videoView;
        ProgressBar progressBar;
        Button uploadButton, likeButton;
        TextView numberOfLikes, uploaderName;
        ImageView imageView, playIcon;
        boolean paused;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            videoView = itemView.findViewById(R.id.videoView);
            progressBar = itemView.findViewById(R.id.progressBar);
            uploadButton = itemView.findViewById(R.id.uploadButton);
            numberOfLikes = itemView.findViewById(R.id.numberOfLikes);
            likeButton = itemView.findViewById(R.id.likeButton);
            uploaderName = itemView.findViewById(R.id.uploderNameTextView);
            imageView = itemView.findViewById(R.id.imageView);
            playIcon = itemView.findViewById(R.id.playIcon);
            paused = false;
        }

        void setDataForImage(VideoModel videoModel, int position) {
            playIcon.setVisibility(View.GONE);
            removeButtons();
            videoView.setVisibility(View.GONE);
//            Glide.with(context).load(videoModel.getVideoUrl()).into(imageView);

            Glide.with(context).load(videoModel.getVideoUrl()).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    progressBar.setVisibility(View.GONE);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ProfileVideos.videos = videos;
                            Intent intent = new Intent(context, ProfileVideos.class);
                            intent.putExtra("Position", String.valueOf(position));
                            context.startActivity(intent);
                        }
                    });
                    return false;
                }
            }).into(imageView);
        }

        void setDataForVideo(VideoModel videoModel) {
            playIcon.setVisibility(View.GONE);
            imageView.setVisibility(View.GONE);
            videoView.setVideoPath(videoModel.getVideoUrl());
            numberOfLikes.setText(String.valueOf(videoModel.getLikes()));
            if (UserData.likedVideos != null && UserData.likedVideos.contains(videoModel.getVideoId()) ) {
                likeButton.setBackgroundResource(R.drawable.red_like_button);
            }
            else {
                likeButton.setBackgroundResource(R.drawable.like_button);
            }

            uploaderName.setText("@"+videoModel.getUploderName());
            uploaderName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(inProfileVideo == true){
                        context.finish();
                    }
                    else {
                        Intent intent = new Intent(context, ProfileActivity.class);
                        intent.putExtra("Uid", videoModel.getUploderId());
                        context.startActivity(intent);
                    }
                }
            });


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

            videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(paused == true) {
                        videoView.resume();
                        playIcon.setVisibility(View.GONE);
                    }
                    else {
                        videoView.pause();
                        playIcon.setVisibility(View.VISIBLE);
                    }
                    paused = paused^true;
                }
            });

        }

        private void removeButtons() {
            uploaderName.setVisibility(View.GONE);
            uploadButton.setVisibility(View.GONE);
            likeButton.setVisibility(View.GONE);
            numberOfLikes.setVisibility(View.GONE);
        }

    }
}
