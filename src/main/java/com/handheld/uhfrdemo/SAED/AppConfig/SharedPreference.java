package com.handheld.uhfrdemo.SAED.AppConfig;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;


public class SharedPreference {

    private static final String IP = "ip";
    private static final String PORT = "port";


    public static SharedPreferences sp;
    public static SharedPreferences.Editor spEdit;


    public static void getInstance(Context context) {
        if (sp == null) {
            sp = PreferenceManager.getDefaultSharedPreferences(context);
            spEdit = sp.edit();
            spEdit.apply();
        }
    }


    public static void saveIp(String ip) {
        spEdit.putString(IP, ip).commit();
    }

    public static void savePort(int port) {
        spEdit.putInt(PORT, port).commit();
    }

    public static String getIp() {
        return sp.getString(IP, "");
    }

    public static int getPort() {
        return sp.getInt(PORT, 0);
    }

    public static void saveLanguage(String l) {
        spEdit.putString("lang", l).commit();
    }

    public static String getLanguage() {
        return sp.getString("lang", "en");
    }

}
