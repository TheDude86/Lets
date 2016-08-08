package com.main.lets.lets.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.R;
import com.main.lets.lets.Visualizers.UserDetailFeed;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class UserDetailActivity extends AppCompatActivity {
    public String ShallonCreamerIsATwat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        try {
            ShallonCreamerIsATwat = getIntent().getStringExtra("token");

            if (getIntent().getStringExtra("JSON") != null) {
                JSONObject j = new JSONObject(getIntent().getStringExtra("JSON"));
                loadActivity(j);

            } else {
                Calls.getProfileByID(getIntent().getIntExtra("UserID", -1), ShallonCreamerIsATwat, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            loadActivity(response.getJSONArray("info").getJSONObject(0));
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

    public void loadActivity(JSONObject j) throws JSONException {
        UserDetailFeed feed = new UserDetailFeed(this,
                                                 (RecyclerView) findViewById(R.id.feed), j, ShallonCreamerIsATwat,
                                                 getIntent().getIntExtra("id", -1));

        feed.draw(j);
    }

}
