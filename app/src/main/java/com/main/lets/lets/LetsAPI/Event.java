package com.main.lets.lets.LetsAPI;

import android.content.Context;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Joe on 5/12/2016.
 */
public class Event extends Entity implements Comparable<Event> {
    private HashMap<String, Double> mCords;
    private int mMaxAttendance = 200;
    private boolean mUserAttending;
    private String mLocationTitle;
    private String mDescription;
    private int mMinAttendance;
    private int mRestrictions;
    private boolean mIsInvite;
    private boolean mIsActive;
    private boolean mIsOwner;
    private double mDistance;
    private int mAttendance;
    private Date mCreated;
    private String mTitle;
    private int mCategory;
    private int mEventID;
    private int mOwnerID;
    private Date mStart;
    private Date mEnd;

    public Event(org.json.JSONObject j) throws JSONException {
        super(j.getInt("event_id"),j.getString("event_name"), EntityType.EVENT);
        mCords = new HashMap<>();

        mCords.put("longitude", j.getDouble("Longitude"));
        mCords.put("latitude", j.getDouble("Latitude"));
        mLocationTitle = j.getString("Location_Title");
        mMaxAttendance = j.getInt("Max_Attendance");
        mMinAttendance = j.getInt("Min_Attendance");
        mDescription = j.getString("Description");
        mTitle = j.getString("Event_Name");
        mOwnerID = j.getInt("Creator_ID");
        mCategory = j.getInt("Category");
        mEventID = j.getInt("Event_ID");

        if(j.has("Distance"))
            mDistance = j.getDouble("Distance");

        mStart = new Date((Long.parseLong(j.getString("Start_Time")
                .substring(6, j.getString("Start_Time").length() - 2))));

        mEnd = new Date((Long.parseLong(j.getString("End_Time")
                .substring(6, j.getString("End_Time").length() - 2))));

        mCreated = new Date((Long.parseLong(j.getString("Time_Created")
                .substring(6, j.getString("Time_Created").length() - 2))));

    }

    public int getmEventID() {
        return this.mEventID;
    }

    public void setmEventID(int mEventID) {
        this.mEventID = mEventID;
    }

    public int getmOwnerID() {
        return this.mOwnerID;
    }

    public void setmOwnerID(int mOwnerID) {
        this.mOwnerID = mOwnerID;
    }

    public int getmAttendance() {
        return this.mAttendance;
    }

    public void setmAttendance(int mAttendance) {
        this.mAttendance = mAttendance;
    }

    public int getmMinAttendance() {
        return this.mMinAttendance;
    }

    public void setmMinAttendance(int mMinAttendance) {
        this.mMinAttendance = mMinAttendance;
    }

    public int getMax() {
        return this.mMaxAttendance;
    }

    public void setMax(int max) {
        this.mMaxAttendance = max;
    }

    public int getCategory() {
        return this.mCategory;
    }

    public void setCategory(int category) {
        this.mCategory = category;
    }

    public int getmRestrictions() {
        return this.mRestrictions;
    }

    public void setmRestrictions(int mRestrictions) {
        this.mRestrictions = mRestrictions;
    }

    public String getmTitle() {
        return this.mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmLocationTitle() {
        return this.mLocationTitle;
    }

    public void setmLocationTitle(String mLocationTitle) {
        this.mLocationTitle = mLocationTitle;
    }

    public String getmDescription() {
        return this.mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public HashMap<String, Double> getmCords() {
        return this.mCords;
    }

    public void setmCords(HashMap mCords) {
        this.mCords = mCords;
    }

    public boolean ismUserAttending() {
        return this.mUserAttending;
    }

    public void setmUserAttending(boolean mUserAttending) {
        this.mUserAttending = mUserAttending;
    }


    public String getTimeSpanString() {
        return (new SimpleDateFormat("h:mm a")).format(mStart) + " - " + (new SimpleDateFormat("h:mm a")).format(mEnd);
    }

    public boolean ismIsOwner() {
        return this.mIsOwner;
    }

    public void setmIsOwner(boolean isOwner) {
        this.mIsOwner = isOwner;
    }

    public boolean ismIsInvite() {
        return this.mIsInvite;
    }

    public void setmIsInvite(boolean isInvite) {
        this.mIsInvite = isInvite;
    }

    public boolean ismIsActive() {
        return this.mIsActive;
    }

    public void setmIsActive(boolean isActive) {
        this.mIsActive = isActive;
    }

    public int priority() {
        return !this.mIsActive ? 0 : (this.mIsInvite ? 4 : (this.mIsActive && this.mCategory > -1 ? 2 : (this.mCategory < 0 ? Math.abs(this.mCategory) : -1)));
    }

    public int compareTo(Event arg0) {
        return this.priority() == arg0.priority() ? (mStart.before(arg0.mStart) ? -1 : (mStart.after(arg0.mStart) ? 1 : 0)) : (this.priority() > arg0.priority() ? -1 : 1);
    }

    public Date getStart() {
        return mStart;
    }

    public void setStart(Date mStart) {
        this.mStart = mStart;
    }

    public Date getEnd() {
        return mEnd;
    }

    public void setEnd(Date mEnd) {
        this.mEnd = mEnd;
    }

    public int getImageResourceId(Context context) {
        return context.getResources().getIdentifier(("j" + Integer.toString(getCategory()))
                .replaceAll("\\s+", "").toLowerCase(), "drawable", context.getPackageName());
    }

    public Date getmCreated() {
        return mCreated;
    }

    public void setmCreated(Date mCreated) {
        this.mCreated = mCreated;
    }

    public double getmDistance() {
        return mDistance;
    }

    public void setmDistance(double mDistance) {
        this.mDistance = mDistance;
    }
}
