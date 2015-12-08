package de.ehealth.project.letitrip_beta.model.weather.model;

import org.json.JSONObject;
import de.ehealth.project.letitrip_beta.model.weather.JSONReceiver;

/**
 * Created by Lars on 11.10.2015.
 */
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
