package com.handheld.uhfrdemo.SAED.UI.Fragments.Prosess;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import android.widget.Spinner;
import android.widget.TextView;


import com.handheld.uhfr.R;

import com.handheld.uhfrdemo.SAED.Adadpter.AdapterItemEpc;

import com.handheld.uhfrdemo.SAED.UI.Activities.ClientActivity;
import com.handheld.uhfrdemo.SAED.ViewModels.MyViewModel;
import com.handheld.uhfrdemo.SAED.ViewModels.Product;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


@SuppressLint("NonConstantResourceId")
public class ScanFragment extends Fragment implements View.OnClickListener,
        AdapterView.OnItemSelectedListener, ClientActivity.OnReadTag, AdapterItemEpc.OnItemClicked {


    ClientActivity myActivity;
    Button btnStart;
    Button btnStop;
    Button btnClear;
    Spinner spinnerTypeScan;
    TextView typeScanTv;


    //region base

    RecyclerView recyclerView;

    AdapterItemEpc adapter;
    ArrayList<String> sentEpc = new ArrayList<>();
    MyViewModel myViewModel;
    View view;
    //endregion

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myActivity = (ClientActivity) requireActivity();
        myViewModel = myActivity.myViewModel;
        myViewModel.productLiveData = new MutableLiveData<>();

        view = inflater.inflate(R.layout.fragment_scan, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        initAdapter();

        initView();

        return view;
    }


    final Observer<Product> Observer = product -> {
        if (!isAdded())
            return;
        if (product == null)
            return;

        sentEpc.add(product.epc);
        adapter.insertItem(product);

    };

    void initView() {

        btnStart = view.findViewById(R.id.read);
        btnStop = view.findViewById(R.id.stop);
        btnClear = view.findViewById(R.id.clean);
        spinnerTypeScan = view.findViewById(R.id.spinner_tybe_scan);
        typeScanTv = view.findViewById(R.id.scan_type_tv);


        btnStart.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        typeScanTv.setOnClickListener(this);

        spinnerTypeScan.setOnItemSelectedListener(this);
    }

    void initAdapter() {

        if (adapter == null)
            adapter = new AdapterItemEpc(myActivity, new ArrayList<>());

        adapter.setOnItemClicked(this);

        recyclerView.setAdapter(adapter);
    }

    void observeProduct() {
        myViewModel.productLiveData.observe(myActivity, Observer);
    }

    void removeObserveProduct() {
        myViewModel.productLiveData.removeObserver(Observer);
    }

    int x = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.read: {
                myActivity.read();
//                x += 1;
//                myViewModel.getProduct(myActivity.socket, "000" + x);
                break;
            }

            case R.id.stop: {
                myActivity.stop();
                break;
            }

            case R.id.clean: {
                break;
            }

            case R.id.scan_type_tv: {
                spinnerTypeScan.performClick();
                break;
            }
        }
    }

    @Override
    public void onDestroyView() {
        if (myActivity.isStart) {
            myActivity.isStart = false;
            ClientActivity.mUhfrManager.stopTagInventory();
        }

        super.onDestroyView();
    }

    @Override
    public void onPause() {
        super.onPause();
        removeObserveProduct();
    }

    @Override
    public void onResume() {
        super.onResume();
        observeProduct();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        myActivity.stop();

        if (ClientActivity.mUhfrManager != null)
            ClientActivity.mUhfrManager.setCancleInventoryFilter();
    }

    @Override
    public void onStop() {
        super.onStop();

        myActivity.stop();
    }

    @Override
    public void onRead(@NotNull String epc, int rssi) {
        if (sentEpc.contains(epc))
            return;

        myViewModel.getProduct(myActivity.socket, epc);
    }

    @Override
    public void onItemClicked(int position, ArrayList<Product> list) {

    }

    int typeScan;

    //region spinner
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        typeScan = position;

        if (position == 0)
            typeScanTv.setText(getResources().getString(R.string.single));
        else
            typeScanTv.setText(getResources().getString(R.string.inventory_epc));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    //endregion

}