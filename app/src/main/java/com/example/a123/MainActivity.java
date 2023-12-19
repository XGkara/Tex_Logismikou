package com.example.a123;

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
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, RoutingListener {

    private final static int LOCATION_REQUEST_CODE = 23;

    final String placeId = "ChIJgUbEo8cfqokR5lP9_Wh_DaM";
    final List placeFields = Arrays.asList(Place.Field.NAME, Place.Field.RATING, Place.Field.OPENING_HOURS);

    private LocationHelper locationHelper;

    private final int FINE_PERMISSION_CODE = 1;
    private GoogleMap myMap;

    Location currentLocation, destinationLocation = null;
    protected LatLng start=null;
    protected LatLng end=null;
    private List<Polyline> polylines=null;

    private static final String TAG = "User";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseAuth auth;
    FirebaseUser user;

    private DatabaseReference reference;
    private String userID;

    private DrawerLayout drawerLayout;



    private void clearPolylines() {
        if (polylines != null) {
            for (Polyline line : polylines) {
                line.remove();
            }
            polylines.clear();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

                    addPlaceToFirestore(place);



                } else {
                    placeInfo.append("Place LatLng is null").append("\n");

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
                showPlaceDetailsDialog(placeInfo.toString(), place);

            }




            @Override
            public void onError(Status status) {
                // Handle the error.
                Toast.makeText(MainActivity.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void showPlaceDetailsDialog(String placeDetails, Place place) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Place Details")
                .setMessage(placeDetails)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Calculate Distance", (dialog, which) -> {
                    if (place != null && place.getLatLng() != null && currentLocation != null) {
                        clearPolylines();
                        start = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        end = place.getLatLng();
                        Findroutes(start, end);
                    }
                })
                .show();
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

        myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                end=latLng;

                myMap.clear();

                start=new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
                //start route finding
                Findroutes(start,end);

                //calculate distance
                float[] results = new float[1];
                Location.distanceBetween(
                        currentLocation.getLatitude(),
                        currentLocation.getLongitude(),
                        end.latitude,
                        end.longitude,
                        results);

                Toast.makeText(MainActivity.this, "Απόσταση: " + results[0] + " meters", Toast.LENGTH_SHORT).show();
            }
        });

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

    private void Findroutes(LatLng start, LatLng end) {

        if(start==null || end==null) {
            Toast.makeText(MainActivity.this,"Unable to get location", Toast.LENGTH_LONG).show();
        }
        else
        {

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(start, end)
                    .key("AIzaSyAkN5S8_mhBiljsTKC7LuvT_eCt1Z8DQFI")  //also define your api key here.
                    .build();
            routing.execute();
        }
    }

    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar= Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();
//        Findroutes(start,end);
    }

    public void onRoutingStart() {
        Toast.makeText(MainActivity.this,"Εύρεση Διαδρομής...",Toast.LENGTH_LONG).show();
    }

    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        
        myMap.clear();
        CameraUpdate center = CameraUpdateFactory.newLatLng(start);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
        if(polylines!=null) {
            polylines.clear();
        }
        PolylineOptions polyOptions = new PolylineOptions();
        LatLng polylineStartLatLng=null;
        LatLng polylineEndLatLng=null;
        float totalDistance = 0;


        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i <route.size(); i++) {

            if(i==shortestRouteIndex)
            {
                polyOptions.color(ContextCompat.getColor(this, R.color.colorPrimary));
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());

                Polyline polyline = myMap.addPolyline(polyOptions);
                polylineStartLatLng=polyline.getPoints().get(0);
                int k=polyline.getPoints().size();
                polylineEndLatLng=polyline.getPoints().get(k-1);
                polylines.add(polyline);

                //upologismos apostashs
                totalDistance += route.get(shortestRouteIndex).getDistanceValue();

            }
            else {

            }

        }

        //Add Marker on route starting position
        MarkerOptions startMarker = new MarkerOptions();
        startMarker.position(polylineStartLatLng);
        startMarker.title("My Location");
        myMap.addMarker(startMarker);

        //Add Marker on route ending position
        MarkerOptions endMarker = new MarkerOptions();
        endMarker.position(polylineEndLatLng);
        endMarker.title("Destination");
        myMap.addMarker(endMarker);

        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, "Απόσταση: " + formatDistance(totalDistance), Snackbar.LENGTH_LONG);
        snackbar.setDuration(6000);
        snackbar.show();
    }

    private String formatDistance(float distance) {
        if (distance < 1000) {
            return String.format("%.0f meters", distance);
        } else {
            return String.format("%.2f km", distance / 1000);
        }
    }
    public void onRoutingCancelled() {

        Findroutes(start,end);
    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Findroutes(start,end);

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