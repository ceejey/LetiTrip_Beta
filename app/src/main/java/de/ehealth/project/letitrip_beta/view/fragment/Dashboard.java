package de.ehealth.project.letitrip_beta.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.ExecutionException;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.task.news.NewsTask;
import de.ehealth.project.letitrip_beta.model.news.Content;
import de.ehealth.project.letitrip_beta.model.news.News;
import de.ehealth.project.letitrip_beta.model.news.Story;
import de.ehealth.project.letitrip_beta.view.MainActivity;

public class Dashboard extends Fragment {

    private FragmentChanger mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        Thread thread = new Thread(){
            @Override
            public void run(){
                fillNewsFeed(view);
            }
        };
        thread.start();

        return view;
    }

    //TODO
    private void fillNewsFeed(final View view){

        Gson gson = new Gson();
        String newsJson = "";
        try {
            newsJson = new NewsTask("gesundheit", "dbce43690d443111a49fc0b38f48e2d4", "de", 15).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        News news = gson.fromJson(newsJson, News.class);
        Content content = news.getContent();
        final List<Story> storyList = content.getStory();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                for (Story story : storyList) {
                    TextView newsTitle = new TextView(view.findViewById(R.id.dashboardView).getContext());
                    newsTitle.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    newsTitle.setText(story.getTitle());
                    ((LinearLayout) view.findViewById(R.id.dashboardView)).addView(newsTitle);
                }
            }
        });
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
