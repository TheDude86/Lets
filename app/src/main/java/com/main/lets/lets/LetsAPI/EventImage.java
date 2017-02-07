package com.main.lets.lets.LetsAPI;

import com.google.firebase.database.Exclude;

/**
 * Created by novosejr on 2/7/2017.
 */

public class EventImage {
    private String URL;
    private int event;
    private String key;
    private int user;

    public EventImage() {

    }

    public EventImage(String u, int e, int us) {
        this(null, u, e, us);
    }

    public EventImage(String k, String u, int e, int us) {
        URL = u;
        event = e;
        key = k;
        user = us;
    }


    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }
}
