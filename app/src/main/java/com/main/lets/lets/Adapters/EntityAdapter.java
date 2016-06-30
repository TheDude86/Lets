package com.main.lets.lets.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.main.lets.lets.Activities.EventDetailActivity;
import com.main.lets.lets.Activities.GroupDetailActivity;
import com.main.lets.lets.Activities.UserDetailActivity;
import com.main.lets.lets.Holders.EntityViewHolder;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.LetsAPI.Event;
import com.main.lets.lets.R;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Joe on 6/6/2016.
 */
public class EntityAdapter extends RecyclerView.Adapter {
    private OnEntityClickListener mOnEntityClicked;

    public enum Viewing {EVENTS, GROUPS, FRIENDS}

    private static final int DETAIL_CODE = 1;
    private String ShallonCreamerIsATwat;
    private ArrayList<String> mList;
    private Activity mActivity;
    private Viewing active;

    public EntityAdapter(Activity a, ArrayList<String> list, Viewing v, String token) {
        ShallonCreamerIsATwat = token;
        mActivity = a;
        mList = list;
        active = v;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (active) {
            case EVENTS:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_entity_with_space, parent, false);

                return new EntityViewHolder(view);
            case FRIENDS:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_entity_with_space, parent, false);

                return new EntityViewHolder(view);
            case GROUPS:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_entity_with_space, parent, false);

                return new EntityViewHolder(view);

        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {

            switch (active) {
                case EVENTS:
                    bindEvent((EntityViewHolder) holder, mList.get(position), position);

                    break;

                case FRIENDS:
                    bindFriend((EntityViewHolder) holder, mList.get(position), position);

                    break;
                case GROUPS:
                    bindGroup((EntityViewHolder) holder, mList.get(position), position);

                    break;

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void bindGroup(EntityViewHolder holder, String data, final int position)
            throws JSONException {
        holder.mTitle.setText(new Entity(new JSONObject(data)).mText);
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnEntityClicked != null)
                    mOnEntityClicked.onClicked(position);
            }
        });

    }

    public void bindFriend(EntityViewHolder holder, final String json, final int position)
            throws JSONException {
        holder.mTitle.setText(new Entity(new JSONObject(json)).mText);
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnEntityClicked != null)
                    mOnEntityClicked.onClicked(position);
            }
        });

    }

    public void bindEvent(final EntityViewHolder holder, final String data, final int position)
            throws JSONException {
        holder.mTitle.setText(new Entity(new JSONObject(data)).mText);
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnEntityClicked != null)
                    mOnEntityClicked.onClicked(position);
            }
        });

    }


    public interface OnEntityClickListener {
        void onClicked(int position);
    }

    public void setOnEntityClickListener(OnEntityClickListener e) {
        mOnEntityClicked = e;

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

}
