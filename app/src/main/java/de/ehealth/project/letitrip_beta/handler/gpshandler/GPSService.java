package de.ehealth.project.letitrip_beta.handler.gpshandler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.ehealth.project.letitrip_beta.view.MainActivity;

public class GPSService extends Service {

    private LocationListener locationListener;
    private LocationManager mylocman;
    private int activeRecordingID; //all location points are saved with this ID
    private int recordingAsBicycle; //0=walk; 1=bicycle
    private Status status;
    private NotificationManager notificationManager;
    private IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public GPSService getService() {
            return GPSService.this;
        }
    }

    @Override
    public void onDestroy() {
        try {
            //TODO creates error "java.lang.IllegalArgumentException: invalid listener: null"
            if (mylocman != null) mylocman.removeUpdates(locationListener);
            status = Status.IDLE;
        } catch (IllegalArgumentException | SecurityException e) {
            e.printStackTrace();
        }
        Log.w("service", "destroyed");
        if (notificationManager != null) notificationManager.cancel(0);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.w("service", "onbind");
        return mBinder;
    }

    @Override
    public void onCreate() {
        Log.w("service", "created");
        mylocman = (LocationManager) getSystemService(LOCATION_SERVICE);
        status = Status.IDLE;
        super.onCreate();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.w("service", "rebind");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.w("service", "started");
        activeRecordingID = (GPSDatabaseHandler.getInstance().getData().getLastRunID()) + 1;
        if (intent.hasExtra("bicycle")) {
            recordingAsBicycle = (intent.getIntExtra("bicycle", 0) == 0 ? 0 : 1);
            Log.w("gpsservice", "recordingAsBicycle=" + recordingAsBicycle);
        }

        if (!mylocman.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(GPSService.this, "GPS aktivieren!", Toast.LENGTH_LONG).show();
            sendBroadcast("GPSActivity", 1);
        } else startTracking();
        return super.onStartCommand(intent, flags, startId);
    }

    public void sendBroadcast(String msg, int ID){
        Intent intent = new Intent("my-event").putExtra(msg, ID);;
        LocalBroadcastManager.getInstance(this.getApplicationContext()).sendBroadcast(intent);
    }

    public void startTracking() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location l) {
                if (l != null) {
                    boolean ins = GPSDatabaseHandler.getInstance().getData().addData(activeRecordingID, l.getLatitude(), l.getLongitude(), l.getAltitude(), recordingAsBicycle);
                    if (ins) {
                        sendBroadcast("MapsActivity", 1);
                        Toast.makeText(GPSService.this, "Accuaracy:" + l.getAccuracy(), Toast.LENGTH_SHORT).show();
                    } else Toast.makeText(GPSService.this, "Error inserting data", Toast.LENGTH_LONG).show();

                    //check this part after first data set is inserted to create an table entry at the SessionOverview fragment
                    if (status != Status.TRACKINGSTARTED) {
                        createNotification();
                        Log.w("service", "Listen to id:" + activeRecordingID);
                        status = Status.TRACKINGSTARTED;
                        sendBroadcast("GPSActivity", 2);
                    }
                }
            }

            public void onProviderDisabled(String provider) {}
            public void onProviderEnabled(String provider) {}
            public void onStatusChanged(String provider, int status, Bundle extras) {}
        };
        try {
           // mylocman.requestLocationUpdates("gps", 3000, 10, locationListener);
            mylocman.requestLocationUpdates("gps", 100, 2, locationListener);

            status = Status.SEARCHINGGPS;
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void createNotification() {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Calendar calender = Calendar.getInstance();
        String time = dateFormat.format(calender.getTime());

        Notification notification = new NotificationCompat.Builder(this)
                .setTicker("GPS Tracking started")
                .setSmallIcon(android.R.drawable.presence_online)
                .setContentTitle("GPS Tracking running (#" + activeRecordingID + ")")
                .setContentText("since " + time)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();

        notification.flags = Notification.FLAG_ONGOING_EVENT;

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

    public enum Status {TRACKINGSTARTED, SEARCHINGGPS, IDLE;}

    public Status getStatus() {
        return status;
    }

    public int getActiveRecordingID() {
        return activeRecordingID;
    }

    public int getRecordingAsBicycle() {
        return recordingAsBicycle;
    }
}
