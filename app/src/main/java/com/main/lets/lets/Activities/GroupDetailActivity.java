package com.main.lets.lets.Activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.florent37.materialviewpager.MaterialViewPager;
import com.github.florent37.materialviewpager.header.HeaderDesign;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.R;
import com.main.lets.lets.Visualizers.GroupDetailFeed;
import com.rey.material.app.Dialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class GroupDetailActivity extends AppCompatActivity {
    public String ShallonCreamerIsATwat;
    public int mID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        ((TextView) findViewById(R.id.btn_join)).setText("Join Group");

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        ShallonCreamerIsATwat = preferences.getString("Token", "");
        mID = preferences.getInt("UserID", -1);

        try {

            if (getIntent().getStringExtra("JSON") != null) {
                loadActivity(new JSONObject(getIntent().getStringExtra("JSON")));

            } else {
                final ProgressDialog dialog = ProgressDialog.show(this, "",
                                                                  "Loading. Please wait...", true);
                Calls.getGroupInfo(getIntent().getIntExtra("GroupID", -1), new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            loadActivity(response);
                            dialog.hide();

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

    public void loadActivity(final JSONObject j) throws JSONException {
        for (int i = 0; i < j.getJSONArray("Group_users").length(); i++) {
            if (j.getJSONArray("Group_users").getJSONObject(i).getInt("user_id") == mID
                    && j.getJSONArray("Group_users").getJSONObject(i).getBoolean("status")) {
                findViewById(R.id.layout_join).setVisibility(View.GONE);
            }
        }

        Calls.getGroupComments(j.getJSONArray("Group_info").getJSONObject(0).getInt("group_id"),
                               ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers,
                                          org.json.JSONArray response) {

                        GroupDetailFeed g = new GroupDetailFeed(GroupDetailActivity.this, j);

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                g.mAdapter.mComments.add(response.getJSONObject(i).toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        g.draw(null);

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                          JSONObject errorResponse) {

                    }

                });
    }

}
