package com.example.android.hellovideo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    TextView nameField, emailField, UIdField;
    ImageView profilePicture;
    Button logoutButton;
    ProgressBar progressBar;
    Boolean forMyProfile;
    Map<String, Object> data;
    String Uid ;

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
        progressBar = findViewById(R.id.progressBarProfileActivity);
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
        }
        else {
            emailField.setVisibility(View.GONE);
        }

        String documentpath = "users"+ "/" + Uid;
        progressBar.setVisibility(View.VISIBLE);
        Map<String, Object> dataFromCall = new HashMap<>();

        if(forMyProfile == false || UserData.userId == null) {
            progressBar.setVisibility(View.VISIBLE);
            data = Database.getUserData(documentpath,ProfileActivity.this, new FirebaseResultListener() {
                @Override
                public void onComplete() {
                    progressBar.setVisibility(View.INVISIBLE);
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
            progressBar.setVisibility(View.INVISIBLE);
            setUserData();
        }



        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.logout();
//                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
//                startActivity(intent);
                onBackPressed();
            }
        });

    }

    private void setUserData() {
        nameField.setText((String) data.get("name"));
        UIdField.setText((String) data.get("Uid"));
        emailField.setText((String) data.get("email"));
    }

    }
