package de.ehealth.project.letitrip_beta.view.adapter;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by Mirorn on 15.12.2015.
 */
public class DeviceSelectorDialog extends DialogFragment {

    //private int id;

    public static DeviceSelectorDialog newInstance(int id) {
        DeviceSelectorDialog dialog = new DeviceSelectorDialog();
        Bundle args = new Bundle();
        args.putInt("id",id);
        dialog.setArguments(args);
        //this.id = id;
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int id = getArguments().getInt("id");
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        if(id == 0) {
            builder.setTitle("FitBit Account").setItems(new String[]{"Mit einem anderen Account verbinden", "Abbrechen"}, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    getTargetFragment().onActivityResult(1, which, getActivity().getIntent());
                }
            });
        }
        else{
            builder.setTitle("Polar").setItems(new String[]{"Löschen", "Abbrechen"}, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    getTargetFragment().onActivityResult(1, which, getActivity().getIntent());
                }
            });
        }

        return builder.create();

    }
}
