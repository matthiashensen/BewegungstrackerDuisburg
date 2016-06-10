package org.hsbo.gierthhensen.bewegungstrackerduisburg;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.location.ActivityRecognition;

//TODO: Android 6.0 Compatibility

/**
 * LocationService / registered in Manifest
 */

public class LocationService extends IntentService {

    private static MyLocationListener myLocationListener;

    // same as in StartActivity
    private static final String BROADCAST = "gierthhensen.hsbo.org.bewegungstrackerduisburg.BROADCAST";
    private static final String DATA = "gierthhensen.hsbo.org.bewegungstrackerduisburg.DATA";

    public LocationService() {
        super("Location Service");
    }

    /**
     * Handles incoming intents
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        String type = intent.getStringExtra("type");
        int trackingRate = intent.getIntExtra("trackingRate", 10000);

        if (myLocationListener == null) {
            myLocationListener = new MyLocationListener(this);
        }

        myLocationListener.setTrackingRate(trackingRate);

        switch (type) {
            case "startTracking":
                myLocationListener.startLocationUpdates();
                break;
            case "endTracking":
                myLocationListener.stopLocationUpdates();
            default:
        }
    }

    /**
     * Sends location as intent
     * @param location
     */
    public void sendLocation(Location location) {
        // send intent with data
        Intent lIntent =
                new Intent(BROADCAST)
                        .putExtra(DATA, location);
        LocalBroadcastManager.getInstance(this).sendBroadcast(lIntent);
    }

    /**
     * LocationListener which calls GoogleAPIs for location via GPS
     */
    private class MyLocationListener implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<Status> {

        private GoogleApiClient myGoogleApiClient;
        private Location myLastLocation;
        private LocationRequest myLocationRequest;
        private LocationService myLocationService;

        private long UPDATE_in_MS = 5 * 1000;


        public MyLocationListener(LocationService locationService) {

            myLocationService = locationService;

            // see android training
            if (myGoogleApiClient == null) {
                myGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .addApi(ActivityRecognition.API)
                        .build();
            }
        }

        /**
         * send request when connected to GoogleAPI
         * @param bundle
         */
        @Override
        public void onConnected(@Nullable Bundle bundle) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: CHECK PERMISSION
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            myLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    myGoogleApiClient);

            myLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(UPDATE_in_MS)
                    .setFastestInterval(UPDATE_in_MS);

            LocationServices.FusedLocationApi
                    .requestLocationUpdates(myGoogleApiClient, myLocationRequest, this);
            ActivityRecognition.ActivityRecognitionApi
                    .requestActivityUpdates(myGoogleApiClient, UPDATE_in_MS, getActivityDetectionPendingIntent()).setResultCallback(this);
        }

        @Override
        public void onConnectionSuspended(int intent) {
            //TODO: On Suspend
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            //TODO: When connection failed
        }

        public void setTrackingRate(int trackingRate) {
            UPDATE_in_MS = trackingRate;
        }

        private PendingIntent getActivityDetectionPendingIntent() {
            Intent intent = new Intent(getBaseContext(), ActivityIntents.class);
            return PendingIntent.getService(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        /**
         * called when location has changed
         * @param location
         */
        @Override
        public void onLocationChanged(Location location) {
            myLastLocation = location;
            myLocationService.sendLocation(location);
        }

        /**
         * Start getting location updates
         */
        public void startLocationUpdates() {
            myGoogleApiClient.connect();
        }

        /**
         * Stop getting location updates
         */
        public void stopLocationUpdates() {
            if (myGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(myGoogleApiClient, this);
                ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(myGoogleApiClient, getActivityDetectionPendingIntent());
            }
            myGoogleApiClient.disconnect();
        }

        @Override
        public void onResult(@NonNull Status status) {

        }
    }
}
