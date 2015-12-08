package de.ehealth.project.letitrip_beta.model.GPS;

/**
 * Created by Lars on 08.12.2015.
 */
public class DataHolder_GPSDatabase {
    private GPSDatabase myDB = null;
    public GPSDatabase getData() {return myDB;}
    public void setData(GPSDatabase myDB) {this.myDB = myDB;}

    private static final DataHolder_GPSDatabase holder = new DataHolder_GPSDatabase();
    public static DataHolder_GPSDatabase getInstance() {return holder;}
}
