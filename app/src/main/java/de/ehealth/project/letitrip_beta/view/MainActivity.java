package de.ehealth.project.letitrip_beta.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.database.GPSDatabase;
import de.ehealth.project.letitrip_beta.handler.database.WeatherDatabase;
import de.ehealth.project.letitrip_beta.handler.fitbit.Oauth;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSDatabaseHandler;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSService;
import de.ehealth.project.letitrip_beta.handler.weather.WeatherDatabaseHandler;
import de.ehealth.project.letitrip_beta.model.fitbit.FitBitAPI;
import de.ehealth.project.letitrip_beta.model.settings.UserSettings;
import de.ehealth.project.letitrip_beta.view.fragment.Bar;
import de.ehealth.project.letitrip_beta.view.fragment.Dashboard;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;
import de.ehealth.project.letitrip_beta.view.fragment.Header;
import de.ehealth.project.letitrip_beta.view.fragment.News;
import de.ehealth.project.letitrip_beta.view.fragment.RecipeDetail;
import de.ehealth.project.letitrip_beta.view.fragment.RecipeFragment;
import de.ehealth.project.letitrip_beta.view.fragment.Session;
import de.ehealth.project.letitrip_beta.view.fragment.SessionDetail;
import de.ehealth.project.letitrip_beta.view.fragment.SessionOverview;
import de.ehealth.project.letitrip_beta.view.fragment.fitbit.FitBitInit;
import de.ehealth.project.letitrip_beta.view.fragment.fitbit.WebviewOauth;
import de.ehealth.project.letitrip_beta.view.fragment.settings.Device;
import de.ehealth.project.letitrip_beta.view.fragment.settings.FitBitTrackerData;
import de.ehealth.project.letitrip_beta.view.fragment.settings.General;
import de.ehealth.project.letitrip_beta.view.fragment.settings.Help;
import de.ehealth.project.letitrip_beta.view.fragment.settings.NewsSettings;
import de.ehealth.project.letitrip_beta.view.fragment.settings.Polar;
import de.ehealth.project.letitrip_beta.view.fragment.settings.Profile;
import de.ehealth.project.letitrip_beta.view.fragment.settings.Settings;

/**
 * This activity manages the fragments inside it and the navigation throught them.
 */
public class MainActivity extends FragmentActivity implements FragmentChanger{

    Fragment mFragmentHeader;
    Fragment mFragmentCaption;
    Fragment mFragmentContent;

    String mOldTag = "";

    public GPSService getGps() {
        return gps;
    }

    public boolean isBound() {
        return bound;
    }
    private GPSService gps;

    boolean bound = false;
    public ServiceConnection mConnection;
    private boolean notificationClicked = false;

    //for firstRun check
    private SharedPreferences sharedPreferences = null;

    public static enum FragmentName{
        DASHBOARD, SESSION_OVERVIEW, SESSION_DETAIL, SESSION, RECIPE, RECIPE_DETAIL, SETTINGS, SETTINGS_GENERAL,
        SETTINGS_PROFILE, SETTINGS_DEVICE, SETTINGS_HELP, WEB_VIEW_OAUTH, FIT_BIT_INIT,
        NEWS, POLAR_DEVICE, NEWS_SETTINGS, FITBIT_TRACKER_DATA;
    }

    /**
     * sends a broadcast with given parameters
     * @param msg
     * @param ID
     */
    public void sendBroadcast(String msg, int ID){
        Intent intent = new Intent("gps-event").putExtra(msg, ID);;
        LocalBroadcastManager.getInstance(this.getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    protected void onStart() {
        bindToService();
        super.onStart();
    }

    /**
     * removes the binding from the gps ervice
     */
    public void unbindFromService(){
        getApplicationContext().unbindService(mConnection);
        bound = false;
    }

    /**
     * stops the gps service (if unbound)
     */
    public void stopService(){
        stopService(new Intent(this, GPSService.class));
    }

    public void bindToService() {
        Intent i = new Intent(this, GPSService.class);
        this.getApplicationContext().bindService(i, mConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Init the GPS/Weather database, the Oauth authentication and shows the dashboard
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Oauth.getOauth().initOauth("3444e1985fcecca0dd97ff85e4253c45", "e4263b0e379b61c4916e4427d594f5c2", "http://www.google.de", FitBitAPI.class);
        UserSettings.loadUser(this);

        GPSDatabaseHandler.getInstance().setData(new GPSDatabase(this));
        WeatherDatabaseHandler.getInstance().setData(new WeatherDatabase(this));

        sharedPreferences = getSharedPreferences("de.ehealth.project.letitrip_beta", MODE_PRIVATE);

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName className, IBinder service) {
                // We've bound to LocalService, cast the IBinder and get LocalService instance
                GPSService.LocalBinder binder = (GPSService.LocalBinder) service;
                gps = binder.getService();
                bound = true;
                sendBroadcast("MainActivity", 1);
                if (notificationClicked) {
                    notificationClicked = false;
                    changeFragment(FragmentName.SESSION);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName arg0) {
                bound = false;
            }
        };

        mFragmentContent = new Dashboard();

        //intent has this extra if user clicks the session notification; open "session" instead
        if (getIntent().hasExtra("notification")){
            int extra = getIntent().getIntExtra("notification",-1);
            if (extra == 123){
                notificationClicked = true;
                getIntent().removeExtra("notification");
            }
        }

        mFragmentCaption = new Bar();
        mFragmentHeader = new Header();

        getSupportFragmentManager().beginTransaction().replace(R.id.headerContainer, mFragmentHeader).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.captionContainer, mFragmentCaption).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.contentContainer, mFragmentContent, "dashboard").commit();

        mOldTag = "dashboard";
    }

    @Override
    protected void onStop() {
        unbindFromService();
        super.onStop();
    }

    /**
     * This method gets called from the child fragment with the fragment tag which shall be showed now and
     * save the order for back navigation.
     * @param fn
     */
    @Override
    public void changeFragment(FragmentName fn) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        int expectedEntryCount;
        String expectedEntry;
        List<String> refillEntrys = new ArrayList<>();

        Fragment fragmentContent = new Dashboard();
        TextView txtHeader = (TextView) mFragmentHeader.getView().findViewById(R.id.txtHeader);

        String newTag = "";

        boolean alreadyAdded = false;

        switch (fn) {
            case DASHBOARD: //Only initializes one time
                txtHeader.setText("Startseite");
                newTag = "dashboard";

                //Remove back stack inclusive dashboard with flag = 1
                fragmentManager.popBackStackImmediate(newTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                //Check whether the fragment was initialized earlier and show the current state again
                fragmentContent = fragmentManager.findFragmentByTag(newTag);

                if (fragmentContent != null)
                    alreadyAdded = true;
                else
                    fragmentContent = new Dashboard();
                break;
            case SESSION_OVERVIEW: //Only initializes one time
                txtHeader.setText("Session");
                newTag = "session_overview";

                expectedEntryCount = 1;
                expectedEntry = "dashboard";
                refillEntrys.add("dashboard");
                fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);

                fragmentContent = new SessionOverview();
                break;
            case SESSION_DETAIL: //Initializes everytime again with new session id
                txtHeader.setText("Session Details");
                newTag = "session_details";

                expectedEntryCount = 1;
                expectedEntry = "dashboard";
                refillEntrys.add("dashboard");
                fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);

                fragmentContent = new SessionDetail();
                break;
            case SESSION:
                txtHeader.setText("Session");
                newTag = "session";

                expectedEntryCount = 1;
                expectedEntry = "dashboard";
                refillEntrys.add("dashboard");
                fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);

                fragmentContent = new Session();
                break;
            case RECIPE:
                txtHeader.setText("Rezepte");
                newTag = "recipe";

                expectedEntryCount = 1;
                expectedEntry = "dashboard";
                refillEntrys.add("dashboard");
                fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);

                fragmentContent = fragmentManager.findFragmentByTag(newTag);
                if (fragmentContent != null)
                    alreadyAdded = true;
                else
                    fragmentContent = new RecipeFragment();
                break;
            case RECIPE_DETAIL:
                txtHeader.setText("Rezepte");
                newTag = "recipe_detail";

                fragmentContent = fragmentManager.findFragmentByTag("recipe");
                if (fragmentContent != null) {
                    expectedEntryCount = 2;
                    expectedEntry = "recipe";
                    refillEntrys.add("dashboard");
                    refillEntrys.add("recipe");
                    fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);
                }
                else{
                    expectedEntryCount = 1;
                    expectedEntry = "dashboard";
                    refillEntrys.add("dashboard");
                    fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);
                }


                fragmentContent = new RecipeDetail();
                break;
            case SETTINGS:
                txtHeader.setText("Einstellungen");
                newTag = "settings";

                expectedEntryCount = 1;
                expectedEntry = "dashboard";
                refillEntrys.add("dashboard");
                fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);

                fragmentContent = new Settings();
                break;
            case SETTINGS_GENERAL:
                txtHeader.setText("Einstellungen");
                newTag = "settings_general";

                expectedEntryCount = 2;
                expectedEntry = "settings";
                refillEntrys.add("dashboard");
                refillEntrys.add("settings");

                fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);

                fragmentContent = new General();
                break;
            case SETTINGS_PROFILE:
                txtHeader.setText("Einstellungen");
                newTag = "settings_profile";

                fragmentContent = fragmentManager.findFragmentByTag("settings");
                if (fragmentContent != null) {
                    expectedEntryCount = 2;
                    expectedEntry = "setings";
                    refillEntrys.add("dashboard");
                    refillEntrys.add("settings");
                    fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);
                }
                else{
                    expectedEntryCount = 1;
                    expectedEntry = "dashboard";
                    refillEntrys.add("dashboard");
                    fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);
                }


                fragmentContent = new Profile();
                break;
            case SETTINGS_DEVICE:
                txtHeader.setText("Einstellungen");
                newTag = "settings_device";

                expectedEntryCount = 2;
                expectedEntry = "settings";
                refillEntrys.add("dashboard");
                refillEntrys.add("settings");

                fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);

                fragmentContent = new Device();
                break;
            case SETTINGS_HELP:
                txtHeader.setText("Einstellungen");
                newTag = "settings_help";

                expectedEntryCount = 2;
                expectedEntry = "settings";
                refillEntrys.add("dashboard");
                refillEntrys.add("settings");

                fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);

                fragmentContent = new Help();
                break;
            case WEB_VIEW_OAUTH:
                txtHeader.setText("Einstellungen");
                newTag = "settings_webview";

                expectedEntryCount = 2;
                expectedEntry = "settings_device";
                refillEntrys.add("dashboard");
                refillEntrys.add("settings");
                refillEntrys.add("settings_device");

                fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);

                fragmentContent = new WebviewOauth();
                break;
            case FIT_BIT_INIT:
                txtHeader.setText("Einstellungen");
                newTag = "settings_fitbit";

                expectedEntryCount = 3;
                expectedEntry = "settings_device";

                refillEntrys.add("dashboard");
                refillEntrys.add("settings");
                refillEntrys.add("settings_device");


                fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);

                fragmentContent = new FitBitInit();
                break;
            case NEWS:
                txtHeader.setText("Nachrichten");
                newTag = "news";

                expectedEntryCount = 1;
                expectedEntry = "dashboard";
                refillEntrys.add("dashboard");
                fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);

                fragmentContent = new News();
                break;
            case POLAR_DEVICE:
                txtHeader.setText("Einstellungen");
                newTag = "settings_polar";

                expectedEntryCount = 3;
                expectedEntry = "settings_device";
                refillEntrys.add("dashboard");
                refillEntrys.add("settings");
                refillEntrys.add("settings_device");

                fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);

                fragmentContent = new Polar();
                break;
            case NEWS_SETTINGS:
                txtHeader.setText("Einstellungen");
                newTag = "settings_news";

                expectedEntryCount = 2;
                expectedEntry = "settings";
                refillEntrys.add("dashboard");
                refillEntrys.add("settings");

                fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);

                fragmentContent = new NewsSettings();
                break;
            case FITBIT_TRACKER_DATA:
                txtHeader.setText("Leistungsübersicht");
                newTag = "tracker_data";

                expectedEntryCount = 1;
                expectedEntry = "dashboard";
                refillEntrys.add("dashboard");

                fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);

                fragmentContent = new FitBitTrackerData();
                break;
            default:
                break;
        }

        if(alreadyAdded){
            if(fragmentManager.findFragmentByTag(mOldTag) != null) {
                transaction.hide(fragmentManager.findFragmentByTag(mOldTag));
            }
            transaction.show(fragmentManager.findFragmentByTag(newTag)).commit();
        }
        else {
            if(fragmentManager.findFragmentByTag(mOldTag) != null) {
                transaction.hide(fragmentManager.findFragmentByTag(mOldTag));

            }
            transaction.add(R.id.contentContainer, fragmentContent, newTag).commit();
        }
        mOldTag = newTag;
    }

    /**
     * This method fill and refill the backstack of the activity and tries to guarantee the right order
     * for back navigation.
     * @param fragmentManager The FragmentManager object which contains the transaction object.
     * @param expectedEntryCount The amount of expected entries, which should be already in the back stack
     * @param expectedEntry The expected entry at the top of the back stack
     * @param refillEntrys The right order of entries, which the back stack should already contain
     * @return Returns true if there was something popped, else false.
     */
    private boolean fillBackStack(FragmentManager fragmentManager, int expectedEntryCount, String expectedEntry, List<String> refillEntrys){
        if(fragmentManager.getBackStackEntryCount() == expectedEntryCount && expectedEntryCount >= 1) {
            if (fragmentManager.getBackStackEntryAt(expectedEntryCount - 1).getName().equals(expectedEntry)) {
                //Remove back stack except the given one with flag = 0
                return fragmentManager.popBackStackImmediate(expectedEntry, 0);

            } else {
                //Empty the back stack and refill with the right entrys
                fragmentManager.popBackStackImmediate(fragmentManager.getBackStackEntryAt(0).getName(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                for(String entry : refillEntrys){
                    fragmentManager.beginTransaction().addToBackStack(entry).commit();
                }
            }

        } else if((fragmentManager.getBackStackEntryCount() > expectedEntryCount ||
                fragmentManager.getBackStackEntryCount() < expectedEntryCount) &&
                fragmentManager.getBackStackEntryCount() >= 1) {
            fragmentManager.popBackStackImmediate(fragmentManager.getBackStackEntryAt(0).getName(),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
            for(String entry : refillEntrys){
                fragmentManager.beginTransaction().addToBackStack(entry).commit();
            }

        } else {
            for(String entry : refillEntrys){
                fragmentManager.beginTransaction().addToBackStack(entry).commit();
            }
            return false;
        }

        return true;
    }

    /**
     * This method gets called on back pressed and shows the top view in the backstack.
     */
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(fragmentManager.getBackStackEntryCount() >= 1) {
            transaction.remove(fragmentManager.findFragmentByTag(mOldTag));
            //There is no proper way to get the latest inserted fragment in the back stack
            String tag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
            transaction.show(fragmentManager.findFragmentByTag(tag)).commit();
            mOldTag = tag;
            changeHeaderOnBackPressed(tag);
        }
        super.onBackPressed();
    }

    /**
     * This method gets called then the backstack gets changed and an old view gets visible.
     * It changes the Header and Bar icon colors to the right visible view.
     * @param tag
     */
    private void changeHeaderOnBackPressed(String tag){
        TextView txtHeader = (TextView) mFragmentHeader.getView().findViewById(R.id.txtHeader);
        switch(tag){
            case "dashboard":
                txtHeader.setText("Startseite");
                Bar.BarHandler.changeButtonColors(Bar.mImgDashboard, Bar.mImgSession, Bar.mImgRecipe, Bar.mImgSettings,
                        0xffffa726, 0xffffffff, 0xffffffff, 0xffffffff);
                break;
            case "session_overview":
                txtHeader.setText("Session");
                Bar.BarHandler.changeButtonColors(Bar.mImgDashboard, Bar.mImgSession, Bar.mImgRecipe, Bar.mImgSettings,
                        0xffffffff, 0xffffa726, 0xffffffff, 0xffffffff);
                break;
            case "session_details":
                txtHeader.setText("Session Details");
                Bar.BarHandler.changeButtonColors(Bar.mImgDashboard, Bar.mImgSession, Bar.mImgRecipe, Bar.mImgSettings,
                        0xffffffff, 0xffffa726, 0xffffffff, 0xffffffff);
                break;
            case "session":
                txtHeader.setText("Session");
                Bar.BarHandler.changeButtonColors(Bar.mImgDashboard, Bar.mImgSession, Bar.mImgRecipe, Bar.mImgSettings,
                        0xffffffff, 0xffffa726, 0xffffffff, 0xffffffff);
                break;
            case "recipe":
                txtHeader.setText("Rezepte");
                Bar.BarHandler.changeButtonColors(Bar.mImgDashboard, Bar.mImgSession, Bar.mImgRecipe, Bar.mImgSettings,
                        0xffffffff, 0xffffffff, 0xffffa726, 0xffffffff);
                break;
            case "recipe_detail":
                txtHeader.setText("Rezepte");
                Bar.BarHandler.changeButtonColors(Bar.mImgDashboard, Bar.mImgSession, Bar.mImgRecipe, Bar.mImgSettings,
                        0xffffffff, 0xffffffff, 0xffffa726, 0xffffffff);
                break;
            case "settings":
                txtHeader.setText("Einstellungen");
                Bar.BarHandler.changeButtonColors(Bar.mImgDashboard, Bar.mImgSession, Bar.mImgRecipe, Bar.mImgSettings,
                        0xffffffff, 0xffffffff, 0xffffffff, 0xffffa726);
                break;
            case "settings_general":
                txtHeader.setText("Einstellungen");
                Bar.BarHandler.changeButtonColors(Bar.mImgDashboard, Bar.mImgSession, Bar.mImgRecipe, Bar.mImgSettings,
                        0xffffffff, 0xffffffff, 0xffffffff, 0xffffa726);
                break;
            case "settings_profile":
                txtHeader.setText("Einstellungen");
                Bar.BarHandler.changeButtonColors(Bar.mImgDashboard, Bar.mImgSession, Bar.mImgRecipe, Bar.mImgSettings,
                        0xffffffff, 0xffffffff, 0xffffffff, 0xffffa726);
                break;
            case "settings_device":
                txtHeader.setText("Einstellungen");
                Bar.BarHandler.changeButtonColors(Bar.mImgDashboard, Bar.mImgSession, Bar.mImgRecipe, Bar.mImgSettings,
                        0xffffffff, 0xffffffff, 0xffffffff, 0xffffa726);
                break;
            case "settings_help":
                txtHeader.setText("Einstellungen");
                Bar.BarHandler.changeButtonColors(Bar.mImgDashboard, Bar.mImgSession, Bar.mImgRecipe, Bar.mImgSettings,
                        0xffffffff, 0xffffffff, 0xffffffff, 0xffffa726);
                break;
            case "settings_fitbit":
                txtHeader.setText("Einstellungen");
                Bar.BarHandler.changeButtonColors(Bar.mImgDashboard, Bar.mImgSession, Bar.mImgRecipe, Bar.mImgSettings,
                        0xffffffff, 0xffffffff, 0xffffffff, 0xffffa726);
                break;
            case "news":
                txtHeader.setText("Nachrichten");
                Bar.BarHandler.changeButtonColors(Bar.mImgDashboard, Bar.mImgSession, Bar.mImgRecipe, Bar.mImgSettings,
                        0xffffffff, 0xffffffff, 0xffffffff, 0xffffa726);
                break;
            case "settings_polar":
                txtHeader.setText("Einstellungen");
                Bar.BarHandler.changeButtonColors(Bar.mImgDashboard, Bar.mImgSession, Bar.mImgRecipe, Bar.mImgSettings,
                        0xffffffff, 0xffffffff, 0xffffffff, 0xffffa726);
                break;
            case "settings_news":
                txtHeader.setText("Einstellungen");
                Bar.BarHandler.changeButtonColors(Bar.mImgDashboard, Bar.mImgSession, Bar.mImgRecipe, Bar.mImgSettings,
                        0xffffffff, 0xffffffff, 0xffffffff, 0xffffa726);
                break;
            default:
                break;
        }
    }
}
