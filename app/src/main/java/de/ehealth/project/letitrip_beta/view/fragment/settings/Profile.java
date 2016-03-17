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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.fitbit.FitBitGetJsonTask;
import de.ehealth.project.letitrip_beta.handler.fitbit.Oauth;
import de.ehealth.project.letitrip_beta.model.settings.UserSettings;
import de.ehealth.project.letitrip_beta.view.MainActivity;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;

/**
 * Created by Mirorn on 08.12.2015.
 */
public class Profile extends Fragment {

    private FragmentChanger mListener;
    private Oauth mOauth = Oauth.getOauth();
    private UserSettings mUser = UserSettings.getmActiveUser();

    private EditText mVtxtFullName;
    private EditText mVtxtHeight;
    private EditText mVtxtAge;
    private TextView mVtxtFahrradTyp;
    private TextView mVtxtFahrradGewicht;
    private TextView mVtxtReifenTyp;
    private EditText mEtxtWeight;
    private EditText mEtxtCity;
    private EditText mBikeWeight;
    private Button mBtnSaveUser;
    private Button mBtnImportFitbit;
    private CheckBox mCbBike;
    private Spinner mSpReifentyp;
    private Spinner mSpFahrradTyp;
    private boolean checkbox = false;

    RadioButton mRbMale;
    RadioButton mRbFemale;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checkprofile, container, false);
        mVtxtFullName = (EditText) view.findViewById(R.id.vtxtFullName);
        mVtxtAge = (EditText) view.findViewById(R.id.vtxtAge);
        mVtxtHeight = (EditText) view.findViewById(R.id.vtxtHeight);
        mEtxtWeight = (EditText) view.findViewById(R.id.etxtWeight);
        mRbMale = (RadioButton) view.findViewById(R.id.rdMale);
        mRbFemale = (RadioButton) view.findViewById(R.id.rdFemale);
        mBtnSaveUser = (Button) view.findViewById(R.id.bnSaveUserProfile);
        mBtnImportFitbit = (Button) view.findViewById(R.id.bnImportFitbit);
        mCbBike = (CheckBox) view.findViewById(R.id.cbFahrrad);
        mVtxtFahrradTyp = (TextView) view.findViewById(R.id.vtxtFahrradtyp);
        mVtxtReifenTyp = (TextView) view.findViewById(R.id.vtxtReifentyp);
        mSpReifentyp = (Spinner) view.findViewById(R.id.spReifenTyp);
        mSpFahrradTyp = (Spinner) view.findViewById(R.id.spFahrradTyp);
        mEtxtCity = (EditText) view.findViewById(R.id.etxtCity);
        mBikeWeight = (EditText) view.findViewById(R.id.vtxtBikeWeight);
        mVtxtFahrradGewicht = (TextView) view.findViewById(R.id.vtxtFahrradGewicht);

        final List<String> typenListe = new ArrayList<String>();

        typenListe.add("Nichts ausgewählt");
        typenListe.add("Stollen (Normal)");
        typenListe.add("Stollen (Groß)");
        typenListe.add("Straße (Standart)");
        typenListe.add("Straße (Schmal)");
        mSpReifentyp.setAdapter(new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item, typenListe));

        for(int i = 0; i < typenListe.size(); i++ ) {
            if(typenListe.get(i).contains(mUser.getmReifenTyp())) {
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
            if(typenListetwo.get(i).contains(mUser.getmFahrradTyp())) {
                mSpFahrradTyp.setSelection(i);
            }
        }

        if(mUser.getmFahrradTyp().equals("Nichts ausgewählt") || mUser.getmReifenTyp().equals("Nichts ausgewählt")) {

            mVtxtFahrradTyp.setVisibility(view.INVISIBLE);
            mVtxtReifenTyp.setVisibility(view.INVISIBLE);
            mSpFahrradTyp.setVisibility(view.INVISIBLE);
            mSpReifentyp.setVisibility(view.INVISIBLE);
            mBikeWeight.setVisibility(view.INVISIBLE);
            mVtxtFahrradGewicht.setVisibility(view.INVISIBLE);
            checkbox = false;
        }
        else{
            mCbBike.setChecked(!mCbBike.isChecked());
            checkbox = true;
            mSpReifentyp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mUser.setmReifenTyp(typenListe.get(position));
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            mSpFahrradTyp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mUser.setmFahrradTyp(typenListetwo.get(position));
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        if (UserSettings.getmActiveUser().getmAccessToken() != null) {
            try {
                new FitBitGetJsonTask(mOauth, FitBitGetJsonTask.ENDPOINT_PROFILE, getActivity()).execute().get();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        mCbBike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkbox) {
                    mBikeWeight.setVisibility(v.VISIBLE);
                    mVtxtFahrradTyp.setVisibility(v.VISIBLE);
                    mVtxtReifenTyp.setVisibility(v.VISIBLE);
                    mSpFahrradTyp.setVisibility(v.VISIBLE);
                    mSpReifentyp.setVisibility(v.VISIBLE);
                    mVtxtFahrradGewicht.setVisibility(v.VISIBLE);
                    mSpReifentyp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            mUser.setmReifenTyp(typenListe.get(position));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    mSpFahrradTyp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            mUser.setmFahrradTyp(typenListetwo.get(position));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    checkbox = true;
                } else {
                    mBikeWeight.setVisibility(v.INVISIBLE);
                    mVtxtFahrradTyp.setVisibility(v.INVISIBLE);
                    mVtxtReifenTyp.setVisibility(v.INVISIBLE);
                    mSpFahrradTyp.setVisibility(v.INVISIBLE);
                    mSpReifentyp.setVisibility(v.INVISIBLE);
                    mVtxtFahrradGewicht.setVisibility(v.INVISIBLE);
                    checkbox = false;
                }
            }
        });


        mBtnSaveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEtxtCity.getText().toString().equals("") || mVtxtAge.getText().toString().equals("") || mVtxtHeight.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), "Bitte füllen Sie alle notwendigen Felder aus!", Toast.LENGTH_LONG).show();
                } else {
                    saveUser();
                }
            }
        });

        mBtnImportFitbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.changeFragment(MainActivity.FragmentName.FIT_BIT_INIT);
            }
        });

        mVtxtFullName.setText(mUser.getmFullname());
        mVtxtAge.setText(mUser.getmAge());
        mVtxtHeight.setText(mUser.getmHeight());
        mEtxtWeight.setText(mUser.getmWeight());
        mBikeWeight.setText(mUser.getmBikeWeight());
        mEtxtCity.setText(UserSettings.getmActiveUser().getmCity());

        if (mUser.getmGender().equals("MALE"))
            mRbMale.setChecked(true);
        else if (mUser.getmGender().equals("FEMALE"))
            mRbFemale.setChecked(true);
        return view;
    }

    public void saveUser() {

        mUser.setmWeight(mEtxtWeight.getText().toString());
        mUser.setmCity(mEtxtCity.getText().toString());
        mUser.setmAge(mVtxtAge.getText().toString());
        mUser.setmHeight(mVtxtHeight.getText().toString());
        mUser.setmFullname(mVtxtFullName.getText().toString());

        //set the bike weight to 10 by default, otherwise to the value provided by the user
        mUser.setmBikeWeight((mBikeWeight.getText().toString().equals("")?"10":mBikeWeight.getText().toString()));

        if (mRbMale.isChecked()){
            mUser.setmGender("MALE");
        }
        else {
            mUser.setmGender("FEMALE");
        }
        //change the difference between current weight and the weight from server
        try {
            new FitBitGetJsonTask(Oauth.getmOauth()
                    , FitBitGetJsonTask.ENDPOINT_WEIGHT, getActivity()
                    , Integer.parseInt(mEtxtWeight.getText().toString())).execute().get();
            UserSettings.getmActiveUser().setmWeight(mEtxtWeight.getText().toString());
            UserSettings.getmActiveUser().saveUser(getActivity());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if(!checkbox){
            mUser.setmFahrradTyp("Nichts ausgewählt");
            mUser.setmReifenTyp("Nichts ausgewählt");
        }

        UserSettings.saveUser(getActivity());
        //FitBitActivityScoreHandler.calcActivtiyScore(getActivity());
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

