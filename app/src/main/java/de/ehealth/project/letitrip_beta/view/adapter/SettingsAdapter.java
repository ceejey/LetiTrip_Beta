package de.ehealth.project.letitrip_beta.view.adapter;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import de.ehealth.project.letitrip_beta.R;

public class SettingsAdapter extends ArrayAdapter<SettingsRow> {

    public SettingsAdapter(Activity context, List<SettingsRow> rowList) {
        super(context, R.layout.fragment_settings_adapter, rowList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.fragment_settings_adapter, null);
        }

        SettingsRow settingsRow = getItem(position);

        if (settingsRow != null) {
            TextView txtItem = (TextView) v.findViewById(R.id.txtItem);
            TextView txtSubItem = (TextView) v.findViewById(R.id.txtSubItem);

            if (txtItem != null) {
                txtItem.setText(settingsRow.getCustomItem());
            }

            if (txtSubItem != null) {
                txtSubItem.setText(settingsRow.getSubItem());
            }
        }

        return v;
    }
}