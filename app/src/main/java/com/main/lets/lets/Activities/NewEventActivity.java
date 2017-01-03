package com.main.lets.lets.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.LetsAPI.UserData;
import com.main.lets.lets.R;
import com.rey.material.app.DatePickerDialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.app.TimePickerDialog;
import com.rey.material.widget.Slider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

@SuppressWarnings("ConstantConditions")
public class NewEventActivity extends AppCompatActivity {
    //HashMap only stores strings because it is used to create the post request and all params
    //must be strings
    private HashMap<String, String> mMap;
    private String ShallonCreamerIsATwat;
    private EditText mLocationLabel;
    private Calendar mCalendar;
    private EditText mLocation;
    private LatLng mCoords;

    private RelativeLayout mDurationLayout;
    private RelativeLayout mLocationLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        UserData d = new UserData(this);

        final Button create = (Button) findViewById(R.id.create);

        final TextView durationLabel = (TextView) findViewById(R.id.duration_label);
        final EditText description = (EditText) findViewById(R.id.description);
        mDurationLayout = (RelativeLayout) findViewById(R.id.duration_layout);
        mLocationLayout = (RelativeLayout) findViewById(R.id.location_layout);
//        final CheckBox isPrivate = (CheckBox) findViewById(R.id.isPrivate);
        final EditText category = (EditText) findViewById(R.id.category);
        mLocationLabel = (EditText) findViewById(R.id.locationLabel);
        final EditText title = (EditText) findViewById(R.id.title);
        final EditText date = (EditText) findViewById(R.id.date);
        final EditText time = (EditText) findViewById(R.id.time);
        Slider duration = (Slider) findViewById(R.id.duration);
        mLocation = (EditText) findViewById(R.id.location);
        ShallonCreamerIsATwat = d.ShallonCreamerIsATwat;
        mCalendar = Calendar.getInstance();

        mDurationLayout.setVisibility(View.GONE);
        mLocationLayout.setVisibility(View.GONE);

        assert durationLabel != null;
        assert description != null;
//        assert isPrivate != null;
        assert duration != null;
        assert category != null;
        assert create != null;
        assert title != null;
        assert date != null;
        assert time != null;


        /*
         *Initializing the HashMap that contains all of the following information:
         *Duration
         *Latitude
         *Longitude
         *Map Title
         *Start
         *Date
         *Category
         *
         * Title and Description need to be retrieved from their TextViews'
         */
        mMap = new HashMap<>();


        //Slider's initial position is 2 so the map is also set to 2 so the user doesn't need
        // to move the slider
        mMap.put("Duration", "2");

        //By default, the event is public unless the user check the private checkbox, stating they want the event to be private
        mMap.put("Public", "true");

//        isPrivate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mMap.put("Hidden", (isPrivate.isChecked()) ? "false" : "true");
//
//            }
//        });

        //The listener for the slider when the position of the slider changes
        duration.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {

            /**
             * When the position of the slider changes, the label and the HashMap are updated here
             * @param view (Unused)
             * @param fromUser (Unused)
             * @param oldPos (Unused)
             * @param newPos (Unused)
             * @param oldValue (Unused)
             * @param newValue New value for the label and Hashmap
             */
            @Override
            public void onPositionChanged
            (Slider view, boolean fromUser, float oldPos, float newPos, int oldValue,
             int newValue) {
                durationLabel.setText(newValue > 1 ? newValue + " Hours" : newValue + " Hour");

                //I forget if you need to remove the old key if you're replacing the value with
                // a new one but it can't hurt
                if (mMap.containsKey("Duration"))
                    mMap.remove("Duration");

                //New value for event's duration
                mMap.put("Duration", newValue + "");

            }
        });

        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onPickButtonClick();

            }
        });

        //The listener for the start time's EditText
        time.setOnClickListener(new View.OnClickListener() {
            /**
             * When the user clicks on the EditText, display a TimePickerDialog for the user
             * to select the start time of the event
             * @param view (Unused)
             */
            @Override
            public void onClick(View view) {
                //Creates a new dialog with it's initial time set to the current time or the
                // previous start time selected by the user
                TimePickerDialog.Builder builder = new TimePickerDialog.Builder(mCalendar
                        .get(Calendar.HOUR_OF_DAY),
                        mCalendar
                                .get(Calendar.MINUTE)) {

                    /**
                     * When the user confirm's the start time of the event, update the HashMap
                     * values and the EditText
                     * @param fragment the parameter from when the dialog closes containing the
                     *                 time's information
                     */
                    //GEFN
                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        //Getting useful dialog from parameter
                        TimePickerDialog dialog = (TimePickerDialog) fragment.getDialog();
                        //Set the EditText to the new time and formatted for users to understand
                        time.setText(dialog.getFormattedTime(new SimpleDateFormat("h:mm a")));

                        //If an old value is in the HashMap remove
                        if (mMap.containsKey("Start"))
                            mMap.remove("Start");

                        //Value for the start date of the event
                        mMap.put("Start",
                                dialog.getFormattedTime(new SimpleDateFormat("HH:mm:ss")));

                        //Updating the Calendar's information (this is important for creating the
                        //end time for the event
                        mCalendar.set(Calendar.HOUR_OF_DAY, dialog.getHour());
                        mCalendar.set(Calendar.MINUTE, dialog.getMinute());

                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                    }
                };

                //Setting the bottom buttons texts' to "OK" and "CANCEL"
                builder.positiveAction("OK")
                        .negativeAction("CANCEL");

                //Showing the dialog over the current Activity
                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getSupportFragmentManager(), "Select Time");
            }
        });


        //The listener for the start date's EditText
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

                        //If the map has an old value, remove it
                        if (mMap.containsKey("Date"))
                            mMap.remove("Date");

                        //Value for the event's start date formatted for post parameters
                        mMap.put("Date",
                                dialog.getFormattedDate(new SimpleDateFormat("MM-dd-yyyy")));

                        //Updating the Calendar's information (this is important for creating the
                        //end time for the event
                        mCalendar.set(Calendar.MONTH, dialog.getMonth());
                        mCalendar.set(Calendar.YEAR, dialog.getYear());
                        mCalendar.set(Calendar.DATE, dialog.getDay());

                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                    }
                };

                //Setting the bottom buttons texts' to "OK" and "CANCEL"
                builder.positiveAction("OK")
                        .negativeAction("CANCEL");

                //Showing the dialog over the current Activity
                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getSupportFragmentManager(), "Select Date");
            }
        });

        //The listener for when the category's EditText
        category.setOnClickListener(new View.OnClickListener() {
            /**
             * When the category EditText is clicked, create a dialog box containing the potential
             * categories and then update the HashMap and EditText once the dialog box closes.
             * @param view (Unused)
             */
            @Override
            public void onClick(View view) {
                //Create the builder for the user to select a category
                SimpleDialog.Builder builder = new SimpleDialog.Builder() {
                    /**
                     * Create's a dialog for the user to select one of nine categories:
                     * 0:  Party
                     * 1: Eat/Drink
                     * 2:Study
                     * 3:TV/Movies
                     * 4:Video Games
                     * 5:Sports
                     * 6:Music
                     * 7:Relax
                     * 8:Other
                     *
                     * When they confirm their choice, the HashMap is updated with the category's
                     * number and the EditText is updated with the title
                     * @param fragment (Used by super method)
                     */
                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        //Set the EditText to the selected category name
                        category.setText(getSelectedValue());

                        //If the map already has a "Category" key, remove it
                        if (mMap.containsKey("Category"))
                            mMap.remove("Category");

                        //Value of the category's number
                        mMap.put("Category", getSelectedIndex() + "");

                        super.onPositiveActionClicked(fragment);
                    }

                    @Override
                    public void onNegativeActionClicked(DialogFragment fragment) {
                        super.onNegativeActionClicked(fragment);
                    }
                };

                //Listing all of the categories for the dialog (order of categories is important)
                //and setting the text for the positive and negative buttons at the bottom of the
                //dialog box
                builder.items(new String[]{"Party", "Eating/ Drinking", "Study", "TV/ Movies",
                        "Video Games", "Sports", "Music", "Relax", "Other"}, 0)
                        .title("Select Category")
                        .positiveAction("OK")
                        .negativeAction("CANCEL");

                //Showing the dialog over the current Activity
                DialogFragment fragment = DialogFragment.newInstance(builder);
                fragment.show(getSupportFragmentManager(), "Select Category");

            }
        });

        //The listener for when the user presses the create button
        create.setOnClickListener(new View.OnClickListener() {

            /**
             * First it checks to see if all of the necessary information has been put in, if not
             * then return and tell the user where they went wrong and if all of the information
             * is there and valid, then set the post request to create the event and if it is
             * successful, start an EventDetailActivity to show the user their new event
             * @param view (Unused)
             */
            @Override
            public void onClick(View view) {

                //Checks to see if a start time has been selected
                if (time.getText().toString().equals("Some Time o'clock")) {
                    return;
                }

                //Checks to see if a category has been selected
                if (category.getText().toString().equals("Category")) {
                    return;
                }

                //Checks to see if a date has been selected
                if (date.getText().toString().equals("Today")) {
                    return;
                }

                //Checks to see if the user has written a title

                if (title.getText().toString().equals("")) {
                    return;
                }

                //Checks to see if the user has selected a location (Only have to check "Latitude"
                //because "Latitude" will never be put without "Longitude" and "Map Title"
                if (mCoords == null || mLocationLabel.getText().toString().equals("")) {
                    return;
                }

                Calendar end = (Calendar) mCalendar.clone();
                end.set(Calendar.HOUR_OF_DAY, end.get(Calendar.HOUR_OF_DAY) + Integer.parseInt(
                        mMap.get("Duration")));

                mMap.put("Title", title.getText().toString());
                mMap.put("Description", description.getText().toString());

                mMap.put("End Time", end.getTimeInMillis() + "");
                mMap.put("Start Time", mCalendar.getTimeInMillis() + "");

                mMap.put("Latitude", mCoords.latitude + "");
                mMap.put("Longitude", mCoords.longitude + "");

                mMap.put("Map Title", mLocationLabel.getText().toString());

                Calls.createEvent(mMap, ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        Log.println(Log.ASSERT, "EventCreateActivity", response.toString());
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                        try {

                            L.println(NewEventActivity.class, response.toString());

                            Intent intent = new Intent(NewEventActivity.this,
                                    EventDetailActivity.class);
                            intent.putExtra("EventID",
                                    response.getJSONObject(0).getInt("Event_ID"));

                            finish();
                            startActivity(intent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable,
                                          org.json.JSONArray errorResponse) {
//                        Log.e("Aync Test Failure", errorResponse.toString());
                    }

                });

            }

        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.menu_context_settings) {
            mDurationLayout.setVisibility(View.VISIBLE);
            mLocationLayout.setVisibility(View.VISIBLE);

        }


        return false;
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == 0
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            //GEFN
            @SuppressWarnings("deprecation")
            Place place = PlacePicker.getPlace(data, this);

            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();


            mLocationLabel.setText(name);
            mCoords = place.getLatLng();
            mLocation.setText(address);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onPickButtonClick() {
        // Construct an intent for the place picker
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(intent, 0);

        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            // ...
        }
    }

}
