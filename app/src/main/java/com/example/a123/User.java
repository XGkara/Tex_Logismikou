package com.example.a123;

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


    // Required empty constructor for Firestore



    private static final String TAG = "User";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String email;
    private String password;
    private String userId;

    public User() {
    }

    public void fetchUserData(String userId, final UserDataCallback callback) {
        db.collection("users")
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                email = document.getString("email");
                                password = document.getString("password");
                                callback.onUserDataFetched(userId, email, password);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.w(TAG, "Error getting document", task.getException());
                        }
                    }
                });

    }



    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUserId() {
        return userId;
    }

    public interface UserDataCallback {
        void onUserDataFetched(String userId, String email, String password);
    }
}




