package de.ehealth.project.letitrip_beta.view.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
            TextView txtSessionNumber = (TextView) v.findViewById(R.id.txtSessionNumber);
            TextView txtDuration = (TextView) v.findViewById(R.id.txtDuration);
            TextView txtDistance = (TextView) v.findViewById(R.id.txtDistance);
            ImageView imgType = (ImageView) v.findViewById(R.id.imgType);

            txtSessionNumber.setText(gpsCustomListItem.getVisibleID()+"");

            if (gpsCustomListItem.isLive()){
                txtHeading.setText("(Läuft) Klicke hier für Details!");
                txtDuration.setText("");
                txtDistance.setText("");
            } else {
                txtHeading.setText(gpsCustomListItem.getStarted());
                txtDuration.setText(gpsCustomListItem.getDuration());
                txtDistance.setText(gpsCustomListItem.getDistanceMeter() + " m");
            }

            if (imgType != null) {
                if (gpsCustomListItem.getType() == 1){
                    imgType.setImageResource(R.drawable.ic_directions_bike_white_24dp);
                } else {
                    imgType.setImageResource(R.drawable.ic_directions_run_white_24dp);
                }
                imgType.setColorFilter(0xff757575, PorterDuff.Mode.MULTIPLY);
            }
        }

        return v;
    }

}
