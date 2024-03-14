package com.poulpinou.belotinator.core;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;

public class Utils {

    public static final int DEFAULT_VICTORY_POINTS = 3000;
    public static final String DATE_FORMAT = "dd-MM-yyyy - HH-mm";
    public static final int POINTS_CAPOT = 90;
    private static int screenHeight;

    /**
     * @param context used to get the WindowManager.
     * @return the vertical size of the Screen.
     */
    public static int getScreenHeight(@NonNull Context context) {
        if(screenHeight == 0){
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            Utils.screenHeight = size.y;
        }
        return screenHeight;
    }

    /**
     * @param dp value to convert.
     * @return The corresponding amount of pixels depending on the screen size.
     */
    public static int getPixelFromDp(int dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return Math.round(dp * (metrics.densityDpi / 160f));
    }

    public enum Result{
        IN_PROGRESS("in_progress"),
        TEAM_A_WON("team_A_won"),
        EQUALITY("equality"),
        TEAM_B_WON("team_B_won");

        private final String name;
        Result(String name){
            this.name = name;
        }

        /**
         * @return True if one of the two teams won, false otherwise.
         */
        public boolean isFinished(){
            return this == TEAM_A_WON || this == TEAM_B_WON;
        }

        /**
         * @return A unique ID used to save this enum in the JSON file.
         */
        public String getName(){
            return this.name;
        }

        /**
         * @param name Loaded result's name.
         * @return The corresponding value of Result instance.
         */
        public static Result fromId(String name){
            for(Result result : Result.values()){
                if(name.equals(result.getName())){
                    return result;
                }
            }
            return IN_PROGRESS;
        }
    }
}
