package com.main.lets.lets.Actions;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Activities.InviteActivity;
import com.main.lets.lets.Adapters.GroupDetailAdapter;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.R;
import com.main.lets.lets.Visualizers.GroupDetailFeed;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jnovosel on 7/4/16.
 */
public class GroupActions implements View.OnClickListener {
    GroupDetailAdapter.Status mStatus;
    GroupDetailFeed mFeed;
    JSONObject mJSON;

    public GroupActions(GroupDetailFeed g, JSONObject j, GroupDetailAdapter.Status s) {
        mStatus = s;
        mFeed = g;
        mJSON = j;

    }

    public void draw(HashMap<String, TextView> actions) {
        for (Object o : actions.keySet().toArray())
            actions.get(o).setOnClickListener(this);

    }

    @Override
    public void onClick(final View v) {
        try {
            Dialog.Builder builder = null;
            DialogFragment fragment;
            switch (((TextView) v).getText().toString()) {
                case "Comment":

                    builder = new SimpleDialog.Builder() {

                        @Override
                        protected void onBuildDone(Dialog dialog) {
                            dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        }

                        @Override
                        public void onPositiveActionClicked(DialogFragment fragment) {
                            mFeed.mComments = new ArrayList<>();
                            final ProgressDialog dialog = ProgressDialog.show(mFeed.mActivity, "",
                                                                              "Loading. Please " +
                                                                                      "wait...",
                                                                              true);
                            EditText e = (EditText) fragment.getDialog().findViewById(R.id.text);
                            try {
                                Calls.addGroupComment(
                                        mJSON.getJSONArray("Group_info").getJSONObject(0)
                                                .getInt("group_id"), e.getText().toString(),
                                        mFeed.ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers,
                                                                  org.json.JSONObject response) {
                                                try {
                                                    Calls.getGroupComments(
                                                            mJSON.getJSONArray("Group_info")
                                                                    .getJSONObject(0)
                                                                    .getInt("group_id"),
                                                            mFeed.ShallonCreamerIsATwat,
                                                            new JsonHttpResponseHandler() {
                                                                @Override
                                                                public void onSuccess(
                                                                        int statusCode,
                                                                        Header[] headers,
                                                                        org.json.JSONArray
                                                                                response) {
                                                                    for (int i = 0; i < response
                                                                            .length(); i++) {
                                                                        try {
                                                                            mFeed.mComments
                                                                                    .add(response.getJSONObject(
                                                                                            i));
                                                                            dialog.hide();
                                                                        } catch (JSONException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
//                                                                draw(mJSON);

                                                                }

                                                                @Override
                                                                public void onFailure(
                                                                        int statusCode,
                                                                        Header[] headers,
                                                                        Throwable throwable,
                                                                        JSONObject errorResponse) {

                                                                }

                                                            });
                                                } catch (JSONException e1) {
                                                    e1.printStackTrace();
                                                }

                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers,
                                                                  Throwable throwable,
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
                            .positiveAction("Comment")
                            .negativeAction("Cancel")
                            .contentView(R.layout.dialog_comment);

                    fragment = DialogFragment.newInstance(builder);
                    fragment.show(mFeed.mActivity.getSupportFragmentManager(), null);

                    break;

                case "Add Admins":
                    ArrayList<String> members = new ArrayList<>();

                    for (JSONObject j : mFeed.mMemberTags) {
                        for (JSONObject j2 : mFeed.mAdminTags) {
                            if (j.getInt("user_id") != j2.getInt("user_id"))
                                members.add(j.getString("name"));
                        }
                    }

                    String[] s = new String[members.size()];

                    for (int i = 0; i < s.length; i++) {
                        s[i] = members.get(i);
                    }

                    builder = new SimpleDialog.Builder() {
                        @Override
                        public void onPositiveActionClicked(DialogFragment fragment) {
                            CharSequence[] values = getSelectedValues();
                            Entity e;
                            for (CharSequence c : values) {
                                for (JSONObject j : mFeed.mMemberTags) {
                                    e = new Entity(j);
                                    if (e.mText.equals(c)) {
                                        try {
                                            Calls.addAdmin(e.mID, mJSON.getJSONArray("Group_info")
                                                                   .getJSONObject(0)
                                                                   .getInt("group_id"),
                                                           mFeed.ShallonCreamerIsATwat,
                                                           new JsonHttpResponseHandler() {
                                                               @Override
                                                               public void onSuccess(int statusCode,
                                                                                     Header[]
                                                                                             headers,
                                                                                     JSONObject
                                                                                             response) {


                                                               }

                                                               @Override
                                                               public void onFailure(int statusCode,
                                                                                     Header[]
                                                                                             headers,
                                                                                     Throwable
                                                                                             throwable,
                                                                                     JSONObject
                                                                                             errorResponse) {

                                                               }
                                                           });
                                        } catch (JSONException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                }
                            }

                            super.onPositiveActionClicked(fragment);
                        }

                        @Override
                        public void onNegativeActionClicked(DialogFragment fragment) {
                            super.onNegativeActionClicked(fragment);
                        }
                    };

                    ((SimpleDialog.Builder) builder).multiChoiceItems(s)
                            .title("Add Admins")
                            .positiveAction("Add")
                            .negativeAction("Cancel");

                    fragment = DialogFragment.newInstance(builder);
                    fragment.show(mFeed.mActivity.getSupportFragmentManager(), null);

                    break;
                case "Remove Members":
                    String[] membersList = new String[mFeed.mMemberTags.size()];
                    for (int i = 0; i < membersList.length; i++)
                        membersList[i] = mFeed.mMemberTags.get(i).getString("name");

                    builder = new SimpleDialog.Builder() {
                        @Override
                        public void onPositiveActionClicked(DialogFragment fragment) {
                            super.onPositiveActionClicked(fragment);
                            CharSequence[] values = getSelectedValues();

                            for (CharSequence s : values) {
                                for (int i = 0; i < mFeed.mMemberTags.size(); i++) {
                                    Entity e = new Entity(mFeed.mMemberTags.get(i));
                                    if (s.toString().equals(e.mText)) {
                                        try {
                                            Calls.removeUserFromGroup(e.mID, mJSON.getJSONArray(
                                                    "Group_info").getJSONObject(0)
                                                                              .getInt("group_id"),
                                                                      mFeed.ShallonCreamerIsATwat,
                                                                      new JsonHttpResponseHandler
                                                                              () {
                                                                          @Override
                                                                          public void onSuccess(
                                                                                  int statusCode,
                                                                                  Header[] headers,
                                                                                  org.json.JSONObject response) {


                                                                          }

                                                                          @Override
                                                                          public void onFailure(
                                                                                  int statusCode,
                                                                                  Header[] headers,
                                                                                  Throwable
                                                                                          throwable,
                                                                                  JSONObject
                                                                                          errorResponse) {

                                                                          }
                                                                      });
                                        } catch (JSONException e1) {
                                            e1.printStackTrace();
                                        }

                                    }

                                }
                            }

                        }

                        @Override
                        public void onNegativeActionClicked(DialogFragment fragment) {
                            super.onNegativeActionClicked(fragment);
                        }
                    };

                    ((SimpleDialog.Builder) builder).multiChoiceItems(membersList)
                            .title("Remove Members")
                            .positiveAction("Remove")
                            .negativeAction("Cancel");

                    fragment = DialogFragment.newInstance(builder);
                    fragment.show(mFeed.mActivity.getSupportFragmentManager(), null);

                    break;
                case "Remove Admins":
                    String[] adminsList = new String[mFeed.mAdminTags.size()];
                    for (int i = 0; i < adminsList.length; i++)
                        adminsList[i] = mFeed.mMemberTags.get(i).getString("name");

                    builder = new SimpleDialog.Builder() {
                        @Override
                        public void onPositiveActionClicked(DialogFragment fragment) {
                            super.onPositiveActionClicked(fragment);
                        }

                        @Override
                        public void onNegativeActionClicked(DialogFragment fragment) {
                            super.onNegativeActionClicked(fragment);
                        }
                    };

                    ((SimpleDialog.Builder) builder).multiChoiceItems(adminsList)
                            .title("Remove Admins")
                            .positiveAction("Remove")
                            .negativeAction("Cancel");

                    fragment = DialogFragment.newInstance(builder);
                    fragment.show(mFeed.mActivity.getSupportFragmentManager(), null);

                    break;
                case "Transfer Ownership":
                    String[] newOwnerList = new String[mFeed.mAdminTags.size()];
                    for (int i = 0; i < newOwnerList.length; i++)
                        newOwnerList[i] = mFeed.mMemberTags.get(i).getString("name");

                    builder = new SimpleDialog.Builder() {
                        @Override
                        public void onPositiveActionClicked(DialogFragment fragment) {
                            super.onPositiveActionClicked(fragment);

                            for (JSONObject j : mFeed.mAdminTags) {
                                Entity e = new Entity(j);

                                if (e.mText.equals(getSelectedValue())) {
                                    try {
                                        Calls.transferOwner(
                                                mJSON.getJSONArray("Group_info").getJSONObject(0)
                                                        .getInt("group_id"), e.mID,
                                                mFeed.ShallonCreamerIsATwat,
                                                new JsonHttpResponseHandler() {
                                                    @Override
                                                    public void onSuccess(int statusCode,
                                                                          Header[] headers,
                                                                          JSONObject response) {
                                                        mFeed.mAdapter.mStatus =
                                                                GroupDetailAdapter.Status.ADMIN;
                                                        mStatus = GroupDetailAdapter.Status.ADMIN;

                                                    }

                                                    @Override
                                                    public void onFailure(int statusCode,
                                                                          Header[] headers,
                                                                          Throwable throwable,
                                                                          JSONObject
                                                                                  errorResponse) {

                                                    }
                                                });
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }

                                }

                            }

                        }

                        @Override
                        public void onNegativeActionClicked(DialogFragment fragment) {
                            super.onNegativeActionClicked(fragment);
                        }
                    };

                    ((SimpleDialog.Builder) builder).items(newOwnerList, -1)
                            .title("Remove Members")
                            .positiveAction("Transfer")
                            .negativeAction("Cancel");

                    fragment = DialogFragment.newInstance(builder);
                    fragment.show(mFeed.mActivity.getSupportFragmentManager(), null);

                    break;
                case "Leave Group":
                    builder = new SimpleDialog.Builder() {
                        @Override
                        public void onPositiveActionClicked(DialogFragment fragment) {
                            super.onPositiveActionClicked(fragment);
                            if (mStatus != GroupDetailAdapter.Status.OWNER) {
                                try {
                                    Calls.leaveGroup(
                                            mJSON.getJSONArray("Group_info").getJSONObject(0)
                                                    .getInt("group_id"),
                                            mFeed.ShallonCreamerIsATwat,
                                            new JsonHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode,
                                                                      Header[] headers,
                                                                      org.json.JSONObject
                                                                              response) {


                                                }

                                                @Override
                                                public void onFailure(int statusCode,
                                                                      Header[] headers,
                                                                      Throwable throwable,
                                                                      JSONObject errorResponse) {

                                                }
                                            });
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                mFeed.mActivity.finish();
                            }

                        }

                        @Override
                        public void onNegativeActionClicked(DialogFragment fragment) {
                            super.onNegativeActionClicked(fragment);
                        }
                    };

                    if (mStatus != GroupDetailAdapter.Status.OWNER) {
                        ((SimpleDialog.Builder) builder).message("Are you sure?")
                                .title("Leave Group")
                                .positiveAction("Leave")
                                .negativeAction("Cancel");

                    } else {
                        ((SimpleDialog.Builder) builder)
                                .message("You can either delete the group " +
                                                 "or transfer ownership then leave")
                                .title("I'm sorry Dave, I'm afraid I can't let you do that...")
                                .positiveAction("Okay")
                                .negativeAction("Cancel");
                    }

                    fragment = DialogFragment.newInstance(builder);
                    fragment.show(mFeed.mActivity.getSupportFragmentManager(), null);

                    break;

                case "Edit Group":
                    mFeed.editGroup();


                    break;
                case "Delete Group":
                    builder = new SimpleDialog.Builder() {
                        @Override
                        public void onPositiveActionClicked(DialogFragment fragment) {
                            super.onPositiveActionClicked(fragment);
                            try {
                                final ProgressDialog dialog = ProgressDialog
                                        .show(mFeed.mActivity, "",
                                              "Loading. Please wait...", true);

                                Calls.deleteGroup(mJSON.getJSONArray("Group_info").getJSONObject(0)
                                                          .getInt("group_id"),
                                                  mFeed.ShallonCreamerIsATwat,
                                                  new JsonHttpResponseHandler() {
                                                      @Override
                                                      public void onSuccess(int statusCode,
                                                                            Header[] headers,
                                                                            org.json.JSONObject response) {
                                                          dialog.hide();
                                                          mFeed.mActivity.finish();

                                                      }

                                                      @Override
                                                      public void onFailure(int statusCode,
                                                                            Header[] headers,
                                                                            Throwable throwable,
                                                                            JSONObject errorResponse) {

                                                      }
                                                  });
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mFeed.mActivity.finish();


                        }

                        @Override
                        public void onNegativeActionClicked(DialogFragment fragment) {
                            super.onNegativeActionClicked(fragment);
                        }
                    };

                    ((SimpleDialog.Builder) builder)
                            .message("Are you sure?  This cannot be undone!")
                            .title("Delete Group")
                            .positiveAction("Delete")
                            .negativeAction("Cancel");

                    fragment = DialogFragment.newInstance(builder);
                    fragment.show(mFeed.mActivity.getSupportFragmentManager(), null);

                    break;
                case "Invite Friends":
                    Intent intent = new Intent(mFeed.mActivity, InviteActivity.class);
                    intent.putExtra("invite_id", mJSON.getJSONArray("Group_info")
                            .getJSONObject(0).getInt("group_id"));
                    intent.putExtra("token", mFeed.ShallonCreamerIsATwat);
                    intent.putExtra("entities", "Friends");
                    intent.putExtra("mode", "U2GFG");
                    intent.putExtra("id", mFeed.mID);
                    mFeed.mActivity.startActivity(intent);

                    break;
                case "Invite to Event":
                    Intent eventIntent = new Intent(mFeed.mActivity, InviteActivity.class);
                    eventIntent.putExtra("invite_id", mJSON.getJSONArray("Group_info")
                            .getJSONObject(0).getInt("group_id"));
                    eventIntent.putExtra("token", mFeed.ShallonCreamerIsATwat);
                    eventIntent.putExtra("entities", "Events");
                    eventIntent.putExtra("mode", "G2EFG");
                    eventIntent.putExtra("id", mFeed.mID);
                    mFeed.mActivity.startActivity(eventIntent);

                    break;

            }




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
