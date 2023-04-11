package com.example.fit_20clc_hcmus_android_final_project.data_struct;

public class Chat {
    private int tripId;
    private String message;
    private int sendTime;
    private String senderName;
    private String senderPhone;

    public Chat(int tripId, String message, int sendTime, String senderName, String senderPhone) {
        this.tripId = tripId;
        this.message = message;
        this.sendTime = sendTime;
        this.senderName = senderName;
        this.senderPhone = senderPhone;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getSendTime() {
        return sendTime;
    }

    public void setSendTime(int sendTime) {
        this.sendTime = sendTime;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }
}
