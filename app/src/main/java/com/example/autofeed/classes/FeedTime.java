package com.example.autofeed.classes;

public class FeedTime {
    private String hour, minute; // set class variables

    public FeedTime() { //create constructor
        this.setHour("0");
        this.setMinute("0");
    }

    public FeedTime(String hour, String minute) {
        this.hour = hour;
        this.minute = minute;
    }

    // create getters and setters
    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMinute() {
        return minute;
    }

    public void setMinute(String minute) {
        this.minute = minute;
    }

}
