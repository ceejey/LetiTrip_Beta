package de.ehealth.project.letitrip_beta.view.adapter;

/**
 * Created by Mirorn on 15.12.2015.
 */
public class DevicesRow {

    private String mCustomItem = "";
    private String mSubItem = "";
    private int mID = 0;
    public DevicesRow(int id, String customItem, String subItem){
        mCustomItem = customItem;
        mSubItem = subItem;
        mID = id;
    }

    public int getID() {
        return mID;
    }

    public String getCustomItem() {
        return mCustomItem;
    }

    public void setCustomItem(String item) {
        mCustomItem = item;
    }

    public String getSubItem() {
        return mSubItem;
    }

    public void setSubItem(String subItem) {
        mSubItem = subItem;
    }
}

