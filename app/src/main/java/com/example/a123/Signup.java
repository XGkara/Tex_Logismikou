package com.example.a123;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.MotionEffect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonqLogIn, buttonSignUp;
    FirebaseAuth mAuth;
    ProgressBar progressBar;

    FirebaseDatabase database;
    DatabaseReference reference;
    private DatabaseReference mDatabase;

    String userID = "test";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initializeViews();
        setClickListeners();
    }

    private void initializeViews() {
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonSignUp = findViewById(R.id.signupbtn);
        progressBar = findViewById(R.id.progressBar);
        buttonqLogIn = findViewById(R.id.qLogIn);
    }

    private void setClickListeners() {
        buttonqLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLogin();
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpUser();
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }

    private void signUpUser() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        handleSignUpResult(task);
                    }
                });
    }

    private void handleSignUpResult(@NonNull Task<AuthResult> task) {
        progressBar.setVisibility(View.GONE);

        if (task.isSuccessful()) {
            saveUserDataToDatabase();
            showToast("You have signed up successfully!");

        } else {
            showToast("Signup failed: " + task.getException().getMessage());
        }
    }

    private void saveUserDataToDatabase() {



        mDatabase = FirebaseDatabase.getInstance().getReference();

        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();

        writeNewUser(userID,email,password);


    }

    public void writeNewUser(String userId, String password, String email) {
        User user = new User(email, password);

        mDatabase.child("users").child(userId).setValue(user);
    }

    private void showToast(String message) {
        Toast.makeText(Signup.this, message, Toast.LENGTH_SHORT).show();
    }


}
