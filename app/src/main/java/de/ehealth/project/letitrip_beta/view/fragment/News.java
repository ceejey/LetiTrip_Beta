package de.ehealth.project.letitrip_beta.view.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.news.NewsHandler;
import de.ehealth.project.letitrip_beta.view.MainActivity;

public class News extends Fragment {

    private FragmentChanger mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        TextView txtNewsTitle = (TextView) view.findViewById(R.id.txtNewsTitle);

        txtNewsTitle.setText(NewsHandler.getmSelectedStory().getTitle());

        String body = NewsHandler.getmSelectedStory().getBody();

        body = Html.escapeHtml(body);
        WebView webView = (WebView) view.findViewById(R.id.webVNews);

        String text = "<html><body>" +
                        "<p align=\"justify\">" + body + "</p> " +
                    "</body></html>";

        webView.loadData(text, "text/html", "utf-8");

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
