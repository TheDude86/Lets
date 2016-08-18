package com.main.lets.lets.Visualizers;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.main.lets.lets.Adapters.EntityAdapter;
import com.main.lets.lets.Adapters.LoginAdapter;
import com.main.lets.lets.Adapters.ProfileAdapter;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Login;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Joe on 5/30/2016.
 */
public class ProfileFeed extends Client {
    public static final String FILENAME = "userInfo";
    String ShallonCreamerIsATwat = "Bearer ";
    UltimateRecyclerView mRecyclerView;
    HashMap<String, Object> mUserInfo;
    ProfileAdapter mProfileAdapter;
    LoginAdapter mLoginAdapter;
    Activity mActivity;
    JSONObject mUser;

    /**
     * The profile feed is used to control the profile adapter and populate it with short hand
     * versions of the user's friends, events attending/attended, and groups.  The profile
     * adapter will then later get the full information about these lists over time
     *
     * @param a used for inflating views and editing data on the UI
     * @param r Recycler view that is filled with the user's profile feed
     * @param m hashmap that contains useful user info such as it's token and other useful stuff
     */
    public ProfileFeed(Activity a, UltimateRecyclerView r, HashMap<String, Object> m) {
        mRecyclerView = r;
        mActivity = a;
        mUserInfo = m;

    }

    /**
     * The profile feed determines whether or not the user has logged in and if the user has then
     * it will load the profile adapter, if the user has not logged in, it will load the login
     * adapter.  The login adapter simply takes in an email and password and sends a post request
     * and if the credentials are valid redraw the feed where the profile feed will then be loaded.
     * If the credentials are invalid, the user will be notified and the user can try again.
     * <p/>
     * The profile feed displays the users information including their friends, groups, and events.
     * The user can search all three feeds and then visit their detail activities including the
     * user's own profile.
     *
     * @param j
     */
    @Override
    public void draw(JSONObject j) {
//        mProfileAdapter.enableLoadMore();

        //This is where the profile feed determines whether the user has logged it, the user will
        // have an access token
        if (ShallonCreamerIsATwat.equals("Bearer ")) {
            //Loads the login adapter
            mRecyclerView.setLayoutManager(
                    new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            mLoginAdapter = new LoginAdapter(mActivity);

            //Setting the click listener for the login button
            mLoginAdapter.setOnLoginClick(new LoginAdapter.OnItemClickListener() {
                /**
                 * When the login button is clicked, it gets the inputed email and password from
                 * the edit texts and sends them to log in.
                 *
                 * @param email email the user inputs in the login adapter
                 * @param password password the user inputs in the login adapter
                 */
                @Override
                public void onItemClick(final String email, final String password) {
                    //Network call to login the user
                    Calls.login(email, password, new JsonHttpResponseHandler() {
                        /**
                         * When the call finishes, the access token is returned in the JSON
                         * object, the token is then saved locally and put into the user info
                         * hashmap.  The user's credentials are then saved on the phone so the app
                         * can automatically log in when the app is opened again.
                         *
                         * @param statusCode (unused)
                         * @param headers (unused)
                         * @param response the JSON object contains the access token the user can
                         *                 then use the token to gain exlusive information and
                         *                 full functionality of the app
                         */
                        @Override
                        public void onSuccess(int statusCode, Header[] headers,
                                              org.json.JSONObject response) {

                            try {
                                ShallonCreamerIsATwat += response.getString("accessToken");
                                mUserInfo.put("userID", response.getInt("user_id"));
                                mUserInfo.put("token", ShallonCreamerIsATwat);
                                Login.saveInfo(email, password, response.getInt("user_id"), mActivity.getBaseContext());
                                draw(null);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    });


                }

            });

            //Setting the adapter to be displayed to the user
            mRecyclerView.setAdapter(mLoginAdapter);
            //If the user has logged in
        } else {
            //Preparing the profile adapter to be set to the recycler view
            mRecyclerView.setLayoutManager(
                    new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));

            //Call made to get the user's information
            Calls.getMyProfile(ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
                /**
                 * When the call returns, the user's information is saved locally to mUser and
                 * passed along the the mProfileAdapter.  It then makes three more calls to get
                 * the user's friends, events attended/ attending, and groups and finally sets
                 * the profile adapter as the active adapter.
                 *
                 * @param statusCode (unused)
                 * @param headers (unused)
                 * @param response JSON array object that contains the user's informations
                 */
                @Override
                public void onSuccess(int statusCode, Header[] headers,
                                      JSONObject response) {
                    try {

                        //Saving the user's info locally
                        mUser = response.getJSONArray("info").getJSONObject(0);
                        ArrayList<String> l = new ArrayList<>();
                        l.add(mUser.toString());

                        //Passes the user's info to the profile adapter
                        mProfileAdapter = new ProfileAdapter(mActivity, l, ShallonCreamerIsATwat,
                                (int) mUserInfo.get("userID"));

                        //These functions loads the user's friends, groups, and events
                        loadFriends();
                        loadGroups();
                        loadAttend();

                        //Sets the adapter
                        mRecyclerView.setAdapter(mProfileAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                /**
                 * If something goes wrong with the call handle it here.
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

    /**
     * Used for auto logging in, if the auto login succeeds, then the main activity will update the
     * token so the login adapter will not be displayed.
     *
     * @param s string to replace the old token with
     */
    public void updateToken(String s) {
        ShallonCreamerIsATwat = s;

    }

    /**
     * Network call to get the logged in user's groups and then passes the group list to the
     * profile adapter
     *
     * @throws JSONException
     */
    public void loadGroups() throws JSONException {

        Calls.getGroups(mUser.getInt("User_ID"), ShallonCreamerIsATwat,
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
                                ArrayList<String> groups = new ArrayList<>();
                                try {

                                    for (int i = 0; i < response.length(); i++) {
                                        //Takes all of the JSON object out of the array and
                                        // placed in a temporary array list
                                        groups.add(response.getJSONObject(i).toString());

                                    }

                                    //Loads the temporary array list into the profile adapter
                                    mProfileAdapter.loadGroups(groups);

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
        Calls.getAttended(mUser.getInt("User_ID"), ShallonCreamerIsATwat,
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

                                      ArrayList<String> events = new ArrayList<>();
                                      for (int i = 0; i < response.length(); i++) {
                                          //Loads the events in the temporary array list
                                          events.add(response.getJSONObject(i).toString());

                                      }

                                      mProfileAdapter.loadEvents(events);

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

        Log.println(Log.ASSERT, "ProfileFeed", mUserInfo.toString());

        Calls.getFriends((Integer) mUserInfo.get("userID"), new JsonHttpResponseHandler() {
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
                ArrayList<String> friends = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {

                        if (response.getJSONObject(i).getBoolean("status"))
                            //Loads the friends into a temporary array list
                            friends.add(response.getJSONObject(i).toString());

                    } catch (org.json.JSONException e) {
                        e.printStackTrace();
                    }

                }

                try {
                    //Loads the temporary array list into the profile adapter
                    mProfileAdapter.loadFriends(friends);
                    //Loads the active feed of the profile adapter to the friends feed
                    if (mProfileAdapter.mDemoHolder != null)
                        mProfileAdapter.mDemoHolder.loadFeed(friends, new ArrayList<String>(),
                                                         EntityAdapter.Viewing.FRIENDS);

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
            public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                  org.json.JSONArray errorResponse) {
                Log.e("Async Test Failure", errorResponse.toString());
            }

        });

    }

}
