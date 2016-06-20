package org.hsbo.gierthhensen.bewegungstrackerduisburg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.app.NotificationCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

/**
 * Created by Lukas Gierth on 26.05.16.
 */

@SuppressLint({"ShowToast"})
public class StartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // objects for location
    private Intent myLocationServiceIntent;
    private Location myLastLocation;
    private DetectedActivity myLikelyActivity;

    private static boolean gpsStat = false;
    private static boolean map_active = false;

    private StatusFragment myStatusFragment;
    private MapFragment myMapFragment;

    public static final String UPDATE_INTERVAL = "updateInterval";

    private static String latitude;
    private static String longitude;

    // same as in LocationService - unique name
    private static final String BROADCAST = "gierthhensen.hsbo.org.bewegungstrackerduisburg.BROADCAST";
    private static final String DATA = "gierthhensen.hsbo.org.bewegungstrackerduisburg.DATA";
    private static final String BROADCAST_ACTIVITY = "gierthhensen.hsbo.org.bewegungstrackerduisburg.BROADCAST_ACTIVITY";
    private static final String DATA_ACTIVITY = "gierthhensen.hsbo.org.bewegungstrackerduisburg.DATA_ACTIVITY";

    /**
     * Is called on first creation of activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Receiver for Location
        myLocationServiceIntent = new Intent(Intent.ACTION_SYNC, null, this, LocationService.class);
        ResponseReceiver responseReceiver = new ResponseReceiver();
        IntentFilter intentFilter = new IntentFilter(BROADCAST);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                responseReceiver, intentFilter);

        // Receiver for DetectedActivity
        ActivityResponseReceiver activitiesResponseReceiver = new ActivityResponseReceiver();
        IntentFilter intentFilter2 = new IntentFilter(BROADCAST_ACTIVITY);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                activitiesResponseReceiver, intentFilter2);

        // Action Bar
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Nav Drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            myStatusFragment = new StatusFragment();
            myMapFragment = new MapFragment();

            transaction.add(R.id.fragment_container, myStatusFragment, "fragment_status");
            transaction.add(R.id.fragment_container, myMapFragment, "fragment_map");

            transaction.hide(myMapFragment);
            transaction.commit();
        }
    }

    /**
     * Press back on Navigation Drawer
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Called when OptionsMenu is created
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.start, menu);
        return true;
    }

    /**
     * Select items in Settings bar (top)
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Select items in Navigation Drawer
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.gps) {
            startGPS();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

        else if (id == R.id.nav_switch_fragment) {
            switchFragment();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (id == R.id.nav_send) {}

        return true;
    }

    /**
     * ResponseReceiver Class
     */
    private class ResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Location location = (Location) intent.getExtras().get(DATA);
            myLastLocation = location;
            updateFeature();
        }
    }

    /**
     * Receiver for Activities
     */
    private class ActivityResponseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> detectedActivities = intent.getParcelableArrayListExtra(DATA_ACTIVITY);
            DetectedActivity likelyActivity = null;
            for (DetectedActivity activity : detectedActivities) {
                if (likelyActivity == null) {
                    likelyActivity = activity;
                    myLikelyActivity = likelyActivity;

                }
            }
        }
    }

    /**
     * Set String to activityType
     * @param ActivityType
     * @return
     */
    public String getDetectedActivity(int ActivityType) {
        switch (ActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return "IN_VEHICLE";
            case DetectedActivity.ON_BICYCLE:
                return "ON_BYCICLE";
            case DetectedActivity.ON_FOOT:
                return "ON_FOOT";
            case DetectedActivity.RUNNING:
                return "RUNNING";
            case DetectedActivity.WALKING:
                return "WALKING";
            case DetectedActivity.STILL:
                return "STILL";
            case DetectedActivity.TILTING:
                return "TILTING";
            case DetectedActivity.UNKNOWN:
                return "UNKNOWN";
            default:
                return "UNKNOWN";
        }
    }

    /**
     * Sets TextView to new coordinates
     * @param
     */
    public void updateFeature () {

        Location location = myLastLocation;
        DetectedActivity activity = myLikelyActivity;

        String act;
        String lat = String.valueOf(location.getLatitude());
        String lon = String.valueOf(location.getLongitude());
        latitude = lat;
        longitude = lon;

        if (activity != null){
            act = getDetectedActivity(activity.getType());
            String test = lat + " / " + lon + " / " + act;
            Toast.makeText(this, test , Toast.LENGTH_SHORT).show();
            //TODO call JS method, push coordinates
        }

        else {
            String test = lat + " / " + lon;
            Toast.makeText(this, test, Toast.LENGTH_SHORT).show();
        }
    }

    //get Latitude and Longitude for Js
    public static class WebInterface {
        private final Context c = null;
        Context mContext;

        /** Instantiate the interface and set the context */
        WebInterface() {
            mContext = c;
        }

        /** Get the value of latitude */
        @JavascriptInterface
        public String getLatitude() {
            return latitude;
        }

        /** Get the value of longitude */
        @JavascriptInterface
        public String getLongitude() {
            return longitude;
        }
    }

    /**
     * Called when GPS button is pressed
     * Triggers LocationService
     */
    public void startGPS() {

        if (gpsStat == false) {

            /**
             * Permission Check for Android 6+
             */
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }

            final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

            if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {

                new AlertDialog.Builder(this)
                        .setTitle("GPS disabled")
                        .setMessage("GPS is disabled")
                        .setPositiveButton("Enable GPS", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

            else {
                myLocationServiceIntent.putExtra("type", "startTracking");
                myLocationServiceIntent.putExtra("trackingRate", getUpdateIntervalFromPreferences());

                startService(myLocationServiceIntent);

                /**
                 *
                 */
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.ic_media_play)
                                .setOngoing(true)
                                .setContentTitle("Bewegungstracker Duisburg")
                                .setContentText("GPS gestartet");

                int mNotificationId = 001;
                NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                mNotifyMgr.notify(mNotificationId, mBuilder.build());

                gpsStat = true;
                Toast.makeText(this, "Started GPS Tracking: " + getUpdateIntervalFromPreferences(), Toast.LENGTH_SHORT).show();
            }

        } else if (gpsStat == true) {
            myLocationServiceIntent.putExtra("type", "endTracking");
            startService(myLocationServiceIntent);

            gpsStat = false;
            cancelNotification(this, 001);

            Toast.makeText(this, "Stopped GPS Tracking", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Switches between status and map
     */
    public void switchFragment(){

        if (map_active == false){
            MapFragment frgMap = new MapFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, frgMap);
            transaction.addToBackStack(null);
            transaction.commit();

            map_active = true;
        }

        else {
            StatusFragment frgStatus = new StatusFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, frgStatus);
            transaction.addToBackStack(null);
            transaction.commit();

            map_active = false;
        }
    }

    /**
     * Cancels Notifications from this app
     * @param ctx
     * @param notifyId
     */
    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    /**
     * TODO: Get interval from settings field
     * @return
     */
    public int getUpdateIntervalFromPreferences() {
        SharedPreferences sPref = this.getPreferences(Context.MODE_PRIVATE);
        int trackingInterval = sPref.getInt(UPDATE_INTERVAL, 5);
        return trackingInterval;
    }

    @Override
    protected void onStart() {
        super.onStart();

        //SharedPreferences sPref = getPreferences(Context.MODE_PRIVATE);
    }


    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.commit();
    }

    /**
     * called when activity destroyed
     * Also destroys notification
     */
    @Override
    public void onDestroy() {

        super.onDestroy();
        cancelNotification(this, 001);
    }
}
