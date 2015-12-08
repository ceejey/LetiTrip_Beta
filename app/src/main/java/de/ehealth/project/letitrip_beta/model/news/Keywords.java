package de.ehealth.project.letitrip_beta.model.news;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Generated;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Keywords {

    @SerializedName("keyword")
    @Expose
    private List<String> keyword = new ArrayList<String>();

    /**
     * @return The keyword
     */
    public List<String> getKeyword() {
        return keyword;
    }

    /**
     * @param keyword The keyword
     */
    public void setKeyword(List<String> keyword) {
        this.keyword = keyword;
    }

}
