package com.example.a123;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private final static int LOCATION_REQUEST_CODE = 23;

    final String placeId = "ChIJgUbEo8cfqokR5lP9_Wh_DaM";
   
    private LocationHelper locationHelper;

    private TextView testText;

    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap myMap;

    Location currentLocation, destinationLocation = null;

    FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseAuth auth;
    FirebaseUser user;




    private User user1 = new User();

    private DatabaseReference reference;
    private String userID;

    private DrawerLayout drawerLayout;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testText = findViewById(R.id.testText);
        Places.initialize(getApplicationContext(), "AIzaSyAkN5S8_mhBiljsTKC7LuvT_eCt1Z8DQFI");
        PlacesClient placesClient = Places.createClient(this);
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);


        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(41.07670157862302, 23.554400400271827),
                new LatLng(41.091226420839696, 23.54935511484131)));


        autocompleteFragment.setCountries("GR");

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.OPENING_HOURS, Place.Field.WEBSITE_URI, Place.Field.ICON_URL, Place.Field.ICON_BACKGROUND_COLOR,Place.Field.PHONE_NUMBER, Place.Field.ADDRESS));
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseUser currentUser = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();
        userID = user.getUid();
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }


        // Initialize LocationHelper in the onCreate method
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize LocationHelper in the onCreate method
        locationHelper = new LocationHelper(this, fusedLocationProviderClient);

        // Call getLastLocation from LocationHelper
        locationHelper.getLastLocation(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                    mapFragment.getMapAsync(MainActivity.this);
                }
            }
        });


        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            List<Address> addressList = null;


            @Override
            public void onPlaceSelected(@NonNull Place place) {
                StringBuilder placeInfo = new StringBuilder();
                placeInfo.append("Place Name: ").append(place.getName()).append("\n\n");

                if (place.getLatLng() != null) {


                    // Move the map camera to the selected place
                    myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 17.0f));

                    // Add a marker at the selected place
                    myMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName()));

                    // Add the place information to Firestore

                }

                // Check for additional details
                if (place.getPhoneNumber() != null) {
                    placeInfo.append("Phone Number: ").append(place.getPhoneNumber()).append("\n\n");
                }

                if (place.getAddress() != null) {
                    placeInfo.append("Address: ").append(place.getAddress()).append("\n\n");
                }


                if (place.getOpeningHours() != null) {
                    placeInfo.append("Opening Hours: ").append(place.getOpeningHours().getWeekdayText()).append("\n\n");
                }

                // Show the information in a Dialog
                showPlaceDetailsDialog(placeInfo.toString());

                addPlaceToFirestore(place);
            }



            @Override
            public void onError(Status status) {
                // Handle the error.
                Toast.makeText(MainActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void addPlaceToFirestore(Place place) {
        // Create a reference to the "places" collection
        CollectionReference placesRef = db.collection("places");

        // Create a document with a unique ID
        String documentId = placesRef.document().getId();

        // Create a Place object with the required information
        PlaceData placeData = new PlaceData(
                place.getId(),
                place.getName(),
                place.getLatLng().latitude,
                place.getLatLng().longitude
        );

        // Add the place data to Firestore
        placesRef.document(documentId).set(placeData);
    }


    private void showPlaceDetailsDialog(String placeDetails) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Place Details")
                .setMessage(placeDetails)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }



    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {




        myMap = googleMap;

        if (currentLocation != null) {
            LatLng lcn = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            myMap.addMarker(new MarkerOptions().position(lcn).title("My Location"));
            myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lcn, 15f));
        } else {
            Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show();


        }

        LatLng serres = new LatLng(41.07670157862302, 23.554400400271827);
        myMap.addMarker(new MarkerOptions().position(serres).title("serres")
                .icon(bitmapDescriptor(getApplicationContext(), R.drawable.pin)));
        LatLng hotel = new LatLng(41.10409809049689, 23.549068805161838);
        myMap.addMarker(new MarkerOptions().position(hotel).title("hotel")
                .icon(bitmapDescriptor(getApplicationContext(), R.drawable.pin)));


        LatLng mouseio_serres = new LatLng(41.091226420839696, 23.54935511484131);
        myMap.addMarker(new MarkerOptions().position(mouseio_serres).title("mouseio_serres")
                .icon(bitmapDescriptor(getApplicationContext(), R.drawable.pin)));


        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.getUiSettings().setCompassEnabled(true);

    }






    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        } else if (item.getItemId() == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private BitmapDescriptor bitmapDescriptor(Context context, int vectorResId){
        Drawable vectorDrawable= ContextCompat.getDrawable(context,vectorResId);
        vectorDrawable.setBounds(0,0, vectorDrawable.getIntrinsicWidth(),vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap=Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas=new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Call getLastLocation from LocationHelper
                locationHelper.getLastLocation(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLocation = location;
                            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                            mapFragment.getMapAsync(MainActivity.this);
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Location permission is denied, please allow the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
}





