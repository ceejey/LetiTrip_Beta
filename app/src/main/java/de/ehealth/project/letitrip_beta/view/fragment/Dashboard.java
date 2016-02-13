package de.ehealth.project.letitrip_beta.view.fragment;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.fitbit.Oauth;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSDatabaseHandler;
import de.ehealth.project.letitrip_beta.handler.news.NewsHandler;
import de.ehealth.project.letitrip_beta.handler.weather.WeatherCallback;
import de.ehealth.project.letitrip_beta.handler.weather.WeatherDatabaseHandler;
import de.ehealth.project.letitrip_beta.handler.weather.WeatherService;
import de.ehealth.project.letitrip_beta.model.fitbit.FitBitAPI;
import de.ehealth.project.letitrip_beta.model.fitbit.FitbitUserProfile;
import de.ehealth.project.letitrip_beta.model.weather.Channel;
import de.ehealth.project.letitrip_beta.model.weather.DescriptionMapping;
import de.ehealth.project.letitrip_beta.view.MainActivity;

public class Dashboard extends Fragment implements WeatherCallback {

    private FragmentChanger mListener;
    private LayoutInflater mInflater;
    private WeatherCallback mWeatherCallbackFragment;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mTaskComplete = false;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mWeatherCallbackFragment = this;

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayoutDashboard);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mTaskComplete = false;
                ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.layoutDashboard);
                viewGroup.removeAllViews();

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        NewsHandler.fillNewsFeed(view, inflater, getActivity());

                        while (true) {
                            try {
                                Thread.sleep(100);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (NewsHandler.ismTaskComplete() && mTaskComplete) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mSwipeRefreshLayout.setRefreshing(false);
                                        mTaskComplete = false;
                                    }
                                });
                            }
                        }
                    }
                };
                thread.start();

                refreshWeather();
            }
        });

        Thread thread = new Thread() {
            @Override
            public void run() {
                NewsHandler.fillNewsFeed(view, inflater, getActivity());
            }
        };
        thread.start();

        mInflater = inflater;

        refreshWeather();

        //init for fitbit connection
        Oauth.getmOauth().initOauth("3444e1985fcecca0dd97ff85e4253c45", "e4263b0e379b61c4916e4427d594f5c2", "http://www.google.de", FitBitAPI.class);
        FitbitUserProfile.loadUser(getActivity());

        return view;
    }
    public void refreshWeather(){
        //only check weather if no weather information are in the database
        Cursor res = WeatherDatabaseHandler.getInstance().getData().getLatestWeather();
        if (res.getCount() == 0){
            Log.w("dashboard", "lade wetter von yahoo");
            new WeatherService(this).execute("Oberhausen");
        } else {
            Log.w("dashboard", "wetter schon vorhanden! lade aus DB");
            showWeather(res);
        }
        res.close();
        mTaskComplete = true;
    }

    public void showWeather(Cursor res){
        if (getView() != null) { //if the fragment gets changed before the task complete, the view becomes a null object reference.

            LinearLayout placeHolder = new LinearLayout(getView().findViewById(R.id.scrollViewDashboard).getContext());
            mInflater.inflate(R.layout.weather_view, placeHolder);
            ((LinearLayout) getView().findViewById(R.id.layoutDashboard)).addView(placeHolder);

            TextView txtWeatherSubHeading = (TextView) placeHolder.findViewById(R.id.txtWeatherSubheading);
            TextView txtWeatherTemp = (TextView) placeHolder.findViewById(R.id.txtWeatherTemp);
            TextView txtWeatherWind = (TextView) placeHolder.findViewById(R.id.txtWeatherWind);
            TextView txtWeatherHumidity = (TextView) placeHolder.findViewById(R.id.txtWeatherHumidity);
            TextView txtWeatherPressure = (TextView) placeHolder.findViewById(R.id.txtWeatherPressure);

            res.moveToFirst();
            String description = res.getString(7);
            //channel.getItem().getCondition().getDescription();
            if (DescriptionMapping.getMap().containsKey(description)){
                txtWeatherSubHeading.setText(DescriptionMapping.getMap().get(description));
            } else {
                txtWeatherSubHeading.setText(description);
            }
            //channel.getItem().getCondition().getTemperature()
            txtWeatherTemp.setText(res.getInt(2)+ " °C");
            //channel.getWind().getSpeed()
            txtWeatherWind.setText(res.getInt(3) + " km/h ("+ GPSDatabaseHandler.getInstance().getData().getDirectionLetter(res.getInt(4))+")");
            //channel.getAtmosphere().getHumidity()
            txtWeatherHumidity.setText(res.getInt(5)+ " %");
            //channel.getAtmosphere().getPressure()
            txtWeatherPressure.setText(res.getDouble(6)+ " mb");
        }
    }

    public void success(Channel channel) {
        Log.w("weather", "success");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH");
        Date date = new Date();

        WeatherDatabaseHandler.getInstance().getData().addData(
                dateFormat.format(date),
                channel.getItem().getCondition().getTemperature(),
                channel.getWind().getSpeed(),
                channel.getWind().getDirection(),
                channel.getAtmosphere().getHumidity(),
                channel.getAtmosphere().getPressure(),
                channel.getItem().getCondition().getDescription()
        );
        Cursor res = WeatherDatabaseHandler.getInstance().getData().getLatestWeather();
        showWeather(res);
        res.close();
    }

    public void failure(Exception exc) {
        if (getView() != null) { //if the fragment gets changed before the task complete, the view becomes a null object reference.
            LinearLayout placeHolder = new LinearLayout(getView().findViewById(R.id.scrollViewDashboard).getContext());
            mInflater.inflate(R.layout.weather_view, placeHolder);
            ((LinearLayout) getView().findViewById(R.id.layoutDashboard)).addView(placeHolder);


            TextView txtWeatherSubHeading = (TextView) placeHolder.findViewById(R.id.txtWeatherSubheading);
            TextView txtWeatherTempHeader = (TextView) placeHolder.findViewById(R.id.txtWeatherTempHeader);
            TextView txtWeatherWindHeader = (TextView) placeHolder.findViewById(R.id.txtWeatherWindHeader);
            TextView txtWeatherHumidityHeader = (TextView) placeHolder.findViewById(R.id.txtWeatherHumidityHeader);
            TextView txtWeatherPressureHeader = (TextView) placeHolder.findViewById(R.id.txtWeatherPressureHeader);
            TextView txtWeatherTemp = (TextView) placeHolder.findViewById(R.id.txtWeatherTemp);
            TextView txtWeatherWind = (TextView) placeHolder.findViewById(R.id.txtWeatherWind);
            TextView txtWeatherHumidity = (TextView) placeHolder.findViewById(R.id.txtWeatherHumidity);
            TextView txtWeatherPressure = (TextView) placeHolder.findViewById(R.id.txtWeatherPressure);
            LinearLayout layoutWeatherRight = (LinearLayout) placeHolder.findViewById(R.id.layoutWeatherRight);

            txtWeatherWindHeader.setVisibility(View.GONE);
            txtWeatherHumidityHeader.setVisibility(View.GONE);
            txtWeatherPressureHeader.setVisibility(View.GONE);
            txtWeatherTemp.setVisibility(View.GONE);
            txtWeatherWind.setVisibility(View.GONE);
            txtWeatherHumidity.setVisibility(View.GONE);
            txtWeatherPressure.setVisibility(View.GONE);
            layoutWeatherRight.setVisibility(View.GONE);

            txtWeatherSubHeading.setText("Ooops!");
            txtWeatherTempHeader.setText("Leider konnten keine Wetterinformationen heruntergeladen werden.");
        }

        mTaskComplete = true;
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


/*
        if (getView() != null) { //if the fragment gets changed before the task complete, the view becomes a null object reference.

            LinearLayout placeHolder = new LinearLayout(getView().findViewById(R.id.scrollViewDashboard).getContext());
            mInflater.inflate(R.layout.weather_view, placeHolder);
            ((LinearLayout) getView().findViewById(R.id.layoutDashboard)).addView(placeHolder);


            TextView txtWeatherSubHeading = (TextView) placeHolder.findViewById(R.id.txtWeatherSubheading);
            TextView txtWeatherTemp = (TextView) placeHolder.findViewById(R.id.txtWeatherTemp);
            TextView txtWeatherWind = (TextView) placeHolder.findViewById(R.id.txtWeatherWind);
            TextView txtWeatherHumidity = (TextView) placeHolder.findViewById(R.id.txtWeatherHumidity);
            TextView txtWeatherPressure = (TextView) placeHolder.findViewById(R.id.txtWeatherPressure);
            String description = channel.getItem().getCondition().getDescription();
            if (DescriptionMapping.getMap().containsKey(description))
                txtWeatherSubHeading.setText(DescriptionMapping.getMap().get(description));
            else
                txtWeatherSubHeading.setText(description);
            txtWeatherTemp.setText(channel.getItem().getCondition().getTemperature() + " °" + channel.getUnits().getTemperature());
            txtWeatherWind.setText(channel.getWind().getSpeed() + " " + channel.getUnits().getSpeed());
            txtWeatherHumidity.setText(channel.getAtmosphere().getHumidity() + " %");
            txtWeatherPressure.setText(channel.getAtmosphere().getPressure() + " " + channel.getUnits().getPressure());
        }*/