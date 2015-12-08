package de.ehealth.project.letitrip_beta.model.news;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Content {

    @SerializedName("story")
    @Expose
    private List<Story> story = new ArrayList<Story>();

    /**
     * @return The story
     */
    public List<Story> getStory() {
        return story;
    }

    /**
     * @param story The story
     */
    public void setStory(List<Story> story) {
        this.story = story;
    }

}
