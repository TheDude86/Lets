package com.main.lets.lets.Adapters;

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

import com.main.lets.lets.Activities.UserDetailActivity;
import com.main.lets.lets.Holders.EntityViewHolder;
import com.main.lets.lets.Holders.UserDetailViewHolder;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Joe on 6/30/2016.
 */
public class UserDetailAdapter extends RecyclerView.Adapter {
    public UserDetailActivity.Relationship mRelationship;
    ArrayList<String> mList = new ArrayList<>();
    OnEntityClickListener mEntityClickListener;
    OnFriendClickListener mFriendClickListener;
    OnGroupClickListener mGroupClickListener;
    OnEventClickListener mEventClickListener;
    public UserDetailViewHolder mHolder;
    private AppCompatActivity mActivity;

    public UserDetailAdapter(AppCompatActivity a, JSONObject j, UserDetailActivity.Relationship r) {
        mList.add(j.toString());
        mRelationship = r;
        mActivity = a;

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0)
            return new UserDetailViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_profile, parent, false));

        return new EntityViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_entity_with_space, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0)
            loadProfile(holder);
        else
            loadEntity(holder, position);


    }

    private void loadEntity(RecyclerView.ViewHolder holder, final int position) {
        EntityViewHolder h = (EntityViewHolder) holder;
        try {
            h.mTitle.setText(new Entity(new JSONObject(mList.get(position))).mText);
            h.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mEntityClickListener != null)
                        mEntityClickListener.OnClick(position - 1);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void loadProfile(RecyclerView.ViewHolder holder) {
        mHolder = (UserDetailViewHolder) holder;
        try {
            JSONObject json = new JSONObject(mList.get(0));
            JSONObject interests = json.getJSONObject("interests");

            mHolder.mScore.setText("Score: " + json.getInt("Score"));
            mHolder.mName.setText(json.getString("User_Name"));
            mHolder.mBio.setText(json.getString("Biography"));
            mHolder.mFriendsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mFriendClickListener != null)
                        mFriendClickListener.OnClick();
                }
            });
            mHolder.mEventsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mEventClickListener != null)
                        mEventClickListener.OnClick();
                }
            });

            mHolder.mGroupsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mGroupClickListener != null)
                        mGroupClickListener.OnClick();
                }
            });

            mHolder.mOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ActionDialogFragment f = new ActionDialogFragment(mActivity);
                    f.show(mActivity.getFragmentManager(), "Test");

                }
            });

            String interestString = "";
            Iterator<String> keys = interests.keys();

            while (keys.hasNext()){
                String key = keys.next();
                if (interests.getBoolean(key)) {
                    if (interestString.length() == 0)
                        interestString = key;
                    else
                        interestString += ", " + key;

                }
            }

            mHolder.mInterests.setText(interestString);


            Picasso.with(mActivity).load(json.getString("Profile_Picture")).into(mHolder.mPic);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        ((UserDetailViewHolder) holder).mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addElement("poo");
            }
        });
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

    public interface OnEntityClickListener {
        void OnClick(int position);
    }

    public interface OnFriendClickListener {
        void OnClick();
    }

    public interface OnEventClickListener {
        void OnClick();
    }

    public interface OnGroupClickListener {
        void OnClick();
    }

    public void setOnEntityClickListener(OnEntityClickListener e){
        mEntityClickListener = e;
    }

    public void setOnFriendClickListener(OnFriendClickListener f) {
        mFriendClickListener = f;
    }

    public void setOnEventClickListener(OnEventClickListener e){
        mEventClickListener = e;
    }

    public void setOnGroupClickListener(OnGroupClickListener g){
        mGroupClickListener = g;
    }



    public class ActionDialogFragment extends DialogFragment {
        CharSequence[] mList = {"Report", "Block"};
        CharSequence[] mFriendList = {"Report", "Block", "Unfriend"};


        AppCompatActivity mActivity;

        public ActionDialogFragment(AppCompatActivity a) {
            mActivity = a;

        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final CharSequence[] list;

            if (mRelationship == UserDetailActivity.Relationship.FRIEND)
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

                    break;

            }

            if (builder != null) {
                fragment = com.rey.material.app.DialogFragment.newInstance(builder);
                fragment.show(mActivity.getSupportFragmentManager(), null);
            }

        }

    }

}
