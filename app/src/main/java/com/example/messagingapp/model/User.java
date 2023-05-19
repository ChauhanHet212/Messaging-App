package com.example.messagingapp.model;

public class User {
    String profilePic;
    String name;
    String number;
    String about;

    public User() {
    }

    public User(String profilePic, String name, String number) {
        this.profilePic = profilePic;
        this.name = name;
        this.number = number;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}
