package com.main.lets.lets.Visualizers;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Adapters.EventDetailAdapter;
import com.main.lets.lets.LetsAPI.Calls;
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
    ArrayList<String> mUsers;
    ArrayList<String> mComments;
    RecyclerView mRecyclerView;
    EventDetailAdapter mEventAdapter;
    Activity mActivity;

    public EventDetailFeed(Activity a, RecyclerView r, JSONObject j) {
        mComments = new ArrayList<>();
        mUsers = new ArrayList<>();
        mRecyclerView = r;
        mActivity = a;

        mRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mEventAdapter = new EventDetailAdapter(a, j.toString());

        mEventAdapter.setOnAttandanceClicked(new EventDetailAdapter.OnAttendanceClicked() {
            @Override
            public void onClicked() {
                mEventAdapter.clearFeed();

                for (String s: mUsers)
                    mEventAdapter.addElement(s);
            }
        });

        mEventAdapter.setOnCommentsClicked(new EventDetailAdapter.OnCommentsClicked() {
            @Override
            public void onClicked() {
                mEventAdapter.clearFeed();

                for (String s: mComments)
                    mEventAdapter.addElement(s);
            }
        });

        mRecyclerView.setAdapter(mEventAdapter);
    }

    @Override
    public void draw(JSONObject j) {
        try {

            Calls.getEvent(j.getInt("Event_ID"), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers,
                                      org.json.JSONObject response) {
                    Log.e("Event", response.toString());

                    try {
                        JSONArray j = response.getJSONArray("Attending_users");
                        String s = "";

                        for (int i = 0; i < j.length(); i++) {
                            mUsers.add(j.getJSONObject(i).toString());
                            mEventAdapter.addElement(j.getJSONObject(i).toString());

                            if (s.length() < 1)
                                s = j.getJSONObject(i).getString("name");
                            else
                                s += ", " + j.getJSONObject(i).getString("name");


                        }

                        mEventAdapter.getmMainHolder().mAttendance.setText(s);

                        j = response.getJSONArray("Comments");

                        for (int i = 0; i < j.length(); i++) {
                            mComments.add(j.getJSONObject(i).toString());

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                      org.json.JSONArray errorResponse) {
                    Log.e("Async Test Failure", errorResponse.toString());
                }

            });


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
