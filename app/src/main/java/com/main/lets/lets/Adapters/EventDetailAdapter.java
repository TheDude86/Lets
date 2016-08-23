package com.main.lets.lets.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.LetsAPI.Event;
import com.main.lets.lets.R;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Joe on 5/30/2016.
 */
public class EventDetailAdapter extends RecyclerView.Adapter {
    public OnAttendanceClicked mAttendanceClicked;
    public OnCommentsClicked mCommentsClicked;
    public OnActionsClicked mActionsClicked;
    public OnEntityClicked mEntityClicked;
    public OnJoinClicked mOnJoinedClicked;
    public ArrayList<String> mList;
    public MemberStatus mStatus;
    public int mID;

    public enum ViewType {USERS, COMMENTS}

    public enum MemberStatus {HOST, MEMBER, GUEST}

    public MainHolder mMainHolder;
    public AppCompatActivity mActivity;
    public ViewType type;

    public EventDetailAdapter(AppCompatActivity a, String eventInfo, MemberStatus status) {
        mList = new ArrayList<>();
        type = ViewType.USERS;
        mList.add(eventInfo);
        mStatus = status;
        mActivity = a;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            mMainHolder = new MainHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_event_detail_main,
                            parent, false));

            return mMainHolder;
        }
        ViewHolder v = new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_entity, parent, false));

        return v;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        try {
            if (position == 0) {
                mMainHolder = ((MainHolder) holder);
                final Event e = new Event(new JSONObject(mList.get(0)));

                ((MainHolder) holder).mTime.setText(e.getTimeSpanString());
                ((MainHolder) holder).mLocation.setText(e.getmLocationTitle());
                ((MainHolder) holder).mDescription.setText(e.getmDescription());

                ((MainHolder) holder).mActions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mActionsClicked != null){
                            mActionsClicked.onClicked();
                        }

                    }
                });

                ((MainHolder) holder).mComments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCommentsClicked != null)
                            mCommentsClicked.onClicked();

                    }
                });

                ((MainHolder) holder).mAttend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mAttendanceClicked != null)
                            mAttendanceClicked.onClicked();
                    }
                });

                ((MainHolder) holder).mJoin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mOnJoinedClicked != null)
                            mOnJoinedClicked.onClicked(e.mID);
                    }
                });

            } else {
                ((ViewHolder) holder).mName.setText(mList.get(position));
                ((ViewHolder) holder).mLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mEntityClicked != null) {
                            mEntityClicked.onClicked(position - 1);
                        }
                    }
                });
            }

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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

    public class MainHolder extends RecyclerView.ViewHolder {
        public TextView mDescription;
        public TextView mAttendance;
        public TextView mLocation;
        public TextView mComments;
        public TextView mActions;
        public TextView mAttend;
        public TextView mJoin;
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
            mJoin = (TextView) itemView.findViewById(R.id.btn_join);

        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout mLayout;
        public TextView mName;

        public ViewHolder(View itemView) {
            super(itemView);
            mLayout = (RelativeLayout) itemView.findViewById(R.id.layout_info);
            mName = (TextView) itemView.findViewById(R.id.txt_entity_title);
        }
    }

    public MainHolder getmMainHolder() {
        return mMainHolder;
    }

    public void setOnAttendanceClicked(OnAttendanceClicked a) {
        mAttendanceClicked = a;
    }

    public void setOnCommentsClicked(OnCommentsClicked c) {
        mCommentsClicked = c;
    }

    public void setOnEntityClicked(OnEntityClicked e) {
        mEntityClicked = e;
    }

    public void setOnActionsClicked(OnActionsClicked a) {
        mActionsClicked = a;
    }

    public void setOnJoinedClicked(OnJoinClicked j) {
        mOnJoinedClicked = j;
    }

    public interface OnAttendanceClicked {
        void onClicked();
    }

    public interface OnCommentsClicked {
        void onClicked();
    }

    public interface OnEntityClicked {
        void onClicked(int position);
    }

    public interface OnActionsClicked {
        void onClicked();
    }

    public interface OnJoinClicked {
        void onClicked(int eventID);
    }
}
