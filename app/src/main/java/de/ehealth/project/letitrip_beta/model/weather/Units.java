package de.ehealth.project.letitrip_beta.model.weather;

import org.json.JSONObject;

public class Units implements JSONReceiver {
    private String temperature;
    private String speed;

    private String pressure;

    public String getPressure() {
        return pressure;
    }
    public String getTemperature() {
        return temperature;
    }
    public String getSpeed() {
        return speed;
    }

    @Override
    public void receive(JSONObject data) {
        temperature = data.optString("temperature");
        speed = data.optString("speed");
        pressure = data.optString("pressure");
    }
}
