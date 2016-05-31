package com.main.lets.lets.LetsAPI;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Joe on 5/30/2016.
 */
public class Entity {
    enum EntityType {EVENT, USER, GROUP, COMMENT}

    public EntityType mType;
    public String mText;
    int mID;

    public Entity(JSONObject j) {
        try {
            if (j.has("Sender")) {
                mText = j.getString("User_Name");
                mID = j.getInt("Sender");
                mType = EntityType.USER;

            } else if (j.has("user_id")) {
                mText = j.getString("name");
                mID = j.getInt("user_id");
                mType = EntityType.USER;

            } else if (j.has("event_id")) {
                mText = j.getString("event_name");
                mID = j.getInt("event_id");
                mType = EntityType.EVENT;

            } else if(j.has("Message")){
                mID = -1;
                mText = j.getString("name") + ": \n" + j.getString("Message");
                mType = EntityType.COMMENT;

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
