package com.handheld.uhfrdemo.SAED.Helpers.system;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

public class GetPermissions {


    private static final int PERMISSION_READ_STATE = 14521;

    /**
     * Request access permissions to call the phone
     *
     * @param activity The current activity
     * @return if get {true} permission gated successfully
     */
    public static boolean getCallPhonePermission(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                activity.requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 10);
            } else {
                return true;
            }
        }

        return false;
    }


    /**
     * Request access permissions to READ EXTERNAL STORAGE
     *
     * @param activity The current activity
     * @return if get {true} permission gated successfully
     */
    public static boolean checkPermeationREAD_EXTERNAL_STORAGE(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
                activity.requestPermissions(new String[]{permission}, 20);
            } else
                return true;
        }
        return false;

    }


    public static boolean checkPermeationLocation(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 40);
        } else {
            return true;
        }
        return false;
    }

    public static boolean saedPermetion(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_READ_STATE);
        else
            return true;

        return false;
    }
}

