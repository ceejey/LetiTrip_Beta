package de.ehealth.project.letitrip_beta.view.fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.database.GPSDatabase;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSCustomListItem;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSDatabaseHandler;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSService;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSTest;
import de.ehealth.project.letitrip_beta.view.MainActivity;

public class SessionOverview extends Fragment {

    private FragmentChanger mListener;
    private TextView gpsStatusTextView;
    private CheckBox byciclecheckBox;
    private ToggleButton gpsEnabledToggle;
    private ListView sessionOverviewListView;

    private ArrayAdapter<GPSCustomListItem> itemsAdapter;
    private int selectedRun = -1;
    private NotificationManager myNotificationManager;
    private DialogInterface.OnClickListener dialogListener;
    private GPSService gps;
    boolean bound = false;
    private BroadcastReceiver broadcastReceiver;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GPSTest myGPSObject;


    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            GPSService.LocalBinder binder = (GPSService.LocalBinder) service;
            gps = binder.getService();
            bound = true;
            Log.w("sessionoverview","GEBUNDEN");
            Log.w("sessionoverview", "status:" + gps.getStatus() + "");

            myGPSObject.updateTrackingUI(gps, gpsEnabledToggle, gpsStatusTextView, byciclecheckBox);
            updateList();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.w("sessionoverview","ungebunden");
            bound = false;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_session_overview, container, false);

        gpsStatusTextView = (TextView) view.findViewById(R.id.gpsStatusTextView);
        gpsEnabledToggle = (ToggleButton) view.findViewById(R.id.gpsEnabledToggle);
        sessionOverviewListView = (ListView) view.findViewById(R.id.sessionOverviewListView);
        byciclecheckBox = (CheckBox) view.findViewById(R.id.byciclecheckBox);

        byciclecheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateActivity(MainActivity.FragmentName.SESSION_DETAIL);
            }
        });
        //tracking on/off
        gpsEnabledToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("sessionoverview", GPSDatabaseHandler.getInstance().getData().testMethod() + "");
                if (bound) {
                    if (gpsEnabledToggle.isChecked()) {
                        Intent i = new Intent(getActivity(), GPSService.class);
                        i.putExtra("bicycle", (byciclecheckBox.isChecked() ? 1 : 0));
                        getActivity().startService(i);
                        gpsStatusTextView.setText("Record starts soon...");
                    } else {
                        Log.w("sessionoverview", "stopping...");
                        //unbind from service to be able to stop it
                        getActivity().unbindService(mConnection);
                        bound = false;

                        Intent i = new Intent(getActivity(), GPSService.class);
                        getActivity().stopService(i);
                        gpsStatusTextView.setText("Tracking disabled.");
                        updateList();
                        //bind again
                        bindToService();
                    }
                } else Log.w("sessionoverview", "bind error");
            }
        });

        sessionOverviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedRun = itemsAdapter.getItem(position).getID();
                showRunDialog(selectedRun);
            }
        });


        //used for delete
        dialogListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        int deletes = GPSDatabaseHandler.getInstance().getData().deleteRun(selectedRun);
                        Toast.makeText(getActivity(), deletes + " Entries deleted.", Toast.LENGTH_SHORT).show();
                        updateList();
                        Log.w("sessionoverview",GPSDatabaseHandler.getInstance().getData().getLastRunID()+"");
                        if (GPSDatabaseHandler.getInstance().getData().getLastRunID()!=0){
                            selectedRun = GPSDatabaseHandler.getInstance().getData().getLastRunID();
                        }
                        if (GPSDatabaseHandler.getInstance().getData().getLastRunID()==0) {
                            selectedRun = -1;
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        return view;
    }

    public void showRunDialog(int id) {
        Toast.makeText(getActivity(),"Shit aint working yet",Toast.LENGTH_SHORT).show();

//            if (id > 0){
//                RunSelectorDialog newFragment = new RunSelectorDialog(id);
//                newFragment.show(getFragmentManager(),"DialogTag");
//            } else {
//                Toast.makeText(getActivity(),"Select/record a run first",Toast.LENGTH_SHORT).show();
//            }

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
        Log.d("sessionoverviewfrag","deattach");

        super.onDetach();
        mListener = null;
    }

    public void updateActivity(MainActivity.FragmentName fn) {
        Log.d("sessionoverviewfrag","update");
        mListener.changeFragment(fn);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("sessionoverviewfrag", "onCreate");
        myGPSObject = new GPSTest();
        GPSDatabase myDB = new GPSDatabase(getActivity());
        GPSDatabaseHandler.getInstance().setData(myDB);

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("sessionoverviewfrag", "onStop");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("sessionoverviewfrag", "onStart");
        bindToService();
        //testMethod();

        //for >android 6.0
        if (Build.VERSION.SDK_INT  >= 23) doPermissionCheck();

        if (GPSDatabaseHandler.getInstance().getData().getLastRunID() == 0) {
            selectedRun = -1;
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int message = intent.getIntExtra("GPSActivity",-1);
                Log.w("sessionoverview","broadcast:"+message);
                if (message == 1) myGPSObject.updateTrackingUI(gps, gpsEnabledToggle, gpsStatusTextView, byciclecheckBox);
                if (message == 2) updateList();
            }
        };
        //registering receiver
        IntentFilter intentFilter = new IntentFilter("android.intent.action.MAIN");
        getActivity().registerReceiver(broadcastReceiver, intentFilter);

        //sessionOverviewListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //sessionOverviewListView.setSelection(2);
    }



    public void bindToService() {
        Intent i = new Intent(getActivity(), GPSService.class);
        getActivity().bindService(i, mConnection, Context.BIND_AUTO_CREATE);
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
//        GPSDatabaseHandler.getInstance().getData().addData(1, 51.500896, 6.890523, 50,0);
//        GPSDatabaseHandler.getInstance().getData().addData(1, 51.662572, 6.612438, 50,0);
//        GPSDatabaseHandler.getInstance().getData().addData(1, 51.575960, 6.707983, 50,0);
//        GPSDatabaseHandler.getInstance().getData().addData(3, 51.500896, 6.890523, 50,1);
//        GPSDatabaseHandler.getInstance().getData().addData(4, 51.662572, 6.612438, 50,1);

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
                if (gps.getStatus() == GPSService.Status.TRACKINGSTARTED)
                    if (i == lastRun)  ins = "(Recording)"+ins;
                valueList.add(new GPSCustomListItem(i,ins));
            }
        }
        itemsAdapter = new ArrayAdapter <GPSCustomListItem> (getActivity(), android.R.layout.simple_list_item_1, valueList);
        sessionOverviewListView.setAdapter(itemsAdapter);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void doPermissionCheck(){
        Log.w("sessionOverview", "permission check");
        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.w("sessionOverview", "not granted");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.w("sessionOverview","granted!");
                } else {
                    Log.w("sessionOverview","not granted");
                }
                return;
            }
        }
    }
    @SuppressLint("ValidFragment")
    public class RunSelectorDialog extends DialogFragment {

        private int id;

        public RunSelectorDialog(int id) {
            this.id = id;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
            builder.setTitle("Run #" + id)
                    .setItems(new String[]{"Show on Map", "Delete", "(debug1)", "(debug2"}, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    //TODO
                                    /*
                                    Intent intent = new Intent(getActivity(), MapsActivity.class);
                                    //only put the runID to the intent if map shouldnt show the current live track
                                    if (!((gps.getStatus() == GPSService.Status.TRACKINGSTARTED) && (gps.getActiveRecordingID() == selectedRun))) {
                                        intent.putExtra("runID", selectedRun);
                                    }

                                    startActivity(intent);*/
                                    break;
                                case 1:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogListener)
                                            .setNegativeButton("No", dialogListener).show();
                                    break;
                                case 2:
                                    myGPSObject.getRunOutput(selectedRun);
                                    Log.w("sessionoverview", selectedRun + "");
                                    break;
                                case 3:
                                    testMethod();
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
            return builder.create();
        }
    }

}
