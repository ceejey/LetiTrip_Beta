package de.ehealth.project.letitrip_beta.view.fragment.fitbit;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.fitbit.FitBitGetJsonTask;
import de.ehealth.project.letitrip_beta.handler.fitbit.Oauth;
import de.ehealth.project.letitrip_beta.model.fitbit.FitbitUserProfile;
import de.ehealth.project.letitrip_beta.view.MainActivity;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;

/**
 * Created by Mirorn on 08.12.2015.
 */
public class CheckUserProfile extends Fragment {

    private FragmentChanger mListener;
    private Oauth mOauth = Oauth.getOauth();
    private FitbitUserProfile mUser;

    private EditText mEtxtFullName;
    private EditText mEtxtDateOfBirth;
    private EditText mEtxtHeight;
    private EditText mEtxtWeight;
    private EditText mEtxtWeightUnit;
    private EditText mEtxtmHeightUnit;
    private Button mBtnSaveUser;
    RadioButton mRbMale;
    RadioButton mRbFemale;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checkprofile, container, false);
        mEtxtFullName = (EditText) view.findViewById(R.id.etxtFullName);
        mEtxtDateOfBirth = (EditText) view.findViewById(R.id.etxtDateofBirth);
        mEtxtHeight = (EditText) view.findViewById(R.id.etxtHeight);
        mEtxtWeight = (EditText) view.findViewById(R.id.etxtWeight);
        mRbMale = (RadioButton) view.findViewById(R.id.rdMale);
        mRbFemale = (RadioButton) view.findViewById(R.id.rdFemale);
        mBtnSaveUser = (Button) view.findViewById(R.id.bnSaveUserProfile);
        try {
            new FitBitGetJsonTask(mOauth, FitBitGetJsonTask.ENDPOINT_PROFILE, getActivity()).execute().get();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mUser = FitbitUserProfile.getmActiveUser();

        if (!mUser.getmEncodedId().isEmpty()) {
            mEtxtFullName.setText(mUser.getmFullname());
            mBtnSaveUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveUser();
                }
            });
            mEtxtDateOfBirth.setText(mUser.getmDateOfBirth());
            mEtxtHeight.setText(mUser.getmHeight());
            mEtxtWeight.setText(mUser.getmWeight());
            if (mUser.getmGender().equals("MALE"))
                mRbMale.setChecked(true);
            else if (mUser.getmGender().equals("FEMALE"))
                mRbFemale.setChecked(true);
        }
        return view;
    }

    public void saveUser() {
        mUser.setmFullname(mEtxtFullName.getText().toString());
        if (mRbMale.isChecked())
            mUser.setmGender("MALE");
        else if (mRbFemale.isChecked())
            mUser.setmGender("FEMALE");
        mUser.setmDateOfBirth(mEtxtDateOfBirth.getText().toString());
        mUser.setmHeight(mEtxtHeight.getText().toString());
        mUser.setmWeight(mEtxtWeight.getText().toString());
        //change the difference between current weight and the weight from server
        try {
            new FitBitGetJsonTask(Oauth.getmOauth()
                    , FitBitGetJsonTask.ENDPOINT_WEIGHT, getActivity()
                    , Integer.parseInt(mEtxtWeight.getText().toString())).execute().get();
            FitbitUserProfile.getmActiveUser().setmWeight(mEtxtWeight.getText().toString());
            FitbitUserProfile.getmActiveUser().saveUser(getActivity());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        FitbitUserProfile.saveUser(getActivity());
        // updateActivity(MainActivity.FragmentEnum.HomeUser);
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

