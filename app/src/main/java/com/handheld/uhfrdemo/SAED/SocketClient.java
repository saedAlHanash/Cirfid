package com.handheld.uhfrdemo.SAED;


import android.view.View;

import com.handheld.uhfrdemo.EpcDataModel;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

public class SocketClient {

    Socket socket;
    InetAddress inetAddress;

    PrintWriter mBufferOut;
    /**
     * call back fro listen connect change stat
     */
    ConnectStat connectStat;

    String ip;
    int port;
    private SendThread sendThread;

    private final Object lock = new Object();

    /**
     * to checking if there thread actually running and try to connect <br>
     * then no need to open new thread to re connect <br>
     * just lat current thread try
     */
    boolean reconnect = false;

    /**
     * set true when socket connect
     * set false when socket lost connect
     */
    boolean isConnect = false;

    DataInputStream finalIn = null;

    /**
     * set listener for connect change in this Socket<br>
     * if lost connect return false and if reconnect return true
     */
    public void setOnChangeConnectStatListener(ConnectStat connectStat) {
        this.connectStat = connectStat;
    }

    /**
     * init PrintWriter to send data in socket
     */
    private void initPrintWriter() throws IOException {
        mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
//        mBufferOut.println("connected with" + socket.getLocalAddress().getHostName() + socket.getPort()); // ارسال من اتصل للسيرفر
    }

    /**
     * start connect with server Socket
     *
     * @return true if connecting
     */
    private boolean connect() {
        try {
            inetAddress = InetAddress.getByName(ip);
            socket = new Socket(inetAddress, port);// الاتصال بال socket

            initPrintWriter();// تهيئة ال buffer writer
            isConnect = true;

            if (connectStat != null)
                connectStat.stat(true); // تغيير الحالة في ال callBack

            //تهيئة متنصت قراءة من ال socket بحيث عند انقطاع الاتصال يقوم ب exception من خلاله نقوم بتغير حالة التصال بال callBack
            //تم اللجوء لهذا الحل لان ال socket.isConnected() تقوم بإرجاع true دوما
            new Thread(runnable).start();

            if (sendThread == null) {
                sendThread = new SendThread();
                sendThread.start();
            }

            return socket.isConnected();

        } catch (IOException e) {
            return false;
        }
    }

    /**
     * to checking if can reConnect with socket <p>
     * will be false when onDestroy Activity
     */
    public boolean tryConnect = true;
    Thread t;

    public void connect(String mIp, int mPort) {
        this.ip = mIp;
        this.port = mPort;
        tryConnect = true;

        t = new Thread(() -> { //لانه لا يمكن الاتصال من ال UI thread
            while (tryConnect) { // من أجل المحاولة والمحاولة حتى تمام عملية الاتصال
                //اذا الاتصال تم
                if (connect()) {
                    if (connectStat != null)
                        connectStat.stat(true);

                    this.tryConnect = false;
                    break;
                } else // اذا لم يتصل
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }

        });

        t.start();
    }

    /**
     * checking if socket is connecting
     */
    public boolean isConnected() {
        if (socket == null) {
            return false;
        }
        return socket.isConnected() && isConnect;
    }

    /**
     * checking if socket is closed connecting
     */
    public boolean isClosed() {
        if (socket == null) {
            return false;
        }
        return socket.isClosed();
    }

    /**
     * reconnect with server socket
     */
    private void reConnect() {
        if (reconnect)
            return;

        reconnect = true;

        new Thread(() -> {
            while (reconnect) {
                //اذا الاتصال تم
                if (connect()) {
                    reconnect = false;
                    break;
                } else // اذا لم يتصل
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        }).start();
    }

    public void reconnect(String mIp, int mPort) {
        this.ip = mIp;
        this.port = mPort;
        close();

        if (t != null)
            t.interrupt();

        connect(mIp, mPort);
    }


    public void sendBoolean(boolean b) {
        if (!socket.isConnected()
                || mBufferOut == null
                || mBufferOut.checkError())
            return;

        mBufferOut.print(b);
//        mBufferOut.flush();
    }

    public void sendInt(int i) {
        if (!socket.isConnected()
                || mBufferOut == null
                || mBufferOut.checkError())
            return;
        mBufferOut.print(i);
//        mBufferOut.flush();
    }

    public void sendFloat(float f) {
        if (!socket.isConnected()
                || mBufferOut == null
                || mBufferOut.checkError())
            return;
        mBufferOut.print(f);
//        mBufferOut.flush();
    }

    public void sendLong(long l) {
        if (!socket.isConnected()
                || mBufferOut == null
                || mBufferOut.checkError())
            return;
        mBufferOut.println(l);
//        mBufferOut.flush();
    }

    public void sendString(String message) {
        if (!socket.isConnected()
                || mBufferOut == null
                || mBufferOut.checkError())
            return;

        mBufferOut.println(message);
        mBufferOut.flush();
    }

    public void sendDataList(ArrayList<EpcDataModel> list) {

        if (sendThread == null)
            return;

        sendThread.SendData(list);
    }

    /**
     * start listener reed in socket <br>
     * الاستفادة : عند فقدان الاتصال يقوم برد أكسبشن لذلك نستفيد منه فقط لمعرفة حالة الاتصال
     */
    private final Runnable runnable = () -> {
        try {
            finalIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                finalIn.readBoolean();
            } catch (IOException ignore) {
                isConnect = false;
                if (connectStat != null)
                    connectStat.stat(false);
                reConnect();
                break;
            }
        }
    };

    /**
     * close connect with server socket
     */
    public void close() {
        new Thread(() -> {
            try {
                this.tryConnect = false;

                if (connectStat != null)
                    connectStat.stat(false);


                if (socket != null)
                    socket.close();

                if (mBufferOut != null)
                    mBufferOut.close();
                if (finalIn != null)
                    finalIn.close();

                if (sendThread != null)
                    sendThread.killSelf();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ).start();


    }


//    /**
//     * start thread to send data to socket <br>
//     * its making for in all items id {@link #mTagList} and checking each item if sent or not<br>
//     * after for finishing thread sleeping<br>
//     * then when an new items adding to adapter thread interrupt and do new for in items and resend
//     * all items not sent<br>
//     */
//    void initSendDataThread(int lastIndex, ArrayList<Object> mTagList) {
//        final int j = lastIndex;
//        thread = new Thread(() -> {
//            while (threadKill) {
//
//                for (int i = j; i < mTagList.size(); i++) {
//
//                    try {
//                        Thread.sleep(20);
//                    } catch (InterruptedException ignored) {
//                    }
//                    if (((ReadOrWriteActivity) activity).socketClient.isConnected()) {//يوجد اتصال
//
//                        lastIndex += 1;
//
//                        if (mTagList.get(i).isSanded)//العنصر تم ارساله
//                            continue;
//
//                        //ارسال العنصر
//                        ((ReadOrWriteActivity) activity).socketClient.sendString(mTagList.get(i).getEpc());
//                        mTagList.get(i).isSanded = true;
//
//                    } else {
//                        //اذا كان ال thread الأول الذي يحاول الاتصال ما زال يحاول
//                        //(موجود في ال activity في تابع ال initSocket)
//                        if (!((ReadOrWriteActivity) activity).tryConnect)
//                            ((ReadOrWriteActivity) activity).socketClient.reConnect();//اعادة الاتصال
//                    }
//                }
//                //تأكد انه لم تصل بياات ريثما يتم العمل على الإرسال
//                //اذا وصل عاود عملية الارسال من جديد
//                if (!newDataWhenThreadSendData) {
//
//                    newDataWhenThreadSendData = false;
//                    try {
//                        thread.sleep(99999999); //sleep long time 💤💤
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                } else  // وإلا قم يتعليم العناصر ب ✔
//                    activity.runOnUiThread(this::notifyDataSetChanged); //✅ تعليم بأنه مرسل
//            }
//        });
//        thread.start();
//    }

    public class SendThread extends Thread {

        public boolean threadKill = true;
        private int lastIndex;
        private boolean isConnect;

        ArrayList<EpcDataModel> mTagList;

        @Override
        public void run() {
            synchronized (lock) {
                while (threadKill) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (!threadKill)
                        break;

                    for (int i = lastIndex; i < mTagList.size(); i++) {

                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException ignored) {
                        }

                        if (!isClosed()) {//يوجد اتصال

                            lastIndex += 1;

//                        if (mTagList.get(i).isSanded)//العنصر تم ارساله
//                            continue;

                            //ارسال العنصر

                            sendString(mTagList.get(i).getepc());

                            mTagList.get(i).setSent(true);

//                        mTagList.get(i).isSanded = true;

                        } else
                            reConnect();//اعادة الاتصال
                    }
                }
            }
        }

        public void SendData(ArrayList<EpcDataModel> list) {
            this.mTagList = list;
            synchronized (lock) {
                lock.notify();
            }
        }

        public void killSelf() {
            this.threadKill = false;

            synchronized (lock) {
                lock.notify();
            }
        }
    }

    public interface ConnectStat {
        void stat(boolean b);
    }


}