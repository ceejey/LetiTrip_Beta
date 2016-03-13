package de.ehealth.project.letitrip_beta.handler.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import de.ehealth.project.letitrip_beta.view.adapter.GPSCustomListItem;

public class GPSDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "LetitripDB3";
    public static final String TABLE_NAME = "GPSDataTable";
    public static final String COLUMN0 = "ID";
    public static final String COLUMN1 = "SessionNumber";
    public static final String COLUMN2 = "Time";
    public static final String COLUMN3 = "Latitude";
    public static final String COLUMN4 = "Longitude";
    public static final String COLUMN5 = "Altitude";
    public static final String COLUMN6 = "Bicycle"; //1=bicycle, 0=walking
    public static final String COLUMN7 = "Pulse";
    public static final String COLUMN8 = "Speed";
    public static final String COLUMN9 = "Watt";
    public static final String COLUMN10 = "Calories";
    public GPSDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE_NAME + " (" +
                COLUMN0 + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN1 + " INTEGER," +
                COLUMN2 + " DATETIME ," +
                COLUMN3 + " REAL," +
                COLUMN4 + " REAL," +
                COLUMN5 + " REAL," +
                COLUMN6 + " INTEGER," +
                COLUMN7 + " INTEGER," +
                COLUMN8 + " REAL," +
                COLUMN9 + " REAL," +
                COLUMN10 + " REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * returns everything matching with the session id
     * @param id the session id
     * @return all entries of the session
     */
    public Cursor getSession(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN1 + " = " + id, null);
        return res;
    }

    /**
     *  get the last stored position (lon, lat) of a point
     * @param id the session id
     * @return the last position (lon,lat)
     */
    public Cursor getLastPosOfSession(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        //SELECT Latitude, Longitude FROM  GPSDataTable WHERE ((RunNumber=1) AND (ID=(SELECT MAX(ID) FROM GPSDataTable))
        Cursor res = db.rawQuery("select " + COLUMN3 + ", " + COLUMN4 + " from " + TABLE_NAME + " where ((" + COLUMN1 + " = " + id + ") and (" + COLUMN0 + "=(select max(" + COLUMN0 + ") from " + TABLE_NAME + ")))", null);
        return res;
    }

    /**
     * add an entry to the database
     * @param sessionNumber
     * @param latitude
     * @param longitude
     * @param altitude
     * @param bicycle
     * @param pulse
     * @return false if an error occured; true if inserting was successful
     */
    public boolean addData(int sessionNumber, double latitude, double longitude, double altitude, int bicycle, int pulse, double speed, double watt, double calories){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        Date date = new Date();

        contentValues.put(COLUMN1, sessionNumber);
        contentValues.put(COLUMN2, dateFormat.format(date));
        contentValues.put(COLUMN3, latitude);
        contentValues.put(COLUMN4, longitude);
        contentValues.put(COLUMN5, altitude);
        contentValues.put(COLUMN6, bicycle);
        contentValues.put(COLUMN7, pulse);
        contentValues.put(COLUMN8, speed);
        contentValues.put(COLUMN9, watt);
        contentValues.put(COLUMN10, calories);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return (result != -1);
    }

    /**
     *
     * @return the last session ID
     */
    public int getLastSessionID(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select distinct " + COLUMN1 + " from " + TABLE_NAME + " where " + COLUMN1 + "=(select max(" + COLUMN1 + ") from " + TABLE_NAME + ")", null);

        if (res != null) if (res.getCount()>0){
            res.moveToFirst();
            int result = res.getInt(0);
            res.close();
            return result;
        } else return 0; else return 0;
    }

    /**
     * get the last id of
     * - a run (if id != -1)
     * - in general (if id == -1)
     * @param id the session id or -1
     * @return the last stored ID (of the session)
     */
    public int getLastID(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res;
        if (id == -1){
            res = db.rawQuery("select max(" + COLUMN0 + ") from " + TABLE_NAME, null);
        } else {
            res = db.rawQuery("select max(" + COLUMN0 + "),"+COLUMN3+" from " + TABLE_NAME + " where "+COLUMN1 + " = "+id, null);
        }
        Log.w("gpsDB","getlastIDCount:"+res.getCount());
        if (res != null) if (res.getCount() > 0){
            res.moveToFirst();
            if (res.getDouble(1) == 0){
                return -1;
            }
            Log.w("gpsDB","bla:"+res.getDouble(1));
            int result = res.getInt(0);
            res.close();
            return result;
        } else return -1; else return -1;
    }

    /**
     * delete a session
     * @param id the session id
     * @return number of entries deleted
     */
    public int deleteSession(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COLUMN1 + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * creates an overview of the run
     * @param id the session id
     * @return a GPSCustomListView model class filled with all necessary information
     */
    public GPSCustomListItem getOverviewOfSession(int id) {
        Cursor res = getSession(id);
        GPSCustomListItem result = new GPSCustomListItem();

        if ((res != null) && (res.getCount() > 0)) {
            res.moveToFirst();

            //0=walk; 1=bicycle
            int bicycle = res.getInt(6);

            long duration = getDuration(id,-1);
            long seconds = (TimeUnit.MILLISECONDS.toSeconds(duration))%60;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)%60;
            long hours = TimeUnit.MILLISECONDS.toHours(duration);

            SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd.MM.yyyy - HH:mm");
            SimpleDateFormat savedDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            Date time = null;

            try {
                time = savedDateFormat.parse(res.getString(2));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            result.setVisibleID(res.getInt(1));
            result.setStarted(displayDateFormat.format(time));
            result.setDuration((hours != 0 ? Long.toString(hours) + ":" : "") + minutes + ":" + ((seconds < 10) ? 0 : "") + seconds);

            double meters = getWalkDistance(id,-1);
            result.setDistanceMeter((int) getWalkDistance(id,-1));
            result.setAverageSpeed(3.6 * (meters / (seconds + (minutes * 60))));
            result.setType(bicycle);
            result.setPositions(res.getCount());

            res.close();
            return result;
        } else return null;
    }

    /**
     * pass id1=X and id2=-1 to get the time of session X
     * pass id1=x and id2=y to get the time between those points
     * @param ID1 first point or session ID
     * @param ID2 second point or -1
     * @return time in milliseconds
     */
    public long getDuration(int ID1, int ID2){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = null;
        if (ID2 == -1){
            res = db.rawQuery("select " + COLUMN2 + " from " + TABLE_NAME + " where " + COLUMN1 + " = " + ID1, null);
        } else {
            res = db.rawQuery("select " + COLUMN2 + " from " + TABLE_NAME + " where " + COLUMN0 + " = " + ID1 + " or " + COLUMN0 + " = " + ID2, null);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        Date startTime = null;
        Date endTime = null;
        if (res.getCount() == 0) return -1;
        try {
            res.moveToFirst();
            startTime = dateFormat.parse(res.getString(0));
            res.moveToLast();
            endTime = dateFormat.parse(res.getString(0));
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            res.close();
        }

        return (endTime.getTime() - startTime.getTime());
    }

    /**
     * get the start timestamp of a run
     * @param id the session id
     * @return the duration in milliseconds
     */
    public long getStartTimeOfSession(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + COLUMN2 + " from " + TABLE_NAME + " where " + COLUMN1 + " = " + id + " order by "+ COLUMN0 + " asc limit 1", null);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = null;
        if (res.getCount() == 0) return -1;
        try {
            res.moveToFirst();
            startTime = dateFormat.parse(res.getString(0));
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            res.close();
        }

        return startTime.getTime();
    }

    /**
     * pass id1=X and id2=-1 to get the distance in meters of session X
     * pass id1=x and id2=y to get the distance in meters between those points
     * @param ID1 first point or session ID
     * @param ID2 second point or -1
     * @return distance in meters
     */
    public double getWalkDistance(int ID1, int ID2){
        double distance = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        if (ID2 == -1){
            res = db.rawQuery("select " + COLUMN3 + ", " + COLUMN4 + "," + COLUMN5 + " from " + TABLE_NAME + " where " + COLUMN1 + "=" + ID1, null);
        } else {
            res = db.rawQuery("select " + COLUMN3 + ", " + COLUMN4 + "," + COLUMN5 + " from " + TABLE_NAME + " where " + COLUMN0 + " = " + ID1 + " or " + COLUMN0 + " = " + ID2, null);
        }

        if (res.getCount() < 2){
            return 0;
        }

        res.moveToFirst();
        double lat = res.getDouble(0);
        double lon = res.getDouble(1);
        double alt = res.getDouble(2);

        while (res.moveToNext()){
            distance += calculateDistance(res.getDouble(0), res.getDouble(1), res.getDouble(2), lat, lon, alt);
            lat = res.getDouble(0);
            lon = res.getDouble(1);
            alt = res.getDouble(2);
        }
        res.close();
        return distance;
    }


    //http://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi
    /*
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     */
    public double calculateDistance(double lat1, double lon1, double el1,
                                    double lat2, double lon2, double el2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    /**
     * get the distance of all stored locations of one day
     * @param date in format: yyyy-MM-dd
     * @return total walkdistance of the whole day in meters
     */
    public int getDistanceOfDay(String date){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + COLUMN1 + ", MIN(" + COLUMN2 + ") from " + TABLE_NAME + " group by " + COLUMN1, null);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date convert = new Date();
        String dateFinal;
        int outputmeters=0;
        if (res.getCount()==0) return -1;
        while (res.moveToNext()){
            Log.w("database", res.getString(1) + "-->" + res.getInt(0));
            try {
                convert=dateFormat.parse(res.getString(1));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateFinal = dateFormat.format(convert);

            //Log.w("database",date+"<->"+dateFinal+"-->?"+(date.equals(dateFinal)));
            //if the input date = iterated date, add the distance of that run to the output
            if (date.equals(dateFinal)) {
                outputmeters += (int) getWalkDistance(res.getInt(0),-1);
            }
        }
        res.close();
        return outputmeters;
    }

    /**
     * pass id1=X and id2=-1 to get the altitude difference of session X
     * pass id1=x and id2=y to get the altitude difference between those points
     * @param ID1 first point or session ID
     * @param ID2 second point or -1
     * @return altitude difference in meters
     */
    public int getAltitudeDifference(int ID1, int ID2){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        if (ID2 == -1){
            res = db.rawQuery("select " + COLUMN5 + " from " + TABLE_NAME + " where " + COLUMN1 + " = " + ID1, null);
        } else {
            res = db.rawQuery("select " + COLUMN5 + " from " + TABLE_NAME + " where " + COLUMN0 + " = " + ID1 + " or " + COLUMN0 + " = " + ID2, null);
        }

        if (res.getCount()==0) return -1;

        res.moveToFirst();
        int val1 = res.getInt(0);
        res.moveToLast();
        int result = res.getInt(0)-val1;
        res.close();
        return result;
    }

    /**
     * pass id1=X and id2=-1 to get the average speed of run X
     * pass id1=x and id2=y to get the average speed between those points
     * @param ID1 first point or session ID
     * @param ID2 second point or -1
     * @return speed in !!!meters per second!!! from the track in between those two points (multiply with 3.6 to get km/h)/the session
     */
    public double getSpeed(int ID1, int ID2){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res;
        if (ID2 == -1){
            res = db.rawQuery("select " + COLUMN2 +","+COLUMN3+","+COLUMN4+","+COLUMN5+","+COLUMN8+ " from " + TABLE_NAME + " where " + COLUMN1 + "="+ID1, null);
        } else {
            res = db.rawQuery("select " + COLUMN2 +","+COLUMN3+","+COLUMN4+","+COLUMN5+ " from " + TABLE_NAME + " where " + COLUMN0+" >= "+ID1+" and "+COLUMN0+ " <= "+ID2, null);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        //get start and end time
        Date time1 = null;
        Date time2 = null;

        if (res.getCount() == 0) return -1;

        if (res.getCount() == 1){
            res.moveToFirst();
            return res.getDouble(4);
        }
        try {
            res.moveToFirst();
            time1 = dateFormat.parse(res.getString(0));
            res.moveToLast();
            time2 = dateFormat.parse(res.getString(0));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long totalSeconds = TimeUnit.MILLISECONDS.toSeconds(time2.getTime() - time1.getTime());
        double totalDistance = 0;

        res.moveToFirst();
        double lat1 = res.getDouble(1);
        double lon1 = res.getDouble(2);
        double alt1 = res.getDouble(3);

        double lat2;
        double lon2;
        double alt2;

        while(res.moveToNext()){
            lat2 = res.getDouble(1);
            lon2 = res.getDouble(2);
            alt2 = res.getDouble(3);

            totalDistance += calculateDistance(lat1,lon1,alt1,lat2,lon2,alt2);

            lat1 = res.getDouble(1);
            lon1 = res.getDouble(2);
            alt1 = res.getDouble(3);
        }
        res.close();
        return totalDistance/totalSeconds;
    }

    /**
     * get the direction between two locations
     * @param ID1
     * @param ID2
     * @return the direction in degrees (0;360)
     */
    public double getWalkDirection(int ID1, int ID2){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + COLUMN3 + "," + COLUMN4 + " from " + TABLE_NAME + " where " + COLUMN0 + " = " + ID1 + " or " + COLUMN0 + " = " + ID2, null);
        if (res.getCount()==0) return -1;
        res.moveToFirst();
        double lat1 = res.getDouble(0);
        double lon1 = res.getDouble(1);

        res.moveToNext();
        double lat2 = res.getDouble(0);
        double lon2 = res.getDouble(1);
        res.close();

        double y = Math.sin(lon2 - lon1) * Math.cos(lat2);
        double x = Math.cos(lat1)*Math.sin(lat2) -
                Math.sin(lat1)*Math.cos(lat2)*Math.cos(lon2 - lon1);
        double brng = Math.atan2(y, x);
        return (Math.toDegrees(brng)+360)%360;
    }

    /**
     * get the burned kcalories per session
     * @param id the session id
     * @return kilo calories per session
     */
    public double getCaloriesBurned(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select " + COLUMN10 + " from " + TABLE_NAME + " where " + COLUMN1 + " = " + id, null);

        if (res.getCount() == 0) return -1;

        res.moveToLast();
        double kcal = res.getDouble(0);
        res.close();
        return kcal;
    }

    /**
     * sources: http://climate.umn.edu/snow_fence/components/winddirectionanddegreeswithouttable3.htm
     *          http://stackoverflow.com/questions/13399821/data-structures-that-can-map-a-range-of-values-to-a-key
     *
     * @param degrees in range of [0..360[
     * @return N, O, S, W etc.
     */
    public String getDirectionLetter(double degrees){
        TreeMap<Double, String> map = new TreeMap<Double, String>();

        map.put(0.0, "N");
        map.put(11.25, null);

        map.put(11.25, "NNO");
        map.put(33.75, null);

        map.put(33.75, "NO");
        map.put(56.25, null);

        map.put(56.25, "ONO");
        map.put(78.75, null);

        map.put(78.75, "O");
        map.put(101.25, null);

        map.put(101.25, "OSO");
        map.put(123.75, null);

        map.put(123.75, "SO");
        map.put(146.25, null);

        map.put(146.25, "SSO");
        map.put(168.75, null);

        map.put(168.75, "S");
        map.put(191.25, null);

        map.put(191.25, "SSW");
        map.put(213.75, null);

        map.put(213.75, "SW");
        map.put(236.25, null);

        map.put(236.25, "WSW");
        map.put(258.75, null);

        map.put(258.75, "W");
        map.put(281.25, null);

        map.put(281.25, "WNW");
        map.put(303.75, null);

        map.put(303.75, "NW");
        map.put(326.25, null);

        map.put(326.25, "NNW");
        map.put(348.75, null);

        map.put(348.75, "N");
        map.put(360.00, null);

        Map.Entry<Double, String> e = map.floorEntry(degrees);
        if (e != null && e.getValue() == null) {
            e = map.lowerEntry(degrees);
        }
        return e == null ? "ERR" : e.getValue();
    }
}
