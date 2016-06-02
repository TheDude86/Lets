package com.main.lets.lets.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.main.lets.lets.R;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.TimePickerDialog;
import com.rey.material.widget.Slider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class EventCreateActivity extends AppCompatActivity {
    private HashMap<String, String> mMap;

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
        mMap = new HashMap<>();


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

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.Builder builder = new TimePickerDialog.Builder(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 1, 00){
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        TimePickerDialog dialog = (TimePickerDialog)fragment.getDialog();
                        time.setText(dialog.getFormattedTime(new SimpleDateFormat("h:mm a")));


                        if(mMap.containsKey("start"))
                            mMap.remove("start");

                        mMap.put("start", dialog.getFormattedTime(new SimpleDateFormat("HH:mm:ss")));

                        Toast.makeText(EventCreateActivity.this, "Start time set to " +
                                dialog.getFormattedTime(SimpleDateFormat.getTimeInstance()), Toast.LENGTH_SHORT).show();
                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        Toast.makeText(EventCreateActivity.this, "Cancelled" , Toast.LENGTH_SHORT).show();
                        super.onNegativeActionClicked(fragment);
                    }
                };

                builder.positiveAction("OK")
                        .negativeAction("CANCEL");

                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getSupportFragmentManager(), "Select Time");
            }
        });

        date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DatePickerDialog.Builder builder = new DatePickerDialog.Builder(){
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        DatePickerDialog dialog = (DatePickerDialog)fragment.getDialog();
                        String d = dialog.getFormattedDate(SimpleDateFormat.getDateInstance());
                        date.setText(d);

                        if(mMap.containsKey("date"))
                            mMap.remove("date");

                        mMap.put("date", dialog.getFormattedDate(new SimpleDateFormat("mm-dd-yyyy")));

                        Toast.makeText(EventCreateActivity.this, "Date is " + d, Toast.LENGTH_SHORT).show();
                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        Toast.makeText(EventCreateActivity.this, "Cancelled" , Toast.LENGTH_SHORT).show();
                        super.onNegativeActionClicked(fragment);
                    }
                };

                builder.positiveAction("OK")
                        .negativeAction("CANCEL");

                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getSupportFragmentManager(), "Select Date");
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
