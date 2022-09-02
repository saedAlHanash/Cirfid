package com.handheld.uhfrdemo.SAED.UI.Fragments.Client;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.handheld.uhfr.R;
import com.handheld.uhfrdemo.SAED.AppConfig.SharedPreference;

import java.util.Hashtable;


public class SettingsFragment extends Fragment {

    EditText ip;
    EditText port;
    Button save;

    View view;

    private final Hashtable<Integer, Integer> mAntMap = new Hashtable<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_settings1, container, false);
        initViews();

        initIpEditText();

        initIpAnPort();

        listeners();

        return view;
    }

    private void initViews() {
        ip = view.findViewById(R.id.ip);
        port = view.findViewById(R.id.port);
        save = view.findViewById(R.id.save);
    }

    void listeners() {
        save.setOnClickListener(saveListener);
    }


    private final View.OnClickListener saveListener = v -> {
        saveInFile();
    };




    //saed :
    void initIpEditText() {
        InputFilter[] filters = new InputFilter[1];
        filters[0] = (source, start, end, dest, dstart, dend) -> {
            if (end > start) {
                String destTxt = dest.toString();
                String resultingTxt = destTxt.substring(0, dstart)
                        + source.subSequence(start, end)
                        + destTxt.substring(dend);
                if (!resultingTxt
                        .matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                    return "";
                } else {
                    String[] splits = resultingTxt.split("\\.");
                    for (String split : splits) {
                        if (Integer.parseInt(split) > 255) {
                            return "";
                        }
                    }
                }
            }
            return null;
        };

        ip.setFilters(filters);
    }

    void initIpAnPort() {

        int mPort = SharedPreference.getPort();
        String mIp = SharedPreference.getIp();

        if (!mIp.isEmpty())
            this.ip.setText(mIp);

        if (mPort != 0)
            this.port.setText(String.valueOf(mPort));
    }

    void saveInFile() {
        String mIp = ip.getText().toString();
        int mPort = Integer.parseInt(port.getText().toString());

        if (mIp.isEmpty()) {
            ip.setError("required");
            return;
        }
        if (mPort == 0) {
            port.setError("required");
            return;
        }

        SharedPreference.saveIp(mIp);
        SharedPreference.savePort(mPort);

        Toast.makeText(requireActivity(), getResources().getString(R.string.done), Toast.LENGTH_SHORT).show();

        requireActivity().onBackPressed();
    }
}