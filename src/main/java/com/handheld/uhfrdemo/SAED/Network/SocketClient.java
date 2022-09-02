//package com.handheld.uhfrdemo.SAED.Network;
//
//import android.app.Activity;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedWriter;
//import java.io.DataInputStream;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//import java.net.InetAddress;
//import java.net.Socket;
//import java.util.List;
//
//public class SocketClient {
//    /**
//     * call back fro listen connect change stat
//     */
//    ConnectStat connectStat;
//
//    Socket socket;
//    InetAddress inetAddress;
//    DataInputStream finalIn = null;
//    PrintWriter mBufferOut;
//
//    SendThread sendThread;
//    ConnectThread connectThread = new ConnectThread();
//
//    String ip;
//    int port;
//
//    /**
//     * to checking if there thread actually running and try to connect <br>
//     * then no need to open new thread to re connect <br>
//     * just lat current thread try
//     */
//    boolean reconnect = false;
//    /**
//     * set true when socket connect
//     * set false when socket lost connect
//     */
//    boolean isConnect = false;
//
//
//    /**
//     * set listener for connect change in this Socket<br>
//     * if lost connect return false and if reconnect return true
//     */
//    public void setOnChangeConnectStatListener(ConnectStat connectStat) {
//        this.connectStat = connectStat;
//    }
//
//    /**
//     * init PrintWriter to send data in socket
//     */
//    private void initPrintWriter() throws IOException {
//        mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
////        mBufferOut.println("connected with" + socket.getLocalAddress().getHostName() + socket.getPort()); // ارسال من اتصل للسيرفر
//    }
//
//
//    private boolean connect() {
//        try {
//            inetAddress = InetAddress.getByName(ip);
//            socket = new Socket(inetAddress, port);// الاتصال بال socket
//
//            initPrintWriter();// تهيئة ال buffer writer
//            isConnect = true;
//
//            connectStat.stat(true); // تغيير الحالة في ال callBack
//
//
//            //تهيئة متنصت قراءة من ال socket بحيث عند انقطاع الاتصال يقوم ب exception من خلاله نقوم بتغير حالة التصال بال callBack
//            //تم اللجوء لهذا الحل لان ال socket.isConnected() تقوم بإرجاع true دوما
//            new Thread(runnable).start();
//
//            if (sendThread == null) {
//                sendThread = new SendThread();
//                sendThread.start();
//            }
//
//            return socket.isConnected();
//
//        } catch (IOException e) {
//            return false;
//        }
//    }
//
//    /**
//     * start connect with server Socket
//     *
//     * @param mIp   socket ip address
//     * @param mPort socket port
//     * @return true if connecting
//     */
//    public void connect(String mIp, int mPort) {
//        this.ip = mIp;
//        this.port = mPort;
//
//        connectThread.start();
//    }
//
//    /**
//     * checking if socket is connecting
//     */
//    public boolean isConnected() {
//        if (socket == null)
//            return false;
//
//        return socket.isConnected() && isConnect;
//    }
//
//    /**
//     * reconnect with server socket
//     */
//    public void reConnect() {
//
//        connectThread.reConnect();
//    }
//
//    public void sendBoolean(boolean b) {
//        if (!socket.isConnected()
//                || mBufferOut == null
//                || mBufferOut.checkError())
//            return;
//
//        mBufferOut.print(b);
////        mBufferOut.flush();
//    }
//
//    public void sendInt(int i) {
//        if (!socket.isConnected()
//                || mBufferOut == null
//                || mBufferOut.checkError())
//            return;
//        mBufferOut.print(i);
////        mBufferOut.flush();
//    }
//
//    public void sendFloat(float f) {
//        if (!socket.isConnected()
//                || mBufferOut == null
//                || mBufferOut.checkError())
//            return;
//        mBufferOut.print(f);
////        mBufferOut.flush();
//    }
//
//    public void sendLong(long l) {
//        if (!socket.isConnected()
//                || mBufferOut == null
//                || mBufferOut.checkError())
//            return;
//        mBufferOut.println(l);
////        mBufferOut.flush();
//    }
//
//    public void sendString(String message) {
//        if (!socket.isConnected()
//                || mBufferOut == null
//                || mBufferOut.checkError())
//            return;
//
//        mBufferOut.println(message);
//        mBufferOut.flush();
//    }
//
//    Activity activity;
//
//    RecycleViewAdapter adapter;
//
//    public void sendDataList(List<TagInfo> list, Activity activity, RecycleViewAdapter adapter) {
//        this.activity = activity;
//        this.adapter = adapter;
//        if (sendThread == null)
//            return;
//
//        sendThread.SendData(list);
//    }
//
//    /**
//     * start listener reed in socket <br>
//     * الاستفادة : عند فقدان الاتصال يقوم برد أكسبشن لذلك نستفيد منه فقط لمعرفة حالة الاتصال
//     */
//    private final Runnable runnable = () -> {
//        try {
//            finalIn = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        while (true) {
//            try {
//                finalIn.readBoolean();
//            } catch (IOException ignore) {
//                isConnect = false;
//                connectStat.stat(false);
//                reConnect();
//                break;
//            }
//        }
//    };
//
//    /**
//     * close connect with server socket
//     */
//    public void close() {
//        new Thread(() -> {
//            try {
//
//                if (socket != null)
//                    socket.close();
//
//                if (mBufferOut != null)
//                    mBufferOut.close();
//
//                if (finalIn != null)
//                    finalIn.close();
//
//                if (sendThread != null)
//                    sendThread.killSelf();
//
//                if (connectThread != null)
//                    connectThread.killSelf();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }
//
//    private final Object lock = new Object();
//
//    /**
//     * checking if socket is closed connecting
//     */
//    public boolean isClosed() {
//        if (socket == null)
//            return false;
//
//        return socket.isClosed();
//    }
//
//    public class SendThread extends Thread {
//
//        private boolean threadKill = true;
//
//        List<TagInfo> mTagList;
//
//        @Override
//        public void run() {
//            synchronized (lock) {
//                while (threadKill) {
//                    try {
//                        lock.wait();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//
//                    if (!threadKill)
//                        break;
//
//                    for (int i = 0; i < mTagList.size(); i++) {
//
//                        //العنصر تم ارساله
//                        if (mTagList.get(i).isSanded)
//                            continue;
//
//                        sleep();
//
//                        //يوجد اتصال
//                        if (isConnected()) {
//                            //ارسال العنصر
//                            sendString(mTagList.get(i).getEpc());
//                            mTagList.get(i).isSanded = true;
//
//                        } else
//                            reConnect();//اعادة الاتصال
//                    }
//
//                    if (activity != null)
//                        activity.runOnUiThread(() -> {
//                            adapter.notifyDataSetChanged();
//                        });
//                }
//            }
//        }
//
//        private void sleep() {
//            try {
//                Thread.sleep(20);
//            } catch (InterruptedException ignored) {
//            }
//        }
//
//        public void SendData(List<TagInfo> list) {
//            this.mTagList = list;
//            synchronized (lock) {
//                lock.notify();
//            }
//        }
//
//        public void killSelf() {
//
//            this.threadKill = false;
//            synchronized (lock) {
//                lock.notify();
//            }
//        }
//    }
//
//    public class ConnectThread extends Thread {
//
//        boolean tryConnect = true;
//
//        @Override
//        public void run() {
//            while (tryConnect) {
//                if (connect()) {
//
//                    if (connectStat != null)
//                        connectStat.stat(true);
//                    tryConnect = false;
//                    break;
//
//                } else
//                    sleep();
//            }
//        }
//
//        private void sleep() {
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        public void reConnect() {
//            if (!tryConnect) {
//                tryConnect = true;
//                this.run();
//            }
//        }
//
//
//        public void killSelf() {
//            this.tryConnect = false;
//            this.interrupt();
//        }
//    }
//
//    public interface ConnectStat {
//        void stat(boolean b);
//    }
//}