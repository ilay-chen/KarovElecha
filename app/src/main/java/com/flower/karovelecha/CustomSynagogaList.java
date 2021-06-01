package com.flower.karovelecha;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by עילאי חן on 07 נובמבר 2016.
 */

public class CustomSynagogaList {

    private List<SynagogaObject> events = null;

    public CustomSynagogaList()
    {

    }
    public CustomSynagogaList(List<SynagogaObject> events)
    {
        this.events = events;
    }


    public SynagogaObject getEvent(int num) { return events.get(num); }

    public List<String> getNames(String settlement)
    {
        List<String> names = new ArrayList<>();
        for(SynagogaObject object: events)
            if(object.getLocation().equals(settlement))
                names.add(object.getName());

        return names;
    }

    public List<String> getIds(String settlement)
    {
        List<String> Ids = new ArrayList<>();
        for(SynagogaObject object: events)
            if(object.getLocation().equals(settlement))
                Ids.add(object.getId());

        return Ids;
    }

    public  CustomTfilotList getAllTfilot(Context context)
    {
        CustomTfilotList allTfilot = new CustomTfilotList(context);
        for(SynagogaObject so : events)
            if(so.getTfilotList() != null)
                allTfilot.add(so.getTfilotList().getEvents());

        return allTfilot;
    }

    public  CustomShiurimList getAllShiurim(Context context)
    {
        CustomShiurimList allShiurim = new CustomShiurimList(context);
        for(SynagogaObject so : events)
            if(so.getShiurimList() != null)
                allShiurim.add(so.getShiurimList().getEvents());

        return allShiurim;
    }

    public SynagogaObject getSynagogaById(String id) {
        for(SynagogaObject object: events)
            if(object.getId().equals(id))
                return  object;
        return null;
    }

    public List<SynagogaObject> getEvents()
    {
        return this.events;
    }

    public void setEvents(List<SynagogaObject> events) { this.events = events;}

    public String[] tostring() {
        String []dataString = new String[events.size()];
        for(int i = 0; i < dataString.length; i++)
            dataString[i] = events.get(i).toString();
        return dataString;
    }
}
