package de.ehealth.project.letitrip_beta.model.weather;

import org.json.JSONObject;


public class Location implements JSONReceiver {

    private String city;
    private String country;
    private String region;

    @Override
    public void receive(JSONObject data) {
        city = data.optString("city");
        country = data.optString("country");
        region = data.optString("region");
    }

    public String getRegion() {
        return region;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }
}
