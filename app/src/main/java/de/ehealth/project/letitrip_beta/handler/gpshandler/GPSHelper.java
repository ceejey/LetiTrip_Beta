package de.ehealth.project.letitrip_beta.handler.gpshandler;

import android.database.Cursor;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

public class GPSHelper {
    public void updateTrackingUI(GPSService gps, Button btnSession, TextView txtStatus, Switch bicycleSwitch, Button pauseButton){
        if (gps.getStatus()== GPSService.Status.SEARCHINGGPS) {
            btnSession.setText("Session beenden");
            txtStatus.setText("Aufnahme startet bald...");
            pauseButton.setText("Pause");
            pauseButton.setEnabled(false);
        } else if (gps.getStatus()== GPSService.Status.TRACKINGSTARTED) {
            btnSession.setText("Session beenden");
            txtStatus.setText("Aufnahme läuft.");
            if (gps.isPaused()) pauseButton.setText("Fortfahren"); else pauseButton.setText("Pause");
            pauseButton.setEnabled(true);
        } else {
            btnSession.setText("Session starten");
            txtStatus.setText("Aufnahme deaktiviert.");
            pauseButton.setText("Pause");
            pauseButton.setEnabled(false);
        }

        if (gps.getRecordingAsBicycle() == 1){
            bicycleSwitch.setChecked(true);
            bicycleSwitch.setText("Fahrrad");
        }
    }

    public void getRunOutput(int selectedRun) {
        Cursor res = GPSDatabaseHandler.getInstance().getData().getSession(Integer.parseInt(selectedRun + ""));

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
