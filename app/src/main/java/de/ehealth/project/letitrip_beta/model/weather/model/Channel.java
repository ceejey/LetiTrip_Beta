package de.ehealth.project.letitrip_beta.model.weather.model;

import org.json.JSONObject;
import de.ehealth.project.letitrip_beta.model.weather.JSONReceiver;

public class Channel implements JSONReceiver {

    private Atmosphere atmosphere;

    private Item item;
    private Units units;
    private Location location;
    private Wind wind;

    public Atmosphere getAtmosphere() {
        return atmosphere;
    }

    public Item getItem() {
        return item;
    }

    public Units getUnits() {
        return units;
    }

    public Location getLocation() {return location;}

    public Wind getWind() {
        return wind;
    }

    @Override
    public void receive(JSONObject data) {
        item = new Item();
        item.receive(data.optJSONObject("item"));

        units = new Units();
        units.receive(data.optJSONObject("units"));

        location = new Location();
        location.receive(data.optJSONObject("location"));

        wind = new Wind();
        wind.receive(data.optJSONObject("wind"));

        atmosphere = new Atmosphere();
        atmosphere.receive(data.optJSONObject("atmosphere"));
    }
}
