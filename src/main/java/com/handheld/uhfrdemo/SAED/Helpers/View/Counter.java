package com.handheld.uhfrdemo.SAED.Helpers.View;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Counter {

    /**
     * A normal timer that tacks any view and head it then displays the timer and when it is finished
     * timer heading and view visible
     *
     * @param _tv      timer TextView
     * @param view     view that you need to head it
     * @param duration Timer duration
     */
    public static void CountTime(TextView _tv, View view, long duration) {
        new CountDownTimer(duration, 1000) {

            public void onTick(long millisUntilFinished) {
                _tv.setVisibility(View.VISIBLE);
                view.setVisibility(View.INVISIBLE);
                _tv.setText(new SimpleDateFormat("mm:ss", Locale.ENGLISH).format(new Date(millisUntilFinished)));
                //   _tv.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                _tv.setVisibility(View.INVISIBLE);
                view.setVisibility(View.VISIBLE);
            }
        }.start();
    }

}
