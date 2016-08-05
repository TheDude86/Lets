package com.main.lets.lets.Adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.main.lets.lets.Holders.EntityViewHolder;
import com.main.lets.lets.Holders.UserDetailViewHolder;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Joe on 6/30/2016.
 */
public class UserDetailAdapter extends RecyclerView.Adapter {
    ArrayList<String> mList = new ArrayList<>();
     OnEntityClickListener mEntityClickListener;
    OnFriendClickListener mFriendClickListener;
    OnGroupClickListener mGroupClickListener;
    OnEventClickListener mEventClickListener;
    public UserDetailViewHolder mHolder;
    private Activity mActivity;

    public UserDetailAdapter(Activity a, JSONObject j) {
        mList.add(j.toString());
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
            mHolder.mInterests.setText("Fix this too");

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

}
