package de.ehealth.project.letitrip_beta.model.weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Forecast implements JSONReceiver{
/*
    private String date;
    private int high;
    private int low;
    private String description;

    public String getDescription() {
        return description;
    }

    public int getLow() {
        return low;
    }

    public String getDate() {
        return date;
    }

    public int getHigh() {
        return high;
    }
*/

    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("day")
    @Expose
    private String day;
    @SerializedName("high")
    @Expose
    private int high;
    @SerializedName("low")
    @Expose
    private int low;
    @SerializedName("text")
    @Expose
    private String text;

    /**
     *
     * @return
     *     The code
     */
    public String getCode() {
        return code;
    }

    /**
     *
     * @param code
     *     The code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     *
     * @return
     *     The date
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * @param date
     *     The date
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     *
     * @return
     *     The day
     */
    public String getDay() {
        return day;
    }

    /**
     *
     * @param day
     *     The day
     */
    public void setDay(String day) {
        this.day = day;
    }

    /**
     *
     * @return
     *     The high
     */
    public int getHigh() {
        return high;
    }

    /**
     *
     * @param high
     *     The high
     */
    public void setHigh(int high) {
        this.high = high;
    }

    /**
     *
     * @return
     *     The low
     */
    public int getLow() {
        return low;
    }

    /**
     *
     * @param low
     *     The low
     */
    public void setLow(int low) {
        this.low = low;
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
        date = data.optString("date");
        high = data.optInt("high");
        low = data.optInt("low");
        text = data.optString("text");
    }

}
