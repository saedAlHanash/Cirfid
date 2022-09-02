package com.handheld.uhfrdemo.SAED.Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class SendThread extends Thread {

    private boolean threadKill = true;
    private final Object lockSend = new Object();

    private PrintWriter mBufferOut;
    private BufferedReader mBufferIn;
    private final MySocket mySocket;

    private String s;

    public SendThread(MySocket mySocket) {
        this.mySocket = mySocket;
    }

    public void setmBufferOut(PrintWriter mBufferOut) {
        this.mBufferOut = mBufferOut;
    }

    public void setmBufferIn(BufferedReader mBufferIn) {
        this.mBufferIn = mBufferIn;
    }

    @Override
    public void run() {
        synchronized (lockSend) {
            while (threadKill) {

                try {
                    lockSend.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (mBufferOut == null || mBufferIn == null)
                    continue;

                mBufferOut.println(s);
                mBufferOut.flush();

                try {

                    mBufferIn.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(20);
        } catch (InterruptedException ignored) {
        }
    }

    public boolean sendString(String s) {
        this.s = s;

        if (!mySocket.isMySocketConnect()
                || mBufferOut == null)
            return false;

        synchronized (lockSend) {
            lockSend.notify();
        }

        return true;
    }

    public void killSelf() {
        this.threadKill = false;
        synchronized (lockSend) {
            lockSend.notify();
        }
    }
}
