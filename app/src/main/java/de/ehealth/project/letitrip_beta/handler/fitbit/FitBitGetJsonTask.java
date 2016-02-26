package de.ehealth.project.letitrip_beta.handler.fitbit;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.ehealth.project.letitrip_beta.handler.database.FitBitUserDataSQLite;
import de.ehealth.project.letitrip_beta.model.fitbit.FitBitUserData;
import de.ehealth.project.letitrip_beta.model.settings.UserSettings;


/** The class "FitBitGetJsonTask" extends "AsyncTask" and its purpose is to request and receive JSon
 * objects. The constructor contains the parameter string "endpoint" which is compared with the
 * 3 static string variables of the class itself. Therefore there a 3 different requested JSon objects:
 * 1. getProfile() - request JsonObject of the current FitBit user
 * 2. getMovementOfTwoWeeks - calls up a Json object for the last two weeks and saves the information
 *    ,but not for today's date, because the data of today can change till 12 AM!
 * 3. getMovementOfToday - calls up Json object for today's date alike the method from above */
public class FitBitGetJsonTask extends AsyncTask<Void, Void, Void> {

    private Oauth mOauth = null;
    private String mJson = null;
    private String mEndpoint = "";
    private ProgressDialog dialog = null;
    private Activity mActivity = null;
    private int mNewWeight = 0;

    public static final String ENDPOINT_PROFILE = "profile";
    public static final String ENDPOINT_MOVES = "activities";
    public static final String ENDPOINT_MOVE = "activity";
    public static final String ENDPOINT_WEIGHT = "weight";

    public FitBitGetJsonTask(Oauth oauth, String endpoint, Activity act){
        mOauth = oauth.getOauth();
        mEndpoint = endpoint;
        mActivity = act;
        dialog = new ProgressDialog(mActivity);
    }

    public FitBitGetJsonTask(Oauth oauth, String endpoint, Activity act, int newWeight){
        mOauth = oauth.getOauth();
        mEndpoint = endpoint;
        mActivity = act;
        dialog = new ProgressDialog(mActivity);
        mNewWeight = newWeight;
    }

    @Override
    protected void onPreExecute()
    {
        this.dialog.setMessage("Please wait");
        this.dialog.show();
    }

    @Override
    protected void onPostExecute(Void v) {
        if(dialog != null) dialog.dismiss();
    }

    @Override
    protected Void doInBackground(Void... params) {
        if(mEndpoint.equals(ENDPOINT_PROFILE)) {
            getProfile();
        }
        if(mEndpoint.equals(ENDPOINT_MOVES)) {
            getMovementOfTwoWeeks();
        }
        if(mEndpoint.equals(ENDPOINT_MOVE)) {
            getMovementOfOneDay();
        }
        if(mEndpoint.equals(ENDPOINT_WEIGHT)) {
            if(mNewWeight != 0){
                setWeight();
            }
        }
        return null;
    }

    /**This method calls up a Json object for the current user. The Request URL can be found in the API documentation:
    "https://dev.fitbit.com/docs". The JSon object is parsed to be a Object of the class "UserSettings"  */
    private void getProfile() {
        sendRequestUrl("https://api.fitbit.com/1/user/-/profile.json");
        UserSettings.JsonToUserProfile(mJson, mOauth.getmAccessToken());
    }

    /**This method calls up a Json object for the last two weeks, but not for today's date!
    If data set already exists in the SQL table of one day then the for loop jumps to the next date
    else the data set will be requested. The exception catch is for the possibility
    of lost connection. The Request URL can be found in the API documentation:
    "https://dev.fitbit.com/docs/activity/". After receiving the response JSon object array this array is
    parsed by "org.json.JSONObject" because it contains then only the JSon object summary which can
    be found in the documentation. This "summary" Json object is parsed again with a "gson" default
    "FitBitUserData" class parser and at last the properties of the "FitBitUserData" object will be added to
    the SQL lite table which implementation can be found in the class "FitBitUserDataSQLite". */
    private void getMovementOfTwoWeeks(){
        Gson gson = new Gson();
        Calendar now = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String[] days = new String[14];
        now.add(Calendar.DAY_OF_MONTH, -14);
        for (int i = 0; i < days.length; i++) {
            days[i] = format.format(now.getTime());
            if(FitBitUserDataSQLite.getInstance(mActivity).getFitBitData(days[i]) == null) {
                try {
                    String requestUrl = "https://api.fitbit.com/1/user/" + "-" +
                            "/" + "activities" + "/date/" + days[i] + ".json";
                    sendRequestUrl(requestUrl);
                    JSONObject obj = new JSONObject(mJson).getJSONObject("summary");
                    mJson = "{\"summary\":" + obj.toString() + "}";
                    if(mJson != null) {
                        FitBitUserData fitbitmov = gson.fromJson(mJson, FitBitUserData.class);
                        FitBitUserDataSQLite.getInstance(mActivity).addFitBitData(fitbitmov, days[i]);
                    }
                }catch(Exception ex){ ex.printStackTrace(); }
            }
            now.add(Calendar.DAY_OF_MONTH, 1);
        }

    }

    /**This method calls up a Json-object for today's date alike the method from above! */
    private void getMovementOfOneDay(){

        Gson gson = new Gson();
        Calendar now = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String today = format.format(now.getTime());

        try {
            String requestUrl = "https://api.fitbit.com/1/user/" + "-" +
                    "/"+ "activities" +"/date/" + today + ".json";
            sendRequestUrl(requestUrl);
            JSONObject obj = new JSONObject(mJson).getJSONObject("summary");
            mJson = "{\"summary\":" + obj.toString()+ "}";
            if(mJson != null) {
                FitBitUserData fitbitmov = gson.fromJson(mJson, FitBitUserData.class);
                FitBitUserDataSQLite.getInstance(mActivity).setCurFitBitUserMovement(fitbitmov);
            }
        }catch(Exception ex){ ex.printStackTrace(); }
    }
    /**It's a method that sends a request URL with the AccessToken which allows to get a response
     from the service provider in this case FitBit. */
    public void sendRequestUrl(String url) {
        OAuthRequest oAuthRequest;
        Response response;
        oAuthRequest = new OAuthRequest(Verb.GET, url);
        mOauth.getmService().signRequest(mOauth.getmAccessToken(), oAuthRequest);
        response = oAuthRequest.send();
        mJson = response.getBody();
        Log.d("Fitbit","!!!!!!" +mJson);
    }
    /** This method set's the current weight of the User from the Gui to FitBit Server*/
    public void setWeight() {

        String url = "https://api.fitbit.com/1/user/" + "-" +
                "/"+ "body/"+ "log/weight"+ ".json";
        OAuthRequest oAuthRequest;
        Response response;
        oAuthRequest = new OAuthRequest(Verb.POST, url);
        oAuthRequest.addBodyParameter("weight", Integer.toString(mNewWeight));
        oAuthRequest.addBodyParameter("date","today");
        mOauth.getmService().signRequest(mOauth.getmAccessToken(), oAuthRequest);
        response = oAuthRequest.send();
        mJson = response.getBody();
        Log.d("Fitbit","!!!!!!" + mJson);
    }

}
