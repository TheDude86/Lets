package com.main.lets.lets.Holders;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.main.lets.lets.Activities.EventDetailActivity;
import com.main.lets.lets.Activities.GroupDetailActivity;
import com.main.lets.lets.Activities.UserDetailActivity;
import com.main.lets.lets.Adapters.EntityAdapter;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.R;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Joe on 6/26/2016.
 */
public class ProfileViewHolder extends UltimateRecyclerviewViewHolder
        implements View.OnClickListener {
    public OnFriendsClickListener mFriendsClicked;
    private static final int USER_DETAIL_CODE = 1;
    public OnGroupsClickListener mGroupsClicked;
    public OnEventsClickListener mEventsClicked;
    private static final int DETAIL_CODE = 1;
    public EntityAdapter.Viewing mActive;
    public ArrayList<String> mDetailList;
    public String ShallonCreamerIsATwat;
    public RecyclerView mRecyclerView;
    public ArrayList<String> mList;
    public RelativeLayout mProfile;
    public Activity mActivity;
    public ImageView mProPic;
    public TextView mName;
    public TextView mBio;
    public TextView mUser;
    public String mSearch;
    public TextView mGroup;
    public TextView mEvent;
    public TextView name;
    public int mID;

    /**
     * The profile view holder diplays the profile feed information, the view holder contains a
     * button to view the user's profile, three buttons to select the user's feed and set one to
     * be active.  There is a search widget to allow for a refined search of the active feed.
     * Finally there is a recycler view that contains the active feed to display the user's
     * friends, events, and groups.
     *
     * @param itemView does stuff and things
     * @param a        also used for other methods for inflating XMLs
     * @param token    access token
     */
    public ProfileViewHolder(final View itemView, final Activity a, final String token, int id) {
        super(itemView);

        //initializing global variables, the arraylist holds the entity feed
        mDetailList = new ArrayList<>();
        ShallonCreamerIsATwat = token;
        mList = new ArrayList<>();
        mActivity = a;
        mID = id;


        //User's info
        mProfile = (RelativeLayout) itemView.findViewById(R.id.profile_layout);
        mProPic = (ImageView) itemView.findViewById(R.id.pro_pic);
        mName = (TextView) itemView.findViewById(R.id.name);
        mBio = (TextView) itemView.findViewById(R.id.bio);

        //Filling the recycler view with the friends list (which is empty at this moment)
        mRecyclerView = (RecyclerView) itemView.findViewById(R.id.entities);

        //The buttons for the user to select which feed to view
        mUser = (TextView) itemView.findViewById(R.id.friends);
        mGroup = (TextView) itemView.findViewById(R.id.groups);
        mEvent = (TextView) itemView.findViewById(R.id.events);

        //Initializing the search string and default viewing feed
        mSearch = "";
        mActive = EntityAdapter.Viewing.FRIENDS;

        //Set the on text update listener for the search widget, when the user inputs text, the
        // feed is updated dyanically
        ((SearchView) itemView.findViewById(R.id.search)).setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        mSearch = newText;
                        loadFeed(mList, mActive);

                        return false;
                    }
                });

        //Setting the buttons' click listeners
        mUser.setOnClickListener(this);
        mEvent.setOnClickListener(this);
        mGroup.setOnClickListener(this);

    }

    /**
     * On click listeners for the three feed choosers and the search widget
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.friends:
                if (mFriendsClicked != null) {
                    setActiveButton(mUser);
                    mFriendsClicked.onItemClick(mRecyclerView);
                }

                break;
            case R.id.groups:
                if (mGroupsClicked != null) {
                    setActiveButton(mGroup);
                    mGroupsClicked.onItemClick(mRecyclerView);
                }

                break;
            case R.id.events:
                if (mEventsClicked != null) {
                    setActiveButton(mEvent);
                    mEventsClicked.onItemClick(mRecyclerView);
                }

                break;
        }

    }

    //These interfaces are used by the profile adapter
    public interface OnFriendsClickListener {
        void onItemClick(RecyclerView r);
    }

    public interface OnGroupsClickListener {
        void onItemClick(RecyclerView r);
    }

    public interface OnEventsClickListener {
        void onItemClick(RecyclerView r);
    }

    //Methods to set the on click listeners
    public void setFriendsClicked(final OnFriendsClickListener itemClickListener) {
        mFriendsClicked = itemClickListener;
    }

    public void setGroupsClicked(final OnGroupsClickListener itemClickListener) {
        mGroupsClicked = itemClickListener;
    }

    public void setEventsClicked(final OnEventsClickListener itemClickListener) {
        mEventsClicked = itemClickListener;
    }

    /**
     * Takes in the feed to load and it's detailed list which is loaded sightly later and also
     * which view the user selected (friends/events/groups).  The list is first sorted and then
     * transferred to the entity adapter where the new entity adapter's on click listener is set
     * which then passes the corresponding Entity string to the detail activity to show the user
     * the entity's details
     *
     * @param list         list of entity short hands
     * @param view         which type of list the array list (friends/events/groups)
     */
    public void loadFeed(ArrayList<String> list,
                         final EntityAdapter.Viewing view) {
        ArrayList<String> searchedList = new ArrayList<>();
        //Saves the active feed locally so when the search widget updates, it can reload the feed
        mActive = view;
        mList = list;

        //Searches the list for the text in the search widget and puts the entities with the
        // user's text into the searched list array list
        for (String l : list) {
            try {
                if (new Entity(new JSONObject(l)).mText.toLowerCase()
                        .contains(mSearch.toLowerCase()))
                    searchedList.add(l);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        //Creating the entity adapter and setting the on click listener
        EntityAdapter e = new EntityAdapter(mActivity, searchedList, view);
        e.setOnEntityClickListener(new EntityAdapter.OnEntityClickListener() {
            @Override
            public void onClicked(int id) {
                Intent intent;
                String s = null;

                switch (view) {
                    case FRIENDS:
                        intent = new Intent(mActivity, UserDetailActivity.class);
                        intent.putExtra("token", ShallonCreamerIsATwat);
                        intent.putExtra("id", mID);
                        intent.putExtra("UserID", id);
                        mActivity.startActivityForResult(intent, USER_DETAIL_CODE);
                        break;
                    case GROUPS:

                        intent = new Intent(mActivity, GroupDetailActivity.class);
                        intent.putExtra("token", ShallonCreamerIsATwat);
                        intent.putExtra("id", mID);
                        intent.putExtra("GroupID", id);
                        mActivity.startActivity(intent);

                        break;
                    case EVENTS:
                        intent = new Intent(mActivity, EventDetailActivity.class);
                        intent.putExtra("token", ShallonCreamerIsATwat);
                        intent.putExtra("EventID", id);
                        intent.putExtra("id", mID);
                        mActivity.startActivityForResult(intent, DETAIL_CODE);

                        break;
                }

            }
        });

        // Loads the new entity adapter with the searched list added
        mRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(e);

    }

    public void setActiveButton(TextView t) {
        t.setBackgroundColor(mActivity.getResources().getColor(R.color.colorPrimary));
        if (mEvent != null && !t.equals(mEvent))
            mEvent.setBackgroundColor(mActivity.getResources().getColor(R.color.white));

        if (mUser != null && !t.equals(mUser))
            mUser.setBackgroundColor(mActivity.getResources().getColor(R.color.white));

        if (mGroup != null && !t.equals(mGroup))
            mGroup.setBackgroundColor(mActivity.getResources().getColor(R.color.white));


    }
}