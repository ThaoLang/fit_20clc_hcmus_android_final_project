package com.example.fit_20clc_hcmus_android_final_project;

public class Post {
    private int avatar_url;
    private String account_name;
    private String create_date;
    private int main_image;
    private String description;
    private int travel_period;
    private int cost;

    public Post(int avatar_url, String account_name, String create_date, int main_image, String description, int travel_period, int cost) {
        this.avatar_url = avatar_url;
        this.account_name = account_name;
        this.create_date = create_date;
        this.main_image = main_image;
        this.description = description;
        this.travel_period = travel_period;
        this.cost = cost;
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

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
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

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
