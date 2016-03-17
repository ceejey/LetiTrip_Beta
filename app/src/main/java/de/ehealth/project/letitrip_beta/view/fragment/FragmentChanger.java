package de.ehealth.project.letitrip_beta.view.fragment;

import de.ehealth.project.letitrip_beta.view.MainActivity;

/**
 * This interface is implemented to make a callback to the activity from his fragments
 */
public interface FragmentChanger {
    void changeFragment(MainActivity.FragmentName fn);
}