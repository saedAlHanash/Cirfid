//package com.handheld.uhfrdemo.SAED.Network.interfaces;
//
//import  ThreadHelper;
//
//public class MySocket1 {
//
//    //region Threads
//    ThreadHelper threadHelper;
//    //endregion
//
//    //region CallBacks
//    public ConnectStat connectStat;
//    public ReceiveMessage receiveMessage;
//
//    //endregion
//
//
//    public MySocket1(String ip, int port) {
//        threadHelper = new ThreadHelper(this, ip, port);
//        threadHelper.start();
//    }
//
//    public void setOnConnectChangeStatListener(ConnectStat connectStat) {
//        this.connectStat = connectStat;
//    }
//
//    public void setOnReceiveMessageListener(ReceiveMessage receiveMessage) {
//        this.receiveMessage = receiveMessage;
//    }
//
//    public void sendMessage(String s) {
//        threadHelper.send(s);
//    }
//
//    public void closeMySocket() {
//        threadHelper.closeMySocket();
//    }
//}
