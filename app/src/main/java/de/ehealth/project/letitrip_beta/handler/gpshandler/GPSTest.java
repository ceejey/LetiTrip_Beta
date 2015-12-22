package de.ehealth.project.letitrip_beta.handler.gpshandler;

import android.database.Cursor;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

public class GPSTest {
    public void updateTrackingUI(GPSService gps, ToggleButton tb1, TextView tv1, Switch bycicleSwitch){
        if (gps.getStatus()== GPSService.Status.SEARCHINGGPS) {
            tb1.setChecked(true);
            tv1.setText("Aufnahme startet bald...");
        } else if (gps.getStatus()== GPSService.Status.TRACKINGSTARTED) {
            tb1.setChecked(true);
            tv1.setText("Aufnahme läuft.");
        } else {
            tb1.setChecked(false);
            tv1.setText("Aufnahme deaktiviert.");
        }

        if (gps.getRecordingAsBicycle() == 1){
            bycicleSwitch.setChecked(true);
            bycicleSwitch.setText("Fahrrad");
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
            res.close();
        }
    }
}
