package org.hsbo.gierthhensen.bewegungstrackerduisburg;

import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * Created by Matthias on 08.06.2016.
 */
public class WriteOnExternalStorage {

    private static final String LOG_TAG = "WriteOnExternalStorage";

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }
}
