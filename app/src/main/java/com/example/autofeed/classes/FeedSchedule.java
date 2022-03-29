package com.example.autofeed.classes;

public class FeedSchedule {
    private int food, numOfFeeds, hour, minute; // set class variables

    public FeedSchedule(int food, int numOfFeeds, int hour, int minute) { // create constructor
        this.food = food;
        this.numOfFeeds = numOfFeeds;
        this.hour = hour;
        this.minute = minute;
    }

    // create getters and setters
    public int getFood() {
        return food;
    }

    public void setFood(int food) {
        this.food = food;
    }

    public int getNumOfFeeds() {
        return numOfFeeds;
    }

    public void setNumOfFeeds(int numOfFeeds) {
        this.numOfFeeds = numOfFeeds;
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
}
