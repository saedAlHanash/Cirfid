package com.handheld.uhfrdemo.SAED.Helpers.system;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;

public class HardWar {

    //here is your method to get the IMEI Number by using the Context that you passed to your class
    @SuppressLint("MissingPermission")
    public static String getIMEINumber(Activity activity) {

        if (!GetPermissions.saedPermetion(activity))
            return "";

        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);

        @SuppressLint({"HardwareIds"})
        String imei = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            imei = tm.getImei();
        }
        else
            imei = tm.getDeviceId();

        return imei;
    }


}
