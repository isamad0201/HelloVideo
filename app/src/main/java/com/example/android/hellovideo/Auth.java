package com.example.android.hellovideo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Auth {

    private static final String COLLECTION_USER = "users";
    private static FirebaseAuth mAuth ;
    static {
        mAuth = FirebaseAuth.getInstance();
    }

    public static void signin(String email, String password, String name, Context context, Activity activity) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Map<String, Object> data = new HashMap<>();
                            data.put("email", email);
                            data.put("name", name);
                            data.put("profilePictureUrl", "");
                            String documentPath = COLLECTION_USER+"/"+user.getUid();
                            Database.addDocument(documentPath, data, "SignedUp Successfully", context);
                            UserData.name = name;
                            UserData.email = email;
                            UserData.userId = mAuth.getUid();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                        MainActivity.adapter.notifyDataSetChanged();
                    }
                });
    }


    public static void login(String email, String password, Context context, Activity activity) {

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("TAG", "signInWithEmail:success");
                            Toast.makeText(context, "Logged in successfully",Toast.LENGTH_SHORT).show();
                            Database.getDocument("users" + "/" + Auth.getUId(), context, new FirebaseResultListener() {
                                @Override
                                public void onComplete() {

                                }

                                @Override
                                public void onComplete(DocumentSnapshot documentSnapshot) {
                                    Map<String ,Object> documentData = documentSnapshot.getData();
                                    UserData.profilePictureUrl = (String) documentData.get("profilePictureUrl");
                                    UserData.name = (String) documentData.get("name");
                                    UserData.email = (String) documentData.get("email");
                                    UserData.userId = Auth.getUId();
                                }

                                @Override
                                public void onComplete(List<DocumentSnapshot> documentSnapshotList) {

                                }
                            });

                            Database.getLikedVideos(new FirebaseResultListener() {
                                @Override
                                public void onComplete() {
                                    MainActivity.adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onComplete(DocumentSnapshot documentSnapshot) {

                                }

                                @Override
                                public void onComplete(List<DocumentSnapshot> documentSnapshotList) {

                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        MainActivity.adapter.notifyDataSetChanged();
                    }
                });
    }

    public static boolean isLoggedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            return false;
        }
        else {
            return true;
        }
    }
    public static String getUId() {
        if(mAuth.getCurrentUser() != null) {
            return mAuth.getUid();
        }
        else
            return "";
    }

    public static void logout() {
        UserData.clear();
        mAuth.signOut();
        MainActivity.adapter.notifyDataSetChanged();
    }
}
