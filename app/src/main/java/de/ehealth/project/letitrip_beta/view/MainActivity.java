package de.ehealth.project.letitrip_beta.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.TextView;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.database.GPSDatabase;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSDatabaseHandler;
import de.ehealth.project.letitrip_beta.view.fragment.Bar;
import de.ehealth.project.letitrip_beta.view.fragment.Dashboard;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;
import de.ehealth.project.letitrip_beta.view.fragment.Header;
import de.ehealth.project.letitrip_beta.view.fragment.News;
import de.ehealth.project.letitrip_beta.view.fragment.Recipe;
import de.ehealth.project.letitrip_beta.view.fragment.Session;
import de.ehealth.project.letitrip_beta.view.fragment.SessionDetail;
import de.ehealth.project.letitrip_beta.view.fragment.SessionOverview;
import de.ehealth.project.letitrip_beta.view.fragment.fitbit.CheckUserProfile;
import de.ehealth.project.letitrip_beta.view.fragment.fitbit.FitBitInit;
import de.ehealth.project.letitrip_beta.view.fragment.fitbit.WebviewOauth;
import de.ehealth.project.letitrip_beta.view.fragment.settings.Device;
import de.ehealth.project.letitrip_beta.view.fragment.settings.General;
import de.ehealth.project.letitrip_beta.view.fragment.settings.Help;
import de.ehealth.project.letitrip_beta.view.fragment.settings.Profile;
import de.ehealth.project.letitrip_beta.view.fragment.settings.Settings;

public class MainActivity extends FragmentActivity implements FragmentChanger, SessionOverview.ShowRunOnMap{

    Fragment mFragmentHeader;
    Fragment mFragmentCaption;
    Fragment mFragmentContent;

    public static enum FragmentName{
        DASHBOARD, SESSION_OVERVIEW, SESSION_DETAIL, SESSION, RECIPE, SETTINGS, SETTINGS_GENERAL,
        SETTINGS_PROFILE, SETTINGS_DEVICE, SETTINGS_HELP, WEB_VIEW_OAUTH, FIT_BIT_INIT, FIT_BIT_CHECKPROFILE,
        NEWS
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();

        mFragmentHeader = new Header();
        mFragmentContent = new Dashboard();
        mFragmentCaption = new Bar();


        fragmentManager.beginTransaction().replace(R.id.headerContainer, mFragmentHeader).commit();
        fragmentManager.beginTransaction().replace(R.id.captionContainer, mFragmentCaption).commit();
        fragmentManager.beginTransaction().replace(R.id.contentContainer, mFragmentContent).commit();

        GPSDatabase myDB = new GPSDatabase(this);
        GPSDatabaseHandler.getInstance().setData(myDB);
    }

    @Override
    public void changeFragment(FragmentName fn) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment fragmentContent = new Dashboard();
        TextView txtHeader = (TextView) mFragmentHeader.getView().findViewById(R.id.txtHeader);

        switch (fn) {
            case DASHBOARD:
                txtHeader.setText("Startseite");
                fragmentContent = new Dashboard();
                break;
            case SESSION_OVERVIEW:
                txtHeader.setText("Session");
                fragmentContent = new SessionOverview();
                break;
            case SESSION_DETAIL:
                txtHeader.setText("Session Details");
                fragmentContent = new SessionDetail();
                break;
            case SESSION:
                txtHeader.setText("Session");
                fragmentContent = new Session();
                break;
            case RECIPE:
                txtHeader.setText("Rezepte");
                fragmentContent = new Recipe();
                break;
            case SETTINGS:
                txtHeader.setText("Einstellungen");
                fragmentContent = new Settings();
                break;
            case SETTINGS_GENERAL:
                txtHeader.setText("Einstellungen");
                fragmentContent = new General();
                break;
            case SETTINGS_PROFILE:
                txtHeader.setText("Einstellungen");
                fragmentContent = new Profile();
                break;
            case SETTINGS_DEVICE:
                txtHeader.setText("Einstellungen");
                fragmentContent = new Device();
                break;
            case SETTINGS_HELP:
                txtHeader.setText("Einstellungen");
                fragmentContent = new Help();
                break;
            case WEB_VIEW_OAUTH:
                txtHeader.setText("Einstellungen");
                fragmentContent = new WebviewOauth();
                break;
            case FIT_BIT_INIT:
                txtHeader.setText("Einstellungen");
                fragmentContent = new FitBitInit();
                break;
            case FIT_BIT_CHECKPROFILE:
                txtHeader.setText("Einstellungen");
                fragmentContent = new CheckUserProfile();
                break;
            case NEWS:
                txtHeader.setText("Nachrichten");
                fragmentContent = new News();
        }

        fragmentManager.beginTransaction().replace(R.id.contentContainer, fragmentContent).commit();
    }

    /**
     *  SessionOverview calls this method if a run should be shown on the map
     */
    @Override
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
    }

}
