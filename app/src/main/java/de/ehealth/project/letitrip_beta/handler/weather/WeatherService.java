package de.ehealth.project.letitrip_beta.handler.weather;

import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import de.ehealth.project.letitrip_beta.model.weather.Channel;


public class WeatherService extends AsyncTask<String, Void, String> {

    private WeatherCallback weatherServiveCallback;
    private Exception ex;

    //register callback
    public WeatherService(WeatherCallback callback) {
        this.weatherServiveCallback = callback;
    }

    @Override
    protected String doInBackground(String... params) {
        String encodedURL = null;
        String yqlQuery = null;
        int count = 0;

        //varargs to String array
        String[] args = new String[params.length];
        for (String temp: params){
            args[count++] = temp;
        }

        //query contains coordinates (instead of a city name)
        if (args.length > 1){
            yqlQuery = "select * from weather.forecast where woeid in (SELECT woeid FROM geo.placefinder WHERE text=\""+args[0]+","+args[1]+"\" and gflags=\"R\") and u='c' ";
        } else {
            yqlQuery = "select * from weather.forecast where woeid in (select woeid from geo.places(1) where text=\""+args[0]+"\")and u='c'";
        }

        //build the encoded URL
        encodedURL = String.format("https://query.yahooapis.com/v1/public/yql?q=%s&format=json", Uri.encode(yqlQuery));
        //Log.w("URL:",encodedURL);

            try {
                URLConnection connection = new URL(encodedURL).openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String in;
                String temp = "";
                while ((in = reader.readLine()) != null){
                    temp += in;
                }

                return temp;
            } catch (MalformedURLException e) {
                ex = new Exception("Keine Verbindung.");
            } catch (IOException e) {
                ex = new Exception("Keine Verbindung.");
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
            //convert the string to a jsonObject
            JSONObject data = new JSONObject(json);
            JSONObject queryResults = data.optJSONObject("query");

            //if no location was found YQL query returns count=0
            if (queryResults.optInt("count") == 0){
                weatherServiveCallback.failure(new Exception("Ort nicht gefunden."));
                return;
            }
            //if coordinates doesn't match to a known location
            if (queryResults.optJSONObject("results").optJSONObject("channel").optString("title").contains("Error")){
                weatherServiveCallback.failure(new Exception("Koordinaten nicht gefunden."));
                return;
            }

            //all weather data is stored in the "channel" jsonObject
            Channel channel = new Channel();
            channel.receive(queryResults.optJSONObject("results").optJSONObject("channel"));

            //call the callback function
            weatherServiveCallback.success(channel);
        } catch (JSONException e) {
            weatherServiveCallback.failure(e);
        }
    }
}
