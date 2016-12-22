package com.main.lets.lets.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.main.lets.lets.Activities.UserDetailActivity;
import com.main.lets.lets.Holders.ProfileViewHolder;
import com.main.lets.lets.R;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Joe on 12/13/2015.
 */
public class ProfileAdapter extends easyRegularAdapter<String, UltimateRecyclerviewViewHolder> {
    public ProfileViewHolder mDemoHolder;
    public ArrayList<String> mFriendTags;
    public String ShallonCreamerIsATwat;
    public ArrayList<String> mEventTags;
    public ArrayList<String> mGroupTags;
    public ArrayList<String> mFriends;
    public ArrayList<String> mEvents;
    public ArrayList<String> mGroups;
    private boolean mLoadFeed = false;
    public JSONObject mUserJSON;
    public Activity mActivity;
    private int mID;


    /**
     * The Profile Adapter contains the user's friends, groups, and attending/attended events.
     * It passes on the active list to the Profile View SearchEntityHolder where it then takes the information
     * to display it to the user.
     *
     * @param context used for various fuctions throughout
     * @param a
     */
    public ProfileAdapter(Activity context, ArrayList<String> a) {
        super(a);
        mActivity = context;

        if (a.size() > 0) {
            try {
                mUserJSON = new JSONObject(a.get(0));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Initializes as empty arrays because the network calls haven't returned yet.
        mGroups = new ArrayList<>();
        mEvents = new ArrayList<>();
        mFriends = new ArrayList<>();

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(mActivity.getBaseContext());

        //This bitch again...
        ShallonCreamerIsATwat = preferences.getString("Token", "");

        mID = preferences.getInt("UserID", -1);

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
        mDemoHolder = new ProfileViewHolder(view, mActivity, ShallonCreamerIsATwat, mID);

        if (mLoadFeed) {
            mLoadFeed = false;
            loadInitial();
        }

        try {
            loadUserInfo(mUserJSON.getString("Profile_Picture"), mUserJSON.getString("User_Name"),
                         mUserJSON.getString("Biography"), mUserJSON.getInt("User_ID"));


        } catch (JSONException e) {
            e.printStackTrace();
        }


        //Setting the onClicked listeners for the buttons of the profile feed, the buttons allow
        //the user to choose which feed they want to search through and when clicked, that feed
        //becomes the active feed in the Entity Adapter
        mDemoHolder.setFriendsClicked(new ProfileViewHolder.OnFriendsClickListener() {
            @Override
            public void onItemClick(RecyclerView r) {
                mDemoHolder.loadFeed(mFriendTags, EntityAdapter.Viewing.FRIENDS);
            }

        });

        mDemoHolder.setGroupsClicked(new ProfileViewHolder.OnGroupsClickListener() {
            @Override
            public void onItemClick(RecyclerView r) {
                mDemoHolder.loadFeed(mGroupTags, EntityAdapter.Viewing.GROUPS);
            }

        });

        mDemoHolder.setEventsClicked(new ProfileViewHolder.OnEventsClickListener() {
            @Override
            public void onItemClick(RecyclerView r) {
                mDemoHolder.loadFeed(mEventTags, EntityAdapter.Viewing.EVENTS);
            }
        });

        return mDemoHolder;
    }

    public void loadInitial() {
        try {
            mDemoHolder.loadFeed(mFriendTags, EntityAdapter.Viewing.FRIENDS);

        } catch (NullPointerException e) {
            mLoadFeed = true;
        }

    }


    public void loadUserInfo(String image, String name, String bio, final int id) {
        Picasso.with(mActivity).load(image).into(mDemoHolder.mProPic);
        mDemoHolder.mName.setText(name);
        mDemoHolder.mBio.setText(bio);

        mDemoHolder.mProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, UserDetailActivity.class);

                intent.putExtra("UserID", id);
                intent.putExtra("token", ShallonCreamerIsATwat);
                mActivity.startActivity(intent);
            }
        });

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

    }



}
