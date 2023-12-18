package com.example.a123;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
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

public class AboutUs extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    private TextView navHeaderEmail;
    private ImageView navHeaderImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout1);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        // Access the TextView and ImageView directly from the nav_header
        navHeaderEmail = navigationView.getHeaderView(0).findViewById(R.id.UserEmail);
        navHeaderImage = navigationView.getHeaderView(0).findViewById(R.id.UserImage);

        // Load user data and update the navigation header
        loadUserData();
    }

    private void loadUserData() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String storedEmail = prefs.getString("user_email", "");
        String storedImageUriString = prefs.getString("user_image", "");

        // Update TextView in nav_header
        if (navHeaderEmail != null) {
            navHeaderEmail.setText(storedEmail);
        }

        // Update ImageView in nav_header
        if (navHeaderImage != null && storedImageUriString != null && !storedImageUriString.isEmpty()) {
            Uri storedImageUri = Uri.parse(storedImageUriString);
            navHeaderImage.setImageURI(storedImageUri);
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
        } else if (item.getItemId() == R.id.nav_settings) {
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivity(intent);
            finish();
        } else if (item.getItemId() == R.id.nav_aboutus) {
            // No need to recreate the current activity
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
