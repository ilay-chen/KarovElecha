package com.flower.karovelecha;

import java.util.Calendar;
import java.util.Date;

import static com.flower.karovelecha.CustomMikveList.splitString;

/**
 * Created by עילאי חן on 31 אוקטובר 2016.
 */

public class MikveObject {
        private String phone;
        private String timeforatoe;
        private String timeforf;
        private String timeforg;
        private String location;
        private String address;
        private String subject;
        private String gender;
        private String price;
        private String longitude, latitude;
        private String HourInText;

        public MikveObject()
        {

        }
        public MikveObject(String location, String adress, String subject, String phone, String timeforatoe, String hourInText
                ,String timeforf, String timeforg, String gender, String price , String longitude, String latitude)
        {
            this.phone = phone;
            this.timeforatoe = timeforatoe;
            this.longitude = longitude;
            this.latitude = latitude;
            this.timeforf = timeforf;
            this.HourInText = hourInText;
            this.location = location;
            this.address = adress;
            this.subject = subject;
            this.timeforg = timeforg;
            this.gender = gender;
            this.price = price;
        }

        public String getId() {
        return getSubject() + "+" + getAddress();
    }

        public String getTimeforatoeText()
        {
            if(HourInText!=null && !HourInText.equals(""))
                return HourInText;

            return this.timeforatoe;
        }

        public String getTimeforatoe() {
            return this.timeforatoe;
        }

        public String getTimeforf() {
            if (!(timeforf.split(splitString).length>1))
                return timeforf.substring(0, timeforf.length()- 4);

            if (timeforf.contains("אין מידע"))
                return timeforf.substring(0, timeforf.indexOf("אין מידע") - 4);

            return this.timeforf; }

        public String getTimeforg() {
            if (!(timeforg.split(splitString).length>1))
                return timeforg.substring(0, timeforg.length()- 4);

            if (timeforg.contains("אין מידע"))
                return timeforg.substring(0, timeforg.indexOf("אין מידע") - 4);

            return this.timeforg;
        }

        public String getPhone() {return this.phone; }

        public String getLocation() {return this.location;}

        public String getAddress() {
            if (address==null) address = "אין מידע";
            return this.address;
        }

        public String getSubject() {
            return this.subject;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getcurrentTime() {
            switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
            {
                case 1: return getTimeforatoeText();
                case 2: return getTimeforatoeText();
                case 3: return getTimeforatoeText();
                case 4: return getTimeforatoeText();
                case 5: return getTimeforatoeText();
                case 6: return getTimeforf();
                case 7: return getTimeforg();
                default: return "אין מידע";
            }

        }

    public String getanotherTime(Date c) {
        switch (c.getDay()+1)
        {
            case 1: return getTimeforatoe();
            case 2: return getTimeforatoe();
            case 3: return getTimeforatoe();
            case 4: return getTimeforatoe();
            case 5: return getTimeforatoe();
            case 6: return getTimeforf();
            case 7: return getTimeforg();
            default: return "אין מידע";
        }

    }

    @Override
    public String toString() {
        return getSubject() + ", " + getcurrentTime() + ", " + "\n" + "כתובת: "  + getAddress()
                + ", טל': " + getonlyPhone().split("G")[0];
    }

//        @Override
//        public String toString() {
//            return getSubject() + ", " + getLocation() + ", " + getAddress() + ", " + getcurrentTime()
//                    + ", מקווה " + getGender() + ", מחיר כניסה: " + getPrice() + " ש''ח.";
//        }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getonlyPhone() {
        if(getPhone().contains("G"))
            return getPhone().split("G")[1].replace("\n","");
        else return "אין מספר";
    }

    public String getLongitude() {
        if(longitude==null) longitude = "0.0";
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        if(latitude==null) latitude = "0.0";
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getHourInText() {
        return HourInText;
    }

    public void setHourInText(String hourInText) {
        HourInText = hourInText;
    }
}