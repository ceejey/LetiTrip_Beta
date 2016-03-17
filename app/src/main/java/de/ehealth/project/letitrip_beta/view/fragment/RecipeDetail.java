package de.ehealth.project.letitrip_beta.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.news.DownloadImageTask;
import de.ehealth.project.letitrip_beta.model.recipe.Ingredient;
import de.ehealth.project.letitrip_beta.model.recipe.Recipe;
import de.ehealth.project.letitrip_beta.model.recipe.RecipeWrapper;
import de.ehealth.project.letitrip_beta.view.MainActivity;

public class RecipeDetail extends Fragment {
    private FragmentChanger mListener;

    /**
     * This method shows a recipe, which was choosen in the RecipeFragment.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);
        Recipe recipe = RecipeWrapper.selectedRecipe;
        ImageView imgRecipe = (ImageView) view.findViewById(R.id.imgRecipe);
        TextView txtRecipeSubHeading = (TextView) view.findViewById(R.id.txtRecipeSubheading);
        TextView txtRecipeBody = (TextView) view.findViewById(R.id.txtRecipeBody);

        TextView txtDifficulty = (TextView) view.findViewById(R.id.txtDifficulty);
        TextView txtPrepTime = (TextView) view.findViewById(R.id.txtPrepTime);
        TextView txtCookTime = (TextView) view.findViewById(R.id.txtCookTime);
        TextView txtPortions = (TextView) view.findViewById(R.id.txtPortions);
        TextView txtIngredients = (TextView) view.findViewById(R.id.txtIngredients);
        TextView txtRecipe = (TextView) view.findViewById(R.id.txtRecipe);

        new DownloadImageTask(imgRecipe, true).execute(recipe.getPic());
        txtRecipeSubHeading.setText(recipe.getName());
        String type = recipe.getType();
        if(type.equals("breakfast"))
            type = "Fr\u00fchstück";
        else if(type.equals("lunch"))
            type = "Mittagessen";
        else if(type.equals("dinner"))
            type = "Abendessen";
        txtRecipeBody.setText(type + " mit " + recipe.getKcal() + " kcal");

        txtDifficulty.setText(recipe.getDifficulty());
        txtPrepTime.setText(recipe.getPreparationTime() + " min");
        txtCookTime.setText(recipe.getCookTime() + " min");
        txtPortions.setText(recipe.getPortion());
        String ingredients = "";
        for(Ingredient ingredient : recipe.getIngredients()){
            ingredients += ingredient.getAmount() + " " + ingredient.getName() + ", ";
        }
        txtIngredients.setText(ingredients);
        txtRecipe.setText(recipe.getRecipe());

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
