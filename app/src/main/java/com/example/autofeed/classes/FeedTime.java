package com.example.autofeed.classes;

public class FeedTime {
    private String hour, minute;


    public FeedTime() {
        this.setHour("0");
        this.setMinute("0");
    }

    public FeedTime(String hour, String minute) {
        this.hour = hour;
        this.minute = minute;
    }

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
