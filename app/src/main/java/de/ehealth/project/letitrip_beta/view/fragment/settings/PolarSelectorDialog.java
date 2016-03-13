package de.ehealth.project.letitrip_beta.view.fragment.settings;


import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.List;

/**
 * Created by Mirorn on 27.12.2015.
 */
public class PolarSelectorDialog extends DialogFragment {

    private List<BluetoothDevice> mDeviceList;

    public static PolarSelectorDialog newInstance(int id) {
        PolarSelectorDialog dialog = new PolarSelectorDialog();
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
        builder.setTitle("Gerät #" + id)
                .setItems(new String[]{"Verbinden"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getTargetFragment().onActivityResult(1,which,getActivity().getIntent());
                    }
                });
        return builder.create();

    }
}