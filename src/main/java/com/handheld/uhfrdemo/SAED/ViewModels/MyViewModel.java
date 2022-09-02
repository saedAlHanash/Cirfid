package com.handheld.uhfrdemo.SAED.ViewModels;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.google.gson.Gson;
import com.handheld.uhfrdemo.SAED.Network.SaedSocket;

import java.util.ArrayList;

public class MyViewModel extends ViewModel {

    Gson gson = new Gson();
    public MutableLiveData<ArrayList<All>> allLiveData = new MutableLiveData<>();
    public MutableLiveData<Product> productLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> connectLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> sendReportLiveData = new MutableLiveData<>();

    public void getAll(SaedSocket socket) {
        if (allLiveData.getValue() != null) {
            allLiveData.postValue(allLiveData.getValue());
            return;
        }

        socket.send("1,");
    }

    public void getProduct(SaedSocket socket, String epc) {
        socket.send("0," + epc);
    }

    private static final String TAG = "MyViewModel";

    public void sendReport(SaedSocket socket, Report report) {

        sendReportLiveData = new MutableLiveData<>();

        String json = gson.toJson(report, Report.class);
        Log.d(TAG, "sendReport: " + json);
        socket.send("2," + json);
    }

}
