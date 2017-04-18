package com.main.lets.lets.LetsAPI;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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

    private DatabaseReference mDatabaseRef;
    private String locationTitle;
    private String description;
    private int maxAttendance;
    private int minAttendance;
    private boolean isInvite;
    private long timeCreated;
    private double longitude;
    private double latitude;
    private long timeStart;
    private int category;
    private String title;
    private String owner;
    private long timeEnd;
    private String info;
    private String key;
    private int ownID;
    private int ID;

    private boolean userAttending;
    private int restrictions;
    private double distance;
    private int attendance;


    private ArrayList<Entity> members = new ArrayList<>();
    private ArrayList<Entity> invites = new ArrayList<>();
    private ArrayList<Entity> cohosts = new ArrayList<>();
    private ArrayList<Comment> comments = new ArrayList<>();




    public enum MemberStatus {OWNER, HOST, MEMBER, INVITE, GUEST, UNKNOWN;}
    public Event(org.json.JSONObject j) throws JSONException {
        super(j.getInt("Event_ID"), j.getString("Event_Name"), EntityType.EVENT);
        info = j.toString();
        loadData(j);

    }

    public Event() {
        super();

    }

    public Event(int eventID) {
        super(eventID, "Blank", EntityType.EVENT);
        ID = eventID;
    }

    public Event(JSONArray eventInfo, JSONArray attendance, JSONArray cohosts, JSONArray comments) throws JSONException {
        super(eventInfo.getJSONObject(0).getInt("Event_ID"),
                eventInfo.getJSONObject(0).getString("Event_Name"),
                EntityType.EVENT);

        info = eventInfo.getJSONObject(0).toString();

        loadData(eventInfo.getJSONObject(0));

        for (int i = 0; i < attendance.length(); i++) {
            Entity e = new Entity(attendance.getJSONObject(i));

            if (e.mStatus)
               members.add(new Entity(attendance.getJSONObject(i)));
            else
               invites.add(new Entity(attendance.getJSONObject(i)));

        }

        for (int i = 0; i < cohosts.length(); i++) {
            this.cohosts.add(new Entity(cohosts.getJSONObject(i)));
        }

        for (int i = 0; i < comments.length(); i++) {
            this.comments.add(new Comment(comments.getJSONObject(i)));
        }

        Collections.sort(this.comments);


    }

    public void loadData(JSONObject j) throws JSONException {


        longitude = j.getDouble("Longitude");
        latitude = j.getDouble("Latitude");
        locationTitle = j.getString("Location_Title");
//        maxAttendance = j.getInt("Max_Attendance");
//        minAttendance = j.getInt("Min_Attendance");
        description = j.getString("Description");
        owner = j.getString("Creator_Name");
        title = j.getString("Event_Name");
        ownID = j.getInt("Creator_ID");
        mCategory = j.getInt("Category");
        ID = j.getInt("Event_ID");

        if (j.has("Distance"))
            distance = j.getDouble("Distance");

        timeCreated = j.getLong("Time_Created");
    }

    public JSONObject toJSON() {
        JSONObject data = new JSONObject();

        try {
            data.put("Longitude", longitude);
            data.put("Latitude", latitude);
            data.put("Location_Title", locationTitle);
            data.put("Description", description);
            data.put("Creator_Name", owner);
            data.put("Event_Name", title);
            data.put("Creator_ID", ownID);
            data.put("Category", category);
            data.put("Event_ID", ID);
            data.put("Time_Created", timeCreated);




        } catch (JSONException e) {
            e.printStackTrace();
        }


        return data;
    }

    public void getEventByID(final onEventLoaded e) {

        Query ref = FirebaseDatabase.getInstance().getReference().child("events")
                .orderByChild("ID").equalTo(ID);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Event event = dataSnapshot.getValue(Event.class);
                e.EventLoaded(event);

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

    public void getEventByID(final onFullEventLoaded e) {

        Calls.getEvent(ID, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    Event event = new Event(response.getJSONArray("Event_info"),
                            response.getJSONArray("Attending_users"),
                            response.getJSONArray("Cohosts"),
                            response.getJSONArray("Comments"));

                    e.EventLoaded(event);

                    DatabaseReference db = FirebaseDatabase.getInstance()
                            .getReference().child("events/" + event.getID() + "/hashtags");

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

        String s = "events/event" + getID() + "/" + url + ".jpg";

        StorageReference mStorageRef = FirebaseStorage.getInstance()
                .getReferenceFromUrl("gs://lets-push-notifications-829d7.appspot.com")
                .child(s);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("pictures");

        EventImage e = new EventImage(s, getID(), id);

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

        for (int i = 0; i < members.size(); i++) {

            if (members.get(i).mID == ID) {
                if (members.get(i).mStatus)
                    s = MemberStatus.MEMBER;

            }

        }

        for (int i = 0; i < invites.size(); i++) {
            if (invites.get(i).mID == ID)
                s = MemberStatus.INVITE;

        }

        for (int i = 0; i < cohosts.size(); i++) {
            if (cohosts.get(i).mID == ID)
                s = MemberStatus.HOST;

        }

        if (ID == ownID)
            s = MemberStatus.OWNER;

        return s;
    }

    public ArrayList<Comment> getComments () {
        return comments;
    }

    public void setComments(ArrayList<Comment> m) {
        comments = m;
    }

    public ArrayList<Entity> getMembers() {
        return  members;
    }

    public void setMembers(ArrayList<Entity> m) {
        members = m;
    }

    public ArrayList<Entity> getInvites() {
        return  members;
    }

    public void setInvites(ArrayList<Entity> m) {
        invites = m;
    }

    public ArrayList<Entity> getCohosts() {
        return cohosts;
    }

    public void setCohosts(ArrayList<Entity> m) {
        cohosts = m;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getOwnID() {
        return this.ownID;
    }

    public void setOwnID(int ownerID) {
        this.ownID = ownerID;
    }

    public int getAttendance() {
        return this.attendance;
    }

    public void setAttendance(int attendance) {
        this.attendance = attendance;
    }

    public int getMinAttendance() {
        return this.minAttendance;
    }

    public void setMinAttendance(int minAttendance) {
        this.minAttendance = minAttendance;
    }

    public int getMax() {
        return this.maxAttendance;
    }

    public void setMax(int max) {
        this.maxAttendance = max;
    }

    public int getCategory() {
        return this.mCategory;
    }

    public void setCategory(int category) {
        this.mCategory = category;
    }

    public int getRestrictions() {
        return this.restrictions;
    }

    public void setRestrictions(int restrictions) {
        this.restrictions = restrictions;
    }

    public String getTitle() {
        return this.title.equals("{null}") ? String.format("%s's Event", getOwner()) : this.title;
    }

    public boolean hasTitle() {
        return !this.title.equals("{null}");
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocationTitle() {
        return this.locationTitle.equals("{null}") ? "No location set" : this.locationTitle;
    }

    public boolean hasLocation() {
        return !this.locationTitle.equals("{null}");
    }

    public void setLocationTitle(String locationTitle) {
        this.locationTitle = locationTitle;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap<String, Double> getmCords() {
        HashMap<String, Double> mCords = new HashMap<>();
        mCords.put("latitude", latitude);
        mCords.put("longitude", longitude);

        return mCords;
    }

    public void setmCords(HashMap<String, Double> mCords) {
        this.latitude = mCords.get("latitude");
        this.longitude = mCords.get("longitude");
    }

    public boolean isUserAttending() {
        return this.userAttending;
    }

    public void setUserAttending(boolean userAttending) {
        this.userAttending = userAttending;
    }


    public String getTimeSpanString() {
        Date start = new Date(timeStart);
        Date end = new Date(timeEnd);

        String s = new SimpleDateFormat("h:mm a").format(start.getTime()) + " - " + (new SimpleDateFormat("h:mm a")).format(end.getTime());

        if (s.equalsIgnoreCase("12:00 AM - 12:00 AM")) {

            return "All Day";
        }

        return s;
    }

    public String getMonth() {
        Date start = new Date(timeStart);

        return (new SimpleDateFormat("MMM").format(start.getTime()));
    }

    public String getDay() {
        Date end = new Date(timeEnd);

        return (new SimpleDateFormat("dd").format(end.getTime()));
    }

    public boolean isInvite() {
        return this.isInvite;
    }

    public void setInvite(boolean isInvite) {
        this.isInvite = isInvite;
    }


    public int priority() {
        return (this.isInvite ? 4 : (this.mCategory > -1 ? 2 : (this.mCategory < 0 ? Math.abs(this.mCategory) : -1)));
    }

    public int compareTo(Event arg0) {
        if ((arg0.getCategory() == 9 || getCategory() == 9) && arg0.getCategory() != getCategory()) {

            return arg0.getCategory() == 9 ? 1 : -1;

        }

        Date start = new Date(timeStart);
        Date end = new Date(timeEnd);

        return this.priority() == arg0.priority() ?
                (start.before(new Date(arg0.timeStart)) ?
                        -1 : (start.after(new Date(arg0.timeStart)) ? 1 : 0)) : (this.priority() > arg0.priority() ? -1 : 1);
    }

    public Date getTimeStart() {
        return new Date(timeStart);
    }

    public void setTimeStart(long mStart) {
        this.timeStart = mStart;
    }

    public Date getTimeEnd() {
        return new Date(timeEnd);
    }

    public void setTimeEnd(long mEnd) {
        this.timeEnd = mEnd;
    }

    public int getImageResourceId(Context context) {
        return context.getResources().getIdentifier(("j" + Integer.toString(getCategory()))
                .replaceAll("\\s+", "").toLowerCase(), "drawable", context.getPackageName());
    }

    public Date getTimeCreated() {
        return new Date(timeStart);
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }



    @Override
    public boolean equals(Object o) {
        if (o instanceof  Event) {
            Event e = (Event) o;

            if (e.getID() == getID())
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
