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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class Database extends AppCompatActivity {


    private static final String GET_TAG = "GET";
    private static final String UPLOAD_TAG = "UPLOAD";
    private static final String COLLECTION_NAME = "all_videos";
    private static final String VIDEO_UPLOAD_SUCCESS_MESSAGE = "Video Uploaded Successfully";

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

                Map<String, Object> vid = new HashMap<>();
                vid.put("UrlPath", downloadUri.toString());
                vid.put("likes", 0);
                vid.put("uploderId", UserData.userId);
                vid.put("uploderName", UserData.name);
                addDocument(COLLECTION_NAME+"/"+vidUniqueId, vid, VIDEO_UPLOAD_SUCCESS_MESSAGE, context);
                Map<String, Object> nullMap = new HashMap<>();
                String documentPath = "users"+"/"+Auth.getUId()+"/"+"videos"+"/"+vidUniqueId;
                addDocument(documentPath, nullMap, "",context);
            }
        });
    }

    public static void addDocument(String documentPath, Map<String, Object> data, String message, Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.document(documentPath);
        documentReference.set(data).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(message != "")
                            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }


    public static ArrayList getData(Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<VideoModel> videos = new ArrayList<>();
        db.collection(COLLECTION_NAME).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    Toast.makeText(context, "Failed to get data", Toast.LENGTH_SHORT).show();
                } else {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot document : documents) {
                        if (document.exists()) {
                            Map<String, Object> vid = document.getData();
                            videos.add(new VideoModel(vid.get("UrlPath").toString(), document.getId(), (long) vid.get("likes"),
                                    vid.get("uploderName").toString(), vid.get("uploderId").toString()));
                            Log.d(GET_TAG,document.getId());
                        }
                    }
                }
            }
        });
        return videos;
    }

    public static void updateLikes (boolean increment, String vidUniqueId) {
        if (vidUniqueId == "") {
            return;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReferenceVideos = db.collection(COLLECTION_NAME).document(vidUniqueId);
        String documentPath = "users"+"/"+Auth.getUId()+"/"+"liked_videos"+"/"+vidUniqueId;
        DocumentReference documentReferenceUsers = db.document(documentPath);

        if(increment == true) {
            documentReferenceVideos.update("likes", FieldValue.increment(1));
            documentReferenceUsers.set(new HashMap<>());
        }
        else {
            documentReferenceVideos.update("likes", FieldValue.increment(-1));
            documentReferenceUsers.delete();
        }

    }

    public static HashSet<String> getLikedVideos () {
        HashSet<String> likedVideos = new HashSet<>();
        String path = "users"+"/"+Auth.getUId()+"/"+"liked_videos";
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(path).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot document : documents) {
                    likedVideos.add(document.getId());
                }
            }
        });
        return likedVideos;
    }

    public static Map<String, Object> setUserData(String documentPath, Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> data = new HashMap<>();

        db.document(documentPath).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String ,Object> documentData = documentSnapshot.getData();
                    Log.d("USER_DATA",String.valueOf(documentData.size())+"SIZE");
                    for (Map.Entry<String, Object> entry : documentData.entrySet()) {
                        data.put(entry.getKey(), entry.getValue());
                        Log.d("USER_DATA",entry.getKey()+"="+entry.getValue());
                    }
                    UserData.userId = Auth.getUId();
                    UserData.name = (String) documentData.get("name");
                    UserData.email = (String) documentData.get("email");
                }
                else {
                    Toast.makeText(context, "Failed to get User data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return data;
    }

}
