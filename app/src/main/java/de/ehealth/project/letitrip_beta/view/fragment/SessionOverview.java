package de.ehealth.project.letitrip_beta.view.fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSCustomListItem;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSDatabaseHandler;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSService;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSHelper;
import de.ehealth.project.letitrip_beta.handler.session.SessionHandler;
import de.ehealth.project.letitrip_beta.handler.weather.WeatherDatabaseHandler;
import de.ehealth.project.letitrip_beta.view.MainActivity;
import de.ehealth.project.letitrip_beta.view.adapter.RunSelectorDialog;

/**
 * Broadcastlist:
 * message      value   meaning
 * GPSActivity  1       GPS not enabled
 * GPSActivity  2       Tracking initially started
 * MapsActivity 1       new position inserted
 */

public class SessionOverview extends Fragment {

    private FragmentChanger mListener;
    private TextView gpsStatusTextView;

    private ToggleButton gpsEnabledToggle;
    private ListView sessionOverviewListView;
    private Switch bicycleSwitch;
    private Button pauseButton;

    private ArrayAdapter<GPSCustomListItem> itemsAdapter;

    private NotificationManager myNotificationManager;
    private DialogInterface.OnClickListener dialogListener;
    private GPSService gps;
    boolean bound = false;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GPSHelper myGPSObject;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            GPSService.LocalBinder binder = (GPSService.LocalBinder) service;
            gps = binder.getService();
            bound = true;
            Log.w("sessionoverview", "bound - status:" + gps.getStatus() + "");

            myGPSObject.updateTrackingUI(gps, gpsEnabledToggle, gpsStatusTextView, bicycleSwitch,pauseButton);
            updateList();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.w("sessionoverview", "unbound");
            bound = false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Log.w("sessionoverview","oncreateview called");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_session_overview, container, false);

        gpsStatusTextView = (TextView) view.findViewById(R.id.gpsStatusTextView);
        gpsEnabledToggle = (ToggleButton) view.findViewById(R.id.gpsEnabledToggle);
        sessionOverviewListView = (ListView) view.findViewById(R.id.sessionOverviewListView);
        bicycleSwitch = (Switch) view.findViewById(R.id.bycicleSwitch);
        pauseButton = (Button) view.findViewById(R.id.pauseButton);

        //tracking on/off
        gpsEnabledToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("sessionoverview", "bound:" + bound);

                if (bound) {
                    if (gpsEnabledToggle.isChecked()) {
                        Log.w("soverview", "checked");
                        Intent i = new Intent(getActivity(), GPSService.class);
                        i.putExtra("bicycle", (bicycleSwitch.isChecked() ? 1 : 0));
                        getActivity().startService(i);
                        gpsStatusTextView.setText("Aufnahme startet bald...");
                    } else {
                        Log.w("sessionoverview", "stopping...");
                        //unbind from service to be able to stop it
                        getActivity().unbindService(mConnection);

                        bound = false;
                        getActivity().stopService(new Intent(getActivity(), GPSService.class));
                        updateList();

                        //bind again
                        getActivity().bindService(new Intent(getActivity(), GPSService.class), mConnection, Context.BIND_AUTO_CREATE);
                    }
                } else Log.w("sessionoverview", "bind error");
            }
        });

        sessionOverviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SessionHandler.setSelectedRunId(itemsAdapter.getItem(position).getID());

                //the dialog menu is only available for finished sessions, live sessions will be shown in the "session" fragment
                if (gps != null){
                    if (gps.getActiveRecordingID() == SessionHandler.getSelectedRunId()){
                        updateActivity(MainActivity.FragmentName.SESSION);
                        return;
                    }
                }
                showRunDialog(SessionHandler.getSelectedRunId());

            }
        });

        //used for delete
        dialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        int deletes = GPSDatabaseHandler.getInstance().getData().deleteRun(SessionHandler.getSelectedRunId());
                        Toast.makeText(getActivity(), deletes + " Einträge gelöscht.", Toast.LENGTH_SHORT).show();
                        updateList();
                        Log.w("sessionoverview", GPSDatabaseHandler.getInstance().getData().getLastRunID() + "");
                        if (GPSDatabaseHandler.getInstance().getData().getLastRunID() != 0) {
                            SessionHandler.setSelectedRunId(GPSDatabaseHandler.getInstance().getData().getLastRunID());
                        }
                        if (GPSDatabaseHandler.getInstance().getData().getLastRunID() == 0) {
                            SessionHandler.setSelectedRunId(-1);
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        bicycleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bicycleSwitch.setText(isChecked ? "Fahrrad" : "Lauf");
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bound) {
                    Button b = (Button) v;
                    if (gps.isPaused()) {
                        gps.resume();
                    } else {
                        gps.pause();
                    }
                    myGPSObject.updateTrackingUI(gps,gpsEnabledToggle,gpsStatusTextView,bicycleSwitch,pauseButton);
                } else Log.w("sessionoverview", "bind error");
            }
        });
        return view;
    }

    public void showRunDialog(int id) {
        if (id > 0){
            DialogFragment newFragment = RunSelectorDialog.newInstance(id);
            newFragment.setTargetFragment(this, 1);
            newFragment.show(getFragmentManager().beginTransaction(),"DialogTag");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof FragmentChanger) {
            mListener = (FragmentChanger) activity;
        } else {
            Log.d("Fitbit", "Wrong interface implemented");
        }

        //TODO
        /*try { <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            interfaceSender = (ShowRunOnMap) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement ShowRunOnMap");
        }*/
    }

    @Override
    public void onDestroy() {
        getActivity().unbindService(mConnection);
        super.onDestroy();
        //Log.w("sessionoverview","ondestroy");
    }

    @Override
    public void onDetach() {
        //Log.d("sessionoverview", "deattach");
        super.onDetach();
        mListener = null;
    }

    public void updateActivity(MainActivity.FragmentName fn) {
        //Log.d("sessionoverview", "update");
        mListener.changeFragment(fn);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d("sessionoverview", "onCreate");
        myGPSObject = new GPSHelper();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("sessionoverview", "onStop");
    }

    @Override
    public void onStart() {
        super.onStart();
        //Log.d("sessionoverview", "onStart");
        getActivity().bindService(new Intent(getActivity(), GPSService.class), mConnection, Context.BIND_AUTO_CREATE);
        testMethod();

        //only gets called when user returns to this fragment via back button
        if ((gps != null) && (sessionOverviewListView.getAdapter() == null)){
            updateList();
            //has to be called again. when opening map and returning to this fragment the service is already bound and
            //"updatTrackingUI" wont be called
            myGPSObject.updateTrackingUI(gps, gpsEnabledToggle, gpsStatusTextView, bicycleSwitch,pauseButton);
        }

        //for >android 6.0
        if (Build.VERSION.SDK_INT >= 23) doPermissionCheck();

        if (GPSDatabaseHandler.getInstance().getData().getLastRunID() == 0) {
            SessionHandler.setSelectedRunId(-1);
        }
    }


    public void testMethod() {
//            DataHolder_Database.getInstance().getData().addData(2, 51.615970, 6.707983, 50,0);
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            DataHolder_Database.getInstance().getData().addData(2, 51.625980, 6.807983, 51, 0);
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            DataHolder_Database.getInstance().getData().addData(2, 51.635990, 6.707983, 52, 0);
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            DataHolder_Database.getInstance().getData().addData(2, 51.646960, 6.907983, 53, 0);
          GPSDatabaseHandler.getInstance().getData().addData(1, 26.790425, 17.537951, 50,0);
          GPSDatabaseHandler.getInstance().getData().addData(1, 68.222841, 14.725451, 50,0);

          //GPSDatabaseHandler.getInstance().getData().addData(1, 51.575960, 6.707983, 50,0);
          //GPSDatabaseHandler.getInstance().getData().addData(3, 51.500896, 6.890523, 50,1);
          //GPSDatabaseHandler.getInstance().getData().addData(4, 51.662572, 6.612438, 50,1);

        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //Date d = new Date();
        //String a = dateFormat.format(d);
        //Log.w("distance", "distance is: " + DataHolder_Database.getInstance().getData().getRunDistanceOfDay(a));
        //Log.w("sessionoverview","bound:"+bound);
        //Log.w("sessionoverview","status" + gps.getStatus());

        //Log.w("sessionoverview",""+DataHolder_Database.getInstance().getData().getAverageSpeed(74,75));
        //Log.w("gps",DataHolder_Database.getInstance().getData().getLastRunID()+"");
        //DataHolder_Database.getInstance().getData().getAverageSpeed(1,2);
    }

    public void updateList() {
        List valueList = new ArrayList<GPSCustomListItem>();
        int lastRun = GPSDatabaseHandler.getInstance().getData().getLastRunID();

        for (int i = 1; i <= lastRun; i++) {
            String ins = GPSDatabaseHandler.getInstance().getData().getOverviewOfRun(i);
            if (ins != null) {
                if (gps.getStatus() == GPSService.Status.TRACKINGSTARTED){
                    if (i == lastRun) ins = "(Tracking)" + "Session #" + i + " - Für Details hier klicken.";
                }
                valueList.add(new GPSCustomListItem(i, ins));
            }
        }
        itemsAdapter = new ArrayAdapter<GPSCustomListItem>(getActivity(), android.R.layout.simple_list_item_1, valueList);
        sessionOverviewListView.setAdapter(itemsAdapter);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void doPermissionCheck() {
        Log.w("sessionOverview", "permission check");
        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w("sessionOverview", "not granted");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //Log.w("sessionoverview", "onpause");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    //handler to receive broadcast messages from gps service
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int message = intent.getIntExtra("GPSActivity", -1);
            Log.w("sessionoverview", "broadcast:" + message);
            if (message == 1) myGPSObject.updateTrackingUI(gps, gpsEnabledToggle, gpsStatusTextView, bicycleSwitch,pauseButton);
            if (message == 2) {
                myGPSObject.updateTrackingUI(gps,gpsEnabledToggle,gpsStatusTextView, bicycleSwitch,pauseButton);
                updateList(); //tracking started in service
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        //Log.w("sessionoverview", "onresume");

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter("gps-event"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.w("sessionOverview", "granted!");
                } else {
                    Log.w("sessionOverview", "not granted");
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 1:
                switch (resultCode){
                    case 0:
                        //only put the runID to the intent if map shouldnt show the current live session
                        if (!((gps.getStatus() == GPSService.Status.TRACKINGSTARTED) && (gps.getActiveRecordingID() == SessionHandler.getSelectedRunId()))) {
                            //TODO
                            //interfaceSender.setSelectedRunID(selectedRun); <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                            SessionHandler.setSelectedRunId(SessionHandler.getSelectedRunId());
                            mListener.changeFragment(MainActivity.FragmentName.SESSION_DETAIL);
                        } else updateActivity(MainActivity.FragmentName.SESSION_DETAIL);
                        break;
                    case 1:
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage("Sind Sie sich sicher?").setPositiveButton("Ja", dialogListener)
                                .setNegativeButton("Nein", dialogListener).show();
                        break;
                    case 2:
                        myGPSObject.getRunOutput(SessionHandler.getSelectedRunId());
                        Log.w("sessionoverview", "weatheravailable?" + WeatherDatabaseHandler.getInstance().getData().getLatestWeather());
                        WeatherDatabaseHandler.getInstance().getData().outPutAll();
                        break;
                    default:
                        break;
                }
            break;
        }
    }

}
