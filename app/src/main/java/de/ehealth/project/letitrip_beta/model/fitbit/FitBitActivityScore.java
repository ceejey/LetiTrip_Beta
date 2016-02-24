package de.ehealth.project.letitrip_beta.model.fitbit;

import android.app.Activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.ehealth.project.letitrip_beta.handler.database.FitBitUserDataSQLite;
import de.ehealth.project.letitrip_beta.handler.fitbit.FitBitGetJsonTask;
import de.ehealth.project.letitrip_beta.handler.fitbit.Oauth;

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

    public static int daysBetween(Calendar startDate, Calendar endDate) {
        Calendar date = (Calendar) startDate.clone();
        int daysBetween = 0;
        while (date.before(endDate)) {
            date.add(Calendar.DAY_OF_MONTH, 1);
            daysBetween++;
        }
        return daysBetween;
    }

    public static void calcActivtiyScore(Activity activity){
        int daysbetweenNowAndResetDate = 0;
        try {
            new FitBitGetJsonTask(Oauth.getmOauth(), FitBitGetJsonTask.ENDPOINT_MOVES,activity).execute().get();
        } catch(Exception ex){ ex.printStackTrace(); }

        // The date that was saved since the User wanted to Reset the ActivityScore
        if(!FitbitUserProfile.getmActiveUser().getmActScoreResetDate().equals("")) {
            Date d1 = new Date(FitbitUserProfile.getmActiveUser().getmActScoreResetDate());


            // Get the date today using Calendar object.
            Date today = Calendar.getInstance().getTime();
            // Using DateFormat format method we can create a string
            // representation of a date with the defined format.

            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(d1);
            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(today);
            // This is import if the user resetes the activity score !
            daysbetweenNowAndResetDate = daysBetween(cal2, cal1) - 1;
        }
        if(daysbetweenNowAndResetDate > 14 || daysbetweenNowAndResetDate <= 0 ){
            daysbetweenNowAndResetDate = 14;
        }
        if(daysbetweenNowAndResetDate != 0) {
            Calendar now = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String[] days = new String[daysbetweenNowAndResetDate];
            now.add(Calendar.DAY_OF_MONTH, -daysbetweenNowAndResetDate);
            for (int i = 0; i < days.length; i++) {
                days[i] = format.format(now.getTime());
                Summary sum = FitBitUserDataSQLite.getInstance(activity).getFitBitData(days[i]);
                mStepsAvg += Integer.parseInt(sum.getSteps());
                mCaloriesBMRAvg += Integer.parseInt(sum.getCaloriesBMR());
                mCaloriesOutAvg += Integer.parseInt(sum.getCaloriesOut());
                now.add(Calendar.DAY_OF_MONTH, 1);
            }
            mStepsAvg = mStepsAvg / daysbetweenNowAndResetDate;
            mCaloriesBMRAvg = mCaloriesBMRAvg / daysbetweenNowAndResetDate;
            mCaloriesOutAvg = mCaloriesOutAvg / daysbetweenNowAndResetDate;
            setUserAims();
            try {
                mActivtiyScoreSteps = mStepsAvg / mStepsAim;
                mActivtiyScoreCalories = mCaloriesOutAvg / mCaloriesAim;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Look at our Aim Table. We decide that our overall aim is an active User
     */
    public static void setUserAims(){
        int user_age = Integer.parseInt(FitbitUserProfile.getmActiveUser().getmAge());
        if(FitbitUserProfile.getmActiveUser().getmGender().contains("MALE")) {
            if (user_age == 18) {
                FitBitActivityScore.getmActivityScore().setmCaloriesAim(3200);
            }
            if (user_age >= 19 && user_age <= 35){
                FitBitActivityScore.getmActivityScore().setmCaloriesAim(3000);
            }
            if (user_age >= 36 && user_age <= 55){
                FitBitActivityScore.getmActivityScore().setmCaloriesAim(2800);
            }
            if (user_age >= 56 && user_age <= 75){
                FitBitActivityScore.getmActivityScore().setmCaloriesAim(2600);
            }
            if (user_age > 75){
                FitBitActivityScore.getmActivityScore().setmCaloriesAim(2400);
            }
        }
        else{

            if (user_age >= 18 && user_age <= 30){
                FitBitActivityScore.getmActivityScore().setmCaloriesAim(2400);
            }
            if (user_age >= 31 && user_age <= 60){
                FitBitActivityScore.getmActivityScore().setmCaloriesAim(2200);
            }
            if (user_age >= 61 ){
                FitBitActivityScore.getmActivityScore().setmCaloriesAim(2000);
            }
        }
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
