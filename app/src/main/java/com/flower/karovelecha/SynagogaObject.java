package com.flower.karovelecha;

import android.content.Context;

import java.util.List;

/**
 * Created by עילאי חן on 07 נובמבר 2016.
 */

public class SynagogaObject {
    private String name, address, location, longitude, latitude, data, rav;
    private List<String> gabaiim;
    private CustomShiurimList ShiurimList;
    private CustomTfilotList TfilotList;

    SynagogaObject(){

    }

    SynagogaObject(Context context) {
        TfilotList = new CustomTfilotList(context);
        ShiurimList = new CustomShiurimList(context);
    }

    SynagogaObject(String name, String address, String location, String longitude, String latitude ,String rav,
                   List<String> gabaiim, String data, Context context)
    {
        this.name = name;
        this.address = address;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
        this.rav = rav;
        this.gabaiim = gabaiim;
        this.data = data;
        TfilotList = new CustomTfilotList(context);
        ShiurimList = new CustomShiurimList(context);
    }

    SynagogaObject(String name, String address, String location, String longitude, String latitude, String rav,
                   List<String> gabaiim, String data, CustomTfilotList TfilotList, CustomShiurimList shiurimList)
    {
        this.name = name;
        this.address = address;
        this.location = location;
        this.longitude = longitude;
        this.latitude = latitude;
        this.rav = rav;
        this.gabaiim = gabaiim;
        this.data = data;
        this.TfilotList = TfilotList;
        this.ShiurimList = shiurimList;
    }

    public void setTfilotList(CustomTfilotList tfilotList) {
        TfilotList = tfilotList;
    }

    public CustomShiurimList getShiurimList() {
        return ShiurimList;
    }

    public void setShiurimList(CustomShiurimList shiurimList) {
        this.ShiurimList = shiurimList;
    }

    public CustomTfilotList getTfilotList()
    {
        return TfilotList;
    }

    public String getName() { return this.name; }

    public String getAddress() { return this.address; }

    public String getLocation() { return this.location; }

    public String getLongitude() { return this.longitude; }

    public String getLatitude() { return this.latitude; }

    public String getData() { return this.data; }

    public String getId() {
        return getLocation() + "+" + getName();
    }

    @Override
    public String toString() {
        return getName() + ", " + getLocation() + ", " + getAddress() + ", " + getLongitude() + ", " + getLatitude() + ", "+"\n" + getSynagogaInformation();
    }

    public String getRav() {
        return rav;
    }

    public void setRav(String rav) {
        this.rav = rav;
    }

    public List<String> getGabaiim() {
        return gabaiim;
    }

    public void setGabaiim(List<String> gabaiim) {
        this.gabaiim = gabaiim;
    }

    public String getGabaiimInText()
    {
        String allInf = "";

        if(gabaiim!= null) {
            if (gabaiim.size() >= 1) allInf += "\n גבאים: \n";

            for (String gabai : gabaiim) {
                allInf += gabai + "\n";
            }
        }

        return allInf;
    }

    public String getSynagogaInformation()
    {
        String allInf = "";
        if (rav!=null && !rav.equals(""))
            allInf = "רב בית הכנסת: " + rav;

        if(gabaiim!= null) {
            if (gabaiim.size() >= 1) allInf += "\n גבאים: \n";

            for (String gabai : gabaiim) {
                allInf += gabai + "\n";
            }
        }
        return allInf;
    }
}
