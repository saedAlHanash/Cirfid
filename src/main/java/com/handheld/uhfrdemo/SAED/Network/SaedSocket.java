package com.handheld.uhfrdemo.SAED.Network;

import org.java_websocket.client.WebSocketClient;

public class SaedSocket {
    Test test;
    WebSocketClient webSocketClient;
    public boolean isClosed;

    public SaedSocket(WebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;

        test = new Test(webSocketClient);
        test.start();

        webSocketClient.connect();
    }

    public void send(String s) {
        test.sendString(s);
    }

    public void close() {
        test.killSelf();
        webSocketClient.close();
        isClosed = true;
    }

}
