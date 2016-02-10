package de.ehealth.project.letitrip_beta.view.fragment.settings;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.model.fitbit.FitbitUserProfile;
import de.ehealth.project.letitrip_beta.view.MainActivity;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;

public class General extends Fragment {

    private FragmentChanger mListener;
    private Button mbtnUpdateRecipes;
    private String requestUrl ="http://recipeapi-ehealthrecipes.rhcloud.com/recipes?since=";
    private  String mRecepiUrl ="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_general, container, false);
        mbtnUpdateRecipes = (Button) view.findViewById(R.id.btnUpdateRecipes);
        mbtnUpdateRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRecipeDatabase();
            }
        });

        return view;
    }

    private void updateRecipeDatabase(){
        mRecepiUrl = requestUrl + FitbitUserProfile.getmActiveUser().getmLastRezeptUpdateSince();
        Date date = new Date();
        int since = date.getDate(); //meintest du gettime oder getdate ? also mit date l
        try {
            final String recepiJson = new UpdateRecipeDatabase().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        FitbitUserProfile.getmActiveUser().setmLastRezeptUpdateSince(Long.toString(since));
        FitbitUserProfile.saveUser(getActivity());
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
    public class UpdateRecipeDatabase extends AsyncTask<Void, Void, String> {

        protected String doInBackground(Void... params) {

            String responseJson = "";
            try {
                String url = mRecepiUrl;

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
}
