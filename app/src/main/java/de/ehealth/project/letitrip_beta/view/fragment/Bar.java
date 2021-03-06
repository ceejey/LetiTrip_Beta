package de.ehealth.project.letitrip_beta.view.fragment;


import android.app.Activity;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import de.ehealth.project.letitrip_beta.R;
import de.ehealth.project.letitrip_beta.model.settings.UserSettings;
import de.ehealth.project.letitrip_beta.view.MainActivity;

public class Bar extends Fragment {

    private FragmentChanger mListener;
    public static void setClickOffset(int clickOffset) {
        BarHandler.clickOffset = clickOffset;
    }

    public static ImageView mImgDashboard;
    public static ImageView mImgSession;
    public static ImageView mImgRecipe;
    public static ImageView mImgSettings;

    /**
     * this class changes the colors of the bar items and set the clickable area for them.
     */
    public static final class BarHandler {
        private static String selBtn = "";

        private static int clickOffset = Integer.parseInt(UserSettings.getmActiveUser().getmClickOffsetForBarSensibility());


        //private static float clickOffset = 0.6f;
        private static int getWidth(RelativeLayout layout) {
            return layout.getRight() - layout.getLeft();
        }

        public static void changeButtonColors(ImageView dashboard, ImageView session, ImageView recipe,
                                              ImageView settings, int dashBoardColor, int sessionColor,
                                              int recipeColor, int settingsColor) {

            dashboard.setColorFilter(dashBoardColor, PorterDuff.Mode.MULTIPLY);
            session.setColorFilter(sessionColor, PorterDuff.Mode.MULTIPLY);
            recipe.setColorFilter(recipeColor, PorterDuff.Mode.MULTIPLY);
            settings.setColorFilter(settingsColor, PorterDuff.Mode.MULTIPLY);
        }
    }


    /**
     * This method shows the bar items
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bar, container, false);

        final ImageView imgDashboard = (ImageView) view.findViewById(R.id.imgDashboard);
        final ImageView imgSession = (ImageView) view.findViewById(R.id.imgSession);
        final ImageView imgRecipe = (ImageView) view.findViewById(R.id.imgRecipe);
        final ImageView imgSettings = (ImageView) view.findViewById(R.id.imgSettings);

        mImgDashboard = imgDashboard;
        mImgSession = imgSession;
        mImgRecipe = imgRecipe;
        mImgSettings = imgSettings;

        final RelativeLayout btnDashboard = (RelativeLayout) view.findViewById(R.id.btnDashboard);
        final RelativeLayout btnSession = (RelativeLayout) view.findViewById(R.id.btnSession);
        final RelativeLayout btnRecipe = (RelativeLayout) view.findViewById(R.id.btnRecipe);
        final RelativeLayout btnSettings = (RelativeLayout) view.findViewById(R.id.btnSettings);

        //Initialize with colored Dashboard icon.
        imgDashboard.setColorFilter(0xffffa726, PorterDuff.Mode.MULTIPLY);
        BarHandler.selBtn = "Dashboard";

        //Choose which button to trigger with x coordinates of the touch, change the fragment and the color of the button.
        btnDashboard.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    if (BarHandler.selBtn.equals("Dashboard")) {
                        int width = BarHandler.getWidth(btnDashboard);
                        if (event.getX() > width / 2.0F + width / 2.0F * BarHandler.clickOffset) {
                            BarHandler.selBtn = "Session";
                            updateActivity(MainActivity.FragmentName.SESSION);
                            BarHandler.changeButtonColors(imgDashboard, imgSession, imgRecipe, imgSettings,
                                    0xffffffff, 0xffffa726, 0xffffffff, 0xffffffff);
                        }
                        else {
                            BarHandler.selBtn = "Dashboard";
                            updateActivity(MainActivity.FragmentName.DASHBOARD);
                            BarHandler.changeButtonColors(imgDashboard, imgSession, imgRecipe, imgSettings,
                                    0xffffa726, 0xffffffff, 0xffffffff, 0xffffffff);
                        }

                    } else {
                        BarHandler.selBtn = "Dashboard";
                        updateActivity(MainActivity.FragmentName.DASHBOARD);
                        BarHandler.changeButtonColors(imgDashboard, imgSession, imgRecipe, imgSettings,
                                0xffffa726, 0xffffffff, 0xffffffff, 0xffffffff);
                    }
                }
                return true;
            }
        });

        btnSession.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    if (BarHandler.selBtn.equals("Session")) {
                        int width = BarHandler.getWidth(btnSession);
                        if (event.getX() > width / 2.0F + width / 2.0F * BarHandler.clickOffset) {
                            BarHandler.selBtn = "Recipe";
                            updateActivity(MainActivity.FragmentName.RECIPE);
                            BarHandler.changeButtonColors(imgDashboard, imgSession, imgRecipe, imgSettings,
                                    0xffffffff, 0xffffffff, 0xffffa726, 0xffffffff);
                        } else if (event.getX() < width / 2.0F - width / 2.0F * BarHandler.clickOffset) {
                            BarHandler.selBtn = "Dashboard";
                            updateActivity(MainActivity.FragmentName.DASHBOARD);
                            BarHandler.changeButtonColors(imgDashboard, imgSession, imgRecipe, imgSettings,
                                    0xffffa726, 0xffffffff, 0xffffffff, 0xffffffff);
                        } else {
                            BarHandler.selBtn = "Session";
                            //updateActivity(MainActivity.FragmentName.SESSION);
                            updateActivity(MainActivity.FragmentName.SESSION_OVERVIEW);
                            BarHandler.changeButtonColors(imgDashboard, imgSession, imgRecipe, imgSettings,
                                    0xffffffff, 0xffffa726, 0xffffffff, 0xffffffff);
                        }

                    } else {
                        BarHandler.selBtn = "Session";
                        //updateActivity(MainActivity.FragmentName.SESSION);
                        updateActivity(MainActivity.FragmentName.SESSION_OVERVIEW);
                        BarHandler.changeButtonColors(imgDashboard, imgSession, imgRecipe, imgSettings,
                                0xffffffff, 0xffffa726, 0xffffffff, 0xffffffff);
                    }
                }
                return true;
            }
        });

        btnRecipe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    if (BarHandler.selBtn.equals("Recipe")) {
                        int width = BarHandler.getWidth(btnRecipe);
                        if (event.getX() > width / 2.0F + width / 2.0F * BarHandler.clickOffset) {
                            BarHandler.selBtn = "Settings";
                            updateActivity(MainActivity.FragmentName.SETTINGS);
                            BarHandler.changeButtonColors(imgDashboard, imgSession, imgRecipe, imgSettings,
                                    0xffffffff, 0xffffffff, 0xffffffff, 0xffffa726);
                        } else if (event.getX() < width / 2.0F - width / 2.0F * BarHandler.clickOffset) {
                            BarHandler.selBtn = "Session";
                            updateActivity(MainActivity.FragmentName.SESSION);
                            BarHandler.changeButtonColors(imgDashboard, imgSession, imgRecipe, imgSettings,
                                    0xffffffff, 0xffffa726, 0xffffffff, 0xffffffff);
                        } else {
                            BarHandler.selBtn = "Recipe";
                            updateActivity(MainActivity.FragmentName.RECIPE);
                            BarHandler.changeButtonColors(imgDashboard, imgSession, imgRecipe, imgSettings,
                                    0xffffffff, 0xffffffff, 0xffffa726, 0xffffffff);
                        }

                    } else {
                        BarHandler.selBtn = "Recipe";
                        updateActivity(MainActivity.FragmentName.RECIPE);
                        BarHandler.changeButtonColors(imgDashboard, imgSession, imgRecipe, imgSettings,
                                0xffffffff, 0xffffffff, 0xffffa726, 0xffffffff);
                    }
                }
                return true;
            }
        });

        btnSettings.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    if (BarHandler.selBtn.equals("Settings")) {
                        int width = BarHandler.getWidth(btnSettings);
                        if (event.getX() < width / 2.0F - width / 2.0F * BarHandler.clickOffset) {
                            BarHandler.selBtn = "Recipe";
                            updateActivity(MainActivity.FragmentName.RECIPE);
                            BarHandler.changeButtonColors(imgDashboard, imgSession, imgRecipe, imgSettings,
                                    0xffffffff, 0xffffffff, 0xffffa726, 0xffffffff);
                        } else {
                            BarHandler.selBtn = "Settings";
                            updateActivity(MainActivity.FragmentName.SETTINGS);
                            BarHandler.changeButtonColors(imgDashboard, imgSession, imgRecipe, imgSettings,
                                    0xffffffff, 0xffffffff, 0xffffffff, 0xffffa726);
                        }

                    } else {
                        BarHandler.selBtn = "Settings";
                        updateActivity(MainActivity.FragmentName.SETTINGS);
                        BarHandler.changeButtonColors(imgDashboard, imgSession, imgRecipe, imgSettings,
                                0xffffffff, 0xffffffff, 0xffffffff, 0xffffa726);
                    }
                }
                return true;
            }
        });

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
