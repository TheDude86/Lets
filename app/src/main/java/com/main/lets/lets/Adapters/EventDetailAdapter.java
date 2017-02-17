package com.main.lets.lets.Adapters;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Activities.EventDetailActivity;
import com.main.lets.lets.Activities.InviteActivity;
import com.main.lets.lets.Dialogs.EventActions;
import com.main.lets.lets.Holders.PictureViewHolder;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.LetsAPI.Event;
import com.main.lets.lets.LetsAPI.EventEntity;
import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.LetsAPI.UserData;
import com.main.lets.lets.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Joe on 5/30/2016.
 */
public class EventDetailAdapter extends FeedAdapter implements View.OnClickListener {
    public OnActionsClicked mActionsClicked;
    public OnJoinClicked mOnJoinedClicked;
    public Event.MemberStatus mStatus;
    public int mID;

    public MainHolder mMainHolder;

    public EventDetailAdapter(AppCompatActivity a, String eventInfo, Event.MemberStatus status) {
        mActive = Active.USER;
        mList.add(eventInfo);
        mStatus = status;
        mActivity = a;

    }

    public EventDetailAdapter(AppCompatActivity a, Event e) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(a.getBaseContext());

        mStatus = e.getUserStatus(preferences.getInt("UserID", -1));
        mList.add(e.getmEventInfo());
        mActive = Active.COMMENT;
        mActivity = a;
        mEvent = e;

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
        if (position == 0) {
            mMainHolder = ((MainHolder) holder);
            loadMainViewHolder();

        } else {
            super.onBindViewHolder(holder, position);

        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mList.size() + mEvent.mComments.size();
    }

    @Override
    public void onClick(View v) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mActivity.getBaseContext());

        switch (v.getId()) {
            case R.id.action1:
                if (mStatus == Event.MemberStatus.GUEST) {

                    Calls.inviteUserToEvent(mEvent.getmEventID(), preferences.getInt("UserID", -1),
                            new UserData(mActivity), new JsonHttpResponseHandler() {

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    mStatus = Event.MemberStatus.MEMBER;
                                    loadMainViewHolder();

                                }
                            });
                }

                break;
            case R.id.action2:
                if (mStatus == Event.MemberStatus.INVITE) {
                    Calls.inviteUserToEvent(mEvent.getmEventID(), preferences.getInt("UserID", -1),
                            new UserData(mActivity), new JsonHttpResponseHandler() {

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    mStatus = Event.MemberStatus.MEMBER;
                                    loadMainViewHolder();

                                }
                            });
                } else if (mStatus == Event.MemberStatus.MEMBER ||
                        mStatus == Event.MemberStatus.HOST ||
                        mStatus == Event.MemberStatus.OWNER) {

                    Intent intent = new Intent(mActivity, InviteActivity.class);
                    intent.putExtra("invite_id", mEvent.getmEventID());
                    intent.putExtra("entities", "Friends:Groups");
                    intent.putExtra("mode", "UG2EFE");
                    mActivity.startActivity(intent);

                }

                break;
            case R.id.action3:
                if (mStatus == Event.MemberStatus.INVITE) {
                    Calls.leaveEvent(mEvent.getmEventID(), preferences.getString("Token", ""), new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            mStatus = Event.MemberStatus.GUEST;
                            loadMainViewHolder();

                        }
                    });
                } else if (mStatus == Event.MemberStatus.MEMBER ||
                        mStatus == Event.MemberStatus.HOST ||
                        mStatus == Event.MemberStatus.OWNER) {

                    String uriBegin = "geo:" +
                            mEvent.getmCords().get("latitude") + "," + mEvent.getmCords()
                            .get("longitude");
                    String query = mEvent.getmCords().get("latitude") + "," +
                            mEvent.getmCords().get("longitude") + "(" + mEvent.getmTitle() + ")";
                    String encodedQuery = Uri.encode(query);
                    String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                    Uri uri = Uri.parse(uriString);
                    Intent intent1 = new Intent(android.content.Intent.ACTION_VIEW, uri);
                    mActivity.startActivity(intent1);

                }

                break;
            case R.id.action4:
                if (mStatus == Event.MemberStatus.MEMBER ||
                        mStatus == Event.MemberStatus.HOST ||
                        mStatus == Event.MemberStatus.OWNER) {

                    EventActions e = new EventActions(mActivity, mEvent, new EventActions.ScreenUpdate() {
                        @Override
                        public void onScreenUpdate(String action) {

                            if (action.equalsIgnoreCase("Leave"))
                                mStatus = Event.MemberStatus.GUEST;
                            else if (action.equalsIgnoreCase("Join"))
                                mStatus = Event.MemberStatus.MEMBER;

                            loadMainViewHolder();

                        }
                    });

                    e.show(mActivity.getFragmentManager(), "PENIS");

                }

                break;
        }

    }

    public void loadMainViewHolder() {
        MainHolder h = mMainHolder;
        h.resetToolbar();

        h.mTime.setText(mEvent.getTimeSpanString());
        h.mLocation.setText(mEvent.getmLocationTitle());
        h.mDescription.setText(mEvent.getmDescription());
        h.mMonth.setText(mEvent.getMonth());
        h.mDay.setText(mEvent.getDay());

        switch (mStatus) {
            case UNKNOWN:

                break;
            case GUEST:
                h.mAction1.setText("Interested");
                h.mImage1.setImageResource(R.drawable.ic_add_black_24dp);

                h.mButton2.setVisibility(View.GONE);
                h.mButton3.setVisibility(View.GONE);
                h.mButton4.setVisibility(View.GONE);

                break;
            case INVITE:
                h.mAction2.setText("Accept");
                h.mImage2.setImageResource(R.drawable.ic_done_black_24dp);

                h.mAction3.setText("Reject");
                h.mImage3.setImageResource(R.drawable.ic_clear_black_24dp);

                break;
            case MEMBER:
                h.mAction1.setText("You're\nInterested!");
                h.mImage1.setImageResource(R.drawable.ic_done_black_24dp);

                h.mAction2.setText("Invite\nFriends");
                h.mImage2.setImageResource(R.drawable.ic_mail_outline_black_24dp);

                if (!mEvent.hasLocation())
                    h.mButton3.setVisibility(View.GONE);

                h.mAction3.setText("Get\nDirections");
                h.mImage3.setImageResource(R.drawable.ic_place_black_24dp1);

                h.mAction4.setText("More\nActions");
                h.mImage4.setImageResource(R.drawable.ic_more_vert_black_24dp);


                break;
            default:
                h.mAction1.setText("You're\nHosting!");
                h.mImage1.setImageResource(R.drawable.ic_done_black_24dp);

                h.mAction2.setText("Invite\nFriends");
                h.mImage2.setImageResource(R.drawable.ic_mail_outline_black_24dp);

                if (!mEvent.hasLocation())
                    h.mButton3.setVisibility(View.GONE);

                h.mAction3.setText("Get\nDirections");
                h.mImage3.setImageResource(R.drawable.ic_place_black_24dp1);

                h.mAction4.setText("More\nActions");
                h.mImage4.setImageResource(R.drawable.ic_more_vert_black_24dp);

                break;

        }

        h.mButton1.setOnClickListener(this);
        h.mButton2.setOnClickListener(this);
        h.mButton3.setOnClickListener(this);
        h.mButton4.setOnClickListener(this);

        int length = (mEvent.mMembers.size() > 5) ? 5 : mEvent.mMembers.size();
        ImageView[] members = {h.mPerson1, h.mPerson2, h.mPerson3, h.mPerson4, h.mPerson5};

        for (int i = 0; i < length; i++) {
            mEvent.mMembers.get(i).loadImage(mActivity, members[i]);
        }

        h.mAttendanceList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAttendance();

            }
        });
    }


    public void showAttendance() {


        final ArrayList<Entity> entityFeed = mEvent.mMembers;

        SearchEntityAdapter adapter = new SearchEntityAdapter(entityFeed, mActivity);

        View view = View.inflate(mActivity, R.layout.dialog_search_entity, null);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.view);
        SearchView searchView = (SearchView) view.findViewById(R.id.search);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Entity> newFeed = new ArrayList<>();

                for (Entity e : entityFeed) {
                    if (e.mText.toLowerCase().contains(newText.toLowerCase()))
                        newFeed.add(e);
                }

                SearchEntityAdapter adapter = new SearchEntityAdapter(newFeed, mActivity);
                recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 1));
                recyclerView.setAdapter(adapter);

                return false;
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(mActivity, 1));
        recyclerView.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("People Interested");
        builder.setView(view);
        builder.setNegativeButton("Cancel", null);
        builder.create().show();


    }

    public void addHashtag(String s) {
        String text = mMainHolder.mHashtags.getText().toString();
        mMainHolder.mHashtags.setText(text + " #" + s);
    }

    public class MainHolder extends RecyclerView.ViewHolder {
        public LinearLayout mAttendanceList;
        public RelativeLayout mButton1;
        public RelativeLayout mButton2;
        public RelativeLayout mButton3;
        public RelativeLayout mButton4;
        public LinearLayout mActionBar;
        public TextView mDescription;
        public TextView mAttendance;
        public TextView mLocation;
        public TextView mHashtags;
        public TextView mAction1;
        public TextView mAction2;
        public TextView mAction3;
        public TextView mAction4;
        public ImageView mImage1;
        public ImageView mImage2;
        public ImageView mImage3;
        public ImageView mImage4;
        public ImageView mPerson1;
        public ImageView mPerson2;
        public ImageView mPerson3;
        public ImageView mPerson4;
        public ImageView mPerson5;
        public TextView mMonth;
        public TextView mDay;

        public TextView mTime;

        public MainHolder(View itemView) {
            super(itemView);

            mAttendanceList = (LinearLayout) itemView.findViewById(R.id.attendance);
            mDescription = (TextView) itemView.findViewById(R.id.event_description);
            mActionBar = (LinearLayout) itemView.findViewById(R.id.actionBar);
            mLocation = (TextView) itemView.findViewById(R.id.event_location);
            mButton1 = (RelativeLayout) itemView.findViewById(R.id.action1);
            mButton2 = (RelativeLayout) itemView.findViewById(R.id.action2);
            mButton3 = (RelativeLayout) itemView.findViewById(R.id.action3);
            mButton4 = (RelativeLayout) itemView.findViewById(R.id.action4);
            mHashtags = (TextView) itemView.findViewById(R.id.hashtags);
            mPerson1 = (ImageView) itemView.findViewById(R.id.person1);
            mPerson2 = (ImageView) itemView.findViewById(R.id.person2);
            mPerson3 = (ImageView) itemView.findViewById(R.id.person3);
            mPerson4 = (ImageView) itemView.findViewById(R.id.person4);
            mPerson5 = (ImageView) itemView.findViewById(R.id.person5);
            mTime = (TextView) itemView.findViewById(R.id.event_time);
            mImage1 = (ImageView) itemView.findViewById(R.id.image1);
            mImage2 = (ImageView) itemView.findViewById(R.id.image2);
            mImage3 = (ImageView) itemView.findViewById(R.id.image3);
            mImage4 = (ImageView) itemView.findViewById(R.id.image4);
            mAction1 = (TextView) itemView.findViewById(R.id.text1);
            mAction2 = (TextView) itemView.findViewById(R.id.text2);
            mAction3 = (TextView) itemView.findViewById(R.id.text3);
            mAction4 = (TextView) itemView.findViewById(R.id.text4);
            mMonth = (TextView) itemView.findViewById(R.id.month);
            mDay = (TextView) itemView.findViewById(R.id.day);

        }

        public void resetToolbar() {
            mButton1.setVisibility(View.VISIBLE);
            mButton2.setVisibility(View.VISIBLE);
            mButton3.setVisibility(View.VISIBLE);
            mButton4.setVisibility(View.VISIBLE);

            mImage1.setImageBitmap(null);
            mImage2.setImageBitmap(null);
            mImage3.setImageBitmap(null);
            mImage4.setImageBitmap(null);

            mAction1.setText("");
            mAction2.setText("");
            mAction3.setText("");
            mAction4.setText("");


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
