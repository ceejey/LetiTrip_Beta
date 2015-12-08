package de.ehealth.project.letitrip_beta.model.fitbit;

import android.app.Activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.ehealth.project.letitrip_beta.handler.database.FitBitUserDataSQLite;
import de.ehealth.project.letitrip_beta.handler.task.fitbit.FitBitGetJsonTask;
import de.ehealth.project.letitrip_beta.handler.task.fitbit.Oauth;

/**
 * Created by Mirorn on 25.11.2015.
 */
public class FitBitActivityScore {

    private static double mActivtiyScoreSteps = 0;
    private static double mActivtiyScoreCalories = 0;
    private static double mStepsAvg = 0;
    private static double mCaloriesBMRAvg = 0;
    private static double mCaloriesOutAvg = 0;
    private static double mStepsAim = 10000; //10 000 set by default
    private static double mCaloriesAim = 4000; //4000 set by default
    private static FitBitActivityScore mActivityScore = new FitBitActivityScore();
    private FitBitActivityScore(){}

    public static FitBitActivityScore getmActivityScore(){
        return mActivityScore;
    }

    public void calcActivtiyScore(Activity activity){

        try {
            new FitBitGetJsonTask(Oauth.getmOauth(), FitBitGetJsonTask.ENDPOINT_MOVES,activity).execute().get();
        } catch(Exception ex){ ex.printStackTrace(); }
        Calendar now = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String[] days = new String[14];
        now.add(Calendar.DAY_OF_MONTH, -14);
        for (int i = 0; i < days.length; i++) {
            days[i] = format.format(now.getTime());
            Summary sum = FitBitUserDataSQLite.getInstance(activity).getFitBitData(days[i]);
            mStepsAvg += Integer.parseInt(sum.getSteps());
            mCaloriesBMRAvg += Integer.parseInt(sum.getCaloriesBMR());
            mCaloriesOutAvg += Integer.parseInt(sum.getCaloriesOut());
            now.add(Calendar.DAY_OF_MONTH, 1);
        }
        mStepsAvg = mStepsAvg /14;
        mCaloriesBMRAvg = mCaloriesBMRAvg /14;
        mCaloriesOutAvg = mCaloriesOutAvg /14;

        try {
            mActivtiyScoreSteps = mStepsAvg / mStepsAim;
            mActivtiyScoreCalories = mCaloriesOutAvg / mCaloriesAim;
        } catch(Exception ex){ ex.printStackTrace(); }

    }

    public static double getmStepsAim() {
        return mStepsAim;
    }

    public static void setmStepsAim(double mStepsAim) {
        FitBitActivityScore.mStepsAim = mStepsAim;
    }

    public static double getmActivtiyScoreSteps() {
        return mActivtiyScoreSteps;
    }

    public static void setmActivtiyScoreSteps(double mActivtiyScoreSteps) {
        FitBitActivityScore.mActivtiyScoreSteps = mActivtiyScoreSteps;
    }

    public static double getmStepsAvg() {
        return mStepsAvg;
    }

    public static void setmStepsAvg(double mStepsAvg) {
        FitBitActivityScore.mStepsAvg = mStepsAvg;
    }

    public static double getmCaloriesBMRAvg() {
        return mCaloriesBMRAvg;
    }

    public static void setmCaloriesBMRAvg(double mCaloriesBMRAvg) {
        FitBitActivityScore.mCaloriesBMRAvg = mCaloriesBMRAvg;
    }

    public static double getmCaloriesOutAvg() {
        return mCaloriesOutAvg;
    }

    public static void setmCaloriesOutAvg(double mCaloriesOutAvg) {
        FitBitActivityScore.mCaloriesOutAvg = mCaloriesOutAvg;
    }

    public static double getmCaloriesAim() {
        return mCaloriesAim;
    }

    public static void setmCaloriesAim(double mCaloriesAim) {
        FitBitActivityScore.mCaloriesAim = mCaloriesAim;
    }

    public static void setmActivityScore(FitBitActivityScore mActivityScore) {
        FitBitActivityScore.mActivityScore = mActivityScore;
    }

    public static double getmActivtiyScoreCalories() {
        return mActivtiyScoreCalories;
    }

    public static void setmActivtiyScoreCalories(double mActivtiyScoreCalories) {
        FitBitActivityScore.mActivtiyScoreCalories = mActivtiyScoreCalories;
    }
}
