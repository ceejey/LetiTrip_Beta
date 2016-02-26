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
import de.ehealth.project.letitrip_beta.model.settings.UserSettings;
import de.ehealth.project.letitrip_beta.view.MainActivity;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;

/**
 * Created by Mirorn on 29.12.2015.
 */
public class NewsSettings extends Fragment {

    private FragmentChanger mListener;
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

    private boolean newsSettingsFounded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_news, container, false);

        if(UserSettings.getmActiveUser().getmNewsSettings() != null)
            mCheckedList = new ArrayList<String>(UserSettings.getmActiveUser().getmNewsSettings());
        else
            mCheckedList = new ArrayList<String>();
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

        if (cbArbeit.isChecked()) {
            currentTasks.add("arbeit");
        }
        if (cbAuto.isChecked()) {
            currentTasks.add("auto");
        }
        if (cbBanken.isChecked()) {
            currentTasks.add("banken");
        }
        if (cbBauwesen.isChecked()) {
            currentTasks.add("bauwesen");
        }
        if (cbBildung.isChecked()) {
            currentTasks.add("bildung");
        }
        if (cbCelebrities.isChecked()) {
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

        set.addAll(currentTasks);
        UserSettings.getmActiveUser().setmNewsSettings(set);
        UserSettings.saveUser(getActivity());
        updateActivity(MainActivity.FragmentName.SETTINGS);
    }

    private void checkSavedNewsSettings(){
        for(int i = 0; i < mCheckedList.size(); i++){
            if(mCheckedList.get(i).equals("arbeit")){
                cbArbeit.setChecked(!cbArbeit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("auto")) {
                cbAuto.setChecked(!cbAuto.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("banken")){
                cbBanken.setChecked(!cbBanken.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("bauwesen")){
                cbBauwesen.setChecked(!cbBauwesen.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("bildung")){
                cbBildung.setChecked(!cbBildung.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("celebrities")){
                cbCelebrities.setChecked(!cbCelebrities.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("chemie")){
                cbChemie.setChecked(!cbChemie.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("computer")){
                cbComputer.setChecked(!cbComputer.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("energie")){
                cbEnergie.setChecked(!cbEnergie.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("fernsehen")){
                cbFernsehen.setChecked(!cbFernsehen.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("fussball")){
                cbFussball.setChecked(!cbFussball.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("gesundheit")){
                cbGesundheit.setChecked(!cbGesundheit.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("handel")){
                cbHandel.setChecked(!cbHandel.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("immobilien")){
                cbImmobilien.setChecked(!cbImmobilien.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("kinder")){
                cbKinder.setChecked(!cbKinder.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("lebensmittel")){
                cbLebensmittel.setChecked(!cbLebensmittel.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("lifestyle")){
                cbLifestyle.setChecked(!cbLifestyle.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("logistik")){
                cbLogistik.setChecked(!cbLogistik.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("maschinenbau")){
                cbMaschinenbau.setChecked(!cbMaschinenbau.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("medien")){
                cbMedien.setChecked(!cbMedien.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("motorsport")){
                cbMotorsport.setChecked(!cbMotorsport.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("presseschau")){
                cbPresseschau.setChecked(!cbPresseschau.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("ratgeber")){
                cbRatgeber.setChecked(!cbRatgeber.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("recht")){
                cbRecht.setChecked(!cbRecht.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("soziales")){
                cbSoziales.setChecked(!cbSoziales.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("telekommunikation")){
                cbTelekommunikation.setChecked(!cbTelekommunikation.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("touristik")){
                cbTouristik.setChecked(!cbTouristik.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("umwelt")){
                cbUmwelt.setChecked(!cbUmwelt.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("unterhaltung")){
                cbUnterhaltung.setChecked(!cbUnterhaltung.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("versicherungen")){
                cbVersicherungen.setChecked(!cbVersicherungen.isChecked());
                newsSettingsFounded = true;
            }
            if(mCheckedList.get(i).equals("wissenschaft")){
                cbWissenschaft.setChecked(!cbWissenschaft.isChecked());
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