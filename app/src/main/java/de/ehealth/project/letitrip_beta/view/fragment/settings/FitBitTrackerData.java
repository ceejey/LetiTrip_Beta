package de.ehealth.project.letitrip_beta.view.fragment.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.database.FitBitUserDataSQLite;
import de.ehealth.project.letitrip_beta.handler.fitbit.FitBitActivityScoreHandler;
import de.ehealth.project.letitrip_beta.handler.fitbit.FitBitGetJsonTask;
import de.ehealth.project.letitrip_beta.handler.fitbit.Oauth;
import de.ehealth.project.letitrip_beta.model.fitbit.Summary;
import de.ehealth.project.letitrip_beta.view.MainActivity;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;

/*
 * Created by Mirorn on 20.11.2015.
 * This fragment shows the FitBitData of today "getTodayInformation()" and also of the last past 2 weeks
 * The information of the last two weeks are separated in two fragments "MoveDiagramm" ans "Calories
 * Diagramm. "
 */
public class FitBitTrackerData extends android.support.v4.app.Fragment{

    private FragmentChanger mListener;
    private TextView mEtxtDate;
    private TextView mEtxtaktSteps;
    private TextView mEtxtaktCalories;
    private TextView mEtxtStepsAim;
    private TextView mEtxtCaloriesAim;
    private EditText mEtxtWeight;
    private ProgressBar mprBarSteps;
    private ProgressBar mprBarCalories;
    private Button mBtRefresh;
    private Fragment mMoveFragment;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

      //  Fragment moveDiagramm = new MoveDiagramm();
      //  FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
      //  transaction.add(R.id.fragmentMove, moveDiagramm).commit();

        View view =  inflater.inflate(R.layout.fragment_fitbit_tracker_data, container, false);


        //mMoveFragment = (Fragment) view.findViewById(R.id.fragmentMove);
        mEtxtDate = (TextView) view.findViewById(R.id.Date);
        mEtxtaktSteps = (TextView) view.findViewById(R.id.txtvSteps);
        mEtxtaktCalories = (TextView) view.findViewById(R.id.vtxtCalories);
        mEtxtStepsAim = (TextView) view.findViewById(R.id.vtxtStapsAim);
        mEtxtCaloriesAim = (TextView) view.findViewById(R.id.vtxtCaloriesAim);
        mEtxtWeight = (EditText) view.findViewById(R.id.etxtWeight);

        mprBarSteps = (ProgressBar) view.findViewById(R.id.progressBarSteps);
        mprBarSteps.setMax(10000);
        mprBarSteps.setScaleY(4f);

        mprBarCalories = (ProgressBar) view.findViewById(R.id.progressBarCalories);
        mprBarCalories.setScaleY(4f);
        mprBarCalories.setMax(5000);

        mBtRefresh = (Button) view.findViewById(R.id.btRefresh);
        mBtRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTodayInformation();
            }
        });

        getTodayInformation();
        FitBitActivityScoreHandler.getmActivityScore().calcActivtiyScore(getActivity());
        return view;
    }


    private void getTodayInformation(){
        Calendar now = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String today = format.format(now.getTime());
        try {
            new FitBitGetJsonTask(Oauth.getmOauth()
                    , FitBitGetJsonTask.ENDPOINT_MOVE, getActivity()).execute().get();
             Summary user = FitBitUserDataSQLite.getInstance(getActivity())
                    .getCurFitBitUserMovement().getSummary();
            mEtxtDate.setText(today);
            mprBarSteps.setProgress(Integer.parseInt(user.getSteps()));
            mprBarCalories.setProgress(Integer.parseInt(user.getCaloriesOut()));
            mEtxtaktSteps.setText(user.getSteps());
            mEtxtaktCalories.setText(user.getCaloriesOut());
            mEtxtStepsAim.setText(String.valueOf((int) FitBitActivityScoreHandler.getmStepsAim()));
            mEtxtCaloriesAim.setText(String.valueOf((int) FitBitActivityScoreHandler.getmCaloriesAim()));
        }catch(Exception ex){
            new AlertDialog.Builder(getActivity())
                    .setTitle("Warning")
                    .setMessage("Connection Problems!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            ex.printStackTrace();
        }
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

