package de.ehealth.project.letitrip_beta.model.recipe;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    @SerializedName("cook_time")
    private Integer cookTime;
    private String difficulty;
    @SerializedName("date_added")
    private long dateAdded;
    private String kcal;
    private String portion;
    private String name;
    private String recipe;
    @SerializedName("preparation_time")
    private Integer preparationTime;
    private List<Ingredient> ingredients = new ArrayList<Ingredient>();
    private String id;
    private String pic;
    private String type;

    public void printRecipe(){
        Log.d("RECIPES", "Recipe:\nId: " + id + "\nName: " + name + "\nDifficulty: " +
            difficulty + "\nKcal: " + kcal + "\nPic: " + pic +
            "\nPortions: " + portion + "\nRecipe: " + recipe + "\nType: " + type +
            "\nCooking Time: " + cookTime + "\nDate added: " + dateAdded + "\nPreparation Time: " +
            preparationTime + "\nIngredients:\n");
        for(Ingredient ingredient : ingredients){
            Log.d("RECIPES", "Name: " + ingredient.getName());
            Log.d("RECIPES", "Amount: " + ingredient.getAmount());
            }
    }
    public long getDate_added() {
        return dateAdded;
    }

    public void setDate_added(long date_added) {
        this.dateAdded = date_added;
    }

    /**
     *
     * @return The cookTime
     */
    public Integer getCookTime() {
        return cookTime;
    }

    /**
     *
     * @param cookTime
     *            The cook_time
     */
    public void setCookTime(Integer cookTime) {
        this.cookTime = cookTime;
    }

    /**
     *
     * @return The difficulty
     */
    public String getDifficulty() {
        return difficulty;
    }

    /**
     *
     * @param difficulty
     *            The difficulty
     */
    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    /**
     *
     * @return The kcal
     */
    public String getKcal() {
        return kcal;
    }

    /**
     *
     * @param kcal
     *            The kcal
     */
    public void setKcal(String kcal) {
        this.kcal = kcal;
    }

    /**
     *
     * @return The portion
     */
    public String getPortion() {
        return portion;
    }

    /**
     *
     * @param portion
     *            The portion
     */
    public void setPortion(String portion) {
        this.portion = portion;
    }

    /**
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     *            The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return The recipe
     */
    public String getRecipe() {
        return recipe;
    }

    /**
     *
     * @param recipe
     *            The recipe
     */
    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    /**
     *
     * @return The preparationTime
     */
    public Integer getPreparationTime() {
        return preparationTime;
    }

    /**
     *
     * @param preparationTime
     *            The preparation_time
     */
    public void setPreparationTime(Integer preparationTime) {
        this.preparationTime = preparationTime;
    }

    /**
     *
     * @return The ingredients
     */
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    /**
     *
     * @param ingredients
     *            The ingredients
     */
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    /**
     *
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     *            The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return The pic
     */
    public String getPic() {
        return pic;
    }

    /**
     *
     * @param pic
     *            The pic
     */
    public void setPic(String pic) {
        this.pic = pic;
    }

    /**
     *
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     *            The type
     */
    public void setType(String type) {
        this.type = type;
    }
}
