package de.ehealth.project.letitrip_beta.view.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.database.FitBitUserDataSQLite;
import de.ehealth.project.letitrip_beta.handler.database.RecipeDatabase;
import de.ehealth.project.letitrip_beta.handler.fitbit.FitBitActivityScoreHandler;
import de.ehealth.project.letitrip_beta.handler.fitbit.FitBitGetJsonTask;
import de.ehealth.project.letitrip_beta.handler.fitbit.Oauth;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSDatabaseHandler;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSService;
import de.ehealth.project.letitrip_beta.handler.news.DownloadImageTask;
import de.ehealth.project.letitrip_beta.handler.news.NewsHandler;
import de.ehealth.project.letitrip_beta.handler.weather.WeatherCallback;
import de.ehealth.project.letitrip_beta.handler.weather.WeatherDatabaseHandler;
import de.ehealth.project.letitrip_beta.handler.weather.WeatherService;
import de.ehealth.project.letitrip_beta.model.fitbit.ActivityScoreSuggestion;
import de.ehealth.project.letitrip_beta.model.fitbit.FitBitUserData;
import de.ehealth.project.letitrip_beta.model.fitbit.Summary;
import de.ehealth.project.letitrip_beta.model.recipe.Recipe;
import de.ehealth.project.letitrip_beta.model.recipe.RecipeWrapper;
import de.ehealth.project.letitrip_beta.model.settings.UserSettings;
import de.ehealth.project.letitrip_beta.model.weather.Channel;
import de.ehealth.project.letitrip_beta.model.weather.DescriptionMapping;
import de.ehealth.project.letitrip_beta.view.MainActivity;

public class Dashboard extends Fragment implements WeatherCallback {

    private FragmentChanger mListener;
    private LayoutInflater mInflater;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mTaskComplete = false;
    private LinearLayout gpsPlaceHolder = null;
    private LinearLayout incompleteProfile = null;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayoutDashboard);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mTaskComplete = false;
                ViewGroup viewGroup = (ViewGroup) view.findViewById(R.id.layoutDashboard);
                viewGroup.removeAllViews();

                showActivityScoreView();
                showIncompleteProfileView();
                refreshWeather();
                showRecipeSuggestion();

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
                setSessionOnDashBoard();
            }
        });

        mInflater = inflater;

        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showIncompleteProfileView();
        showActivityScoreView();
        setSessionOnDashBoard();
        refreshWeather();
        showRecipeSuggestion();
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run(){
                NewsHandler.fillNewsFeed(view, mInflater, getActivity());
            }
        });
        thread.start();
    }

    public void showActivityScoreView(){
        if (UserSettings.getmActiveUser().getmFitBitUserID() != ""){ //only show the activity score when a fitbit account is connected
            LinearLayout placeHolder = new LinearLayout(getView().findViewById(R.id.scrollViewDashboard).getContext());
            mInflater.inflate(R.layout.score_view, placeHolder);
            TextView txtActivityScore = (TextView) placeHolder.findViewById(R.id.txtHeading);
            TextView txtSuggestion = (TextView) placeHolder.findViewById(R.id.txtSuggestion);
            FitBitActivityScoreHandler.calcActivtiyScore(getActivity());
            double activityScore = (FitBitActivityScoreHandler.getmActivtiyScoreSteps() + FitBitActivityScoreHandler.getmActivtiyScoreCalories()) / 2;
            txtActivityScore.setText("Activity Score: " + new DecimalFormat("0.00").format(activityScore));
            ActivityScoreSuggestion sugg = new ActivityScoreSuggestion();
            String suggStr = sugg.getSuggestion(activityScore);
            Log.d("Suggestion", suggStr);
            txtSuggestion.setText(suggStr);
            ImageView img = (ImageView) placeHolder.findViewById(R.id.imageView2);
            img.setColorFilter(0xff757575, PorterDuff.Mode.MULTIPLY);
            placeHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateActivity(MainActivity.FragmentName.FITBIT_TRACKER_DATA);
                }
            });
            ((LinearLayout) getView().findViewById(R.id.layoutDashboard)).addView(placeHolder, 0);
        }
    }

    public void showRecipeSuggestion(){
        if (getView() != null) {


            Recipe bestMatchRecipe = new Recipe();

            try {
                new FitBitGetJsonTask(Oauth.getmOauth(), FitBitGetJsonTask.ENDPOINT_MOVE, getActivity()).execute().get();
                Summary sum = FitBitUserDataSQLite.getInstance(getActivity())
                        .getCurFitBitUserMovement().getSummary();
                Log.d("Test", sum.getActivityCalories() + " " + sum.getCaloriesBMR() + " " + sum.getCaloriesOut());

                Log.d("Recipe", "recipe started");
                RecipeDatabase recipeDb = new RecipeDatabase(getActivity());
                List<Recipe> recipeList = recipeDb.getAllRecipes();
                Log.d("Recipe", "" + recipeList.size());

                if (!recipeList.isEmpty()) {
                    boolean firstIter = true;
                    boolean foundRecipe = false;

                    Integer caloriesBurned = Integer.parseInt(sum.getActivityCalories());

                    Log.d("TEST", "Calories burned: " + caloriesBurned);

                    for (Recipe recipe : recipeList) {
                        if(firstIter) {
                            bestMatchRecipe = recipe;
                            firstIter = false;
                        }
                        Log.d("Calories: ", "" + (Integer.parseInt(recipe.getKcal()) - caloriesBurned) + "\n" + (Integer.parseInt(bestMatchRecipe.getKcal()) - caloriesBurned));
                        Integer caloriesRecipe = Integer.parseInt(recipe.getKcal());

                        if(caloriesRecipe <= caloriesBurned){
                            if(Integer.parseInt(bestMatchRecipe.getKcal()) >= caloriesBurned &&
                                    Integer.parseInt(bestMatchRecipe.getKcal()) - caloriesBurned >= caloriesBurned - caloriesRecipe){
                                bestMatchRecipe = recipe;
                                foundRecipe = true;
                            }
                            else if(caloriesRecipe >= Integer.parseInt(bestMatchRecipe.getKcal())){
                                bestMatchRecipe = recipe;
                                foundRecipe = true;
                            }
                        }
                        if(caloriesRecipe >= caloriesBurned){
                            if(Integer.parseInt(bestMatchRecipe.getKcal()) <= caloriesBurned &&
                                    caloriesBurned - Integer.parseInt(bestMatchRecipe.getKcal())  >= caloriesRecipe - caloriesBurned){
                                bestMatchRecipe = recipe;
                                foundRecipe = true;
                            }
                            else if(caloriesRecipe <= Integer.parseInt(bestMatchRecipe.getKcal())){
                                bestMatchRecipe = recipe;
                                foundRecipe = true;
                            }
                        }

                    }

                    if(foundRecipe) {
                        LinearLayout placeHolder = new LinearLayout(getView().findViewById(R.id.scrollViewDashboard).getContext());
                        mInflater.inflate(R.layout.recipe_view, placeHolder);
                        ((LinearLayout) getView().findViewById(R.id.layoutDashboard)).addView(placeHolder);

                        ImageView imgRecipe = (ImageView) placeHolder.findViewById(R.id.imgRecipe);
                        TextView txtRecipeSubHeading = (TextView) placeHolder.findViewById(R.id.txtRecipeSubheading);
                        TextView txtRecipeBody = (TextView) placeHolder.findViewById(R.id.txtRecipeBody);

                        new DownloadImageTask(imgRecipe, true).execute(bestMatchRecipe.getPic());
                        txtRecipeSubHeading.setText(bestMatchRecipe.getName());
                        String type = bestMatchRecipe.getType();
                        if (type.equals("breakfast"))
                            type = "Fr\u00fchstück";
                        else if (type.equals("lunch"))
                            type = "Mittagessen";
                        else if (type.equals("dinner"))
                            type = "Abendessen";
                        txtRecipeBody.setText(type + " mit " + bestMatchRecipe.getKcal() + " kcal");
                        final Recipe clickedRecipe = bestMatchRecipe;
                        placeHolder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                if (getActivity() instanceof FragmentChanger) {

                                    RecipeWrapper.selectedRecipe = clickedRecipe;

                                    FragmentChanger changer = (FragmentChanger) getActivity();
                                    changer.changeFragment(MainActivity.FragmentName.RECIPE_DETAIL);

                                } else {
                                    Log.e("Error", "Can't bind onClickListener for showing Recipe");
                                }

                            }

                        });
                    }
                    else{
                        LinearLayout placeHolder = new LinearLayout(getView().findViewById(R.id.scrollViewDashboard).getContext());
                        mInflater.inflate(R.layout.recipe_view, placeHolder);
                        ((LinearLayout) getView().findViewById(R.id.layoutDashboard)).addView(placeHolder);

                        ImageView imgRecipe = (ImageView) placeHolder.findViewById(R.id.imgRecipe);
                        TextView txtRecipeSubHeading = (TextView) placeHolder.findViewById(R.id.txtRecipeSubheading);
                        TextView txtRecipeBody = (TextView) placeHolder.findViewById(R.id.txtRecipeBody);
                        TextView txtInfo = (TextView) placeHolder.findViewById(R.id.txtInfo);

                        txtInfo.setVisibility(View.GONE);

                        imgRecipe.setVisibility(View.GONE);
                        txtRecipeSubHeading.setText("Ooops!");
                        txtRecipeBody.setText("Es konnten kein passender Vorschlag angezeigt werden.");
                    }
                } else {
                    LinearLayout placeHolder = new LinearLayout(getView().findViewById(R.id.scrollViewDashboard).getContext());
                    mInflater.inflate(R.layout.recipe_view, placeHolder);
                    ((LinearLayout) getView().findViewById(R.id.layoutDashboard)).addView(placeHolder);

                    ImageView imgRecipe = (ImageView) placeHolder.findViewById(R.id.imgRecipe);
                    TextView txtRecipeSubHeading = (TextView) placeHolder.findViewById(R.id.txtRecipeSubheading);
                    TextView txtRecipeBody = (TextView) placeHolder.findViewById(R.id.txtRecipeBody);
                    TextView txtInfo = (TextView) placeHolder.findViewById(R.id.txtInfo);

                    txtInfo.setVisibility(View.GONE);
                    imgRecipe.setVisibility(View.GONE);

                    txtRecipeSubHeading.setText("Ooops!");
                    txtRecipeBody.setText("Es konnten keine Rezepte gefunden werden. Aktualisieren Sie die Datenbank in den Einstellungen.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void showIncompleteProfileView(){
        if (getView() != null){
            if ((UserSettings.getmActiveUser().getmAge()=="") ||
                (UserSettings.getmActiveUser().getmCity()=="") ||
                (UserSettings.getmActiveUser().getmGender()=="")||
                (UserSettings.getmActiveUser().getmHeight()=="")||
                (UserSettings.getmActiveUser().getmWeight()==""))
            {
                    incompleteProfile = new LinearLayout(getView().findViewById(R.id.scrollViewDashboard).getContext());
                    mInflater.inflate(R.layout.incomplete_profile_view, incompleteProfile);
                    ((LinearLayout) getView().findViewById(R.id.layoutDashboard)).addView(incompleteProfile);
                    ImageView imgInfo = (ImageView) incompleteProfile.findViewById(R.id.imageView);
                    imgInfo.setColorFilter(0xff757575, PorterDuff.Mode.MULTIPLY);
                    incompleteProfile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((LinearLayout) getView().findViewById(R.id.layoutDashboard)).removeView(incompleteProfile);
                            mListener.changeFragment(MainActivity.FragmentName.SETTINGS_PROFILE);
                        }
                    });
            }
        }
    }

    public void refreshWeather(){
        //only check weather if no weather information are in the database
        Cursor res = WeatherDatabaseHandler.getInstance().getData().getLatestWeather();
        if (res.getCount() == 0){
            Log.w("dashboard", "lade wetter für "+ UserSettings.getmActiveUser().getmCity()+" von yahoo");
            new WeatherService(this).execute(UserSettings.getmActiveUser().getmCity());
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
            if (DescriptionMapping.getMap().containsKey(description.toLowerCase())){
                txtWeatherSubHeading.setText(DescriptionMapping.getMap().get(description.toLowerCase()));
            } else {
                txtWeatherSubHeading.setText(description);
            }
            txtWeatherTemp.setText(res.getInt(2)+ " °C");
            txtWeatherWind.setText(res.getInt(3) + " km/h ("+ GPSDatabaseHandler.getInstance().getData().getDirectionLetter(res.getInt(4))+")");
            txtWeatherHumidity.setText(res.getInt(5)+ " %");
            txtWeatherPressure.setText(res.getDouble(6)+ " mb");
        }
    }

    public void setSessionOnDashBoard(){
        if ((((MainActivity)getActivity()).getGps()) != null){
            if ((((MainActivity)getActivity()).getGps().getStatus() == GPSService.Status.TRACKINGSTARTED) || (((MainActivity)getActivity()).getGps().getStatus() == GPSService.Status.PAUSED)){

                if (gpsPlaceHolder != null) {
                    if (gpsPlaceHolder.getVisibility() == View.VISIBLE) {
                        ((LinearLayout) getView().findViewById(R.id.layoutDashboard)).removeView(gpsPlaceHolder);
                        gpsPlaceHolder = null;
                    }
                }
                if (gpsPlaceHolder != null){
                    ((LinearLayout) getView().findViewById(R.id.layoutDashboard)).removeView(gpsPlaceHolder);
                }
                gpsPlaceHolder = new LinearLayout(getView().findViewById(R.id.scrollViewDashboard).getContext());
                gpsPlaceHolder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.changeFragment(MainActivity.FragmentName.SESSION);
                    }
                });
                mInflater.inflate(R.layout.gps_view, gpsPlaceHolder);
                ((LinearLayout) getView().findViewById(R.id.layoutDashboard)).addView(gpsPlaceHolder,1);
                TextView txtHeading = (TextView) gpsPlaceHolder.findViewById(R.id.txtHeading);
                ImageView imgType = (ImageView) gpsPlaceHolder.findViewById(R.id.imgType);
                imgType.setColorFilter(0xff757575, PorterDuff.Mode.MULTIPLY);

                if (((MainActivity)getActivity()).getGps().getRecordingAsBicycle()==1){
                    imgType.setImageResource(R.drawable.ic_directions_bike_white_24dp);
                } else {
                    imgType.setImageResource(R.drawable.ic_directions_run_white_24dp);
                }

                txtHeading.setText("Session "+((MainActivity)getActivity()).getGps().getActiveRecordingID()+" aktiv");

            } else {
                ((LinearLayout) getView().findViewById(R.id.layoutDashboard)).removeView(gpsPlaceHolder);
                gpsPlaceHolder = null;
            }
        }
    }

    public void success(Channel channel) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH");
        Date date = new Date();

        WeatherDatabaseHandler.getInstance().getData().addData(
                dateFormat.format(date),
                channel.getItem().getCondition().getTemp(),
                channel.getWind().getSpeed(),
                channel.getWind().getDirection(),
                channel.getAtmosphere().getHumidity(),
                channel.getAtmosphere().getPressure(),
                channel.getItem().getCondition().getText()
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
            txtWeatherTempHeader.setText("Leider konnten keine Wetterinformationen heruntergeladen werden. "+exc.getMessage());
        }

        mTaskComplete = true;
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(broadcastReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver, new IntentFilter("gps-event"));
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden){
            setSessionOnDashBoard();
        }
        super.onHiddenChanged(hidden);
    }

    //Receiver to add gps status when service located the position
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int message = intent.getIntExtra("MainActivity", -1);
            if (message == 1){ //MainActivity connected to gps;tacking started/stopped
                setSessionOnDashBoard();
            }
        }
    };


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