package de.ehealth.project.letitrip_beta.view.fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSDatabaseHandler;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSHelper;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSService;
import de.ehealth.project.letitrip_beta.handler.session.SessionHandler;
import de.ehealth.project.letitrip_beta.handler.weather.WeatherDatabaseHandler;
import de.ehealth.project.letitrip_beta.view.MainActivity;
import de.ehealth.project.letitrip_beta.view.adapter.GPSCustomListItem;
import de.ehealth.project.letitrip_beta.view.adapter.GPSListAdapter;
import de.ehealth.project.letitrip_beta.view.adapter.RunSelectorDialog;

/**
 * Broadcastlist:
 * message      value   meaning
 * GPSService   1       GPS not enabled
 * GPSService   2       Tracking stopped; Main connected to gps
 * GPSService   3       GPS accuracy too low
 * GPSService   4       Tracking initially started
 * GPSService   5       new position inserted
 * MainActivity 1       bound to gps
 */

public class SessionOverview extends Fragment {

    private FragmentChanger mListener;
    private TextView gpsStatusTextView;

    private Button btnStartSession;
    private ListView sessionOverviewListView;
    private ImageView imgRun, imgBike;
    private Button btnPauseSession;

    private NotificationManager myNotificationManager;
    private DialogInterface.OnClickListener dialogListener;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GPSHelper myGPSObject;

    /**
     * creates all ui elements and their listeners
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_session_overview, container, false);

        gpsStatusTextView = (TextView) view.findViewById(R.id.gpsStatusTextView);
        btnStartSession = (Button) view.findViewById(R.id.gpsEnabledToggle);
        sessionOverviewListView = (ListView) view.findViewById(R.id.sessionOverviewListView);
        imgRun = (ImageView) view.findViewById(R.id.imgRun);
        imgBike = (ImageView) view.findViewById(R.id.imgBike);
        btnPauseSession = (Button) view.findViewById(R.id.pauseButton);
        btnPauseSession.setVisibility(View.GONE);

        //tracking on/off
        btnStartSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Log.d("sessionoverview", "bound:" + ((MainActivity) getActivity()).isBound());

            if (((MainActivity)getActivity()).isBound()) {
                if ((((MainActivity)getActivity()).getGps().getStatus() == GPSService.Status.IDLE)) {
                    Log.w("sessionoverview", "starting...");
                    Intent i = new Intent(getActivity(), GPSService.class);
                    getActivity().startService(i);
                    gpsStatusTextView.setText("Aufnahme startet bald...");
                    btnStartSession.setText("Session beenden");
                } else {
                    Log.w("sessionoverview", "stopping...");
                    //unbindFromService from service to be able to stop it
                    ((MainActivity)getActivity()).unbindFromService();
                    ((MainActivity)getActivity()).stopService();

                    //bind again
                    ((MainActivity)getActivity()).bindToService();
                    gpsStatusTextView.setText("Aufnahme deaktiviert");
                    btnStartSession.setText("Session starten");
                    btnPauseSession.setText("Pause");
                    btnPauseSession.setVisibility(View.GONE);
                }
            } else Log.w("sessionoverview", "bind error");
            }
        });

        sessionOverviewListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GPSCustomListItem row = (GPSCustomListItem) parent.getItemAtPosition(position);
                SessionHandler.setSelectedRunId(row.getID());

                //the dialog menu is only available for finished sessions, live sessions will be shown in the "session" fragment
                if (((MainActivity)getActivity()).getGps() != null) {
                    if (((MainActivity)getActivity()).getGps().getActiveRecordingID() == SessionHandler.getSelectedRunId()) {
                        updateActivity(MainActivity.FragmentName.SESSION);
                        return;
                    } else showRunDialog(SessionHandler.getSelectedRunId());
                }
            }
        });

        //used for delete
        dialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        int deletes = GPSDatabaseHandler.getInstance().getData().deleteSession(SessionHandler.getSelectedRunId());
                        Toast.makeText(getActivity(), deletes + " Einträge gelöscht.", Toast.LENGTH_SHORT).show();
                        updateList();
                        Log.w("sessionoverview", GPSDatabaseHandler.getInstance().getData().getLastSessionID() + "");
                        if (GPSDatabaseHandler.getInstance().getData().getLastSessionID() != 0) {
                            SessionHandler.setSelectedRunId(GPSDatabaseHandler.getInstance().getData().getLastSessionID());
                        } else {
                            SessionHandler.setSelectedRunId(-1);
                        }

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        imgRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionHandler.setRunType(0);
                myGPSObject.updateTrackingUI(((MainActivity)getActivity()).getGps(), btnStartSession, btnPauseSession, gpsStatusTextView, imgRun, imgBike);
            }
        });

        imgBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionHandler.setRunType(1);
                myGPSObject.updateTrackingUI(((MainActivity)getActivity()).getGps(), btnStartSession, btnPauseSession, gpsStatusTextView, imgRun, imgBike);
            }
        });

        btnPauseSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity)getActivity()).isBound()) {
                    if (((MainActivity)getActivity()).getGps().isPaused()) {
                        Log.w("soverview","isPaused");
                        ((MainActivity)getActivity()).getGps().resume();
                    } else {
                        ((MainActivity)getActivity()).getGps().pause();
                    }
                    myGPSObject.updateTrackingUI(((MainActivity)getActivity()).getGps(), btnStartSession, btnPauseSession, gpsStatusTextView, imgRun, imgBike);
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
    }

    @Override
    public void onDestroy() {
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

    /**
     * initializes the ui
     */
    @Override
    public void onStart() {
        super.onStart();
        Log.d("sessionoverview", "onStart");
        testMethod();

        //for >= android 6.0
        if (Build.VERSION.SDK_INT >= 23) doPermissionCheck();

        if (GPSDatabaseHandler.getInstance().getData().getLastSessionID() == 0) {
            SessionHandler.setSelectedRunId(-1);
        }

        myGPSObject.updateTrackingUI(((MainActivity) getActivity()).getGps(), btnStartSession, btnPauseSession, gpsStatusTextView, imgRun, imgBike);
        updateList();
    }

    //todo delete later
    public void testMethod() {
      GPSDatabaseHandler.getInstance().getData().addData(1, 26.790425, 17.537951, 50,0,100);
      GPSDatabaseHandler.getInstance().getData().addData(1, 68.222841, 14.725451, 50,0,120);
    }

    /**
     * adds all available sessions (also live sessions) in descending order to the listview
     */
    public void updateList() {
        List valueList = new ArrayList<GPSCustomListItem>();
        int lastRun = GPSDatabaseHandler.getInstance().getData().getLastSessionID();

        for (int i = lastRun; i > 0; i--) {
            GPSCustomListItem ins = GPSDatabaseHandler.getInstance().getData().getOverviewOfSession(i);
            if (ins != null) {
                if (((MainActivity)getActivity()).getGps().getStatus() == GPSService.Status.TRACKINGSTARTED){
                    if (i == lastRun) {
                        ins.setLive(true);
                    }
                }

                //set id manually since the gpsdatabase doesn't know it
                ins.setID(i);
                valueList.add(ins);
            }
        }

        ListAdapter customAdapter = new GPSListAdapter(getActivity(),valueList);
        sessionOverviewListView.setAdapter(customAdapter);
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
            int message = intent.getIntExtra("GPSService", -1);
            if (message == 1)  myGPSObject.updateTrackingUI(((MainActivity)getActivity()).getGps(), btnStartSession, btnPauseSession, gpsStatusTextView, imgRun, imgBike);
            if ((message == 2) ||(message == 4)){ //tracking started/stopped in service
                myGPSObject.updateTrackingUI(((MainActivity) getActivity()).getGps(), btnStartSession, btnPauseSession, gpsStatusTextView, imgRun, imgBike);
                updateList();
                if (message == 4){
                    SessionHandler.setSelectedRunId(((MainActivity)getActivity()).getGps().getActiveRecordingID());
                    mListener.changeFragment(MainActivity.FragmentName.SESSION);
                }
            } else if (message == 3) {
                gpsStatusTextView.setText("GPS zu ungenau...");
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
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

    /**
     * handles the result code of the clicked listview popup
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 1:
                switch (resultCode){
                    case 0:
                        SessionHandler.setSelectedRunId(SessionHandler.getSelectedRunId());
                        mListener.changeFragment(MainActivity.FragmentName.SESSION_DETAIL);
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
                        Log.w("sessionoverview", "bound:" + ((MainActivity)getActivity()).isBound());
                        Log.w("sessionoverview", "status:" + ((MainActivity)getActivity()).getGps().getStatus());

                        break;
                    default:
                        break;
                }
            break;
        }
    }
}
