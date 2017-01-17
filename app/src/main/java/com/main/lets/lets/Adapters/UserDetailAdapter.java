package com.main.lets.lets.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Activities.UserDetailActivity;
import com.main.lets.lets.Holders.EntityViewHolder;
import com.main.lets.lets.Holders.PictureViewHolder;
import com.main.lets.lets.Holders.UserDetailViewHolder;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.LetsAPI.User;
import com.main.lets.lets.LetsAPI.UserData;
import com.main.lets.lets.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Joe on 6/30/2016.
 */
public class UserDetailAdapter extends FeedAdapter {
    public UserDetailViewHolder mHolder;
    public boolean isEditing = false;
    public User mUser;

    public UserDetailAdapter(AppCompatActivity a, User u) {
        mList.add("S");
        mActivity = a;
        mUser = u;

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new UserDetailViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_profile, parent, false), mUser, mActivity, isEditing);

        return new PictureViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_entity_with_picture, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0)
            loadProfile(holder);
        else
            super.onBindViewHolder(holder, position);


    }


    private void loadProfile(RecyclerView.ViewHolder holder) {
        mHolder = (UserDetailViewHolder) holder;

        mHolder.mScore.setText("Score: " + mUser.getScore());
        mHolder.mName.setText(mUser.getName());
        mHolder.mBio.setText(mUser.getBio());

        mHolder.mBio.setHint("bio coming soon...");

        setUsers(mHolder.mFriendsButton);
        setEvents(mHolder.mEventsButton);
        setGroups(mHolder.mGroupsButton);

        mHolder.mOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActionDialogFragment f = new ActionDialogFragment(mActivity);
                f.show(mActivity.getFragmentManager(), "Test");

            }
        });

        mHolder.mInterests.setText(mUser.getInterestsString());
        mUser.loadImage(mActivity, mHolder.mPic);

    }

    public void notifyNewPicture(String url) {

        mUser.setPropic(url);
        mUser.loadImage(mActivity, mHolder.mPic);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addElement(String s) {
        mList.add(s);
        notifyItemInserted(mList.size() - 1);
        notifyDataSetChanged();
    }

    public void clearFeed() {
        int end = mList.size();

        for (int i = 1; i < end; i++) {
            mList.remove(1);
            notifyItemRemoved(1);
            notifyItemRangeChanged(1, mList.size());
        }

    }

    public void notifyFromCreate() {
        isEditing = true;

        if (mHolder != null) {
            mHolder.setEditing(true);
            mHolder.setEditToolbar();

        }

    }

    @SuppressLint("ValidFragment")
    public class ActionDialogFragment extends DialogFragment {
        CharSequence[] mList = {"Report"};
        CharSequence[] mFriendList = {"Report", "Unfriend"};


        AppCompatActivity mActivity;

        public ActionDialogFragment(AppCompatActivity a) {
            mActivity = a;

        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final CharSequence[] list;

            if (mUser.mRelationship == User.Relationship.FRIEND)
                list = mFriendList;
            else
                list = mList;


            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle("Select Action")
                    .setItems(list, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            runAction(list[which].toString());

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }

        public void runAction(String s) {
            com.rey.material.app.Dialog.Builder builder = null;
            com.rey.material.app.DialogFragment fragment;

            switch (s) {
                case "Report":

                    break;
                case "Block":


                    break;
                case "Unfriend":

                    AlertDialog.Builder build = new AlertDialog.Builder(mActivity);
                    build.setTitle("Remove Friend?");
                    build.setMessage("Do you want to remove this person as a friend?");
                    build.setNegativeButton("Cancel", null);
                    build.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            UserData d = new UserData(mActivity);
                            Calls.removeFriend(mUser.mID, d.ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    mHolder.setGuestToolbar();
                                    mHolder.setSearchToolbar();

                                }
                            });

                        }
                    });

                    build.create().show();

                    break;

            }

            if (builder != null) {
                fragment = com.rey.material.app.DialogFragment.newInstance(builder);
                fragment.show(mActivity.getSupportFragmentManager(), null);
            }

        }

    }

}
