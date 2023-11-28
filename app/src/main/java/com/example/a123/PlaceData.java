package com.example.a123;
import com.google.firebase.firestore.GeoPoint;

public class PlaceData {
        private String placeId;
        private String placeName;
        private GeoPoint location;
        // Required empty constructor for Firestore
        public PlaceData() {
        }
        public PlaceData(String placeId, String placeName, double latitude, double longitude) {
            this.placeId = placeId;
            this.placeName = placeName;
            this.location = new GeoPoint(latitude, longitude);
        }
        public String getPlaceId() {
            return placeId;
        }
        public String getPlaceName() {
            return placeName;
        }
        public GeoPoint getLocation() {
            return location;
        }
    }