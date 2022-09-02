package com.handheld.uhfrdemo.SAED.Network;

import android.util.Log;


import com.handheld.uhfrdemo.SAED.Network.interfaces.ConnectStat;
import com.handheld.uhfrdemo.SAED.Network.interfaces.ReceiveMessage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class MySocket {

    //region mySocket
    Socket socket;
    private boolean isConnect = false;
    InetAddress inetAddress;
    SocketAddress socketAddress;
    //endregion

    //region IOBuffer
    BufferedReader mBufferIn;
    PrintWriter mBufferOut;

    //endregion

    //region Threads
     ConnectThread connectThread;
     SendThread sendThread;
     ReceiverThread receiverThread;

    //endregion

    //region CallBacks
    public ConnectStat connectStat;
    public ReceiveMessage receiveMessage;

    //endregion


    public MySocket() {
        connectThread = new  ConnectThread(this);

        sendThread = new  SendThread(this);
        sendThread.start();
        socket = new Socket();
    }

    public boolean isMySocketConnect() {
        return isConnect;
    }

    public void setMySocketConnect(boolean connect) {
        isConnect = connect;

        if (connectStat != null)
            connectStat.connectStat(connect);

        if (!connect)
            reConnect();
    }

    public void setOnConnectChangeStatListener(ConnectStat connectStat) {
        this.connectStat = connectStat;
    }

    public void setOnReceiveMessageListener(ReceiveMessage receiveMessage) {
        this.receiveMessage = receiveMessage;
    }

    private void initIoStream() throws IOException {

        mBufferOut = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream())), true);

        mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void sendMessage(String s) {
        sendThread.sendString(s);
    }

    public void connect(String ip, int port) {

        this.ip = ip;
        this.port = port;


        if (!connectThread.isAlive())
            connectThread = new  ConnectThread(this);

        connectThread.start();
    }

    String ip;
    int port;

    private static final String TAG = "MainActivity";

    protected boolean connect() {
        try {

            if (socketAddress == null)
                socketAddress = new InetSocketAddress(InetAddress.getByName(ip), port);

            socket = new Socket();
            socket.connect(socketAddress);// الاتصال بال socket

            initIoStream();

            receiverThread = new  ReceiverThread(this, receiveMessage);

            receiverThread.setmBufferIn(mBufferIn);
            sendThread.setmBufferOut(mBufferOut);

            receiverThread.start();

            Log.d(TAG, "connect: true");
            return true;

        } catch (IOException e) {
            Log.e(TAG, "connect: false", e);

            return false;
        }
    }

    protected void reConnect() {
        connectThread.reconnect();
    }

    public void closeMySocket() {
        new Thread(() -> {
            try {

                if (socket != null)
                    socket.close();

                if (mBufferOut != null)
                    mBufferOut.close();

                if (mBufferIn != null)
                    mBufferIn.close();

                if (connectThread != null)
                    connectThread.killSelf();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }).start();
    }
}
