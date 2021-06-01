package com.flower.karovelecha;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.jar.Pack200;

/**
 * Created by עילאי חן on 09 אוקטובר 2016.
 */
public class TfilaObject{

    private String kind;
    private Date dateTime;
    private String Days;
    private String Hour;
    private String Min;
    private String location;
    private String address;
    private String subject;
    private String synagoga;
    private int ChosenDay = 0;
    private String HourInText;

    public TfilaObject()
    {

    }

    public TfilaObject(String days, String hour, String min, String subject, String kind, SynagogaObject synagoga)
    {
        this.dateTime = new Date();
        this.Days = days;
        this.Hour = hour;
        this.Min = min;
        this.synagoga = synagoga.getName();
        this.subject = subject;
        this.kind = kind;
        this.address = synagoga.getAddress();
        this.location = synagoga.getLocation();
    }

    public String getDays() {  return this.Days; }

    public String getDaysName() {
        String days = "";
        for(int i = 0 ; i < Days.length(); i++)
            days += getDay(Integer.parseInt(Days.substring(i,i+1))) + ", ";
        return days;
    }

    public String getHour() { return this.Hour; }

    public String getMin() {return this.Min; }

    public String getLocation() {return this.location;}

    public String getAddress() {return this.address;}

    public String getSubject() {
        return this.subject;
    }

    public String getSynagoga()  { return this.synagoga; }

    public void setSynagoga(String synagoga) { this.synagoga = synagoga; }

    public void setDays(String days) { this.Days = days; }

    public void setHour(String hour) { this.Hour = hour; }

    public void setMin(String min) { this.Min = min; }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getKind() {return this.kind; }

    public void setKind(String kind) {this.kind = kind; }

    public String getSynagogaName() {return this.synagoga; }

    @Override
    public String toString() {
        return showDays(getChosenDay()) + ", " +getSubject() + ", " + getSynagogaName() + ", " +
                getLocation() + ", " + "כתובת: " + getAddress() + ", " + "נוסח: " + kind+ ".";
    }

    public String showDays(int days)
    {
        while (days!=0) {

            if(days<10)
                return getDay(days);

            return getDay(days%10) + ", " + showDays(days/10);
        }
        return "לא פעיל באף יום";
    }

    public static String getDay(int d) {

        switch (d) {
            case 0:
                return "";
            case 1:
                return  "יום א'";
            case 2:
                return "יום ב'";
            case 3:
                return "יום ג'";
            case 4:
                return "יום ד'";
            case 5:
                return "יום ה'";
            case 6:
                return "יום ו'";
            case 7:
                return  "שבת";
            case 8:
                return "יום א'";

            default: return "לא פעיל באף יום";
        }
    }

    public int getChosenDay() {
        return ChosenDay;
    }

    public void setChosenDay(int chosenDay) {
        ChosenDay = chosenDay;
    }

    public String getHourInText() {
        return HourInText;
    }

    public void setHourInText(String hourInText) {
        HourInText = hourInText;
    }

    public String getHourToPrint()
    {
        if(getHourInText()!= null && !getHourInText().equals(""))
            return HourInText;
        else
            return getHour() + ":" + getMin();
    }
}