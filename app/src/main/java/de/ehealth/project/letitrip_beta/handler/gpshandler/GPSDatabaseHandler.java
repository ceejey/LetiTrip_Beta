package de.ehealth.project.letitrip_beta.handler.gpshandler;


import de.ehealth.project.letitrip_beta.handler.database.GPSDatabase;

public class GPSDatabaseHandler {
    private static final GPSDatabaseHandler holder = new GPSDatabaseHandler();
    private GPSDatabase myDB = null;

    public static GPSDatabaseHandler getInstance() {
        return holder;
    }

    public GPSDatabase getData() {
        return myDB;
    }

    public void setData(GPSDatabase myDB) {
        this.myDB = myDB;
    }
}