package com.main.lets.lets.LetsAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.LinkedList;

/**
 * Created by Joe on 5/12/2016.
 */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private int userID;
    private String name;
    private String bio;
    private int interests;
    private Timestamp birthday;
    private int events;
    private int score;
    private int friends;
    private int ownerList;
    private boolean active;
    private boolean gender;
    private String propic;
    private LinkedList<Group> mGroups;

    public User(int id, String name, String bio, int events, int score, Timestamp birthday, int friends, boolean active, int ownerList) {
        this.setUserID(id);
        this.setName(name);
        this.setBio(bio);
        this.setEvents(events);
        this.setScore(score);
        this.setBirthday(birthday);
        this.setFriends(friends);
        this.setActive(active);
        this.setOwnerList(ownerList);
        mGroups = new LinkedList<>();

    }

    public User(JSONObject j) throws JSONException {
        this.setBirthday(new Timestamp(Long.parseLong(j.getString("Birthday")
                .substring(6, j.getString("Birthday").length() - 2))));
        this.setGender(j.getInt("Gender") == 1 ? true : false);
        this.setPropic(j.getString("Profile_Picture"));
        this.setInterests(j.getInt("Interests"));
        this.setName(j.getString("User_Name"));
        this.setBio(j.getString("Biography"));
        this.setUserID(j.getInt("User_ID"));
        this.setScore(j.getInt("Score"));
        mGroups = new LinkedList<>();

    }

    public int getUserID() {
        return this.userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBio() {
        return this.bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public int getEvents() {
        return this.events;
    }

    public void setEvents(int events) {
        this.events = events;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getFriends() {
        return this.friends;
    }

    public void setFriends(int friends) {
        this.friends = friends;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String toString() {
        String s = "";
        s = s + "ID: " + this.userID + " Name: " + this.name + " Events: " + this.events + " Score: " + this.score + " Friends: " + this.friends + " Active: " + this.active + " Bio: " + this.bio;
        return s;
    }

    public boolean isGender() {
        return this.gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public int getInterests() {
        return this.interests;
    }

    public void setInterests(int interests) {
        this.interests = interests;
    }

    public int getOwnerList() {
        return this.ownerList;
    }

    public void setOwnerList(int ownerList) {
        this.ownerList = ownerList;
    }

    public Timestamp getBirthday() {
        return this.birthday;
    }

    public void setBirthday(Timestamp birthday) {
        this.birthday = birthday;
    }

    public LinkedList<Group> getmGroups() {
        return mGroups;
    }

    public void setmGroups(LinkedList<Group> mGroups) {
        this.mGroups = mGroups;
    }

    public String getPropic() {
        return propic;
    }

    public void setPropic(String propic) {
        this.propic = propic;
    }
}

