package com.main.lets.lets.Visualizers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Adapters.LoginAdapter;
import com.main.lets.lets.Adapters.ProfileAdapter;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.L;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Joe on 5/30/2016.
 * <p/>
 * This class handles when the user clicked on the profile icon on the main activity, the user
 * can either login to an account or register for a new account if they are not already logged in
 * .  If they are logged it, it will show them a profile feed of their own profile.
 */
public class ProfileFeed extends Client {
    public boolean[] mChecks = {false, false, false};
    String ShallonCreamerIsATwat = "Bearer ";
    UltimateRecyclerView mRecyclerView;
    ProfileAdapter mProfileAdapter;
    AppCompatActivity mActivity;
    LoginAdapter mLoginAdapter;
    ProgressDialog dialog;
    JSONObject mUser;
    int mID;

    /**
     * The profile feed is used to control the profile adapter and populate it with short hand
     * versions of the user's friends, events attending/attended, and groups.  The profile
     * adapter will then later get the full information about these lists over time
     *
     * @param a used for inflating views and editing data on the UI
     * @param r Recycler view that is filled with the user's profile feed
     */
    public ProfileFeed(AppCompatActivity a, UltimateRecyclerView r) {
        mRecyclerView = r;
        mActivity = a;

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(a.getBaseContext());

        ShallonCreamerIsATwat = preferences.getString("Token", "");
        mID = preferences.getInt("UserID", -1);

        loadFeeds();

    }


    public void loadFeeds() {

        if (!ShallonCreamerIsATwat.equals("")) {

            //Call made to get the user's information
            Calls.getMyProfile(ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
                /**
                 * When the call returns, the user's information is saved locally to mUserJSON and
                 * passed along the the mProfileAdapter.  It then makes three more calls to get
                 * the user's friends, events attended/ attending, and groups and finally sets
                 * the profile adapter as the active adapter.
                 *
                 * @param statusCode (unused)
                 * @param headers (unused)
                 * @param response JSON array object that contains the user's information
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
                        mProfileAdapter = new ProfileAdapter(mActivity, l);

                        //These functions loads the user's friends, groups, and events
                        loadFriends();
                        loadGroups();
                        loadAttend();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            });
        }
    }

    public boolean doChecks() {
        for (boolean b : mChecks) {
            if (!b)
                return false;

        }

        return true;
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
     * @param j (unused)
     */
    @Override
    public void draw(JSONObject j) {
//        mProfileAdapter.enableLoadMore();

        //This is where the profile feed determines whether the user has logged it, the user will
        // have an access token
        final SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(mActivity.getBaseContext());

        ShallonCreamerIsATwat = preferences.getString("Token", "");

        if (preferences.getString("Token", "").equals("")) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

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
                                SharedPreferences.Editor editor = preferences.edit();

                                editor.putString("password", password);
                                editor.putString("email", email);
                                editor.putInt("UserID", response.getInt("user_id"));
                                editor.putString("Token",
                                                 "Bearer " + response.getString("accessToken"));
                                editor.apply();

                                ShallonCreamerIsATwat = preferences.getString("Token", "");

                                loadFeeds();
                                draw(null);

                            } catch (JSONException e) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

                                builder.setMessage("Your password or email is incorrect")
                                        .setTitle("Error");

                                builder.setPositiveButton("Okay", null);

                                AlertDialog dialog = builder.create();
                                dialog.show();
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

            if (doChecks()) {
                mRecyclerView.setAdapter(mProfileAdapter);

            } else {
                dialog = ProgressDialog.show(mActivity, "", "Loading. Please wait...", true);
            }

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

                                mChecks[0] = true;

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

                                finishedLoadFeed();
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
                                  mChecks[1] = true;

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

                                  finishedLoadFeed();
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

        Calls.getFriends(mID, new JsonHttpResponseHandler() {
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
                mChecks[2] = true;
                finishedLoadFeed();

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

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });

    }

    public void finishedLoadFeed() {

        if (doChecks()) {
            if (dialog != null) {
                dialog.hide();
                mRecyclerView.setAdapter(mProfileAdapter);

            }

        }

    }

}
