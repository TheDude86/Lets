package com.main.lets.lets.LetsAPI;

import android.util.Log;

import com.main.lets.lets.Visualizers.Client;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joe on 5/30/2016.
 */
public class Entity  extends Client{
    enum EntityType {EVENT, USER, GROUP, COMMENT}

    public EntityType mType;
    public String mText;
    public int mID;

    public Entity(int i, String s, EntityType e){
        mType = e;
        mText = s;
        mID = i;

    }

    public Entity(JSONObject j) {
        try {
            if (j.has("Sender")) {
                mType = EntityType.USER;
                mID = j.getInt("Sender");
                mText = j.getString("User_Name");

            } else if (j.has("user_id")) {
                mType = EntityType.USER;
                mID = j.getInt("user_id");
                mText = j.getString("name");

            } else if (j.has("event_id")) {
                mType = EntityType.EVENT;
                mID = j.getInt("event_id");
                mText = j.getString("event_name");

            } else if(j.has("Message")){
                mID = -1;
                mType = EntityType.COMMENT;
                mText = j.getString("name") + ": \n" + j.getString("Message");

            } else if(j.has("group_name")){
                mType = EntityType.GROUP;
                mID = j.getInt("group_id");
                mText = j.getString("group_name");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void draw(JSONObject j) {
        Log.println(Log.ASSERT, "Asshat", "Don't use this method");
        int x = 3/0;

    }

}
