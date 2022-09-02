package com.handheld.uhfrdemo.SAED.UI.Fragments.Client;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.handheld.uhfr.R;
import com.handheld.uhfrdemo.Fragment1_Inventory;
import com.handheld.uhfrdemo.SAED.AppConfig.FC;
import com.handheld.uhfrdemo.SAED.AppConfig.FN;
import com.handheld.uhfrdemo.SAED.AppConfig.SharedPreference;
import com.handheld.uhfrdemo.SAED.Helpers.View.FTH;
import com.handheld.uhfrdemo.SAED.Helpers.system.HardWar;
import com.handheld.uhfrdemo.SAED.UI.Activities.ClientActivity;
import com.handheld.uhfrdemo.SAED.UI.Fragments.Prosess.ScanFragment;

import java.util.Locale;


@SuppressLint("NonConstantResourceId")
public class ClientFragment extends Fragment implements View.OnClickListener {


    CardView smartScan;
    CardView inventory;
    CardView scan;
    CardView settings;

    View view;
    ClientActivity myActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myActivity = (ClientActivity) requireActivity();
        view = inflater.inflate(R.layout.fragment_client, container, false);
        initView();

        listeners();

        return view;
    }

    private void initView() {
        smartScan = view.findViewById(R.id.smart_scan);
        inventory = view.findViewById(R.id.inventory);
        scan = view.findViewById(R.id.scan);
        settings = view.findViewById(R.id.settings);
    }

    void listeners() {
        // المسح الذكي
        smartScan.setOnClickListener(this);
        // جرد المخزن
        inventory.setOnClickListener(this);
        // مسح منتج
        scan.setOnClickListener(this);
        //اعدادات
        settings.setOnClickListener(this);
    }

    void startSetting() {
        FTH.addFragmentUpFragment(FC.CLIENT_C, requireActivity(),
                new SettingsFragment(), FN.SETTING_FN);
    }

    void startInventory() {
        FTH.addFragmentUpFragment(FC.CLIENT_C, requireActivity(),
                new InventoryFragment(), FN.SETTING_FN);
    }

    void startScanFragment() {
        FTH.addFadFragmentUpFragment(FC.CLIENT_C, myActivity,
                new ScanFragment(), FN.SCAN_FN);
    }

    void startSmartScanFragment() {
        FTH.addFragmentUpFragment(FC.CLIENT_C, requireActivity(),
                new SmartScanFragment(), FN.SMART_SCAN_FN);
    }

        private static final String TAG = "ClientFragment";
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // المسح الذكي
            case R.id.smart_scan: {
                Log.d(TAG, "onCreateView: " + HardWar.getIMEINumber(requireActivity()));
                break;
            }
            // جرد المخزن
            case R.id.inventory: {

                startInventory();
                break;
            }
            // مسح منتج
            case R.id.scan: {
                startScanFragment();
                break;
            }
            //اعدادات
            case R.id.settings: {

                startSetting();
                break;
            }
        }
    }


}