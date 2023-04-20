package com.example.fit_20clc_hcmus_android_final_project.data_struct;

public class Chat {
    private String tripId;
    private String message;
    private int sendTime;
    private String senderName;
    private String senderEmail;
    private String senderAvatarURL;

    public String getSenderAvatarURL() {
        return senderAvatarURL;
    }

    public void setSenderAvatarURL(String senderAvatarURL) {
        this.senderAvatarURL = senderAvatarURL;
    }

    public Chat(String tripId, String message, int sendTime, String senderName, String senderEmail, String senderImageURL) {
        this.tripId = tripId;
        this.message = message;
        this.sendTime = sendTime;
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.senderAvatarURL = senderImageURL;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
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

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }
}
