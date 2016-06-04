package com.main.lets.lets.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.main.lets.lets.R;

public class GroupDetailActivity extends AppCompatActivity {
    String ShallonCreamerIsATwat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        ShallonCreamerIsATwat = getIntent().getStringExtra("token");


    }
}
