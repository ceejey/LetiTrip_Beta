package de.ehealth.project.letitrip_beta.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.database.GPSDatabase;
import de.ehealth.project.letitrip_beta.handler.database.WeatherDatabase;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSDatabaseHandler;
import de.ehealth.project.letitrip_beta.handler.session.SessionHandler;
import de.ehealth.project.letitrip_beta.handler.weather.WeatherDatabaseHandler;
import de.ehealth.project.letitrip_beta.view.fragment.Bar;
import de.ehealth.project.letitrip_beta.view.fragment.Dashboard;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;
import de.ehealth.project.letitrip_beta.view.fragment.Header;
import de.ehealth.project.letitrip_beta.view.fragment.News;
import de.ehealth.project.letitrip_beta.view.fragment.Recipe;
import de.ehealth.project.letitrip_beta.view.fragment.Session;
import de.ehealth.project.letitrip_beta.view.fragment.SessionDetail;
import de.ehealth.project.letitrip_beta.view.fragment.SessionOverview;
import de.ehealth.project.letitrip_beta.view.fragment.fitbit.FitBitInit;
import de.ehealth.project.letitrip_beta.view.fragment.fitbit.WebviewOauth;
import de.ehealth.project.letitrip_beta.view.fragment.settings.Device;
import de.ehealth.project.letitrip_beta.view.fragment.settings.General;
import de.ehealth.project.letitrip_beta.view.fragment.settings.Help;
import de.ehealth.project.letitrip_beta.view.fragment.settings.NewsSettings;
import de.ehealth.project.letitrip_beta.view.fragment.settings.Polar;
import de.ehealth.project.letitrip_beta.view.fragment.settings.Profile;
import de.ehealth.project.letitrip_beta.view.fragment.settings.Settings;

public class MainActivity extends FragmentActivity implements FragmentChanger{

    Fragment mFragmentHeader;
    Fragment mFragmentCaption;
    Fragment mFragmentContent;

    String mOldTag = "";

    public static enum FragmentName{
        DASHBOARD, SESSION_OVERVIEW, SESSION_DETAIL, SESSION, RECIPE, SETTINGS, SETTINGS_GENERAL,
        SETTINGS_PROFILE, SETTINGS_DEVICE, SETTINGS_HELP, WEB_VIEW_OAUTH, FIT_BIT_INIT,
        NEWS, POLAR_DEVICE, NEWS_SETTINGS
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentHeader = new Header();
        mFragmentContent = new Dashboard();
        mFragmentCaption = new Bar();

        getSupportFragmentManager().beginTransaction().replace(R.id.headerContainer, mFragmentHeader).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.captionContainer, mFragmentCaption).commit();
        getSupportFragmentManager().beginTransaction().replace(R.id.contentContainer, mFragmentContent, "dashboard").commit();

        mOldTag = "dashboard";

        GPSDatabaseHandler.getInstance().setData(new GPSDatabase(this));
        WeatherDatabaseHandler.getInstance().setData(new WeatherDatabase(this));
    }

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

                fragmentContent = fragmentManager.findFragmentByTag(newTag);

                if (fragmentContent != null)
                    alreadyAdded = true;
                else
                    fragmentContent = new SessionOverview();
                break;
            case SESSION_DETAIL: //Initializes everytime again with new session id
                txtHeader.setText("Session Details");
                newTag = "session_details";

                expectedEntryCount = 2;
                expectedEntry = "session_overview";
                refillEntrys.add("dashboard");
                refillEntrys.add("session_overview");
                fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);

                fragmentContent = fragmentManager.findFragmentByTag(newTag);
                if (fragmentContent != null)
                    transaction.remove(fragmentContent); //REMOVE THE SESSION DETAILS AND PUT NEW EXTRAS

                fragmentContent = new SessionDetail();
                //TODO
                Bundle args = new Bundle();//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                args.putInt("runID", SessionHandler.getSelectedRunId());//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                fragmentContent.setArguments(args);//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

                break;
            case SESSION:
                txtHeader.setText("Session");
                newTag = "session";

                expectedEntryCount = 2;
                expectedEntry = "session_overview";
                refillEntrys.add("dashboard");
                refillEntrys.add("session_overview");
                fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);

                fragmentContent = fragmentManager.findFragmentByTag(newTag);
                if (fragmentContent != null)
                    alreadyAdded = true;
                else
                    fragmentContent = new Session();
                break;
            case RECIPE:
                txtHeader.setText("Rezepte");
                fragmentContent = new Recipe();
                break;
            case SETTINGS:
                txtHeader.setText("Einstellungen");
                newTag = "settings";

                expectedEntryCount = 1;
                expectedEntry = "dashboard";
                refillEntrys.add("dashboard");
                fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);

                fragmentContent = fragmentManager.findFragmentByTag(newTag);
                if (fragmentContent != null)
                    alreadyAdded = true;
                else
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

                fragmentContent = fragmentManager.findFragmentByTag(newTag);
                if (fragmentContent != null)
                    alreadyAdded = true;
                else
                    fragmentContent = new General();
                break;
            case SETTINGS_PROFILE:
                txtHeader.setText("Einstellungen");
                newTag = "settings_profile";

                expectedEntryCount = 2;
                expectedEntry = "settings";
                refillEntrys.add("dashboard");
                refillEntrys.add("settings");
                fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);

                fragmentContent = fragmentManager.findFragmentByTag(newTag);
                if (fragmentContent != null)
                    alreadyAdded = true;
                else
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

                fragmentContent = fragmentManager.findFragmentByTag(newTag);
                if (fragmentContent != null)
                    alreadyAdded = true;
                else
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

                fragmentContent = fragmentManager.findFragmentByTag(newTag);
                if (fragmentContent != null)
                    alreadyAdded = true;
                else
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

                fragmentContent = fragmentManager.findFragmentByTag(newTag);
                if (fragmentContent != null)
                    transaction.remove(fragmentContent);

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

                fragmentContent = fragmentManager.findFragmentByTag(newTag);
                if (fragmentContent != null)
                    alreadyAdded = true;
                else
                    fragmentContent = new FitBitInit();
                break;
            case NEWS:
                txtHeader.setText("Nachrichten");
                newTag = "news";

                expectedEntryCount = 1;
                expectedEntry = "dashboard";
                refillEntrys.add("dashboard");
                fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);

                fragmentContent = fragmentManager.findFragmentByTag(newTag);
                if (fragmentContent != null)
                    transaction.remove(fragmentContent);

                fragmentContent = new News();
                break;
            case POLAR_DEVICE:
                txtHeader.setText("Polar-Einstellungen");
                newTag = "settings_polar";

                expectedEntryCount = 3;
                expectedEntry = "settings_device";
                refillEntrys.add("dashboard");
                refillEntrys.add("settings");
                refillEntrys.add("settings_device");
                fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);

                fragmentContent = fragmentManager.findFragmentByTag(newTag);
                if (fragmentContent != null)
                    alreadyAdded = true;
                else
                    fragmentContent = new Polar();
                break;
            case NEWS_SETTINGS:
                txtHeader.setText("News-Einstellungen");
                newTag = "settings_news";

                expectedEntryCount = 2;
                expectedEntry = "settings";
                refillEntrys.add("dashboard");
                refillEntrys.add("settings");
                fillBackStack(fragmentManager, expectedEntryCount, expectedEntry, refillEntrys);

                fragmentContent = fragmentManager.findFragmentByTag(newTag);
                if (fragmentContent != null)
                    alreadyAdded = true;
                else
                    fragmentContent = new NewsSettings();
        }

        if(alreadyAdded){
            if(fragmentManager.findFragmentByTag(mOldTag) != null) {
                Log.d("TEST", "HIDE FRAGMENT: " + mOldTag);
                transaction.hide(fragmentManager.findFragmentByTag(mOldTag));
            }
            Log.d("TEST", "SHOW FRAGMENT: " + newTag);
            transaction.show(fragmentManager.findFragmentByTag(newTag)).commit();
        }
        else {
            if(fragmentManager.findFragmentByTag(mOldTag) != null) {
                Log.d("TEST", "HIDE FRAGMENT: " + mOldTag);
                transaction.hide(fragmentManager.findFragmentByTag(mOldTag));
            }
            Log.d("TEST", "INIT FRAGMENT: " + newTag);
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
            Log.d("Test", "Expected entry count is right and > 1");
            if (fragmentManager.getBackStackEntryAt(expectedEntryCount - 1).getName().equals(expectedEntry)) {
                Log.d("Test", "Expected entry is right");
                //Remove back stack except the given one with flag = 0
                return fragmentManager.popBackStackImmediate(expectedEntry, 0);

            } else {
                Log.d("Test", "Expected entry is wrong, pop all");
                //Empty the back stack and refill with the right entrys
                fragmentManager.popBackStackImmediate(fragmentManager.getBackStackEntryAt(0).getName(),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
                for(String entry : refillEntrys){
                    Log.d("Test", "Add to back stack: " + entry);
                    fragmentManager.beginTransaction().addToBackStack(entry).commit();
                }
            }

        } else if((fragmentManager.getBackStackEntryCount() > expectedEntryCount ||
                fragmentManager.getBackStackEntryCount() < expectedEntryCount) &&
                fragmentManager.getBackStackEntryCount() >= 1) {
            Log.d("Test", "Back stack entry count is bigger or smaller than expected entry count but not empty, pop all");
            fragmentManager.popBackStackImmediate(fragmentManager.getBackStackEntryAt(0).getName(),
                    FragmentManager.POP_BACK_STACK_INCLUSIVE);
            for(String entry : refillEntrys){
                Log.d("Test", "Add to back stack: " + entry);
                fragmentManager.beginTransaction().addToBackStack(entry).commit();
            }

        } else {
            Log.d("Test", "Expected entry count is empty");
            for(String entry : refillEntrys){
                Log.d("Test", "Add to back stack: " + entry);
                fragmentManager.beginTransaction().addToBackStack(entry).commit();
            }
            return false;
        }

        return true;
    }

    /**
     *  SessionOverview calls this method if a run should be shown on the map
     */
    /*@Override
    public void setSelectedRunID(int id) {
        Log.w("activity","setSelectedRunID called!");
        SessionDetail newFragment = new SessionDetail();
        Bundle args = new Bundle();
        args.putInt("runID", id);
        newFragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentContainer, newFragment).commit();

        //gpsservice wont be able to stop anymore when this line is called
        //transaction.addToBackStack(null); //to be able to use the back button

        newFragment.setSelectedRunID(id);

        //changeFragment(FragmentName.SESSION_DETAIL);
    }*/

    @Override
    public void onBackPressed() {
        Log.d("Test", "Current backstack count: " + getSupportFragmentManager().getBackStackEntryCount());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(fragmentManager.getBackStackEntryCount() >= 1) {
            transaction.hide(fragmentManager.findFragmentByTag(mOldTag));

            //There is no proper way to get the latest inserted fragment in the back stack
            String tag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
            transaction.show(fragmentManager.findFragmentByTag(tag)).commit();

            mOldTag = tag;
        }
        super.onBackPressed();
    }

}
