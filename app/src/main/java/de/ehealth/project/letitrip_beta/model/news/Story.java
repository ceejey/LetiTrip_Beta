package de.ehealth.project.letitrip_beta.model.news;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Story {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("published")
    @Expose
    private String published;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("ressort")
    @Expose
    private String ressort;
    @SerializedName("company")
    @Expose
    private Company company;
    @SerializedName("keywords")
    @Expose
    private Keywords keywords;
    @SerializedName("media")
    @Expose
    private Media media;
    @SerializedName("highlight")
    @Expose
    private String highlight;
    @SerializedName("short")
    @Expose
    private String _short;

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The url
     */
    public String getUrl() {
        return url;
    }

    /**
     *
     * @param url
     * The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     *
     * @return
     * The title
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     * The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     * The body
     */
    public String getBody() {
        return body;
    }

    /**
     *
     * @param body
     * The body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     *
     * @return
     * The published
     */
    public String getPublished() {
        return published;
    }

    /**
     *
     * @param published
     * The published
     */
    public void setPublished(String published) {
        this.published = published;
    }

    /**
     *
     * @return
     * The language
     */
    public String getLanguage() {
        return language;
    }

    /**
     *
     * @param language
     * The language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     *
     * @return
     * The ressort
     */
    public String getRessort() {
        return ressort;
    }

    /**
     *
     * @param ressort
     * The ressort
     */
    public void setRessort(String ressort) {
        this.ressort = ressort;
    }

    /**
     *
     * @return
     * The company
     */
    public Company getCompany() {
        return company;
    }

    /**
     *
     * @param company
     * The company
     */
    public void setCompany(Company company) {
        this.company = company;
    }

    /**
     *
     * @return
     * The keywords
     */
    public Keywords getKeywords() {
        return keywords;
    }

    /**
     *
     * @param keywords
     * The keywords
     */
    public void setKeywords(Keywords keywords) {
        this.keywords = keywords;
    }

    /**
     *
     * @return
     * The media
     */
    public Media getMedia() {
        return media;
    }

    /**
     *
     * @param media
     * The media
     */
    public void setMedia(Media media) {
        this.media = media;
    }

    /**
     *
     * @return
     * The highlight
     */
    public String getHighlight() {
        return highlight;
    }

    /**
     *
     * @param highlight
     * The highlight
     */
    public void setHighlight(String highlight) {
        this.highlight = highlight;
    }

    /**
     *
     * @return
     * The _short
     */
    public String getShort() {
        return _short;
    }

    /**
     *
     * @param _short
     * The short
     */
    public void setShort(String _short) {
        this._short = _short;
    }

}