package com.main.lets.lets.Adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.LetsAPI.Event;
import com.main.lets.lets.R;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Joe on 5/30/2016.
 */
public class EventDetailAdapter extends easyRegularAdapter<String, UltimateRecyclerviewViewHolder> {
    public ArrayList<String> mComment;
    public ArrayList<String> mUsers;
    enum ViewType {USERS, COMMENTS}
    ArrayList<String> mEntities;
    MainHolder mMainHolder;
    Activity mActivity;
    ViewType type;

    public EventDetailAdapter(Activity a, ArrayList<String> s) {
        super(s);
        mComment = new ArrayList<>();
        mUsers = new ArrayList<>();
        type = ViewType.USERS;
        mActivity = a;
        mEntities = s;

    }

    public void insert(String s) {
        mEntities.add(s);

    }

    @Override
    protected int getNormalLayoutResId() {

        return R.layout.row_event_detail_main;
    }

    @Override
    protected UltimateRecyclerviewViewHolder newViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    protected void withBindHolder(UltimateRecyclerviewViewHolder holder, String data, int position) {
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public UltimateRecyclerviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {

            if (viewType == 0) {
                mMainHolder = new MainHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.row_event_detail_main, parent, false));

                Event e = new Event(new JSONObject(mEntities.get(0)));

                mMainHolder.mTime.setText(e.getTimeSpanString());
                mMainHolder.mLocation.setText(e.getmLocationTitle());
                mMainHolder.mDescription.setText(e.getmDescription());

                return mMainHolder;
            }

            Entity e = new Entity(new JSONObject(mEntities.get(viewType)));
            ViewHolder v = new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_entity, parent, false));

            v.mName.setText(e.mText);

            return v;

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public class MainHolder extends UltimateRecyclerviewViewHolder {
        public TextView mDescription;
        public TextView mAttendance;
        public TextView mLocation;
        public TextView mComments;
        public TextView mActions;
        public TextView mAttend;
        public TextView mTime;

        public MainHolder(View itemView) {
            super(itemView);

            mDescription = (TextView) itemView.findViewById(R.id.event_description);
            mAttendance = (TextView) itemView.findViewById(R.id.event_attendance);
            mLocation = (TextView) itemView.findViewById(R.id.event_location);
            mComments = (TextView) itemView.findViewById(R.id.btn_comments);
            mActions = (TextView) itemView.findViewById(R.id.btn_actions);
            mAttend = (TextView) itemView.findViewById(R.id.btn_attend);
            mTime = (TextView) itemView.findViewById(R.id.event_time);

            mActions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            mComments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(type == ViewType.USERS) {
                        for (int i = 0; i < mUsers.size(); i++) {
                            removeLast();
                        }

                        for (int i = 0; i < mComment.size(); i++) {
                            insert(mComment.get(i));
                        }
                        type = ViewType.COMMENTS;
                    }
                }
            });

            mAttend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(type == ViewType.COMMENTS) {
                        for (int i = 0; i < mComment.size(); i++) {
                            removeLast();
                        }

                        for (int i = 0; i < mUsers.size(); i++) {
                            insert(mUsers.get(i));
                        }
                        type = ViewType.USERS;
                    }
                }
            });

        }
    }

    public class ViewHolder extends UltimateRecyclerviewViewHolder {
        public TextView mName;

        public ViewHolder(View itemView) {
            super(itemView);
            mName = (TextView)itemView.findViewById(R.id.txt_entity_title);

        }
    }

    public MainHolder getmMainHolder() {
        return mMainHolder;
    }
}
