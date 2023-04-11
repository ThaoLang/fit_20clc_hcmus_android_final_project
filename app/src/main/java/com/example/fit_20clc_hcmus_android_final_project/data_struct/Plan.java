package com.example.fit_20clc_hcmus_android_final_project.data_struct;

import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Plan implements Serializable {

    private String planId;
    private String imageLink;
    private String name;
    private String owner_email;
    private String departure_date;
    private String return_date;
    private List<String> passengers;
    private boolean isPublic;
    private String status;
    private List<Destination> listOfLocations = null;
    private Float rating;
    private List<String> listOfComments = null;

    private List<String> listOfEditors = null;

    public Plan()
    {
        imageLink = "None";
        name= "None";
        owner_email= "None";
        departure_date= "None";
        return_date= "None";
        passengers = new ArrayList<String>();
        isPublic= false;
        status = "None";
        listOfLocations = new ArrayList<Destination>();
        rating = 0F;
        listOfComments= new ArrayList<String>();
        listOfEditors = new ArrayList<String>();
    }

    public Plan(String inputPlanId, String inputName, String inputOwnerEmail, String inputDepartureDate, String inputEndDate, boolean inputIsPublic, Float inputRating, String inputImageLink)
    {
        planId = inputPlanId;
        imageLink = inputImageLink;
        name = inputName;
        owner_email= inputOwnerEmail;
        departure_date= inputDepartureDate;
        return_date= inputEndDate;
        isPublic= inputIsPublic;
        rating= inputRating;
        listOfLocations = new ArrayList<Destination>();
        listOfComments= new ArrayList<String>();
        listOfEditors = new ArrayList<String>();
    }

//    public Plan(String hoi_an_tour, String none, String inputDepartureDate, String inputEndDate, int i, boolean inputIsPublic, float inputRating) {
//    }


    public String getPlanId() {
        return planId;
    }

    public String getImageLink() {return imageLink;}
    public String getName()
    {
        return name;
    }

    public String getOwner_email()
    {
        return owner_email;
    }

    public String getDeparture_date()
    {
        return departure_date;
    }

    public String getReturn_date()
    {
        return  return_date;
    }

    public List<String> getPassengers()
    {
        return passengers;
    }

    public Float getRating()
    {
        return rating;
    }

    public boolean getPublicAttribute()
    {
        return isPublic;
    }

    public List<Destination> getListOfLocations()
    {
        return listOfLocations;
    }

    public List<String> getListOfComments()
    {
        return listOfComments;
    }

    public List<String> getSet_of_editors()
    {
        return listOfEditors;
    }

    //setter
    public String getStatus() {return status;}

    public void setPlanId(String inputPlanId)
    {
        planId = inputPlanId;
    }

    public void setImageLink(String inputImageLink)
    {
        imageLink = inputImageLink;
    }
    public void setName(String inputName)
    {
        name = inputName;
    }

    public void setOwner_email(String inputOwnerEmail)
    {
        owner_email = inputOwnerEmail;
    }

    public void setDeparture_date(String inputDepartureDate)
    {
        departure_date = inputDepartureDate;
    }

    public void setReturn_date(String inputEndDate)
    {
        return_date = inputEndDate;
    }

    public void setPublicAttribute(boolean inputPublic)
    {
        isPublic = inputPublic;
    }

    public void setRating(Float inputRating)
    {
        rating =  inputRating;
    }

    public void setListOfLocations(List<Destination> newListOfLocations)
    {
        listOfLocations = newListOfLocations;
    }

    public void setPassengers(List<String> inputPassengers)
    {
        passengers = inputPassengers;
    }

    public void setListOfComments(List<String> newListOfComments)
    {
        listOfComments = newListOfComments;
    }

    public void setListOfEditors(List<String> newListOfEditors)
    {
        listOfEditors = newListOfEditors;
    }

    public void setPublic(boolean inputPublic)
    {
        isPublic = inputPublic;
    }

    public void setStatus(String inputStatus)
    {
        status =  inputStatus;
    }

    public boolean addNewLocation(Destination newLocation)
    {
        if(newLocation != null && listOfLocations != null)
        {
            listOfLocations.add(newLocation);
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean updateLocation(int index, Destination newLocation)
    {
        if(listOfLocations == null)
        {
            return false;
        }
        if(index < 0 || index > listOfLocations.size()-1)
        {
            return false;
        }
        listOfLocations.set(index, newLocation);
        return true;
    }

    public static byte[] planToByteArray(@NotNull Plan inputPlan)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(inputPlan);
            oos.close();
            return baos.toByteArray();
        }
        catch(IOException e)
        {
            System.out.println(e);
            return null;
        }

    }

    public static Plan byteArrayToObject(@NotNull byte[] inputByteArray)
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(inputByteArray);
        try
        {
            ObjectInputStream ois = new ObjectInputStream(bais);
            Plan plan = (Plan) ois.readObject();
            return plan;
        }
        catch(IOException|ClassNotFoundException e)
        {
            System.out.println(e);
            return null;
        }
    }

}
