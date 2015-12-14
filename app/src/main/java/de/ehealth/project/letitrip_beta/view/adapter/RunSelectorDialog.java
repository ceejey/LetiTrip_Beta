package de.ehealth.project.letitrip_beta.view.adapter;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class RunSelectorDialog extends DialogFragment {

    //private int id;

    public static RunSelectorDialog newInstance(int id) {
        RunSelectorDialog dialog = new RunSelectorDialog();
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
        builder.setTitle("Session #" + id)
                .setItems(new String[]{"Karte", "Löschen", "(debug1)", "(debug2"}, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getTargetFragment().onActivityResult(1,which,getActivity().getIntent());
                    }
                });
        return builder.create();

    }
}