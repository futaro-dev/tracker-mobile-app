package com.example.trackerapp.data.model;

public class User {
    public String id;
    public String username;
    public String email;

    public User() {}

    public User(String id, String email, String username) {
        this.id = id;
        this.email = email;
        this.username = username;
    }
}
