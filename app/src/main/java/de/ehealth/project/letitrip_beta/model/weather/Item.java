package de.ehealth.project.letitrip_beta.model.weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Item implements JSONReceiver {

    private Condition condition;
    private Forecast[] forecast;

    /**
     *
     * @param ID the future day [0..4]
     * @return a model class where weather data for this specific date are stored
     */
    public Forecast getForecast(int ID) {
        return forecast[ID];
    }


    public Condition getCondition() {
        return condition;
    }

    @Override
    public void receive(JSONObject data) {
        condition = new Condition();
        condition.receive(data.optJSONObject("condition"));


        JSONArray arr = data.optJSONArray("forecast");
        forecast = new Forecast[arr.length()];

        for (int i = 0; i < forecast.length; i++){
            try {
                forecast[i] = new Forecast();
                forecast[i].receive(arr.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}


