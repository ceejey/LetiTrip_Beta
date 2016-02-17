package de.ehealth.project.letitrip_beta.view.adapter;

public class GPSCustomListItem {

    private int ID; //not visible
    private int visibleID;//visible ID
    private String started;
    private String duration;
    private int positions;
    private int distanceMeter;
    private double averageSpeed;
    private int type;

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    private boolean live;

    public GPSCustomListItem(int ID, int visibleID, String started, String duration, int distanceMeter, double averageSpeed, int type, int positions) {
        this.ID = ID;
        this.visibleID = visibleID;
        this.started = started;
        this.duration = duration;
        this.positions = positions;
        this.distanceMeter = distanceMeter;
        this.averageSpeed = averageSpeed;
        this.type = type;
    }

    public GPSCustomListItem() {
        this.live = false;
    }

    public int getVisibleID() {
        return visibleID;
    }

    public void setVisibleID(int visibleID) {
        this.visibleID = visibleID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getPositions() {
        return positions;
    }

    public void setPositions(int positions) {
        this.positions = positions;
    }

    public int getDistanceMeter() {
        return distanceMeter;
    }

    public void setDistanceMeter(int distanceMeter) {
        this.distanceMeter = distanceMeter;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
