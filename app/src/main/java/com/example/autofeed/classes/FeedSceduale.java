package com.example.autofeed.classes;

public class FeedSceduale {
    private String food, numOfFeeds, hour, minute;

    public FeedSceduale(String food, String numOfFeeds, String hour, String minute) {
        this.food = food;
        this.numOfFeeds = numOfFeeds;
        this.hour = hour;
        this.minute = minute;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getNumOfFeeds() {
        return numOfFeeds;
    }

    public void setNumOfFeeds(String numOfFeeds) {
        this.numOfFeeds = numOfFeeds;
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
