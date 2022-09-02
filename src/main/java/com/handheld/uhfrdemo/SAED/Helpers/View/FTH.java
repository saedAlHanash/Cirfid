package com.handheld.uhfrdemo.SAED.Helpers.View;


import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.handheld.uhfr.R;

/**
 * all this methods work with {map_fragment_container}
 */
public class FTH {
    static FragmentManager fm;
    static FragmentTransaction ft;

    /**
     * Completely replace the fragment with another with container map_fragment_container
     *
     * @param fragmentActivity FragmentActivity
     * @param fragment         The new fragment
     */
    public static void replaceSlidFragment(@IdRes int container,
                                           FragmentActivity fragmentActivity,
                                           Fragment fragment) {
        ft = fragmentActivity.
                getSupportFragmentManager().
                beginTransaction().
                setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down);
        ft.replace(container, fragment);
        ft.commit();
    }

    public static void replaceFadFragment(@IdRes int container,
                                            FragmentActivity fragmentActivity,
                                            Fragment fragment) {
        ft = fragmentActivity.
                getSupportFragmentManager().
                beginTransaction().
                setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);
        ft.replace(container, fragment);
        ft.commit();
    }

    public static void replaceSlidFragmentTag(@IdRes int container,
                                          FragmentActivity fragmentActivity,
                                          Fragment fragment,String name) {
        ft = fragmentActivity.
                getSupportFragmentManager().
                beginTransaction().
                setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down);

        ft.replace(container, fragment,name);
        ft.commit();
    }

    public static void replaceFadFragmentTag(@IdRes int container,
                                          FragmentActivity fragmentActivity,
                                          Fragment fragment,String name) {
        ft = fragmentActivity.
                getSupportFragmentManager().
                beginTransaction().
                setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);

        ft.replace(container, fragment,name);
        ft.commit();
    }

    /**
     * replace the fragment with another and add to Back stack with container map_fragment_container
     *
     * @param fragmentActivity FragmentActivity
     * @param fragment         The new fragment
     */
    public static void addToStakeFragment(@IdRes int container,
                                          FragmentActivity fragmentActivity,
                                          Fragment fragment,
                                          String name) {
        ft = fragmentActivity.
                getSupportFragmentManager().
                beginTransaction().
                setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down);

        ft.replace(container, fragment,name);
        ft.attach(fragment);
        ft.addToBackStack(name);
        ft.commit();
    }

    /**
     * add fragment up fragment and add to Back stack with container map_fragment_container
     *
     * @param fragmentActivity FragmentActivity
     * @param fragment         The new fragment
     */
    public static void addFragmentUpFragment(@IdRes int container,
                                             FragmentActivity fragmentActivity,
                                             Fragment fragment,
                                             String name) {
        ft = fragmentActivity.
                getSupportFragmentManager().
                beginTransaction().
                setCustomAnimations(R.anim.slide_up, R.anim.slide_down, R.anim.slide_up, R.anim.slide_down);

        ft.add(container, fragment,name);
        ft.attach(fragment);
        ft.addToBackStack(name);
        ft.commit();
    }

    public static void addFadFragmentUpFragment(@IdRes int container,
                                                FragmentActivity fragmentActivity,
                                                Fragment fragment,
                                                String name) {
        ft = fragmentActivity.
                getSupportFragmentManager().
                beginTransaction().
                setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out);

        ft.add(container, fragment,name);
        ft.attach(fragment);
        ft.addToBackStack(name);
        ft.commit();
    }

    /**
     * Delete the named fragment from the stack
     *
     * @param fragmentActivity FragmentActivity
     * @param fragmentName     The new fragment
     */
    public static void popFragmentFromStack(FragmentActivity fragmentActivity, String fragmentName) {
        fragmentActivity.
                getSupportFragmentManager().
                popBackStackImmediate(fragmentName, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }


    public static void popTopStack(FragmentActivity fragmentActivity) {
        fragmentActivity.getSupportFragmentManager().popBackStack();
    }

    public static void popAllStack(FragmentActivity fragmentActivity) {
        FragmentManager fm = fragmentActivity.getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); i++)
            fm.popBackStack();
    }


    /**
     * get latest fragment on back Stack
     *
     * @param fragmentActivity FragmentActivity
     * @return fragment with type {@link Fragment}
     */
    public static Fragment getCurrentFragment(FragmentActivity fragmentActivity) {
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();

        String fragmentTag =
                fragmentManager.
                        getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();

        return fragmentManager.findFragmentByTag(fragmentTag);
    }

    /**
     * get name of latest fragment on back Stack
     *
     * @param fragmentActivity FragmentActivity
     * @return name fragment on top stack
     */
    public static String getCurrentFragmentName(FragmentActivity fragmentActivity) {
        FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
        String name = "";
        try {
            name = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        } catch (Exception ignore) {
        }

        return name;
    }

}
