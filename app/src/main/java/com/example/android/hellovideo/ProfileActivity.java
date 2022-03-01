package com.example.android.hellovideo;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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

    RecyclerView recyclerView;
    ArrayList<VideoModel> videos;
    VideoAdapter adapter;



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
            Database.getDocument(documentpath,ProfileActivity.this, new FirebaseResultListener() {
                @Override
                public void onComplete() {

                }

                        @Override
                        public void onComplete(DocumentSnapshot documentSnapshot) {
                            Map<String ,Object> documentData = documentSnapshot.getData();
                            for (Map.Entry<String, Object> entry : documentData.entrySet()) {
                                data.put(entry.getKey(), entry.getValue());
                            }
                            data.put("Uid", Uid);
                            setUserData();
                        }

                        @Override
                        public void onComplete(List<DocumentSnapshot> documentSnapshotList) {

                        }
                    });
        }
        else {
            data.put("name", UserData.name);
            data.put("email", UserData.email);
            data.put("Uid", Uid);
            data.put("profilePictureUrl", UserData.profilePictureUrl);
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
        recyclerView = findViewById(R.id.recycleView);
//        setVideos();
        getVideosId();

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
//            Glide.with(this).load(url).into(profilePicture);
            Glide.with(this).load(url).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    profileProgressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    profileProgressBar.setVisibility(View.GONE);
                    return false;
                }
            }).placeholder(R.drawable.ic_baseline_account_circle_24)
                    .transform(new CircleCrop())
                    .into(profilePicture);
        }
        profileProgressBar.setVisibility(View.GONE);
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
        adapter = new VideoAdapter(ProfileActivity.this, videos, true, false);
        GridLayoutManager layoutManager=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void getVideos(List<String> documentsIds) {
        Database.getSpecificDocuments("all_videos", documentsIds, ProfileActivity.this, new FirebaseResultListener() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onComplete(DocumentSnapshot documentSnapshot) {

            }

            @Override
            public void onComplete(List<DocumentSnapshot> documentSnapshotList) {
                videos = new ArrayList<VideoModel>();
                for(DocumentSnapshot document : documentSnapshotList) {
                    if(document.exists()) {
                        Map<String, Object> vid = document.getData();
                        videos.add(new VideoModel(vid.get("UrlPath").toString(), document.getId(), (long) vid.get("likes"),
                                vid.get("uploaderName").toString(), vid.get("uploaderId").toString()));
                        Log.d("ProfileVid", document.getId());
                        Log.d("ProfileVid", vid.get("UrlPath").toString());
                    }
                }
                setVideos();
            }
        });
    }

    private void getVideosId() {
        Database.getCollection("users" + "/" + Uid + "/" + "videos", ProfileActivity.this, new FirebaseResultListener() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onComplete(DocumentSnapshot documentSnapshot) {

            }

            @Override
            public void onComplete(List<DocumentSnapshot> documentSnapshotList) {
                List<String> documentIds = new LinkedList<>();
                for(DocumentSnapshot document : documentSnapshotList) {
                    documentIds.add(document.getId());
                }
                getVideos(documentIds);
            }
        });
    }


    }
