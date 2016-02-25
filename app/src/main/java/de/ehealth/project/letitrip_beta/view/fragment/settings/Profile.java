package de.ehealth.project.letitrip_beta.view.fragment.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.fitbit.FitBitActivityScoreHandler;
import de.ehealth.project.letitrip_beta.handler.fitbit.FitBitGetJsonTask;
import de.ehealth.project.letitrip_beta.handler.fitbit.Oauth;
import de.ehealth.project.letitrip_beta.model.fitbit.FitbitUserProfile;
import de.ehealth.project.letitrip_beta.view.MainActivity;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;

/**
 * Created by Mirorn on 08.12.2015.
 */
public class Profile extends Fragment {

    private FragmentChanger mListener;
    private Oauth mOauth = Oauth.getOauth();
    private FitbitUserProfile mUser;

    private TextView mVtxtFullName;
    private TextView mVtxtHeight;
    private TextView mVtxtAge;
    private TextView mVtxtFahrradTyp;
    private TextView mVtxtReifenTyp;
    private EditText mEtxtWeight;
    private EditText mEtxtCity;
    private Button mBtnSaveUser;
    private CheckBox mCbBike;
    private Spinner mSpReifentyp;
    private Spinner mSpFahrradTyp;
    private boolean checkbox = false;

    RadioButton mRbMale;
    RadioButton mRbFemale;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checkprofile, container, false);
        mVtxtFullName = (TextView) view.findViewById(R.id.vtxtFullName);
        mVtxtAge = (TextView) view.findViewById(R.id.vtxtAge);
        mVtxtHeight = (TextView) view.findViewById(R.id.vtxtHeight);
        mEtxtWeight = (EditText) view.findViewById(R.id.etxtWeight);
        mRbMale = (RadioButton) view.findViewById(R.id.rdMale);
        mRbFemale = (RadioButton) view.findViewById(R.id.rdFemale);
        mBtnSaveUser = (Button) view.findViewById(R.id.bnSaveUserProfile);
        mCbBike = (CheckBox) view.findViewById(R.id.cbFahrrad);
        mVtxtFahrradTyp = (TextView) view.findViewById(R.id.vtxtFahrradtyp);
        mVtxtReifenTyp = (TextView) view.findViewById(R.id.vtxtReifentyp);
        mSpReifentyp = (Spinner) view.findViewById(R.id.spReifenTyp);
        mSpFahrradTyp = (Spinner) view.findViewById(R.id.spFahrradTyp);
        mEtxtCity = (EditText) view.findViewById(R.id.etxtCity);
        mEtxtCity.setText(FitbitUserProfile.getmActiveUser().getmCity());

        final List<String> typenListe = new ArrayList<String>();

        typenListe.add("Nichts ausgewählt");
        typenListe.add("Stollen (Normal)");
        typenListe.add("Stollen (Groß)");
        typenListe.add("Straße (Standart)");
        typenListe.add("Straße (Schmal)");
        mSpReifentyp.setAdapter(new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, typenListe));

        for(int i = 0; i < typenListe.size(); i++ ) {
            if(typenListe.get(i).contains(FitbitUserProfile.getmReifenTyp())) {
                mSpReifentyp.setSelection(i);
            }
        }
        final List<String> typenListetwo = new ArrayList<String>();

        typenListetwo.add("Nichts ausgewählt");
        typenListetwo.add("Cityrad (aufrechtsitzend)");
        typenListetwo.add("Stollen (vorgebeugt)");
        typenListetwo.add("Tourenrad tiefer Lenker (vorgebeugt)");
        typenListetwo.add("Rennrad (vorgebeugt)");
        mSpFahrradTyp.setAdapter(new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, typenListetwo));
        for(int i = 0; i < typenListetwo.size(); i++ ) {
            if(typenListetwo.get(i).contains(FitbitUserProfile.getmFahrradTyp())) {
                mSpFahrradTyp.setSelection(i);
            }
        }

        if(FitbitUserProfile.getmFahrradTyp().equals("Nichts ausgewählt") || FitbitUserProfile.getmReifenTyp().equals("Nichts ausgewählt")) {

            mVtxtFahrradTyp.setVisibility(view.INVISIBLE);
            mVtxtReifenTyp.setVisibility(view.INVISIBLE);
            mSpFahrradTyp.setVisibility(view.INVISIBLE);
            mSpReifentyp.setVisibility(view.INVISIBLE);
            checkbox = false;
        }
        else{
            mCbBike.setChecked(!mCbBike.isChecked());
            checkbox = true;
            mSpReifentyp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    FitbitUserProfile.setmReifenTyp(typenListe.get(position));
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            mSpFahrradTyp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    FitbitUserProfile.setmFahrradTyp(typenListetwo.get(position));
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
        try {
            new FitBitGetJsonTask(mOauth, FitBitGetJsonTask.ENDPOINT_PROFILE, getActivity()).execute().get();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        mUser = FitbitUserProfile.getmActiveUser();
        mCbBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkbox) {
                    mVtxtFahrradTyp.setVisibility(v.VISIBLE);
                    mVtxtReifenTyp.setVisibility(v.VISIBLE);
                    mSpFahrradTyp.setVisibility(v.VISIBLE);
                    mSpReifentyp.setVisibility(v.VISIBLE);
                    mSpReifentyp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            FitbitUserProfile.setmReifenTyp(typenListe.get(position));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    mSpFahrradTyp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            FitbitUserProfile.setmFahrradTyp(typenListetwo.get(position));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    checkbox = true;
                }
                else {
                    mVtxtFahrradTyp.setVisibility(v.INVISIBLE);
                    mVtxtReifenTyp.setVisibility(v.INVISIBLE);
                    mSpFahrradTyp.setVisibility(v.INVISIBLE);
                    mSpReifentyp.setVisibility(v.INVISIBLE);
                    checkbox = false;
                }
            }
        });
        if (!mUser.getmEncodedId().isEmpty()) {
            mVtxtFullName.setText(mUser.getmFullname());
            mBtnSaveUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveUser();
                }
            });
            mVtxtAge.setText(mUser.getmAge());
            mVtxtHeight.setText(mUser.getmHeight());
            mEtxtWeight.setText(mUser.getmWeight());
            if (mUser.getmGender().equals("MALE"))
                mRbMale.setChecked(true);
            else if (mUser.getmGender().equals("FEMALE"))
                mRbFemale.setChecked(true);
        }

        return view;
    }

    public void saveUser() {

        mUser.setmWeight(mEtxtWeight.getText().toString());
        mUser.setmCity(mEtxtCity.getText().toString());
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

        if(!checkbox){
            FitbitUserProfile.setmFahrradTyp("Nichts ausgewählt");
            FitbitUserProfile.setmReifenTyp("Nichts ausgewählt");
        }

        FitbitUserProfile.saveUser(getActivity());
        FitBitActivityScoreHandler.calcActivtiyScore(getActivity());
        updateActivity(MainActivity.FragmentName.SETTINGS);
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

