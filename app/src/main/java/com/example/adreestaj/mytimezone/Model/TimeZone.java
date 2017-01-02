package com.example.adreestaj.mytimezone.Model;

import java.util.ArrayList;

/**
 * Created by Adrees Taj on 1/1/2017.
 */

public class TimeZone {
    private String timeZoneId;
    private String timeZoneName;
    private ArrayList<String> cities;

    public TimeZone(String timeZoneId, String timeZoneName, ArrayList<String> cities) {
        this.timeZoneId = timeZoneId;
        this.timeZoneName = timeZoneName;
        this.cities = cities;
    }

    public String getTimeZoneId() {
        return timeZoneId;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.timeZoneId = timeZoneId;
    }

    public String getTimeZoneName() {
        return timeZoneName;
    }

    public void setTimeZoneName(String timeZoneName) {
        this.timeZoneName = timeZoneName;
    }

    public ArrayList<String> getCities() {
        return cities;
    }

    public void setCities(ArrayList<String> cities) {
        this.cities = cities;
    }
}
