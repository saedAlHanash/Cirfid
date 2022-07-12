package com.handheld.uhfrdemo.SAED;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.handheld.uhfr.R;

public class DialogSetIp extends Dialog {
    Activity activity;

    EditText ip;
    EditText port;
    Button save;

    public DialogSetIp(@NonNull Activity activity) {
        super(activity);
        this.activity = activity;
        setContentView(R.layout.dialog_ip);

        initView();

        listeners();
    }

    private void initView() {

        ip = findViewById(R.id.ip);
        port = findViewById(R.id.port);
        save = findViewById(R.id.save_ip);

    }

    void listeners() {
        save.setOnClickListener(v -> {

            if (!checkFealds())
                return;

            SharedPreference.getInstance(activity);
            SharedPreference.saveIp(ip.getText().toString());
            SharedPreference.savePort(Integer.parseInt(port.getText().toString()));
            Toast.makeText(activity, "done", Toast.LENGTH_SHORT).show();
            this.cancel();

        });
    }

    private boolean checkFealds() {
        if (ip.getText().toString().isEmpty()) {
            ip.setError("");
            return false;
        }
        if (port.getText().toString().isEmpty()) {
            port.setError("");
            return false;
        }
        return true;
    }


}
