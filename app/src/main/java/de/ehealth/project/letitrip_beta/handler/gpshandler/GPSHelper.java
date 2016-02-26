package de.ehealth.project.letitrip_beta.handler.gpshandler;

import android.database.Cursor;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import de.ehealth.project.letitrip_beta.handler.session.SessionHandler;

public class GPSHelper {

    /**
     * updates all UI components of SessionOverview
     * @param gps
     * @param btnSession
     * @param btnPause
     * @param txtStatus
     * @param imgRun
     * @param imgBike
     */
    public void updateTrackingUI(GPSService gps, Button btnSession, Button btnPause, TextView txtStatus, ImageView imgRun, ImageView imgBike){
        if (gps.getStatus()== GPSService.Status.SEARCHINGGPS) {
            btnSession.setText("Session beenden");
            txtStatus.setText("Session startet bald...");
            btnPause.setText("Pause");
            btnPause.setVisibility(View.GONE);
        } else if (gps.getStatus()== GPSService.Status.TRACKINGSTARTED) {
            btnSession.setText("Session beenden");
            txtStatus.setText("Session läuft.");
            btnPause.setText("Pause");
            btnPause.setVisibility(View.VISIBLE);
        } else if (gps.getStatus() == GPSService.Status.PAUSED) {
            btnSession.setText("Session beenden");
            txtStatus.setText("Session pausiert.");
            btnPause.setText("Fortfahren");
            btnPause.setVisibility(View.VISIBLE);
        } else {
            btnSession.setText("Session starten");
            txtStatus.setText("Session deaktiviert.");
            btnPause.setText("Pause");
            btnPause.setVisibility(View.GONE);
        }

        if (SessionHandler.getRunType() == 1){
            imgRun.setColorFilter(0xff757575, PorterDuff.Mode.MULTIPLY);
            imgBike.setColorFilter(0xff5c6bc0, PorterDuff.Mode.MULTIPLY);
        } else {
            imgRun.setColorFilter(0xff5c6bc0, PorterDuff.Mode.MULTIPLY);
            imgBike.setColorFilter(0xff757575, PorterDuff.Mode.MULTIPLY);
        }
    }

    /**
     * debug method. prints all information of a specific session
     * @param selectedSession
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
