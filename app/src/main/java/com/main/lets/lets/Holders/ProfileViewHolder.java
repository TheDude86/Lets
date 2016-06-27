package com.main.lets.lets.Holders;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.main.lets.lets.Adapters.EntityAdapter;
import com.main.lets.lets.R;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;

import java.util.ArrayList;

/**
 * Created by Joe on 6/26/2016.
 */
public class ProfileViewHolder extends UltimateRecyclerviewViewHolder implements View.OnClickListener {
    public OnFriendsClickListener mFriendsClicked;
    public OnGroupsClickListener mGroupsClicked;
    public OnEventsClickListener mEventsClicked;
    public String ShallonCreamerIsATwat;
    public RecyclerView mRecyclerView;
    public ArrayList<String> mFriends;
    public ArrayList<String> mEvents;
    public ArrayList<String> mGroups;
    public Activity mActivity;
    public TextView interests;
    public ImageView mPicture;
    public TextView friends;
    public Button bFriends;
    public TextView score;
    public Button bGroups;
    public Button bEvents;
    public TextView name;
    public TextView bio;

    /**
     * The profile view holder diplays the profile feed information, the view holder contains
     *
     * @param itemView
     * @param a
     * @param token
     */
    public ProfileViewHolder(final View itemView, final Activity a, final String token) {
        super(itemView);

        ShallonCreamerIsATwat = token;
        mFriends = new ArrayList<>();
        mEvents = new ArrayList<>();
        mGroups = new ArrayList<>();
        mActivity = a;

        mRecyclerView = (RecyclerView) itemView.findViewById(R.id.entities);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(new EntityAdapter(mActivity, mFriends,
                EntityAdapter.Viewing.FRIENDS, ShallonCreamerIsATwat));

        bFriends = (Button) itemView.findViewById(R.id.friends);
        bGroups = (Button) itemView.findViewById(R.id.groups);
        bEvents = (Button) itemView.findViewById(R.id.events);

        bFriends.setOnClickListener(this);
        bEvents.setOnClickListener(this);
        bGroups.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
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

    public interface OnFriendsClickListener {
        void onItemClick(RecyclerView r);
    }

    public interface OnGroupsClickListener {
        void onItemClick(RecyclerView r);
    }

    public interface OnEventsClickListener {
        void onItemClick(RecyclerView r);
    }

    public void setFriendsClicked(final OnFriendsClickListener itemClickListener) {
        mFriendsClicked = itemClickListener;
    }

    public void setGroupsClicked(final OnGroupsClickListener itemClickListener) {
        mGroupsClicked = itemClickListener;
    }

    public void setEventsClicked(final OnEventsClickListener itemClickListener) {
        mEventsClicked = itemClickListener;
    }

    public void loadFeed(ArrayList<String> list, EntityAdapter.Viewing view) {
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerView.setAdapter(new EntityAdapter(mActivity, list, view, ShallonCreamerIsATwat));

    }
}