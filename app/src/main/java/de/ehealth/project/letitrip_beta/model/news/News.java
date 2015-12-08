package de.ehealth.project.letitrip_beta.model.news;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class News {

    @SerializedName("success")
    @Expose
    private String success;
    @SerializedName("request")
    @Expose
    private Request request;
    @SerializedName("content")
    @Expose
    private Content content;

    /**
     * @return The success
     */
    public String getSuccess() {
        return success;
    }

    /**
     * @param success The success
     */
    public void setSuccess(String success) {
        this.success = success;
    }

    /**
     * @return The request
     */
    public Request getRequest() {
        return request;
    }

    /**
     * @param request The request
     */
    public void setRequest(Request request) {
        this.request = request;
    }

    /**
     * @return The content
     */
    public Content getContent() {
        return content;
    }

    /**
     * @param content The content
     */
    public void setContent(Content content) {
        this.content = content;
    }

}

