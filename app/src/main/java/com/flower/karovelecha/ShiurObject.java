package com.flower.karovelecha;

import java.util.Date;

/**
 * Created by עילאי חן on 29 אוקטובר 2016.
 */

public class ShiurObject {
    private String kind;
    private Date dateTime;
    private String Days;
    private String Hour;
    private String Min;
    private String location;
    private String address;
    private String subject;
    private String startDate;
    private String endDate;
    private String synagoga;
    private int ChosenDay = 0;
    private String HourInText;

    public ShiurObject()
    {

    }
    public ShiurObject(String days, String hour, String min, String location,
                       String address, String subject, String kind, String startDate, String endDate, SynagogaObject synagoga)
    {
        this.dateTime = new Date();
        this.Days = days;
        this.Hour = hour;
        this.Min = min;
        this.location = location;
        this.address = address;
        this.subject = subject;
        this.kind = kind;
        this.startDate = startDate;
        this.synagoga = synagoga.getName();
        this.endDate = endDate;
    }

    public Date getDateObject() {
        return this.dateTime;
    }

    public String getDays() {  return this.Days; }

    public String getDaysName()
    {
        String days = "";
        for(int i = 0 ; i < Days.length(); i++)
            days += getDay(Integer.parseInt(Days.substring(i,i+1))) + ", ";
        return days;
    }

    public String getHour() { return this.Hour; }

    public String getStartDate() { return this.startDate; }

    public String getEndDate() { return this.endDate; }

    public String getMin() {return this.Min; }

    public String getLocation() {return this.location;}

    public String getAddress() {return this.address;}

    public String getSubject() {
        return this.subject;
    }

    public void setDays(String days) { this.Days = days; }

    public void setHour(String hour) { this.Hour = hour; }

    public void setMin(String min) { this.Min = min; }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setAddress(String adress) {
        this.address = adress;
    }

    public String getKind() {return this.kind; }

    public void setStartDate(String startDate) { this.startDate = startDate; }

    public void setEndDate(String endDate) { this.endDate = endDate; }

    public void setKind(String kind) {this.kind = kind; }

    public void setSynagoga(String synagoga) { this.synagoga = synagoga; }

    public String getSynagoga()  { return this.synagoga; }

    public String getSynagogaName() {return this.synagoga; }

    @Override
    public String toString() {
        return showDays(getChosenDay()) + ", " + "שם השיעור: " + getSubject() + ", " + getSynagogaName() + ", "
                + getLocation() + ", " + "כתובת: " + getAddress() + ", " + "נושא השיעור: " + kind + ".";
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

    public String getDay(int d) {
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