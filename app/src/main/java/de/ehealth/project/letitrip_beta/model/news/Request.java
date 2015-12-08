package de.ehealth.project.letitrip_beta.model.news;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Request {

    @SerializedName("uri")
    @Expose
    private String uri;
    @SerializedName("start")
    @Expose
    private String start;
    @SerializedName("limit")
    @Expose
    private String limit;
    @SerializedName("format")
    @Expose
    private String format;
    @SerializedName("branche")
    @Expose
    private String branche;

    /**
     * @return The uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri The uri
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return The start
     */
    public String getStart() {
        return start;
    }

    /**
     * @param start The start
     */
    public void setStart(String start) {
        this.start = start;
    }

    /**
     * @return The limit
     */
    public String getLimit() {
        return limit;
    }

    /**
     * @param limit The limit
     */
    public void setLimit(String limit) {
        this.limit = limit;
    }

    /**
     * @return The format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format The format
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @return The branche
     */
    public String getBranche() {
        return branche;
    }

    /**
     * @param branche The branche
     */
    public void setBranche(String branche) {
        this.branche = branche;
    }

}
