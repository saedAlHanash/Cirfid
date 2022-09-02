package com.handheld.uhfrdemo.SAED.UI.Fragments.Client;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.handheld.uhfr.R;
import com.handheld.uhfrdemo.SAED.Helpers.Converters.DateConverter;
import com.handheld.uhfrdemo.SAED.Helpers.NoteMessage;
import com.handheld.uhfrdemo.SAED.Helpers.system.GetPermissions;
import com.handheld.uhfrdemo.SAED.Helpers.system.HardWar;
import com.handheld.uhfrdemo.SAED.UI.Activities.ClientActivity;
import com.handheld.uhfrdemo.SAED.ViewModels.All;
import com.handheld.uhfrdemo.SAED.ViewModels.MyViewModel;
import com.handheld.uhfrdemo.SAED.ViewModels.Report;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;


@SuppressLint("NonConstantResourceId")
public class InventoryFragment extends Fragment implements View.OnClickListener,
        ClientActivity.OnReadTag {

    //region GLOBAL VAR

    //region Views
    Spinner warehousesSpinner;
    Spinner inventorySpinner;

    TextView expected;
    TextView scanned;
    TextView notFound;
    TextView undefined;

    Button read;
    Button stop;
    Button report;
    ProgressBar progressBar;

    //endregion

    //region spinners list
    ArrayList<String> listStringWarehouses = new ArrayList<>();
    ArrayList<String> listStringInventory = new ArrayList<>();
    ArrayAdapter<String> adapterWarehouses;
    ArrayAdapter<String> adapterInventory;

    //endregion

    //region lists models
    ArrayList<All> list;
    ArrayList<All.Location> listLocations;
    ArrayList<All.Epc> listEpc;

    ArrayList<String> listStringEpc = new ArrayList<>();
    ArrayList<String> scannedList = new ArrayList<>();
    ArrayList<String> undefinedList = new ArrayList<>();

    //endregion

    ClientActivity myActivity;
    MyViewModel myViewModel;
    View view;

    //endregion

    private static final String TAG = "InventoryFragment";

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myActivity = (ClientActivity) requireActivity();
        myViewModel = myActivity.myViewModel;
        view = inflater.inflate(R.layout.fragment_inventory1, container, false);
        initViews();

        listeners();

        getAll();

        return view;
    }

    private void initViews() {
        warehousesSpinner = view.findViewById(R.id.warehouses_spinner);
        inventorySpinner = view.findViewById(R.id.inventory_spinner);
        expected = view.findViewById(R.id.expected);
        scanned = view.findViewById(R.id.scaned);
        notFound = view.findViewById(R.id.not_found);
        undefined = view.findViewById(R.id.undefined);
        read = view.findViewById(R.id.read);
        stop = view.findViewById(R.id.stop);
        report = view.findViewById(R.id.report);
        progressBar = view.findViewById(R.id.progressIndicator);
    }

    void listeners() {

        warehousesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initInventorySpinners(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        inventorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initListEpc(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        read.setOnClickListener(this);
        stop.setOnClickListener(this);
        report.setOnClickListener(this);
    }

    //region data socket

    void getAll() {
        startLoading();
        myViewModel.getAll(myActivity.socket);
        myViewModel.allLiveData.observe(myActivity, observer);
    }

    final Observer<ArrayList<All>> observer = list -> {
        if (!isAdded())
            return;
        endLoading();

        if (list == null || list.isEmpty())
            return;

        this.list = list;

        initWarehousesSpinners();
    };

    //endregion

    //region adapters spinner and init list

    // 1
    void initWarehousesSpinners() {

        listStringWarehouses.clear();
        for (All all : list)
            listStringWarehouses.add(all.name);

        adapterWarehouses = new ArrayAdapter<>(myActivity,
                R.layout.item_spinner, R.id.textView, listStringWarehouses);

        adapterWarehouses.setDropDownViewResource(R.layout.item_spinner_drop);
        warehousesSpinner.setAdapter(adapterWarehouses);
    }

    //2
    void initInventorySpinners(int id) {

        if (list == null || list.isEmpty())
            return;

        listLocations = list.get(id).locations;

        if (listLocations == null)
            return;

        listStringInventory.clear();
        for (All.Location location : listLocations)
            listStringInventory.add(location.name);

        adapterInventory = new ArrayAdapter<>(myActivity,
                R.layout.item_spinner, R.id.textView, listStringInventory);
        adapterInventory.setDropDownViewResource(R.layout.item_spinner_drop);

        inventorySpinner.setAdapter(adapterInventory);
    }

    //3
    void initListEpc(int id) {
        if (listLocations == null)
            return;
        listEpc = listLocations.get(id).epcs;
        listStringEpc.clear();

        if (listEpc == null)
            return;

        for (All.Epc epc : listEpc)
            listStringEpc.add(epc.epc);

        expected.setText(String.valueOf(listEpc.size()));
    }

    //endregion

    //region Handlers

    private final Handler handler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    scanned.setText(String.valueOf(scannedList.size()));
                    notFound.setText(String.valueOf(listStringEpc.size()));
                    break;
                }
                case 1: {
                    undefined.setText(String.valueOf(undefinedList.size()));
                    notFound.setText(String.valueOf(listStringEpc.size()));
                    break;
                }
                case 2: {
                    NoteMessage.showSnackBar(myActivity, "تم قراءة جميع العناصر المتوقعة وإيقاف المسح");
                    break;
                }
            }

        }

    };

    //endregion

    //region show hied
    void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    void endLoading() {
        progressBar.setVisibility(View.GONE);
    }

    //endregion

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

            case R.id.report: {

                sendReport();

                break;
            }
        }
    }

    private void sendReport() {
        startLoading();
        myViewModel.sendReport(myActivity.socket, createReport());
        myViewModel.sendReportLiveData.observe(myActivity, doneSend -> {

            if (doneSend == null || !isAdded())
                return;

            endLoading();

            if (doneSend)
                NoteMessage.showSnackBar(myActivity, "تم ارسال التقرير بنجاح");
            else
                NoteMessage.showSnackBar(myActivity, "format error ");

        });
    }

    private Report createReport() {
        Report report = new Report();
        report.setWid(list.get(warehousesSpinner.getSelectedItemPosition()).id);
        report.setDt(new Date());
        report.setLid(listLocations.get(inventorySpinner.getSelectedItemPosition()).id);
        report.setUsr(HardWar.getIMEINumber(myActivity));

        report.setEpcs1(new ArrayList<>());
        report.setEpcs2(scannedList);
        report.setEpcs3(undefinedList);
        report.setEpcs4(listStringEpc);

        return report;
    }

    void removeObserver() {
        if (myViewModel.allLiveData != null)
            myViewModel.allLiveData.removeObserver(observer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeObserver();
    }

    @Override
    public void onStop() {
        super.onStop();
        myActivity.stop();
    }

    @Override
    public void onRead(@NotNull String epc, int rssi) {

        if (listStringEpc.isEmpty()) {
            handler.sendEmptyMessage(2);
            myActivity.stop();
        }

        if (listStringEpc.contains(epc)) {
            scannedList.add(epc);
            listStringEpc.remove(epc);

            handler.sendEmptyMessage(0);

        } else if (!undefinedList.contains(epc)) {
            undefinedList.add(epc);
            handler.sendEmptyMessage(1);
        }
    }
}