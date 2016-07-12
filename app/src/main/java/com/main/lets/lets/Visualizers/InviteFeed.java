package com.main.lets.lets.Visualizers;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Adapters.SearchAdapter;
import com.main.lets.lets.Holders.SearchViewHolder;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.LetsAPI.Login;
import com.main.lets.lets.R;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;

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

    /**
     * Yes this enum has shit names, I KNOW, but they're acronyms and this was the most concise
     * way to label them, and the acronyms are as follows:
     * <p/>
     * - UG2EFE: "User/Group to Event from Event" (Invite users and groups to an event from the event
     * detail activity)
     * <p/>
     * - G2EFG: "Group to Event from Group" (Invite group to an event from the group detail activity)
     * <p/>
     * - U2GFG: "User to Group from Group" (Invite users to a group from the group detail activity)
     */
    public enum Mode {
        UG2EFE, G2EFG, U2GFG, U2CFE
    }

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
                if (mActive == Mode.U2GFG) {
                    for (final Integer i : mSelectedUsers) {
                        Calls.joinGroup(i, inviteID, ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
                            }
                        });
                    }

                    mActivity.finish();

                } else if (mActive == Mode.UG2EFE) {

                    for (final Integer i : mSelectedGroups) {
                        Log.println(Log.ASSERT, "InviteFeed", " event ID: " + inviteID + "Group ID: " + i);
                        Calls.inviteGroupToEvent(inviteID, i, ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);

                            }
                        });

                    }

                    for (final Integer i : mSelectedUsers) {
                        Log.println(Log.ASSERT, "InviteFeed", " event ID: " + inviteID + "User ID: " + i);
                        Calls.inviteUserToEvent(inviteID, i, ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                super.onSuccess(statusCode, headers, response);

                            }
                        });
                    }

                    mActivity.finish();

                } else if (mActive == Mode.U2CFE){

                    for (final Integer i : mSelectedUsers) {
                        Calls.addCohost(mInviteID, i, ShallonCreamerIsATwat, new JsonHttpResponseHandler(){

                            @Override
                            public void onSuccess(int statusCode, Header[] headers,
                                                  JSONObject response) {
                                super.onSuccess(statusCode, headers, response);
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
                public void onClicked(final int position, SearchViewHolder holder) {
                    holder.mLayout.setBackgroundColor(Color.WHITE);
                    Dialog.Builder builder = new SimpleDialog.Builder() {
                        @Override
                        public void onPositiveActionClicked(DialogFragment fragment) {
                            super.onPositiveActionClicked(fragment);
                            try {
                                Calls.inviteGroupToEvent(new Entity(list.getJSONObject(position)).mID,
                                        mInviteID, ShallonCreamerIsATwat, new JsonHttpResponseHandler(){
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        Log.println(Log.ASSERT, "InviteFeed", response.toString());
                                        mActivity.finish();
                                    }
                                });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onNegativeActionClicked(DialogFragment fragment) {
                            super.onNegativeActionClicked(fragment);
                        }
                    };

                    try {
                        ((SimpleDialog.Builder) builder)
                                .message("Invite group to \"" +
                                        new Entity(list.getJSONObject(position)).mText + "\"?")
                                .title("Invite Group")
                                .positiveAction("Invite")
                                .negativeAction("Cancel");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    DialogFragment fragment = DialogFragment.newInstance(builder);
                    fragment.show(mActivity.getSupportFragmentManager(), null);

                }

            };

            mUserClickHandler = new SearchAdapter.OnEntityClickListener() {
                @Override
                public void onClicked(int position, SearchViewHolder holder) {
                    try {
                        mSelectedUsers.add(new Entity(list.getJSONObject(position)).mID);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            };

            mGroupClickHandler = new SearchAdapter.OnEntityClickListener() {
                @Override
                public void onClicked(int position, SearchViewHolder holder) {
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
            mAdapter.mSelectable = true;

            mRecyclerView.setAdapter(mAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
