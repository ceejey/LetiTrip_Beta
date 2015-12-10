package de.ehealth.project.letitrip_beta.handler.gpshandler;

public class GPSCustomListItem {
    private int ID;
    private String text;

    public int getID() {
        return ID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public GPSCustomListItem(int ID, String text){
        this.ID=ID;
        this.text=text;
    }

    @Override
    public String toString() {
        return String.valueOf(this.text);
    }

}
