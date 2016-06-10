package org.hsbo.gierthhensen.bewegungstrackerduisburg;

import android.content.Intent;
import android.app.IntentService;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

/**
 * Internal Service for activity recognition
 */
public class ActivityIntents extends IntentService{

    private static final String BROADCAST_ACTIVITY = "gierthhensen.hsbo.org.bewegungstrackerduisburg.BROADCAST_ACTIVITY";
    private static final String DATA_ACTIVITY = "gierthhensen.hsbo.org.bewegungstrackerduisburg.DATA_ACTIVITY";


    public ActivityIntents() {
        super("ActivityIntents");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        Intent lIntent = new Intent(BROADCAST_ACTIVITY);

        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

        lIntent.putExtra(DATA_ACTIVITY, detectedActivities);
        LocalBroadcastManager.getInstance(this).sendBroadcast(lIntent);
    }
}
