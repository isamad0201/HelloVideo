package com.example.android.hellovideo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

public class ProfileVideos extends AppCompatActivity {

    static ArrayList<VideoModel> videos;
    static VideoAdapter adapter;
    static RecyclerView recyclerView;
    static int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // Remove status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.profile_videos);

        position = 0;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            position =  Integer.valueOf(extras.getString("Position"));
        }

        recyclerView = findViewById(R.id.recycleView);
        adapter = new VideoAdapter(ProfileVideos.this, videos, false, true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.getLayoutManager().scrollToPosition(position);
        recyclerView.setAdapter(adapter);

    }

}
