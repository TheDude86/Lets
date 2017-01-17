package com.main.lets.lets.Adapters;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
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
    NewSearchAdapter mAdapter;
    AppCompatActivity mActivity;

    public NotificationAdapter(AppCompatActivity a, NewSearchAdapter s) {
        mAdapter = s;
        mActivity = a;

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
        return 1;
    }

    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((Holder) holder).mView.setLayoutManager(new GridLayoutManager(mActivity, 1));
        ((Holder) holder).mView.setAdapter(mAdapter);

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_recycler_view, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

    }


    public class Holder extends RecyclerView.ViewHolder {
        RecyclerView mView;

        public Holder(View itemView) {
            super(itemView);

            mView = (RecyclerView) itemView.findViewById(R.id.view);

        }
    }

}
