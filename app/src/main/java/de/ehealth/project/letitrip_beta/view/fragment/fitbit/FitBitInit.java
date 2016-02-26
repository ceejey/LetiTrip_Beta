package de.ehealth.project.letitrip_beta.view.fragment.fitbit;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.fitbit.Oauth;
import de.ehealth.project.letitrip_beta.model.settings.UserSettings;
import de.ehealth.project.letitrip_beta.view.MainActivity;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;

/**
 * Created by Mirorn on 08.12.2015.
 */
public class FitBitInit extends Fragment {

    private FragmentChanger mListener;
    Oauth mOauth = Oauth.getOauth();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (!UserSettings.getmActiveUser().getmFitBitUserID().equals("")) {
            //updateActivity(MainActivity.);
        }
        View view = inflater.inflate(R.layout.fragment_fitbit_initial, container, false);
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

