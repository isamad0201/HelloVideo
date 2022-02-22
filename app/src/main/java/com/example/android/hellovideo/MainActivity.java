package com.example.android.hellovideo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_FILE = 1;
    ViewPager2 viewPager2;
    ArrayList<VideoModel> videos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // Remove status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        viewPager2 = (ViewPager2) findViewById(R.id.viewpager);
        videos = Database.getData(MainActivity.this);

        addDefaultVideoUrl(videos);
        Collections.shuffle(videos);
        viewPager2.setAdapter(new VideoAdapter(videos, MainActivity.this));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("UPLOAD", "Vid selected");
        super.onActivityResult(requestCode, resultCode, data);
        if(PICK_FILE == requestCode || resultCode == RESULT_OK || data != null || data.getData()!=null) {
            Uri uri = data.getData();
            if(uri.toString().contains("video")) {
                Log.d("UPLOAD",uri.getPath().toString());
                Database.upload(uri, MainActivity.this);
            } else {
                Toast.makeText(MainActivity.this, "Choose video only", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void addDefaultVideoUrl (ArrayList videos) {
        videos.add(new VideoModel("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4","", 0));
        videos.add(new VideoModel("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4","", 0));
        videos.add(new VideoModel("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4","", 0));
        videos.add(new VideoModel("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4","", 0));
        videos.add(new VideoModel("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/VolkswagenGTIReview.mp4","", 0));
    }


}