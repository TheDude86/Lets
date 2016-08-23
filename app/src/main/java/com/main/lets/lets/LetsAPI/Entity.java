package com.main.lets.lets.LetsAPI;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Activities.EventDetailActivity;
import com.main.lets.lets.Activities.GroupDetailActivity;
import com.main.lets.lets.Activities.UserDetailActivity;
import com.main.lets.lets.Visualizers.Client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Joe on 5/30/2016.
 */
public class Entity extends Client {
    enum EntityType {EVENT, USER, GROUP, COMMENT}

    public EntityType mType;
    public int mCategory;
    public String mText;
    public String mPic;
    public int mID;

    /**
     * @param i
     * @param s
     * @param e
     */
    public Entity(int i, String s, EntityType e) {
        mPic = "";
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

            } else if (j.has("user_id") && !j.has("text")) {
                mType = EntityType.USER;
                mID = j.getInt("user_id");
                mText = j.getString("name");
                if (j.has("pic_ref"))
                    mPic = j.getString("pic_ref");

            } else if (j.has("event_id") && !j.has("text")) {
                mType = EntityType.EVENT;
                mID = j.getInt("event_id");
                mText = j.getString("event_name");
                if (j.has("category"))
                    mCategory = j.getInt("category");

            } else if (j.has("text")) {
                mType = EntityType.COMMENT;
                mID = j.getInt("user_id");
                mText = j.getString("text");

            } else if (j.has("group_name")) {
                mType = EntityType.GROUP;
                mID = j.getInt("group_id");
                mText = j.getString("group_name");
                if (j.has("pic_ref"))
                    mPic = j.getString("pic_ref");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void draw(JSONObject j) {
        int x = 3 / 0;

    }

    public void loadDetailActivity(final AppCompatActivity mActivity, final String token,
                                   final int id) {
        final JsonHttpResponseHandler jsonHttpResponseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Intent intent;
                    switch (mType) {
                        case USER:
                            intent = new Intent(mActivity, UserDetailActivity.class);

                            intent.putExtra("UserID", id);
                            intent.putExtra("token", token);
                            mActivity.startActivity(intent);

                            break;
                        case EVENT:
                            intent = new Intent(mActivity, EventDetailActivity.class);
                            intent.putExtra("JSON", response.getJSONArray("Event_info")
                                    .getJSONObject(0).toString());
                            intent.putExtra("token", token);
                            intent.putExtra("id", id);
                            mActivity.startActivity(intent);
                            break;
                        case GROUP:
                            intent = new Intent(mActivity, GroupDetailActivity.class);
                            intent.putExtra("JSON", response.toString());
                            intent.putExtra("token", token);
                            intent.putExtra("id", id);
                            mActivity.startActivity(intent);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    Intent intent = new Intent(mActivity, UserDetailActivity.class);
                    intent.putExtra("JSON", response.getJSONObject(0).toString());
                    intent.putExtra("token", token);
                    intent.putExtra("id", id);
                    mActivity.startActivity(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        switch (mType) {
            case USER:
                Calls.getProfileByID(mID, token, jsonHttpResponseHandler);

                break;
            case EVENT:
                Calls.getEvent(mID, jsonHttpResponseHandler);

                break;
            case GROUP:
                Calls.getGroupInfo(mID, jsonHttpResponseHandler);
        }
    }

}
