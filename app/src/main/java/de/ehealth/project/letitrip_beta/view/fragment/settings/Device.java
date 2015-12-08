package de.ehealth.project.letitrip_beta.view.fragment.settings;


import android.app.Activity;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.view.MainActivity;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;

public class Device extends Fragment {

    private FragmentChanger mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_device, container, false);

        ImageView imgAddDevice = (ImageView) view.findViewById(R.id.imgAddDevice);
        imgAddDevice.setColorFilter(0xff5c6bc0, PorterDuff.Mode.MULTIPLY);
        //Test
        return view;
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void updateActivity(MainActivity.FragmentName fn) {
        mListener.changeFragment(fn);
    }
}
