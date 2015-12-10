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
import de.ehealth.project.letitrip_beta.model.news.Content;
import de.ehealth.project.letitrip_beta.model.news.Image;
import de.ehealth.project.letitrip_beta.model.news.Media;
import de.ehealth.project.letitrip_beta.model.news.News;
import de.ehealth.project.letitrip_beta.model.news.Story;
import de.ehealth.project.letitrip_beta.view.MainActivity;

public class Dashboard extends Fragment {

    private FragmentChanger mListener;

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
