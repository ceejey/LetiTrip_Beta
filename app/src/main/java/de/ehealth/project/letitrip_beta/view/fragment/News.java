package de.ehealth.project.letitrip_beta.view.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.news.NewsHandler;
import de.ehealth.project.letitrip_beta.model.news.Story;
import de.ehealth.project.letitrip_beta.view.MainActivity;

public class News extends Fragment {

    private FragmentChanger mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        TextView txtNewsTitle = (TextView) view.findViewById(R.id.txtNewsTitle);
        TextView txtNewsText = (TextView) view.findViewById(R.id.txtNewsText);

        txtNewsTitle.setText(NewsHandler.getmSelectedStory().getTitle());

        String body = NewsHandler.getmSelectedStory().getBody();
        for(int i = 0; i < body.length() ; i++){
            if(body.charAt(i) == '\n' && body.charAt(i+1) != '\n' && body.charAt(i+2) != '\n'){
                if(body.length() > i+1)
                    body = body.substring(0, i) + " " + body.substring(i+1, body.length());
            }
        }
        txtNewsText.setText(body);


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
