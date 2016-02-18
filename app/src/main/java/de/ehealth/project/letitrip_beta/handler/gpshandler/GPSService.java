package de.ehealth.project.letitrip_beta.handler.gpshandler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.ehealth.project.letitrip_beta.handler.polar.PolarHandler;
import de.ehealth.project.letitrip_beta.handler.session.SessionHandler;
import de.ehealth.project.letitrip_beta.view.MainActivity;

public class GPSService extends Service {

    private LocationListener locationListener;
    private LocationManager locationManager;
    private int activeRecordingID; //all location points are saved with this ID
    private int recordingAsBicycle; //0=walk; 1=bicycle
    private PolarHandler polar;

    private boolean standing = false;
    private boolean firstPoint = true;
    private List<Float> bearingList = new ArrayList<>();

    private Status status;
    private NotificationManager notificationManager;
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
        Log.w("service", "DESTROYED");
        activeRecordingID = -1;
        if (notificationManager != null) notificationManager.cancel(0);
        polar.disconnectFromPolarDevice();
        sendBroadcast("GPSService",2); //let the UI refresh
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.w("service", "onbind");
        return mBinder;
    }

    @Override
    public void onCreate() {
        Log.w("service", "created");
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        polar = new PolarHandler(getApplicationContext());
        status = Status.IDLE;
        super.onCreate();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.w("service", "rebind");
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
        Log.w("service", "started");
        activeRecordingID = (GPSDatabaseHandler.getInstance().getData().getLastSessionID()) + 1;
        recordingAsBicycle = SessionHandler.getRunType();

        polar.searchPolarDevice();

        //gps enabled?
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(GPSService.this, "GPS aktivieren!", Toast.LENGTH_LONG).show();
            sendBroadcast("GPSService", 1);
        } else startTracking();
        return super.onStartCommand(intent, flags, startId);
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
     * connects to the polar device and starts the gps tracking
     */
    public void startTracking() {
        //connect to polar
        List<BluetoothDevice> deviceList = polar.getDeviceList();
        if (deviceList.size() != 0){
            polar.connectToPolarDevice(deviceList.get(0));
            polar.receiveHeartRate();
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location l) {
                if ((l != null) && (status != Status.PAUSED)) {
                    //only insert data when accuaracy is good enough
                    if (l.getAccuracy() < 25){

                        //Log.w("gpsservice", "Accuracy: " + l.getAccuracy() + "\nSpeed: " + l.getSpeed());
                        if(!standing || l.getSpeed() >= 2.5f) { //Also take the first standing point
                            standing = false;
                            boolean ins = GPSDatabaseHandler.getInstance().getData().addData(activeRecordingID, l.getLatitude(), l.getLongitude(), l.getAltitude(), recordingAsBicycle, PolarHandler.mHeartRate);
                            if (ins) {
                                sendBroadcast("GPSService", 5);
                            } else
                                Toast.makeText(GPSService.this, "Error inserting data", Toast.LENGTH_LONG).show();

                            //check this part after first data set is inserted to create an table entry at the SessionOverview fragment
                            if (status != Status.TRACKINGSTARTED) {
                                createNotification();
                                Log.w("gpsservice", "tracking started at id:" + activeRecordingID);
                                status = Status.TRACKINGSTARTED;
                                sendBroadcast("GPSService", 4);
                            }
                        } else if(l.getSpeed() <= 2.5F && standing == false){
                            standing = true;
                        }
                    } else {
                        Log.w("gpsservice","accuracy too low("+l.getAccuracy()+") skipping position.");
                        sendBroadcast("GPSService",3);
                    }
                } else Log.w("gpsservice","paused or no location");
            }

            public void onProviderDisabled(String provider) {}
            public void onProviderEnabled(String provider) {}
            public void onStatusChanged(String provider, int status, Bundle extras) {}
        };
        try {
                                                   //TODO set time to 2000
            locationManager.requestLocationUpdates("gps", 1000, 0, locationListener);
            status = Status.SEARCHINGGPS;
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
        i.putExtra("notification",1);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);

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
}
