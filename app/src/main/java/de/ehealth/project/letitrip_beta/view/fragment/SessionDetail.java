package de.ehealth.project.letitrip_beta.view.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.concurrent.TimeUnit;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSDatabaseHandler;
import de.ehealth.project.letitrip_beta.view.MainActivity;

public class SessionDetail extends Fragment {

    private FragmentChanger mListener;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private TextView tv1;
    private int showThisRun;
    private BroadcastReceiver broadcastReceiver;
    private de.ehealth.project.letitrip_beta.handler.gpshandler.GPSService gps;
    private boolean bound = false;
    private PolylineOptions route;
    private Marker liveMarker;
    private ServiceConnection mConnection;
    private int lastSpeedID = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_session_detail, container, false);
        tv1 = (TextView)view.findViewById(R.id.textView2);

        final Button bt = (Button) view.findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMapType(mMap, bt);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        route = new PolylineOptions();


        if (getActivity().getIntent().hasExtra("runID")){
            showThisRun = (int) getActivity().getIntent().getExtras().get("runID");
        } else showThisRun = -1;

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                de.ehealth.project.letitrip_beta.handler.gpshandler.GPSService.LocalBinder binder = (de.ehealth.project.letitrip_beta.handler.gpshandler.GPSService.LocalBinder) service;
                gps = binder.getService();
                bound = true;
                setUpMapIfNeeded();
                Log.w("maps", "GEBUNDEN");
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                Log.w("maps","ungebunden");
                bound = false;
            }
        };

    }
    /**
     *
     * @param tv1
     * @param showThisRun
     * @param averageSpeed pass 0 if it shouldnt be displayed
     */
    public void updateInfoBox(TextView tv1, int showThisRun, double averageSpeed){
        long duration = GPSDatabaseHandler.getInstance().getData().getDurationOfRun(showThisRun);
        long seconds = (TimeUnit.MILLISECONDS.toSeconds(duration))%60;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        tv1.setText("Run #" + showThisRun +
                "\nDuration: " + minutes + ":" + (seconds < 10 ? "0" + seconds : seconds + "") + " minutes" +
                "\nDistance: " + ((int) GPSDatabaseHandler.getInstance().getData().getWalkDistance(showThisRun)) + "meters" +
                "\nAverage speed: " + averageSpeed + "km/h" +
                "\nTemperature: " +
                "\nWind: ");


    }

    @Override
    public void onStart() {
        super.onStart();
        Log.w("maps", "start");
        bindToService();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int message = intent.getIntExtra("MapsActivity",-1);
                Log.w("maps","broadcast:"+message);

                //new position received, add it to the route+update liveMarker
                if (message == 1) {
                    Cursor res = GPSDatabaseHandler.getInstance().getData().getLastPosOfRun(gps.getActiveRecordingID());
                    res.moveToFirst();

                    int tempLastID = GPSDatabaseHandler.getInstance().getData().getLastID();
                    if (lastSpeedID == -1) {
                        lastSpeedID = tempLastID;
                    } else {
                        updateInfoBox(tv1,showThisRun,(GPSDatabaseHandler.getInstance().getData().getAverageSpeed(lastSpeedID,tempLastID)*3.6));
                        lastSpeedID = tempLastID;
                    }
                    LatLng temp = new LatLng(res.getDouble(0), res.getDouble(1));

                    route.add(temp);
                    mMap.addPolyline(route);
                    liveMarker.remove();
                    liveMarker = mMap.addMarker(new MarkerOptions()
                            .position(temp)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                }
            }
        };

        //registering receiver
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN");
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    public void setUpUI(){

    }

    public void bindToService() {
        Intent i = new Intent(getActivity(), de.ehealth.project.letitrip_beta.handler.gpshandler.GPSService.class);
        getActivity().bindService(i, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap(boolean)} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            //TODO
            // Try to obtain the map from the SupportMapFragment.
           //mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            boolean endMarker = true;
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                Log.w("map","showthisrun:" + showThisRun);
                if ((gps.getStatus() == de.ehealth.project.letitrip_beta.handler.gpshandler.GPSService.Status.TRACKINGSTARTED) && (showThisRun == -1)) {
                    showThisRun = gps.getActiveRecordingID();
                    endMarker = false;
                }
                setUpMap(endMarker);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.w("maps", "pause");
        try{
            getActivity().unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
        }
        if (bound) {
            getActivity().unbindService(mConnection);
            bound = false;
        }
    }

    /**
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap(boolean endMarker) {
        //add the live liveMarker initially
        liveMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .visible(false));

        //is the int set to show a specific run at activity startup?
        if (showThisRun != -1) {
            showRunOnMap(showThisRun, tv1, mMap, route, endMarker, GPSDatabaseHandler.getInstance().getData().getAverageSpeed(showThisRun, 0));
            showThisRun = -1;
        }
    }
    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.blabla1:
                Toast.makeText(MapsActivity.this, "Test1", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.blabla2:
                Toast.makeText(MapsActivity.this, "Test2", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    public void showRunOnMap(int showThisRun, TextView tv1, GoogleMap mMap, PolylineOptions route, boolean endMarker, double averageSpeed){
        //save run data to an array
        Cursor res = GPSDatabaseHandler.getInstance().getData().getRun(showThisRun);

        //display the data
        updateInfoBox(tv1,showThisRun,averageSpeed);
        while (res.moveToNext()){
            route.add(new LatLng(res.getDouble(3), res.getDouble(4)));
        }

        res.moveToFirst();
        Marker m1 = mMap.addMarker(new MarkerOptions()
                .title("Start")
                .position(new LatLng(res.getDouble(3), res.getDouble(4))));
        m1.showInfoWindow();

        //zoom to start of the route
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(res.getDouble(3), res.getDouble(4)), 15));

        //only set end liveMarker for non-live track
        if (endMarker) {
            res.moveToLast();
            mMap.addMarker(new MarkerOptions()
                    .title("End")
                    .position(new LatLng(res.getDouble(3), res.getDouble(4))
                    ));
        }

        route.width(6);
        route.color(Color.RED);
        mMap.addPolyline(route);

    }

    public void switchMapType(GoogleMap mMap, Button bt) {
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            bt.setText("Switch to Hybrid View");
        } else if (mMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            bt.setText("Switch to Normal View");
        } else {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            bt.setText("Switch to Terrain View");
        }
    }

    public void updateActivity(MainActivity.FragmentName fn) {
        mListener.changeFragment(fn);
    }
}
