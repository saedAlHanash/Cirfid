package com.handheld.uhfrdemo.SAED.UI.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import com.handheld.uhfr.R;

import com.handheld.uhfrdemo.SAED.AppConfig.FC;
import com.handheld.uhfrdemo.SAED.Helpers.View.FTH;
import com.handheld.uhfrdemo.SAED.UI.Fragments.Auth.AuthFragment;


public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        FTH.replaceFadFragment(FC.AUTH_C, this, new AuthFragment());
    }

}