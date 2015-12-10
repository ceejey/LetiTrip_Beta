package de.ehealth.project.letitrip_beta.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.ExecutionException;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.news.NewsHandler;
import de.ehealth.project.letitrip_beta.handler.task.news.DownloadImageTask;
import de.ehealth.project.letitrip_beta.handler.task.news.NewsTask;
import de.ehealth.project.letitrip_beta.handler.task.weather.WeatherCallback;
import de.ehealth.project.letitrip_beta.handler.task.weather.WeatherService;
import de.ehealth.project.letitrip_beta.model.news.Content;
import de.ehealth.project.letitrip_beta.model.news.Image;
import de.ehealth.project.letitrip_beta.model.news.Media;
import de.ehealth.project.letitrip_beta.model.news.News;
import de.ehealth.project.letitrip_beta.model.news.Story;
import de.ehealth.project.letitrip_beta.model.weather.Channel;
import de.ehealth.project.letitrip_beta.model.weather.DescriptionMapping;
import de.ehealth.project.letitrip_beta.view.MainActivity;

public class Dashboard extends Fragment implements WeatherCallback {

    private FragmentChanger mListener;
    private LayoutInflater mInflater;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        Thread thread = new Thread() {
            @Override
            public void run() {
                NewsHandler.fillNewsFeed(view, inflater, getActivity());
            }
        };
        thread.start();

        mInflater = inflater;
        new WeatherService(this).execute("Oberhausen");

        return view;
    }

    public void success(Channel channel){
        LinearLayout placeHolder = new LinearLayout(getView().findViewById(R.id.scrollViewDashboard).getContext());
        mInflater.inflate(R.layout.weather_view, placeHolder);
        ((LinearLayout) getView().findViewById(R.id.layoutDashboard)).addView(placeHolder);


        TextView txtWeatherSubHeading = (TextView) getView().findViewById(R.id.txtWeatherSubheading);
        TextView txtWeatherTemp = (TextView) getView().findViewById(R.id.txtWeatherTemp);
        TextView txtWeatherWind = (TextView) getView().findViewById(R.id.txtWeatherWind);
        TextView txtWeatherHumidity = (TextView) getView().findViewById(R.id.txtWeatherHumidity);
        TextView txtWeatherPressure = (TextView) getView().findViewById(R.id.txtWeatherPressure);
        String description = channel.getItem().getCondition().getDescription();
        if(DescriptionMapping.getMap().containsKey(description))
            txtWeatherSubHeading.setText(DescriptionMapping.getMap().get(description));
        else
            txtWeatherSubHeading.setText(description);
        txtWeatherTemp.setText( channel.getItem().getCondition().getTemperature() + " °" + channel.getUnits().getTemperature());
        txtWeatherWind.setText( channel.getWind().getSpeed() + " " + channel.getUnits().getSpeed());
        txtWeatherHumidity.setText( channel.getAtmosphere().getHumidity() + " %");
        txtWeatherPressure.setText(channel.getAtmosphere().getPressure() + " " + channel.getUnits().getPressure());
    }

    public void failure(Exception exc){
        LinearLayout placeHolder = new LinearLayout(getView().findViewById(R.id.scrollViewDashboard).getContext());
        mInflater.inflate(R.layout.weather_view, placeHolder);
        ((LinearLayout) getView().findViewById(R.id.layoutDashboard)).addView(placeHolder);


        TextView txtWeatherSubHeading = (TextView) getView().findViewById(R.id.txtWeatherSubheading);
        TextView txtWeatherTempHeader = (TextView) getView().findViewById(R.id.txtWeatherTempHeader);
        TextView txtWeatherWindHeader = (TextView) getView().findViewById(R.id.txtWeatherWindHeader);
        TextView txtWeatherHumidityHeader = (TextView) getView().findViewById(R.id.txtWeatherHumidityHeader);
        TextView txtWeatherPressureHeader = (TextView) getView().findViewById(R.id.txtWeatherPressureHeader);
        TextView txtWeatherTemp = (TextView) getView().findViewById(R.id.txtWeatherTemp);
        TextView txtWeatherWind = (TextView) getView().findViewById(R.id.txtWeatherWind);
        TextView txtWeatherHumidity = (TextView) getView().findViewById(R.id.txtWeatherHumidity);
        TextView txtWeatherPressure = (TextView) getView().findViewById(R.id.txtWeatherPressure);

        txtWeatherWindHeader.setVisibility(View.GONE);
        txtWeatherHumidityHeader.setVisibility(View.GONE);
        txtWeatherPressureHeader.setVisibility(View.GONE);
        txtWeatherTemp.setVisibility(View.GONE);
        txtWeatherWind.setVisibility(View.GONE);
        txtWeatherHumidity.setVisibility(View.GONE);
        txtWeatherPressure.setVisibility(View.GONE);

        txtWeatherSubHeading.setText("Ooops!");
        txtWeatherTempHeader.setText("Leider konnten keine Wetterinformationen heruntergeladen werden.");
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
