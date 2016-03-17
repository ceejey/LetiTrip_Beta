package de.ehealth.project.letitrip_beta.handler.recipe;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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

import de.ehealth.project.letitrip_beta.handler.database.RecipeDatabase;
import de.ehealth.project.letitrip_beta.model.settings.UserSettings;
import de.ehealth.project.letitrip_beta.model.recipe.Recipe;
import de.ehealth.project.letitrip_beta.model.recipe.RecipeWrapper;

/**
 * Created by Mirorn on 24.02.2016.
 */
public class RecipeUpdateHandler {
    private String requestUrl ="http://recipeapi-ehealthrecipes.rhcloud.com/recipes?since=";
    private String mRecepiUrl ="";
    private Activity mActivity;

    public void updateRecipeDatabase(Activity activity){
        String since = UserSettings.getmActiveUser().getmLastRezeptUpdateSince();
        if(!since.isEmpty())
            mRecepiUrl = requestUrl + since;
        else
            mRecepiUrl = requestUrl + "0";

        mActivity = activity;

        try {
            final String recipeJson = new UpdateRecipeDatabase().execute().get();
            if(!recipeJson.isEmpty()) {

                DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                // Get the date today using Calendar object.
                Date today = Calendar.getInstance().getTime();
                // Using DateFormat format method we can create a string
                // representation of a date with the defined format.
                String reportDate = df.format(today);

                RecipeWrapper.getInstance().fromJsonToRecipes(recipeJson);
                RecipeDatabase recipeDb = new RecipeDatabase(activity);

                List<Recipe> recipes = RecipeWrapper.getInstance().getRecipes();
                for (Recipe recipe : recipes) {
                    if (recipeDb.getRecipe(recipe.getId()) == null) {
                        recipeDb.addData(recipe.getCookTime(), recipe.getDifficulty(), recipe.getDate_added(), recipe.getKcal(),
                                recipe.getPortion(), recipe.getName(), recipe.getRecipe(), recipe.getPreparationTime(),
                                recipe.getIngredients(), recipe.getId(), recipe.getPic(), recipe.getType());
                    }
                }

                UserSettings.getmActiveUser().setmLastRezeptUpdateSince(reportDate);
                UserSettings.saveUser(activity);
                Toast.makeText(activity, "Update erfolgreich!", Toast.LENGTH_SHORT).show();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            Toast.makeText(activity,"Update fehlgeschlagen! Prüfen Sie ihre Internetverbindung.", Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            Toast.makeText(activity,"Update fehlgeschlagen! Prüfen Sie ihre Internetverbindung.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
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

            } catch (IOException ex) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mActivity, "Update fehlgeschlagen! Prüfen Sie ihre Internetverbindung.", Toast.LENGTH_LONG).show();
                    }
                });
                ex.printStackTrace();
            }
            return responseJson;
        }
    }
}