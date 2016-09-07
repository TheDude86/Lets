package com.main.lets.lets.Visualizers;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Activities.InviteActivity;
import com.main.lets.lets.Activities.UserDetailActivity;
import com.main.lets.lets.Adapters.EventDetailAdapter;
import com.main.lets.lets.Adapters.FeedAdapter;
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
    ArrayList<JSONObject> mUsers;
    String ShallonCreamerIsATwat;
    AppCompatActivity mActivity;
    RecyclerView mRecyclerView;
    JSONArray mCoHosts;
    JSONObject mJSON;
    int mID;

    public EventDetailFeed(AppCompatActivity a, RecyclerView r) {

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(a.getBaseContext());

        ShallonCreamerIsATwat = preferences.getString("Token", "");
        mID = preferences.getInt("UserID", -1);

        mUsers = new ArrayList<>();
        mRecyclerView = r;
        mActivity = a;


    }

    @Override
    public void draw(final JSONObject j) {
        try {
            mJSON = j;

            mRecyclerView.setLayoutManager(
                    new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            mEventAdapter = new EventDetailAdapter(mActivity, j.toString(),
                    (new Event(j).getmOwnerID() == mID) ?
                            EventDetailAdapter.MemberStatus.HOST :
                            EventDetailAdapter.MemberStatus.GUEST);

            mEventAdapter.setOnActionsClicked(new EventDetailAdapter.OnActionsClicked() {
                @Override
                public void onClicked() {
                    try {
                        ActionDialogFragment f = new ActionDialogFragment(mActivity, new Event(j),
                                mEventAdapter.mStatus);

                        f.mOnJoin = new OnJoin() {
                            @Override
                            public void onJoin() {
                                try {
                                    if (ShallonCreamerIsATwat.equals("")) {


                                    } else {
                                        Calls.inviteUserToEvent(new Event(j).getmEventID(), mID, ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                mEventAdapter.mStatus = EventDetailAdapter.MemberStatus.MEMBER;
                                                draw(j);
                                            }
                                        });

                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        };

                        f.mOnLeave = new OnLeave() {
                            @Override
                            public void onLeave() {
                                mEventAdapter.mStatus = EventDetailAdapter.MemberStatus.GUEST;
                                draw(j);
                            }
                        };

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

                    try {
                        JSONArray json = response.getJSONArray("Attending_users");
                        mCoHosts = response.getJSONArray("Cohosts");
                        String s = "";

                        for (int i = 0; i < json.length(); i++) {

                            if (json.getJSONObject(i).getBoolean("status")) {
                                mEventAdapter.mUsers.add(json.getJSONObject(i).toString());
                                mEventAdapter.addElement(json.getJSONObject(i).toString());

                                if (new Entity(json.getJSONObject(i)).mID == mID) {
                                    mEventAdapter.getmMainHolder().mJoin.setText((new Event(j).getEnd()
                                            .before(Calendar.getInstance().getTime())) ?
                                            "You attended this event" :
                                            "You're attending!");

                                    if (mEventAdapter.mStatus != EventDetailAdapter.MemberStatus.HOST)
                                        mEventAdapter.mStatus = EventDetailAdapter.MemberStatus.MEMBER;
                                }

                                if (mEventAdapter.mStatus == EventDetailAdapter.MemberStatus.GUEST) {
                                    mEventAdapter.setOnJoinedClicked(new EventDetailAdapter.OnJoinClicked() {
                                        @Override
                                        public void onClicked(int eventID) {

                                            Calls.inviteUserToEvent(eventID, mID, ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
                                                @Override
                                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                                    mEventAdapter.mStatus = EventDetailAdapter.MemberStatus.MEMBER;
                                                    draw(j);
                                                }
                                            });

                                        }
                                    });
                                }

                                if (s.length() < 1)
                                    s = json.getJSONObject(i).getString("name");
                                else
                                    s += ", " + json.getJSONObject(i).getString("name");

                            }

                        }

                        mEventAdapter.getmMainHolder().mAttendance.setText(s);

                        json = response.getJSONArray("Comments");

                        for (int i = 0; i < json.length(); i++) {
                            mEventAdapter.mComments.add(json.getJSONObject(i).toString());

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                      org.json.JSONArray errorResponse) {
//                    Log.e("Async Test Failure", errorResponse.toString());
                }

            });


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("ValidFragment")
    public class ActionDialogFragment extends DialogFragment {
        CharSequence[] memberActions = {"Invite", "Comment", "Show on Map",
                "Leave Event"};
        CharSequence[] hostActions = {"Invite", "Comment", "Show on Map",
                "Delete Event", "Add Co-hosts", "Remove Co-hosts", "Edit Event"};

        CharSequence[] guestActions = {"Join Event"};

        EventDetailAdapter.MemberStatus mStatus;
        AppCompatActivity mActivity;
        OnLeave mOnLeave;
        OnJoin mOnJoin;
        Event mEvent;

        public ActionDialogFragment(AppCompatActivity a, Event e,
                                    EventDetailAdapter.MemberStatus s) {
            mActivity = a;
            mStatus = s;
            mEvent = e;
        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final CharSequence[] list;
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
                            runAction(mEvent, list[which].toString());

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

        public void runAction(final Event event, String s) {
            com.rey.material.app.Dialog.Builder builder = null;
            com.rey.material.app.DialogFragment fragment;

            final SharedPreferences preferences = PreferenceManager
                    .getDefaultSharedPreferences(mActivity.getBaseContext());

            switch (s) {
                case "Invite":
                    Intent intent = new Intent(mActivity, InviteActivity.class);
                    intent.putExtra("invite_id", event.getmEventID());
                    intent.putExtra("entities", "Friends:Groups");
                    intent.putExtra("id", mEvent.getmEventID());
                    intent.putExtra("mode", "UG2EFE");
                    mActivity.startActivity(intent);

                    break;
                case "Comment":
                    builder = new SimpleDialog.Builder() {

                        @Override
                        protected void onBuildDone(com.rey.material.app.Dialog dialog) {
                            dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                        }

                        @Override
                        public void onPositiveActionClicked(
                                com.rey.material.app.DialogFragment fragment) {
                            final EditText e = (EditText) fragment.getDialog()
                                    .findViewById(R.id.text);
                            final ProgressDialog dialog = ProgressDialog.show(mActivity, "",
                                    "Loading. Please " +
                                            "wait...",
                                    true);
                            Calls.addComment(event.getmEventID(), preferences.getString("Token", ""),
                                    e.getText().toString(),
                                    new JsonHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int statusCode,
                                                              Header[] headers,
                                                              org.json.JSONObject
                                                                      response) {
                                            String comment = "";
                                            try {
                                                for (String s : mEventAdapter.mUsers) {
                                                    Entity entity = new Entity(
                                                            new JSONObject(s));
                                                    if (mID == entity.mID)
                                                        comment = entity.mText + ":\n" + e
                                                                .getText().toString();
                                                }

                                                JSONObject j = new JSONObject();
                                                j.put("user_id", mID);
                                                j.put("event_id", event.getmEventID());
                                                j.put("text", e.getText().toString());
                                                j.put("timestamp",
                                                        "/Date(" + Calendar.getInstance()
                                                                .getTimeInMillis() + ")/");

                                                mEventAdapter.mComments.add(j.toString());
                                            } catch (JSONException e1) {
                                                e1.printStackTrace();
                                            }

                                            if (mEventAdapter.mActive == FeedAdapter.Active.COMMENT)
                                                mEventAdapter.addElement(comment);

                                            dialog.hide();

                                        }

                                        @Override
                                        public void onFailure(int statusCode,
                                                              Header[] headers,
                                                              Throwable throwable,
                                                              JSONObject errorResponse) {

                                        }

                                    });

                            super.onPositiveActionClicked(fragment);
                        }

                        @Override
                        public void onNegativeActionClicked(
                                com.rey.material.app.DialogFragment fragment) {
                            super.onNegativeActionClicked(fragment);
                        }
                    };

                    builder.title("Add Comment")
                            .positiveAction("Comment")
                            .negativeAction("Cancel")
                            .contentView(R.layout.dialog_comment);

                    break;
                case "Show on Map":
                    String uriBegin = "geo:" +
                            event.getmCords().get("latitude") + "," + event.getmCords()
                            .get("longitude");
                    String query = event.getmCords().get("latitude") + "," +
                            event.getmCords().get("longitude") + "(" + event.getmTitle() + ")";
                    String encodedQuery = Uri.encode(query);
                    String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                    Uri uri = Uri.parse(uriString);
                    Intent intent1 = new Intent(android.content.Intent.ACTION_VIEW, uri);
                    startActivity(intent1);

                    break;
                case "Add Co-hosts":
                    Intent inviteIntent = new Intent(mActivity, InviteActivity.class);
                    inviteIntent.putExtra("invite_id", event.getmEventID());
                    inviteIntent.putExtra("token", ShallonCreamerIsATwat);
                    inviteIntent.putExtra("entities", "Friends");
                    inviteIntent.putExtra("mode", "U2CFE");
                    inviteIntent.putExtra("id", mID);
                    mActivity.startActivity(inviteIntent);

                    break;
                case "Remove Co-hosts":
                    CharSequence[] list = new CharSequence[mCoHosts.length()];
                    for (int i = 0; i < mCoHosts.length(); i++) {
                        try {
                            list[i] = new Entity(mCoHosts.getJSONObject(i)).mText;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    builder = new SimpleDialog.Builder() {
                        @Override
                        public void onPositiveActionClicked(
                                com.rey.material.app.DialogFragment fragment) {
                            CharSequence[] values = getSelectedValues();
                            Entity e;

                            for (int i = 0; i < values.length; i++) {
                                for (int j = 0; j < mCoHosts.length(); j++) {
                                    try {
                                        Entity user = new Entity(mCoHosts.getJSONObject(j));
                                        if (values[i].equals(user.mText)) {


                                            Calls.removeCohost(user.mID, event.getmEventID(), ShallonCreamerIsATwat, new JsonHttpResponseHandler() {


                                                @Override
                                                public void onSuccess(int statusCode,
                                                                      Header[] headers,
                                                                      JSONObject response) {
                                                    draw(mJSON);
                                                }
                                            });

                                        }
                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }

                                }

//                                Calls.removeCohost();

                            }

                            super.onPositiveActionClicked(fragment);
                        }

                        @Override
                        public void onNegativeActionClicked(
                                com.rey.material.app.DialogFragment fragment) {
                            super.onNegativeActionClicked(fragment);
                        }
                    };

                    ((SimpleDialog.Builder) builder).multiChoiceItems(list)
                            .title("Remove Cohosts")
                            .positiveAction("Remove")
                            .negativeAction("Cancel");

                    break;
                case "Leave Event":
                    AlertDialog alertDialog = new AlertDialog.Builder(mActivity).create();

                    alertDialog.setTitle("Are you sure?");

                    alertDialog.setMessage("Are you sure you want to leave this event?");

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Unattend", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Calls.leaveEvent(mEvent.getmEventID(), ShallonCreamerIsATwat, new JsonHttpResponseHandler() {

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    if (mOnLeave != null)
                                        mOnLeave.onLeave();

                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    if (mOnLeave != null)
                                        mOnLeave.onLeave();

                                }
                            });

                        }

                    });

                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });

                    alertDialog.show();

                    break;
                case "Edit Event":
                    AlertDialog editDialog = new AlertDialog.Builder(mActivity).create();

                    editDialog.setTitle("Coming Soon!");

                    editDialog.setMessage("Woot!");

                    editDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Awesome", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }

                    });

                    editDialog.show();

                    break;
                case "Join Event":

                    if (mOnJoin != null) {
                        mOnJoin.onJoin();
                    }

                    break;

            }

            if (builder != null) {
                fragment = com.rey.material.app.DialogFragment.newInstance(builder);
                fragment.show(mActivity.getSupportFragmentManager(), null);
            }

        }

    }

    public interface OnLeave {
        void onLeave();
    }

    public interface OnJoin {
        void onJoin();
    }

}
