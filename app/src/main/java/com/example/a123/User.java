package com.example.a123;
<<<<<<< Updated upstream

public class User {
=======
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class User {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
>>>>>>> Stashed changes
    public String email;
    public String userUID;
    public String password;

<<<<<<< Updated upstream
    public User(){
=======
    public User() {

>>>>>>> Stashed changes
    }

    public User(String email, String userUID,String password){
        this.email = email;
        this.userUID = userUID;
        this.password = password;
<<<<<<< Updated upstream
    }
}
=======

    }

    public void listenToFirestoreChanges() {
        db.collection("test")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@NonNull QuerySnapshot queryDocumentSnapshots, @NonNull FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "New document: " + dc.getDocument().getData());
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified document: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed document: " + dc.getDocument().getData());
                                    break;
                            }
                        }
                    }
                });
    }
}


>>>>>>> Stashed changes

