package de.ehealth.project.letitrip_beta.view.fragment.settings;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.recipe.RecipeUpdateHandler;
import de.ehealth.project.letitrip_beta.model.settings.UserSettings;
import de.ehealth.project.letitrip_beta.view.MainActivity;
import de.ehealth.project.letitrip_beta.view.fragment.Bar;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;

public class General extends Fragment {

    private FragmentChanger mListener;
    private Button mbtnUpdateRecipes;
    private Button mbtnResetApp;
    private SeekBar msbrTouchSensibility;

    private ImageView imgUpdate;
    private ImageView imgResetActScor;
    private ImageView imgResetApp;
    private ImageView imgTouch;
    private TextView mtxtViewRecipiUpdate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_general, container, false);

        mtxtViewRecipiUpdate = (TextView) view.findViewById(R.id.txtUpdateDate);
        mtxtViewRecipiUpdate.setText(UserSettings.getmActiveUser().getmLastRezeptUpdateSince());
        imgUpdate = (ImageView) view.findViewById(R.id.imgUpdate);
        imgResetActScor = (ImageView) view.findViewById(R.id.imgResetActScore);
        imgResetApp = (ImageView) view.findViewById(R.id.imgResetApp);
        imgTouch = (ImageView) view.findViewById(R.id.imgTouch);

        imgUpdate.setColorFilter(0xff757575, PorterDuff.Mode.MULTIPLY);
        imgResetActScor.setColorFilter(0xff757575, PorterDuff.Mode.MULTIPLY);
        imgResetApp.setColorFilter(0xff757575, PorterDuff.Mode.MULTIPLY);
        imgTouch.setColorFilter(0xff757575, PorterDuff.Mode.MULTIPLY);

        mbtnUpdateRecipes = (Button) view.findViewById(R.id.btnUpdateRecipes);
        mbtnUpdateRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRecipeDatabase();
            }
        });
        mbtnResetApp = (Button) view.findViewById(R.id.btnResetApp);
        mbtnResetApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder
                        .setMessage("Sind Sie sicher ?")
                        .setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // TODO
                                resetApp();
                            }
                        })
                        .setNegativeButton(getString(R.string.decline), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // TODO
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        });
        msbrTouchSensibility = (SeekBar)view.findViewById(R.id.sbrTouchSensibility);
        msbrTouchSensibility.setMax(30);
        msbrTouchSensibility.setProgress(Integer.parseInt(UserSettings.getmActiveUser().getmClickOffsetForBarSensibility()));
        msbrTouchSensibility.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                UserSettings.getmActiveUser().setmClickOffsetForBarSensibility(Integer.toString(msbrTouchSensibility.getProgress()));
                UserSettings.saveUser(getActivity());
                Bar.setClickOffset(msbrTouchSensibility.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
            }
        });
        return view;
    }

    private void updateRecipeDatabase(){
        new RecipeUpdateHandler().updateRecipeDatabase(getActivity());
        mtxtViewRecipiUpdate.setText(UserSettings.getmActiveUser().getmLastRezeptUpdateSince());
    }

    private void resetApp(){
        UserSettings.getmActiveUser().deleteUser(getActivity());
        if (getActivity().deleteDatabase("LetitripDB")) Log.w("general","DB deleted");
        if (getActivity().deleteDatabase("LetitripDB2")) Log.w("general","DB deleted");
        if (getActivity().deleteDatabase("LetitripDB3")) Log.w("general","DB deleted");
        if (getActivity().deleteDatabase("FitbitUserDB")) Log.w("general","DB deleted");

        SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences("userprofile", Context.MODE_PRIVATE);
        prefs.edit().clear().commit();

        SharedPreferences prefs2 = getActivity().getApplicationContext().getSharedPreferences("de.ehealth.project.letitrip_beta", Context.MODE_PRIVATE);
        prefs2.edit().clear().commit();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Zurücksetzen erfolgreich.\nDie App wird nun neugestartet.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = getActivity().getBaseContext().getPackageManager()
                                .getLaunchIntentForPackage(getActivity().getBaseContext().getPackageName());
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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
