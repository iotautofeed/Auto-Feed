package com.example.autofeed.classes;

public class FeedTime {
    private String hour, minute, state;


    public FeedTime() {
        this.setHour("0");
        this.setMinute("0");
        this.setState("0");
    }

    public FeedTime(String hour, String minute, String state) {
        this.hour = hour;
        this.minute = minute;
        this.state = state;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
