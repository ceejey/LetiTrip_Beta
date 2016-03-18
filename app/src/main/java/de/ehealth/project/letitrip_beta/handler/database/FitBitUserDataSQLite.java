package de.ehealth.project.letitrip_beta.handler.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import de.ehealth.project.letitrip_beta.model.fitbit.FitBitUserData;
import de.ehealth.project.letitrip_beta.model.fitbit.Summary;


/**
 * Created by Mirorn on 11.11.2015.
 */
public class FitBitUserDataSQLite extends SQLiteOpenHelper {

    private static FitBitUserDataSQLite sInstance = null ;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "FitbitUserDB";

    // Users Table name
    private static final String TABLE_USER = "fitbituser";
    // Values
    private static final String ID = "id";
    private static final String ACTIVITY_CALORIES = "activityCalories";
    private static final String CALORIES_BMR = "caloriesBMR";
    private static final String CALORIES_Out = "caloriesOut";
    private static final String STEPS = "steps";
    private static final String DATE = "date";
    // This static property is an important interface for the class "HomeUser" because it
    // delivers the data of the current day
    private static FitBitUserData mCurFitBitUserData = null;
    private static final String[] COLUMNS = {ID, ACTIVITY_CALORIES, CALORIES_BMR, CALORIES_Out, DATE, STEPS};

    public static FitBitUserDataSQLite getInstance(Context context) {

        // Use the application context, which will ensure that it
        // don't accidentally leak an Activity's context.
        if (sInstance == null) {
            sInstance = new FitBitUserDataSQLite(context.getApplicationContext());
        }
        return sInstance;
    }
    private FitBitUserDataSQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_USER_TABLE = "CREATE TABLE "+ TABLE_USER +
                " ( " + ID + " INTEGER PRIMARY KEY , " +
                ACTIVITY_CALORIES + " TEXT , " +
                CALORIES_BMR + " TEXT, " +
                CALORIES_Out + " TEXT, " +
                DATE + " TEXT UNIQUE, " +
                STEPS + " TEXT)";
        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
    }

    /* At first this Method searches for a entry which contains the parameter string "day" if an entry
    * has been found it will be only updated "updateFitBitData()" because DATE is a unique field
      else a new entry will be made and */
    public void addFitBitData(FitBitUserData fitBitUserData, String day) {

        Summary sum = fitBitUserData.getSummary();
        printTable(TABLE_USER);

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor =
            db.query(TABLE_USER, // a. table
                    COLUMNS, // b. column names
                    DATE + " LIKE ?", // c. selections
                    new String[]{day}, // d. selections args
                    null, // e. group by
                    null, // f. having
                    null, // g. order by
                    null); // h. limit */
                cursor.getCount();
            if (cursor.getCount() == 0) {
                ContentValues values = new ContentValues();
                values.put(ACTIVITY_CALORIES, sum.getActivityCalories());
                values.put(CALORIES_BMR, sum.getCaloriesBMR());
                values.put(CALORIES_Out, sum.getCaloriesOut());
                values.put(STEPS, sum.getSteps());
                values.put(DATE, day);
                db.insert(TABLE_USER, // table
                        null, //nullColumnHack
                        values); // key/value -> keys = column names/ values = column values
                printTable(TABLE_USER);
                db.close();
            }
            else {
                updateFitBitData(fitBitUserData, day);
            }
            cursor.close();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void updateFitBitData(FitBitUserData fitBitUserData, String day) {

        Summary sum = fitBitUserData.getSummary();
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ACTIVITY_CALORIES, sum.getActivityCalories());
        values.put(CALORIES_BMR, sum.getCaloriesBMR());
        values.put(CALORIES_Out, sum.getCaloriesOut());
        values.put(STEPS, sum.getSteps());
        values.put(DATE, day);
        db.update(TABLE_USER, //table
                values, // column/value
                DATE + " LIKE ?", // selections
                new String[]{day}); //selection args
        db.close();

        printTable(TABLE_USER);
    }

    // Searches for an entry that contains the string parameter
    public Summary getFitBitData(String day){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =
                db.query(TABLE_USER, // a. table
                        COLUMNS, // b. column names
                        DATE + " = ?", // c. selections
                        new String[]{day}, // d. selections args
                        null, // e. group by
                        null, // f. having
                        null, // g. order by
                        null); // h. limit
        Summary user = new Summary();
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            user.setActivityCalories(cursor.getString(1));
            user.setCaloriesBMR(cursor.getString(2));
            user.setCaloriesOut(cursor.getString(3));
            user.setSteps(cursor.getString(5));

        } else {return null;}
        cursor.close();
        return user;
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    private void printTable(String tablename){
        String selectQuery = "SELECT * FROM " + tablename;

        // Achtung!!! ggf die Datenbank-Connection von aussen rein geben
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Cursor dbCursor = db.query(tablename, null, null, null, null, null, null);
        String[] columnNames = dbCursor.getColumnNames();

        if (cursor.moveToFirst()) {
            // +++ Tabellenkopf +++
            String columnSeperator = " | ";
            String rowSeperator = "-----------------------------------------";
            String row = columnSeperator;

            for (String name : cursor.getColumnNames()) {
                row = row + name + columnSeperator;
            }

            // ueber den Cursor iterieren
            do {
                // +++ Tabellenzeile +++
                row = columnSeperator;
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    String newRow = cursor.getString(i);

                    // Inhalt formatieren
                    if(newRow == null){
                        newRow = "null";
                    }else{
                        if(newRow.length() > 20){
                            newRow = newRow.substring(0, 19);
                            newRow = newRow + "...";
                        }
                    }
                    row = row + newRow;
                    // Trennzeichen
                    row = row + columnSeperator;
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbCursor.close();
    }

    public FitBitUserData getCurFitBitUserMovement() {
        return mCurFitBitUserData;
    }

    public void setCurFitBitUserMovement(FitBitUserData curFitBitUserData) {
        FitBitUserDataSQLite.mCurFitBitUserData = curFitBitUserData;
    }
}
