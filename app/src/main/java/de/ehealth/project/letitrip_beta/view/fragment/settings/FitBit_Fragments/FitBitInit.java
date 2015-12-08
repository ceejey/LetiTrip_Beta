package de.ehealth.project.letitrip_beta.view.fragment.settings.FitBit_Fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.model.Fitbit_Tracker.FitBitAPI;
import de.ehealth.project.letitrip_beta.model.Fitbit_Tracker.FitBit_Oauth.Oauth;
import de.ehealth.project.letitrip_beta.model.Fitbit_Tracker.FitbitUserProfile;
import de.ehealth.project.letitrip_beta.view.MainActivity;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;

/**
 * Created by Mirorn on 08.12.2015.
 */
public class FitBitInit extends Fragment{

    private FragmentChanger mListener;
    Oauth mOauth = Oauth.getOauth();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mOauth.initOauth("3444e1985fcecca0dd97ff85e4253c45", "e4263b0e379b61c4916e4427d594f5c2", "http://www.google.de", FitBitAPI.class);
        FitbitUserProfile.loadUser(getActivity());
        if (!FitbitUserProfile.getmActiveUser().getmEncodedId().equals("")){
            //updateActivity(MainActivity.);
        }
        View view =  inflater.inflate(R.layout.fragment_fitbit_initial, container, false);
        Button button = (Button) view.findViewById(R.id.startOauthButton);
        button.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View v) {
                        updateActivity(MainActivity.FragmentName.WEB_VIEW_OAUTH);
                    }
                });
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

