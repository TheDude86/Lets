package com.main.lets.lets.Visualizers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
    public ArrayList<JSONObject> mMemberTags;
    public ArrayList<JSONObject> mAdminTags;
    public ArrayList<JSONObject> mComments;
    public String ShallonCreamerIsATwat;
    public GroupDetailAdapter mAdapter;
    public AppCompatActivity mActivity;
    public ArrayList<String> mMembers;
    public RecyclerView mRecyclerView;
    public TextView mJoinButton;
    public int mID;

    public GroupDetailFeed(AppCompatActivity a, String token, int id) {
        mRecyclerView = (RecyclerView) a.findViewById(R.id.feed);
        mJoinButton = (TextView) a.findViewById(R.id.btn_join);
        mMemberTags = new ArrayList<>();
        mAdminTags = new ArrayList<>();
        ShallonCreamerIsATwat = token;
        mComments = new ArrayList<>();
        mMembers = new ArrayList<>();
        mActivity = a;
        mID = id;

        mJoinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public void draw(final JSONObject j) {
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,
                StaggeredGridLayoutManager.VERTICAL));

        try {
            mAdapter = new GroupDetailAdapter(mActivity,
                    j.getJSONArray("Group_info").getJSONObject(0).toString(), mID);

            for (int i = 0; i < j.getJSONArray("Group_users").length(); i++) {
                mAdapter.addElement(j.getJSONArray("Group_users").getJSONObject(i).toString());
                mMemberTags.add(j.getJSONArray("Group_users").getJSONObject(i));
                if (j.getJSONArray("Group_users").getJSONObject(i).getInt("user_id") == mID)
                    mAdapter.updateStatus(GroupDetailAdapter.Status.MEMBER);
            }

            loadUserDetails(0);

            for (int i = 0; i < j.getJSONArray("Group_admins").length(); i++) {
                mAdminTags.add(j.getJSONArray("Group_admins").getJSONObject(i));
                if (j.getJSONArray("Group_admins").getJSONObject(i).getInt("user_id") == mID)
                    mAdapter.updateStatus(GroupDetailAdapter.Status.ADMIN);
            }

            mAdapter.setOnCommentsClickListener(new GroupDetailAdapter.OnCommentsClickListener() {
                @Override
                public void onClick() {
                    mAdapter.clearFeed();
                    try {

                        for (JSONObject j : mComments) {
                            String s = null;
                            for (JSONObject m : mMemberTags) {
                                if (m.getInt("user_id") == j.getInt("user_id")) {
                                    s = m.getString("name") + ":\n";

                                    break;
                                }

                            }

                            if (s == null) {
                                for (JSONObject m : mAdminTags) {
                                    if (m.getInt("user_id") == j.getInt("user_id")) {
                                        s = m.getString("name") + ":\n";

                                        break;
                                    }

                                }
                            }

                            mAdapter.addElement(s + j.getString("text"));

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

            mAdapter.setOnMembersClickListener(new GroupDetailAdapter.OnMembersClickListener() {
                @Override
                public void onClick() {
                    mAdapter.clearFeed();

                    for (JSONObject j : mMemberTags)
                        mAdapter.addElement(j.toString());

                }
            });

            mAdapter.setOnEntityClickListener(new GroupDetailAdapter.OnEntityClickListener() {
                @Override
                public void onClick(int position) {
                    Intent intent = new Intent(mActivity, UserDetailActivity.class);
                    intent.putExtra("JSON", mMembers.get(position));
                    intent.putExtra("token", ShallonCreamerIsATwat);
                    mActivity.startActivity(intent);
                }
            });

            mAdapter.setOnDraw(new GroupDetailAdapter.OnDraw() {
                @Override
                public void draw(HashMap<String, TextView> actions) {
                    GroupActions groupActions = new GroupActions(GroupDetailFeed.this, j, mAdapter.mStatus);
                    groupActions.draw(actions);

                }
            });


            mRecyclerView.setAdapter(mAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void loadUserDetails(final int position) {
        if (position >= mMemberTags.size())
            return;

        try {
            Calls.getProfileByID(mMemberTags.get(position).getInt("user_id"), ShallonCreamerIsATwat,
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers,
                                              org.json.JSONArray response) {
                            try {
                                mMembers.add(response.getJSONObject(0).toString());
                                loadUserDetails(position + 1);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

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

}
