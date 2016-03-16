package de.ehealth.project.letitrip_beta.model.weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.json.JSONObject;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Wind implements JSONReceiver {

    @SerializedName("chill")
    @Expose
    private String chill;
    @SerializedName("direction")
    @Expose
    private int direction;
    @SerializedName("speed")
    @Expose
    private int speed;

    /**
     *
     * @return
     *     The chill
     */
    public String getChill() {
        return chill;
    }

    /**
     *
     * @param chill
     *     The chill
     */
    public void setChill(String chill) {
        this.chill = chill;
    }

    /**
     *
     * @return
     *     The direction
     */
    public int getDirection() {
        return direction;
    }

    /**
     *
     * @param direction
     *     The direction
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }

    /**
     *
     * @return
     *     The speed
     */
    public int getSpeed() {
        return speed;
    }

    /**
     *
     * @param speed
     *     The speed
     */
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public void receive(JSONObject data) {
        direction = data.optInt("direction");
        speed = data.optInt("speed");
    }
}
