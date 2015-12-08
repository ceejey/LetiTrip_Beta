package de.ehealth.project.letitrip_beta.model.weather.model;

import org.json.JSONObject;
import de.ehealth.project.letitrip_beta.model.weather.JSONReceiver;

/**
 * Created by Lars on 11.11.2015.
 */
public class Wind implements JSONReceiver {

    private int direction;
    private int speed;

    @Override
    public void receive(JSONObject data) {
        direction = data.optInt("direction");
        speed = data.optInt("speed");
    }

    public int getDirection() {
        return direction;
    }

    public int getSpeed() {
        return speed;
    }
}
