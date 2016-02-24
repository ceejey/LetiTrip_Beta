package de.ehealth.project.letitrip_beta.view.fragment.settings;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.model.fitbit.FitbitUserProfile;
import de.ehealth.project.letitrip_beta.view.MainActivity;
import de.ehealth.project.letitrip_beta.view.adapter.SettingsAdapter;
import de.ehealth.project.letitrip_beta.view.adapter.SettingsRow;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;

public class Settings extends Fragment {


    private FragmentChanger mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        ListView listSettings = (ListView) view.findViewById(R.id.listSettings);
        List<SettingsRow> itemList = new ArrayList<>();
        itemList.add(new SettingsRow("Allgemein", "Einstellungen für die App"));
        itemList.add(new SettingsRow("Profil", "Hat sich ihr Gewicht oder das Fahrrad geändert?"));
        itemList.add(new SettingsRow("Geräte", "App mit einem Gerät koppeln?"));
        itemList.add(new SettingsRow("News", "Welche Themen sollen angezeigt werden?"));
        itemList.add(new SettingsRow("Hilfe", "Allgemein"));
        ListAdapter customAdapter = new SettingsAdapter(getActivity(), itemList);
        listSettings.setAdapter(customAdapter);

        //listSettings.setClickable(true);
        //listSettings.setOnItemClickListener();
        listSettings.setClickable(true);
        listSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                SettingsRow row = (SettingsRow) arg0.getItemAtPosition(position);
                if (row.getCustomItem().equals("Allgemein")) {
                    updateActivity(MainActivity.FragmentName.SETTINGS_GENERAL);
                }
                if (row.getCustomItem().equals("Profil")) {
                    if (!FitbitUserProfile.getmActiveUser().getmEncodedId().equals("")) {
                        updateActivity(MainActivity.FragmentName.SETTINGS_PROFILE);
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder //
                                .setMessage("Bitte verbinden Sie sich mit einem Fitbitracker unter Einstellungen -> Geräte")
                                        .setNegativeButton(getString(R.string.decline), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // TODO
                                                dialog.dismiss();
                                            }
                                        });
                        builder.show();
                    }
                }
                if (row.getCustomItem().equals("Geräte")) {
                    updateActivity(MainActivity.FragmentName.SETTINGS_DEVICE);
                }
                if (row.getCustomItem().equals("News")) {
                    updateActivity(MainActivity.FragmentName.NEWS_SETTINGS);
                }
                if (row.getCustomItem().equals("Hilfe")) {
                    updateActivity(MainActivity.FragmentName.SETTINGS_HELP);
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof FragmentChanger) {
            mListener = (FragmentChanger) activity;
        }
        if (FitbitUserProfile.getmActiveUser().getmEncodedId().equals("")){
            updateActivity(MainActivity.FragmentName.FIT_BIT_INIT);
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
