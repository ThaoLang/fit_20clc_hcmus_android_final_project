package com.example.fit_20clc_hcmus_android_final_project;

public class FavoriteLocation {
    private int image;
    private  String name;

    public FavoriteLocation(int image, String name) {
        this.image = image;
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
