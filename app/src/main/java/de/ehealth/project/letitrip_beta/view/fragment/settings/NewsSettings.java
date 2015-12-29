package de.ehealth.project.letitrip_beta.view.fragment.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.model.fitbit.FitbitUserProfile;
import de.ehealth.project.letitrip_beta.view.MainActivity;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;

/**
 * Created by Mirorn on 29.12.2015.
 */
public class NewsSettings extends Fragment {

    private FragmentChanger mListener;
    private CheckBox cbNewsEinschalten;
    private Button mBTSaveNewsSettings;

    private CheckBox cbArbeit;
    private CheckBox cbAuto;
    private CheckBox cbBanken;
    private CheckBox cbBauwesen;
    private CheckBox cbBildung;
    private CheckBox cbCelebrities;
    private CheckBox cbChemie;
    private CheckBox cbComputer;
    private CheckBox cbEnergie;
    private CheckBox cbFernsehen;
    private CheckBox cbFussball;
    private CheckBox cbGesundheit;
    private CheckBox cbHandel;
    private CheckBox cbImmobilien;
    private CheckBox cbKinder;
    private CheckBox cbLebensmittel;
    private CheckBox cbLifestyle;
    private CheckBox cbLogistik;
    private CheckBox cbMaschinenbau;
    private CheckBox cbMedien;
    private CheckBox cbMotorsport;
    private CheckBox cbPresseschau;
    private CheckBox cbRatgeber;
    private CheckBox cbRecht;
    private CheckBox cbSoziales;
    private CheckBox cbTelekommunikation;
    private CheckBox cbTouristik;
    private CheckBox cbUmwelt;
    private CheckBox cbUnterhaltung;
    private CheckBox cbVersicherungen;
    private CheckBox cbWissenschaft;
    private List<String> mCheckedList = null;

    private boolean checkboxNewsEinschalten = false;
    private boolean newsSettingsFounded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_news, container, false);

        mCheckedList = new ArrayList<String>(FitbitUserProfile.getmActiveUser().getmNewsSettings());

        cbNewsEinschalten = (CheckBox) view.findViewById(R.id.cbNewsEinschalten);
        mBTSaveNewsSettings = (Button) view.findViewById(R.id.bnSpeichernNews);

        cbArbeit = (CheckBox) view.findViewById(R.id.cbArbeit);
        cbAuto = (CheckBox) view.findViewById(R.id.cbAuto);
        cbBanken = (CheckBox) view.findViewById(R.id.cbBanken);
        cbBauwesen = (CheckBox) view.findViewById(R.id.cbBauwesen);
        cbBildung = (CheckBox) view.findViewById(R.id.cbBildung);
        cbCelebrities = (CheckBox) view.findViewById(R.id.cbCelebrities);
        cbChemie = (CheckBox) view.findViewById(R.id.cbChemie);
        cbComputer = (CheckBox) view.findViewById(R.id.cbComputer);
        cbEnergie = (CheckBox) view.findViewById(R.id.cbEnergie);
        cbFernsehen = (CheckBox) view.findViewById(R.id.cbFernsehen);
        cbFussball = (CheckBox) view.findViewById(R.id.cbFussball);
        cbGesundheit = (CheckBox) view.findViewById(R.id.cbGesundheit);
        cbHandel = (CheckBox) view.findViewById(R.id.cbHandel);
        cbImmobilien = (CheckBox) view.findViewById(R.id.cbImmobilien);
        cbKinder = (CheckBox) view.findViewById(R.id.cbKinder);
        cbLebensmittel = (CheckBox) view.findViewById(R.id.cbLebensmittel);
        cbLifestyle = (CheckBox) view.findViewById(R.id.cbLifestyle);
        cbLogistik = (CheckBox) view.findViewById(R.id.cbLogistik);
        cbMaschinenbau = (CheckBox) view.findViewById(R.id.cbMaschinenbau);
        cbMedien = (CheckBox) view.findViewById(R.id.cbMedien);
        cbMotorsport = (CheckBox) view.findViewById(R.id.cbMotorsport);
        cbPresseschau = (CheckBox) view.findViewById(R.id.cbPresseschau);
        cbRatgeber = (CheckBox) view.findViewById(R.id.cbRatgeber);
        cbRecht = (CheckBox) view.findViewById(R.id.cbRecht);
        cbSoziales = (CheckBox) view.findViewById(R.id.cbSoziales);
        cbTelekommunikation = (CheckBox) view.findViewById(R.id.cbTelekommunikation);
        cbTouristik = (CheckBox) view.findViewById(R.id.cbTouristik);
        cbUmwelt = (CheckBox) view.findViewById(R.id.cbUmwelt);
        cbUnterhaltung = (CheckBox) view.findViewById(R.id.cbUnterhaltung);
        cbVersicherungen = (CheckBox) view.findViewById(R.id.cbVersicherungen);
        cbWissenschaft = (CheckBox) view.findViewById(R.id.cbWissenschaft);
        checkSavedNewsSettings();
        if(!newsSettingsFounded) {
            cbArbeit.setVisibility(view.INVISIBLE);
            cbAuto.setVisibility(view.INVISIBLE);
            cbBanken.setVisibility(view.INVISIBLE);
            cbBauwesen.setVisibility(view.INVISIBLE);
            cbBildung.setVisibility(view.INVISIBLE);
            cbCelebrities.setVisibility(view.INVISIBLE);
            cbChemie.setVisibility(view.INVISIBLE);
            cbComputer.setVisibility(view.INVISIBLE);
            cbEnergie.setVisibility(view.INVISIBLE);
            cbFernsehen.setVisibility(view.INVISIBLE);
            cbFussball.setVisibility(view.INVISIBLE);
            cbGesundheit.setVisibility(view.INVISIBLE);
            cbHandel.setVisibility(view.INVISIBLE);
            cbImmobilien.setVisibility(view.INVISIBLE);
            cbKinder.setVisibility(view.INVISIBLE);
            cbLebensmittel.setVisibility(view.INVISIBLE);
            cbLifestyle.setVisibility(view.INVISIBLE);
            cbLogistik.setVisibility(view.INVISIBLE);
            cbMaschinenbau.setVisibility(view.INVISIBLE);
            cbMedien.setVisibility(view.INVISIBLE);
            cbMotorsport.setVisibility(view.INVISIBLE);
            cbPresseschau.setVisibility(view.INVISIBLE);
            cbRatgeber.setVisibility(view.INVISIBLE);
            cbRecht.setVisibility(view.INVISIBLE);
            cbSoziales.setVisibility(view.INVISIBLE);
            cbTelekommunikation.setVisibility(view.INVISIBLE);
            cbTouristik.setVisibility(view.INVISIBLE);
            cbUmwelt.setVisibility(view.INVISIBLE);
            cbUnterhaltung.setVisibility(view.INVISIBLE);
            cbVersicherungen.setVisibility(view.INVISIBLE);
            cbWissenschaft.setVisibility(view.INVISIBLE);
            checkboxNewsEinschalten = false;
        }
        else {
            cbNewsEinschalten.setChecked(!cbNewsEinschalten.isChecked());
            checkboxNewsEinschalten = true;
        }
        cbNewsEinschalten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkboxNewsEinschalten) {
                    cbArbeit.setVisibility(v.VISIBLE);
                    cbAuto.setVisibility(v.VISIBLE);
                    cbBanken.setVisibility(v.VISIBLE);
                    cbBauwesen.setVisibility(v.VISIBLE);
                    cbBildung.setVisibility(v.VISIBLE);
                    cbCelebrities.setVisibility(v.VISIBLE);
                    cbChemie.setVisibility(v.VISIBLE);
                    cbComputer.setVisibility(v.VISIBLE);
                    cbEnergie.setVisibility(v.VISIBLE);
                    cbFernsehen.setVisibility(v.VISIBLE);
                    cbFussball.setVisibility(v.VISIBLE);
                    cbGesundheit.setVisibility(v.VISIBLE);
                    cbHandel.setVisibility(v.VISIBLE);
                    cbImmobilien.setVisibility(v.VISIBLE);
                    cbKinder.setVisibility(v.VISIBLE);
                    cbLebensmittel.setVisibility(v.VISIBLE);
                    cbLifestyle.setVisibility(v.VISIBLE);
                    cbLogistik.setVisibility(v.VISIBLE);
                    cbMaschinenbau.setVisibility(v.VISIBLE);
                    cbMedien.setVisibility(v.VISIBLE);
                    cbMotorsport.setVisibility(v.VISIBLE);
                    cbPresseschau.setVisibility(v.VISIBLE);
                    cbRatgeber.setVisibility(v.VISIBLE);
                    cbRecht.setVisibility(v.VISIBLE);
                    cbSoziales.setVisibility(v.VISIBLE);
                    cbTelekommunikation.setVisibility(v.VISIBLE);
                    cbTouristik.setVisibility(v.VISIBLE);
                    cbUmwelt.setVisibility(v.VISIBLE);
                    cbUnterhaltung.setVisibility(v.VISIBLE);
                    cbVersicherungen.setVisibility(v.VISIBLE);
                    cbWissenschaft.setVisibility(v.VISIBLE);

                    checkboxNewsEinschalten = true;
                } else {
                    cbArbeit.setVisibility(v.INVISIBLE);
                    cbAuto.setVisibility(v.INVISIBLE);
                    cbBanken.setVisibility(v.INVISIBLE);
                    cbBauwesen.setVisibility(v.INVISIBLE);
                    cbBildung.setVisibility(v.INVISIBLE);
                    cbCelebrities.setVisibility(v.INVISIBLE);
                    cbChemie.setVisibility(v.INVISIBLE);
                    cbComputer.setVisibility(v.INVISIBLE);
                    cbEnergie.setVisibility(v.INVISIBLE);
                    cbFernsehen.setVisibility(v.INVISIBLE);
                    cbFussball.setVisibility(v.INVISIBLE);
                    cbGesundheit.setVisibility(v.INVISIBLE);
                    cbHandel.setVisibility(v.INVISIBLE);
                    cbImmobilien.setVisibility(v.INVISIBLE);
                    cbKinder.setVisibility(v.INVISIBLE);
                    cbLebensmittel.setVisibility(v.INVISIBLE);
                    cbLifestyle.setVisibility(v.INVISIBLE);
                    cbLogistik.setVisibility(v.INVISIBLE);
                    cbMaschinenbau.setVisibility(v.INVISIBLE);
                    cbMedien.setVisibility(v.INVISIBLE);
                    cbMotorsport.setVisibility(v.INVISIBLE);
                    cbPresseschau.setVisibility(v.INVISIBLE);
                    cbRatgeber.setVisibility(v.INVISIBLE);
                    cbRecht.setVisibility(v.INVISIBLE);
                    cbSoziales.setVisibility(v.INVISIBLE);
                    cbTelekommunikation.setVisibility(v.INVISIBLE);
                    cbTouristik.setVisibility(v.INVISIBLE);
                    cbUmwelt.setVisibility(v.INVISIBLE);
                    cbUnterhaltung.setVisibility(v.INVISIBLE);
                    cbVersicherungen.setVisibility(v.INVISIBLE);
                    cbWissenschaft.setVisibility(v.INVISIBLE);

                    checkboxNewsEinschalten = false;
                }
            }
        });
        mBTSaveNewsSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNesSettings();
            }
        });
        // Inflate the layout for this fragment
        return view;
    }
    private void saveNesSettings(){
        ArrayList currentTasks = new ArrayList<String>();
        Set<String> set = new HashSet<String>();
        if(!checkboxNewsEinschalten) {
            currentTasks.add("Nicht Ausgewählt");
        }
        else {
            if (cbArbeit.isChecked()) {
                currentTasks.add("arbeit");
            }
            if (cbAuto.isChecked()) {
                currentTasks.add("auto");
            }
            if (cbBanken.isChecked()) {
                currentTasks.add("banken");
            }
            if (cbBildung.isChecked()) {
                currentTasks.add("bauwesen");
            }
            if (cbCelebrities.isChecked()) {
                currentTasks.add("bildung");
            }
            if (cbChemie.isChecked()) {
                currentTasks.add("celebrities");
            }
            if (cbChemie.isChecked()) {
                currentTasks.add("chemie");
            }
            if (cbComputer.isChecked()) {
                currentTasks.add("computer");
            }
            if (cbEnergie.isChecked()) {
                currentTasks.add("energie");
            }
            if (cbFernsehen.isChecked()) {
                currentTasks.add("fernsehen");
            }
            if (cbFussball.isChecked()) {
                currentTasks.add("fussball");
            }
            if (cbGesundheit.isChecked()) {
                currentTasks.add("gesundheit");
            }
            if (cbHandel.isChecked()) {
                currentTasks.add("handel");
            }
            if (cbImmobilien.isChecked()) {
                currentTasks.add("immobilien");
            }
            if (cbKinder.isChecked()) {
                currentTasks.add("kinder");
            }
            if (cbLebensmittel.isChecked()) {
                currentTasks.add("lebensmittel");
            }
            if (cbLifestyle.isChecked()) {
                currentTasks.add("lifestyle");
            }
            if (cbLogistik.isChecked()) {
                currentTasks.add("logistik");
            }
            if (cbMaschinenbau.isChecked()) {
                currentTasks.add("maschinenbau");
            }
            if (cbMedien.isChecked()) {
                currentTasks.add("medien");
            }
            if (cbMotorsport.isChecked()) {
                currentTasks.add("motorsport");
            }
            if (cbPresseschau.isChecked()) {
                currentTasks.add("presseschau");
            }
            if (cbRatgeber.isChecked()) {
                currentTasks.add("ratgeber");
            }
            if (cbRecht.isChecked()) {
                currentTasks.add("recht");
            }
            if (cbSoziales.isChecked()) {
                currentTasks.add("soziales");
            }
            if (cbTelekommunikation.isChecked()) {
                currentTasks.add("telekommunikation");
            }
            if (cbTouristik.isChecked()) {
                currentTasks.add("touristik");
            }
            if (cbUmwelt.isChecked()) {
                currentTasks.add("umwelt");
            }
            if (cbUnterhaltung.isChecked()) {
                currentTasks.add("unterhaltung");
            }
            if (cbVersicherungen.isChecked()) {
                currentTasks.add("versicherungen");
            }
            if (cbWissenschaft.isChecked()) {
                currentTasks.add("wissenschaft");
            }
        }



        set.addAll(currentTasks);
        FitbitUserProfile.getmActiveUser().setmNewsSettings(set);
        FitbitUserProfile.saveUser(getActivity());
    }

    private void checkSavedNewsSettings(){
        for(int i = 0; i < mCheckedList.size(); i++){
            if(mCheckedList.get(i).equals("arbeit")){
                cbArbeit.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("auto")) {
                cbAuto.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("banken")){
                cbBanken.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("bauwesen")){
                cbBauwesen.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("bildung")){
                cbBildung.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("celebrities")){
                cbCelebrities.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("chemie")){
                cbChemie.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("computer")){
                cbComputer.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("energie")){
                cbEnergie.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("fernsehen")){
                cbFernsehen.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("fussball")){
                cbFussball.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("gesundheit")){
                cbGesundheit.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("handel")){
                cbHandel.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("immobilien")){
                cbImmobilien.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("kinder")){
                cbKinder.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("lebensmittel")){
                cbLebensmittel.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("lifestyle")){
                cbLifestyle.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("logistik")){
                cbLogistik.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("maschinenbau")){
                cbMaschinenbau.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("medien")){
                cbMedien.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("motorsport")){
                cbMotorsport.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("presseschau")){
                cbPresseschau.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("ratgeber")){
                cbRatgeber.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("recht")){
                cbRecht.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("soziales")){
                cbSoziales.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("telekommunikation")){
                cbTelekommunikation.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("touristik")){
                cbTouristik.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("umwelt")){
                cbUmwelt.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("unterhaltung")){
                cbUnterhaltung.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("versicherungen")){
                cbVersicherungen.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("wissenschaft")){
                cbWissenschaft.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
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