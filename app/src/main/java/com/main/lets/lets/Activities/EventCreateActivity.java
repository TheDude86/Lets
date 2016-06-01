package com.main.lets.lets.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.main.lets.lets.R;
import com.rey.material.widget.Slider;

public class EventCreateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);

        CircularProgressButton create = (CircularProgressButton) findViewById(R.id.create);
        final TextView durationLabel = (TextView) findViewById(R.id.duration_label);
        EditText description = (EditText) findViewById(R.id.description);
        final EditText location = (EditText) findViewById(R.id.location);
        final EditText category = (EditText) findViewById(R.id.category);
        Slider duration = (Slider) findViewById(R.id.duration);
        final EditText title = (EditText) findViewById(R.id.title);
        final EditText date = (EditText) findViewById(R.id.date);
        final EditText time = (EditText) findViewById(R.id.time);


        duration.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
            @Override
            public void onPositionChanged
                    (Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {
                durationLabel.setText(newValue > 1 ? newValue + " Hours" : newValue + " Hour");
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EventCreateActivity.this, SelectLocationActivity.class);
                startActivity(i);

            }
        });

        create.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (time.getText().toString().equals("Some Time o'clock"))
                    return;
                if (location.getText().toString().equals("Location")) {
                    return;
                }
                if (category.getText().toString().equals("Category")) {
                    return;
                }
                if (date.getText().toString().equals("Today")) {
                    return;
                }
                if (title.getText().toString().equals("")) {
                    return;
                }



            }


        });

    }
}
