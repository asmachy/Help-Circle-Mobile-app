package com.example.protik.helpcircle;

public class UserLocationModel {
    private double latitude;
    private double longitude;
    private String userName;

    public UserLocationModel() {
    }

    public UserLocationModel(double latitude, double longitude, String userName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.userName = userName;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setUserName(String userName){
        this.userName = userName;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getUserName(){
        return userName;
    }
}
