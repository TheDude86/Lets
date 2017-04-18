package com.main.lets.lets.LetsAPI;

import com.google.firebase.database.Exclude;

/**
 * Created by novosejr on 3/9/2017.
 */

public class Chat {

    private long timestamp;
    private String message;
    private String author;
    private String key;
    private String pic;
    private int id;

    public Chat() {

    }

    public Chat(String m, String a, String p, long t, int id) {
        timestamp = t;
        this.id = id;
        message = m;
        author = a;
        pic = p;
    }

    public Chat(String m, String a, String p, long t, int id, String key) {
        this.key = key;
        timestamp = t;
        this.id = id;
        message = m;
        author = a;
        pic = p;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String name) {
        this.message = name;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }



    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
