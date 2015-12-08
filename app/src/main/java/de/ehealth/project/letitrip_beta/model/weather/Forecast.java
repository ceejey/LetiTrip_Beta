package de.ehealth.project.letitrip_beta.model.weather;

import org.json.JSONObject;

public class Forecast implements JSONReceiver{

    private String date;
    private int high;
    private int low;

    public int getLow() {
        return low;
    }

    public String getDate() {
        return date;
    }

    public int getHigh() {
        return high;
    }

    @Override
    public void receive(JSONObject data) {
        date = data.optString("date");
        high = data.optInt("high");
        low = data.optInt("low");
    }

}
