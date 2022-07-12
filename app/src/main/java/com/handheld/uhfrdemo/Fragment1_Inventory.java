package com.handheld.uhfrdemo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handheld.uhfr.R;
import com.handheld.uhfrdemo.SAED.DialogSetIp;
import com.handheld.uhfrdemo.SAED.SharedPreference;
import com.handheld.uhfrdemo.SAED.SocketClient;
import com.uhf.api.cls.Reader.TAGINFO;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import cn.pda.serialport.Tools;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import static com.handheld.uhfrdemo.Util.context;


public class Fragment1_Inventory extends Fragment implements OnCheckedChangeListener, OnClickListener {
    final String TAG = "Fragment1";
    private View view;// this fragment UI
    private TextView tvTagCount;//tag count text view
    private TextView tvTagSum;//tag sum text view
    private TextView tvRunCount;//tag sum text view
    private TextView tvTitle;//tag sum text view

    private ListView lvEpc;// epc list view
    private Button btnStart;//inventory button
    private Button btnClear;// clear button
    private Button btnExport;// clear button
    private Button ipAddressBtn;// ip button
    //    private Button btnTime;// clear button
    private CheckBox checkMulti;//multi model check box
    private CheckBox checkTid;//multi model check box
    private CheckBox checkPlay;//multi model check box

    private Set<String> epcSet = null; //store different EPC
    private ArrayList<EpcDataModel> listEpc = null;//EPC list
    private Map<String, Integer> mapEpc = null; //store EPC position
    private EPCadapter adapter;//epc list adapter

    private boolean isMulti = false;// multi mode flag
    private boolean isPlay = true;// multi mode flag
    private boolean isTid = false;// multi mode flag
    private int allCount = 0;// inventory count

    private long lastTime = 0L;// record play sound time
    private long timeout;
    private SharedPreferences mSharedPreferences;

    //saed:
    MainActivity myActivity;
    DialogSetIp dialog;

    long statenvtick;
    //handler
    @SuppressLint("HandlerLeak")
    private final Handler handler1 = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    String epc = msg.getData().getString("data");
                    String rssi = msg.getData().getString("rssi");

                    if (epc == null || epc.length() == 0)
                        return;

                    int position;
                    allCount++;
                    if (epcSet == null) {//first

                        epcSet = new HashSet<>();
                        listEpc = new ArrayList<>();
                        mapEpc = new HashMap<>();

                        epcSet.add(epc);
                        mapEpc.put(epc, 0);
                        EpcDataModel epcTag = new EpcDataModel();
                        epcTag.setepc(epc);
                        epcTag.setrssi(rssi);
                        epcTag.setCount(1);
                        listEpc.add(epcTag);

                        adapter = new EPCadapter(requireActivity(), listEpc);

                        if (socketClient.isConnected())
                            socketClient.sendDataList(listEpc);

                        lvEpc.setAdapter(adapter);

                        MainActivity.mSetEpcs = epcSet;

                    } else {

                        if (epcSet.contains(epc)) {//set already exit

                            position = mapEpc.get(epc);
                            EpcDataModel epcOld = listEpc.get(position);
                            epcOld.setrssi(rssi);
                            epcOld.setCount(epcOld.getCount() + 1);
                            listEpc.set(position, epcOld);

                        } else {

                            epcSet.add(epc);
                            mapEpc.put(epc, listEpc.size());
                            EpcDataModel epcTag = new EpcDataModel();
                            epcTag.setepc(epc);
                            epcTag.setrssi(rssi);
                            epcTag.setCount(1);
                            listEpc.add(epcTag);

                            MainActivity.mSetEpcs = epcSet;

                        }

                        tvTagCount.setText(String.valueOf(allCount));
                        tvTagSum.setText(String.valueOf(listEpc.size()));

                        if (socketClient.isConnected())
                            socketClient.sendDataList(listEpc);

                        adapter.notifyDataSetChanged();
                    }

                    break;
                case 1980: // فقط من أجل تعيين textView

                    String countString = tvRunCount.getText().toString();

                    if (countString.equals("") || countString == null)
                        tvRunCount.setText(String.valueOf(1));

                    else {
                        int previousCount = -100;

                        try {
                            previousCount = Integer.parseInt(countString);
                        } catch (Exception ignore) {
                        }

                        int nowCount = previousCount + 1;

                        tvRunCount.setText(String.valueOf(nowCount));
                    }
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // TODO Auto-generated method stub

        view = inflater.inflate(R.layout.fragment_inventory, container, false);

        mSharedPreferences = requireActivity().getSharedPreferences("UHF", Context.MODE_PRIVATE);
        timeout = mSharedPreferences.getInt("timeOut", 10000);

        Log.e(TAG, String.valueOf(timeout));

        initView();

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.rfid.FUN_KEY");

        requireActivity().getApplicationContext().registerReceiver(keyReceiver, filter);

        ip = SharedPreference.getIp().replaceAll("\\s+", "");
        port = SharedPreference.getPort();

        if (ip.isEmpty())
            Toast.makeText(myActivity, "pleas set ip address", Toast.LENGTH_SHORT).show();
        else
            ipAddressBtn.setText(ip + ":" + port);

        initSocket(ip, port);

        return view/*super.onCreateView(inflater, container, savedInstanceState)*/;
    }


    //saed:
    public SocketClient socketClient = new SocketClient();
    /**
     * to checking if can reConnect with socket <p>
     * will be false when onDestroy Activity
     */
    public boolean tryConnect = true;
    /**
     * socket ip address
     */
    public String ip;
    /**
     * socket port
     */
    public int port;
    TextView notConnectTv;

    /**
     * start connection with socket
     *
     * @param mIp   socket ip address
     * @param mPort socket port
     */
    public void initSocket(String mIp, int mPort) {

        new Thread(() -> { //لانه لا يمكن الاتصال من ال UI thread
            while (tryConnect) { // من أجل المحاولة والمحاولة حتى تمام عملية الاتصال

                //اذا الاتصال تم
                if (socketClient.connect(mIp, mPort)) {
                    requireActivity().runOnUiThread(() -> notConnectTv.setVisibility(View.GONE));
                    this.tryConnect = false;
                    break;
                } else // اذا لم يتصل
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }

        }).start();

        //call back active when connect stat change
        socketClient.setOnChangeConnectStatListener(connect -> {

            if (!isAdded())
                return;

            myActivity.runOnUiThread(() -> {


                if (connect)
                    notConnectTv.setVisibility(View.GONE);
                else
                    notConnectTv.setVisibility(View.VISIBLE);
            });
        });
    }

//    Handler handler;
//    Runnable runnable;
//
//    public void SAED() {
//
//        runnable = () -> {
//
//            Message msg = new Message();
//
//            msg.what = 1; // message id
//
//            Bundle b = new Bundle(); // data
//            int x = new Random().nextInt();
//            b.putString("data", String.valueOf(x)); //epc
//            b.putString("rssi", 50 + ""); // rssi
//            msg.setData(b);
//
//            handler1.sendMessage(msg);
//
//            handler.postDelayed(runnable, 500);
//        };
//
//        handler = new Handler(requireActivity().getMainLooper());
//        handler.postDelayed(runnable, 3000);
//    }

    @SuppressLint("SetTextI18n")
    private void initView() {

        tvTagCount = (TextView) view.findViewById(R.id.textView_tag_count);
        lvEpc = (ListView) view.findViewById(R.id.listView_epc);
        btnStart = (Button) view.findViewById(R.id.button_start);
//        btnTime = (Button) view.findViewById(R.id.button_time_start);
        tvTagSum = (TextView) view.findViewById(R.id.textView_tag);
        tvRunCount = (TextView) view.findViewById(R.id.textView_run_count);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        checkMulti = (CheckBox) view.findViewById(R.id.checkBox_multi);
        checkTid = (CheckBox) view.findViewById(R.id.checkBox_tid);
        checkPlay = (CheckBox) view.findViewById(R.id.checkBox_sound);
        checkMulti.setOnCheckedChangeListener(this);
        checkTid.setOnCheckedChangeListener(this);
        checkPlay.setOnCheckedChangeListener(this);
        btnClear = (Button) view.findViewById(R.id.button_clear_epc);
        btnExport = view.findViewById(R.id.button_export);

        //saed:
        myActivity = (MainActivity) requireActivity();
        dialog = new DialogSetIp(myActivity);

        ipAddressBtn = view.findViewById(R.id.ip_address);
        ipAddressBtn.setOnClickListener(this::onClick);

        dialog.setOnCancelListener(d -> {
            ip = SharedPreference.getIp().replaceAll("\\s+", "");
            port = SharedPreference.getPort();

            ipAddressBtn.setText(ip + ":" + port);
        });

        lvEpc.setFocusable(false);
        lvEpc.setClickable(false);
        lvEpc.setItemsCanFocus(false);
        lvEpc.setScrollingCacheEnabled(false);
        lvEpc.setOnItemClickListener(null);
        btnStart.setOnClickListener(this);
        btnExport.setOnClickListener(this);

//        btnTime.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        this.notConnectTv = view.findViewById(R.id.not_connect);
    }


    private boolean f1hidden = false;

    private boolean isStart = false;

    private final Runnable runnable_MainActivity = new Runnable() {
        @Override
        public void run() {

            if (!isStart)
                return;

            List<TAGINFO> list1;

            if (isMulti) //multi mode
                list1 = MainActivity.mUhfrManager.tagInventoryRealTime();

            else {

                if (isTid)
                    list1 = MainActivity.mUhfrManager.tagEpcTidInventoryByTimer((short) 50);
                else
                    list1 = MainActivity.mUhfrManager.tagInventoryByTimer((short) 50);
            }

            String data;
            int rssi;

            handler1.sendEmptyMessage(1980);//تهيئة عدد القراءات

            if (list1 != null && list1.size() > 0) {

                if (isPlay)
                    Util.play(1, 0);

                for (TAGINFO tfs : list1) {
                    byte[] epcdata = tfs.EpcId;

                    if (isTid)
                        data = Tools.Bytes2HexString(tfs.EmbededData, tfs.EmbededDatalen);
                    else
                        data = Tools.Bytes2HexString(epcdata, epcdata.length);

                    rssi = tfs.RSSI;
                    //الرسالة المرسلة لل handler تحتوي المعلومات
                    Message msg = new Message();

                    msg.what = 1; // message id

                    Bundle b = new Bundle(); // data
                    b.putString("data", data); //epc
                    b.putString("rssi", rssi + ""); // rssi
                    msg.setData(b);

                    handler1.sendMessage(msg);
                }
            }

            handler1.postDelayed(runnable_MainActivity, 100);

        }
    };

    private final Runnable runnable_MainActivity1 = new Runnable() {
        @Override
        public void run() {
            List<TAGINFO> list1;
            list1 = MainActivity.mUhfrManager.tagInventoryRealTime();
            String data;
            if (list1 != null && list1.size() > 0) {//
                Log.e(TAG, list1.size() + "");
//                if(isPlay) {
                Util.play(1, 0);
//                }
                for (TAGINFO tfs : list1) {
                    byte[] epcdata = tfs.EpcId;
                    data = Tools.Bytes2HexString(epcdata, epcdata.length);
                    Message msg = new Message();
                    msg.what = 1;
                    Bundle b = new Bundle();
                    b.putString("data", data);
                    msg.setData(b);
                    handler1.sendMessage(msg);//عرض االمعلومات
                }
            }

            long read = System.currentTimeMillis();

            long readtime = read - statenvtick;

            if (readtime > timeout) { // شرط التوقف
                MainActivity.mUhfrManager.asyncStopReading();
                handler1.removeCallbacks(runnable_MainActivity1);
            } else
                handler1.postDelayed(runnable_MainActivity1, 100);
        }
    };


    /**
     * call when start or stop reading
     */
    public void isRead() {

        if (MainActivity.mUhfrManager == null) {
            showToast(requireActivity().getString(R.string.connection_failed));
            return;
        }

        if (!isStart) { //بداية المسح

            checkMulti.setEnabled(false);
            checkTid.setEnabled(false);
            btnStart.setText(this.getString(R.string.stop_inventory_epc));

            MainActivity.mUhfrManager.setGen2session(isMulti);

            if (isMulti)
                MainActivity.mUhfrManager.asyncStartReading();

            handler1.post(runnable_MainActivity);

            isStart = true;

        } else { // التوقف عن المسح

            checkMulti.setEnabled(true);
            checkTid.setEnabled(true);

            if (isMulti)
                MainActivity.mUhfrManager.asyncStopReading();

            handler1.removeCallbacks(runnable_MainActivity);

            btnStart.setText(this.getString(R.string.start_inventory_epc));

            isStart = false;
        }
    }

    private void clearEpc() {

        if (epcSet != null)
            epcSet.clear();

        if (listEpc != null)
            listEpc.clear();

        if (mapEpc != null)
            mapEpc.clear(); //store EPC position

        if (adapter != null)
            adapter.notifyDataSetChanged();

        allCount = 0;
        tvTagSum.setText("0");
        tvTagCount.setText("0");
        if (MainActivity.mSetEpcs != null) {
            MainActivity.mSetEpcs.clear();
        }
//        lvEpc.removeAllViews();
    }

    //show tips
    private Toast toast;

    private void showToast(String info) {
        if (toast == null) toast = Toast.makeText(requireActivity(), info, Toast.LENGTH_SHORT);
        else toast.setText(info);
        toast.show();
    }

    //key receiver
    private long startTime = 0;
    private boolean keyUpFlag = true;

    private final BroadcastReceiver keyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            {
                if (f1hidden)
                    return;

                int keyCode = intent.getIntExtra("keyCode", 0);

                if (keyCode == 0) //H941
                    keyCode = intent.getIntExtra("keycode", 0);


                boolean keyDown = intent.getBooleanExtra("keydown", false);

                if (keyUpFlag && keyDown && System.currentTimeMillis() - startTime > 500) {

                    keyUpFlag = false;
                    startTime = System.currentTimeMillis();

                    if ((keyCode == KeyEvent.KEYCODE_F3 || keyCode == KeyEvent.KEYCODE_F4))
                        onClick(btnStart);

                } else if (keyDown)
                    startTime = System.currentTimeMillis();
                else
                    keyUpFlag = true;
            }
        }
    };

    public void save(/*List<String> listepc,*/ String fileName) {
        WritableWorkbook wwb = null;
        File file = new File(Environment.getExternalStorageDirectory() + "/EPC");
        if (!file.exists()) {
            file.mkdir();
            Log.e("hai-1", "1");
        }
        try {
            //Create a statistical file based on the current file path and instantiate an object that operates excel
            wwb = Workbook.createWorkbook(new File(Environment.getExternalStorageDirectory() + "/EPC/" + fileName));
            Log.e("hai-1", "2");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("hai-1", e.toString());
        }

        if (wwb != null) {
            // Create a tab at the bottom The parameters are the name of the tab and the index of the selection card
            WritableSheet writableSheet = wwb.createSheet("1", 0);
            //Create excel header information
            String[] topic = {"ID", "EPC", "Count"};
            for (int i = 0; i < topic.length; i++) {
                //Fill data in cells horizontally
                Label labelC = new Label(i, 0, topic[i]);
                try {
                    writableSheet.addCell(labelC);
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }


            if (listEpc != null) {
                for (int i = 0; i < listEpc.size(); i++) {
                    Label labelC1 = new Label(0, i + 1, i + 1 + "");
                    Label labelC2 = new Label(1, i + 1, listEpc.get(i).getepc() + "");
                    Label labelC3 = new Label(2, i + 1, listEpc.get(i).getCount() + "");
                    try {
                        writableSheet.addCell(labelC1);
                        writableSheet.addCell(labelC2);
                        writableSheet.addCell(labelC3);
                    } catch (WriteException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                wwb.write();
                wwb.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        }
        sysToScan(Environment.getExternalStorageDirectory() + "/EPC/" + fileName);
    }

    public void sysToScan(String filePath) {
        //Scan files in the specified folder
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(filePath);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        //Broadcast to the system
        context.sendBroadcast(intent);
    }

    public String FileName() {
        String res;
        long time = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time);
        res = simpleDateFormat.format(date).trim() + ".xls";
        Log.e("hai-1", res);
        return res;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub

        switch (buttonView.getId()) {
            case R.id.checkBox_multi: {
                isMulti = isChecked;
                break;
            }

            case R.id.checkBox_sound: {
                isPlay = isChecked;
                break;
            }

            case R.id.checkBox_tid: {
                if (isChecked) {
                    isTid = true;
                    tvTitle.setText("TID");
                    isMulti = false;
                    checkMulti.setChecked(false);
                    checkMulti.setEnabled(false);
                } else {
                    isTid = false;
                    tvTitle.setText("EPC");
                    checkMulti.setEnabled(true);
                }
                break;
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_start:
                isRead();
                break;
            case R.id.button_clear_epc:
                clearEpc();
                break;
            case R.id.button_export:
                if (listEpc != null && listEpc.size() != 0) {
                    save(FileName());
                    Toast.makeText(getContext(), "Success" + listEpc.size(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Fila", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ip_address: {
                dialog.show();
                break;
            }

//            case R.id.button_time_start:
//                statenvtick = System.currentTimeMillis();
//                Log.e(TAG,"statetime:"+statenvtick);
//                    Log.e(TAG,"isMulti-true");
//                    MainActivity.mUhfrManager.asyncStartReading();
//
//                handler1.postDelayed(runnable_MainActivity1, 0);
//                break;


        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Log.e(TAG, "[onHiddenChanged] start");

        super.onHiddenChanged(hidden);
        f1hidden = hidden;

        if (isStart) {
            isStart = false;
            checkMulti.setEnabled(true);
            checkTid.setEnabled(true);

            if (isMulti)
                MainActivity.mUhfrManager.asyncStopReading();

            handler1.removeCallbacks(runnable_MainActivity);
            btnStart.setText(this.getString(R.string.start_inventory_epc));
        }

        if (MainActivity.mUhfrManager != null)
            MainActivity.mUhfrManager.setCancleInventoryFilter();

        Log.e(TAG, "[onHiddenChanged] end");
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub

        if (isStart) {
            isStart = false;
//            isRunning = false;
            MainActivity.mUhfrManager.stopTagInventory();
        }
        this.tryConnect = false;

        if (socketClient != null) {
            socketClient.setOnChangeConnectStatListener(null);
            this.socketClient.close();
        }
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        Log.e(TAG, "[onStart] start");
        super.onStart();
        MainActivity activity = (MainActivity) requireActivity();
        if (activity != null) {
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.rfid.FUN_KEY");
            requireActivity().getApplicationContext().registerReceiver(keyReceiver, filter);

        }
        Log.e(TAG, "[onStart] end");
    }

    @Override
    public void onStop() {
        Log.e(TAG, "[onStop] start");
        super.onStop();
        if (isStart) {
            isStart = false;
            checkMulti.setEnabled(true);
            checkTid.setEnabled(true);
            if (isMulti) {
                MainActivity.mUhfrManager.asyncStopReading();
            }
            handler1.removeCallbacks(runnable_MainActivity);
            btnStart.setText(this.getString(R.string.start_inventory_epc));
        }
        MainActivity activity = (MainActivity) requireActivity();
        if (activity != null) {
            activity.getApplicationContext().unregisterReceiver(keyReceiver);
        }
        Log.e(TAG, "[onStop] end");
    }

}
