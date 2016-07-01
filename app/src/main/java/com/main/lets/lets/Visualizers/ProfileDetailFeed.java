package com.main.lets.lets.Visualizers;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Adapters.EntityAdapter;
import com.main.lets.lets.Adapters.UserDetailAdapter;
import com.main.lets.lets.LetsAPI.Calls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jnovosel on 6/24/16.
 */
public class ProfileDetailFeed extends Client {
    ArrayList<String> mFriends, mFriendTags, mGroups, mEvents, mEventTags, mGroupTags;
    String ShallonCreamerIsATwat;
    UserDetailAdapter mAdapter;
    RecyclerView mRecyclerView;
    JSONObject mJSON;

    public ProfileDetailFeed(RecyclerView recyclerView, JSONObject j, String token) {
        mFriendTags = new ArrayList<>();
        mEventTags = new ArrayList<>();
        mGroupTags = new ArrayList<>();
        ShallonCreamerIsATwat = token;
        mRecyclerView = recyclerView;
        mFriends = new ArrayList<>();
        mGroups = new ArrayList<>();
        mEvents = new ArrayList<>();
        mJSON = j;

        try {
            loadAttend();
            loadGroups();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void draw(JSONObject j) {
        mRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mAdapter = new UserDetailAdapter(mJSON);
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
                                        mGroupTags.add(response.getJSONObject(i).toString());

                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            /**
                             * Called when an error occurs somewhere with the call.
                             *
                             * @param statusCode (unused)
                             * @param headers (unused)
                             * @param throwable (unused)
                             * @param errorResponse (unused)
                             */
                            @Override
                            public void onFailure(int statusCode, Header[] headers,
                                                  Throwable throwable,
                                                  org.json.JSONArray errorResponse) {
                                Log.e("Async Test Failure", errorResponse.toString());
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
                                          mEventTags.add(response.getJSONObject(i).toString());

                                      }


                                  } catch (org.json.JSONException e) {
                                      e.printStackTrace();
                                  }
                              }

                              /**
                               * Called when an error occurs somewhere with the call.
                               *
                               * @param statusCode (unused)
                               * @param headers (unused)
                               * @param throwable (unused)
                               * @param errorResponse (unused)
                               */
                              @Override
                              public void onFailure(int statusCode, Header[] headers,
                                                    String errorResponse, Throwable throwable) {
                                  Log.e("Async Test Failure", errorResponse);
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
        Calls.getFriends(ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
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

                        if (response.getJSONObject(i).getBoolean("status"))
                            //Loads the friends into a temporary array list
                            mFriendTags.add(response.getJSONObject(i).toString());

                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }

                }

            }

            /**
             * Called when an error occurs somewhere with the call.
             *
             * @param statusCode (unused)
             * @param headers (unused)
             * @param throwable (unused)
             * @param errorResponse (unused)
             */
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  org.json.JSONArray errorResponse) {
                Log.e("Async Test Failure", errorResponse.toString());
            }

        });

    }

}
