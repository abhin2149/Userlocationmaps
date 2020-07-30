package com.example.abhinav.userlocationmaps.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("last_latitude")
    @Expose
    private Double lastLatitude;
    @SerializedName("last_longitude")
    @Expose
    private Double lasLongitude;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("beat")
    @Expose
    private String beat;
    @SerializedName("reg_no")
    @Expose
    private String registrationNumber;
    @SerializedName("phone_no")
    @Expose
    private String phoneNumber;
    @SerializedName("time")
    @Expose
    private String time;

    public User(String id, Double lastLatitude, Double lasLongitude, String name, String beat, String registrationNumber, String phoneNumber, String time) {
        this.id = id;
        this.lastLatitude = lastLatitude;
        this.lasLongitude = lasLongitude;
        this.name = name;
        this.beat = beat;
        this.registrationNumber = registrationNumber;
        this.phoneNumber = phoneNumber;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getLastLatitude() {
        return lastLatitude;
    }

    public void setLastLatitude(Double lastLatitude) {
        this.lastLatitude = lastLatitude;
    }

    public Double getLasLongitude() {
        return lasLongitude;
    }

    public void setLasLongitude(Double lasLongitude) {
        this.lasLongitude = lasLongitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeat() {
        return beat;
    }

    public void setBeat(String beat) {
        this.beat = beat;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
