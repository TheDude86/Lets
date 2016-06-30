package com.main.lets.lets.Adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.main.lets.lets.Activities.EventDetailActivity;
import com.main.lets.lets.Activities.MainActivity;
import com.main.lets.lets.Holders.ProfileViewHolder;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.LetsAPI.User;
import com.main.lets.lets.R;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Joe on 12/13/2015.
 */
public class ProfileAdapter extends easyRegularAdapter<String, UltimateRecyclerviewViewHolder> {
    private String ShallonCreamerIsATwat;
    public ProfileViewHolder mDemoHolder;
    public ArrayList<String> mFriendTags;
    public ArrayList<String> mEventTags;
    public ArrayList<String> mGroupTags;
    public ArrayList<String> mFriends;
    public ArrayList<String> mGroups;
    public ArrayList<String> mEvents;
    private Activity mActivity;

    private ImageView mPicture;


    /**
     * The Profile Adapter contains the user's friends, groups, and attending/attended events.
     * It passes on the active list to the Profile View Holder where it then takes the information
     * to display it to the user.
     *
     * @param context used for various fuctions throughout
     * @param a
     * @param token
     */
    public ProfileAdapter(Activity context, ArrayList<String> a, String token) {
        super(a);
        mActivity = context;

        //Initializes as empty arrays because the network calls haven't returned yet.
        mGroups = new ArrayList<>();
        mEvents = new ArrayList<>();
        mFriends = new ArrayList<>();

        //This bitch again...
        ShallonCreamerIsATwat = token;

        /**
         *These hold the entity's short hand:
         *      {
         *          "id": ##,
         *          "name": "@@@@@",
         *          "status: true/false     (only if the entity is a friend)
         *      }
         *
         * which are used for the feed displayed on the Profile Feed as well as to get the
         * entity's full information
         */
        mFriendTags = new ArrayList<>();
        mGroupTags = new ArrayList<>();
        mEventTags = new ArrayList<>();

    }


    /**
     * Used by super class
     *
     * @return
     */

    @Override
    protected int getNormalLayoutResId() {
        return R.layout.row_profile;
    }

    /**
     * Used by super class
     *
     * @param view (unused)
     * @return
     */
    @Override
    protected UltimateRecyclerviewViewHolder newViewHolder(View view) {

        return newViewHolder(view);
    }

    /**
     * Used by super class
     *
     * @param holder   (unused)
     * @param data     (unused)
     * @param position (unused)
     */
    @Override
    protected void withBindHolder(UltimateRecyclerviewViewHolder holder, String data,
                                  int position) {
    }

    /**
     * Returns the position as the view type, the onCreateViewHolder method then determines which
     * view holder to load, in this case there is only one element so it doesn't really do much.
     *
     * @param position is the position of the element being loaded in the recycler view
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /**
     * Inflates the XML file to be displayed for the current element, the first (and only) element
     * is the profile feed viewer (good enough name I guess) and sets the listeners for the buttons
     * and the search widget.
     *
     * @param parent   used by methods that I don't know how they work
     * @param viewType is the same as position but more relavent here to determine which layout to
     *                 load
     * @return
     */
    @Override
    public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        //inflates the XML file
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_profile_demo, parent, false);

        //initializing the view holder
        mDemoHolder = new ProfileViewHolder(view, mActivity, ShallonCreamerIsATwat);


        //Setting the onClicked listeners for the buttons of the profile feed, the buttons allow
        //the user to choose which feed they want to search through and when clicked, that feed
        //becomes the active feed in the Entity Adapter
        mDemoHolder.setFriendsClicked(new ProfileViewHolder.OnFriendsClickListener() {
            @Override
            public void onItemClick(RecyclerView r) {
                mDemoHolder.loadFeed(mFriendTags, mFriends, EntityAdapter.Viewing.FRIENDS);
            }

        });

        mDemoHolder.setGroupsClicked(new ProfileViewHolder.OnGroupsClickListener() {
            @Override
            public void onItemClick(RecyclerView r) {
                mDemoHolder.loadFeed(mGroupTags, mGroups, EntityAdapter.Viewing.GROUPS);
            }

        });

        mDemoHolder.setEventsClicked(new ProfileViewHolder.OnEventsClickListener() {
            @Override
            public void onItemClick(RecyclerView r) {
                mDemoHolder.loadFeed(mEventTags, mEvents, EntityAdapter.Viewing.EVENTS);
            }
        });

        return mDemoHolder;
    }

    /**
     * This takes in the short hand versions of events that are retrieved from the "/getAttended"
     * call and then gets the full group info recursively here.
     *
     * @param events list of group short hands as JSONs, they contain the group title and ID
     * @throws JSONException
     */
    public void loadEvents(ArrayList<String> events) throws JSONException {
        mEventTags = events;
        loadEventsHelper(events, 0);

    }

    /**
     * Helper function for loadEventss, uses the index paramter to iterate through the events
     * array list
     *
     * @param events list of group short hands as JSONs, they contain the group title and ID
     * @param index  used to get element from events and iterate through the list
     * @throws JSONException
     */
    public void loadEventsHelper(final ArrayList<String> events, final int index)
            throws JSONException {
        //Once it goes through the entire list, return
        if (events.size() <= index)
            return;

        //Network call made to get an event's information
        Calls.getEvent(new JSONObject(events.get(index)).getInt("event_id"),
                           new JsonHttpResponseHandler() {

                               /**
                                * When the call is finished, a JSON object is returned as the
                                * response containing all of the information about the group, the
                                * JSON's string is then placed in an array list and then calls
                                * itself again
                                * @param statusCode (unused)
                                * @param headers    (unused)
                                * @param response   JSON containing all of a group's information
                                */
                               @Override
                               public void onSuccess(int statusCode, Header[] headers,
                                                     JSONObject response) {

                                   //TODO add check to make sure a valid event was returned

                                   //Adding the response to the array list, the response contains
                                   // the group's info, members, and admins
                                   mEvents.add(response.toString());

                                   try {
                                       //Recursively calls itself again iterating through the
                                       // list by one
                                       loadEventsHelper(events, index + 1);
                                   } catch (JSONException e) {
                                       e.printStackTrace();
                                   }

                               }

                               /**
                                * Called when there is an error with the server
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
     * This takes in the short hand versions of groups that are retrieved from the "/getGroups" call
     * and then gets the full group info recursively here.
     *
     * @param groups list of group short hands as JSONs, they contain the group title and ID
     * @throws JSONException
     */
    public void loadGroups(ArrayList<String> groups) throws JSONException {
        mGroupTags = groups;
        loadGroupsHelper(groups, 0);

    }

    /**
     * Helper function for loadGroups, uses the index paramter to iterate through the groups
     * array list
     *
     * @param groups list of group short hands as JSONs, they contain the group title and ID
     * @param index  used to get element from groups and iterate through the list
     * @throws JSONException
     */
    public void loadGroupsHelper(final ArrayList<String> groups, final int index)
            throws JSONException {
        //Once it goes through the entire list, return
        if (groups.size() <= index)
            return;

        //Network call made to get a group's information
        Calls.getGroupInfo(new Entity(new JSONObject(groups.get(index))).mID,
                           new JsonHttpResponseHandler() {

                               /**
                                * When the call is finished, a JSON object is returned as the
                                * response containing all of the information about the group, the
                                * JSON's string is then placed in an array list and then calls
                                * itself again
                                * @param statusCode (unused)
                                * @param headers    (unused)
                                * @param response   JSON containing all of a group's information
                                */
                               @Override
                               public void onSuccess(int statusCode, Header[] headers,
                                                     JSONObject response) {

                                   //TODO add check to make sure a valid group was returned

                                   //Adding the response to the array list, the response contains
                                   // the group's info, members, and admins
                                   mGroups.add(response.toString());

                                   try {
                                       //Recursively calls itself again iterating through the
                                       // list by one
                                       loadGroupsHelper(groups, index + 1);
                                   } catch (JSONException e) {
                                       e.printStackTrace();
                                   }

                               }

                               /**
                                * Called when there is an error with the server
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
     * This takes in the short hand versions of friends that are retrieved from the "/getFriends"
     * call and then gets the full friends info recursively here.
     *
     * @param friends list of friends short hands as JSONs, they contain the user's name, ID, and
     *                the friendship's status
     * @throws JSONException
     */
    public void loadFriends(ArrayList<String> friends) throws JSONException {
        mFriendTags = friends;
        loadFriendsHelper(friends, 0);

    }

    /**
     * Helper function for loadFriends, uses the index paramter to iterate through the friends
     * array list
     *
     * @param friends list of friend short hands as JSONs, they contain the user's name, ID, and
     *                the friendship's status
     * @param index   used to get element from friends and iterate through the list
     * @throws JSONException
     */
    public void loadFriendsHelper(final ArrayList<String> friends, final int index)
            throws JSONException {
        //Once it goes through the entire list, return
        if (friends.size() <= index) {
            mDemoHolder.mDetailList = mFriends;
            return;
        }

        //Network call to get a user's full information
        Calls.getProfileByID(new Entity(new JSONObject(friends.get(index))).mID,
                             ShallonCreamerIsATwat, new JsonHttpResponseHandler() {

                    //TODO add check to make sure a valid friend was returned

                    /**
                     * When the call is done, it will return a JSON array object that will
                     * contain all of the user's information and that will be
                     * @param statusCode (unused)
                     * @param headers (unused)
                     * @param response JSON array object with all of user's info
                     */
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        try {
                            //Adds the user info to the friends array list
                            mFriends.add(response.getJSONObject(0).toString());
                            //Recursively calls itself again and iterates through the index by one
                            loadFriendsHelper(friends, index + 1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    /**
                     * Called when there is an error with the network call
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

    /**
     * Sets the user's profile picture in the profile feed
     *
     * @param b
     */
    public void setmPicture(Bitmap b) {
        mPicture.setImageBitmap(b);

    }

}
