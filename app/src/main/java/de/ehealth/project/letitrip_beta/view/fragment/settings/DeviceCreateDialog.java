package de.ehealth.project.letitrip_beta.view.fragment.settings;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.model.fitbit.FitbitUserProfile;
import de.ehealth.project.letitrip_beta.view.MainActivity;
import de.ehealth.project.letitrip_beta.view.adapter.DevicesRow;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;

/**
 * Created by Mirorn on 14.12.2015.
 */
public class DeviceCreateDialog extends DialogFragment {

    private FragmentChanger mListener;

    public static DeviceCreateDialog newInstance() {
        DeviceCreateDialog dialog = new DeviceCreateDialog();
      //  Bundle args = new Bundle();
      //  args.putInt("id",id);
      //  dialog.setArguments(args);
        //this.id = id;
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View modifyView = inflater.inflate(R.layout.dialog_devices, container, false);
        ImageView fitBit = (ImageView) modifyView.findViewById(R.id.imv_FitBit);
        List<DevicesRow> itemList = new ArrayList<>();
        if (FitbitUserProfile.getmActiveUser().getmEncodedId().equals("")) {
            fitBit.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: {
                            ImageView view = (ImageView) v;
                            //overlay is black with transparency of 0x77 (119)
                            view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                            view.invalidate();
                            updateActivity(MainActivity.FragmentName.FIT_BIT_INIT);
                            view.getDrawable().clearColorFilter();
                            getDialog().dismiss();
                            break;
                        }
                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_CANCEL: {
                            ImageView view = (ImageView) v;
                            //clear the overlay
                            view.getDrawable().clearColorFilter();
                            view.invalidate();
                            break;
                        }
                    }
                    return true;
                }
            });
        }
        else{
            fitBit.setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
            modifyView.findViewById(R.id.layout_FitBit).setBackgroundColor(Color.GRAY);
        }
        return modifyView;

    }
    public void updateActivity(MainActivity.FragmentName fn) {
        mListener.changeFragment(fn);
    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof FragmentChanger) {
            mListener = (FragmentChanger) activity;
        } else {
            Log.d("Fitbit", "Wrong interface implemented");
        }
    }
}

