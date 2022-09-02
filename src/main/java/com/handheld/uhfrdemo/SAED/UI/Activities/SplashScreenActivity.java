package com.handheld.uhfrdemo.SAED.UI.Activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.handheld.uhfr.R;
import com.handheld.uhfrdemo.SAED.AppConfig.SharedPreference;

import java.util.Locale;

public class SplashScreenActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        SharedPreference.getInstance(this);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        new Handler().postDelayed((Runnable) this::startAuth, 2500);
    }

    private void startAuth() {
        Locale locale;
        if (SharedPreference.getLanguage().isEmpty()) {
            locale = new Locale("ar");
            SharedPreference.saveLanguage("ar");
        } else
            locale = new Locale(SharedPreference.getLanguage());

        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config,
                getResources().getDisplayMetrics());

        startActivity(new Intent(this, AuthActivity.class));
        this.finish();
    }
}