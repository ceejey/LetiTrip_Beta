package de.ehealth.project.letitrip_beta.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;
import de.ehealth.project.letitrip_beta.R;

/**
 * Created by Lars on 15.02.2016.
 */
public class GPSListAdapter extends ArrayAdapter<GPSCustomListItem>{

    public GPSListAdapter(Context context, List<GPSCustomListItem> list) {
        super(context, R.layout.fragment_session_overview_adapter,list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.fragment_session_overview_adapter, null);
        }

        GPSCustomListItem gpsCustomListItem = getItem(position);

        if (gpsCustomListItem != null) {
            TextView txtHeading = (TextView) v.findViewById(R.id.txtHeading);
            TextView txtDate = (TextView) v.findViewById(R.id.txtDate);
            TextView txtDuration = (TextView) v.findViewById(R.id.txtDuration);
            TextView txtAvgSpeed = (TextView) v.findViewById(R.id.txtAvgSpeed);
            ImageView imgType = (ImageView) v.findViewById(R.id.imgType);

            if (gpsCustomListItem.isLive()){
                txtHeading.setText("(Live) Session #"+gpsCustomListItem.getVisibleID());
                txtDuration.setText("");
                txtAvgSpeed.setText("Klick für mehr Infos!");
            } else {
                if (txtHeading != null) {
                    txtHeading.setText("Session #"+gpsCustomListItem.getVisibleID()+" (#pos:"+gpsCustomListItem.getPositions()+")");
                }

                if (txtDuration != null) {
                    txtDuration.setText(gpsCustomListItem.getDuration()+ " Minuten");
                }

                if (txtAvgSpeed != null) {
                    txtAvgSpeed.setText("\u00D8Geschwindigkeit: "+new DecimalFormat("#.#").format(gpsCustomListItem.getAverageSpeed())+" km/h");
                }
            }

            if (txtDate != null) {
                txtDate.setText("Gestartet: "+gpsCustomListItem.getStarted());
            }

            if (imgType != null) {
                if (!gpsCustomListItem.isType()){
                    imgType.setImageResource(R.drawable.ic_directions_bike_black_48dp);
                } else {
                    imgType.setImageResource(R.drawable.ic_directions_run_black_48dp);
                }
            }
        }

        return v;
    }

}
