package de.ehealth.project.letitrip_beta.handler.weather;

import de.ehealth.project.letitrip_beta.handler.database.WeatherDatabase;

public class WeatherDatabaseHandler {
    private static final WeatherDatabaseHandler holder = new WeatherDatabaseHandler();
    private WeatherDatabase myDB = null;

    public static WeatherDatabaseHandler getInstance() {
        return holder;
    }

    public WeatherDatabase getData() {
        return myDB;
    }

    public void setData(WeatherDatabase myDB) {
        this.myDB = myDB;
    }
}
