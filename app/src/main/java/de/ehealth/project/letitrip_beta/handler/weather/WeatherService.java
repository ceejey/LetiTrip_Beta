package de.ehealth.project.letitrip_beta.handler.weather;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import de.ehealth.project.letitrip_beta.model.weather.Channel;

//select * from weather.forecast where woeid in (SELECT woeid FROM geo.placefinder WHERE text="52.4242,6.52342" and gflags="R")

public class WeatherService extends AsyncTask<String, Void, String> {

        private WeatherCallback weatherServiveCallback;
        private Exception ex;

        public WeatherService(WeatherCallback callback) {
            this.weatherServiveCallback =callback;
        }


    @Override
    protected String doInBackground(String... params) {
        String encodedURL = null;
        String yqlQuery = null;
        int count=0;

        //varargs to String array
        String[] args = new String[params.length];
        for (String temp: params){
            args[count++] = temp;
        }

        //yql query contains coordinates
        if (args.length > 1){
            yqlQuery = "select * from weather.forecast where woeid in (SELECT woeid FROM geo.placefinder WHERE text=\""+args[0]+","+args[1]+"\" and gflags=\"R\") and u='c' ";
        } else {
            yqlQuery = "select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\""+args[0]+"\")and u='c'";
        }

        //build the encoded URL
        encodedURL = String.format("https://query.yahooapis.com/v1/public/yql?q=%s&format=json", Uri.encode(yqlQuery));

        Log.w("URL:",encodedURL);
            try {
                URLConnection connection = new URL(encodedURL).openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuffer builder = new StringBuffer();
                String temp;
                while ((temp = reader.readLine()) != null){
                    builder.append(temp);
                }

                return builder.toString();
            } catch (MalformedURLException e) {
                ex = e;
            } catch (IOException e) {
                ex = e;
            }


        return null;
    }

    @Override
    protected void onPostExecute(String json) {
        super.onPostExecute(json);
        if ((json == null) || (ex != null)) {
            weatherServiveCallback.failure(ex);
            return;
        }

        try {
            JSONObject data = new JSONObject(json);
            JSONObject queryResults = data.optJSONObject("query");

            //if no location was found YQL query returns count=0
            if (queryResults.optInt("count") == 0){
                weatherServiveCallback.failure(new Exception("Location not found."));
                return;
            }
            //if coordinates don't match to a known location
            if (queryResults.optJSONObject("results").optJSONObject("channel").optString("title").contains("Error")){
                weatherServiveCallback.failure(new Exception("Coordinates not found."));
                return;
            }

            Channel channel = new Channel();
            channel.receive(queryResults.optJSONObject("results").optJSONObject("channel"));

            weatherServiveCallback.success(channel);

        } catch (JSONException e) {
            weatherServiveCallback.failure(e);
        }
    }
}
