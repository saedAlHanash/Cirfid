package com.handheld.uhfrdemo.SAED.UI.Activities;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handheld.uhfr.R;

import com.handheld.uhfr.UHFRManager;
import com.handheld.uhfrdemo.EpcDataModel;
import com.handheld.uhfrdemo.Fragment1_Inventory;
import com.handheld.uhfrdemo.Fragment2_ReadAndWrite;
import com.handheld.uhfrdemo.Fragment3_Lock;
import com.handheld.uhfrdemo.Fragment4_Kill;
import com.handheld.uhfrdemo.Fragment5_Settings;
import com.handheld.uhfrdemo.MainActivity;
import com.handheld.uhfrdemo.SAED.AppConfig.FC;
import com.handheld.uhfrdemo.SAED.AppConfig.SharedPreference;
import com.handheld.uhfrdemo.SAED.Helpers.Converters.GzipConverter;
import com.handheld.uhfrdemo.SAED.Helpers.Images.ConverterImage;
import com.handheld.uhfrdemo.SAED.Helpers.NoteMessage;
import com.handheld.uhfrdemo.SAED.Helpers.View.FTH;
import com.handheld.uhfrdemo.SAED.Network.SaedSocket;
import com.handheld.uhfrdemo.SAED.UI.Fragments.Client.ClientFragment;
import com.handheld.uhfrdemo.SAED.ViewModels.All;
import com.handheld.uhfrdemo.SAED.ViewModels.MyViewModel;
import com.handheld.uhfrdemo.SAED.ViewModels.Product;
import com.handheld.uhfrdemo.ScanUtil;
import com.handheld.uhfrdemo.Util;
import com.uhf.api.cls.Reader;
import com.uhf.api.cls.Reader.TAGINFO;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.pda.serialport.Tools;

@SuppressLint("NonConstantResourceId")
public class ClientActivity extends AppCompatActivity {

    //region GLOBAL

    //region Views

    FrameLayout clientContainer;
    TextView notConnectTv;

    //endregion

    //region connection config
    /**
     * socket ip address
     */
    public String ip;
    /**
     * socket port
     */
    public int port;
    URI uri;

    //endregion

    //region sockets
    WebSocketClient webSocketClient;
    public SaedSocket socket;
    //endregion

    public MyViewModel myViewModel;
    Gson gson = new Gson();

    OnReadTag onReadTag;

    //endregion

    //region  other
    private final String TAG = "SAED_";

    private FragmentManager mFm; //fragment manager
    private FragmentTransaction mFt;//fragment transaction
    private Fragment1_Inventory fragment1;//
    private Fragment2_ReadAndWrite fragment2;
    private Fragment3_Lock fragment3;
    private Fragment4_Kill fragment4;
    private Fragment5_Settings fragment5;
    public static UHFRManager mUhfrManager;//uhf
    public static Set<String> mSetEpcs; //epc set ,epc list
    private ScanUtil instance;


    /**
     * To resolve the problem of every time you
     * click the desktop icon, the MainActivity page will be launched
     */
    private boolean mMultiCreate = false;

    private SharedPreferences mSharedPreferences;

    //endregion

    //region base

    public boolean isMulti = false;// multi mode flag
    public boolean isPlay = true;// multi mode flag
    public boolean isTid = false;// multi mode flag

    //endregion

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //region start
        if (!this.isTaskRoot()) {
            mMultiCreate = true;
            this.finish();
            return;
        }

        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            mMultiCreate = true;
            this.finish();
            return;
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Util.initSoundPool(this);//Init sound pool
        mSharedPreferences = getSharedPreferences("UHF", MODE_PRIVATE);

        //endregion
        setContentView(R.layout.activity_client);

        myViewModel = ViewModelProviders.of(this).get(MyViewModel.class);

        startClientFragment();

        ip = SharedPreference.getIp().replaceAll("\\s+", "");
        port = SharedPreference.getPort();

        if (ip == null || ip.isEmpty())
            return;

        initSocket(ip, port);

        initView();
    }

    void initView() {
        clientContainer = findViewById(R.id.client_container);
        notConnectTv = findViewById(R.id.not_connect);
    }

    //region getData
    public void getAll() {
        myViewModel.getAll(socket);
    }

    public void getProduct(String epc) {
        myViewModel.getProduct(socket, epc);
    }

    //endregion

    //region observers
    final Observer<Boolean> observer = isConnect -> {

        if (isConnect == null)
            return;
        if (isConnect)
            notConnectTv.setVisibility(View.GONE);
        else
            notConnectTv.setVisibility(View.VISIBLE);
    };

//endregion

    //region socket

    /**
     * start connection with socket
     *
     * @param mIp   socket ip address
     * @param mPort socket port
     */
    public void initSocket(String mIp, int mPort) {

        initUri(mIp, mPort);

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake h) {
                Log.d(TAG, "onOpen: ");

                getAll();

                handler.sendEmptyMessage(201);
            }

            @Override
            public void onMessage(String message) {

                if (message.trim().equals("401")) {
                    handler.sendEmptyMessage(401);
                    return;
                }

                if (message.trim().equals("200"))
                    handler.sendEmptyMessage(1000);

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d(TAG, "onClose: ");
                handler.sendEmptyMessage(202);
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, "onError: ", ex);
                handler.sendEmptyMessage(500);
            }

            @Override
            public void onMessage(ByteBuffer bytes) {
                Log.d(TAG, "onMessage: have message ");
                byte[] bytes1 = bytes.array();
                String message = null;

                try {
                    message = GzipConverter.decompress(bytes1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (message == null) {
                    handler.sendEmptyMessage(500);
                    return;
                }

                if (message.trim().equals("404")) {
                    handler.sendEmptyMessage(404);
                    return;
                }

                if (message.trim().equals("401")) {
                    handler.sendEmptyMessage(401);
                    return;
                }

                if (message.trim().equals("200")) {
                    handler.sendEmptyMessage(1000);
                    return;
                }

                if (message.charAt(0) == '[') {

                    ArrayList<All> alls = gson.fromJson(message,
                            new TypeToken<ArrayList<All>>() {
                            }.getType());

                    myViewModel.allLiveData.postValue(alls);

                    return;
                }

                Product product = gson.fromJson(message, Product.class);
                product.bitmap = ConverterImage.convertBase64ToBitmap(product.im);
                product.bitmap = ConverterImage.getResizedBitmap(product.bitmap,300);
                myViewModel.productLiveData.postValue(product);

                handler.sendEmptyMessage(200);
            }
        };

        socket = new SaedSocket(webSocketClient);
    }

    private void initUri(String mIp, int mPort) {
        try {
            uri = new URI("ws://" + mIp + ":" + mPort);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region handler
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            switch (msg.what) {
                case 404:
                    Toast.makeText(ClientActivity.this,
                            "not found", Toast.LENGTH_SHORT).show();
                    break;

                case 500:
                    Toast.makeText(ClientActivity.this,
                            "have error", Toast.LENGTH_SHORT).show();
                    break;

                case 200:

                    break;

                case 201:
                    myViewModel.connectLiveData.postValue(true);
                    break;

                case 202:

                    handler.postDelayed(() -> {
                        if (!socket.isClosed) {
                            webSocketClient.setConnectionLostTimeout(25);
                            webSocketClient.reconnect();
                            Log.d(TAG, "handleMessage: reconnect");
                        }
                    }, 30000);

                    myViewModel.connectLiveData.postValue(false);
                    break;

                case 401:
                    myViewModel.sendReportLiveData.setValue(false);
                    break;

                case 1000:
                    myViewModel.sendReportLiveData.setValue(true);

            }
        }
    };

    //endregion

    //region process

    public boolean isStart = false;

    private final Runnable runnable_ClientActivity = new Runnable() {
        @Override
        public void run() {
            if (!isStart)
                return;

            List<TAGINFO> list1;

            // getList item
            if (isMulti) {
                list1 = mUhfrManager.tagInventoryRealTime();
            } else {
                if (isTid)
                    list1 = mUhfrManager.tagEpcTidInventoryByTimer((short) 50);
                else
                    list1 = mUhfrManager.tagInventoryByTimer((short) 50);
            }

            String data;

            if (list1 != null && list1.size() > 0) {
                if (isPlay)
                    Util.play(1, 0);

                for (TAGINFO tfs : list1) {
                    byte[] epcdata = tfs.EpcId;

                    if (isTid)
                        data = Tools.Bytes2HexString(tfs.EmbededData, tfs.EmbededDatalen);
                    else
                        data = Tools.Bytes2HexString(epcdata, epcdata.length);

                    int rssi = tfs.RSSI;

                    //handler message
                    Message msg = new Message();
                    msg.what = 1;
                    Bundle b = new Bundle();
                    b.putString("data", data);
                    b.putInt("rssi", rssi);
                    msg.setData(b);

                    handler1.sendMessage(msg);
                }

            }

            handler1.postDelayed(runnable_ClientActivity, 50);
        }
    };

    public void read() {

        if (isStart)
            return;

        if (mUhfrManager == null) {
            showToast(getString(R.string.connection_failed));
            return;
        }


        mUhfrManager.setGen2session(isMulti);

        if (isMulti)
            mUhfrManager.asyncStartReading();

        handler1.post(runnable_ClientActivity);

        isStart = true;
    }

    public void stop() {

        if (!isStart)
            return;

        if (mUhfrManager == null) {
            showToast(getString(R.string.connection_failed));
            return;
        }

        if (isMulti)
            mUhfrManager.asyncStopReading();


        handler1.removeCallbacks(runnable_ClientActivity);

        isStart = false;
    }

    //endregion

    //region show tips
    private Toast toast;

    void showToast(String info) {
        if (toast == null) toast = Toast.makeText(this, info, Toast.LENGTH_SHORT);
        else toast.setText(info);
        toast.show();
    }

    //endregion

    //region handler process
    private final Handler handler1 = new Handler(Looper.getMainLooper()) {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            if (onReadTag == null)
                return;

            switch (msg.what) {

                case 1:
                    String epc = msg.getData().getString("data");
                    int rssi = msg.getData().getInt("rssi");

                    if (epc == null || epc.length() == 0)
                        return;

                    onReadTag.onRead(epc, rssi);

                    break;

                case 1980:
                    break;
            }
        }
    };

    //endregion

    void startClientFragment() {
        FTH.replaceFadFragment(FC.CLIENT_C,
                this, new ClientFragment());
    }

    void removeObservers() {
        myViewModel.connectLiveData.removeObserver(observer);
    }

    @Override
    protected void onPause() {
        super.onPause();
        removeObservers();
        stop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (webSocketClient != null && webSocketClient.isClosed())
            webSocketClient.reconnect();

        myViewModel.connectLiveData.observeForever(observer);
    }


    //region other

    @Override
    protected void onStart() {
        Log.e(TAG, "[onStart] start");
        super.onStart();
        if (Build.VERSION.SDK_INT == 29) {
            instance = ScanUtil.getInstance(this);
            instance.disableScanKey("134");
        }
        mUhfrManager = UHFRManager.getInstance();// Init Uhf module

        if (mUhfrManager != null) {
            Reader.READER_ERR err = mUhfrManager.setPower(
                    mSharedPreferences.getInt("readPower", 33),
                    mSharedPreferences.getInt("writePower", 33));//set uhf module power

            if (err == Reader.READER_ERR.MT_OK_ERR) {
                mUhfrManager.setRegion(Reader.Region_Conf.valueOf(
                        mSharedPreferences.getInt("freRegion", 1)));

                Toast.makeText(getApplicationContext(),
                        "FreRegion:" + Reader.Region_Conf.valueOf(
                                mSharedPreferences.getInt("freRegion", 1)) +
                                "\n" + "Read Power:" + mSharedPreferences.getInt("readPower", 33) +
                                "\n" + "Write Power:" + mSharedPreferences.getInt("writePower", 33),
                        Toast.LENGTH_LONG).show();

//                showToast(getString(R.string.inituhfsuccess));
            } else {

                Reader.READER_ERR err1 = mUhfrManager.setPower(30, 30);//set uhf module power
                if (err1 == Reader.READER_ERR.MT_OK_ERR) {
                    mUhfrManager.setRegion(Reader.Region_Conf.valueOf(
                            mSharedPreferences.getInt("freRegion", 1)));

                    Toast.makeText(getApplicationContext(),
                            "FreRegion:" + Reader.Region_Conf.valueOf(
                                    mSharedPreferences.getInt("freRegion", 1)) +
                                    "\n" + "Read Power:" + 30 +
                                    "\n" + "Write Power:" + 30, Toast.LENGTH_LONG).show();
                } else {
                    NoteMessage.showSnackBar(this, getString(R.string.inituhffail));
                }
            }
        } else
            NoteMessage.showSnackBar(this, getString(R.string.inituhffail));

        Log.e(TAG, "[onStart] end");

        //saed
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.rfid.FUN_KEY");
        getApplicationContext().registerReceiver(keyReceiver, filter);
    }

    private long startTime = 0;
    private boolean keyUpFalg = true;
    private final BroadcastReceiver keyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int keyCode = intent.getIntExtra("keyCode", 0);

            if (keyCode == 0) //H941
                keyCode = intent.getIntExtra("keycode", 0);

            boolean keyDown = intent.getBooleanExtra("keydown", false);
            if (keyUpFalg && keyDown && System.currentTimeMillis() - startTime > 500) {

                keyUpFalg = false;
                startTime = System.currentTimeMillis();
                if (keyCode == KeyEvent.KEYCODE_F3 || keyCode == KeyEvent.KEYCODE_F4)
                    if (isStart)
                        stop();
                    else
                        read();

            } else if (keyDown)
                startTime = System.currentTimeMillis();
            else
                keyUpFalg = true;


        }
    };

    @Override
    protected void onStop() {
        Log.e(TAG, "[onStop] start");
        super.onStop();

        if (Build.VERSION.SDK_INT == 29)
            instance.enableScanKey("134");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (mUhfrManager != null) {
            //close uhf module
            mUhfrManager.close();
            mUhfrManager = null;
        }

        try {
            getApplicationContext().unregisterReceiver(keyReceiver);
        } catch (Exception ignore) {
        }

        Log.e(TAG, "[onStop] end");
    }


    @Override
    protected void onDestroy() {
        if (mUhfrManager != null && !mMultiCreate) {
            //close uhf module
            mUhfrManager.close();
            mUhfrManager = null;
        }

        if (socket != null)
            socket.close();

        super.onDestroy();

    }

    private long exitTime = 0;//key down time

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
//            case KeyEvent.KEYCODE_BACK:
//                if (System.currentTimeMillis() - exitTime >= 2000) {
//                    exitTime = System.currentTimeMillis();
//                    NoteMessage.showSnackBar(this, getString(R.string.quit_on_double_click_));
//                    return true;
//                } else {
//                    NoteMessage.showSnackBar(this, getString(R.string.exiting));
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    finish();
//                }
//                break;
        }
        return super.onKeyDown(keyCode, event);
    }
    //endregion

    public interface OnReadTag {
        void onRead(@NotNull String epc, int rssi);
    }
}