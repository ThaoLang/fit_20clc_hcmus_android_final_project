package com.example.fit_20clc_hcmus_android_final_project.data_struct;

public class Post {
    private int avatar_url;
    private String account_name;
    private int main_image;
    private String description;
    private int travel_period;
    private int number_like;
    private int number_comment;

    public Post(int avatar_url, String account_name, int main_image, String description, int travel_period, int number_like,int number_comment) {
        this.avatar_url = avatar_url;
        this.account_name = account_name;
        this.main_image = main_image;
        this.description = description;
        this.travel_period = travel_period;
        this.number_like = number_like;
        this.number_comment = number_comment;
    }


    public int getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(int avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getAccount_name() {
        return account_name;
    }

    public void setAccount_name(String account_name) {
        this.account_name = account_name;
    }

    public int getNumber_like() {
        return number_like;
    }

    public void setNumber_like(int number_like) {
        this.number_like = number_like;
    }

    public int getNumber_comment() {
        return number_comment;
    }

    public void setNumber_comment(int number_comment) {
        this.number_comment = number_comment;
    }

    public int getMain_image() {
        return main_image;
    }

    public void setMain_image(int main_image) {
        this.main_image = main_image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTravel_period() {
        return travel_period;
    }

    public void setTravel_period(int travel_period) {
        this.travel_period = travel_period;
    }
}
