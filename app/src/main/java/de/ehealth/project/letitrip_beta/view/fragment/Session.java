package de.ehealth.project.letitrip_beta.view.fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSDatabaseHandler;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSService;
import de.ehealth.project.letitrip_beta.view.MainActivity;

public class Session extends Fragment {

    private FragmentChanger mListener;
    private TextView puls, watt, geschw, laufRichtung, temp, wind, distanz, geschwSession, zeit, laufFahrrad;
    private GPSService gps;
    private boolean bound = false;
    private int showThisRun;
    private int lastID;
    private Button bt;
    private DecimalFormat df;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            GPSService.LocalBinder binder = (GPSService.LocalBinder) service;
            gps = binder.getService();
            bound = true;

            showThisRun = gps.getActiveRecordingID();
            Log.w("showthosrunset",showThisRun+"");

            updateUI();
            updateStaticUI();
            Log.w("session", "GEBUNDEN");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.w("session","ungebunden");
            bound = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        df = new DecimalFormat("0.0");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_session, container, false);

        // Inflate the layout for this fragment
        puls = (TextView) view.findViewById(R.id.textView9);
        watt = (TextView) view.findViewById(R.id.textView10);
        geschw = (TextView) view.findViewById(R.id.textView11);
        laufRichtung = (TextView) view.findViewById(R.id.textView18);
        temp = (TextView) view.findViewById(R.id.textView12);
        wind = (TextView) view.findViewById(R.id.textView13);
        distanz = (TextView) view.findViewById(R.id.textView14);
        geschwSession = (TextView) view.findViewById(R.id.textView15);
        zeit = (TextView) view.findViewById(R.id.textView16);
        laufFahrrad = (TextView) view.findViewById(R.id.textView17);
        bt = (Button) view.findViewById(R.id.button2);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateActivity(MainActivity.FragmentName.SESSION_DETAIL);
            }
        });
        return view;
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateActivity(MainActivity.FragmentName fn) {
        mListener.changeFragment(fn);
    }

    @Override
    public void onStart() {
        Log.w("session", "onStart");
        bindToService();

        lastID = GPSDatabaseHandler.getInstance().getData().getLastID();

        super.onStart();
    }

    public void updateStaticUI(){
        laufFahrrad.setText("Art:"+(gps.getRecordingAsBicycle()==0?"Lauf":"Fahrrad"));

    }

    private void updateUI() {
        puls.setText("Puls nicht verfügbar");

        watt.setText("Watt nicht verfügbar");

        int currentID = GPSDatabaseHandler.getInstance().getData().getLastID();
        if (currentID != lastID){
            geschw.setText("Geschwindigkeit: "+df.format(GPSDatabaseHandler.getInstance().getData().getAverageSpeed(lastID,currentID))+" km/h");
            double degrees = GPSDatabaseHandler.getInstance().getData().getWalkDirection(lastID, currentID);
            laufRichtung.setText("Laufrichtung: "+ GPSDatabaseHandler.getInstance().getData().getDirectionLetter(degrees)+" ("+df.format(degrees)+")");
        } else {
            geschw.setText("Geschwindigkeit: Warte...");
            laufRichtung.setText("Laufrichtung: Warte...");

        }
        lastID = currentID;

        geschwSession.setText("\u00D8Geschwindigkeit (Session):"+df.format(GPSDatabaseHandler.getInstance().getData().getAverageSpeed(showThisRun,0))+" km/h");

        temp.setText("Temperatur nicht verfügbar");

        wind.setText("Wind nicht verfügbar.");

        distanz.setText("Distanz: " + df.format(((int) GPSDatabaseHandler.getInstance().getData().getWalkDistance(showThisRun))) + " Meter");

        long duration = GPSDatabaseHandler.getInstance().getData().getDurationOfRun(showThisRun);
        long seconds = (TimeUnit.MILLISECONDS.toSeconds(duration))%60;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        zeit.setText("Dauer: "+ minutes + ":" + (seconds < 10 ? "0" + seconds : seconds + "") + " Minuten");
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter("my-event"));
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.w("session", "pause");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);

        if (bound) {
            Log.w("sessiondetail","UNBINDING");
            getActivity().unbindService(mConnection);
            bound = false;
        }
        super.onPause();
    }


    //handler to receive broadcast messages from gps service
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int message = intent.getIntExtra("MapsActivity",-1);
            Log.w("session","broadcast:"+message);

            //new position received, add it to the route+update liveMarker
            if (message == 1) {
                updateUI();
            }
        }
    };

    public void bindToService() {
        Intent i = new Intent(getActivity(), de.ehealth.project.letitrip_beta.handler.gpshandler.GPSService.class);
        getActivity().bindService(i, mConnection, Context.BIND_AUTO_CREATE);
    }
}
