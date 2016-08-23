package com.main.lets.lets.Visualizers;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Actions.GroupActions;
import com.main.lets.lets.Activities.UserDetailActivity;
import com.main.lets.lets.Adapters.GroupDetailAdapter;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jnovosel on 7/2/16.
 */
public class GroupDetailFeed extends Client {
    public ArrayList<JSONObject> mAdminTags;
    public String ShallonCreamerIsATwat;
    public GroupDetailAdapter mAdapter;
    public AppCompatActivity mActivity;
    public RecyclerView mRecyclerView;
    public TextView mJoinButton;
    public JSONObject mJSON;
    public int mID;

    public GroupDetailFeed(AppCompatActivity a, JSONObject j) {

        mRecyclerView = (RecyclerView) a.findViewById(R.id.feed);
        mJoinButton = (TextView) a.findViewById(R.id.btn_join);
        mAdminTags = new ArrayList<>();
        mActivity = a;
        mJSON = j;

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(mActivity.getBaseContext());

        ShallonCreamerIsATwat = preferences.getString("Token", "");
        mID = preferences.getInt("UserID", -1);

        try {
            mAdapter = new GroupDetailAdapter(mActivity,
                                              mJSON.getJSONArray("Group_info").getJSONObject(0).toString(), mID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void draw(final JSONObject j) {
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,
                StaggeredGridLayoutManager.VERTICAL));

        try {

            for (int i = 0; i < mJSON.getJSONArray("Group_users").length(); i++) {

                if (mJSON.getJSONArray("Group_users").getJSONObject(i).getBoolean("status")){
                    mAdapter.addElement(mJSON.getJSONArray("Group_users").getJSONObject(i).toString());
                    mAdapter.mUsers.add(mJSON.getJSONArray("Group_users").getJSONObject(i).toString());

                }

                if (mJSON.getJSONArray("Group_users").getJSONObject(i).getInt("user_id") == mID){
                    if (mJSON.getJSONArray("Group_users").getJSONObject(i).getBoolean("status"))
                        mAdapter.updateStatus(GroupDetailAdapter.Status.MEMBER);
                    else
                        mAdapter.updateStatus(GroupDetailAdapter.Status.INVITE);
                }
            }


            for (int i = 0; i < mJSON.getJSONArray("Group_admins").length(); i++) {
                mAdminTags.add(mJSON.getJSONArray("Group_admins").getJSONObject(i));
                if (mJSON.getJSONArray("Group_admins").getJSONObject(i).getInt("user_id") == mID)
                    mAdapter.updateStatus(GroupDetailAdapter.Status.ADMIN);
            }


            mAdapter.setOnDraw(new GroupDetailAdapter.OnDraw() {
                @Override
                public void draw(HashMap<String, TextView> actions) {
                    GroupActions groupActions = new GroupActions(GroupDetailFeed.this, mJSON, mAdapter.mStatus);
                    groupActions.draw(actions);

                }
            });

            if (mAdapter.mStatus == GroupDetailAdapter.Status.INVITE){
                mJoinButton.setText("Respond to Invite");
                mJoinButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();

                        alertDialog.setTitle("Respond to Invite");

                        alertDialog.setMessage("Would you like to join this group?");

                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Reject", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    Calls.respondToGroupInvite(mJSON.getJSONArray("Group_info")
                                            .getJSONObject(0).getInt("group_id"), false, ShallonCreamerIsATwat, new JsonHttpResponseHandler(){
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                            mAdapter.mStatus = GroupDetailAdapter.Status.GUEST;
                                            mJoinButton.setText("Join Group");
                                            draw(mJSON);
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } });

                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Join", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    Calls.respondToGroupInvite(mJSON.getJSONArray("Group_info")
                                            .getJSONObject(0).getInt("group_id"), true, ShallonCreamerIsATwat, new JsonHttpResponseHandler(){
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                            mAdapter.mStatus = GroupDetailAdapter.Status.MEMBER;
                                            mJoinButton.setVisibility(View.GONE);
                                            draw(mJSON);
                                        }
                                    });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }});

                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                            }});

                        alertDialog.show();


                    }
                });

            } else {
                mJoinButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Calls.joinGroup(mID, mJSON.getJSONArray("Group_info")
                                            .getJSONObject(0).getInt("group_id"), ShallonCreamerIsATwat,
                                    new JsonHttpResponseHandler(){
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers,
                                                              org.json.JSONObject response) {
                                            mActivity.findViewById(R.id.layout_join)
                                                    .setVisibility(View.GONE);

                                            mAdapter.mStatus = GroupDetailAdapter.Status.MEMBER;

                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                                              JSONObject errorResponse) {

                                        }

                                    });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }


            mRecyclerView.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void editGroup() {
        if (mAdapter.toggleEditable()) {
            mActivity.findViewById(R.id.layout_join).setVisibility(View.VISIBLE);
            mJoinButton.setText("Save Changes");
        } else
            mActivity.findViewById(R.id.layout_join).setVisibility(View.GONE);

    }

}
