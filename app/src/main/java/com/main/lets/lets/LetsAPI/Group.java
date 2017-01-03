package com.main.lets.lets.LetsAPI;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Joe on 5/13/2016.
 */
public class Group extends Entity{

    public enum Status {UNKNOWN, GUEST, INVITE, MEMBER, ADMIN, OWNER}

    private Status mStatus = Status.UNKNOWN;

    public ArrayList<Comment> mComments = new ArrayList<>();
    public ArrayList<Entity> mMembers = new ArrayList<>();
    public ArrayList<Entity> mAdmins = new ArrayList<>();
    private org.json.JSONObject JSON;
    private boolean isHidden;
    private boolean isPublic;
    private String pic_ref;
    private int mSettings;
    private String mTitle;
    private int ownerID;
    private String mBio;
    private int groupID;

    public Group(org.json.JSONObject j) throws JSONException {
        super(j.getInt("group_id"), j.getString("group_name"), EntityType.GROUP);
        mTitle = j.getString("group_name");
        isPublic = j.getBoolean("public");
        isHidden = j.getBoolean("hidden");
        mSettings = j.getInt("settings");
        pic_ref = j.getString("pic_ref");
        groupID = j.getInt("group_id");
        ownerID = j.getInt("god");
        mBio = j.getString("bio");
        JSON = j;

    }


    public Group(int i) {
        super(i, "", EntityType.GROUP);

    }

    public void loadGroup(final AppCompatActivity a, final OnLoadListener l) {

        mComments = new ArrayList<>();
        mMembers = new ArrayList<>();

        Calls.getGroupInfo(mID, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    JSONObject j = response.getJSONArray("Group_info").getJSONObject(0);
                    mTitle = j.getString("group_name");
                    isPublic = j.getBoolean("public");
                    isHidden = j.getBoolean("hidden");
                    mSettings = j.getInt("settings");
                    pic_ref = j.getString("pic_ref");
                    groupID = j.getInt("group_id");
                    ownerID = j.getInt("god");
                    mBio = j.getString("bio");
                    JSON = j;

                    mStatus = Status.GUEST;

                    JSONArray users = response.getJSONArray("Group_users");
                    int ID = (new UserData(a).ID);

                    for (int i = 0; i < users.length(); i++) {
                        Entity e = new Entity(users.getJSONObject(i));
                        if (e.mID == ID) {
                            if (e.mStatus)
                                mStatus = Status.MEMBER;
                            else
                                mStatus = Status.INVITE;

                        }

                        if (e.mStatus)
                            mMembers.add(e);

                    }

                    JSONArray admins = response.getJSONArray("Group_admins");
                    for (int i = 0; i < admins.length(); i++) {

                        Entity e = new Entity(admins.getJSONObject(i));
                        if (e.mID == ID) {
                            mStatus = Status.ADMIN;

                        }

                        mAdmins.add(e);
                    }

                    if (ownerID == ID)
                        mStatus = Status.OWNER;

                    Calls.getGroupComments(mID, (new UserData(a)).ShallonCreamerIsATwat, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers,
                                              JSONArray response) {

                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    mComments.add(new Comment(response.getJSONObject(i)));

                                }

                                l.OnUpdate();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });


    }

    public void saveGroup(AppCompatActivity a, final User.OnLoadListener l) {

        Calls.editGroup(mID, mTitle, mBio, isPublic, isHidden, pic_ref, (new UserData(a).ShallonCreamerIsATwat), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                l.update();
            }
        });

    }

    public interface OnLoadListener {
        void OnUpdate();

    }

    @Override
    public void loadImage(Activity a, ImageView v) {
        mPic = pic_ref;
        super.loadImage(a, v);
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

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public Status getmStatus() {
        return mStatus;
    }

    public void setmStatus(Status mStatus) {
        this.mStatus = mStatus;
    }

}
