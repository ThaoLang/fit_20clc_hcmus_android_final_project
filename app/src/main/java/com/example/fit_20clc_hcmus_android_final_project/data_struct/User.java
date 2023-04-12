package com.example.fit_20clc_hcmus_android_final_project.data_struct;

import com.google.firebase.database.IgnoreExtraProperties;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


@IgnoreExtraProperties
public class User {
    private String _username;
    private String _useremail;
    private String _userphone;
    private String _useraddress;

    private List<String> _plans;

    private List<String> _favorite_locations;
    private String _userbio;

    public User()
    {
        _username = "00";
        _useremail = "None";
        _userphone = "000";
        _plans = new ArrayList<>();
        _favorite_locations = new ArrayList<>();
        _useraddress = "0000";
        _userbio = "00000";
    }

    public User(String name, String email, String phone, String address, String bio, List<String> plans, List<String> favorite_locations)
    {
        this._useremail = email;
        this._username = name;
        this._userphone = phone;
        this._useraddress = address;
        this._userbio = bio;
        this._favorite_locations = favorite_locations;
        this._plans = plans;
    }

    public void setEmail(String inputEmail)
    {
        _useremail= inputEmail;
    }

    public void setPlans(List<String> newPlans)
    {
        this._plans = newPlans;
    }

    public void addNewPlan(String planId)
    {
        if(planId.isEmpty())
        {
            return;
        }
        _plans.add(planId);
    }

    public void addNewFavoriteLocation(String locationID)
    {
        _favorite_locations.add(locationID);
    }


    public boolean removeFavoriteLocation(String locationId)
    {
        if(locationId.isEmpty())
        {
            return false;
        }
        for(int i = 0; i<_favorite_locations.size(); i++)
        {
            if(_favorite_locations.get(i).equals(locationId))
            {
                _favorite_locations.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean removePlan(String planId)
    {
        if(planId.isEmpty())
        {
            return false;
        }
        for(int i = 0; i<_plans.size(); i++)
        {
            if(_plans.get(i).equals(planId))
            {
                _plans.remove(i);
                return true;
            }
        }
        return false;
    }

    public void setName(String newName)
    {
        this._username = newName;
    }

    public void setPhone(String newPhone)
    {
        this._userphone = newPhone;
    }

    public void setAddress(String newAddress)
    {
        this._useraddress = newAddress;
    }

    public void setBio(String newBio)
    {
        this._userbio = newBio;
    }

    public void setFavorite_locations(List<String> favorite_locations)
    {
        this._favorite_locations = favorite_locations;
    }

    public void setUserEmail(String newEmail)
    {
        this._useremail = newEmail;
    }

    public String getUserEmail()
    {
        return _useremail;
    }

    public String getName()
    {
        return this._username;
    }

    public String getPhone()
    {
        return this._userphone;
    }

    public String getAddress()
    {
        return this._useraddress;
    }

    public String getBio()
    {
        return this._userbio;
    }

    public List<String> getPlans()
    {
        return this._plans;
    }

    public List<String> getFavorite_locations()
    {
        return this._favorite_locations;
    }


    public String UserToString()
    {
        String str = new StringBuilder().append("|").append(_username).append("|").append(_userphone)
                .append("|").append(_useraddress).toString();

        return str;
    }

    public void addNewPlanId(@NotNull String newPlanId)
    {
        _plans.add(newPlanId);
    }

    public void removePlanByPlanId(@NotNull String planId)
    {
        for(int i=0; i< _plans.size(); i++)
        {
            if(_plans.get(i).equals(planId))
            {
                _plans.remove(i);
                break;
            }
        }
    }

    public void removePlanByIndex(@NotNull int index)
    {
        if(index < 0 || index > _plans.size() - 1)
        {
            return;
        }
        _plans.remove(index);
    }
}
