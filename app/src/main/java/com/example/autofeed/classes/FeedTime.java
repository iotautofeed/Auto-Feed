package com.example.autofeed.classes;

public class FeedTime {
    private int hour, minute, state;


    public FeedTime() {
        this.setHour(0);
        this.setMinute(0);
        this.setState(0);
    }

    public FeedTime(int hour, int minute, int state) {
        this.hour = hour;
        this.minute = minute;
        this.state = state;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
