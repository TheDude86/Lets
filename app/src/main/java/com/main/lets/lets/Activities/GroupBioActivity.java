package com.main.lets.lets.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.main.lets.lets.R;

public class GroupBioActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_bio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final TextView bio = (TextView) findViewById(R.id.bio);
        Button button = (Button) findViewById(R.id.next);

        assert button != null;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bio.getText().toString().equals("")) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(GroupBioActivity.this);
                    builder.setTitle("Your group's bio is empty!");
                    builder.setMessage("Your group can't have an empty Bio!  For reasons that I " +
                            "(the programmer behind the app) don't feel like typing out right now. " +
                            "(I'm writing this at 3:00AM btw...)");

                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();

                } else {
                    Intent i = new Intent(GroupBioActivity.this, GroupPrivacyActivity.class);
                    i.putExtra("bio", bio.getText().toString());
                    i.putExtra("name", getIntent().getStringExtra("name"));
                    i.putExtra("image", getIntent().getParcelableExtra("image"));

                    startActivity(i);

                }



            }
        });


    }
}
