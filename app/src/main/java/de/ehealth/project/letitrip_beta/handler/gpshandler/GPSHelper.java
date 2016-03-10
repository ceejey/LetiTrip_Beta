package de.ehealth.project.letitrip_beta.handler.gpshandler;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.session.SessionHandler;

public class GPSHelper {

    private GPSService gps;
    private Button btnSession;
    private Button btnPause;
    private TextView txtStatus;
    private ImageView imgRun;
    private ImageView imgBike;
    private Resources resource;
    private Context context;

    public GPSHelper(GPSService gps, Button btnSession, Button btnPause, TextView txtStatus, ImageView imgRun, ImageView imgBike, Resources resource, Context context) {
        this.gps = gps;
        this.btnSession = btnSession;
        this.btnPause = btnPause;
        this.txtStatus= txtStatus;
        this.imgRun = imgRun;
        this.imgBike = imgBike;
        this.resource = resource;
        this.context= context;
    }

    /**
     * updates all UI components of SessionOverview
     */
    public void updateTrackingUI(){
        if (gps.getStatus()== GPSService.Status.SEARCHINGGPS) {
            btnSession.setText("Session beenden");
            txtStatus.setText("Session wird vorbereitet..");
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

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        Bitmap bitmap;
        imgRun.setImageResource(R.drawable.ic_directions_run_white_24dp);
        imgBike.setImageResource(R.drawable.ic_directions_bike_white_24dp);

        if (SessionHandler.getRunType() == 1){
            bitmap = BitmapFactory.decodeResource(resource, R.drawable.ic_directions_bike_white_24dp,options);
            imgRun.setColorFilter(0xff757575, PorterDuff.Mode.MULTIPLY);
            imgBike.setColorFilter(0xff5c6bc0, PorterDuff.Mode.MULTIPLY);
        } else {
            bitmap = BitmapFactory.decodeResource(resource, R.drawable.ic_directions_run_white_24dp,options);
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
