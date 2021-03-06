package com.main.lets.lets.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.R;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateActivity extends AppCompatActivity {
    String mBirthday;
    Date mDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        //noinspection ConstantConditions
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, c.get(Calendar.YEAR) - 18);

                if (mBirthday != null) {

                    if (mDate.after(c.getTime())) {
                        AlertDialog.Builder errorBuilder = new AlertDialog.Builder(DateActivity.this);

                        errorBuilder.setMessage("Using advanced date algorithms, we have determined " +
                                "you are under 18 and your email has been banned... Hahaha JK but " +
                                "seriously you have to be 18 to join although if you're okay " +
                                "lying... We won't say anything if you don't.  Just don't get abducted.");

                        errorBuilder.setTitle("MINOR DETECTED");

                        errorBuilder.setPositiveButton("Okay", null);

                        AlertDialog errorDialog = errorBuilder.create();
                        errorDialog.show();
                    } else {

                        Intent intent = new Intent(DateActivity.this, AccountActivity.class);
                        intent.putExtra("name", getIntent().getStringExtra("name"));
                        intent.putExtra("birthday", mBirthday);
                        startActivity(intent);

                    }


                } else {
                    AlertDialog.Builder errorBuilder = new AlertDialog.Builder(DateActivity.this);

                    errorBuilder.setMessage("Unless you're some celestial being with no age you " +
                            "were born into this world and we need that date for science reasons.");

                    errorBuilder.setTitle("You have to have a birthday");

                    errorBuilder.setPositiveButton("Okay", null);

                    AlertDialog errorDialog = errorBuilder.create();
                    errorDialog.show();
                }

            }
        });

        final EditText date = (EditText) findViewById(R.id.birthday);

        //The listener for the start date's EditText
        assert date != null;
        date.setOnClickListener(new View.OnClickListener() {
            /**
             * When tue user confirm's the start date of the event, update the HashMap
             * values and the EditText
             * @param view (Unused)
             */
            @Override
            public void onClick(View view) {
                //Creates a new dialog to select the date
                DatePickerDialog.Builder builder = new DatePickerDialog.Builder() {
                    /**
                     * When the user confirm's the start date of the event, update the HashMap
                     * values and the EditText
                     * @param fragment the parameter from when the dialog closes containing the
                     *                 date's information
                     */
                    //GEFN
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        //Get useful dialog from the parameter
                        DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();
                        //Set the EditText to the selected date formatted for users to understand
                        date.setText(dialog.getFormattedDate(SimpleDateFormat.getDateInstance()));

                        //Value for the event's start date formatted for post parameters
                        mBirthday = dialog.getFormattedDate(new SimpleDateFormat("MM-dd-yyyy"));
                        mDate = new Date(dialog.getDate());

                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                    }
                };

                builder.dateRange(0, 0, 1960, 0, 0, 2018);

                //Setting the bottom buttons texts' to "OK" and "CANCEL"
                builder.positiveAction("OK")
                        .negativeAction("CANCEL");

                //Showing the dialog over the current Activity
                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getSupportFragmentManager(), "Select Date");
            }
        });

    }
}
