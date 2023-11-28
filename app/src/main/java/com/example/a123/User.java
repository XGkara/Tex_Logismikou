package com.example.a123;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class User {
    private String email;
    private String password;

    // Required empty constructor for Firestore
    public User() {
    }
    public User(String email, String password) {
        this.email = email;
        this.password = password;

    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
}




