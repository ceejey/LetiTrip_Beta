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

import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSDatabaseHandler;

public class WeatherDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "LetitripDB2";
    public static final String TABLE_NAME = "WeatherDataTable";
    public static final String COLUMN0 = "ID";
    public static final String COLUMN1 = "Date";
    public static final String COLUMN2 = "Temperature";
    public static final String COLUMN3 = "WindSpeed";
    public static final String COLUMN4 = "WindDirection";
    public static final String COLUMN5 = "Humidty";
    public static final String COLUMN6 = "Pressure";
    public static final String COLUMN7 = "Description";

    public WeatherDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE_NAME + " (" +
                COLUMN0 + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN1 + " DATETIME UNIQUE," +
                COLUMN2 + " INTEGER," +
                COLUMN3 + " INTEGER," +
                COLUMN4 + " INTEGER," +
                COLUMN5 + " INTEGER," +
                COLUMN6 + " REAL," +
                COLUMN7 + " TEXT" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addData(String date, int temperature, int windSpeed, int windDirection, int humidity, double pressure, String description){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN1, date);
        contentValues.put(COLUMN2, temperature);
        contentValues.put(COLUMN3, windSpeed);
        contentValues.put(COLUMN4, windDirection);
        contentValues.put(COLUMN5, humidity);
        contentValues.put(COLUMN6, pressure);
        contentValues.put(COLUMN7, description);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return (result != -1);
    }

    /**
     * refreshes weather every full hour
     * @return cursor with the latest weather data
     */
    public Cursor getLatestWeather(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH");
        Date date = new Date();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN1 + " like ?", new String[]{dateFormat.format(date)});
        return res;
    }

    /**
     * debug method. prints the whole database to log
     */
    public void outPutAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        if (res != null){
            String str="";
            while (res.moveToNext()){
                str="";
                str+=("ID:"+res.getString(0)+"\t");
                str+=("date:"+res.getString(1)+"\t");
                str+=("temp:"+res.getString(2)+"\t");
                str+=("windspeed:"+res.getString(3)+"\t");
                str+=("dir:"+res.getString(4)+"\t");
                str+=("humidity:"+res.getString(5)+"\t");
                str+=("pressure:"+res.getString(6)+"\t");
                str+=("descr:"+res.getString(7)+"\n");

                Log.w("weather", str);
            }
            res.close();
        }
    }

    /**
     * Get the Weather entry of a specific run
     * @param id the run ID
     * @return an entry with the weather date; is null if no weather is available
     */
    public Cursor getWeatherOfRun(int id){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH");
        SimpleDateFormat gpsDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        Date timeStampFirstPosition = null;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = GPSDatabaseHandler.getInstance().getData().getSession(id);
        res.moveToFirst();

        try {
            timeStampFirstPosition = gpsDateFormat.parse(res.getString(2));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        res.close();
        res = db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN1 + " like ?", new String[]{dateFormat.format(timeStampFirstPosition)});
        return res;
    }
}
