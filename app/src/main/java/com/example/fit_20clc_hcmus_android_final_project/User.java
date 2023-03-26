package com.example.fit_20clc_hcmus_android_final_project;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class User {
    private String _uid;
    private String _username;
    private String _userphone;
    private String _useraddress;

    public User()
    {
        _uid = "0";
        _username = "00";
        _userphone = "000";
        _useraddress = "0000";
    }

    public User(String id, String name, String phone, String address)
    {
        this._uid = id;
        this._username = name;
        this._userphone = phone;
        this._useraddress = address;
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

    public String UserToString()
    {
        String str = new StringBuilder().append(_uid).append("|").append(_username).append("|").append(_userphone)
                .append("|").append(_useraddress).toString();

        return str;
    }

}
