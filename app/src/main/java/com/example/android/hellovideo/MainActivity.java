package com.example.android.hellovideo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_FILE = 1;
    static ViewPager2 viewPager2;
    static ArrayList<VideoModel> videos;
    static HashSet<String> likedVideos;
    Button profileButton;
    static ProgressBar progressBar;
    static VideoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // Remove status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBarMainPage);
        progressBar.setVisibility(View.VISIBLE);

        viewPager2 = (ViewPager2) findViewById(R.id.viewpager);
//        videos = Database.getData(MainActivity.this);
        this.setVideos();
        profileButton = findViewById(R.id.profileButton);

        setProfileButtonListner();

        addDefaultVideoUrl(videos);


    }

    private void setProfileButtonListner() {
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(! Auth.isLoggedIn()) {
                    ShowDialogBox.showLoginDialogBox(MainActivity.this);
                }
                else {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    intent.putExtra("Uid", Auth.getUId());
                    startActivity(intent);
                }
            }
        });
    }

    private void setVideos() {
        videos = Database.getData(MainActivity.this, new FirebaseResultListener() {
            @Override
            public void onComplete() {
                Collections.shuffle(videos);
                progressBar.setVisibility(View.INVISIBLE);
                adapter = new VideoAdapter(MainActivity.this);
                viewPager2.setAdapter(adapter);
                Log.d("DEBUG","in listener");
            }
        });
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

    public static void selectAndUpload(Activity context) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        context.startActivityForResult(galleryIntent, PICK_FILE);

        // its onActivityResult() is in MainActivity
    }

    private void addDefaultVideoUrl (ArrayList videos) {
//        videos.add(new VideoModel("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4","", 0,"u1",""));
//        videos.add(new VideoModel("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4","", 0,"u2",""));
//        videos.add(new VideoModel("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4","", 0,"u3",""));
//        videos.add(new VideoModel("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4","", 0,"u4",""));
//        videos.add(new VideoModel("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/VolkswagenGTIReview.mp4","", 0,"u5",""));
    }


}