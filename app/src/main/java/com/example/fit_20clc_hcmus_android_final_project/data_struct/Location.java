package com.example.fit_20clc_hcmus_android_final_project.data_struct;

import java.util.ArrayList;
import java.util.List;

public class Location {
    private String name;
    private String formalName; //the name presented on Google Map
    private String longitude; //kinh do
    private String latitude; //vido
    private String imageLink;
    private Float rating;
    private List<CommentInfo> commentInfoList; //includes IDs of comment lists

    public Location()
    {
        name= "None";
        formalName= "None";
        longitude= "None";
        latitude= "None";
        imageLink= "None";
        rating= 0F;
        commentInfoList = new ArrayList<CommentInfo>();
    }

    public Location(String inputName, String inputFormalName, String inputLongitude, String inputLatitude, String inputImageLink, Float inputRating)
    {
        name= inputName;
        formalName= inputFormalName;
        longitude= inputLongitude;
        latitude= inputLatitude;
        imageLink= inputImageLink;
        rating= inputRating;
        commentInfoList = new ArrayList<CommentInfo>();
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

    public String getImageLink()
    {
        return imageLink;
    }

    public Float getRating()
    {
        return rating;
    }

    public List<CommentInfo> getCommentInfoList()
    {
        return commentInfoList;
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

    public void setImageLink(String inputImageLink)
    {
        imageLink = inputImageLink;
    }

    public void setRating(Float inputRating)
    {
        rating = inputRating;
    }

    public void setCommentInfoList(ArrayList<CommentInfo> inputCommentInfoList)
    {
        commentInfoList = inputCommentInfoList;
    }
}

