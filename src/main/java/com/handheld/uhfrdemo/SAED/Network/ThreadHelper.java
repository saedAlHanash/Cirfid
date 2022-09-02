//package com.handheld.uhfrdemo.SAED.Network;
//
//import android.util.Log;
//
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.net.Socket;
//
//public class ThreadHelper extends Thread {
//
//
//    // 0,epc => json product
//    // 1, => json all
//    // 2,json report => done
//    // 3, => 404
//    // 4,epc => done
//
//    private static final String TAG = "ThreadHelper";
//
//    private final static int SLEEP_TIME = 5000;
//    private final static int TIME_OUT = 10000;
//
//    private final Object lockSend = new Object();
//
//    private boolean threadKill = true;
//
//    private PrintWriter mBufferOut;
//    private BufferedReader mBufferIn;
//
//    private final MySocket1 mySocket;
//    private Socket socket;
//
//    private String s;
//
//    boolean isConnected = false;
//
//    void setConnected(boolean connected) {
//        isConnected = connected;
//
//        if (mySocket.connectStat != null)
//            mySocket.connectStat.connectStat(connected);
//    }
//
//    String ip;
//    int port;
//
//    public ThreadHelper(MySocket1 mySocket, String ip, int port) {
//        this.ip = ip;
//        this.port = port;
//        this.mySocket = mySocket;
//    }
//
//    @Override
//    public void run() {
//        while (threadKill) {
//
//            if (!tryConnect())
//                continue;
//
//            synchronized (lockSend) {
//
//                try {
//
//                    Log.d(TAG, "run: looked");
//                    lockSend.wait();
//
//                } catch (InterruptedException ignored) {
//                }
//
//                Log.d(TAG, "run: notify and send");
//
//                mBufferOut.println(s);
//
//                try {
//
//                    String message = mBufferIn.readLine();
//
//                    Log.d(TAG, "run: receive");
//
//                    if (message == null)
//                        throw new IOException();
//
//                    if (message.length() < 3)
//                        return;
//
//                    message = message.substring(2);
//
//                    if (mySocket.receiveMessage == null)
//                        continue;
//
//                    if (message.charAt(0) == '{')
//                        mySocket.receiveMessage.receiveMessage(message);
//                    else
//                        mySocket.receiveMessage.receiveMessage(null);
//
//
//                } catch (IOException ignored) {
//
//                    if (socket.isClosed())
//                        break;
//
//                    setConnected(false);
//
//                }
//            }
//
//        }
//
//    }
//
//    private boolean tryConnect() {
//
//        if (!isConnected) {
//            try {
//                Log.d(TAG, "tryConnect: try ");
//
//                socket = new Socket();
//                socket.connect(new InetSocketAddress(
//                        InetAddress.getByName(ip), port), TIME_OUT);
//
//                setConnected(true);
//
//                initIoStream(socket);
//                Log.d(TAG, "tryConnect: connected ");
//
//            } catch (IOException e) {
//                sleep();
//                return false;
//            }
//        }
//
//        return isConnected;
//    }
//
//
//    private void sleep() {
//        try {
//            Thread.sleep(SLEEP_TIME);
//        } catch (InterruptedException ignored) {
//        }
//    }
//
//    public void killSelf() {
//        this.threadKill = false;
//    }
//
//    public void closeMySocket() {
//
//        new Thread(() -> {
//            try {
//
//                if (socket != null)
//                    socket.close();
//
//                if (mBufferOut != null)
//                    mBufferOut.close();
//
//                if (mBufferIn != null)
//                    mBufferIn.close();
//
//                killSelf();
//
//            } catch (Exception ignored) {
//            }
//
//        }).start();
//    }
//
//    public void send(String s) {
//        if (!isConnected)
//            return;
//
//        this.s = s;
//
//        synchronized (lockSend) {
//            lockSend.notify();
//        }
//
//    }
//
//    private void initIoStream(Socket socket) throws IOException {
//
//        mBufferOut = new PrintWriter(new BufferedWriter(
//                new OutputStreamWriter(socket.getOutputStream())), true);
//
//        mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//    }
//}
