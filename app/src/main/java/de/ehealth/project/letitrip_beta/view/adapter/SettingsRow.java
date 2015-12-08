package de.ehealth.project.letitrip_beta.view.adapter;

/**
 * Created by eHealth on 07.12.2015.
 */
public class SettingsRow {

    private String customItem = "";
    private String subItem = "";

    public SettingsRow(String customItem, String subItem){
        this.customItem = customItem;
        this.subItem = subItem;
    }

    public String getCustomItem() {
        return customItem;
    }

    public void setCustomItem(String item) {
        customItem = item;
    }

    public String getSubItem() {
        return subItem;
    }

    public void setSubItem(String subItem) {
        this.subItem = subItem;
    }
}
