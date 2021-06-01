package com.flower.karovelecha;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by עילאי חן on 25 אוקטובר 2016.
 */

public class CustomShiurimList implements GeoTask.Geo{

    private List<ShiurObject> events = null;
    private List<Double> Durations = new ArrayList();
    private List settlements = new ArrayList() , cordSettlements = new ArrayList();
    private int maxResults = 4;

    CustomShiurimList() {}

    public CustomShiurimList(Context context)
    {
        events = new ArrayList<>();
        settlements = Arrays.asList(context.getResources().getStringArray(R.array.settlements));
        cordSettlements = Arrays.asList(context.getResources().getStringArray(R.array.engSettlements));
    }
    public CustomShiurimList(List<ShiurObject> events, Context context)
    {
        this.events = events;
        settlements = Arrays.asList(context.getResources().getStringArray(R.array.settlements));
        cordSettlements = Arrays.asList(context.getResources().getStringArray(R.array.engSettlements));
    }

    public ShiurObject getEvent(int num) { return events.get(num); }

    public void add(List<ShiurObject> newShiurim)
    {
        events.addAll(newShiurim);
    }

    public List<ShiurObject> getEvents() {return events;}

    public Boolean isDay(int thisday, String days)
    {
        for(int i = 0 ; i < days.length(); i++)
            if(Integer.parseInt(days.substring(i,i+1))==thisday)
                return true;
        return false;
    }

    public Boolean checkDate(String start, String end)
    {

        Date min = new Date(), max = new Date();// assume these are set to something
        min.setDate(Integer.parseInt(start.split("-")[0]));
        min.setMonth(Integer.parseInt(start.split("-")[1])-1);
        min.setYear(Integer.parseInt(start.split("-")[2]) - 1900);
        //min.setHours(Integer.parseInt(start.split("-")[3]));
        //min.setMinutes(Integer.parseInt(start.split("-")[4]));

        max.setDate(Integer.parseInt(end.split("-")[0]));
        max.setMonth(Integer.parseInt(end.split("-")[1])-1);
        max.setYear(Integer.parseInt(end.split("-")[2]) - 1900);
        max.setHours(Integer.parseInt(end.split("-")[3]));
        max.setMinutes(Integer.parseInt(end.split("-")[4]));
        Date d = new Date();         // the date in question
        Boolean a = d.getTime() >= min.getTime(), b = d.getTime() <= max.getTime();

        return d.getTime() >= min.getTime() &&
                d.getTime() <= max.getTime();
    }

    public Boolean checkDate(String start, String end, Date checkDate)
    {

        Date min = new Date(), max = new Date();// assume these are set to something
        min.setDate(Integer.parseInt(start.split("-")[0]));
        min.setMonth(Integer.parseInt(start.split("-")[1])-1);
        min.setYear(Integer.parseInt(start.split("-")[2]) - 1900);
        min.setHours(0);
        min.setMinutes(0);

        max.setDate(Integer.parseInt(end.split("-")[0]));
        max.setMonth(Integer.parseInt(end.split("-")[1])-1);
        max.setYear(Integer.parseInt(end.split("-")[2]) - 1900);
        max.setHours(23);
        max.setMinutes(59);
        Date d = checkDate;         // the date in question

        Boolean a = d.getTime() >= min.getTime(), b = d.getTime() <= max.getTime();

        return d.getTime() >= min.getTime() &&
                d.getTime() <= max.getTime();
    }

    public List SortByTime(String settlement, int day) {
        int DayToCheck = day;
        int thisday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        int Hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int Min = Calendar.getInstance().get(Calendar.MINUTE);

        if (day != thisday) {
            Hour = 0;
            Min = 0;
        }
        //TfilaObject TfilaObject[] = new TfilaObject[this.events.length];

        List<ShiurObject> list = new ArrayList<>();

        //all relevant events
        for (ShiurObject e : events) {
            if (checkDate(e.getStartDate() + "-" + 0 + "-" + 0,
                    e.getEndDate() + "-" + Integer.parseInt(e.getHour()) + "-" + Integer.parseInt(e.getMin())) &&
                    isDay(DayToCheck, e.getDays()))
                if (Integer.parseInt(e.getHour()) > Hour) {
                    list.add(e);
                    e.setChosenDay(DayToCheck);
                } else if (Integer.parseInt(e.getHour()) >= Hour && Integer.parseInt(e.getMin()) >= Min &&
                        isDay(DayToCheck, e.getDays())) {
                    list.add(e);
                    e.setChosenDay(DayToCheck);
                }
        }

            //sort by hour
            for (int i = 0; i < list.size(); i++) {
                for (int j = 0; j < list.size(); j++) {
                    if (Integer.parseInt(list.get(i).getHour()) < Integer.parseInt(list.get(j).getHour())) {
                        Collections.swap(list, j, i);
                    }
                    //sort by minute
                    else if (Integer.parseInt(list.get(i).getHour()) == Integer.parseInt(list.get(j).getHour())) {
                        if (Integer.parseInt(list.get(i).getMin()) < Integer.parseInt(list.get(j).getMin())) {
                            Collections.swap(list, j, i);
                        }
                    }

                }
            }

            return list;

//        if (thisday == DayToCheck) {
//            events.removeAll(list);
//            //list.addAll(SortByTime(events, ++DayToCheck));
//            list.addAll(SortByDistance(getClosetName()));
//            return list;
//        }
//        else {
//            if (list.size() < maxResults) maxResults = list.size();
//
//            return list.subList(0, maxResults);
//        }
        }


    public List AddAnotherDay(List tempDurations, List settlements, int day)
    {
        if(day==8) day=1;
        int i;
        List list = new ArrayList();
        for(i = 0; i < tempDurations.size(); i++) {
            list = SortByTime(settlements.get(i).toString(), day);
            if(list.size()!=0) {
                return list;
            }
        }
        return list;
    }

    public Boolean needToSearch(String location) {

        for(Object obj : settlements)
            if (obj.toString().equals(location))
                return false;
        return true;
    }

    public void FindClosest(Context context, String location) {
        String allsettlements = "";
        int i = 0;
        for (i = 0; i < cordSettlements.size(); i++) {
            allsettlements += cordSettlements.get(i) + "||";
        }
        allsettlements = allsettlements.substring(0,allsettlements.length()-2);

        findDistance(location, allsettlements, context);
    }

    public List duplicate(List<ShiurObject> list)
    {
        for(int i = 0; i < list.size(); i++)
            for(int j = 0; j < list.size(); j++)
                if(i!=j && list.get(i).equals(list.get(j))) {
                    String days = list.get(i).getChosenDay()+"";
                    days += (list.get(i).getChosenDay()-1) + "";
                    list.get(i).setChosenDay(Integer.parseInt(days));
                    list.remove(j);
                }

        return list;
    }

    public List SortByDistance() {

        List<String> settlements = this.settlements;
        List<Double> tempDurations = Durations;
        List list = new ArrayList();
        Boolean nothingToFind = true;
        int i;
        String cityName = "";
        int thisday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        for(i = 0; i < tempDurations.size(); i++)
        {
            for(int j = 0; j < tempDurations.size(); j++)
                if(tempDurations.get(i)<tempDurations.get(j))
                {
                    Collections.swap(tempDurations, j, i);
                    Collections.swap(settlements, j, i);
                }
        }

        for(i = 0; i < tempDurations.size(); i++) {
            list = SortByTime(settlements.get(i).toString(), thisday);
            if (list.size()!=0)
                break;
        }
        list.addAll(AddAnotherDay(tempDurations, settlements, ++thisday));

        list = duplicate(list);

        if (list.size() < maxResults) maxResults = list.size();

        return list.subList(0,maxResults);
    }

    public void findDistance(String str_from, String str_to, Context context) {
        CleanDuration();

        if(str_to.equals("גבעות"))
            str_to = "גבעות, אלון שבות";
        else if(str_to.equals("קידר דרום"))
            str_to = "kedar south, keidar";
        else if(str_to.equals("מרחבי דוד"))
            str_to = "kedar south";
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + str_from + "&destinations=" + str_to + "&mode=driving&key=AIzaSyAQzk24-5AglCWK-G3OiySguooNUtaxGN0";
        url = url.replaceAll(" ","%20");
        new GeoTask(context).execute(url);
    }

    public List<ShiurObject> allToday(String kind, String location, String Date) {
        List<ShiurObject> listbefore = new ArrayList<ShiurObject>();
        Date date = new Date();
        date.setDate(Integer.parseInt(Date.split("-")[0]));
        date.setMonth(Integer.parseInt(Date.split("-")[1])-1);
        date.setYear(Integer.parseInt(Date.split("-")[2]) - 1900);
        Calendar time = Calendar.getInstance();
        time.setTime(date);

        for(ShiurObject ShiurObject : events)
            if (checkDate(ShiurObject.getStartDate() + "-" + Integer.parseInt(ShiurObject.getHour()) +
                    "-" + Integer.parseInt(ShiurObject.getMin()), ShiurObject.getEndDate(), date))
                if ((ShiurObject.getKind().equals(kind) || kind.equals("הכל")) &&
                        isDay(time.get(Calendar.DAY_OF_WEEK), ShiurObject.getDays()))
                    if(ShiurObject.getLocation().equals(location)) {
                        listbefore.add(ShiurObject);
                        ShiurObject.setChosenDay(time.get(Calendar.DAY_OF_WEEK));
                    }

//sort by hour
        for(int i = 0; i < listbefore.size(); i++)
        {
            for(int j = 0; j < listbefore.size(); j++)
            {
                if(Integer.parseInt(listbefore.get(i).getHour()) < Integer.parseInt(listbefore.get(j).getHour()))
                {
                    Collections.swap(listbefore, j, i);
                }
                //sort by minute
                else if(Integer.parseInt(listbefore.get(i).getHour()) == Integer.parseInt(listbefore.get(j).getHour()))
                {
                    if(Integer.parseInt(listbefore.get(i).getMin()) < Integer.parseInt(listbefore.get(j).getMin()))
                    {
                        Collections.swap(listbefore, j, i);
                    }
                }

            }
        }

        return listbefore;
    }

    public void AddEvent(ShiurObject ShiurObject) {events.add(ShiurObject);  }

    public void AddDuration(Double d)
    {
        Durations.add(d);
    }

    public void CleanDuration()
    {
        Durations = new ArrayList<>();
    }

    public Boolean isfull() {

        if(Durations.size() == settlements.size())
            return true;
        else
            return false;
    }

    public String[] tostring() {
        String []dataString = new String[events.size()];
        for(int i = 0; i < dataString.length; i++)
            dataString[i] = events.get(i).toString();
        return dataString;
    }

    public String[] Oclock()
    {
        String []dataString = new String[events.size()];
        for(int i = 0; i < dataString.length; i++)
            dataString[i] = events.get(i).getHourToPrint();
        return dataString;
    }

    @Override
    public void GetDuration(List<String> Closetsettlement) {

    }

    public String[] getAllIds()
    {
        String []Ids = new String[events.size()];
        for (int i = 0; i < events.size(); i++)
            Ids[i] = events.get(i).getLocation() + "+" + events.get(i).getSynagogaName();

        return Ids;
    }
}