package com.main.lets.lets.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.main.lets.lets.R;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.DialogFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateActivity extends AppCompatActivity {
    String mBirthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        //noinspection ConstantConditions
        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBirthday != null) {
                    Intent intent = new Intent(DateActivity.this, AccountActivity.class);
                    intent.putExtra("name", getIntent().getStringExtra("name"));
                    intent.putExtra("birthday", mBirthday);
                    startActivity(intent);

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
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        //Get useful dialog from the parameter
                        DatePickerDialog dialog = (DatePickerDialog) fragment.getDialog();
                        //Set the EditText to the selected date formatted for users to understand
                        date.setText(dialog.getFormattedDate(SimpleDateFormat.getDateInstance()));

                        //Value for the event's start date formatted for post parameters
                        mBirthday = dialog.getFormattedDate(new SimpleDateFormat("MM-dd-yyyy"));

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
