package de.ehealth.project.letitrip_beta.view.fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSDatabaseHandler;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSService;
import de.ehealth.project.letitrip_beta.handler.polar.PolarHandler;
import de.ehealth.project.letitrip_beta.view.MainActivity;

public class Session extends Fragment {

    private FragmentChanger mListener;
    private TextView txtpuls, txtWatt, txtgeschw, txtlaufRichtung, txttemp, txtwind, txtDistanz, txtGeschwSession, txtzeit, txtWindDirection, txtCalories;
    private Button showOnMap;
    private ImageView imgType, imgWalkDir, imgWindDir;
    private DecimalFormat df;
    private Handler handler; //update duration every second

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            long currentTime = new Date().getTime();
            long startTime = ((MainActivity)getActivity()).getGps().getStartTime();
            long seconds = (TimeUnit.MILLISECONDS.toSeconds(currentTime-startTime))%60;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(currentTime-startTime)%60;
            long hours = TimeUnit.MILLISECONDS.toHours(currentTime-startTime);
            txtzeit.setText((hours != 0?Long.toString(hours)+":":"") + minutes + ":" + (seconds < 10 ? "0" + seconds : seconds));
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        df = new DecimalFormat("0.0");
        handler = new Handler();
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
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
        txtWatt = (TextView) view.findViewById(R.id.textView10);
        txtgeschw = (TextView) view.findViewById(R.id.textView11);
        txtlaufRichtung = (TextView) view.findViewById(R.id.textView18);
        txttemp = (TextView) view.findViewById(R.id.textView12);
        txtwind = (TextView) view.findViewById(R.id.textView13);
        txtWindDirection = (TextView) view.findViewById(R.id.textView30);
        txtDistanz = (TextView) view.findViewById(R.id.textView14);
        txtGeschwSession = (TextView) view.findViewById(R.id.textView15);
        txtzeit = (TextView) view.findViewById(R.id.textView16);
        imgType = (ImageView) view.findViewById(R.id.imgType);
        showOnMap = (Button) view.findViewById(R.id.button2);
        txtCalories = (TextView) view.findViewById(R.id.textView35);
        imgWalkDir = (ImageView) view.findViewById(R.id.imgWalkDir);
        imgWindDir = (ImageView) view.findViewById(R.id.imgWindDir);

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

    /**
     * updates ui elements that only need one update at startup
     */
    public void updateStaticUI(){
        imgType.setColorFilter(0xff757575, PorterDuff.Mode.MULTIPLY);
        imgWalkDir.setColorFilter(0xff757575, PorterDuff.Mode.MULTIPLY);
        imgWindDir.setColorFilter(0xff757575, PorterDuff.Mode.MULTIPLY);

        if (((MainActivity) getActivity()).getGps().getRecordingAsBicycle() == 1){
            imgType.setImageResource(R.drawable.ic_directions_bike_white_24dp);
        } else {
            imgType.setImageResource(R.drawable.ic_directions_run_white_24dp);
        }

        if ((((MainActivity) getActivity()).getGps().getTemperature()) != -300){
            txttemp.setText((((MainActivity) getActivity()).getGps().getTemperature()) + " °C");
            txtwind.setText((((MainActivity) getActivity()).getGps().getWindSpeedKmH()) + " km/h");
            txtWindDirection.setText(GPSDatabaseHandler.getInstance().getData().getDirectionLetter((((MainActivity) getActivity()).getGps().getWindDirection())));
            imgWindDir.setRotation((((MainActivity) getActivity()).getGps().getWindDirection()));
        } else {
            txttemp.setText("N/A");
            txtwind.setText("N/A");
            txtWindDirection.setText("N/A");
        }
    }

    /**
     * updates all visible UI elements
     */
    private void updateUI() {
        GPSService gps = ((MainActivity) getActivity()).getGps();
        txtpuls.setText((PolarHandler.mHeartRate == 0) ? "N/A" : PolarHandler.mHeartRate + " bpm");
        txtlaufRichtung.setText(GPSDatabaseHandler.getInstance().getData().getDirectionLetter(gps.getWalkDirection()));
        imgWalkDir.setRotation(gps.getWalkDirection());
        txtgeschw.setText(df.format(gps.getSpeedMperS()*3.6) + " km/h");

        double seconds = (TimeUnit.MILLISECONDS.toSeconds(new Date().getTime()-((MainActivity)getActivity()).getGps().getStartTime()));
        double avgKmH = ((((float)gps.getTotalDistance()/seconds))*3.6);
        txtGeschwSession.setText(df.format(avgKmH) + " km/h");
        txtDistanz.setText(gps.getTotalDistance() + " Meter");
        txtWatt.setText((gps.getTemperature() == -300 ? "N/A" : (gps.getWatt() < 0 ? "0" : df.format(gps.getWatt()))));
        txtCalories.setText(gps.getTemperature() == -300 ? "N/A" : (gps.getKcaloriesBurned() == -1)?"N/A":df.format(gps.getKcaloriesBurned())+" kcal");
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter("gps-event"));
        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        handler.removeCallbacks(runnable);
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
            } else if (message == 4){
                updateStaticUI();
            }
        }
    };
}
