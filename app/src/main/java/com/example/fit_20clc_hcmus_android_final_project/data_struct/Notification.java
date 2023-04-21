package com.example.fit_20clc_hcmus_android_final_project.data_struct;

public class Notification {
    public Notification(String title, String content, String tripId) {
        this.title = title;
        this.content = content;
        this.tripId = tripId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    private String title;
    private String content;
    private String tripId;
}
