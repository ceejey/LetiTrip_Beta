package de.ehealth.project.letitrip_beta.view.fragment;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
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
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSService;
import de.ehealth.project.letitrip_beta.handler.session.SessionHandler;
import de.ehealth.project.letitrip_beta.model.settings.UserSettings;
import de.ehealth.project.letitrip_beta.view.MainActivity;
import de.ehealth.project.letitrip_beta.view.adapter.GPSCustomListItem;
import de.ehealth.project.letitrip_beta.view.adapter.GPSListAdapter;
import de.ehealth.project.letitrip_beta.view.adapter.RunSelectorDialog;

/**
 * Broadcastlist:
 * message      value   meaning
 * GPSService   1       GPS not enabled
 * GPSService   2       Tracking stopped
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

    private DialogInterface.OnClickListener dialogListener;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

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
        UserSettings.loadUser(getActivity());
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
                if (((MainActivity)getActivity()).isBound()) {
                    if ((((MainActivity)getActivity()).getGps().getStatus() == GPSService.Status.IDLE)) {
                        Intent i = new Intent(getActivity(), GPSService.class);
                        getActivity().startService(i);
                        gpsStatusTextView.setText("Session wird vorbereitet..");
                        btnStartSession.setText("Session beenden");
                    } else {
                        //unbindFromService from service to be able to stop it
                        ((MainActivity)getActivity()).unbindFromService();
                        ((MainActivity)getActivity()).stopService();

                        //bind again
                        ((MainActivity)getActivity()).bindToService();
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
                if ((((MainActivity)getActivity()).getGps() != null)  && (row.getDisplayType() != 2)){
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
                        GPSDatabaseHandler.getInstance().getData().deleteSession(SessionHandler.getSelectedRunId());
                        Toast.makeText(getActivity(),"Session gelöscht.", Toast.LENGTH_SHORT).show();
                        updateList();
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
                updateTrackingUI();
            }
        });

        imgBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionHandler.setRunType(1);
                updateTrackingUI();
            }
        });

        btnPauseSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity)getActivity()).isBound()) {
                    if (((MainActivity)getActivity()).getGps().isPaused()) {
                        ((MainActivity)getActivity()).getGps().resume();
                    } else {
                        ((MainActivity)getActivity()).getGps().pause();
                    }
                    updateTrackingUI();
                } else Log.w("sessionoverview", "bind error");
            }
        });

        return view;
    }

    public void showRunDialog(int id) {
        if (id > 0){
            DialogFragment newFragment = RunSelectorDialog.newInstance(id);
            newFragment.setTargetFragment(this, 1);
            newFragment.show(getFragmentManager().beginTransaction(), "DialogTag");
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateActivity(MainActivity.FragmentName fn) {
        mListener.changeFragment(fn);
    }

    /**
     * initializes the ui
     */
    @Override
    public void onStart() {
        super.onStart();
        //for >= android 6.0
        if (Build.VERSION.SDK_INT >= 23) doPermissionCheck();

        if (GPSDatabaseHandler.getInstance().getData().getLastSessionID() == 0) {
            SessionHandler.setSelectedRunId(-1);
        }

        updateTrackingUI();
        updateList();
    }

    /**
     * adds all available sessions (also live sessions) in descending order to the listview
     */
    public void updateList() {
        List valueList = new ArrayList<GPSCustomListItem>();
        int lastRun = GPSDatabaseHandler.getInstance().getData().getLastSessionID();

        if (lastRun==0){
            GPSCustomListItem emptyPlaceHolder = new GPSCustomListItem();
            emptyPlaceHolder.setDisplayType(2);
            valueList.add(emptyPlaceHolder);
        } else
        for (int i = lastRun; i > 0; i--) {
            GPSCustomListItem ins = GPSDatabaseHandler.getInstance().getData().getOverviewOfSession(i);
            if (ins != null) {
                if (((MainActivity)getActivity()).getGps().getStatus() == GPSService.Status.TRACKINGSTARTED){
                    if (i == lastRun) {
                        ins.setDisplayType(1);
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
        if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    //handler to receive broadcast messages from gps service
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int message = intent.getIntExtra("GPSService", -1);
            if (message == 1)  updateTrackingUI();
            if ((message == 2) ||(message == 4)){ //tracking started/stopped in service
                updateTrackingUI();
                updateList();
                if (message == 4){
                    SessionHandler.setSelectedRunId(((MainActivity)getActivity()).getGps().getActiveRecordingID());
                    mListener.changeFragment(MainActivity.FragmentName.SESSION);
                }
            } else if (message == 3) {
                gpsStatusTextView.setText("Warte auf GPS...");
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
                    default:
                        break;
                }
            break;
        }
    }

    /**
     * updates all UI components of SessionOverview
     */
    public void updateTrackingUI(){
        if (((MainActivity)getActivity()).getGps().getStatus()== GPSService.Status.SEARCHINGGPS) {
            btnStartSession.setText("Session beenden");
            gpsStatusTextView.setText("Session wird vorbereitet..");
            btnPauseSession.setText("Pause");
            btnPauseSession.setVisibility(View.GONE);
        } else if (((MainActivity)getActivity()).getGps().getStatus()== GPSService.Status.TRACKINGSTARTED) {
            btnStartSession.setText("Session beenden");
            gpsStatusTextView.setText("Session läuft.");
            btnPauseSession.setText("Pause");
            btnPauseSession.setVisibility(View.VISIBLE);
        } else if (((MainActivity)getActivity()).getGps().getStatus() == GPSService.Status.PAUSED) {
            btnStartSession.setText("Session beenden");
            gpsStatusTextView.setText("Session pausiert.");
            btnPauseSession.setText("Fortfahren");
            btnPauseSession.setVisibility(View.VISIBLE);
        } else {
            btnStartSession.setText("Session starten");
            gpsStatusTextView.setText("Session deaktiviert.");
            btnPauseSession.setText("Pause");
            btnPauseSession.setVisibility(View.GONE);
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bitmap;
        imgRun.setImageResource(R.drawable.ic_directions_run_white_24dp);
        imgBike.setImageResource(R.drawable.ic_directions_bike_white_24dp);

        if (SessionHandler.getRunType() == 1){
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_directions_bike_white_24dp,options);
            imgRun.setColorFilter(0xff757575, PorterDuff.Mode.MULTIPLY);
            imgBike.setColorFilter(0xff5c6bc0, PorterDuff.Mode.MULTIPLY);
        } else {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_directions_run_white_24dp,options);
            imgRun.setColorFilter(0xff5c6bc0, PorterDuff.Mode.MULTIPLY);
            imgBike.setColorFilter(0xff757575, PorterDuff.Mode.MULTIPLY);
        }

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xff757575);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        Bitmap mutableBitmap = Bitmap.createBitmap(bitmap).copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(mutableBitmap);
        canvas.drawRect(0, 0, bitmap.getWidth(), bitmap.getHeight(), paint);

        if (SessionHandler.getRunType() == 1){
            imgRun.setColorFilter(0xff757575, PorterDuff.Mode.MULTIPLY);
            imgBike.setColorFilter(0xff5c6bc0, PorterDuff.Mode.MULTIPLY);
            imgBike.setAdjustViewBounds(true);
            imgBike.setImageBitmap(mutableBitmap);
        } else {
            imgRun.setColorFilter(0xff5c6bc0, PorterDuff.Mode.MULTIPLY);
            imgBike.setColorFilter(0xff757575, PorterDuff.Mode.MULTIPLY);
            imgRun.setAdjustViewBounds(true);
            imgRun.setImageBitmap(mutableBitmap);
        }
    }

    /**
     * debug method. prints all information of a specific session
     * @param selectedSession the session ID
     */
    public void getRunOutput(int selectedSession) {
        Cursor res = GPSDatabaseHandler.getInstance().getData().getSession(Integer.parseInt(selectedSession + ""));

        if (res != null){
            String str="";
            while (res.moveToNext()){
                str="";
                str+=("ID:"+res.getString(0)+"\t");
                str+=("RunID:"+res.getString(1)+"\t");
                str+=("Time:"+res.getString(2)+"\t");
                str+=("Latitude:"+res.getString(3)+"\t");
                str+=("Longitude:"+res.getString(4)+"\t");
                str+=("Altitude:"+res.getString(5)+"\t");
                str+=("bicycle:"+res.getString(6)+"\n");
                str+=("pulse:"+res.getString(7)+"\n");
                str+=("speed:"+res.getString(8)+"\n");
                str+=("watt:"+res.getString(9)+"\n");
                str+=("calories:"+res.getString(10)+"\n");
                Log.w("gps", str);
            }
            res.close();
        }
    }
}
