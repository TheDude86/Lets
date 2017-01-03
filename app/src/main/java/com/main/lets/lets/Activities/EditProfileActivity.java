package com.main.lets.lets.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.LetsAPI.User;
import com.main.lets.lets.R;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class EditProfileActivity extends AppCompatActivity {
    User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText birthday = (EditText) findViewById(R.id.birthday);

        final RadioButton male = (RadioButton) findViewById(R.id.btn_male);
        final RadioButton female = (RadioButton) findViewById(R.id.btn_female);
        final RadioButton freak = (RadioButton) findViewById(R.id.btn_freak);

        Button save = (Button) findViewById(R.id.save);

        final ProgressDialog loading = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);

        mUser = new User(getIntent().getIntExtra("ID", -1));
        mUser.load(this, new User.OnLoadListener() {
            @Override
            public void update() {

                Date d = new Date(mUser.getBirthday().getTime());
                assert birthday != null;

                SimpleDateFormat s = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                s.setTimeZone(TimeZone.getTimeZone("UTC"));

                birthday.setText(s.format(d));

                male.setChecked(mUser.genderInt == 0);
                female.setChecked(mUser.genderInt == 1);
                freak.setChecked(mUser.genderInt == 2);

                loading.hide();

            }
        });

        assert save != null;
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (male.isChecked())
                    mUser.genderInt = 0;
                else if (female.isChecked())
                    mUser.genderInt = 1;
                else
                    mUser.genderInt = 2;

                try {

                    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                    Timestamp bday = new Timestamp(formatter.parse(birthday.getText().toString()).getTime());
                    mUser.setBirthday(bday);

                } catch (ParseException e) {
                    e.printStackTrace();
                }


                mUser.saveUser(EditProfileActivity.this, new User.OnLoadListener() {
                    @Override
                    public void update() {

                        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
                        builder.setTitle("Profile updated");
                        builder.setMessage("Your profile has been updated");
                        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });

                        builder.create().show();

                    }
                });

            }
        });

        assert birthday != null;
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat s = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

                try {
                    Date date = s.parse(birthday.getText().toString());

                    DatePickerFragment d = new DatePickerFragment(date, birthday);
                    d.show(EditProfileActivity.this.getFragmentManager(), "Birthday!!!");
                } catch (ParseException e) {
                    e.printStackTrace();
                }



            }
        });

    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        private Calendar mStart;
        private EditText mBirthday;

        @SuppressLint("ValidFragment")
        public DatePickerFragment(Date d,  EditText day) {
            mStart = Calendar.getInstance();
            mBirthday = day;
            mStart.setTime(d);

        }

        public DatePickerFragment() {
            super();

        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            int year = mStart.get(Calendar.YEAR);
            int month = mStart.get(Calendar.MONTH);
            int day = mStart.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);

            mBirthday.setText(new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(c.getTime()));

        }
    }

}
