package com.main.lets.lets.Visualizers;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Adapters.SearchAdapter;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jnovosel on 7/10/16.
 */
public class InviteFeed extends Client {
    SearchAdapter.OnEntityClickListener mEventClickHandler;
    SearchAdapter.OnEntityClickListener mGroupClickHandler;
    SearchAdapter.OnEntityClickListener mUserClickHandler;
    ArrayList<Integer> mSelectedEvents;
    ArrayList<Integer> mSelectedGroups;
    ArrayList<Integer> mSelectedUsers;

    public enum Mode {GROUP, EVENT}

    String ShallonCreamerIsATwat;
    AppCompatActivity mActivity;
    RecyclerView mRecyclerView;
    ArrayList<String> mEvents;
    ArrayList<String> mGroups;
    ArrayList<String> mUsers;
    SearchAdapter mAdapter;
    int mInviteID;
    Mode mActive;
    int mID;

    public InviteFeed(AppCompatActivity a, Mode m, String token, int id, final int inviteID) {
        ShallonCreamerIsATwat = token;
        mInviteID = inviteID;
        mActivity = a;
        mActive = m;
        mID = id;

        mSelectedEvents = new ArrayList<>();
        mSelectedGroups = new ArrayList<>();
        mSelectedUsers = new ArrayList<>();
        mEvents = new ArrayList<>();
        mGroups = new ArrayList<>();
        mUsers = new ArrayList<>();

        mRecyclerView = (RecyclerView) mActivity.findViewById(R.id.feed);


        mActivity.findViewById(R.id.invite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActive == Mode.GROUP) {
                    for (final Integer i : mSelectedUsers) {
                        Calls.joinGroup(i, inviteID, ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                Log.println(Log.ASSERT, "InviteFeed", i + " " + response.toString());
                            }
                        });
                    }

                    mActivity.finish();

                } else {
                    for (final Integer i : mSelectedGroups) {
                        Calls.inviteGroupToEvent(inviteID, i, ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                Log.println(Log.ASSERT, "InviteFeed", i + " " + response.toString());

                            }
                        });

                    }

                    for (final Integer i : mSelectedUsers) {
                        Calls.inviteUserToEvent(inviteID, i, ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                                Log.println(Log.ASSERT, "InviteFeed", i + " " + response.toString());

                            }
                        });
                    }

                    mActivity.finish();

                }

            }
        });

    }

    @Override
    public void draw(JSONObject j) {
        try {
            SearchAdapter.OnEntityClickListener activeHandler = null;
            final JSONArray list = j.getJSONArray("json");
            ArrayList<Integer> selected = null;
            SearchFeed.Viewing active = null;

            mEventClickHandler = new SearchAdapter.OnEntityClickListener() {
                @Override
                public void onClicked(int position) {
                    try {
                        mSelectedEvents.add(new Entity(list.getJSONObject(position)).mID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            mUserClickHandler = new SearchAdapter.OnEntityClickListener() {
                @Override
                public void onClicked(int position) {
                    try {
                        mSelectedUsers.add(new Entity(list.getJSONObject(position)).mID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            };

            mGroupClickHandler = new SearchAdapter.OnEntityClickListener() {
                @Override
                public void onClicked(int position) {
                    try {
                        mSelectedGroups.add(new Entity(list.getJSONObject(position)).mID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            switch (j.getInt("type")) {
                case 0:
                    activeHandler = mUserClickHandler;
                    active = SearchFeed.Viewing.USER;
                    selected = mSelectedUsers;

                    break;
                case 1:
                    activeHandler = mEventClickHandler;
                    active = SearchFeed.Viewing.EVENT;
                    selected = mSelectedEvents;

                    break;
                case 2:
                    activeHandler = mGroupClickHandler;
                    active = SearchFeed.Viewing.GROUP;
                    selected = mSelectedGroups;

                    break;

            }

            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            mAdapter = new SearchAdapter(mActivity, j.getJSONArray("json"), active);
            mAdapter.setOnEntityClicked(activeHandler);
            mAdapter.setSelected(selected);

            mRecyclerView.setAdapter(mAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
