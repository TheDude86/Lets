package com.main.lets.lets.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.main.lets.lets.R;

@SuppressWarnings("ConstantConditions")
public class NewGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = ((EditText) findViewById(R.id.set_name)).getText().toString();

                if (s.equals("")) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(NewGroupActivity.this);
                    builder.setTitle("You can't have a group with no name");
                    builder.setMessage("Your group has to have a name!  " +
                            "What if you want your friend to join your nameless group?  They'll be like " +
                            "\"Oh that group sounds cool!  What's its name?\" " +
                            "And you'll have to be like \"Uh... It doesn't have one\"");

                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();

                } else {
                    Intent i = new Intent(NewGroupActivity.this, NewGroupPictureActivity.class);
                    i.putExtra("name", s);
                    startActivity(i);

                }



            }
        });


    }
}
