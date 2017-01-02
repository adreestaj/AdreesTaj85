package com.example.adreestaj.mytimezone.Model;

import java.util.ArrayList;

/**
 * Created by Adrees Taj on 1/1/2017.
 */

public class TimeZoneList {
    private static TimeZoneList mInstance = null;
    private ArrayList<TimeZone> timeZoneList;
    private TimeZoneList(){
        timeZoneList = new ArrayList<TimeZone>();
    }
    public static TimeZoneList getInstance(){
        if (mInstance == null)
            mInstance = new TimeZoneList();

        return mInstance;
    }

    public ArrayList<TimeZone> getTimeZoneList(){
        return timeZoneList;
    }

   /* public void addService(Service service){
        serviceList.add(service);
    }*/
}
