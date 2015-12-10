package de.ehealth.project.letitrip_beta.model.weather;

import org.json.JSONObject;

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
