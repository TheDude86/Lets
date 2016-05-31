package com.main.lets.lets.LetsAPI;

import org.json.JSONException;

/**
 * Created by Joe on 5/13/2016.
 */
public class Group {
    private org.json.JSONObject JSON;
    private int groupID;
    private int ownerID;
    private String mTitle;
    private String mBio;
    private String pic_ref;
    private boolean isPublic;
    private int mSettings;

    public Group(org.json.JSONObject j) throws JSONException {
        JSON = j;
        groupID = j.getInt("group_id");
        ownerID = j.getInt("god");
        mTitle = j.getString("group_name");
        mBio = j.getString("bio");
        pic_ref = j.getString("pic_ref");
        isPublic = j.getBoolean("public");
        mSettings = j.getInt("settings");

    }

    public org.json.JSONObject getJSON() {
        return JSON;
    }

    public void setJSON(org.json.JSONObject JSON) {
        this.JSON = JSON;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmBio() {
        return mBio;
    }

    public void setmBio(String mBio) {
        this.mBio = mBio;
    }

    public String getPic_ref() {
        return pic_ref;
    }

    public void setPic_ref(String pic_ref) {
        this.pic_ref = pic_ref;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public int getmSettings() {
        return mSettings;
    }

    public void setmSettings(int mSettings) {
        this.mSettings = mSettings;
    }
}
