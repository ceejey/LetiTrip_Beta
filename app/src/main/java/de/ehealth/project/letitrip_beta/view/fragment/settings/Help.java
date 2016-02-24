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
                " dass ein FitBit Tracker definitiv verwendet wird. Um mehrfaches Eingeben der Persöhnlichen" +
                " Daten zu vermeiden, basiert die App auf den vollständigen Daten des Benutzer Fitbit Accounts " +
                " Bitte beachte immer dein aktuelles Gewicht, sowie dein Standort anzugeben." +
                "                                                                 Stand: 24.Februar 2016");
        parent.setArrayChildren(arrayChildren);
        //in this array we add the Parent object. We will use the arrayParents at the setAdapter
        arrayParents.add(parent);

        parent = new Parent();
        arrayChildren = new ArrayList<String>();
        parent.setTitle("Wozu muss ich meinen Standort angeben");
        arrayChildren.add("Die App benötigt Wetter Informationen die abhängig vom Standort sind. " +
                "Damit die Berechnung der erbrachten Leistung (siehe Dokumentation) wärend des Trainings" +
                "zu bestimmen");
        parent.setArrayChildren(arrayChildren);
    //in this array we add the Parent object. We will use the arrayParents at the setAdapter
        arrayParents.add(parent);

        parent = new Parent();
        arrayChildren = new ArrayList<String>();
        parent.setTitle("Was bedeutet Activity Score");
        arrayChildren.add("1. Der Fitbit Tracker dient zur Bestimmung des Aktivitätsgrades des Benutzters im Alltag." +
                "Der Fitbit interne Algorithmus bestimmt anhand der Benutzerinformationen, sowie gelaufen Schritten grob" +
                "die verbrauchten Kalorien. Die Anzahl an gelaufenen Schritten, sowie die verbrannten Kalorien werden hier pro Tag gespeichert." +
                "Wird nun dieser Alltag spezifische Activity Score berechnet, so basiert die Rechnung auf den Daten der letzten zwei Wochen. Deshalb" +
                "ist dieser Wert erst nach zwei Wochen korrekt! ");
        parent.setArrayChildren(arrayChildren);
        //in this array we add the Parent object. We will use the arrayParents at the setAdapter
        arrayParents.add(parent);

        parent = new Parent();
        arrayChildren = new ArrayList<String>();
        parent.setTitle("Warum ist mein Activity Score so hoch, nach dem ich diesen Zurück gesetzt habe");
        arrayChildren.add("Die App merkt sich den Zeitpunkt an dem der Activity Score zurückgesetzt wurde. Ab diesem " +
                "Zeitpunkt fängt nun die Score berechnung an, was zu ungenauigkeiten führt. Deshalb ist dieser Werst erst nach zwei" +
                "Wochen korrekt");
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
