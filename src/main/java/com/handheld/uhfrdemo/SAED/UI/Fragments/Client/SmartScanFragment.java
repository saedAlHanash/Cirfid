package com.handheld.uhfrdemo.SAED.UI.Fragments.Client;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.handheld.uhfr.R;
import com.handheld.uhfrdemo.SAED.UI.Activities.ClientActivity;

import org.jetbrains.annotations.NotNull;


@SuppressLint("NonConstantResourceId")
public class SmartScanFragment extends Fragment implements View.OnClickListener, ClientActivity.OnReadTag {
    View view;

    ImageView imageView8;
    EditText epc;

    Button read;
    Button stop;
    TextView rssiTv;

    ClientActivity myActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myActivity = (ClientActivity) requireActivity();

        view = inflater.inflate(R.layout.fragment_smart_scan, container, false);
        initViews();

        read.setOnClickListener(this);
        stop.setOnClickListener(this);

        return view;
    }

    private void initViews() {
        imageView8 = view.findViewById(R.id.imageView8);
        epc = view.findViewById(R.id.epc);
        read = view.findViewById(R.id.read);
        stop = view.findViewById(R.id.stop);
        rssiTv = view.findViewById(R.id.rssi);
    }


    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @SuppressLint("SetTextI18n")
        @Override
        public void handleMessage(Message msg) {
            if (msg.what > 0 && msg.what <= 7) {
                imageView8.setImageLevel(msg.what);
                rssiTv.setText("-" + rssi + "dB");
            }
        }
    };

    int rssi;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.read: {
                myActivity.read();
                break;
            }
            case R.id.stop: {

                myActivity.stop();
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myActivity.stop();
    }

    @Override
    public void onStop() {
        super.onStop();
        myActivity.stop();
    }

    @Override
    public void onRead(@NotNull String epc, int rssi) {

        if (!epc.equals(this.epc.getText().toString()))
            return;

        this.rssi = rssi;

        if (rssi < 60) {
            handler.sendEmptyMessage(0);
            return;
        }

        if (rssi > 60 && rssi < 70)
            handler.sendEmptyMessage(1);

        else if (rssi > 70 && rssi < 80)
            handler.sendEmptyMessage(2);

        else if (rssi > 80 && rssi < 90)
            handler.sendEmptyMessage(3);

        else if (rssi > 90 && rssi < 100)
            handler.sendEmptyMessage(4);

        else if (rssi > 100 && rssi < 110)
            handler.sendEmptyMessage(5);

        else if (rssi > 110)
            handler.sendEmptyMessage(6);

    }
}