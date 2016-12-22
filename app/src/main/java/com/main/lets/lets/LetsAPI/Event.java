package com.main.lets.lets.LetsAPI;

import android.content.Context;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Adapters.EventDetailAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

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
    private String mOwnerName;
    private String mEventInfo;
    private JSONObject mJSON;
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

    public ArrayList<Entity> mMembers = new ArrayList<>();
    public ArrayList<Entity> mCohosts = new ArrayList<>();
    public ArrayList<Comment> mComments = new ArrayList<>();

    public enum MemberStatus {OWNER, HOST, MEMBER, INVITE, GUEST, UNKNOWN}


    public Event(org.json.JSONObject j) throws JSONException {
        super(j.getInt("Event_ID"), j.getString("Event_Name"), EntityType.EVENT);
        mEventInfo = j.toString();
        loadData(j);

    }

    public Event(int eventID) {
        super(eventID, "Blank", EntityType.EVENT);
        mEventID = eventID;
    }

    public Event(JSONArray eventInfo, JSONArray attendance, JSONArray cohosts, JSONArray comments) throws JSONException {
        super(eventInfo.getJSONObject(0).getInt("Event_ID"),
                eventInfo.getJSONObject(0).getString("Event_Name"),
                EntityType.EVENT);

        mEventInfo = eventInfo.getJSONObject(0).toString();

        loadData(eventInfo.getJSONObject(0));

        for (int i = 0; i < attendance.length(); i++) {
            Entity e = new Entity(attendance.getJSONObject(i));

            if (e.mStatus)
               mMembers.add(new Entity(attendance.getJSONObject(i)));

        }

        for (int i = 0; i < cohosts.length(); i++) {
            mCohosts.add(new Entity(cohosts.getJSONObject(i)));
        }

        for (int i = 0; i < comments.length(); i++) {
            mComments.add(new Comment(comments.getJSONObject(i)));
        }


    }

    public void loadData(JSONObject j) throws JSONException {
        mCords = new HashMap<>();

        mCords.put("longitude", j.getDouble("Longitude"));
        mCords.put("latitude", j.getDouble("Latitude"));
        mLocationTitle = j.getString("Location_Title");
        mMaxAttendance = j.getInt("Max_Attendance");
        mMinAttendance = j.getInt("Min_Attendance");
        mDescription = j.getString("Description");
        mOwnerName = j.getString("Creator_Name");
        mTitle = j.getString("Event_Name");
        mOwnerID = j.getInt("Creator_ID");
        mCategory = j.getInt("Category");
        mEventID = j.getInt("Event_ID");
        mJSON = j;

        if (j.has("Distance"))
            mDistance = j.getDouble("Distance");

        mStart = new Date((Long.parseLong(j.getString("Start_Time")
                .substring(6, j.getString("Start_Time").length() - 2))));

        mEnd = new Date((Long.parseLong(j.getString("End_Time")
                .substring(6, j.getString("End_Time").length() - 2))));

        mCreated = new Date((Long.parseLong(j.getString("Time_Created")
                .substring(6, j.getString("Time_Created").length() - 2))));
    }



    public void getEventByID(final onEventLoaded e) {

        Calls.getEvent(mEventID, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    e.EventLoaded(new Event(response.getJSONArray("Event_info"),
                            response.getJSONArray("Attending_users"),
                            response.getJSONArray("Cohosts"),
                            response.getJSONArray("Comments")));

                } catch (JSONException e1) {
                    e1.printStackTrace();
                }


            }
        });

    }

    public MemberStatus getUserStatus(int ID) {
        MemberStatus s = MemberStatus.GUEST;

        for (int i = 0; i < mMembers.size(); i++) {
            if (mMembers.get(i).mID == ID) {
                if (mMembers.get(i).mStatus)
                    s = MemberStatus.MEMBER;
                else
                    s = MemberStatus.INVITE;

            }

        }

        for (int i = 0; i < mCohosts.size(); i++) {
            if (mCohosts.get(i).mID == ID)
                s = MemberStatus.HOST;

        }

        if (ID == mOwnerID)
            s = MemberStatus.OWNER;

        return s;
    }

    public ArrayList<Entity> getmCohosts() {return mCohosts;}

    public String getmEventInfo() {
        return mEventInfo;
    }

    public void setmEventInfo(String mEventInfo) {
        this.mEventInfo = mEventInfo;
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

        return (new SimpleDateFormat("h:mm a").format(mStart.getTime()) + " - " + (new SimpleDateFormat("h:mm a")).format(mEnd.getTime()));
    }

    public String getMonth() {

        return (new SimpleDateFormat("MMM").format(mStart.getTime()));
    }

    public String getDay() {

        return (new SimpleDateFormat("dd").format(mStart.getTime()));
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

    public JSONObject getmJSON() {
        return mJSON;
    }

    public void setmJSON(JSONObject mJSON) {
        this.mJSON = mJSON;
    }

    public String getmOwnerName() {
        return mOwnerName;
    }

    public void setmOwnerName(String mOwnerName) {
        this.mOwnerName = mOwnerName;
    }

    public interface onEventLoaded {
        public void EventLoaded(Event e);
    }

}
