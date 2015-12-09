package de.ehealth.project.letitrip_beta.handler.gpshandler;

import android.database.Cursor;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.ToggleButton;

public class GPSTest {
    public void updateTrackingUI(GPSService gps, ToggleButton tb1, TextView tv1, CheckBox cb1){
        if (gps.getStatus()== GPSService.Status.SEARCHINGGPS) {
            tb1.setChecked(true);
            tv1.setText("Record starting soon...");
        } else if (gps.getStatus()== GPSService.Status.TRACKINGSTARTED) {
            tb1.setChecked(true);
            tv1.setText("Tracking started.");
        } else{
            tb1.setChecked(false);
            tv1.setText("Tracking disabled.");
        }

        if (gps.getRecordingAsBicycle()==1){
            cb1.setChecked(true);
        }
    }

    public void getRunOutput(int selectedRun) {
        Cursor res = GPSDatabaseHandler.getInstance().getData().getRun(Integer.parseInt(selectedRun+""));

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
                Log.w("gps", str);
            }

        }
    }
}