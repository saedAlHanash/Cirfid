package com.handheld.uhfrdemo.SAED.ViewModels;


import java.util.ArrayList;
import java.util.Date;

public class Report {

    public int wid;
    public int lid;
    public Date dt;
    public String usr;

    public ArrayList<String> epcs1;
    public ArrayList<String> epcs2;
    public ArrayList<String> epcs3;
    public ArrayList<String> epcs4;

    public void setWid(int wid) {
        this.wid = wid;
    }

    public void setLid(int lid) {
        this.lid = lid;
    }

    public void setDt(Date dt) {
        this.dt = dt;
    }

    public void setUsr(String usr) {
        this.usr = usr;
    }

    public void setEpcs1(ArrayList<String> epcs1) {
        this.epcs1 = epcs1;
    }

    public void setEpcs2(ArrayList<String> epcs2) {
        this.epcs2 = epcs2;
    }

    public void setEpcs3(ArrayList<String> epcs3) {
        this.epcs3 = epcs3;
    }

    public void setEpcs4(ArrayList<String> epcs4) {
        this.epcs4 = epcs4;
    }

    public Report() {
        this.wid = 0;
        this.lid = 0;
        this.dt = new Date();
        this.usr = "";
        this.epcs1 = new ArrayList<>();
        this.epcs2 = new ArrayList<>();
        this.epcs3 = new ArrayList<>();
        this.epcs4 = new ArrayList<>();

    }
}
