package com.main.lets.lets.Visualizers;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Activities.UserDetailActivity;
import com.main.lets.lets.Adapters.EventDetailAdapter;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.LetsAPI.Event;
import com.main.lets.lets.R;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Joe on 5/30/2016.
 */
public class EventDetailFeed extends Client {
    EventDetailAdapter mEventAdapter;
    String ShallonCreamerIsATwat;
    AppCompatActivity mActivity;
    ArrayList<String> mComments;
    RecyclerView mRecyclerView;
    ArrayList<String> mUsers;
    int mID;

    public EventDetailFeed(AppCompatActivity a, RecyclerView r, String token, int id) {
        ShallonCreamerIsATwat = token;
        mComments = new ArrayList<>();
        mUsers = new ArrayList<>();
        mRecyclerView = r;
        mActivity = a;
        mID = id;

    }

    @Override
    public void draw(final JSONObject j) {
        try {
            mRecyclerView.setLayoutManager(
                    new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            mEventAdapter = new EventDetailAdapter(mActivity, j.toString(),
                    (new Event(j).getmOwnerID() == mID) ? EventDetailAdapter.MemberStatus.HOST :
                            EventDetailAdapter.MemberStatus.GUEST);

            mEventAdapter.setOnAttendanceClicked(new EventDetailAdapter.OnAttendanceClicked() {
                @Override
                public void onClicked() {
                    mEventAdapter.type = EventDetailAdapter.ViewType.USERS;
                    mEventAdapter.clearFeed();

                    try {
                        for (String s : mUsers)
                            mEventAdapter.addElement(new Entity(new JSONObject(s)).mText);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            mEventAdapter.setOnCommentsClicked(new EventDetailAdapter.OnCommentsClicked() {
                @Override
                public void onClicked() {
                    mEventAdapter.type = EventDetailAdapter.ViewType.COMMENTS;
                    mEventAdapter.clearFeed();

                    try {
                        for (String s : mComments) {
                            Entity comment = new Entity(new JSONObject(s));

                            for (String u : mUsers) {
                                Entity user = new Entity(new JSONObject(u));
                                if (comment.mID == user.mID) {
                                    mEventAdapter.addElement(user.mText + ":\n" + comment.mText);

                                }

                            }

                        }

                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }


                }
            });

            mEventAdapter.setOnEntityClicked(new EventDetailAdapter.OnEntityClicked() {
                @Override
                public void onClicked(int position) {
                    if (mEventAdapter.type == EventDetailAdapter.ViewType.USERS) {
                        Intent intent = new Intent(mActivity, UserDetailActivity.class);
                        intent.putExtra("token", ShallonCreamerIsATwat);
                        intent.putExtra("JSON", mUsers.get(position));
                        mActivity.startActivity(intent);
                    }
                }
            });

            mEventAdapter.setOnActionsClicked(new EventDetailAdapter.OnActionsClicked() {
                @Override
                public void onClicked() {
                    try {
                        ActionDialogFragment f = new ActionDialogFragment(mActivity, new Event(j), mEventAdapter.mStatus);
                        f.show(mActivity.getFragmentManager(), "Test");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            mRecyclerView.setAdapter(mEventAdapter);

            Calls.getEvent(j.getInt("Event_ID"), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers,
                                      org.json.JSONObject response) {
                    Log.e("Event", response.toString());

                    try {
                        JSONArray json = response.getJSONArray("Attending_users");
                        String s = "";

                        for (int i = 0; i < json.length(); i++) {
                            mUsers.add(json.getJSONObject(i).toString());
                            mEventAdapter.addElement(new Entity(json.getJSONObject(i)).mText);

                            if (new Entity(json.getJSONObject(i)).mID == mID) {
                                mEventAdapter.getmMainHolder().mJoin.setText((new Event(j).getEnd()
                                        .before(Calendar.getInstance().getTime())) ?
                                        "You attended this event" : "You're attending!");

                                if (mEventAdapter.mStatus != EventDetailAdapter.MemberStatus.HOST)
                                    mEventAdapter.mStatus = EventDetailAdapter.MemberStatus.MEMBER;
                            }

                            if (s.length() < 1)
                                s = json.getJSONObject(i).getString("name");
                            else
                                s += ", " + json.getJSONObject(i).getString("name");


                        }

                        mEventAdapter.getmMainHolder().mAttendance.setText(s);

                        json = response.getJSONArray("Comments");

                        for (int i = 0; i < json.length(); i++) {
                            mComments.add(json.getJSONObject(i).toString());

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                      org.json.JSONArray errorResponse) {
                    Log.e("Async Test Failure", errorResponse.toString());
                }

            });


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public class ActionDialogFragment extends DialogFragment {
        CharSequence[] memberActions = {"Invite User", "Invite Group", "Comment", "Show on Map",
                "Leave Event"};
        CharSequence[] hostActions = {"Invite User", "Invite Group", "Comment", "Show on Map",
                "Delete Event", "Add Co-hosts", "Remove Co-hosts", "Edit Event"};

        CharSequence[] guestActions = {"Join Event"};

        EventDetailAdapter.MemberStatus mStatus;
        AppCompatActivity mActivity;
        Event mEvent;

        public ActionDialogFragment(AppCompatActivity a, Event e, EventDetailAdapter.MemberStatus s) {
            mActivity = a;
            mStatus = s;
            mEvent = e;
        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            CharSequence[] list;
            switch (mStatus) {
                case HOST:
                    list = hostActions;

                    break;
                case MEMBER:
                    list = memberActions;

                    break;
                case GUEST:
                    list = guestActions;

                    break;

                default:
                    list = guestActions;

            }

            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle("Select Action")
                    .setItems(list, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            runAction(mEvent, which);

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }

        public void runAction(final Event event, int i) {
            com.rey.material.app.Dialog.Builder builder = null;
            com.rey.material.app.DialogFragment fragment;

            switch (i) {
                case 0:

                    break;
                case 1:

                    break;

                case 2:
                    builder = new SimpleDialog.Builder() {

                        @Override
                        protected void onBuildDone(com.rey.material.app.Dialog dialog) {
                            dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        }

                        @Override
                        public void onPositiveActionClicked(com.rey.material.app.DialogFragment fragment) {
                            final EditText e = (EditText) fragment.getDialog().findViewById(R.id.text);
                            final ProgressDialog dialog = ProgressDialog.show(mActivity, "",
                                    "Loading. Please wait...", true);
                            Calls.addComment(event.getmEventID(), ShallonCreamerIsATwat, e.getText().toString(),
                                    new JsonHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers,
                                                              org.json.JSONObject response) {
                                            String comment = "";
                                            try {
                                                for (String s : mUsers) {
                                                    Entity entity = new Entity(new JSONObject(s));
                                                    if (mID == entity.mID)
                                                        comment = entity.mText + ":\n" + e.getText().toString();
                                                }

                                                JSONObject j = new JSONObject();
                                                j.put("user_id", mID);
                                                j.put("event_id", event.getmEventID());
                                                j.put("text", e.getText().toString());
                                                j.put("timestamp", "/Date(" + Calendar.getInstance().getTimeInMillis() + ")/");
                                                mComments.add(j.toString());
                                            } catch (JSONException e1) {
                                                e1.printStackTrace();
                                            }

                                            if (mEventAdapter.type == EventDetailAdapter.ViewType.COMMENTS)
                                                mEventAdapter.addElement(comment);

                                            dialog.hide();

                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                                              JSONObject errorResponse) {

                                        }

                                    });

                            super.onPositiveActionClicked(fragment);
                        }

                        @Override
                        public void onNegativeActionClicked(com.rey.material.app.DialogFragment fragment) {
                            super.onNegativeActionClicked(fragment);
                        }
                    };

                    builder.title("Add Comment")
                            .positiveAction("Comment")
                            .negativeAction("Cancel")
                            .contentView(R.layout.dialog_comment);

                    break;
            }

            if (builder != null) {
                fragment = com.rey.material.app.DialogFragment.newInstance(builder);
                fragment.show(mActivity.getSupportFragmentManager(), null);
            }

        }

    }

}
