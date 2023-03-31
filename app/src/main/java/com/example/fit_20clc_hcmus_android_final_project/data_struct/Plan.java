package com.example.fit_20clc_hcmus_android_final_project.data_struct;

import java.util.ArrayList;
import java.util.List;

public class Plan {

    private String name;
    private String owner_email;
    private String departure_date;
    private String end_date;
    private int init_number_of_people;
    private boolean isPublic;
    private List<Location> listOfLocations = null;
    private Float rating;
    private List<String> listOfComments = null;

    public Plan()
    {
        name= "None";
        owner_email= "None";
        departure_date= "None";
        end_date= "None";
        init_number_of_people= 0;
        isPublic= false;
        listOfLocations = new ArrayList<Location>();
        rating = 0F;
        listOfComments= new ArrayList<String>();
    }

    public Plan(String inputName, String inputOwnerEmail, String inputDepartureDate, String inputEndDate,
                int initPeople, boolean inputIsPublic, Float inputRating)
    {
        name = inputName;
        owner_email= inputOwnerEmail;
        departure_date= inputDepartureDate;
        end_date= inputEndDate;
        init_number_of_people = initPeople;
        isPublic= inputIsPublic;
        rating= inputRating;
        listOfLocations = new ArrayList<Location>();
        listOfComments= new ArrayList<String>();
    }

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

    public String getEnd_date()
    {
        return  end_date;
    }

    public int getInit_number_of_people()
    {
        return init_number_of_people;
    }

    public Float getRating()
    {
        return rating;
    }

    public boolean getPublicAttribute()
    {
        return isPublic;
    }

    public List<Location> getListOfLocations()
    {
        return listOfLocations;
    }

    public List<String> getListOfComments()
    {
        return listOfComments;
    }

    //setter

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

    public void setEnd_date(String inputEndDate)
    {
        end_date = inputEndDate;
    }

    public void setPublicAttribute(boolean inputPublic)
    {
        isPublic = inputPublic;
    }

    public void setRating(Float inputRating)
    {
        rating =  inputRating;
    }

    public void setListOfLocations(List<Location> newListOfLocations)
    {
        listOfLocations = newListOfLocations;
    }

    public void setInit_number_of_people(int newInitPeople)
    {
        init_number_of_people = newInitPeople;
    }

    public void setListOfComments(List<String> newListOfComments)
    {
        listOfComments = newListOfComments;
    }

    public boolean addNewLocation(Location newLocation)
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

    public boolean updateLocation(int index, Location newLocation)
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

}
