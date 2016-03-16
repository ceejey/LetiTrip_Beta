package de.ehealth.project.letitrip_beta.model.weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Item implements JSONReceiver {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("long")
    @Expose
    private String _long;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("pubDate")
    @Expose
    private String pubDate;
    @SerializedName("condition")
    @Expose
    private Condition condition;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("forecast")
    @Expose
    private List<Forecast> forecast = new ArrayList<Forecast>();

    /**
     *
     * @return
     *     The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     *     The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     *     The lat
     */
    public String getLat() {
        return lat;
    }

    /**
     *
     * @param lat
     *     The lat
     */
    public void setLat(String lat) {
        this.lat = lat;
    }

    /**
     *
     * @return
     *     The _long
     */
    public String getLong() {
        return _long;
    }

    /**
     *
     * @param _long
     *     The long
     */
    public void setLong(String _long) {
        this._long = _long;
    }

    /**
     *
     * @return
     *     The link
     */
    public String getLink() {
        return link;
    }

    /**
     *
     * @param link
     *     The link
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     *
     * @return
     *     The pubDate
     */
    public String getPubDate() {
        return pubDate;
    }

    /**
     *
     * @param pubDate
     *     The pubDate
     */
    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    /**
     *
     * @return
     *     The condition
     */
    public Condition getCondition() {
        return condition;
    }

    /**
     *
     * @param condition
     *     The condition
     */
    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    /**
     *
     * @return
     *     The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     *     The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     *     The forecast
     */
    public List<Forecast> getForecast() {
        return forecast;
    }

    /**
     *
     * @param forecast
     *     The forecast
     */
    public void setForecast(List<Forecast> forecast) {
        this.forecast = forecast;
    }

    @Override
    public void receive(JSONObject data) {
        condition = new Condition();
        condition.receive(data.optJSONObject("condition"));


        JSONArray arr = data.optJSONArray("forecast");
        for (int i = 0; i < arr.length(); i++){
            try {
                Forecast f = new Forecast();
                f.receive(arr.getJSONObject(i));
                forecast.add(f);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}


