package de.ehealth.project.letitrip_beta.handler.news;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.ExecutionException;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.model.news.Content;
import de.ehealth.project.letitrip_beta.model.news.Image;
import de.ehealth.project.letitrip_beta.model.news.News;
import de.ehealth.project.letitrip_beta.model.news.Story;
import de.ehealth.project.letitrip_beta.view.MainActivity;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;

/**
 * Created by eHealth on 09.12.2015.
 */
public class NewsHandler {

    private static Story mSelectedStory;
    private static boolean mTaskComplete = false;

    public static void fillNewsFeed(final View view, final LayoutInflater inflater, final Activity activity) {

        mTaskComplete = false;

        try {

            final String newsJson = new NewsTask("gesundheit", "75ba7c79e2aeff0db0e33b1db79ee188", "de", 15).execute().get();

            if (!newsJson.isEmpty()) {

                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        Gson gson = new Gson();
                        News news = gson.fromJson(newsJson, News.class);

                        if(news.getSuccess().equals("1") && news.getContent() != null && news.getContent().getStory() != null) {
                            Content content = news.getContent();
                            List<Story> storyList = content.getStory();

                            for (Story story : storyList) {

                                if (story.getTitle() != null && story.getPublished() != null && story.getRessort() != null &&
                                        story.getBody() != null && !story.getTitle().isEmpty() && !story.getPublished().isEmpty() &&
                                        !story.getBody().isEmpty() && !story.getRessort().isEmpty()) {

                                    LinearLayout placeHolder = new LinearLayout(view.findViewById(R.id.layoutDashboard).getContext());
                                    inflater.inflate(R.layout.news_view, placeHolder);
                                    ((LinearLayout) view.findViewById(R.id.layoutDashboard)).addView(placeHolder);

                                    TextView txtNewsImgCaption = (TextView) placeHolder.findViewById(R.id.txtNewsImgCaption);
                                    ImageView imgNews = (ImageView) placeHolder.findViewById(R.id.imgNews);

                                    if (story.getMedia() != null && story.getMedia().getImage() != null) {

                                        List<Image> imageList = story.getMedia().getImage();

                                        for (Image image : imageList) {

                                            if (image.getUrl() != null && image.getCaption() != null && !image.getUrl().isEmpty() &&
                                                    image.getCaption().contains("Veröffentlichung bitte unter Quellenangabe:")) {

                                                txtNewsImgCaption.setVisibility(View.VISIBLE);
                                                imgNews.setVisibility(View.VISIBLE);

                                                new DownloadImageTask(imgNews).execute(image.getUrl());

                                                String caption = image.getCaption();

                                                caption = caption.substring(caption.indexOf("Veröffentlichung bitte unter Quellenangabe: ") +
                                                        46, caption.lastIndexOf("\""));

                                                txtNewsImgCaption.setText(caption);
                                            } else {
                                                txtNewsImgCaption.setVisibility(View.GONE);
                                                imgNews.setVisibility(View.GONE);
                                            }
                                        }

                                    } else {
                                        txtNewsImgCaption.setVisibility(View.GONE);
                                        imgNews.setVisibility(View.GONE);
                                    }

                                    TextView txtNewsSubHeading = (TextView) placeHolder.findViewById(R.id.txtNewsSubheading);
                                    TextView txtNewsBody = (TextView) placeHolder.findViewById(R.id.txtNewsBody);

                                    txtNewsSubHeading.setText(story.getTitle());
                                    String published = story.getPublished();
                                    txtNewsBody.setText(story.getRessort() + " vom: " + published.substring(0, published.indexOf("T")));

                                    final Story clickedStory = story;

                                    placeHolder.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            if (activity instanceof FragmentChanger) {

                                                mSelectedStory = clickedStory;

                                                FragmentChanger changer = (FragmentChanger) activity;
                                                changer.changeFragment(MainActivity.FragmentName.NEWS);

                                            } else {
                                                Log.e("Error", "Can't bind onClickListener for reading News");
                                            }

                                        }

                                    });
                                }

                            }
                        } else {
                            showError(view, inflater, activity);
                        }
                    }

                });
            } else {

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showError(view, inflater, activity);
                    }
                });

            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mTaskComplete = true;
    }

    private static void showError(final View view, final LayoutInflater inflater, final Activity activity){
        LinearLayout placeHolder = new LinearLayout(view.findViewById(R.id.scrollViewDashboard).getContext());
        inflater.inflate(R.layout.news_view, placeHolder);
        ((LinearLayout) view.findViewById(R.id.layoutDashboard)).addView(placeHolder);
        TextView txtNewsImgCaption = (TextView) view.findViewById(R.id.txtNewsImgCaption);
        ImageView imgNews = (ImageView) view.findViewById(R.id.imgNews);
        TextView txtNewsSubHeading = (TextView) view.findViewById(R.id.txtNewsSubheading);
        TextView txtNewsBody = (TextView) view.findViewById(R.id.txtNewsBody);
        txtNewsImgCaption.setVisibility(View.GONE);
        imgNews.setVisibility(View.GONE);
        txtNewsSubHeading.setText("Ooops!");
        txtNewsBody.setText("Leider konnten keine Nachrichten heruntergeladen werden.");
    }

    public static Story getmSelectedStory() {
        return mSelectedStory;
    }

    public static void setmSelectedStory(Story mSelectedStory) {
        NewsHandler.mSelectedStory = mSelectedStory;
    }

    public static boolean ismTaskComplete() {
        return mTaskComplete;
    }


    public static void setmTaskComplete(boolean mTaskComplete) {
        NewsHandler.mTaskComplete = mTaskComplete;
    }
}
