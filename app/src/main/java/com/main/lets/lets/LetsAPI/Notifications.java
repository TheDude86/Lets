package com.main.lets.lets.LetsAPI;

import android.support.v7.app.AppCompatActivity;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Visualizers.NotificationFeed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by novosejr on 1/7/2017.
 */

public class Notifications extends Search{
    UserData mUserData;

    public Notifications(UserData d) {
        super(null);
        mUserData = d;

    }

    public void updateNotifications(onUpdateListener l) {

        mListener = l;

        Calls.getNotifications(mUserData.ShallonCreamerIsATwat, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    JSONArray friends = response.getJSONArray("friends");
                    JSONArray events = response.getJSONArray("events");
                    JSONArray groups = response.getJSONArray("groups");

                    for (int i = 0; i < friends.length(); i++) {
                        Entity e = new Entity(friends.getJSONObject(i));


                        if (!e.mStatus && mUserData.ID != e.mSender) {

                            mUsers.add(e);
                        }
                    }

                    for (int i = 0; i < events.length(); i++){
                        Entity e = new Entity(events.getJSONObject(i));
                        if (!e.mStatus)
                            mEvents.add(e);
                    }

                    for (int i = 0; i < groups.length(); i++){
                        Entity e = new Entity(groups.getJSONObject(i));
                        if (!e.mStatus)
                            mGroups.add(e);
                    }



                    mListener.onUpdate();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

    }

}
