package de.ehealth.project.letitrip_beta.model.weather;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.json.JSONObject;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Condition implements JSONReceiver {

    @SerializedName("code")
    @Expose
    private int code;
    @SerializedName("date")
    @Expose
    private final ThreadLocal<String> date = new ThreadLocal<>();
    @SerializedName("temp")
    @Expose
    private int temp;
    @SerializedName("text")
    @Expose
    private String text;

    /**
     *
     * @return
     *     The code
     */
    public int getCode() {
        return code;
    }

    /**
     *
     * @param code
     *     The code
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     *
     * @return
     *     The date
     */
    public String getDate() {
        return date.get();
    }

    /**
     *
     * @param date
     *     The date
     */
    public void setDate(String date) {
        this.date.set(date);
    }

    /**
     *
     * @return
     *     The temp
     */
    public int getTemp() {
        return temp;
    }

    /**
     *
     * @param temp
     *     The temp
     */
    public void setTemp(int temp) {
        this.temp = temp;
    }

    /**
     *
     * @return
     *     The text
     */
    public String getText() {
        return text;
    }

    /**
     *
     * @param text
     *     The text
     */
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void receive(JSONObject data) {
        code = data.optInt("code");
        temp = data.optInt("temp");
        text = data.optString("text");
    }



    /*
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
    }*/
}
