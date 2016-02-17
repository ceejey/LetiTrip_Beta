package de.ehealth.project.letitrip_beta.handler.gpshandler;

import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

public class GPSHelper {
    public void updateTrackingUI(GPSService gps, Button btnSession, Button btnPause, TextView txtStatus, Switch bicycleSwitch){
        if (gps.getStatus()== GPSService.Status.SEARCHINGGPS) {
            btnSession.setText("Session beenden");
            txtStatus.setText("Aufnahme startet bald...");
            btnPause.setText("Pause");
            btnPause.setVisibility(View.GONE);
        } else if (gps.getStatus()== GPSService.Status.TRACKINGSTARTED) {
            btnSession.setText("Session beenden");
            txtStatus.setText("Aufnahme läuft.");
            btnPause.setText("Pause");
            btnPause.setVisibility(View.VISIBLE);
        } else if (gps.getStatus() == GPSService.Status.PAUSED) {
            btnSession.setText("Session beenden");
            txtStatus.setText("Aufnahme pausiert.");
            btnPause.setText("Fortfahren");
            btnPause.setVisibility(View.VISIBLE);
        } else {
            btnSession.setText("Session starten");
            txtStatus.setText("Aufnahme deaktiviert.");
            btnPause.setText("Pause");
            btnPause.setVisibility(View.GONE);
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
                str+=("pulse:"+res.getString(7));
                Log.w("gps", str);
            }
            res.close();
        }
    }
}
