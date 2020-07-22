package com.example.abhinav.userlocationmaps.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Asset {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("image")
    @Expose
    private String image;

    /**
     * No args constructor for use in serialization
     *
     */
    public Asset() {
    }

    /**
     *
     * @param image
     * @param latitude
     * @param description
     * @param id
     * @param time
     * @param longitude
     */
    public Asset(String id, Double latitude, Double longitude, String description, String time, String image) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.time = time;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
