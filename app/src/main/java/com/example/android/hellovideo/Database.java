package com.example.android.hellovideo;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public class Database extends AppCompatActivity {


    private static final String GET_TAG = "GET";
    private static final String UPLOAD_TAG = "UPLOAD";
    private static final String URL_FIELD_VIDEO = "UrlPath", URL_FIELD_IMAGE = "profilePictureUrl";
    private static final String COLLECTION_NAME_ALL_VIDEOS = "all_videos";
    private static final String VIDEO_UPLOAD_SUCCESS_MESSAGE = "Video Uploaded Successfully";
    private static final String PROFILE_UPLOAD_SUCCESS_MESSAGE = "Profile picture updated successfully";

    public static void upload(Uri uri, Context context, boolean isVideo) {

        FirebaseStorage storage = FirebaseStorage.getInstance();
        String uniqueId = String.valueOf(System.currentTimeMillis());
        StorageReference storageRef = storage.getReference().child(uniqueId);

        UploadTask uploadTask = storageRef.putFile(uri);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.d(UPLOAD_TAG, String.valueOf(task.getException()));
                    throw task.getException();
                }
                return storageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                Log.d("UPLOAD",downloadUri.toString());

                uploadUrl(downloadUri, context, uniqueId, isVideo);
            }
        });
    }
    private static void uploadImageUrl(Uri downloadUri, Context context, String uniqueId) {
        Map<String, Object> data = new HashMap<>();
        data.put(URL_FIELD_IMAGE, downloadUri.toString());
        String documentPath = "users"+"/"+Auth.getUId();
        addDocument(documentPath, data, PROFILE_UPLOAD_SUCCESS_MESSAGE,context);
        UserData.profilePictureUrl = downloadUri.toString();
    }
    private static void uploadUrl(Uri downloadUri, Context context, String uniqueId, boolean isVideo) {
        if (! isVideo) {
            Map<String, Object> data = new HashMap<>();
            data.put(URL_FIELD_IMAGE, downloadUri.toString());
            String documentPath = "users"+"/"+Auth.getUId();
            updateFields(documentPath, data, PROFILE_UPLOAD_SUCCESS_MESSAGE,context);
            UserData.profilePictureUrl = downloadUri.toString();
            return;
        }
        Map<String, Object> vid = new HashMap<>();
        vid.put("likes", 0);
        vid.put(URL_FIELD_VIDEO, downloadUri.toString());

        if(UserData.name == null) {
            Database.getUserData("users" + "/" + Auth.getUId(), context, new FirebaseResultListener() {
                        @Override
                        public void onComplete() {
                            vid.put("uploaderId", UserData.userId);
                            vid.put("uploaderName", UserData.name);
                            addDocument(COLLECTION_NAME_ALL_VIDEOS+"/"+uniqueId, vid, VIDEO_UPLOAD_SUCCESS_MESSAGE, context);

                            Map<String, Object> nullMap = new HashMap<>();
                            String documentPath = "users"+"/"+Auth.getUId()+"/"+ "videos"+"/"+uniqueId;
                            addDocument(documentPath, nullMap, VIDEO_UPLOAD_SUCCESS_MESSAGE, context);
                        }
                    }
                    , true);
        }
        else {
            vid.put("uploaderId", UserData.userId);
            vid.put("uploaderName", UserData.name);
            addDocument(COLLECTION_NAME_ALL_VIDEOS+"/"+uniqueId, vid, VIDEO_UPLOAD_SUCCESS_MESSAGE, context);

            Map<String, Object> nullMap = new HashMap<>();
            String documentPath = "users"+"/"+Auth.getUId()+"/"+ "videos"+"/"+uniqueId;
            addDocument(documentPath, nullMap, VIDEO_UPLOAD_SUCCESS_MESSAGE, context);
        }

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

    public static void updateFields(String documentPath, Map<String, Object> data, String message, Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.document(documentPath);
        documentReference.update(data).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(message != "")
                            Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    public static void deleteFromFirebseStorage (String url) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        ref.delete();
    }

    public static ArrayList getData(Context context, FirebaseResultListener firebaseResultListener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<VideoModel> videos = new ArrayList<>();
        db.collection(COLLECTION_NAME_ALL_VIDEOS).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots.isEmpty()) {
                    Toast.makeText(context, "Failed to get data", Toast.LENGTH_SHORT).show();
                } else {
                    List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot document : documents) {
                        if (document.exists()) {
                            Map<String, Object> vid = document.getData();
                            MainActivity.videos.add(new VideoModel(vid.get("UrlPath").toString(), document.getId(), (long) vid.get("likes"),
                                    vid.get("uploaderName").toString(), vid.get("uploaderId").toString()));
                            Log.d(GET_TAG,document.getId());
                        }
                    }
                }
                if(Auth.isLoggedIn()) {
                    if(UserData.likedVideos == null)
                        Database.getLikedVideos(firebaseResultListener);
                    else
                        firebaseResultListener.onComplete();
                }
                else{
                    UserData.likedVideos = new HashSet<>();
                    firebaseResultListener.onComplete();
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
        DocumentReference documentReferenceVideos = db.collection(COLLECTION_NAME_ALL_VIDEOS).document(vidUniqueId);
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

    public static void getLikedVideos (FirebaseResultListener firebaseResultListener) {
        if(UserData.likedVideos == null)
            UserData.likedVideos = new HashSet<>();
        String path = "users"+"/"+Auth.getUId()+"/"+"liked_videos";
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(path).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                for (DocumentSnapshot document : documents) {
                    UserData.likedVideos.add(document.getId());
                }
                firebaseResultListener.onComplete();
            }
        });
    }

    public static Map<String, Object> getUserData(String documentPath, Context context, FirebaseResultListener firebaseResultListener, boolean forCurrUser) {
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
                    if(forCurrUser == true) {
                        UserData.userId = Auth.getUId();
                        UserData.name = (String) documentData.get("name");
                        UserData.email = (String) documentData.get("email");
                        if (documentData.containsKey(URL_FIELD_IMAGE)) {
                            UserData.profilePictureUrl = (String) documentData.get(URL_FIELD_IMAGE);
                        }
                    }
                    firebaseResultListener.onComplete();
                }
                else {
                    Toast.makeText(context, "Failed to get User data", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return data;
    }

}
