package com.main.lets.lets.LetsAPI;

import com.google.firebase.database.Exclude;

/**
 * Created by novosejr on 2/13/2017.
 */

public class Hashtag {
    //Because firebase sucks

    private String name;
    private String key;

    public Hashtag() {

    }

    public Hashtag(String n) {
        name = n;
    }

    public Hashtag(String n, String k) {
        name = n;
        key = k;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
