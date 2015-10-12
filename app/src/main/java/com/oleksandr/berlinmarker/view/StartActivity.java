package com.oleksandr.berlinmarker.view;

import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.activeandroid.query.Select;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oleksandr.berlinmarker.R;
import com.oleksandr.berlinmarker.model.MapMarkers;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * @author Oleksandr Dudinskyi (dudinskyj@gmail.com)
 */
public class StartActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final float BERLIN_LATITUDE = 52.5f;
    private static final float MIN_BERLIN_LATITUDE = 50.0f;
    private static final float MAX_BERLIN_LATITUDE = 55.0f;
    private static final float BERLIN_LONGITUDE = 13.4f;
    private static final float MIN_BERLIN_LONGITUDE = 10.0f;
    private static final float MAX_BERLIN_LONGITUDE = 16.0f;
    private static final int ZOOM_LEVEL = 10;
    private Random mRandom = new Random();

    private GoogleMap mMap;
    private List<MapMarkers> mMarkersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMarkersList = new Select().from(MapMarkers.class).execute();
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(BERLIN_LATITUDE, BERLIN_LONGITUDE);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, ZOOM_LEVEL));
        if (mMarkersList != null) {
            for (MapMarkers mapMarkers : mMarkersList) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(mapMarkers.latitude, mapMarkers.longitude));
                markerOptions.title(getMarkerTitle(mapMarkers.latitude, mapMarkers.longitude));
                mMap.addMarker(markerOptions);
            }
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

                // Creating a marker
                MarkerOptions markerOptions = new MarkerOptions();

                // Setting the position for the marker
                markerOptions.position(latLng);

                markerOptions.title(getMarkerTitle(latLng.latitude, latLng.longitude));
                MapMarkers mapMarkers = new MapMarkers(latLng.latitude, latLng.longitude);
                mapMarkers.save();
                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                // Placing a marker on the touched position
                mMap.addMarker(markerOptions);
            }
        });
    }

    private void startAnimation(final MarkerOptions markerOptions, final LatLng target) {

        final long duration = 3000;
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();

        Point startPoint = proj.toScreenLocation(markerOptions.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);

        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * target.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * target.latitude + (1 - t) * startLatLng.latitude;
                markerOptions.position(new LatLng(lat, lng));
                if (t < 1.0) {
                    // Post again 10ms later.
                    handler.postDelayed(this, 10);
                } else {
                    // animation ended
                }
            }
        });
    }

    private String getMarkerTitle(double latitude, double longitude) {
        Geocoder gcd = new Geocoder(StartActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                address.getLocality();
                return address.getLocality() + ", " + address.getCountryName();
            }
        } catch (IOException e) {
            e.printStackTrace();
            //add default title
            return latitude + " : " + longitude;
        }
        return latitude + " : " + longitude;
    }

    private void generateALotOfMarkers() {
        for (int i = 0; i < 1000; i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            float latitude = nextFloat(MIN_BERLIN_LATITUDE, MAX_BERLIN_LATITUDE);
            float longitude = nextFloat(MIN_BERLIN_LONGITUDE, MAX_BERLIN_LONGITUDE);
            markerOptions.position(new LatLng(latitude, longitude));
            markerOptions.title(getMarkerTitle(latitude, longitude));
            mMap.addMarker(markerOptions);
        }
    }

    public float nextFloat(float min, float max) {
        return min + mRandom.nextFloat() * (max - min);
    }
}
