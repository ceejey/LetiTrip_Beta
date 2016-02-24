package de.ehealth.project.letitrip_beta.view.fragment.settings;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.database.RecipeDatabase;
import de.ehealth.project.letitrip_beta.model.fitbit.FitBitActivityScore;
import de.ehealth.project.letitrip_beta.model.fitbit.FitbitUserProfile;
import de.ehealth.project.letitrip_beta.model.recipe.Recipe;
import de.ehealth.project.letitrip_beta.model.recipe.RecipeWrapper;
import de.ehealth.project.letitrip_beta.view.MainActivity;
import de.ehealth.project.letitrip_beta.view.fragment.Bar;
import de.ehealth.project.letitrip_beta.view.fragment.FragmentChanger;

public class General extends Fragment {

    private FragmentChanger mListener;
    private Button mbtnUpdateRecipes;
    private Button mbtnResetApp;
    private Button mbtnResetActivityScore;
    private SeekBar msbrTouchSensibility;

    private ImageView imgUpdate;
    private ImageView imgResetActScor;
    private ImageView imgResetApp;
    private ImageView imgTouch;
    private TextView mUpdateRecipesDate;

    private String requestUrl ="http://recipeapi-ehealthrecipes.rhcloud.com/recipes?since=";
    private String mRecepiUrl ="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_general, container, false);

        imgUpdate = (ImageView) view.findViewById(R.id.imgUpdate);
        imgResetActScor = (ImageView) view.findViewById(R.id.imgResetActScore);
        imgResetApp = (ImageView) view.findViewById(R.id.imgResetApp);
        imgTouch = (ImageView) view.findViewById(R.id.imgTouch);

        imgUpdate.setColorFilter(0xff757575, PorterDuff.Mode.MULTIPLY);
        imgResetActScor.setColorFilter(0xff757575, PorterDuff.Mode.MULTIPLY);
        imgResetApp.setColorFilter(0xff757575, PorterDuff.Mode.MULTIPLY);
        imgTouch.setColorFilter(0xff757575, PorterDuff.Mode.MULTIPLY);
        mbtnResetActivityScore = (Button) view.findViewById(R.id.btnResetActScore);
        mbtnResetActivityScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetActScore();
            }
        });
        mbtnUpdateRecipes = (Button) view.findViewById(R.id.btnUpdateRecipes);
        mbtnUpdateRecipes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRecipeDatabase();
            }
        });
        mbtnResetApp = (Button) view.findViewById(R.id.btnResetApp);
        mbtnResetApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder //
                        .setMessage("Sind Sie sicher ?")
                        .setPositiveButton(getString(R.string.accept), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // TODO
                                resetApp();
                            }
                        }) //
                        .setNegativeButton(getString(R.string.decline), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // TODO
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        });
        msbrTouchSensibility = (SeekBar)view.findViewById(R.id.sbrTouchSensibility);
        msbrTouchSensibility.setMax(30);
        msbrTouchSensibility.setProgress(Integer.parseInt(FitbitUserProfile.getmActiveUser().getmClickOffsetForBarSensibility()));
        msbrTouchSensibility.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                Log.d("Fitbit", "TEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEST");
                FitbitUserProfile.getmActiveUser().setmClickOffsetForBarSensibility(Integer.toString(msbrTouchSensibility.getProgress()));
                FitbitUserProfile.saveUser(getActivity());
                Bar.setClickOffset(msbrTouchSensibility.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
            }
        });
        mUpdateRecipesDate= (TextView)view.findViewById(R.id.txtUpdateDate);
        mUpdateRecipesDate.setText(FitbitUserProfile.getmActiveUser().getmLastRezeptUpdateSince());
        return view;
    }

    private void updateRecipeDatabase(){
        mRecepiUrl = requestUrl + FitbitUserProfile.getmActiveUser().getmLastRezeptUpdateSince();

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        // Get the date today using Calendar object.
        Date today = Calendar.getInstance().getTime();
        // Using DateFormat format method we can create a string
        // representation of a date with the defined format.
        String reportDate = df.format(today);

        try {
            final String recipeJson = new UpdateRecipeDatabase().execute().get();

            RecipeWrapper.getInstance().fromJsonToRecipes(recipeJson);

            RecipeDatabase recipeDb = new RecipeDatabase(getActivity());
            List<Recipe> recipes = RecipeWrapper.getInstance().getRecipes();
            for(Recipe recipe : recipes) {
                if(recipeDb.getRecipe(recipe.getId()) == null) {
                    recipeDb.addData(recipe.getCookTime(), recipe.getDifficulty(), recipe.getDate_added(), recipe.getKcal(),
                            recipe.getPortion(), recipe.getName(), recipe.getRecipe(), recipe.getPreparationTime(),
                            recipe.getIngredients(), recipe.getId(), recipe.getPic(), recipe.getType());
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        mUpdateRecipesDate.setText(reportDate);
        FitbitUserProfile.getmActiveUser().setmLastRezeptUpdateSince(reportDate);
        FitbitUserProfile.saveUser(getActivity());
    }

    private void resetActScore() {
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    // Get the date today using Calendar object.
        Date today = Calendar.getInstance().getTime();
    // Using DateFormat format method we can create a string
    // representation of a date with the defined format.
        String reportDate = df.format(today);

        FitbitUserProfile.getmActiveUser().setmActScoreResetDate(reportDate);
        FitbitUserProfile.saveUser(getActivity());


        FitBitActivityScore.calcActivtiyScore(getActivity());
    }

    private void resetApp(){
        FitbitUserProfile.deleteUser(getActivity());
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
