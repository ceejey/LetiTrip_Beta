package de.ehealth.project.letitrip_beta.view.fragment.settings;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.view.MainActivity;
import de.ehealth.project.letitrip_beta.view.adapter.HelpExpandableListAdapter;
import de.ehealth.project.letitrip_beta.view.adapter.Parent;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;

public class Help extends Fragment {

    private FragmentChanger mListener;
    private ExpandableListView mExpandableList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_help, container, false);
        mExpandableList = (ExpandableListView)  view.findViewById(R.id.listViewHelp);

        ArrayList<Parent> arrayParents = new ArrayList<Parent>();
        ArrayList<String> arrayChildren = new ArrayList<String>();
        Parent parent = new Parent();
        parent.setTitle("Warum muss die App mit einem Fitbit Account verbunden werden?");
        arrayChildren.add("In dieser Version der App wird davon ausgegangen," +
                " dass ein FitBit Tracker verwendet wird. Um mehrfaches Eingeben der persönlichen" +
                " Daten zu vermeiden, basiert die App auf den vollständigen Daten des Fitbit Accounts des Benutzers." +
                " Bitte beachte immer dein aktuelles Gewicht, Größe, Alter sowie deinen Standort anzugeben." +
                "\nStand: 24.Februar 2016");
        parent.setArrayChildren(arrayChildren);
        //in this array we add the Parent object. We will use the arrayParents at the setAdapter
        arrayParents.add(parent);

        parent = new Parent();
        arrayChildren = new ArrayList<String>();
        parent.setTitle("Wozu muss ich meinen Standort angeben?");
        arrayChildren.add("Die App benötigt Wetterinformationen, die abhängig vom Standort sind. " +
                "Die Berechnung der erbrachten Leistung (siehe Dokumentation) wird unter anderem mit diesen " +
                "Informationen während des Trainings bestimmt.");
        parent.setArrayChildren(arrayChildren);
        //in this array we add the Parent object. We will use the arrayParents at the setAdapter
        arrayParents.add(parent);

        parent = new Parent();
        arrayChildren = new ArrayList<String>();
        parent.setTitle("Was ist der Activity Score?");
        arrayChildren.add("Der Fitbit Tracker dient zur Bestimmung des Aktivitätsgrades des Benutzters im Alltag." +
                "Der Fitbit interne Algorithmus bestimmt anhand der Benutzerinformationen, sowie der gelaufen Schritte, grob " +
                "die verbrauchten Kalorien. Die Anzahl an gelaufenen Schritten, sowie den verbrannten Kalorien werden hier pro Tag gespeichert. " +
                "Wird nun dieser alltagspezifische Activity Score berechnet, so basiert die Rechnung auf den Daten der letzten zwei Wochen. Deshalb " +
                "ist dieser Wert erst nach zwei Wochen korrekt!\nNatürlich wird der Score nur berechnet und angezeigt, wenn ein Fitbit Account verbunden ist.");
        parent.setArrayChildren(arrayChildren);
        //in this array we add the Parent object. We will use the arrayParents at the setAdapter
        arrayParents.add(parent);

        parent = new Parent();
        arrayChildren = new ArrayList<String>();
        parent.setTitle("Was ist ein idealer Activity Score?");
        arrayChildren.add("Ein idealer Activity Score liegt bei 1. Dieser wird erreicht, wenn Sie sich im Alltag ausreichend bewegen.");
        parent.setArrayChildren(arrayChildren);
        //in this array we add the Parent object. We will use the arrayParents at the setAdapter
        arrayParents.add(parent);


        parent = new Parent();
        arrayChildren = new ArrayList<String>();
        parent.setTitle("Warum ist mein Activity Score so hoch, nachdem ich diesen zurückgesetzt habe?");
        arrayChildren.add("Die App merkt sich den Zeitpunkt, an dem der Activity Score zurückgesetzt wurde. Ab diesem " +
                "Zeitpunkt fängt nun die Scoreberechnung an, was zu Ungenauigkeiten führt. Deshalb ist dieser Wert erst nach zwei " +
                "Wochen aussagekräftig");
        parent.setArrayChildren(arrayChildren);
        //in this array we add the Parent object. We will use the arrayParents at the setAdapter
        arrayParents.add(parent);

        parent = new Parent();
        arrayChildren = new ArrayList<String>();
        parent.setTitle("Wird mein mobiles Datenvolumen während einer Session verbraucht?");
        arrayChildren.add("Nein. Alle benötigten Daten werden vor Beginn der Session gesammelt. Unter Umständen wird" +
                " Datenvolumen verbraucht, wenn die Kartenansicht aufgerufen wird und der aktuelle Ort nicht im Zwischenspeicher gespeichert ist.");
        parent.setArrayChildren(arrayChildren);
        //in this array we add the Parent object. We will use the arrayParents at the setAdapter
        arrayParents.add(parent);

        parent = new Parent();
        arrayChildren = new ArrayList<String>();
        parent.setTitle("Wie starte ich eine Session?");
        arrayChildren.add("Eine Session (Workout) kann im zweiten Reiter (\"Session\") der App gestartet werden. Unter " +
                "Umständen dauert es einen Moment bis die Session beginnt. Dies ist abhängig vom GPS Empfang. Die App kann " +
                "dann jedoch minimiert werden, die Session beginnt automatisch, sobald die Position ermittelt wurde.");
        parent.setArrayChildren(arrayChildren);
        //in this array we add the Parent object. We will use the arrayParents at the setAdapter
        arrayParents.add(parent);

        parent = new Parent();
        arrayChildren = new ArrayList<String>();
        parent.setTitle("Warum wird der Watt/Kalorien-Wert während einer Session nicht berechnet?");
        arrayChildren.add("Die Watt/Kalorien-Berechnung zieht Wetterdaten mit heran. Diese müssen in aktueller Form vorhanden sein."+
                            "Die Stadt muss selbstverständlich in den Einstellungen eingetragen sein.");
        parent.setArrayChildren(arrayChildren);
        //in this array we add the Parent object. We will use the arrayParents at the setAdapter
        arrayParents.add(parent);
        //sets the adapter that provides data to the list.
        mExpandableList.setAdapter(new HelpExpandableListAdapter(getActivity(), arrayParents));
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
