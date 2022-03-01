package com.example.android.hellovideo;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public interface FirebaseResultListener {
    void onComplete();
    void onComplete(DocumentSnapshot documentSnapshot);
    void onComplete(List<DocumentSnapshot> documentSnapshotList);
}
