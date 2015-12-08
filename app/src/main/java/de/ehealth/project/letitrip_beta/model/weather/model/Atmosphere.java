package de.ehealth.project.letitrip_beta.model.weather.model;

import org.json.JSONObject;

import de.ehealth.project.letitrip_beta.model.weather.JSONReceiver;

public class Atmosphere implements JSONReceiver {

    private double pressure;
    private int humidity;

    public int getHumidity() {
        return humidity;
    }

    public double getPressure() {
        return pressure;
    }

    @Override
    public void receive(JSONObject data) {
        pressure = data.optDouble("pressure");
        humidity = data.optInt("humidity");
    }
}
