package com.main.lets.lets.LetsAPI;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Joe on 5/12/2016.
 */
public class User extends Entity {
    public enum Relationship {NONE, SENT, RECIEVED, FRIEND, OWNER}

    public HashMap<String, Boolean> mInterests = new HashMap<>();
    public ArrayList<EventEntity> mEvents = new ArrayList<>();
    public ArrayList<Entity> mFriends = new ArrayList<>();
    public ArrayList<Entity> mGroups = new ArrayList<>();
    public Relationship mRelationship = Relationship.NONE;
    private LinkedList<Group> mGroupList;
    private Timestamp birthday;
    private boolean gender;
    private boolean active;
    private int ownerList;
    private String propic;
    private int interests;
    public int genderInt;
    private String name;
    private int friends;
    private String bio;
    private int userID;
    private int events;
    private int score;

    public User(int id, String name, String bio, int events, int score, Timestamp birthday, int friends, boolean active, int ownerList) {
        super(id, name, EntityType.USER);

        mGroupList = new LinkedList<>();
        this.setOwnerList(ownerList);
        this.setBirthday(birthday);
        this.setFriends(friends);
        this.setEvents(events);
        this.setActive(active);
        this.setScore(score);
        this.setUserID(id);
        this.setName(name);
        this.setBio(bio);

    }

    public User(JSONObject j) throws JSONException {
        super(j.getInt("User_ID"), j.getString("User_Name"), EntityType.USER);

        this.setBirthday(new Timestamp(Long.parseLong(j.getString("Birthday")
                .substring(6, j.getString("Birthday").length() - 2))));
        this.setGender(j.getInt("Gender") == 1 ? true : false);
        this.setPropic(j.getString("Profile_Picture"));
        this.setInterests(j.getInt("Interests"));
        this.setName(j.getString("User_Name"));
        this.setBio(j.getString("Biography"));
        this.genderInt = j.getInt("Gender");
        this.setUserID(j.getInt("User_ID"));
        this.setScore(j.getInt("Score"));
        mGroupList = new LinkedList<>();


    }

    public User(int i) {
        super(i, "NULL", EntityType.USER);

    }

    public void loadFull(final AppCompatActivity a, final OnLoadListener load) {
        load(a, new OnLoadListener() {
            @Override
            public void update() {

                final int ID = new UserData(a).ID;

                if (ID == userID) {
                    mRelationship = Relationship.OWNER;
                }

                Calls.getFriends(mID, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          JSONArray response) {

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                if (response.getJSONObject(i).getBoolean("status"))
                                    mFriends.add(new Entity(response.getJSONObject(i)));

                                if (response.getJSONObject(i)
                                        .getInt("user_id") == ID) {
                                    if (response.getJSONObject(i)
                                            .getInt("sender") == ID)
                                        mRelationship = Relationship.SENT;
                                    else
                                        mRelationship = Relationship.RECIEVED;

                                    if (response.getJSONObject(i)
                                            .getBoolean("status")) {
                                        mRelationship = Relationship.FRIEND;
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        UserData d = new UserData(a);

                        Calls.getGroups(mID, d.ShallonCreamerIsATwat, new JsonHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        mGroups.add(new Entity(response.getJSONObject(i)));

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                load.update();

                            }
                        });

                    }
                });

            }
        });

    }

    public void saveUser(final AppCompatActivity a, final OnLoadListener load) {
        String token = new UserData(a).ShallonCreamerIsATwat;
        HashMap<String, Object> info = new HashMap<>();

        String s = "";

        Set<String> interestList = mInterests.keySet();

        for (String str : interestList) {
            if (mInterests.get(str))
                s = s + str + ",";

        }

        if (s.endsWith(","))
            s = s.substring(0, s.length() - 1);

        if (s.equals("")) {
            {
                final CharSequence[] items = {" Party ", " Eating & Drinking ", " Studying ",
                        " TV & Movies ", " Video Games ", " Sports ", " Music ", " Relax ",
                        " Other "};
                // arraylist to keep the selected items

                final boolean[] interests = new boolean[mInterests.size()];

                final String[] categories = {"Party",
                        "Eat/Drink",
                        "Study",
                        "TV/Movies",
                        "Video Games",
                        "Sports",
                        "Music",
                        "Relax",
                        "Other"};

                for (int i = 0; i < interests.length; i++) {
                    interests[i] = mInterests.get(categories[i]);

                }

                AlertDialog mainDialog = new AlertDialog.Builder(a)
                        .setTitle("You must select at least one interest")
                        .setCancelable(true)
                        .setMultiChoiceItems(items, interests,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                                        interests[indexSelected] = isChecked;

                                    }
                                })
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                                for (int i = 0; i < categories.length; i++) {
                                    mInterests.put(categories[i], interests[i]);

                                }

                                saveUser(a, new User.OnLoadListener() {
                                    @Override
                                    public void update() {
                                        load.update();
                                    }
                                });


                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        }).create();
                mainDialog.show();


            }

        } else {
            info.put("interests", s);
            info.put("birthday", new Date(birthday.getTime()));
            info.put("id", mID);
            info.put("privacy", 0);
            info.put("picRef", propic);
            info.put("gender", genderInt);
            info.put("name", name);
            info.put("bio", bio);

            Calls.editProfile(info, token, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    load.update();
                }
            });

        }
    }

    public void load(Context a, final OnLoadListener load) {

        final UserData u = new UserData(a);

        Calls.getProfileByID(mID, u.ShallonCreamerIsATwat, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    JSONObject j = response.getJSONArray("info").getJSONObject(0);

                    setBirthday(new Timestamp(Long.parseLong(j.getString("Birthday")
                            .substring(6, j.getString("Birthday").length() - 2))));
                    setGender(j.getInt("Gender") == 1);
                    setPropic(j.getString("Profile_Picture"));
                    setInterests(j.getInt("Interests"));
                    setName(j.getString("User_Name"));
                    setBio(j.getString("Biography"));
                    genderInt = j.getInt("Gender");
                    setUserID(j.getInt("User_ID"));
                    setScore(j.getInt("Score"));

                    JSONObject interests = response.getJSONObject("interests");
                    Iterator<String> i = interests.keys();

                    while (i.hasNext()) {
                        String s = i.next();
                        mInterests.put(s, interests.getBoolean(s));

                    }

                    Calls.getAttended(mID, u.ShallonCreamerIsATwat, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    mEvents.add(new EventEntity(response.getJSONObject(i)));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }

                            load.update();

                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public String getInterestsString() {
        String s = "";

        Set<String> interests = mInterests.keySet();

        for (String str: interests) {
            if (mInterests.get(str))
                s = s + str + ", ";

        }

        if (s.endsWith(", "))
            s = s.substring(0, s.length() - 2);

        return s;
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

    public LinkedList<Group> getmGroupList() {
        return mGroupList;
    }

    public void setmGroupList(LinkedList<Group> mGroupList) {
        this.mGroupList = mGroupList;
    }

    public String getPropic() {
        return propic;
    }

    public void setPropic(String propic) {
        this.propic = propic;
    }

    @Override
    public void loadImage(Activity a, ImageView v) {
        mPic = propic;

        super.loadImage(a, v);

    }

    public interface OnLoadListener {
        void update();
    }

}

