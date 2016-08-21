package com.main.lets.lets.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Adapters.EventDetailAdapter;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.R;
import com.main.lets.lets.Visualizers.UserDetailFeed;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class UserDetailActivity extends AppCompatActivity{
    public enum Relationship {NONE, SENT, RECIEVED, FRIEND, OWNER}

    public Relationship mRelationship = Relationship.NONE;
    public String ShallonCreamerIsATwat;
    public UserDetailFeed mFeed;
    public JSONObject mUserInfo;
    public int mUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        final RelativeLayout action = (RelativeLayout) findViewById(R.id.add_friend);
        final TextView actionText = (TextView) findViewById(R.id.add_text);

        try {
            ShallonCreamerIsATwat = getIntent().getStringExtra("token");
            mUserID = getIntent().getIntExtra("UserID", -1);

            if (getIntent().getStringExtra("JSON") != null) {

                JSONObject j = new JSONObject(getIntent().getStringExtra("JSON"));
                loadActivity(j);

            } else {
                Calls.getProfileByID(mUserID, ShallonCreamerIsATwat, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {

                            mUserInfo = (response.getJSONArray("info")
                                    .getJSONObject(0));

                            mUserInfo.put("interests", response.getJSONObject("interests"));

                            SharedPreferences sharedPref = PreferenceManager
                                    .getDefaultSharedPreferences(getBaseContext());
                            final int userID = sharedPref.getInt("UserID", -1);

                            Log.println(Log.ASSERT, " UserDetailActivity", String.valueOf(userID));

                            if (mUserInfo.getInt("User_ID") == userID) {
                                mRelationship = Relationship.OWNER;
                            }

                            loadAction(action, actionText);
                            loadActivity(mUserInfo);

                            if (mRelationship != Relationship.OWNER) {

                                Calls.getFriends(mUserID, new JsonHttpResponseHandler() {

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers,
                                                          JSONArray response) {

                                        for (int i = 0; i < response.length(); i++) {
                                            try {
                                                if (response.getJSONObject(i)
                                                        .getInt("user_id") == userID) {
                                                    if (response.getJSONObject(i)
                                                            .getInt("sender") == userID)
                                                        mRelationship = Relationship.SENT;
                                                    else
                                                        mRelationship = Relationship.RECIEVED;

                                                    if (response.getJSONObject(i)
                                                            .getBoolean("status")) {
                                                        mRelationship = Relationship.FRIEND;
                                                    }

                                                    loadAction(action, actionText);

                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                        }


                                    }
                                });

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void loadAction(final RelativeLayout r, final TextView t) {

        Log.println(Log.ASSERT, "UserDetailActivity", mRelationship.toString());

        switch (mRelationship) {
            case NONE:
                t.setText("Add Friend");
                r.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calls.sendFriendRequest(mUserID, ShallonCreamerIsATwat, new JsonHttpResponseHandler(){

                            @Override
                            public void onSuccess(int statusCode, Header[] headers,
                                                  JSONObject response) {

                                Log.println(Log.ASSERT, "UserDetailActivity", response.toString());


                                mRelationship = Relationship.SENT;
                                loadAction(r, t);

                            }
                        });
                    }
                });

                break;
            case SENT:
                t.setText("Friend request sent");

                r.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        AlertDialog alertDialog = new AlertDialog.Builder(UserDetailActivity.this).create();

                        alertDialog.setTitle("Revoke Request");

                        alertDialog.setMessage("Revoke friend request?");

                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Revoke", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                Calls.removeFriend(mUserID, ShallonCreamerIsATwat, new JsonHttpResponseHandler() {

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers,
                                                          JSONObject response) {

                                        Log.println(Log.ASSERT, "UserDetailActivity", response.toString());

                                        mRelationship = Relationship.NONE;
                                        loadAction(r, t);

                                    }
                                });

                            } });

                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                            }});


                        alertDialog.show();

                    }
                });

                break;
            case RECIEVED:
                t.setText("Respond to friend request");

                r.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        r.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                AlertDialog alertDialog = new AlertDialog.Builder(UserDetailActivity.this).create();

                                alertDialog.setTitle("Respond");

                                alertDialog.setMessage("Respond to friend request");

                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Reject", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int id) {

                                        Calls.removeFriend(mUserID, ShallonCreamerIsATwat, new JsonHttpResponseHandler() {

                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers,
                                                                  JSONObject response) {

                                                mRelationship = Relationship.NONE;
                                                loadAction(r, t);

                                            }
                                        });

                                    } });

                                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Accept", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int id) {

                                        Calls.sendFriendRequest(mUserID, ShallonCreamerIsATwat, new JsonHttpResponseHandler(){

                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers,
                                                                  JSONObject response) {

                                                Log.println(Log.ASSERT, "UserDetailActivity", response.toString());

                                                try {
                                                    mRelationship = Relationship.FRIEND;
                                                    loadActivity(mUserInfo);
                                                    loadAction(r, t);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                            }

                                        });

                                    }});

                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int id) {

                                    }});

                                alertDialog.show();


                            }
                        });

                    }
                });

                break;
            default:
                r.setVisibility(View.GONE);
                break;
        }

    }

    public void loadActivity(JSONObject j) throws JSONException {
        mFeed = new UserDetailFeed(this, (RecyclerView) findViewById(R.id.feed), j,
                                   ShallonCreamerIsATwat, mRelationship);

        mFeed.draw(j);
    }


}
