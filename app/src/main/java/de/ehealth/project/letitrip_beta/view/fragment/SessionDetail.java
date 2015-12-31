package de.ehealth.project.letitrip_beta.view.fragment;


import android.app.Activity;
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
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSDatabaseHandler;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSService;
import de.ehealth.project.letitrip_beta.handler.session.SessionHandler;
import de.ehealth.project.letitrip_beta.view.MainActivity;

public class SessionDetail extends Fragment implements SessionOverview.ShowRunOnMap{

    private FragmentChanger mListener;
    //private ServiceConnection mConnection;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private MapView mapView;
    private PolylineOptions route;
    private Marker liveMarker;

    private GPSService gps;

    private Button bt;
    private TextView infoBox;
    private int showThisRun = SessionHandler.getSelectedRunId();
    private boolean bound = false;
    private int lastSpeedID = -1;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            GPSService.LocalBinder binder = (GPSService.LocalBinder) service;
            gps = binder.getService();
            bound = true;
            setUpMapIfNeeded();
            Log.w("sessiondetail", "GEBUNDEN");
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.w("sessiondetail","ungebunden");
            bound = false;
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.w("sessiondetail","oncreateview");

        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_session_detail, container, false);
        infoBox = (TextView)view.findViewById(R.id.infoBox);

        bt = (Button) view.findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchMapType();
            }
        });

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof FragmentChanger) {
            mListener = (FragmentChanger) activity;
        } else {
            Log.d("SessionDetail", "Wrong interface implemented");
        }
    }

    @Override
    public void onDetach() {
        Log.w("sessiondetail","ondeattach");
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.w("sessiondetail","oncreate"+ showThisRun + "");

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        route = new PolylineOptions();
    }

    //handler to receive broadcast messages from gps service
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int message = intent.getIntExtra("MapsActivity",-1);
            Log.w("sessionDetail","broadcast:"+message);

            //new position received, add it to the route+update liveMarker
            if ((message == 1) && (showThisRun == gps.getActiveRecordingID())) {
                Cursor res = GPSDatabaseHandler.getInstance().getData().getLastPosOfRun(gps.getActiveRecordingID());
                res.moveToFirst();

                int tempLastID = GPSDatabaseHandler.getInstance().getData().getLastID();
                if (lastSpeedID == -1) {
                    lastSpeedID = tempLastID;
                } else {
                    Log.w("sessiondetail","lastSpeedID="+lastSpeedID+"-tempLastID="+tempLastID+"-showthisrun:"+showThisRun);
                    updateInfoBox((GPSDatabaseHandler.getInstance().getData().getAverageSpeed(lastSpeedID,tempLastID)*3.6));
                    lastSpeedID = tempLastID;
                }
                LatLng temp = new LatLng(res.getDouble(0), res.getDouble(1));

                res.close();

                route.add(temp);
                mMap.addPolyline(route);
                liveMarker.remove();
                liveMarker = mMap.addMarker(new MarkerOptions()
                        .position(temp)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                updateInfoBox(GPSDatabaseHandler.getInstance().getData().getAverageSpeed(showThisRun,0));
            }
        }
    };

    @Override
    public void onStop() {
        Log.w("sessiondetail","onstop");
        super.onStop();
    }

    @Override
    public void onStart() {
        Log.w("sessiondetail", "showThisRunSTART"+showThisRun);
        bindToService();
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.w("sessiondetail","onresume");
        mapView.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter("gps-event"));
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.w("sessiondetail", "pause");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);

        if (bound) {
            Log.w("sessiondetail","UNBINDING");
            getActivity().getApplicationContext().unbindService(mConnection);
            bound = false;
        }
        //getActivity().getSupportFragmentManager().popBackStack();
        Log.w("...",bound+"yoooo");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.w("sessiondetail","ondestroy");
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    /**
     *
     * @param averageSpeed
     */
    public void updateInfoBox(double averageSpeed){
        long duration = GPSDatabaseHandler.getInstance().getData().getDurationOfRun(showThisRun);
        long seconds = (TimeUnit.MILLISECONDS.toSeconds(duration))%60;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        infoBox.setText("Session #" + showThisRun +
                "\nDauer: " + minutes + ":" + (seconds < 10 ? "0" + seconds : seconds + "") + " Minuten" +
                "\nDistanz: " + ((int) GPSDatabaseHandler.getInstance().getData().getWalkDistance(showThisRun)) + " Meter" +
                "\n\u00D8 Geschwindigkeit: " + decimalFormat.format(averageSpeed) + "km/h" +
                "\nTemperatur: " +
                "\nWind: ");

    }

    public void bindToService() {
        Intent i = new Intent(getActivity(), de.ehealth.project.letitrip_beta.handler.gpshandler.GPSService.class);
        getActivity().getApplicationContext().bindService(i, mConnection, Context.BIND_AUTO_CREATE);
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
            // Try to obtain the map
            mMap = mapView.getMap();
            MapsInitializer.initialize(this.getActivity());

            boolean endMarker = true;
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                Log.w("sessionDetail","showthisrun:" + showThisRun);

                //live
                if ((gps.getStatus() == de.ehealth.project.letitrip_beta.handler.gpshandler.GPSService.Status.TRACKINGSTARTED) && (showThisRun == -1)) {
                    showThisRun = gps.getActiveRecordingID();
                    endMarker = false;
                }

                setUpMap(endMarker);
            }
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
        Log.w("sessiondetail","showthisrun:"+showThisRun);

        if (showThisRun != -1) {
            showRunOnMap(endMarker, GPSDatabaseHandler.getInstance().getData().getAverageSpeed(showThisRun, 0)*3.6);
            //TODO why?
            //if (gps.getActiveRecordingID() != showThisRun) showThisRun = -1;
        }
    }

    public void showRunOnMap(boolean endMarker, double averageSpeed){
        //save run data to an array
        Cursor res = GPSDatabaseHandler.getInstance().getData().getRun(showThisRun);

        //display the data
        updateInfoBox(averageSpeed);
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
        res.close();

        route.width(6);
        route.color(Color.RED);
        mMap.addPolyline(route);
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_map, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.w("CLICK","CLICK"+item.getOrder()+"-"+item.getGroupId()+"-"+item.getItemId()+"-"+item.getTitle());
                switch (item.getItemId()) {
                    case R.id.menuitem1:
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        break;
                    case R.id.menuitem2:
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        break;
                    case R.id.menuitem3:
                        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        break;
                    case R.id.menuitem4:
                        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                        break;
                }
                return true;
            }
        });
        popup.show();

    }

    public void switchMapType() {
        showPopup(getView());
    }

    public void updateActivity(MainActivity.FragmentName fn) {
        mListener.changeFragment(fn);
    }

    @Override
    public void setSelectedRunID(int id) {
        showThisRun = id;
    }
}
