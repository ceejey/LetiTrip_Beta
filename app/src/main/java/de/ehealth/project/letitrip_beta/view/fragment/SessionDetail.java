package de.ehealth.project.letitrip_beta.view.fragment;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
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
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSDatabaseHandler;
import de.ehealth.project.letitrip_beta.handler.polar.PolarHandler;
import de.ehealth.project.letitrip_beta.handler.session.SessionHandler;
import de.ehealth.project.letitrip_beta.handler.weather.WeatherDatabaseHandler;
import de.ehealth.project.letitrip_beta.view.MainActivity;

public class SessionDetail extends Fragment {

    private FragmentChanger mListener;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private MapView mapView;
    private PolylineOptions route;
    private Marker liveMarker;
    private Button btnSwitchMapType;
    private TextView infoBox;
    private LinkedList<Integer> last5Pulses;
    private Date vibrated; //only vibrate every 20 seconds
    private Date lastTimePulsShown;
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.w("sessiondetail","saveinstance");

        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.w("sessiondetail", "oncreateview");

        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_session_detail, container, false);
        infoBox = (TextView)view.findViewById(R.id.infoBox);

        btnSwitchMapType = (Button) view.findViewById(R.id.button);
        btnSwitchMapType.setOnClickListener(new View.OnClickListener() {
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
        Log.w("sessiondetail", "ondeattach");
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.w("sessiondetail", "oncreate" + SessionHandler.getSelectedRunId() + "");

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        route = new PolylineOptions();
        last5Pulses = new LinkedList<Integer>();
        vibrated = new Date();
        lastTimePulsShown = new Date();
    }

    //handler to receive broadcast messages from gps service
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        int message = intent.getIntExtra("GPSService",-1);
        //Log.w("sessionDetail","broadcast:"+message);

        //new position received, add it to the route+update liveMarker if the active track is displayed
        if ((message == 5) && (SessionHandler.getSelectedRunId() == ((MainActivity)getActivity()).getGps().getActiveRecordingID())) {
            Cursor res = GPSDatabaseHandler.getInstance().getData().getLastPosOfSession(((MainActivity)getActivity()).getGps().getActiveRecordingID());
            res.moveToFirst();
            LatLng temp = new LatLng(res.getDouble(0), res.getDouble(1));
            res.close();

            route.add(temp);
            mMap.addPolyline(route);
            liveMarker.remove();
            liveMarker = mMap.addMarker(new MarkerOptions()
                    .position(temp)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            updatePulseMarker(temp);
            updateInfoBox();
        }
        }
    };

    /**
     * add a pulse marker with a color matching to the pulse value
     * show the marker every 30 seconds or if the change is greater than 20%
     * @param pos current position
     */
    private void updatePulseMarker(LatLng pos) {
        int temp =  PolarHandler.mHeartRate;
        if (temp == 0) return;
        if (last5Pulses.size()<5){
            last5Pulses.add(temp);
        } else if (last5Pulses.getLast() != temp) { //add a new pulse if 3 pulses are available and its a new value
            last5Pulses.removeFirst();
            last5Pulses.add(temp);
            double average=0;
            for (int i=0;i<last5Pulses.size();i++){
                average+=last5Pulses.get(i);
            }
            average/=last5Pulses.size();
            //20% higher pulse than the average of the previous 3 pulses
            //OR
            //30 seconds passed since the pulse got displayed the last time
            float difference = Math.abs(((temp / ((float) average / 100)))-100);
            int timePassedLastPulseShown = (int)(((new Date().getTime()-lastTimePulsShown.getTime())/1000));
            //Log.w("sessionDetail","dif:"+difference +"--timePassed:"+timePassedLastPulseShown);
            if ((difference>20) || (timePassedLastPulseShown>=30)){
                mMap.addMarker(new MarkerOptions()
                                .position(pos)
                                .icon(BitmapDescriptorFactory.defaultMarker(mapPulseToColor(temp)))
                                .title("Puls: "+temp)
                );
                lastTimePulsShown = new Date();
            }
        }
    }


    /**
     * maps the pulse to a matching color (low pulse = blue; high pulse = red)
     * @param pulse the pulse
     * @return a matching color hue
     */
    public float mapPulseToColor(int pulse){
//        public static final float HUE_RED = 0.0F;
//        public static final float HUE_ORANGE = 30.0F;
//        public static final float HUE_YELLOW = 60.0F;
//        public static final float HUE_GREEN = 120.0F;
//        public static final float HUE_CYAN = 180.0F;
//        public static final float HUE_AZURE = 210.0F;
//        public static final float HUE_BLUE = 240.0F;
        if (pulse < 70){
            return 240F;
        } else if (pulse < 200){
            //pulse     color
            //70        240
            //200       0
            //(LowerBorder - value) * (100 / upperBorder) --> 0-100%
            double percentOfMaxPulse = (pulse - 70) * ((float)100 / 130); //if pulse is 70, the value is 0; if pulse is 200, the value is 100

            return (-1*((240 * (float)percentOfMaxPulse/ 100) - 240));
        } else {
            if ((new Date().getTime()-vibrated.getTime()/1000)>30){
                Vibrator v = (Vibrator) getActivity().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(200);
            }
            vibrated = new Date();
            return 0F;
        }
    }

    @Override
    public void onStop() {
        Log.w("sessiondetail", "onstop");
        super.onStop();
    }

    @Override
    public void onStart() {
        Log.w("sessiondetail", "onstart");
        //bindToService();
        setUpMapIfNeeded();
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.w("sessiondetail", "onresume");
        mapView.onResume();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter("gps-event"));
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.w("sessiondetail", "pause");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        //getActivity().getSupportFragmentManager().popBackStack();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.w("sessiondetail", "ondestroy");
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

    @Override
    public void onDestroyView() {
        Log.w("sessiondetail", "onDestroyView");
        super.onDestroyView();
    }

    /**
     * updates the box underneath the map
     */
    public void updateInfoBox(){
        long duration = GPSDatabaseHandler.getInstance().getData().getDurationOfSession(SessionHandler.getSelectedRunId());
        long seconds = (TimeUnit.MILLISECONDS.toSeconds(duration))%60;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)%60;
        long hours = TimeUnit.MILLISECONDS.toHours(duration);
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        Cursor res = WeatherDatabaseHandler.getInstance().getData().getWeatherOfRun(SessionHandler.getSelectedRunId());
        res.moveToFirst();

        int temperature = -300;
        int wind = -1;
        String description = null;
        if (res.getCount() != 0){
            res.moveToFirst();
            temperature = res.getInt(2);
            wind = res.getInt(3);
            description = res.getString(7);
        }
        res.close();

        infoBox.setText("Session " + SessionHandler.getSelectedRunId() +
                "\nDauer: " + (hours != 0?hours+":":"")+minutes + ":" + (seconds < 10 ? "0" + seconds : seconds + "") + " Minuten" +
                "\nDistanz: " + ((int) GPSDatabaseHandler.getInstance().getData().getWalkDistance(SessionHandler.getSelectedRunId())) + " Meter" +
                "\n\u00D8 Geschwindigkeit: " + decimalFormat.format(GPSDatabaseHandler.getInstance().getData().getSpeed(SessionHandler.getSelectedRunId(), -1) * 3.6) + "km/h" +
                ((temperature!=-300)?(description+"\nTemperatur: " +temperature+"°C"+
                "\nWind: "+wind+ "km/h"):""));
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
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

            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //add the live liveMarker initially
        liveMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .visible(false));

        //is the int set to show a specific run at activity startup?
        Log.w("sessiondetail","showthisrun:"+SessionHandler.getSelectedRunId());

        if (SessionHandler.getSelectedRunId() != -1) {
            showRunOnMap();
        }
    }

    /**
     * shows the selected run on the map
     */
    public void showRunOnMap(){
        //save run data to an array
        Cursor res = GPSDatabaseHandler.getInstance().getData().getSession(SessionHandler.getSelectedRunId());

        //display the data
        updateInfoBox();
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
        if (!(
                (((MainActivity)getActivity()).getGps().getActiveRecordingID() == GPSDatabaseHandler.getInstance().getData().getLastSessionID()) &&
                (((MainActivity)getActivity()).getGps().getActiveRecordingID() == SessionHandler.getSelectedRunId())
            )) {
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

    /**
     * change the map view
     * @param v
     */
    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_map, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.w("CLICK",item.getOrder()+"-"+item.getGroupId()+"-"+item.getItemId()+"-"+item.getTitle());
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

}
