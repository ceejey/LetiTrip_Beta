package de.ehealth.project.letitrip_beta.model.Fitbit_Tracker.FitBitUserMovement;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class FitBitUserData {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("summary")
    @Expose
    private Summary summary;

    /**
     *
     * @return
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     * The summary
     */
    public Summary getSummary() {
        return summary;
    }

    /**
     *
     * @param summary
     * The summary
     */
    public void setSummary(Summary summary) {
        this.summary = summary;
    }

}