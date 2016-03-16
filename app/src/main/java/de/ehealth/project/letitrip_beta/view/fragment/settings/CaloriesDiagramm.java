package de.ehealth.project.letitrip_beta.view.fragment.settings;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.ehealth.project.letitrip_beta.handler.database.FitBitUserDataSQLite;
import de.ehealth.project.letitrip_beta.handler.fitbit.FitBitGetJsonTask;
import de.ehealth.project.letitrip_beta.handler.fitbit.Oauth;
import de.ehealth.project.letitrip_beta.model.fitbit.Summary;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;


public class CaloriesDiagramm extends android.support.v4.app.Fragment {

    private FragmentChanger mlistener;

    private ArrayList<BarEntry> mMoveEntries = new ArrayList<>();
    ArrayList<String> mlabels = new ArrayList<String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        createDataSet();
        BarChart Chart = new BarChart(getActivity());
        BarDataSet dataset = new BarDataSet(mMoveEntries, "kcal");
        dataset.setColor(0xFF5c6bc0);
        BarData data = new BarData(mlabels, dataset);
        Chart.setDescription("");
        Chart.setData(data);

        return Chart;
    }
    public void createDataSet(){
        try {
            new FitBitGetJsonTask(Oauth.getmOauth(), FitBitGetJsonTask.ENDPOINT_MOVES,getActivity()).execute().get();
        }catch(Exception ex){ ex.printStackTrace(); }

        Calendar now = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String[] days = new String[14];
        now.add(Calendar.DAY_OF_MONTH, -14);
        for (int i = 0; i < days.length; i++) {
            days[i] = format.format(now.getTime());
            Summary sum = FitBitUserDataSQLite.getInstance(getActivity()).getFitBitData(days[i]);
            if(sum.getSteps() != "0") {
                mMoveEntries.add(new BarEntry(Integer.parseInt(sum.getCaloriesOut()), i));
                mlabels.add(days[i]);
            }
            now.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof FragmentChanger) {
            mlistener = (FragmentChanger) activity;
        } else {
            Log.d("Fitbit", "Wrong interface implemented");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mlistener = null;
    }
}