package com.example.a123;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

public class Settings extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;

    private EditText emailEditText, passwordEditText;
    private Button saveButton;
    private ImageView uploadImageView;
    private SharedPreferences sharedPreferences;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        NavigationView navigationView = findViewById(R.id.nav_view);

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Find views by ID
        emailEditText = findViewById(R.id.uploadEmail);
        saveButton = findViewById(R.id.saveButton);
        uploadImageView = findViewById(R.id.uploadImage);

        // Set click listener for the saveButton
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get entered email and password
                String enteredEmail = emailEditText.getText().toString();
                String enteredPassword = passwordEditText.getText().toString();

                // Save the entered data in SharedPreferences
                saveUserData(enteredEmail, enteredPassword);

                // Update the nav_header with the entered email and password
                updateNavHeader(enteredEmail, enteredPassword);
            }
        });

        // Set click listener for the uploadImage
        uploadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker();
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout3);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            try {
                // Set the selected image to uploadImageView
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                uploadImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveUserData(String email, String password) {
        // Use SharedPreferences.Editor to save user data
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_email", email);
        editor.putString("user_password", password);
        // Save the image URI as a string
        editor.putString("user_image", selectedImageUri.toString());
        editor.apply();
    }

    private void updateNavHeader(String email, String password) {
        // Load TextViews and ImageView in nav_header by ID
        TextView navHeaderEmail = findViewById(R.id.UserEmail);
        ImageView navHeaderImage = findViewById(R.id.UserImage);

        // Update TextViews and ImageView in nav_header
        navHeaderEmail.setText(email);
        if (selectedImageUri != null) {
            navHeaderImage.setImageURI(selectedImageUri);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_home) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.nav_logout) {

            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.nav_history) {
            Intent intent = new Intent(getApplicationContext(), History.class);
            startActivity(intent);
            finish();
        }
        else if (item.getItemId() == R.id.nav_settings) {
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivity(intent);
            finish();
        }
        else if (item.getItemId() == R.id.nav_aboutus) {
            Intent intent = new Intent(getApplicationContext(), AboutUs.class);

            startActivity(intent);
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    public void onUploadImageClick(View view) {
        openImagePicker();
    }
}

