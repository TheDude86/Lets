package com.main.lets.lets.LetsAPI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Activities.EventDetailActivity;
import com.main.lets.lets.Activities.GroupDetailActivity;
import com.main.lets.lets.Activities.UserDetailActivity;
import com.main.lets.lets.Visualizers.Client;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Joe on 5/30/2016.
 *
 * Class used to turn json objects into entity short hands and then retrieve their full details.
 */
public class Entity extends Client {
    public enum EntityType {EVENT, USER, GROUP, COMMENT, UTITLITY}

    public static final int UTITLITY_HEADER = 0;
    public static final int UTITLITY_LOADMORE = 1;

    public EntityType mType;
    public Drawable mPicture;
    public boolean mStatus;
    public String mDetail;
    public int mCategory;
    public String mText;
    public String mPic;
    public int mID;

    /**
     * Create an entity manually
     *
     * @param i entity ID
     * @param s entity detail String
     * @param e entity type (Event, User, Group, or Comment)
     */
    public Entity(int i, String s, EntityType e) {
        mCategory = -1;
        mPic = "";
        mType = e;
        mText = s;
        mID = i;

    }

    public Entity(JSONObject j) {
        try {
            if (j.has("Sender")) {
                mDetail = "";
                mType = EntityType.USER;
                mID = j.getInt("Sender");
                mText = j.getString("User_Name");
                if (j.has("pic_ref"))
                    mPic = j.getString("pic_ref");

            } else if (j.has("user_id") && !j.has("text")) {
                mDetail = "";
                mType = EntityType.USER;
                mID = j.getInt("user_id");
                mText = j.getString("name");
                if (j.has("pic_ref"))
                    mPic = j.getString("pic_ref");

            } else if (j.has("event_id") && !j.has("text")) {
                mType = EntityType.EVENT;
                mID = j.getInt("event_id");
                mText = j.getString("event_name");

                if (j.has("category")) {
                    mCategory = j.getInt("category");
                }

                if (j.has("attendance_count")){
                    int i = j.getInt("attendance_count");
                    mDetail = i + (i > 1 ? " People " : " Person ") + "attending";

                } else {
                    mDetail = "";

                }

            } else if (j.has("text")) {
                if (j.has("pic_ref"))
                    mPic = j.getString("pic_ref");

                if (j.has("user_name"))
                    mText = j.getString("user_name");

                mType = EntityType.COMMENT;
                mID = j.getInt("user_id");
                mDetail = j.getString("text");

            } else if (j.has("group_name")) {
                mType = EntityType.GROUP;
                mID = j.getInt("group_id");
                mText = j.getString("group_name");

                if (j.has("member_count")){
                    int i = j.getInt("member_count");
                    mDetail = i + (i > 1 ? " Members" : " Member");

                } else {
                    mDetail = "";

                }

                if (j.has("pic_ref"))
                    mPic = j.getString("pic_ref");
            }

            if (j.has("status")) {
                mStatus = j.getBoolean("status");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void draw(JSONObject j) {
        //noinspection NumericOverflow
        int x = 3 / 0;

    }

    public void loadImage(Activity a, ImageView v) {

        if (mPicture == null) {
            if (mPic != null && !mPic.equals("")) {
                Picasso.with(a).load(mPic).into(v);
                mPicture = v.getDrawable();


            }

            if (mType == EntityType.EVENT)
                Picasso.with(a).load(a.getResources().getIdentifier(("j" + Integer.toString(mCategory))
                                                                            .replaceAll("\\s+", "").toLowerCase(), "drawable", a.getPackageName())).into(v);

        } else {
            v.setImageDrawable(mPicture);
        }

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

    @Override
    public boolean equals(Object o) {
        if (o.getClass().equals(Entity.class)) {
            Entity e = (Entity) o;

            if (e.mID == mID && e.mType == mType)
                return true;

        }


        return false;
    }
}
