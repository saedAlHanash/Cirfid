package com.handheld.uhfrdemo.SAED;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class SharedPreference {

    public static final String CASH_TRIP = "T_C";
    public static final String TOKEN = "token";
    public static final String MY_ID = "id";
    public static final String CASH_SHOW_INTRO = "s_i";
    public static final String MY_EMAIL = "my_e";
    public static final String MY_PHONE = "my_p";
    public static final String MY_NAME = "my_n";
    public static final String CASH_FIRE_TOKEN = "firebase_token";
    public static final String CASH_AVAILABLE = "c_a";
    public static final String COD_STAT = "c_c_s";
    public static final String SEND_FIR_TOKEN = "f_c_s_t";
    public static final String USERS = "u";
    private static final String USER_INFO = "u_i";
    private static final String DEACTIVATE = "dac";
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


//_______ cashing ____________________________________________________________

    /**
     * for cashing trip id <br>
     * call it when accept trip or stat
     *
     * @param id trip id from trip info
     */
    public static void CASH_TRIP(int id) {
        spEdit.putInt(CASH_TRIP, id).apply();
    }

    /**
     * for cashing firebase token in SharedPreference to send it to server
     *
     * @param token string firebase token
     */
    public static void CACHE_FIRE_TOKEN(String token) {
        spEdit.putString(CASH_FIRE_TOKEN, token).commit();
    }

    /**
     * to cashing if user watch intro <br>
     * call it after user click next button in intro activity
     */
    public static void CASH_SHOW_INTRO() {
        spEdit.putString(CASH_SHOW_INTRO, "true").apply();
    }

    /**
     * to cashing if update firebase token in server
     */
    public static void CASH_SEND_FIR_TOKEN() {
        spEdit.putString(SEND_FIR_TOKEN, "true").apply();
    }

    /**
     * to removing trip id from SharedPreference<br>
     * <u>call it if trip canceled or driver reject trip</u>
     */
    public static void REMOVE_CASH_TRIP() {
        spEdit.remove(CASH_TRIP).commit();
    }

    /**
     * for cashing if driver available
     */
    public static void CASH_AVAILABLE() {
        spEdit.putString(CASH_AVAILABLE, "true").apply();
    }

    /**
     * to removing driver  available stat
     */
    public static void REMOVE_AVAILABLE() {
        spEdit.remove(CASH_AVAILABLE).apply();
    }

    /**
     * for cash if user go to forget password fragment and put hes phone
     */
    public static void CASH_COD_STAT() {
        spEdit.putString(COD_STAT, "true").apply();
    }


    /**
     * if user use forget password with same phone before counter 10 min then continue counter time <br>
     * if user put another phone then rest counter
     *
     * @param phone string phone number to cash it
     */
    public static void CASH_PHONE_FORGET_PASS(String phone) {
        spEdit.putString("c_f_p_p", phone).apply();
    }

//______ checking ____________________________________________________________

    /**
     * if there trip id cashed
     *
     * @return true if there trip id
     */
    public static boolean IS_THERE_TRIP() {
        return sp.getInt(CASH_TRIP, -1) > 0;
    }

    /**
     * if send token to server
     */
    public static boolean IS_SEND_FIR_TOKEN() {

        return !sp.getString(SEND_FIR_TOKEN, "").isEmpty();

    }

    /**
     * if user watch intro
     */
    public static boolean IS_SHOW_INTRO() {
        return !sp.getString(CASH_SHOW_INTRO, "").isEmpty();
    }

    /**
     * if user login to system
     */
    public static boolean IS_THERE_ACCESS_TOKEN() {
        return !sp.getString(TOKEN, "").equals("");
    }

    /**
     * if driver was available before out the app
     */
    public static boolean IS_AVAILABLE() {
        return !sp.getString(CASH_AVAILABLE, "").isEmpty();
    }

    /**
     * للتحقق من وجود تم تغيير كلمة السر لفتح الواجهة المناسبة عند فتح التطبيق
     */
    public static boolean IS_THERE_COD() {
        return !sp.getString(COD_STAT, "").isEmpty();
    }

    /**
     * if set timer after resend cod
     */
    public static boolean IS_THERE_TIMER() {
        return !sp.getString("timer", "").isEmpty();
    }

//_____ remove ____________________________________________________________

    /**
     * for delete stat forget password cod
     */
    public static void REMOVE_COD_STAT() {
        spEdit.remove(COD_STAT).apply();
    }

    /**
     * remove timer after passing 10 min
     */
    public static void REMOVE_TIMER() {
        spEdit.remove("timer").apply();
    }

    public static void REMOVE_TOKEN() {
        spEdit.remove(TOKEN).apply();
    }

//_____ get ____________________________________________________________

    /**
     * get user id
     */
    public static int GET_TRIP_ID() {
        return sp.getInt(CASH_TRIP, 0);
    }

    /**
     * get phone number cashing after forget pass posses
     *
     * @return phone number as strin g
     */
    public static String GET_FORGET_PASS_PHONE() {
        return sp.getString("c_f_p_p", "");
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
