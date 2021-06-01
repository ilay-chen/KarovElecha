package com.flower.karovelecha;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by עילאי חן on 31 אוקטובר 2016.
 */

public class CustomMikveList implements GeoTask.Geo{

    private List<MikveObject> events = null;
    private List<Double> Durations = new ArrayList();
    private List settlements = new ArrayList(), cordSettlements = new ArrayList();
    private int maxResults = 2;
    static String splitString = " עד ";

    public CustomMikveList(Context context)
    {
        events = new ArrayList<>();
        settlements = Arrays.asList(context.getResources().getStringArray(R.array.settlements));
        cordSettlements = Arrays.asList(context.getResources().getStringArray(R.array.engSettlements));
    }
//    public CustomMikveList(List<MikveObject> events, List<String> settlements)
//    {
//        this.events = events;
//        this.settlements = settlements;
//        //cordSettlements = Arrays.asList(getResources().getStringArray(R.array.engSettlements));
//    }

    public CustomMikveList(List<MikveObject> events, Context context)
    {
        this.events = events;
        this.settlements = Arrays.asList(context.getResources().getStringArray(R.array.settlements));
        cordSettlements = Arrays.asList(context.getResources().getStringArray(R.array.engSettlements));
    }

    public MikveObject getEvent(int num) { return events.get(num); }

    public List<MikveObject> getEvents() {return events;}

    public Boolean inRange(MikveObject e)
    {
        Date min = new Date(), max = new Date();// assume these are set to something
        min.setHours(Integer.parseInt(e.getcurrentTime().split(splitString)[0].split(":")[0]));
        min.setMinutes(Integer.parseInt(e.getcurrentTime().split(splitString)[0].split(":")[1]));
        max.setHours(Integer.parseInt(e.getcurrentTime().split(splitString)[1].split(":")[0]));
        max.setMinutes(Integer.parseInt(e.getcurrentTime().split(splitString)[1].split(":")[1]));
        Date d = new Date();         // the date in question

        return d.getTime() >= min.getTime() &&
                d.getTime() <= max.getTime();
    }

//    public List SortByTime(List<MikveObject> events)
//    {
//        int thisday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
//        int thishour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
//        int thismin = Calendar.getInstance().get(Calendar.MINUTE);
//        //TfilaObject TfilaObject[] = new TfilaObject[this.events.length];
//
//        List<MikveObject> list = new ArrayList<>();
//
//        //check if is friday or sturday - not needed to check.
//        if (thisday==6 || thisday==7) return events;
//
//        //all today events
//        for(MikveObject e : events)
//                //if(inRange(e)) //check if mikve is open now, if not, wont show him.
//                    list.add(e);
//
//        //sort by hour
//        for(int i = 0; i < list.size(); i++)
//        {
//            for(int j = 0; j < list.size(); j++)
//            {
//                if(Integer.parseInt(list.get(i).getcurrentTime().split("-")[0].split(":")[0]) <
//                        Integer.parseInt(list.get(j).getcurrentTime().split("-")[0].split(":")[0]))
//                {
//                    Collections.swap(list, j, i);
//                }
//                //sort by minute
//                else if(Integer.parseInt(list.get(i).getcurrentTime().split("-")[0].split(":")[0]) ==
//                        Integer.parseInt(list.get(j).getcurrentTime().split("-")[0].split(":")[0]))
//                {
//                    if(Integer.parseInt(list.get(i).getcurrentTime().split("-")[0].split(":")[1]) <
//                            Integer.parseInt(list.get(j).getcurrentTime().split("-")[0].split(":")[1]))
//                    {
//                        Collections.swap(list, j, i);
//                    }
//                }
//
//            }
//        }
//
//        return list;
//    }

    public List SortByTime(String settlement, int day)
    {
        int DayToCheck = day;
        int thisday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        List<MikveObject> list = new ArrayList<>();

        //all today events
        for(MikveObject e : events)
            if(e.getLocation().equals(settlement))
            //if(inRange(e)) //check if mikve is open now, if not, wont show him.
            list.add(e);

        //check if is friday or sturday - not needed to check.
        if (thisday==6 || thisday==7) return list;

        //sort by hour
        for(int i = 0; i < list.size(); i++)
        {
            for(int j = 0; j < list.size(); j++)
            {
                if(Integer.parseInt(list.get(i).getTimeforatoe().split(splitString)[0].split(":")[0]) <
                        Integer.parseInt(list.get(j).getTimeforatoe().split(splitString)[0].split(":")[0]))
                {
                    Collections.swap(list, j, i);
                }
                //sort by minute
                else if(Integer.parseInt(list.get(i).getTimeforatoe().split(splitString)[0].split(":")[0]) ==
                        Integer.parseInt(list.get(j).getTimeforatoe().split(splitString)[0].split(":")[0]))
                {
                    if(Integer.parseInt(list.get(i).getTimeforatoe().split(splitString)[0].split(":")[1]) <
                            Integer.parseInt(list.get(j).getTimeforatoe().split(splitString)[0].split(":")[1]))
                    {
                        Collections.swap(list, j, i);
                    }
                }

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

//    public List SortByDistance(String location) {
//        List<MikveObject> listbefore = new ArrayList<MikveObject>();
//
//        for(MikveObject MikveObject : events)
//            if (MikveObject.getLocation().equals(location))
//                listbefore.add(MikveObject);
//
//        return SortByTime(listbefore);
//    }

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

    public List duplicate(List<MikveObject> list)
    {
        for(int i = 0; i < list.size(); i++)
            for(int j = 0; j < list.size(); j++)
                if(i!=j && list.get(i).equals(list.get(j))) {
                    list.remove(j);
                }

        return list;
    }

    public List<MikveObject> allToday(String location) {
        List<MikveObject> listbefore = new ArrayList<MikveObject>();
        int thisday = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        return SortByTime(location, thisday);
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

//    public String getClosetName(List<String> settlements) {
//        int i;
//        String cityName = "";
//        for (i = 0; i < Durations.size(); i++) {
//            if(cityName != "") {i--;break;}
//            for (int j = 0; j < Durations.size(); j++) {
//                if (i != j && Durations.get(i).doubleValue() > Durations.get(j).doubleValue())
//                    break;
//                if(j == Durations.size()-1) {
//                    cityName = settlements.get(i).toString();
//                    break;
//                }
//            }
//        }
//        if(i == Durations.size()) --i;
//        if(i==-1) i =0;
//
//        if(Durations.size() == 0) {
//            return "empty";
//        }
//
//        List list = SortByDistance(settlements.get(i).toString());
//        if(list.size() == 0)
//        {
//            Durations.remove(i);
//            settlements.remove(i);
//            return getClosetName(settlements);
//        }
//
//        Log.d("test", settlements.get(i).toString() + " ," + i);
//        return settlements.get(i).toString();
//    }

//    public List searchMore(String location)
//    {
//        List<MikveObject> listbefore = new ArrayList<MikveObject>();
//        for(MikveObject MikveObject : events)
//                if(MikveObject.getLocation().equals(location))
//                    listbefore.add(MikveObject);
//
//        //return SortByTime(listbefore);
//        return listbefore;
//    }

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


    public void AddEvent(MikveObject MikveObject) {events.add(MikveObject);  }

    public void GetDuration (String result) {
        if(!result.equals("err")) {
            String res[] = result.split(",");
            Double min = Double.parseDouble(res[0]) / 60;
            Durations.add(min);
        }
        else
            Durations.add(10000.000);
        //tv_result1.setText(min.toString());
    }

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
        String[]dataString = new String[events.size()];
        for(int i = 0; i < dataString.length; i++)
            dataString[i] = events.get(i).toString();
        return dataString;
    }

    public String[] Oclock()
    {
        String []dataString = new String[events.size()];
        for(int i = 0; i < dataString.length; i++)
            //dataString[i] = events.get(i).getcurrentTime();
            dataString[i] = "";
        return dataString;
    }

    public String[] Oclock(String Date)
    {
        Date checkDate = new Date();
        checkDate.setDate(Integer.parseInt(Date.split("-")[0]));
        checkDate.setMonth(Integer.parseInt(Date.split("-")[1])-1);
        checkDate.setYear(Integer.parseInt(Date.split("-")[2]) - 1900);

        String []dataString = new String[events.size()];
        for(int i = 0; i < dataString.length; i++)
            dataString[i] = events.get(i).getanotherTime(checkDate);
        return dataString;
    }

    public MikveObject getMikveByName(String name)
    {
        for(MikveObject MikveObject : events)
            if(MikveObject.getSubject().equals(name))
                return MikveObject;

        return null;
    }

    public MikveObject getMikveById(String id) {
        for(MikveObject object: events)
            if(object.getId().equals(id))
                return  object;
        return null;
    }

    @Override
    public void GetDuration(List<String> Closetsettlement) {

    }

    public String[] getAllIds()
    {
        String []Ids = new String[events.size()];
        for (int i = 0; i < events.size(); i++)
            Ids[i] = events.get(i).getSubject() + "+" + events.get(i).getAddress();

        return Ids;
    }
}
