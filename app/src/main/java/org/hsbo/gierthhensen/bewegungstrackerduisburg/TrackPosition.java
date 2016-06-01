package org.hsbo.gierthhensen.bewegungstrackerduisburg;

/**
 * Created by lukas on 30.05.16.
 * PositionObject
 */
public class TrackPosition {
    private double latitude;
    private double longitude;
    private long sessionID;

    public TrackPosition(double latitude, double longitude, long sessionID) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.sessionID = sessionID;
    }

    /**
     * Setter
     * @param longitude
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setSessionID(long sessionID) {
        this.sessionID = sessionID;
    }

    /**
     * Getter
     * @return
     */
    public double getLongitude() {
        return longitude;
    }


    public double getLatitude() {
        return latitude;
    }


    public double getSessionID() {
        return sessionID;
    }
}
