package com.dash.myapplication;

import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import static java.security.AccessController.getContext;

public class MapsActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        OnMarkerDragListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    protected Location mLastLocation;
    private Marker myLocationMarker;
    //protected TextView mLatitudeText;
    //protected TextView mLongitudeText;
    private GoogleApiClient mGoogleApiClient;
    public static final String TAG = MapsActivity.class.getSimpleName();
    private LocationRequest mLocationRequest = new LocationRequest();

    public Map<String, Location> mLocations = new HashMap<>();
    public Map<String, Marker> mMarkers = new HashMap<>();

    public String androidId;

    HttpClient httpclient = new DefaultHttpClient();

    // for colors
    float[] hues = {210.0f, 240.0f, 180.0f, 120.0f, 300.0f, 30.0f, 0.0f, 330.0f, 270.0f, 60.0f};
    int hueRotator = 0;
    boolean zoomOnce = true;
    boolean justUpdatedPin = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        setUpMapIfNeeded();
        buildGoogleApiClient();
        // Create the LocationRequest object
        createLocationRequest();

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        myLocationMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(39.327099, -76.6208752))
                .title(androidId.toString())
                .snippet("BITCH")
                .icon(BitmapDescriptorFactory.defaultMarker(hues[hueRotator++ % hues.length])));
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        mMap.setOnMarkerDragListener(this);


        //if (mLastLocation == null) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        //}
        //else {
            handleNewLocation(mLastLocation);
        //}

        //postData();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services connected.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    private void handleNewLocation(Location location) {
        // send new location to server
        //postData().execute();
        // set myLocationMarker to the new location
        myLocationMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        if(zoomOnce) {
            mMap.animateCamera(CameraUpdateFactory
                    .newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17.0f));
            zoomOnce=false;
        }
        Log.d(TAG, location.toString());

        // Let the server know about our updated location
        new UploadLocationTask(this).execute(location);

        //location.

        //Here's how we declare a new latlng, for the sake of placing future points
        // private static final LatLng MELBOURNE = new LatLng(-37.813, 144.962);
    }


    protected void createLocationRequest() {
        mLocationRequest.setInterval(100);
        mLocationRequest.setFastestInterval(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onLocationChanged(Location location) {

        handleNewLocation(location);
        new DownloadLocationsTask(this).execute();

        redrawMarkers();

        Log.d(TAG, mLocations.toString());
    }


    // iterate through mLocations
    // if uid has a marker, update marker with location
    // else make a new
    private void redrawMarkers() {
        Iterator it = mLocations.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry<String, Location> pair = (Map.Entry<String, Location>)it.next();
            String uid = pair.getKey();
            Location l = pair.getValue();
            Marker m;
            // if we already know about Marker m,
            if (mMarkers.containsKey(uid)) {
                // set markers positions from mMarkers HashMap updated by DownloadLocationsTask.java
                m = mMarkers.get(uid);
                // don't set position when "pin" and justUpdatedPin
                if (!uid.equals("pin") || !justUpdatedPin) {
                    m.setPosition(new LatLng(l.getLatitude(), l.getLongitude()));
                } else {
                    justUpdatedPin = false;
                }
            // add new markers (other than yourself)
            } else if (!uid.equals(androidId)) {
                m = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(l.getLatitude(), l.getLongitude()))
                        .title(androidId.toString())
                        .snippet("BITCH")
                        .icon(BitmapDescriptorFactory.defaultMarker(hues[hueRotator++ % hues.length])));
                // set pin to draggable
                if (uid.equals("pin")) { m.setDraggable(true); }
                // add new marker to HashMap mMarkers
                mMarkers.put(uid, m);
            }
        }
    }

    public void pinDrop(View view) {
        // if pin doesn't exist, make one
        // else update location?

        //int drawableResourceId = this.getResources().getIdentifier("dash.png", "drawable", this.getPackageName());
        if (mMarkers.containsKey("pin")) {
            // do some stuff?

        } else {
            Marker goal;
            goal = mMap.addMarker(new MarkerOptions()
                    .position(mMap.getCameraPosition().target)
                    .draggable(true)
                            //.icon(BitmapDescriptorFactory.fromResource(drawableResourceId)));
                    .icon(BitmapDescriptorFactory.defaultMarker(hues[hueRotator++ % hues.length])));
            // wyman quad
            mMarkers.put("pin", goal);
            new UploadPinTask(this).execute(goal.getPosition());

            //mMarkers.put
        }

    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
       new UploadPinTask(this).execute(marker.getPosition());
       justUpdatedPin = true;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

}
