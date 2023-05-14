package com.example.fit_20clc_hcmus_android_final_project.data_struct;

import android.util.Log;

import com.example.fit_20clc_hcmus_android_final_project.CustomInterface.DataAccessBufferItem;
import com.example.fit_20clc_hcmus_android_final_project.DatabaseAccess;
import com.google.firebase.database.IgnoreExtraProperties;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@IgnoreExtraProperties
public class User implements Serializable {
    private String _username;
    private String _useremail;
    private String _userphone;
    private String _useraddress;

    private List<String> _plans;

    private List<String> _favorite_locations;
    private String _userbio;

    private String _avatar_url;

    public User()
    {
        _username = "00";
        _useremail = "None";
        _userphone = "000";
        _plans = new ArrayList<>();
        _favorite_locations = new ArrayList<>();
        _useraddress = "0000";
        _userbio = "00000";
        _avatar_url= DatabaseAccess.default_avatar_url[0];
    }

    public User(String name, String email, String phone, String address, String bio, List<String> plans, List<String> favorite_locations,String avatar_url)
    {
        this._useremail = email;
        this._username = name;
        this._userphone = phone;
        this._useraddress = address;
        this._userbio = bio;
        this._favorite_locations = favorite_locations;
        this._plans = plans;
        this._avatar_url=avatar_url;
    }

    public String getAvatarUrl() {
        return _avatar_url;
    }

    public void setAvatarUrl(String _avatar_url) {
        this._avatar_url = _avatar_url;
    }

//    public void setUserEmail(String inputEmail)
//    {
//        _useremail= inputEmail;
//    }

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


    public static byte[] fromObjectToBytes(User user) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(user);
            baos.close();
            oos.close();
            return baos.toByteArray();
        } catch (IOException e) {
            Log.e("<<Serializable error>>", e.getMessage());
            return null;
        }
    }

    public static User fromBytesToObject(byte[] bytes)
    {
        try
        {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            User user = (User) ois.readObject();
            bais.close();
            ois.close();
            return user;
        }
        catch (IOException ioe)
        {
            return null;
        }
        catch (ClassNotFoundException cnfe)
        {
            return null;
        }
    }
}
