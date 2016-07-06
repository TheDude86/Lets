package com.main.lets.lets.Adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.LetsAPI.Event;
import com.main.lets.lets.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Joe on 5/30/2016.
 */
public class EventDetailAdapter extends RecyclerView.Adapter {
    public OnAttendanceClicked mAttendanceClicked;
    public OnCommentsClicked mCommentsClicked;
    public ArrayList<String> mList;
    enum ViewType {USERS, COMMENTS}
    MainHolder mMainHolder;
    Activity mActivity;
    ViewType type;

    public EventDetailAdapter(Activity a, String eventInfo) {
        mList = new ArrayList<>();
        type = ViewType.USERS;
        mList.add(eventInfo);
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            if (position == 0) {
                mMainHolder = ((MainHolder) holder);
                Event e = new Event(new JSONObject(mList.get(0)));

                ((MainHolder) holder).mTime.setText(e.getTimeSpanString());
                ((MainHolder) holder).mLocation.setText(e.getmLocationTitle());
                ((MainHolder) holder).mDescription.setText(e.getmDescription());

                ((MainHolder) holder).mActions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                ((MainHolder) holder).mComments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mCommentsClicked != null)
                            mCommentsClicked.onClicked();

                    }
                });

                ((MainHolder) holder).mAttend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mAttendanceClicked != null)
                            mAttendanceClicked.onClicked();
                    }
                });

            } else {
                Entity e = new Entity(new JSONObject(mList.get(position)));

                ((ViewHolder) holder).mName.setText(e.mText);
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

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mName;

        public ViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.txt_entity_title);

        }
    }

    public MainHolder getmMainHolder() {
        return mMainHolder;
    }

    public void setOnAttandanceClicked(OnAttendanceClicked a){
        mAttendanceClicked = a;
    }

    public void setOnCommentsClicked(OnCommentsClicked c){
        mCommentsClicked = c;
    }

    public interface OnAttendanceClicked {
        void onClicked();
    }

    public interface OnCommentsClicked {
        void onClicked();
    }
}
