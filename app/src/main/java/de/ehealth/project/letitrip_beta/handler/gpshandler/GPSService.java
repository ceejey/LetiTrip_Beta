package de.ehealth.project.letitrip_beta.handler.gpshandler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.ehealth.project.letitrip_beta.handler.calc.WattHandler;
import de.ehealth.project.letitrip_beta.handler.polar.PolarCallback;
import de.ehealth.project.letitrip_beta.handler.polar.PolarHandler;
import de.ehealth.project.letitrip_beta.handler.session.SessionHandler;
import de.ehealth.project.letitrip_beta.handler.weather.WeatherDatabaseHandler;
import de.ehealth.project.letitrip_beta.model.settings.UserSettings;
import de.ehealth.project.letitrip_beta.view.MainActivity;

public class GPSService extends Service implements PolarCallback {

    private LocationListener locationListener;
    private LocationManager locationManager;
    private int activeRecordingID; //all location points are saved with this ID
    private int recordingAsBicycle; //0=walk; 1=bicycle

    private long startTime;

    private PolarHandler polar;

    private WattHandler wattHandler;

    private List <BluetoothDevice> deviceList;
    private boolean firstPoint;
    private Status status;
    private NotificationManager notificationManager;

    private double kcaloriesBurned; //total

    private double speedMperS;
    private double distSinceLastUpdate;
    private int totalDistance;
    private float windSpeedKmH;
    private int temperature;
    private int windDirection;
    private int humidity;
    private int pressure;
    private int walkDirection;
    private int altitudeDifference;
    private int lastID;
    private float weight, height, bicycleWeight;
    private double watt;

    private IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {

        public GPSService getService() {
            return GPSService.this;
        }

    }
    /**
     * status of the service
     */
    public enum Status {TRACKINGSTARTED, SEARCHINGGPS, IDLE, PAUSED;}
    /**
     * service was stopped
     * stop location updates, remove notification, disconnect from polar
     */
    @Override
    public void onDestroy() {
        try {
            if (locationListener != null) locationManager.removeUpdates(locationListener);
            status = Status.IDLE;
        } catch (IllegalArgumentException | SecurityException e) {
            e.printStackTrace();
        }
        activeRecordingID = -1;
        speedMperS = 0;
        kcaloriesBurned = 0;
        startTime = 0;
        distSinceLastUpdate = 0;
        totalDistance = 0;
        windSpeedKmH = 0;
        temperature = -300;
        windDirection = 0;
        humidity = 0;
        pressure = 0;
        walkDirection = 0;
        altitudeDifference = 0;
        firstPoint = true;

        lastID = -1;
        if (notificationManager != null)
            notificationManager.cancel(0);

        if(polar.isDeviceConnected())
            polar.disconnectFromPolarDevice();

        sendBroadcast("GPSService",2); //let the UI refresh
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        polar = new PolarHandler(getApplicationContext(), this);
        PolarHandler.setInstance(polar);

        status = Status.IDLE;
        super.onCreate();
    }

    /**
     * is called when the user swipes the app away. stop the service, everything else is already destroyed (database handler, UI,..)
     * @param rootIntent
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    /**
     * initializes the service
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //app was swiped away, android deleted everything already. stop the service too
        if (GPSDatabaseHandler.getInstance().getData() == null){
            stopSelf();
            return START_NOT_STICKY;
        }
        activeRecordingID = (GPSDatabaseHandler.getInstance().getData().getLastSessionID()) + 1;
        recordingAsBicycle = SessionHandler.getRunType();

        kcaloriesBurned = 0;
        wattHandler = new WattHandler();
        lastID = -1;
        firstPoint = true;
        Cursor res = WeatherDatabaseHandler.getInstance().getData().getLatestWeather();

        if (res != null){ //check if its not null seperarated
            if (res.getCount() == 1){
                res.moveToFirst();
                temperature = res.getInt(2);
                windSpeedKmH = res.getInt(3);
                windDirection = res.getInt(4);
                humidity = res.getInt(5);
                pressure = res.getInt(6);
                res.close();
            } else {
                temperature = -300;
                windSpeedKmH = -1;
                windDirection = -1;
                humidity = -1;
                pressure = -1;
            }
        } else{
            temperature = -300;
            windSpeedKmH = -1;
            windDirection = -1;
            humidity = -1;
            pressure = -1;
        }


        UserSettings fitbitUserProfile = UserSettings.getmActiveUser();

        try {
            weight = (float)Integer.parseInt(fitbitUserProfile.getmWeight());
            height = (float)Integer.parseInt(fitbitUserProfile.getmHeight());
            bicycleWeight = (float) Integer.parseInt(fitbitUserProfile.getmBikeWeight());
        } catch (NumberFormatException e){
            e.printStackTrace();
            Toast.makeText(GPSService.this, "Kann Profildaten nicht laden!", Toast.LENGTH_LONG).show();
            return super.onStartCommand(intent, flags, startId);
        }

        polar.searchPolarDevice();

        //gps enabled?
        boolean gpsDisabled = (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        boolean bluetoothDisabled = (!BluetoothAdapter.getDefaultAdapter().isEnabled());
        boolean polarDeviceKnown = !(UserSettings.getmActiveUser().getmPolarDeviceID().equals(""));

        if ((bluetoothDisabled && polarDeviceKnown) || gpsDisabled) {
            Toast.makeText(GPSService.this, (gpsDisabled?"GPS aktivieren!\n":"")+
                    ""+((bluetoothDisabled && polarDeviceKnown)?"Bluetooth aktivieren, um Pulsmesserdaten zu erfassen.":""), Toast.LENGTH_LONG).show();
        }

        if (gpsDisabled){
            sendBroadcast("GPSService", 1);
        } else startTracking();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * callback function. is called after finishing the search
     */
    public void polarDiscoveryFinished(){
        //Connect to Polar
        if(polar.isDeviceFound()) {
            deviceList = polar.getDeviceList();
            if (deviceList.size() != 0) {
                //connect to the stored polar device
                for (int i=0; i < deviceList.size();i++){
                    if (deviceList.get(i).getName().equals(UserSettings.getmActiveUser().getmPolarDeviceID())){
                        polar.connectToPolarDevice(deviceList.get(i));
                    }
                }
            }
        }
    }

    /**
     * callback function. is called after a device is connected
     */
    public void polarDeviceConnected(){
        if(polar.isDeviceConnected()) {
            polar.receiveHeartRate();

        }
    }

    /**
     * sends a broadcast with given parameters
     * @param msg
     * @param ID
     */
    public void sendBroadcast(String msg, int ID){
        Intent intent = new Intent("gps-event").putExtra(msg, ID);;
        LocalBroadcastManager.getInstance(this.getApplicationContext()).sendBroadcast(intent);
    }

    /**
     * connects to the polar device (if available) and starts the gps tracking
     */
    public void startTracking() {

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location l) {
                if ((l != null) && (status != Status.PAUSED) && (!Double.isNaN(l.getLongitude())) && (!Double.isNaN(l.getLatitude())) && (!Double.isNaN(l.getAltitude()))) {
                    //only insert data when accuaracy is good enough
                    if (l.getAccuracy() < 25){

                        if((l.getSpeed() >= 1f) || (firstPoint)) { //take the first point and points faster than 1 meter per second
                            firstPoint = false;

                            speedMperS = l.getSpeed();
                            double passedTime;

                            int currentID = GPSDatabaseHandler.getInstance().getData().getLastID(activeRecordingID);
                            if ((currentID != lastID) && (lastID != -1) && (currentID != -1)){
                                walkDirection = (int)GPSDatabaseHandler.getInstance().getData().getWalkDirection(lastID, currentID);
                                altitudeDifference = GPSDatabaseHandler.getInstance().getData().getAltitudeDifference(lastID, currentID);
                                distSinceLastUpdate = GPSDatabaseHandler.getInstance().getData().getWalkDistance(lastID, currentID);
                                passedTime = TimeUnit.MILLISECONDS.toSeconds(GPSDatabaseHandler.getInstance().getData().getDuration(lastID, currentID));
                            } else { //not 2 points received yet
                                distSinceLastUpdate = 0;
                                passedTime = 0;
                            }

                            float angleToWind = (float) Math.abs(windDirection-walkDirection);

                            //calculate watt
                            try {
                                //bicycle
                                if (recordingAsBicycle == 1){
                                    watt = wattHandler.calcWatts(
                                            weight,
                                            bicycleWeight,
                                            height,
                                            9.81F,
                                            (float) speedMperS,
                                            (float) altitudeDifference,
                                            (float) distSinceLastUpdate,
                                            (float) (windSpeedKmH / 3.6),
                                            angleToWind,
                                            (float) temperature,
                                            (float) pressure * 100,
                                            ((float) humidity) / 100,
                                            0.007F,
                                            0.276F,
                                            1.1F);
                                } else { //walking
                                    watt = wattHandler.calcRunningWatts(
                                            weight,
                                            height,
                                            9.81F,
                                            (float)speedMperS,
                                            (float)altitudeDifference,
                                            (float)passedTime);
                                }
                                if (!Double.isNaN(watt) && (watt > 0)){
                                    double newKcal = wattHandler.calcKcal(watt, passedTime);
                                    kcaloriesBurned = kcaloriesBurned + newKcal;
                                }
                            } catch (NumberFormatException ex){
                                ex.printStackTrace();
                                temperature = -300;
                            }

                            lastID = currentID;

                            boolean ins = GPSDatabaseHandler.getInstance().getData().addData(activeRecordingID, l.getLatitude(), l.getLongitude(), l.getAltitude(), recordingAsBicycle, PolarHandler.mHeartRate,l.getSpeed(),watt, kcaloriesBurned);
                            if (ins) { //insertion successful?
                                sendBroadcast("GPSService", 5);
                                totalDistance = (int) GPSDatabaseHandler.getInstance().getData().getWalkDistance(activeRecordingID,-1);
                            } else {
                                Toast.makeText(GPSService.this, "Fehler beim Einfügen der Daten", Toast.LENGTH_LONG).show();
                            }

                            //check this part after first data set is inserted to create an table entry at the SessionOverview fragment
                            if (status != Status.TRACKINGSTARTED) {
                                createNotification();
                                status = Status.TRACKINGSTARTED;
                                startTime = new Date().getTime();
                                sendBroadcast("GPSService", 4);
                            }
                        }
                    } else {
                        sendBroadcast("GPSService",3);
                    }
                }
            }

            public void onProviderDisabled(String provider) {}
            public void onProviderEnabled(String provider) {}
            public void onStatusChanged(String provider, int status, Bundle extras) {}
        };
        try {
            //start location updates
            locationManager.requestLocationUpdates("gps", 2000, 1, locationListener);
            status = Status.SEARCHINGGPS;
            speedMperS = -1;
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * creates a notification when tracking started
     */
    public void createNotification() {
        //on click
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("notification",123); //mainactivity receives this value; opens session fragment
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Calendar calender = Calendar.getInstance();
        String time = dateFormat.format(calender.getTime());

        Notification notification = new NotificationCompat.Builder(this)
                .setTicker("Tracking ist gestartet")
                .setSmallIcon(android.R.drawable.presence_online)
                .setContentTitle("Session "+activeRecordingID+" aktiv")
                .setContentText("seit " + time)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        notification.flags = Notification.FLAG_ONGOING_EVENT;

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    public Status getStatus() {
        return status;
    }

    public int getActiveRecordingID() {
        return activeRecordingID;
    }

    public int getRecordingAsBicycle() {
        return recordingAsBicycle;
    }

    public boolean isPaused() {
        return (status == Status.PAUSED);
    }

    /**
     * pauses the service
     */
    public void pause(){
        status = Status.PAUSED;
    }

    /**
     * resumes the service if possible
     */
    public void resume(){
        if ((status!= Status.IDLE) && (status != Status.SEARCHINGGPS)){
            status = Status.TRACKINGSTARTED;
        }
    }

    public double getDistSinceLastUpdate() {
        return distSinceLastUpdate;
    }

    public double getSpeedMperS() {
        return speedMperS;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public float getWindSpeedKmH() {
        return windSpeedKmH;
    }

    public int getTemperature() {
        return temperature;
    }

    public int getWindDirection() {
        return windDirection;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getPressure() {
        return pressure;
    }

    public int getWalkDirection() {
        return walkDirection;
    }

    public int getAltitudeDifference() {
        return altitudeDifference;
    }

    public int getLastID() {
        return lastID;
    }

    public double getKcaloriesBurned() {
        return kcaloriesBurned;
    }

    public double getWatt() {
        return watt;
    }

    public long getStartTime() {
        return startTime;
    }
}
