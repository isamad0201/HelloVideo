package com.example.android.hellovideo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Database extends AppCompatActivity {


    private static final String GET_TAG = "GET";
    private static final String UPLOAD_TAG = "UPLOAD";

    public static void upload(Uri uri, Context context) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        String vidUniqueId = String.valueOf(System.currentTimeMillis());
        StorageReference storageRef = storage.getReference().child(vidUniqueId);

        UploadTask uploadTask = storageRef.putFile(uri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.d(UPLOAD_TAG, ">>>>>>>>>>"+String.valueOf(task.getException()));
                    throw task.getException();
                }

                return storageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                Log.d("UPLOAD",downloadUri.toString());
                uploadVideoUrl(downloadUri.toString(), context, vidUniqueId);
                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static void uploadVideoUrl(String videoUrl, Context context, String vidUniqueId) {


        Map<String, Object> vid = new HashMap<>();

        vid.put("UrlPath", videoUrl);
        vid.put("likes", 0);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("all_videos").document(vidUniqueId).set(vid).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"nwe Upload success",Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public static ArrayList getData(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<VideoModel> videos = new ArrayList<>();
        db.collection("all_videos").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    Toast.makeText(context, "Failed to get data", Toast.LENGTH_SHORT).show();
                } else {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot document : documents) {
                        if (document.exists()) {
                            Map<String, Object> vid = document.getData();
                            videos.add(new VideoModel(vid.get("UrlPath").toString(), document.getId(), (long) vid.get("likes")));
                            Log.d(GET_TAG,document.getId());
                        }
                    }
                }
            }
        });
        return videos;
    }

}
