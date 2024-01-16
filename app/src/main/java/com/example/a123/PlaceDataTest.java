package com.example.a123;

import com.google.firebase.firestore.GeoPoint;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PlaceDataTest {
    @Test
    public void testPlaceDataConstructorAndGetters() {
        String placeId = "123";
        String placeName = "Test Place";
        double latitude = 12.345;
        double longitude = 67.890;

        PlaceData placeData = new PlaceData(placeId, placeName, latitude, longitude);

        assertEquals(placeId, placeData.getPlaceId());
        assertEquals(placeName, placeData.getPlaceName());
        GeoPoint location = placeData.getLocation();
        assertEquals(latitude, location.getLatitude(), 0.001);
        assertEquals(longitude, location.getLongitude(), 0.001);
    }

    @Test
    public void testPlaceDataDefaultConstructor() {

        PlaceData placeData = new PlaceData();

        assertNotNull(placeData);
    }
}
