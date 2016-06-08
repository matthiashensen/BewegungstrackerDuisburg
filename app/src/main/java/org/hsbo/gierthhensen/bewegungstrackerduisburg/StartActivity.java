package org.hsbo.gierthhensen.bewegungstrackerduisburg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.app.NotificationCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Lukas Gierth on 26.05.16.
 */

@SuppressLint({"ShowToast"})
public class StartActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // objects for location
    private Intent myLocationServiceIntent;
    private Location myLastLocation;
    private static boolean gpsStat = false;
    private static boolean map_active = false;

    // same as in LocationService - unique name
    private static final String BROADCAST = "gierthhensen.hsbo.org.bewegungstrackerduisburg.BROADCAST";
    private static final String DATA = "gierthhensen.hsbo.org.bewegungstrackerduisburg.DATA";

    /**
     * Is called on first creation of activity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        addDynamicFragment();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // location
        myLocationServiceIntent = new Intent(Intent.ACTION_SYNC, null, this, LocationService.class);
        ResponseReceiver responseReceiver = new ResponseReceiver();
        IntentFilter intentFilter = new IntentFilter(BROADCAST);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                responseReceiver, intentFilter);

        // Action Bar
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Nav Drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void addDynamicFragment() {
        Fragment frg = StatusFragment.newInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, frg).commit();
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
        }

        else if (id == R.id.nav_share) {
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
            updatePoint(myLastLocation);
        }
    }

    /**
     * Sets TextView to new coordinates
     * @param location
     */
    public void updatePoint (Location location) {
        String lat = Double.toString(location.getLatitude());
        String lon = Double.toString(location.getLongitude());
        TextView coordinates = (TextView) findViewById(R.id.coordinates);
        coordinates.setText(lon + " , " + lat);
    }

    /**
     * Called when GPS button is pressed
     * Triggers LocationService
     */
    public void startGPS() {
        if (gpsStat == false) {
            myLocationServiceIntent.putExtra("type", "startTracking");
            startService(myLocationServiceIntent);

            TextView statusText = (TextView) findViewById(R.id.statusText);
            statusText.setText("GPS Tracking running");
            statusText.setTextColor(Color.GREEN);
            TextView cText = (TextView) findViewById(R.id.coordinates);
            cText.setTextColor(Color.YELLOW);

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
            Toast.makeText(this, "Started GPS Tracking", Toast.LENGTH_SHORT).show();

        } else if (gpsStat == true) {
            myLocationServiceIntent.putExtra("type", "endTracking");
            startService(myLocationServiceIntent);

            TextView statusText = (TextView) findViewById(R.id.statusText);
            statusText.setText("Service Not Running");
            statusText.setTextColor(Color.RED);
            TextView cText = (TextView) findViewById(R.id.coordinates);
            cText.setTextColor(Color.RED);

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
     * called when activity destroyed
     * Also destroys notification
     */
    @Override
    public void onDestroy() {

        super.onDestroy();
        cancelNotification(this, 001);
    }
}
