package com.example.android.hellovideo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    TextView nameField, emailField, UIdField, textViewProfileEmail;
    ImageView profilePicture, profileImageChange, profileImageChangeCameraIcon;
    Button logoutButton;
    ProgressBar profileProgressBar;
    Boolean forMyProfile;
    Map<String, Object> data;
    String Uid ;
    View emailDash;

    static ViewPager2 viewPager2;
    static ArrayList<VideoModel> videos;
    static VideoAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // Remove status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_profile);

        nameField = findViewById(R.id.profileName);
        emailField = findViewById(R.id.profileEmail);
        UIdField = findViewById(R.id.profileUId);
        logoutButton = findViewById(R.id.logoutButton);
        profilePicture = findViewById(R.id.profilePicture);
        textViewProfileEmail = findViewById(R.id.textViewProfileEmail);
        emailDash = findViewById(R.id.dash3);
        profileImageChange = findViewById(R.id.profileImageChange);
        profileImageChangeCameraIcon = findViewById(R.id.profileImageChangeCameraIcon);
        profileProgressBar = findViewById(R.id.profileProgress);

        data = new HashMap<>();
        forMyProfile = true;
        Uid = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Uid =  extras.getString("Uid");
            forMyProfile =  Uid.equals(Auth.getUId());
            Log.d("PRO2", Uid);
        }
        Log.d("PRO2","OUT-"+forMyProfile);

        if(forMyProfile == true) {
            emailField.setVisibility(View.VISIBLE);
            textViewProfileEmail.setVisibility(View.VISIBLE);
            emailDash.setVisibility(View.VISIBLE);
            profileImageChangeCameraIcon.setVisibility(View.VISIBLE);
            profileImageChange.setVisibility(View.VISIBLE);
        }
        else {
            emailField.setVisibility(View.GONE);
            textViewProfileEmail.setVisibility(View.GONE);
            emailDash.setVisibility(View.GONE);
            profileImageChange.setVisibility(View.GONE);
            profileImageChangeCameraIcon.setVisibility(View.GONE);
        }

        String documentpath = "users"+ "/" + Uid;
        Map<String, Object> dataFromCall = new HashMap<>();

        if(forMyProfile == false || UserData.userId == null) {
            data = Database.getUserData(documentpath,ProfileActivity.this, new FirebaseResultListener() {
                @Override
                public void onComplete() {
                    data.put("Uid", Uid);
                    setUserData();
                }
            }
            , forMyProfile);
        }
        else {
            data.put("name", UserData.name);
            data.put("email", UserData.email);
            data.put("Uid", Uid);
            setUserData();
        }



        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.logout();
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
                onBackPressed();
            }
        });
        viewPager2 = findViewById(R.id.viewpager);

        setVideos();

        profileImageChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, PICK_IMAGE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("UPLOAD", "Img selected");
        super.onActivityResult(requestCode, resultCode, data);
        if(PICK_IMAGE == requestCode || resultCode == RESULT_OK || data != null || data.getData()!=null) {
            Uri uri = data.getData();
            if(uri.toString().contains("image")) {
                ShowDialogBox.showSelectImageDialogBox(uri, ProfileActivity.this, new ProfilePictureUpdateResultListener() {
                    @Override
                    public void onComplete() {
                        setProfilePicture(UserData.profilePictureUrl);
                    }
                });
            } else {
                Toast.makeText(ProfileActivity.this, "Choose image only", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void setProfilePicture(String url) {
        profileProgressBar.setVisibility(View.VISIBLE);
        if(url != null && url != "") {
            Glide.with(this).load(url).into(profilePicture);
        }
        profileProgressBar.setVisibility(View.INVISIBLE);
    }

    private void setUserData() {
        nameField.setText((String) data.get("name"));
        UIdField.setText((String) data.get("Uid"));
        emailField.setText((String) data.get("email"));
        String profileUrl = "";
        if(data.containsKey("profilePictureUrl"))
            profileUrl = (String) data.get("profilePictureUrl");
        setProfilePicture(profileUrl);
    }

    private void setVideos() {
        videos = MainActivity.videos;
        adapter = new VideoAdapter(ProfileActivity.this);
        viewPager2.setAdapter(adapter);
    }
    }
