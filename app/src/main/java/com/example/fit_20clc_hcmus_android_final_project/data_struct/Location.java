package com.example.fit_20clc_hcmus_android_final_project.data_struct;

import java.util.ArrayList;
import java.util.List;

public class Location {
    protected String name;
    protected String formalName; //the name presented on Google Map
    protected String longitude; //kinh do
    protected String latitude; //vido

    public Location()
    {
        name= "None";
        formalName= "None";
        longitude= "None";
        latitude= "None";
    }

    public Location(String inputName, String inputFormalName, String inputLongitude, String inputLatitude)
    {
        name= inputName;
        formalName= inputFormalName;
        longitude= inputLongitude;
        latitude= inputLatitude;
    }

    public String getName()
    {
        return name;
    }

    public String getFormalName()
    {
        return formalName;
    }

    public String getLongitude()
    {
        return longitude;
    }

    public String getLatitude()
    {
        return latitude;
    }


    public void setName(String inputName)
    {
        name = inputName;
    }

    public void setFormalName(String inputFormalName)
    {
        formalName = inputFormalName;
    }

    public void setLongitude(String inputLongitude)
    {
        longitude = inputLongitude;
    }

    public void setLatitude(String inputLatitude)
    {
        latitude = inputLatitude;
    }

}

