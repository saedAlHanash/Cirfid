package com.handheld.uhfrdemo.SAED.Network;

public class ConnectThread extends Thread {

    boolean tryConnect = true;

    private final static int SLEEP_TIME = 5000;
    MySocket mySocket;


    public ConnectThread(MySocket mySocket) {
        this.mySocket = mySocket;
    }

    @Override
    public void run() {
        while (tryConnect) {
            if (mySocket == null)
                break;

            if (mySocket.connect()) {
                mySocket.setMySocketConnect(true);
                tryConnect = false;
                break;

            } else
                sleep();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void killSelf() {
        this.tryConnect = false;
        this.interrupt();
    }


    public void reconnect() {
        if (!tryConnect) {
            tryConnect = true;
            this.run();
        }
    }
}
