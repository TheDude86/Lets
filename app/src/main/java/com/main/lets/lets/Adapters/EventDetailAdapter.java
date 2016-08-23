package com.main.lets.lets.Adapters;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.main.lets.lets.Holders.PictureViewHolder;
import com.main.lets.lets.LetsAPI.Event;
import com.main.lets.lets.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Joe on 5/30/2016.
 */
public class EventDetailAdapter extends FeedAdapter {
    public OnActionsClicked mActionsClicked;
    public OnJoinClicked mOnJoinedClicked;
    public MemberStatus mStatus;
    public int mID;

    public enum MemberStatus {HOST, MEMBER, GUEST}

    public MainHolder mMainHolder;

    public EventDetailAdapter(AppCompatActivity a, String eventInfo, MemberStatus status) {
        mActive = Active.USER;
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
        PictureViewHolder v = new PictureViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_entity_with_picture, parent, false));

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

                setComments(((MainHolder) holder).mComments);
                setUsers(((MainHolder) holder).mAttend);


                ((MainHolder) holder).mJoin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (mOnJoinedClicked != null)
                            mOnJoinedClicked.onClicked(e.mID);
                    }
                });

            } else {
                super.onBindViewHolder(holder, position);

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

    public void setOnActionsClicked(OnActionsClicked a) {
        mActionsClicked = a;
    }

    public void setOnJoinedClicked(OnJoinClicked j) {
        mOnJoinedClicked = j;
    }


    public interface OnActionsClicked {
        void onClicked();
    }

    public interface OnJoinClicked {
        void onClicked(int eventID);
    }
}
