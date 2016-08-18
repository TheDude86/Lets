package com.main.lets.lets.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Adapters.SearchAdapter;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Entity;
import com.main.lets.lets.R;
import com.main.lets.lets.Visualizers.InviteFeed;
import com.main.lets.lets.Visualizers.SearchFeed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class InviteActivity extends AppCompatActivity implements View.OnClickListener {
    String ShallonCreamerIsATwat;
    InviteFeed mInviteFeed;
    JSONObject mEvents;
    JSONObject mGroups;
    JSONObject mUsers;
    String mMode;
    int mID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);


        String[] buttons = getIntent().getStringExtra("entities").split(":");

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        ShallonCreamerIsATwat = preferences.getString("Token", "");
        mID = preferences.getInt("UserID", -1);

        Log.println(Log.ASSERT, "InviteActivity", "ID: " + mID + " Token: " + ShallonCreamerIsATwat);

        loadFeed(buttons);

        mMode = getIntent().getStringExtra("mode");

        switch (mMode) {
            case "G2EFG":
                mInviteFeed = new InviteFeed(this, InviteFeed.Mode.G2EFG, ShallonCreamerIsATwat, mID,
                        getIntent().getIntExtra("invite_id", -1));

                break;
            case "U2GFG":
                mInviteFeed = new InviteFeed(this, InviteFeed.Mode.U2GFG, ShallonCreamerIsATwat, mID,
                        getIntent().getIntExtra("invite_id", -1));

                break;
            case "UG2EFE":
                mInviteFeed = new InviteFeed(this, InviteFeed.Mode.UG2EFE, ShallonCreamerIsATwat, mID,
                        getIntent().getIntExtra("invite_id", -1));

                break;
            case "U2CFE":
                mInviteFeed = new InviteFeed(this, InviteFeed.Mode.U2CFE, ShallonCreamerIsATwat, mID,
                                             getIntent().getIntExtra("invite_id", -1));

                break;
        }

        TextView right = (TextView) findViewById(R.id.entity_right);
        TextView left = (TextView) findViewById(R.id.entity_left);

        right.setOnClickListener(this);
        left.setOnClickListener(this);

        left.setText(buttons[0]);
        if (buttons.length > 1)
            right.setText(buttons[1]);
        else
            findViewById(R.id.toolbar_entities).setVisibility(View.GONE);


    }

    @Override
    public void onClick(View v) {
        switch (((TextView) v).getText().toString()) {
            case "Friends":
                mInviteFeed.draw(mUsers);

                break;
            case "Events":
                mInviteFeed.draw(mEvents);

                break;
            case "Groups":
                mInviteFeed.draw(mGroups);

                break;

        }

    }

    public void loadFeed(final String[] feeds) {
        for (String s : feeds) {
            switch (s) {
                case "Friends":

                    Calls.getFriends(mID, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers,
                                              JSONArray response) {

                            try {
                                Log.println(Log.ASSERT, "InviteActivity", "Members: " + response.toString());

                                for (int i = 0; i < response.length(); i++){
                                    if (response.getJSONObject(i) != null) {
                                        if (!response.getJSONObject(i).getBoolean("status")){
                                            response.remove(i);
                                            i--;
                                        }
                                    }

                                }

                                JSONObject j = new JSONObject();

                                j.put("json", response);
                                j.put("type", 0);
                                mUsers = j;


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            mInviteFeed.draw(mUsers);

                        }

                    });

                    break;
                case "Events":
                    Calls.getAttended(mID, ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            try {
                                JSONObject j = new JSONObject();
                                j.put("json", response);
                                j.put("type", 1);
                                mEvents = j;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (!inArray(feeds, "Friends"))
                                mInviteFeed.draw(mEvents);

                        }
                    });

                    break;
                case "Groups":
                    Calls.getAdminGroups(mID, ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            try {
                                JSONObject j = new JSONObject();
                                j.put("json", response);
                                j.put("type", 2);
                                mGroups = j;
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (!inArray(feeds, "Events"))
                                mInviteFeed.draw(mGroups);

                        }
                    });

                    break;
            }
        }

    }

    public boolean inArray(Object[] list, Object object) {
        for (Object o : list) {
            if (o.equals(object))
                return true;
        }

        return false;
    }

}