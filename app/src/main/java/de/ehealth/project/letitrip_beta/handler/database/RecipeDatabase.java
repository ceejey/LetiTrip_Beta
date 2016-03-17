package de.ehealth.project.letitrip_beta.handler.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import de.ehealth.project.letitrip_beta.model.recipe.Ingredient;
import de.ehealth.project.letitrip_beta.model.recipe.Recipe;

/**
 * This class is the database for recipes.
 */

public class RecipeDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "LetitripDB";
    public static final String TBL_RECIPE_NAME = "RecipeTbl";
    public static final String TBL_INGREDIENT_NAME = "IngredientTbl";

    public static final String REC_COLUMN0 = "id";
    public static final String REC_COLUMN1 = "difficulty";
    public static final String REC_COLUMN2 = "kcal";
    public static final String REC_COLUMN3 = "portion";
    public static final String REC_COLUMN4 = "name";
    public static final String REC_COLUMN5 = "recipe";
    public static final String REC_COLUMN6 = "preparationTime";
    public static final String REC_COLUMN7 = "pic";
    public static final String REC_COLUMN8 = "type";
    public static final String REC_COLUMN9 = "dateAdded";
    public static final String REC_COLUMN10 = "cookTime";

    public static final String ING_COLUMN0 = "id";
    public static final String ING_COLUMN1 = "name";
    public static final String ING_COLUMN2 = "amount";

    public RecipeDatabase(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TBL_RECIPE_NAME + " (" +
                REC_COLUMN0 + " TEXT PRIMARY KEY," +
                REC_COLUMN1 + " TEXT," +
                REC_COLUMN2 + " TEXT," +
                REC_COLUMN3 + " TEXT," +
                REC_COLUMN4 + " TEXT," +
                REC_COLUMN5 + " TEXT," +
                REC_COLUMN6 + " INTEGER," +
                REC_COLUMN7 + " TEXT," +
                REC_COLUMN8 + " TEXT," +
                REC_COLUMN9 + " INTEGER," +
                REC_COLUMN10 + " INTEGER" + ")");

        db.execSQL("create table if not exists " + TBL_INGREDIENT_NAME + " (" +
                ING_COLUMN0 + " TEXT," +
                ING_COLUMN1 + " TEXT," +
                ING_COLUMN2 + " TEXT," +
                "FOREIGN KEY(" + ING_COLUMN0 + ") REFERENCES " + TBL_RECIPE_NAME + "(" + REC_COLUMN0 + "))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TBL_RECIPE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_INGREDIENT_NAME);
        onCreate(db);
    }

    public boolean addData(Integer cookTime, String difficulty, long dateAdded, String kcal, String portion,
                           String name, String recipe, Integer preparationTime, List<Ingredient> ingredients,
                           String id, String pic, String type)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues recipeContentValues = new ContentValues();
        ContentValues ingredientContentValues = new ContentValues();

        recipeContentValues.put(REC_COLUMN0, id);
        recipeContentValues.put(REC_COLUMN1, difficulty);
        recipeContentValues.put(REC_COLUMN2, kcal);
        recipeContentValues.put(REC_COLUMN3, portion);
        recipeContentValues.put(REC_COLUMN4, name);
        recipeContentValues.put(REC_COLUMN5, recipe);
        recipeContentValues.put(REC_COLUMN6, preparationTime);
        recipeContentValues.put(REC_COLUMN7, pic);
        recipeContentValues.put(REC_COLUMN8, type);
        recipeContentValues.put(REC_COLUMN9, dateAdded);
        recipeContentValues.put(REC_COLUMN10, cookTime);

        long result = db.insert(TBL_RECIPE_NAME, null, recipeContentValues);


        for(Ingredient ingredient : ingredients) {
            ingredientContentValues.put(ING_COLUMN0, id);
            ingredientContentValues.put(ING_COLUMN1, ingredient.getName());
            ingredientContentValues.put(ING_COLUMN2, ingredient.getAmount());
            result = db.insert(TBL_INGREDIENT_NAME, null, ingredientContentValues);
        }

        return (result != -1);
    }

    /**
     * refreshes weather every full hour
     * @return cursor with the latest weather data
     */
    public List<Recipe> getRecipesBetween(long timeSince, long timeTill){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TBL_RECIPE_NAME + " where " + REC_COLUMN9 + " > " + timeSince + " and " + REC_COLUMN9 + "< " + timeTill, null);

        List<Recipe> recipes = new ArrayList<>();

        if (res.getCount() != 0) {
            res.moveToFirst();

            do{
                Recipe recipe = new Recipe();
                recipe.setId(res.getString(0));
                recipe.setDifficulty(res.getString(1));
                recipe.setKcal(res.getString(2));
                recipe.setPortion(res.getString(3));
                recipe.setName(res.getString(4));
                recipe.setRecipe(res.getString(5));
                recipe.setPreparationTime(res.getInt(6));
                recipe.setPic(res.getString(7));
                recipe.setType(res.getString(8));
                recipe.setDate_added(res.getInt(9));
                recipe.setCookTime(res.getInt(10));

                Cursor ingRes = db.rawQuery("select * from " + TBL_INGREDIENT_NAME + " where " + ING_COLUMN0 + " like " + recipe.getId() , null);
                List<Ingredient> ingredients = new ArrayList<>();
                if (ingRes.getCount() != 0) {
                    ingRes.moveToFirst();
                    do{
                        Ingredient ingredient = new Ingredient();
                        ingredient.setName(ingRes.getString(1));
                        ingredient.setAmount(ingRes.getString(2));

                        ingredients.add(ingredient);
                    } while (ingRes.moveToNext());
                }

                recipe.setIngredients(ingredients);
                recipes.add(recipe);
            } while (res.moveToNext());
        }

        res.close();

        return recipes;
    }

    public List<Recipe> getAllRecipes(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TBL_RECIPE_NAME, null);

        List<Recipe> recipes = new ArrayList<>();

        if (res.getCount() != 0) {
            res.moveToFirst();

            do{
                Recipe recipe = new Recipe();
                recipe.setId(res.getString(0));
                recipe.setDifficulty(res.getString(1));
                recipe.setKcal(res.getString(2));
                recipe.setPortion(res.getString(3));
                recipe.setName(res.getString(4));
                recipe.setRecipe(res.getString(5));
                recipe.setPreparationTime(res.getInt(6));
                recipe.setPic(res.getString(7));
                recipe.setType(res.getString(8));
                recipe.setDate_added(res.getInt(9));
                recipe.setCookTime(res.getInt(10));

                Cursor ingRes = db.rawQuery("select * from " + TBL_INGREDIENT_NAME + " where " + ING_COLUMN0 + " like " + recipe.getId() , null);
                List<Ingredient> ingredients = new ArrayList<>();
                if (ingRes.getCount() != 0) {
                    ingRes.moveToFirst();
                    do {
                        Ingredient ingredient = new Ingredient();
                        ingredient.setName(ingRes.getString(1));
                        ingredient.setAmount(ingRes.getString(2));

                        ingredients.add(ingredient);
                    } while (ingRes.moveToNext());
                }

                recipe.setIngredients(ingredients);
                recipes.add(recipe);
            } while (res.moveToNext());
        }

        res.close();

        return recipes;
    }

    public Recipe getRecipe(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TBL_RECIPE_NAME + " where " + REC_COLUMN0 + " like " + id, null);

        Recipe recipe = new Recipe();

        if (res.getCount() != 0) {
            res.moveToFirst();

            recipe.setId(res.getString(0));
            recipe.setDifficulty(res.getString(1));
            recipe.setKcal(res.getString(2));
            recipe.setPortion(res.getString(3));
            recipe.setName(res.getString(4));
            recipe.setRecipe(res.getString(5));
            recipe.setPreparationTime(res.getInt(6));
            recipe.setPic(res.getString(7));
            recipe.setType(res.getString(8));
            recipe.setDate_added(res.getInt(9));
            recipe.setCookTime(res.getInt(10));

            Cursor ingRes = db.rawQuery("select * from " + TBL_INGREDIENT_NAME + " where " + ING_COLUMN0 + " like " + recipe.getId() , null);

            List<Ingredient> ingredients = new ArrayList<>();

            if (ingRes.getCount() != 0) {
                ingRes.moveToFirst();
                Ingredient ingredient = new Ingredient();
                ingredient.setName(ingRes.getString(1));
                ingredient.setAmount(ingRes.getString(2));

                ingredients.add(ingredient);
            }

            ingRes.close();

            recipe.setIngredients(ingredients);
        } else return null;

        res.close();

        return recipe;
    }

}
