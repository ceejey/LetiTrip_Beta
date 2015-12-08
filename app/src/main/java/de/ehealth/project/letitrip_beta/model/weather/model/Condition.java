package de.ehealth.project.letitrip_beta.model.weather.model;


import org.json.JSONObject;
import de.ehealth.project.letitrip_beta.model.weather.JSONReceiver;

public class Condition implements JSONReceiver {

    private int code;
    private int temperature;
    private String description;

    @Override
    public void receive(JSONObject data) {
        code = data.optInt("code");
        temperature = data.optInt("temp");
        description = data.optString("text");
    }

    public int getCode() {
        return code;
    }

    public int getTemperature() {
        return temperature;
    }

    public String getDescription() {
        return description;
    }
}
