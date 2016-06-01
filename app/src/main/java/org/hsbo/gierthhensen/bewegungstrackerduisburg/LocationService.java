package org.hsbo.gierthhensen.bewegungstrackerduisburg;

import android.app.IntentService;
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
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationService extends IntentService {

    private static MyLocationListener myLocationListener;
    private static final String BROADCAST = "gierthhensen.hsbo.org.bewegungstrackerduisburg.BROADCAST";
    private static final String DATA = "gierthhensen.hsbo.org.bewegungstrackerduisburg.DATA";

    public LocationService() {
        super("Location Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String type = intent.getStringExtra("type");
        if (myLocationListener == null) {
            myLocationListener = new MyLocationListener(this);
        }

        switch (type) {
            case "start":
                myLocationListener.startLocationUpdates();
                break;
            case "end":
                myLocationListener.stopLocationUpdates();
            default:
        }
    }

    public void sendLocation(Location location) {
        Intent lIntent =
                new Intent(BROADCAST)
                        .putExtra(DATA, location);
        LocalBroadcastManager.getInstance(this).sendBroadcast(lIntent);
    }

    private class MyLocationListener implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

        private GoogleApiClient myGoogleApiClient;
        private Location myLastLocation;
        private LocationRequest myLocationRequest;
        private LocationService myLocationService;

        private static final long UPDATE_MS = 5 * 1000;
        private static final long FASTEST_MS = 5 * 1000;

        public MyLocationListener(LocationService locationService) {

            myLocationService = locationService;

            // see android training
            if (myGoogleApiClient == null) {
                myGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
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
                    .setInterval(UPDATE_MS)
                    .setFastestInterval(FASTEST_MS);

            LocationServices.FusedLocationApi
                    .requestLocationUpdates(myGoogleApiClient, myLocationRequest, this);
        }

        @Override
        public void onConnectionSuspended(int i) {
            //TODO
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }

        @Override
        public void onLocationChanged(Location location) {
            myLastLocation = location;
            myLocationService.sendLocation(location);
        }

        public void startLocationUpdates() {
            myGoogleApiClient.connect();
        }

        public void stopLocationUpdates() {
            if (myGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(myGoogleApiClient, this);
            }
            myGoogleApiClient.disconnect();
        }
    }
}
