package de.ehealth.project.letitrip_beta.model.Fitbit_Tracker.FitBitUserMovement;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Summary {

    @SerializedName("activityCalories")
    @Expose
    private String activityCalories;
    @SerializedName("caloriesBMR")
    @Expose
    private String caloriesBMR;
    @SerializedName("caloriesOut")
    @Expose
    private String caloriesOut;
    @SerializedName("steps")
    @Expose
    private String steps;

    /**
     *
     * @return
     * The activityCalories
     */
    public String getActivityCalories() {
        return activityCalories;
    }

    /**
     *
     * @param activityCalories
     * The activityCalories
     */
    public void setActivityCalories(String activityCalories) {
        this.activityCalories = activityCalories;
    }

    /**
     *
     * @return
     * The caloriesBMR
     */
    public String getCaloriesBMR() {
        return caloriesBMR;
    }

    /**
     *
     * @param caloriesBMR
     * The caloriesBMR
     */
    public void setCaloriesBMR(String caloriesBMR) {
        this.caloriesBMR = caloriesBMR;
    }

    /**
     *
     * @return
     * The caloriesOut
     */
    public String getCaloriesOut() {
        return caloriesOut;
    }

    /**
     *
     * @param caloriesOut
     * The caloriesOut
     */
    public void setCaloriesOut(String caloriesOut) {
        this.caloriesOut = caloriesOut;
    }

    /**
     *
     * @return
     * The steps
     */
    public String getSteps() {
        return steps;
    }

    /**
     *
     * @param steps
     * The steps
     */
    public void setSteps(String steps) {
        this.steps = steps;
    }

}