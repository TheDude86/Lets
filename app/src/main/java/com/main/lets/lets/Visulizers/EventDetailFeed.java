package com.main.lets.lets.Visulizers;

import android.app.Activity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.main.lets.lets.Adapters.EventDetailAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Joe on 5/30/2016.
 */
public class EventDetailFeed extends Client {
    UltimateRecyclerView mRecyclerView;
    EventDetailAdapter mEventAdapter;
    Activity mActivity;

    public EventDetailFeed(Activity a, UltimateRecyclerView r, JSONObject j) {
        ArrayList<String> l = new ArrayList<>();
        mRecyclerView = r;
        mActivity = a;

        l.add(j.toString());
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mEventAdapter = new EventDetailAdapter(a, l);
        mRecyclerView.setAdapter(mEventAdapter);

    }

    @Override
    public void draw(JSONObject j) {
        try {

            RequestParams params = new RequestParams();
            params.put("event_id", j.getInt("Event_ID"));
            post("event/getEventById", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, org.json.JSONObject response) {
                    Log.e("Event", response.toString());

                    try {
                        JSONArray j = response.getJSONArray("Attending_users");
                        String s = "";

                        for (int i = 0; i < j.length(); i++) {
                            mEventAdapter.insert(j.getJSONObject(i).toString());
                            mEventAdapter.mUsers.add(j.getJSONObject(i).toString());

                            if (s.length() < 1)
                                s = j.getJSONObject(i).getString("name");
                            else
                                s += ", " + j.getJSONObject(i).getString("name");


                        }

                        mEventAdapter.getmMainHolder().mAttendance.setText(s);

                        j = response.getJSONArray("Comments");

                        for (int i = 0; i < j.length(); i++) {
                            mEventAdapter.mComment.add(j.getJSONObject(i).toString());

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                      org.json.JSONArray errorResponse) {
                    Log.e("Aync Test Failure", errorResponse.toString());
                }

            });


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
