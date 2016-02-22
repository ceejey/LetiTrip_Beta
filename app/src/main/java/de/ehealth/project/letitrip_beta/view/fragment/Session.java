package de.ehealth.project.letitrip_beta.view.fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.calc.WattHandler;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSDatabaseHandler;
import de.ehealth.project.letitrip_beta.handler.polar.PolarHandler;
import de.ehealth.project.letitrip_beta.handler.session.SessionHandler;
import de.ehealth.project.letitrip_beta.handler.weather.WeatherDatabaseHandler;
import de.ehealth.project.letitrip_beta.view.MainActivity;

public class Session extends Fragment {

    private FragmentChanger mListener;
    private TextView txtpuls, txtwatt, txtgeschw, txtlaufRichtung, txttemp, txtwind, txtdistanz, txtgeschwSession, txtzeit;
    private ImageView imgType;
    private int lastID;
    private Button showOnMap;
    private DecimalFormat df;
    private Handler handler; //update duration every second
    private WattHandler wattHandler;
    private double speedMperS; //txttemp
    private int dist; //txttemp
    private float windSpeedKmH;
    private int temperature;
    private int windDirection;
    private int humidity;
    private int pressure;
    private double walkDirection;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long duration = GPSDatabaseHandler.getInstance().getData().getDurationOfSession(SessionHandler.getSelectedRunId());
            long seconds = (TimeUnit.MILLISECONDS.toSeconds(duration))%60;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)%60;
            long hours = TimeUnit.MILLISECONDS.toHours(duration);
            txtzeit.setText("Dauer: "+ (hours != 0?Long.toString(hours):"") + minutes + ":" + (seconds < 10 ? "0" + seconds : seconds + "") + " Minuten");
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        df = new DecimalFormat("0.0");
        handler = new Handler();
        wattHandler = new WattHandler();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        Log.w("session", "onStart");
        //bindToService();
        lastID = -1;
        Cursor res = WeatherDatabaseHandler.getInstance().getData().getLatestWeather();
        res.moveToFirst();
        if (res.getCount() == 1){
            temperature = res.getInt(2);
            windSpeedKmH = res.getInt(3);
            windDirection = res.getInt(4);
            humidity = res.getInt(5);
            pressure = res.getInt(6);
        } else {
            temperature = -300;
            windSpeedKmH = -1;
            windDirection = -1;
            humidity = -1;
            pressure = -1;
        }
        res.close();

        updateStaticUI();
        updateUI();
        handler.postDelayed(runnable, 1000);
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session, container, false);

        // Inflate the layout for this fragment
        txtpuls = (TextView) view.findViewById(R.id.textView9);
        txtwatt = (TextView) view.findViewById(R.id.textView10);
        txtgeschw = (TextView) view.findViewById(R.id.textView11);
        txtlaufRichtung = (TextView) view.findViewById(R.id.textView18);
        txttemp = (TextView) view.findViewById(R.id.textView12);
        txtwind = (TextView) view.findViewById(R.id.textView13);
        txtdistanz = (TextView) view.findViewById(R.id.textView14);
        txtgeschwSession = (TextView) view.findViewById(R.id.textView15);
        txtzeit = (TextView) view.findViewById(R.id.textView16);
        imgType = (ImageView) view.findViewById(R.id.imgType);
        showOnMap = (Button) view.findViewById(R.id.button2);

        showOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateActivity(MainActivity.FragmentName.SESSION_DETAIL);
            }
        });
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof FragmentChanger) {
            mListener = (FragmentChanger) activity;
        } else {
            Log.d("Fitbit", "Wrong interface implemented");
        }
    }

    public void updateActivity(MainActivity.FragmentName fn) {
        mListener.changeFragment(fn);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }


    /**
     * updates ui elements that only need one update at startup
     */
    public void updateStaticUI(){
        imgType.setColorFilter(0xff757575, PorterDuff.Mode.MULTIPLY);
        if (((MainActivity) getActivity()).getGps().getRecordingAsBicycle() == 1){
            imgType.setImageResource(R.drawable.ic_directions_bike_white_48dp);
        } else {
            imgType.setImageResource(R.drawable.ic_directions_run_white_48dp);
        }

        if (temperature != -300){
            txttemp.setText("Temperatur: " + temperature + " °C");
            txtwind.setText("Wind: " + windSpeedKmH + " km/h (" + GPSDatabaseHandler.getInstance().getData().getDirectionLetter(windDirection) + "[" + windDirection + "])");
        } else {
            txttemp.setText("Temperatur nicht verfügbar");
            txtwind.setText("Wind nicht verfügbar.");
        }

    }

    /**
     * updates all visible UI elements
     */
    private void updateUI() {
        txtpuls.setText((PolarHandler.mHeartRate == 0) ? "Puls nicht verfügbar" : "Puls: " + PolarHandler.mHeartRate);

        int currentID = GPSDatabaseHandler.getInstance().getData().getLastID();
        if ((currentID != lastID) && (lastID != -1)){
            speedMperS = GPSDatabaseHandler.getInstance().getData().getSpeed(lastID, currentID);
            txtgeschw.setText("Geschwindigkeit: " + df.format(3.6 * speedMperS) + " km/h");
            walkDirection = GPSDatabaseHandler.getInstance().getData().getWalkDirection(lastID, currentID);
            txtlaufRichtung.setText("Laufrichtung: " + GPSDatabaseHandler.getInstance().getData().getDirectionLetter(walkDirection) + " (" + df.format(walkDirection) + ")");
        } else {
            txtgeschw.setText("Geschwindigkeit: Warte...");
            txtlaufRichtung.setText("Laufrichtung: Warte...");
        }

        lastID = currentID;

        txtgeschwSession.setText("\u00D8Geschwindigkeit (Session):" + df.format(3.6 * GPSDatabaseHandler.getInstance().getData().getSpeed((((MainActivity) (getActivity())).getGps().getActiveRecordingID()), -1)) + " km/h");
        dist = (int) GPSDatabaseHandler.getInstance().getData().getWalkDistance(((MainActivity) (getActivity())).getGps().getActiveRecordingID());

        txtdistanz.setText("Distanz: " + dist + " Meter");

        float angleToWind = (float) Math.abs(windDirection-walkDirection);


        Log.w("session", "windAngle:" + windDirection + "--yourDirection:" + walkDirection+"angle:"+angleToWind);
        double watt = wattHandler.calcWatts( //TODO fehlende parameter
                75F,
                10F,
                180F,
                9.81F,
                (float) speedMperS,
                (float) GPSDatabaseHandler.getInstance().getData().getAltitudeDifference((((MainActivity)getActivity()).getGps().getActiveRecordingID()),-1),
                (float) dist,
                (float) (windSpeedKmH / 3.6),
                angleToWind,
                (float) temperature,
                (float) pressure,
                (float) humidity,
                0.007F,
                0.276F,
                1.1F
        );

        Log.w("session","used paras\n"+
                "speed(m/s)"+(float) speedMperS+"\n"+
                "altitudeChange"+(float) GPSDatabaseHandler.getInstance().getData().getAltitudeDifference((((MainActivity)getActivity()).getGps().getActiveRecordingID()),-1)+"\n"+
                "dist"+(float) dist+"\n"+
                "windspeed"+(float) (windSpeedKmH / 3.6)+"\n"+
                "angleToWind"+angleToWind+"\n"+
                "temp"+(float) temperature+"\n"+
                "pressure"+(float) pressure+"\n"+
                "humidity"+(float) humidity);

        txtwatt.setText("Watt: " + (temperature==-300?"Wetterdaten fehlen":df.format(watt)));
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter("gps-event"));
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        Log.w("session", "pause");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    //handler to receive broadcast messages from gps service
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int message = intent.getIntExtra("GPSService",-1);
            //new position received
            if (message == 5) {
                updateUI();
            }
        }
    };
}
