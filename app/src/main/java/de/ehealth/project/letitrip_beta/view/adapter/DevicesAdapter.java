package de.ehealth.project.letitrip_beta.view.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.ehealth.project.letitrip_beta.R;

/**
 * Created by Mirorn on 15.12.2015.
 */
public class DevicesAdapter extends ArrayAdapter<DevicesRow> {

        public DevicesAdapter(Activity context, List<DevicesRow> deviceList) {
            super(context, R.layout.fragment_settings_adapter, deviceList);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.fragment_settings_adapter, null);
            }

            DevicesRow devicesRow = getItem(position);

            if (devicesRow != null) {
                TextView txtItem = (TextView) v.findViewById(R.id.txtItem);
                TextView txtSubItem = (TextView) v.findViewById(R.id.txtSubItem);

                if (txtItem != null) {
                    txtItem.setText(devicesRow.getCustomItem());
                }

                if (txtSubItem != null) {
                    txtSubItem.setText(devicesRow.getSubItem());
                }
            }

            return v;
        }
    }

