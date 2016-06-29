package com.main.lets.lets.Holders;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
    public OnGroupsClickListener mGroupsClicked;
    public OnEventsClickListener mEventsClicked;
    public EntityAdapter.Viewing mActive;
    public String ShallonCreamerIsATwat;
    public RecyclerView mRecyclerView;
    public ArrayList<String> mList;
    public Activity mActivity;
    public TextView interests;
    public ImageView mPicture;
    public TextView friends;
    public Button bFriends;
    public String mSearch;
    public TextView score;
    public Button bGroups;
    public Button bEvents;
    public TextView name;
    public TextView bio;

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
    public ProfileViewHolder(final View itemView, final Activity a, final String token) {
        super(itemView);

        //initializing global variables, the arraylist holds the entity feed
        ShallonCreamerIsATwat = token;
        mList = new ArrayList<>();
        mActivity = a;

        //Filling the recycler view with the friends list (which is empty at this moment)
        mRecyclerView = (RecyclerView) itemView.findViewById(R.id.entities);

        //The buttons for the user to select which feed to view
        bFriends = (Button) itemView.findViewById(R.id.friends);
        bGroups = (Button) itemView.findViewById(R.id.groups);
        bEvents = (Button) itemView.findViewById(R.id.events);

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
        bFriends.setOnClickListener(this);
        bEvents.setOnClickListener(this);
        bGroups.setOnClickListener(this);

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
                    mFriendsClicked.onItemClick(mRecyclerView);
                }

                break;
            case R.id.groups:
                if (mGroupsClicked != null) {
                    mGroupsClicked.onItemClick(mRecyclerView);
                }

                break;
            case R.id.events:
                if (mEventsClicked != null) {
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

    //Loads the feed into the recycler view
    public void loadFeed(ArrayList<String> list, EntityAdapter.Viewing view) {
        ArrayList<String> searchedList = new ArrayList<>();
        mList = list;

        for (String l : list) {
            try {
                if (new Entity(new JSONObject(l)).mText.contains(mSearch))
                    searchedList.add(l);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        mRecyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(
                new EntityAdapter(mActivity, searchedList, view, ShallonCreamerIsATwat));

    }
}