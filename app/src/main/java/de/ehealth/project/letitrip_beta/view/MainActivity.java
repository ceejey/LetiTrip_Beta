package de.ehealth.project.letitrip_beta.view;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.widget.TextView;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.view.fragment.Bar;
import de.ehealth.project.letitrip_beta.view.fragment.Dashboard;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;
import de.ehealth.project.letitrip_beta.view.fragment.Header;
import de.ehealth.project.letitrip_beta.view.fragment.Recipe;
import de.ehealth.project.letitrip_beta.view.fragment.Session;
import de.ehealth.project.letitrip_beta.view.fragment.SessionDetail;
import de.ehealth.project.letitrip_beta.view.fragment.SessionOverview;
import de.ehealth.project.letitrip_beta.view.fragment.settings.Device;
import de.ehealth.project.letitrip_beta.view.fragment.settings.General;
import de.ehealth.project.letitrip_beta.view.fragment.settings.Help;
import de.ehealth.project.letitrip_beta.view.fragment.settings.Profile;
import de.ehealth.project.letitrip_beta.view.fragment.settings.Settings;

public class MainActivity extends Activity implements FragmentChanger{

    Fragment mFragmentHeader;
    Fragment mFragmentCaption;
    Fragment mFragmentContent;
    Fragment mFragmentBar;

    public static enum FragmentName{
        DASHBOARD, SESSION_OVERVIEW, SESSION_DETAIL, SESSION, RECIPE, SETTINGS, SETTINGS_GENERAL,
        SETTINGS_PROFILE, SETTINGS_DEVICE, SETTINGS_HELP
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();

        mFragmentHeader = new Header();
        mFragmentContent = new Dashboard();
        mFragmentCaption = new Bar();

        fragmentManager.beginTransaction().replace(R.id.headerContainer, mFragmentHeader).commit();
        fragmentManager.beginTransaction().replace(R.id.captionContainer, mFragmentCaption).commit();
        fragmentManager.beginTransaction().replace(R.id.contentContainer, mFragmentContent).commit();
    }

    @Override
    public void changeFragment(FragmentName fn) {

        FragmentManager fragmentManager = getFragmentManager();

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

        }

        fragmentManager.beginTransaction().replace(R.id.contentContainer, fragmentContent).commit();
    }
}
