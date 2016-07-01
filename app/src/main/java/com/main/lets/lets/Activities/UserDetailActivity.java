package com.main.lets.lets.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.main.lets.lets.R;
import com.main.lets.lets.Visualizers.ProfileDetailFeed;

import org.json.JSONException;
import org.json.JSONObject;

public class UserDetailActivity extends AppCompatActivity {
    public String ShallonCreamerIsATwat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);

        try {
            ShallonCreamerIsATwat = getIntent().getStringExtra("token");
            JSONObject j = new JSONObject(getIntent().getStringExtra("JSON"));
            ProfileDetailFeed feed = new ProfileDetailFeed((RecyclerView) findViewById(R.id.feed),
                                                           j, ShallonCreamerIsATwat);
            feed.draw(j);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
