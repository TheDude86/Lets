package com.main.lets.lets.Adapters;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.main.lets.lets.Holders.EntityViewHolder;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.R;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by novosejr on 8/21/2016.
 */
public class NotificationAdapter extends UltimateViewAdapter {
    String ShallonCreamerIsATwat;
    AppCompatActivity mActivity;
    JSONArray mNotifications;
    int mEventHeader;
    int mGroupHeader;

    public NotificationAdapter(AppCompatActivity a, JSONObject notifications) {
        mActivity = a;

        mNotifications = new JSONArray();

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(mActivity.getBaseContext());

        ShallonCreamerIsATwat = preferences.getString("Token", "");

        try {
            JSONArray mFriends = notifications.getJSONArray("friends");
            JSONArray mEvents = notifications.getJSONArray("events");
            JSONArray mGroups = notifications.getJSONArray("groups");

            mNotifications.put("Never gonna give you up! Never gonna let you down!");

            for (int i = 0; i < mFriends.length(); i++) {
                if (!mFriends.getJSONObject(i).getBoolean("status") &&
                        mFriends.getJSONObject(i).getInt("sender") != preferences.getInt("UserID", -1)) {
                    mNotifications.put(mFriends.getJSONObject(i));
                }

            }

            mEventHeader = mNotifications.length();
            mNotifications.put("Boobs");

            for (int i = 0; i < mEvents.length(); i++) {
                if (!mEvents.getJSONObject(i).getBoolean("status")) {
                    mNotifications.put(mEvents.getJSONObject(i));

                }

            }

            mGroupHeader = mNotifications.length();
            mNotifications.put("Hey Shallon, you're a white trash whore");

            for (int i = 0; i < mGroups.length(); i++) {
                if (!mGroups.getJSONObject(i).getBoolean("status")) {
                    mNotifications.put(mGroups.getJSONObject(i));

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public RecyclerView.ViewHolder newFooterHolder(View view) {
        return null;
    }

    @Override
    public RecyclerView.ViewHolder newHeaderHolder(View view) {
        return null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {
        return new EntityViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_entity_with_picture, parent, false));
    }

    @Override
    public int getAdapterItemCount() {
        return mNotifications.length();
    }

    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            ((EntityHeader) holder).mTitle.setText("Friend Requests");
        } else if (position == mEventHeader) {
            ((EntityHeader) holder).mTitle.setText("Event Invites");
        } else if (position == mGroupHeader) {
            ((EntityHeader) holder).mTitle.setText("Group Invites");
        } else {
            try {
                final Entity e = new Entity(mNotifications.getJSONObject(position));
                ((EntityViewHolder) holder).mTitle.setText(e.mText);
                ((EntityViewHolder) holder).mLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        e.loadDetailActivity(mActivity, ShallonCreamerIsATwat, e.mID);
                    }
                });
                Picasso.with(mActivity).load(e.mPic).into(((EntityViewHolder) holder).mImage);

            } catch (JSONException e1) {
                e1.printStackTrace();
            }

        }



    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return new EntityHeader(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.header_notification, parent, false));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0 || viewType == mEventHeader || viewType == mGroupHeader)
            return new EntityHeader(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.header_notification, parent, false));


        return new EntityViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_entity_with_picture, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((EntityHeader) holder).mTitle.setText("Hia");
    }

    public class EntityHeader extends RecyclerView.ViewHolder {
        public TextView mTitle;

        public EntityHeader(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.header);

        }
    }

}
