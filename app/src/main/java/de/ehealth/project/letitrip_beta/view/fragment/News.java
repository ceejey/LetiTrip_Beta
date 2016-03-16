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
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.news.DownloadImageTask;
import de.ehealth.project.letitrip_beta.handler.news.NewsHandler;
import de.ehealth.project.letitrip_beta.model.news.Image;
import de.ehealth.project.letitrip_beta.model.news.Story;
import de.ehealth.project.letitrip_beta.view.MainActivity;

public class News extends Fragment {

    private FragmentChanger mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        Story story = NewsHandler.getmSelectedStory();

        ImageView imgNews = (ImageView) view.findViewById(R.id.imgNews);
        TextView txtNewsImgCaption = (TextView) view.findViewById(R.id.txtNewsImgCaption);
        TextView txtNewsSubHeading = (TextView) view.findViewById(R.id.txtNewsSubheading);
        TextView txtNewsBody = (TextView) view.findViewById(R.id.txtNewsBody);
        TextView txtNewsStory = (TextView) view.findViewById(R.id.txtNewsStory);



        if(story.getMedia() != null && story.getMedia().getImage() != null) {

            List<Image> imageList = story.getMedia().getImage();
            if(!imageList.isEmpty()) {
                for (Image image : imageList) {

                    if (image.getUrl() != null && image.getCaption() != null && !image.getUrl().isEmpty() &&
                            image.getCaption().contains("Veröffentlichung bitte unter Quellenangabe:")) {

                        new DownloadImageTask(imgNews, false).execute(image.getUrl());
                        String caption = image.getCaption();

                        caption = caption.substring(caption.indexOf("Veröffentlichung bitte unter Quellenangabe: ") +
                                46, caption.lastIndexOf("\""));
                        txtNewsImgCaption.setText(caption);

                    } else {
                        txtNewsImgCaption.setVisibility(View.GONE);
                        imgNews.setVisibility(View.GONE);
                    }
                }
            }else {
                txtNewsImgCaption.setVisibility(View.GONE);
                imgNews.setVisibility(View.GONE);
            }
        } else {
            txtNewsImgCaption.setVisibility(View.GONE);
            imgNews.setVisibility(View.GONE);
        }

        txtNewsSubHeading.setText(story.getTitle());
        String published = story.getPublished();

        txtNewsBody.setText(story.getRessort() + " vom: " + published.substring(0, published.indexOf("T")));

        String storyStr = story.getBody().replaceAll("[\\t\\n\\r]"," ");

        txtNewsStory.setText(storyStr);


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
