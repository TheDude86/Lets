package com.main.lets.lets.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.main.lets.lets.R;

@SuppressWarnings("ConstantConditions")
public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.login).setOnClickListener(this);
        findViewById(R.id.create).setOnClickListener(this);
        findViewById(R.id.guest).setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        int action = 1;

        if (v.getId() == R.id.create)
            action = 2;
        else if (v.getId() == R.id.guest)
            action = 0;

        Intent i = new Intent();
        i.putExtra("action", action);
        setResult(Activity.RESULT_OK , i);
        finish();
    }
}
