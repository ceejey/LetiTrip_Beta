package de.ehealth.project.letitrip_beta.model.weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Units implements JSONReceiver {

    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("pressure")
    @Expose
    private String pressure;
    @SerializedName("speed")
    @Expose
    private String speed;
    @SerializedName("temperature")
    @Expose
    private String temperature;

    /**
     *
     * @return
     *     The distance
     */
    public String getDistance() {
        return distance;
    }

    /**
     *
     * @param distance
     *     The distance
     */
    public void setDistance(String distance) {
        this.distance = distance;
    }

    /**
     *
     * @return
     *     The pressure
     */
    public String getPressure() {
        return pressure;
    }

    /**
     *
     * @param pressure
     *     The pressure
     */
    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    /**
     *
     * @return
     *     The speed
     */
    public String getSpeed() {
        return speed;
    }

    /**
     *
     * @param speed
     *     The speed
     */
    public void setSpeed(String speed) {
        this.speed = speed;
    }

    /**
     *
     * @return
     *     The temperature
     */
    public String getTemperature() {
        return temperature;
    }

    /**
     *
     * @param temperature
     *     The temperature
     */
    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    @Override
    public void receive(JSONObject data) {
        temperature = data.optString("temperature");
        speed = data.optString("speed");
        pressure = data.optString("pressure");
    }
}
