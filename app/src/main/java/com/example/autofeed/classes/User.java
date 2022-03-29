package com.example.autofeed.classes;

public class User {
    private String email, password, name;// set class variables

    public User(String email, String password, String name) { //create constructor
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public User() { // create default constructor
        this.setEmail("example@gmail.com");
        this.setPassword("**************");
        this.setName("Name");
    }
    // create getters and setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
