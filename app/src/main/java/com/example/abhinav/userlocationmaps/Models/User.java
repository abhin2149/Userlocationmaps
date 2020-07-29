package com.example.abhinav.userlocationmaps.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("last_latitude")
    @Expose
    private Double last_latitude;
    @SerializedName("last_longitude")
    @Expose
    private Double last_longitude;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("beat")
    @Expose
    private String beat;
    @SerializedName("reg_no")
    @Expose
    private Integer registration_no;
    @SerializedName("phone_no")
    @Expose
    private Integer phone_no;
    @SerializedName("time")
    @Expose
    private String time;

    public User(String id, Double last_latitude, Double last_longitude, String name, String beat, Integer registration_no, Integer phone_no, String time) {
        this.id = id;
        this.last_latitude = last_latitude;
        this.last_longitude = last_longitude;
        this.name = name;
        this.beat = beat;
        this.registration_no = registration_no;
        this.phone_no = phone_no;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getLast_latitude() {
        return last_latitude;
    }

    public void setLast_latitude(Double last_latitude) {
        this.last_latitude = last_latitude;
    }

    public Double getLast_longitude() {
        return last_longitude;
    }

    public void setLast_longitude(Double last_longitude) {
        this.last_longitude = last_longitude;
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

    public Integer getRegistration_no() {
        return registration_no;
    }

    public void setRegistration_no(Integer registration_no) {
        this.registration_no = registration_no;
    }

    public Integer getPhone_no() {
        return phone_no;
    }

    public void setPhone_no(Integer phone_no) {
        this.phone_no = phone_no;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
