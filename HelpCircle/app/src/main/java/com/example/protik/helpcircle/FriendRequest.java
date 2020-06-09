package com.example.protik.helpcircle;

public class FriendRequest {

    public String date, user_id;

    public FriendRequest() {

    }

    public FriendRequest(String date, String user_id) {
        this.date = date;
        this.user_id = user_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
