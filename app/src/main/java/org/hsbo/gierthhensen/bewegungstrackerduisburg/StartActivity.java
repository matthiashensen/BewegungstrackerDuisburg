package org.hsbo.gierthhensen.bewegungstrackerduisburg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

    // same as in LocationService - unique name
    private static final String BROADCAST = "gierthhensen.hsbo.org.bewegungstrackerduisburg.BROADCAST";
    private static final String DATA = "gierthhensen.hsbo.org.bewegungstrackerduisburg.DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // location
        myLocationServiceIntent = new Intent(Intent.ACTION_SYNC, null, this, LocationService.class);
        ResponseReceiver responseReceiver = new ResponseReceiver();
        IntentFilter intentFilter = new IntentFilter(BROADCAST);
        LocalBroadcastManager.getInstance(this).registerReceiver(
                responseReceiver, intentFilter);

        // Floating Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Show Own Tracking Route", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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

        else if (id == R.id.nav_share) {}
        else if (id == R.id.nav_send) {}

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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

    // test method
    public void updatePoint (Location location) {
        String lat = Double.toString(location.getLatitude());
        String lon = Double.toString(location.getLongitude());
        Toast.makeText(this, lon+" , "+lat , Toast.LENGTH_SHORT).show();
    }

    public void startGPS() {
        if (gpsStat == false) {
            gpsStat = true;
            myLocationServiceIntent.putExtra("type", "startTracking");
            startService(myLocationServiceIntent);
        } else if (gpsStat == true) {
            gpsStat = false;
            myLocationServiceIntent.putExtra("type", "endTracking");
            startService(myLocationServiceIntent);
        }
    }
}
