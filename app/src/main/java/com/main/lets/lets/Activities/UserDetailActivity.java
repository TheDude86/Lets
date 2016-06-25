package com.main.lets.lets.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.main.lets.lets.R;
import com.main.lets.lets.Visulizers.ProfileDetailFeed;

import org.json.JSONException;
import org.json.JSONObject;

public class UserDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.row_profile);

        try {
            JSONObject j = new JSONObject(getIntent().getStringExtra("JSON"));
            ProfileDetailFeed feed = new ProfileDetailFeed(j.getInt("user_id"));

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
