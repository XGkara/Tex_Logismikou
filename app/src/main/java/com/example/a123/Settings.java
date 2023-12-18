package com.example.a123;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

public class Settings extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView uploadImageView;
    private EditText uploadEmailEditText;
    private Button saveButton;
    private NavigationView navigationView;
    private View headerView;
    private TextView userEmailTextView;
    private ImageView userImageView;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout3);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        headerView = navigationView.getHeaderView(0);

        userEmailTextView = headerView.findViewById(R.id.UserEmail);
        userImageView = headerView.findViewById(R.id.UserImage);

        uploadImageView = findViewById(R.id.uploadImage);
        uploadEmailEditText = findViewById(R.id.uploadEmail);
        saveButton = findViewById(R.id.saveButton);

        loadUserData();
        navigationView.setNavigationItemSelectedListener(this);

        uploadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImagePicker();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = uploadEmailEditText.getText().toString().trim();
                userEmailTextView.setText(userEmail);

                saveUserData(userEmail, selectedImageUri);

                if (selectedImageUri != null) {
                    userImageView.setImageURI(selectedImageUri);
                }

                closeDrawer();
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            uploadImageView.setImageURI(selectedImageUri);
        }
    }

    private void saveUserData(String userEmail, Uri imageUri) {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_email", userEmail);
        editor.putString("user_image", imageUri != null ? imageUri.toString() : "");
        editor.apply();
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String storedEmail = prefs.getString("user_email", "");
        String storedImageUriString = prefs.getString("user_image", "");

        if (userEmailTextView != null) {
            userEmailTextView.setText(storedEmail);
        }

        if (userImageView != null && storedImageUriString != null && !storedImageUriString.isEmpty()) {
            Uri storedImageUri = Uri.parse(storedImageUriString);
            userImageView.setImageURI(storedImageUri);
        }
    }

    private void closeDrawer() {
        if (navigationView != null) {
            DrawerLayout drawerLayout = findViewById(R.id.drawer_layout3);
            drawerLayout.closeDrawer(GravityCompat.START);
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

        return true;
    }

}

