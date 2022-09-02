package com.handheld.uhfrdemo.SAED.ViewModels;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class All {

    public static class Epc {
        public String epc;
        @SerializedName("pn")
        public String productName;

        @NonNull
        @Override
        public String toString() {
            return "Epc{" +
                    "epc='" + epc + '\'' +
                    ", pn='" + productName + '\'' +
                    '}';
        }
    }

    public static class Location {
        public int id;
        public String name;
        public ArrayList<Epc> epcs;

        @NonNull
        @Override
        public String toString() {
            return "Location{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", epcs=" + epcs +
                    '}';
        }
    }

    public int id;
    public String name;
    public ArrayList<Location> locations;

    @NonNull
    @Override
    public String toString() {
        return "All{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", locations=" + locations +
                '}';
    }
}
