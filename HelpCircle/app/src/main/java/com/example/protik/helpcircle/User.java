package com.example.protik.helpcircle;

public class User {

    public String name, email, phone, user_id;

    public User() {

    }

    public User(String name, String email, String phone, String user_id) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

}
