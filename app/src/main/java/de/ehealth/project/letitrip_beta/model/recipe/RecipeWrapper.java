package de.ehealth.project.letitrip_beta.model.recipe;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class RecipeWrapper {

    private List<Recipe> recipes;
    private static RecipeWrapper instance = new RecipeWrapper();
    public static Recipe selectedRecipe;

    private RecipeWrapper(){ }

    public static RecipeWrapper getInstance(){
        return instance;
    }

    public void fromJsonToRecipes(String json){
        Gson gson = new Gson();
        recipes = gson.fromJson(json, new TypeToken<List<Recipe>>(){}.getType());
    }

    public void printRecipes(){
        for(Recipe recipe : recipes){
            recipe.printRecipe();
        }
    }
    /**
     * @return the recipes
     */
    public List<Recipe> getRecipes() {
        return recipes;
    }

    /**
     * @param recipes
     *            the recipes to set
     */
    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }


}
