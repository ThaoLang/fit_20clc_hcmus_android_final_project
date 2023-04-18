package com.example.fit_20clc_hcmus_android_final_project.data_struct;

import android.util.Log;

import com.google.firebase.database.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Destination extends Location implements Serializable {
    protected String imageLink;
    protected Float rating;
    protected List<String> commentInfoList; //includes IDs of comment lists
    protected String startTime;
    protected String endTime;
    protected String startDate;
    protected String endDate;
    protected String description;
    protected String departureLocation;


    public Destination()
    {
        name= "None";
        formalName= "None";
        longitude= "None";
        latitude= "None";
        imageLink= "None";
        rating= 0F;
        startTime = "None";
        endTime = "None";
        startDate = "None";
        endDate = "None";
        description = "None";
        commentInfoList = new ArrayList<String>();

        departureLocation="None";
    }

    public String getDescription() {
        return description;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public void setDepartureLocation(String departureLocation) {
        this.departureLocation = departureLocation;
    }

    public Destination(String inputName, String inputFormalName, String inputLongitude, String inputLatitude, String inputImageLink, Float inputRating,
                       String inputStartTime, String inputEndTime, String inputStartDate, String inputEndDate, String inputDescription, String inputDepartureLocation)
    {
        name= inputName;
        formalName= inputFormalName;
        longitude= inputLongitude;
        latitude= inputLatitude;
        imageLink= inputImageLink;
        rating= inputRating;
        startTime = inputStartTime;
        endTime = inputEndTime;
        startDate = inputStartDate;
        endDate = inputEndDate;
        description = inputDescription;
        commentInfoList = new ArrayList<String>();
        departureLocation=inputDepartureLocation;
    }


    public void setImageLink(String inputImageLink)
    {
        imageLink = inputImageLink;
    }

    public void setRating(Float inputRating)
    {
        rating = inputRating;
    }

    public void setStartTime(String inputStartTime)
    {
        startTime = inputStartTime;
    }

    public void setEndTime(String inputEndTime)
    {
        endTime = inputEndTime;
    }

    public void setStartDate(String inputStartDate)
    {
        startDate = inputStartDate;
    }

    public void setEndDate(String inputEndDate)
    {
        endDate = inputEndDate;
    }

    public void setDescription(String inputDescription)
    {
        description = inputDescription;
    }

    public void setCommentInfoList(List<String> newList)
    {
        commentInfoList = newList;
    }

    public String getImageLink()
    {
        return imageLink;
    }

    public String getStartTime()
    {
        return startTime;
    }

    public String getEndTime()
    {
        return endTime;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public List<String> getCommentInfoList()
    {
        return commentInfoList;
    }


    public static byte[] toByteArray(@NotNull Destination destination)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(destination);
            return baos.toByteArray();

        } catch (IOException e) {
            Log.e("<<Serializable error>>", e.getMessage());
            return null;
        }
    }

    public static Destination toObject(@NotNull byte[] inputByteArray)
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(inputByteArray);
        try
        {
            ObjectInputStream ois = new ObjectInputStream(bais);
            Destination dest = (Destination) ois.readObject();
            return dest;
        }
        catch(IOException | ClassNotFoundException e)
        {
            Log.e("<<Serializable error>>", e.getMessage());
            return null;
        }
    }

}
