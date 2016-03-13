package de.ehealth.project.letitrip_beta.view.fragment;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.handler.database.RecipeDatabase;
import de.ehealth.project.letitrip_beta.handler.gpshandler.GPSDatabaseHandler;
import de.ehealth.project.letitrip_beta.handler.news.DownloadImageTask;
import de.ehealth.project.letitrip_beta.model.news.Story;
import de.ehealth.project.letitrip_beta.model.recipe.Recipe;
import de.ehealth.project.letitrip_beta.model.recipe.RecipeWrapper;
import de.ehealth.project.letitrip_beta.model.weather.DescriptionMapping;
import de.ehealth.project.letitrip_beta.view.MainActivity;

public class RecipeFragment extends Fragment {

    private FragmentChanger mListener;
    private LayoutInflater mInflater;
    private Recipe mSelectedRecipe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_recipe, container, false);
        mInflater = inflater;

        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getView() != null) { //if the fragment gets changed before the task complete, the view becomes a null object reference.
            Log.d("Recipe", "recipe started");
            RecipeDatabase recipeDb = new RecipeDatabase(getActivity());
            List<Recipe> recipeList = recipeDb.getAllRecipes();
            Log.d("Recipe", "" + recipeList.size());
            for (Recipe recipe : recipeList) {
                LinearLayout placeHolder = new LinearLayout(view.findViewById(R.id.scrollViewRecipe).getContext());
                mInflater.inflate(R.layout.recipe_view, placeHolder);
                ((LinearLayout) view.findViewById(R.id.layoutRecipe)).addView(placeHolder);

                ImageView imgRecipe = (ImageView) placeHolder.findViewById(R.id.imgRecipe);
                TextView txtRecipeSubHeading = (TextView) placeHolder.findViewById(R.id.txtRecipeSubheading);
                TextView txtRecipeBody = (TextView) placeHolder.findViewById(R.id.txtRecipeBody);

                new DownloadImageTask(imgRecipe).execute(recipe.getPic());
                txtRecipeSubHeading.setText(recipe.getName());
                String type = recipe.getType();
                if(type.equals("breakfast"))
                    type = "Fr\u00fchstück";
                else if(type.equals("lunch"))
                    type = "Mittagessen";
                else if(type.equals("dinner"))
                    type = "Abendessen";
                txtRecipeBody.setText(type + " mit " + recipe.getKcal() + " kcal");
                final Recipe clickedRecipe = recipe;
                placeHolder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (getActivity() instanceof FragmentChanger) {

                            RecipeWrapper.selectedRecipe = clickedRecipe;

                            FragmentChanger changer = (FragmentChanger) getActivity();
                            changer.changeFragment(MainActivity.FragmentName.RECIPE_DETAIL);

                        } else {
                            Log.e("Error", "Can't bind onClickListener for showing Recipe");
                        }

                    }

                });
            }
        }
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
