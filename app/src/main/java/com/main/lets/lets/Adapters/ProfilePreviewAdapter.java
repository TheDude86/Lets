package com.main.lets.lets.Adapters;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.LetsAPI.Event;
import com.main.lets.lets.LetsAPI.EventEntity;
import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.LetsAPI.User;
import com.main.lets.lets.R;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by novosejr on 12/18/2016.
 */
public class ProfilePreviewAdapter extends UltimateViewAdapter<UltimateRecyclerviewViewHolder> {
    AppCompatActivity mActivity;
    EventHolder mFuture;
    EventHolder mPast;
    User mUser;
    int mID;

    public ProfilePreviewAdapter(AppCompatActivity a, int i) {
        mActivity = a;
        mID = i;

        mUser = new User(i);
        mUser.load(a, new User.OnLoadListener() {

            @Override
            public void update() {

                L.println(ProfilePreviewAdapter.class, "Test");

                for (EventEntity e: mUser.mEvents) {

                    if (e.mEnd.before(Calendar.getInstance().getTime())) {
                        mPast.mAdapter.mFeed.add(e);
                        mPast.mAdapter.notifyItemChanged(mFuture.mAdapter.mFeed.size() - 1);

                    } else {
                        mFuture.mAdapter.mFeed.add(0, e);
                        mFuture.mAdapter.notifyItemChanged(0);
                    }

                }



            }

        });

    }


    @Override
    public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == 0)
            return new MainHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_profile_preview, parent,
                            false));

        return new EventHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_profile_preview_events, parent,
                        false));

    }

    @Override
    public UltimateRecyclerviewViewHolder newFooterHolder(View view) {
        return null;
    }

    @Override
    public UltimateRecyclerviewViewHolder newHeaderHolder(View view) {
        return null;
    }

    @Override
    public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindViewHolder(UltimateRecyclerviewViewHolder holder, int position) {

        if (position == 1) {
            mFuture = (EventHolder) holder;


        } else if (position == 2) {
            mPast = (EventHolder) holder;
            mPast.mTitle.setText("Past Events");
        }



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

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public int getAdapterItemCount() {
        return 0;
    }

    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    public class MainHolder extends UltimateRecyclerviewViewHolder {
        ImageView mImage;
        TextView mName;

        public MainHolder(View itemView) {
            super(itemView);

            mImage = (ImageView) itemView.findViewById(R.id.image);
            mName = (TextView) itemView.findViewById(R.id.name);

        }

    }

    public class EventHolder extends UltimateRecyclerviewViewHolder {
        SearchEntityAdapter mAdapter;
        RecyclerView mRecyclerView;
        TextView mTitle;

        public EventHolder(View itemView) {
            super(itemView);

            mTitle = (TextView) itemView.findViewById(R.id.title);
            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.view);

            mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 1));
            mAdapter = new SearchEntityAdapter(new ArrayList<Entity>(), mActivity);
            mRecyclerView.setAdapter(mAdapter);

        }

    }



}
