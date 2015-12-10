package de.ehealth.project.letitrip_beta.handler.task.news;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewsTask extends AsyncTask<Void, Void, String> {

    private String mBranche = "";
    private String mApiKey = "";
    private String mLanguage = "";
    private Integer mLimit = 1;

    public NewsTask(String branche, String apiKey, String language, Integer limit) {
        mBranche = branche;
        mApiKey = apiKey;
        mLanguage = language;
        mLimit = limit;
    }

    protected String doInBackground(Void... params) {

        String responseJson = "";
        try {

            String url = "http://api.presseportal.de/api/article/branche/" + mBranche + "?api_key=" + mApiKey + "&format=json" +
                    "&lang=" + mLanguage + "&limit=" + mLimit;

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            responseJson = response.toString();
            Log.d("TEST", responseJson);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return responseJson;
    }
}