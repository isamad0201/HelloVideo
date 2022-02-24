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

import java.util.HashMap;
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
                            Database.uploadInFireStore(data, context,COLLECTION_USER ,user.getUid(), "SignedUp Successfully");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public static void login(String email, String password, Context context, Activity activity) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(context, "Logged in successfully",Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
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
}
