package com.main.lets.lets.Visualizers;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Activities.UserDetailActivity;
import com.main.lets.lets.Adapters.GroupDetailAdapter;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.R;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jnovosel on 7/2/16.
 */
public class GroupDetailFeed extends Client {
    public ArrayList<JSONObject> mMemberTags;
    public ArrayList<JSONObject> mAdminTags;
    public ArrayList<JSONObject> mComments;
    public String ShallonCreamerIsATwat;
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
    public void draw(JSONObject j) {
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,
                StaggeredGridLayoutManager.VERTICAL));

        try {
            final GroupDetailAdapter g = new GroupDetailAdapter(mActivity,
                    j.getJSONArray("Group_info").getJSONObject(0).toString(), mID);

            for (int i = 0; i < j.getJSONArray("Group_users").length(); i++) {
                g.addElement(j.getJSONArray("Group_users").getJSONObject(i).toString());
                mMemberTags.add(j.getJSONArray("Group_users").getJSONObject(i));
                if (j.getJSONArray("Group_users").getJSONObject(i).getInt("user_id") == mID)
                    g.updateStatus(GroupDetailAdapter.Status.MEMBER);
            }

            loadUserDetails(0);

            for (int i = 0; i < j.getJSONArray("Group_admins").length(); i++) {
                mAdminTags.add(j.getJSONArray("Group_admins").getJSONObject(i));
                if (j.getJSONArray("Group_admins").getJSONObject(i).getInt("user_id") == mID)
                    g.updateStatus(GroupDetailAdapter.Status.ADMIN);
            }

            g.setOnCommentsClickListener(new GroupDetailAdapter.OnCommentsClickListener() {
                @Override
                public void onClick() {
                    g.clearFeed();
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

                            g.addElement(s + j.getString("text"));

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

            g.setOnMembersClickListener(new GroupDetailAdapter.OnMembersClickListener() {
                @Override
                public void onClick() {
                    g.clearFeed();

                    for (JSONObject j : mMemberTags)
                        g.addElement(j.toString());

                }
            });

            g.setOnEntityClickListener(new GroupDetailAdapter.OnEntityClickListener() {
                @Override
                public void onClick(int position) {
                    Intent intent = new Intent(mActivity, UserDetailActivity.class);
                    intent.putExtra("JSON", mMembers.get(position));
                    intent.putExtra("token", ShallonCreamerIsATwat);
                    mActivity.startActivity(intent);
                }
            });

            switch (g.mStatus) {
                case MEMBER:
                    loadMemberActions(j, g);
            }

            loadMemberActions(j, g);

            mRecyclerView.setAdapter(g);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void loadMemberActions(final JSONObject j, GroupDetailAdapter g) {
        g.mActions.get("comment").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDialog.Builder builder = new SimpleDialog.Builder() {

                    @Override
                    protected void onBuildDone(Dialog dialog) {
                        dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    }

                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        EditText e = (EditText) fragment.getDialog().findViewById(R.id.text);
                        Log.println(Log.ASSERT, "GroupDetailFeed", "Test");

                        try {
                            Calls.addGroupComment(j.getJSONArray("Group_info").getJSONObject(0)
                                    .getInt("group_id"), e.getText().toString(),
                                    ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers,
                                                              org.json.JSONObject response) {
                                            Log.println(Log.ASSERT, "GroupDetailFeed", response.toString());

                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                                              JSONObject errorResponse) {

                                        }

                            });
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                    }
                };

                builder.title("Add Comment")
                        .positiveAction("COMMENT")
                        .negativeAction("CANCEL")
                        .contentView(R.layout.dialog_comment);

                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(mActivity.getSupportFragmentManager(), null);
            }
        });

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
