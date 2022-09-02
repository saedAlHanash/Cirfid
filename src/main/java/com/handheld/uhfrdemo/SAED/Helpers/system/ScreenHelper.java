package com.handheld.uhfrdemo.SAED.Helpers.system;

import android.app.Activity;
import android.view.WindowManager;

public class ScreenHelper {
    /**
     * To hide the status bar when calling
     *
     * @param activity The current activity
     */
    public static void hideStatusBar(Activity activity) {
        // Hide status bar
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * To make the status bar transparent when invoked
     *
     * @param activity The current activity
     */
    public static void statusBarTransparent(Activity activity) {
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }
}
