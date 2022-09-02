package com.handheld.uhfrdemo.SAED.Network;


import com.handheld.uhfrdemo.SAED.Network.interfaces.ReceiveMessage;

import java.io.BufferedReader;
import java.io.IOException;

public class ReceiverThread extends Thread {

    private final MySocket mySocket;
    private BufferedReader mBufferIn;
    private final ReceiveMessage receiveMessage;


    public ReceiverThread(MySocket mySocket, ReceiveMessage receiveMessage) {
        this.mySocket = mySocket;
        this.receiveMessage = receiveMessage;
    }

    public void setmBufferIn(BufferedReader mBufferIn) {
        this.mBufferIn = mBufferIn;
    }

    @Override
    public void run() {
        while (true) {
            try {

                String message = mBufferIn.readLine();

                if (message == null)
                    throw new IOException();

                if (message.length() < 3)
                    return;

                message = message.substring(2);

                if (receiveMessage != null) {
                    if (message.charAt(0) == '{')
                        receiveMessage.receiveMessage(message);
                    else
                        receiveMessage.receiveMessage(null);
                }

            } catch (IOException ignore) {

                if (mySocket.socket.isClosed()) //if we close socket
                    break;

                mySocket.setMySocketConnect(false);

                break;
            }
        }
    }

}
