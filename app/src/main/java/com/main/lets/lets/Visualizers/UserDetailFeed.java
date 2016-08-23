package com.main.lets.lets.Visualizers;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Activities.EventDetailActivity;
import com.main.lets.lets.Activities.GroupDetailActivity;
import com.main.lets.lets.Activities.UserDetailActivity;
import com.main.lets.lets.Adapters.GroupDetailAdapter;
import com.main.lets.lets.Adapters.UserDetailAdapter;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jnovosel on 6/24/16.
 */
public class UserDetailFeed extends Client {
    public UserDetailActivity.Relationship mRelationship;

    String ShallonCreamerIsATwat;
    UserDetailAdapter mAdapter;
    RecyclerView mRecyclerView;
    AppCompatActivity mActivity;
    JSONObject mJSON;

    public UserDetailFeed(AppCompatActivity a, RecyclerView recyclerView, JSONObject j,
                          String token, UserDetailActivity.Relationship r) {

        ShallonCreamerIsATwat = token;
        mRecyclerView = recyclerView;

        mRelationship = r;
        mActivity = a;
        mJSON = j;

        mAdapter = new UserDetailAdapter(mActivity, mJSON, mRelationship);


        try {
            loadAttend();
            loadGroups();
            loadFriends();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void draw(JSONObject j) {

        mRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

        mRecyclerView.setAdapter(mAdapter);

    }


    /**
     * Network call to get the logged in user's groups and then passes the group list to the
     * profile adapter
     *
     * @throws JSONException
     */
    public void loadGroups() throws JSONException {

        Calls.getGroups(mJSON.getInt("User_ID"), ShallonCreamerIsATwat,
                        new JsonHttpResponseHandler() {
                            /**
                             * When the call is made, it returns a JSON array object of all of
                             * the groups the user attends.  The JSON objects from the array are
                             * then added to the profile adapter.
                             *
                             * @param statusCode (unused)
                             * @param headers (unused)
                             * @param response JSON array of all groups the user is a part of
                             */
                            @Override
                            public void onSuccess(int statusCode, Header[] headers,
                                                  org.json.JSONArray response) {
                                try {
                                    for (int i = 0; i < response.length(); i++) {
                                        //Takes all of the JSON object out of the array and
                                        // placed in a temporary array list
                                        mAdapter.mGroups.add(response.getJSONObject(i).toString());

                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }


                        });


    }


    /**
     * Network call to get the logged in user's events and then passes the group list to the
     * profile adapter
     *
     * @throws JSONException
     */
    public void loadAttend() throws JSONException {
        //Call to get the events the user is attending/ attended
        Calls.getAttended(mJSON.getInt("User_ID"), ShallonCreamerIsATwat,
                          new JsonHttpResponseHandler() {
                              /**
                               * When the call is made, it returns a JSON array object of all of
                               * the events the user attends.  The JSON objects from the array are
                               * then added to the profile adapter.
                               *
                               * @param statusCode (unused)
                               * @param headers (unused)
                               * @param response JSON array of all events the user is a part of
                               */
                              @Override
                              public void onSuccess(int statusCode, Header[] headers,
                                                    org.json.JSONArray response) {
                                  try {
                                      for (int i = 0; i < response.length(); i++) {
                                          //Loads the events in the temporary array list
                                          mAdapter.mEvents.add(response.getJSONObject(i).toString());

                                      }

                                  } catch (org.json.JSONException e) {
                                      e.printStackTrace();
                                  }
                              }

                          });

    }

    /**
     * Network call to get the logged in user's friends and then passes the group list to the
     * profile adapter
     *
     * @throws JSONException
     */
    public void loadFriends() throws JSONException {
        //Call to get the user's friends
        Calls.getFriends(mJSON.getInt("User_ID"), new JsonHttpResponseHandler() {
            /**
             * When the call is made, it returns a JSON array object of all of
             * the friends the user attends.  The JSON objects from the array are
             * then added to the profile adapter.
             *
             * @param statusCode (unused)
             * @param headers (unused)
             * @param response JSON array of all friends the user is a part of
             */
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {


                        if (response.getJSONObject(i).getBoolean("status")) {
                            //Loads the friends into a temporary array list
                            mAdapter.mUsers.add(response.getJSONObject(i).toString());


                        }


                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }

                }

                mAdapter.mHolder.mFriends.setText(mAdapter.mUsers.size() +
                                                          ((mAdapter.mUsers.size() > 1) ? " Friends" :
                                                                  " Friend"));

                for (String l : mAdapter.mUsers)
                    mAdapter.addElement(l);

            }

        });

    }


}
