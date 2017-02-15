package com.main.lets.lets.LetsAPI;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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
    DatabaseReference mDatabaseRef;
    private String mLocationTitle;
    StorageReference mStorageRef;
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
    private LatLng mCoords;
    private Date mCreated;
    private String mTitle;
    private int mEventID;
    private int mOwnerID;
    private Date mStart;
    private Date mEnd;
    private int mPicCount = 0;

    public ArrayList<Entity> mMembers = new ArrayList<>();
    public ArrayList<Entity> mInvites = new ArrayList<>();
    public ArrayList<Entity> mCohosts = new ArrayList<>();
    public ArrayList<Comment> mComments = new ArrayList<>();



    public enum MemberStatus {OWNER, HOST, MEMBER, INVITE, GUEST, UNKNOWN;}
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
            else
               mInvites.add(new Entity(attendance.getJSONObject(i)));

        }

        for (int i = 0; i < cohosts.length(); i++) {
            mCohosts.add(new Entity(cohosts.getJSONObject(i)));
        }

        for (int i = 0; i < comments.length(); i++) {
            mComments.add(new Comment(comments.getJSONObject(i)));
        }

        Collections.sort(mComments);


    }

    public void loadData(JSONObject j) throws JSONException {
        mCords = new HashMap<>();

        mCoords = new LatLng(j.getDouble("Latitude"), j.getDouble("Longitude"));

        mCords.put("longitude", j.getDouble("Longitude"));
        mCords.put("latitude", j.getDouble("Latitude"));
        mLocationTitle = j.getString("Location_Title");
//        mMaxAttendance = j.getInt("Max_Attendance");
//        mMinAttendance = j.getInt("Min_Attendance");
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

//        mCreated = new Date((Long.parseLong(j.getString("Time_Created")
//                .substring(6, j.getString("Time_Created").length() - 2))));
    }

    public void getEventByID(final onEventLoaded e) {

        Calls.getEvent(mEventID, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    Event event = new Event(response.getJSONArray("Event_info"),
                            response.getJSONArray("Attending_users"),
                            response.getJSONArray("Cohosts"),
                            response.getJSONArray("Comments"));

                    e.EventLoaded(event);

                } catch (JSONException e1) {
                    L.println(Event.class, response.toString());
                    e1.printStackTrace();
                }


            }
        });

    }

    public void getEventByID(final onFullEventLoaded e) {

        Calls.getEvent(mEventID, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    Event event = new Event(response.getJSONArray("Event_info"),
                            response.getJSONArray("Attending_users"),
                            response.getJSONArray("Cohosts"),
                            response.getJSONArray("Comments"));

                    e.EventLoaded(event);

                    DatabaseReference db = FirebaseDatabase.getInstance()
                            .getReference().child("events/" + event.getmEventID());

                    db.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            String string = dataSnapshot.getValue(String.class);
                            e.HashtagLoaded(string);

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } catch (JSONException e1) {
                    e1.printStackTrace();
                }


            }
        });

    }

    public void getImages(final OnPictureUploaded l) {


        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("pictures");

        mDatabaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                EventImage e = dataSnapshot.getValue(EventImage.class);

                L.println(Event.class, e.getURL());

                StorageReference storage = FirebaseStorage.getInstance()
                        .getReferenceFromUrl("gs://lets-push-notifications-829d7.appspot.com")
                        .child(e.getURL());

                storage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        l.ImageUploaded(uri);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void uploadImage(int id, Bitmap b, String url, final OnPictureUploaded l) {

        String s = "events/event" + getmEventID() + "/" + url + ".jpg";
        mStorageRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl("gs://lets-push-notifications-829d7.appspot.com")
                .child(s);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("pictures");

        EventImage e = new EventImage(s, getmEventID(), id);

        mDatabaseRef.push().setValue(e);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mStorageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                l.ImageUploaded(downloadUrl);
            }
        });


    }



    public MemberStatus getUserStatus(int ID) {
        MemberStatus s = MemberStatus.GUEST;

        for (int i = 0; i < mMembers.size(); i++) {
            L.println(Event.class, mID + " ID");

            if (mMembers.get(i).mID == ID) {
                if (mMembers.get(i).mStatus)
                    s = MemberStatus.MEMBER;

            }

        }

        for (int i = 0; i < mInvites.size(); i++) {
            if (mInvites.get(i).mID == ID)
                s = MemberStatus.INVITE;

        }

        for (int i = 0; i < mCohosts.size(); i++) {
            if (mCohosts.get(i).mID == ID)
                s = MemberStatus.HOST;

        }

        if (ID == mOwnerID)
            s = MemberStatus.OWNER;

        return s;
    }

    public LatLng getmCoords() {
        return mCoords;
    }

    public void setmCoords(LatLng mCoords) {
        this.mCoords = mCoords;
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
        String s = new SimpleDateFormat("h:mm a").format(mStart.getTime()) + " - " + (new SimpleDateFormat("h:mm a")).format(mEnd.getTime());

        if (s.equalsIgnoreCase("12:00 AM - 12:00 AM")) {

            return "All Day";
        }

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
        if ((arg0.getCategory() == 9 || getCategory() == 9) && arg0.getCategory() != getCategory()) {

            return arg0.getCategory() == 9 ? 1 : -1;

        }


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

    @Override
    public boolean equals(Object o) {
        if (o instanceof  Event) {
            Event e = (Event) o;

            if (e.getmEventID() == getmEventID())
                return true;

        }

        return false;
    }

    public interface onEventLoaded {
        void EventLoaded(Event e);
    }

    public interface onFullEventLoaded {
        void EventLoaded(Event e);
        void HashtagLoaded(String s);
    }

    public interface OnPictureUploaded {
        void ImageUploaded(Uri url);
    }

}
