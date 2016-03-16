package de.ehealth.project.letitrip_beta.model.weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.json.JSONObject;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Atmosphere implements JSONReceiver {

    @SerializedName("humidity")
    @Expose
    private int humidity;
    @SerializedName("pressure")
    @Expose
    private double pressure;
    @SerializedName("rising")
    @Expose
    private String rising;
    @SerializedName("visibility")
    @Expose
    private String visibility;

    /**
     *
     * @return
     *     The humidity
     */
    public int getHumidity() {
        return humidity;
    }

    /**
     *
     * @param humidity
     *     The humidity
     */
    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    /**
     *
     * @return
     *     The pressure
     */
    public double getPressure() {
        return pressure;
    }

    /**
     *
     * @param pressure
     *     The pressure
     */
    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    /**
     *
     * @return
     *     The rising
     */
    public String getRising() {
        return rising;
    }

    /**
     *
     * @param rising
     *     The rising
     */
    public void setRising(String rising) {
        this.rising = rising;
    }

    /**
     *
     * @return
     *     The visibility
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     *
     * @param visibility
     *     The visibility
     */
    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    @Override
    public void receive(JSONObject data) {
        pressure = data.optDouble("pressure");
        humidity = data.optInt("humidity");
    }
}