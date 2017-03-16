package com.main.lets.lets.Adapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Activities.EventDetailActivity;
import com.main.lets.lets.LetsAPI.Calls;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by novosejr on 1/8/2017.
 */

public class CreateEventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private HashMap<String, String> mMap = new HashMap<>();
    private DescriptionHolder mDescription;
    private AppCompatActivity mActivity;
    private CategoryHolder mCategory;
    private LocationHolder mLocation;
    private HashtagHolder mHashtags;
    private NameHolder mName;
    private DateHolder mDate;

    public CreateEventAdapter(AppCompatActivity a) {
        mActivity = a;
        mMap.put("Duration", "2");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            mName = new NameHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_create_name,
                            parent, false));

            return mName;
        } else if (viewType == 1) {
            mDate = new DateHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_create_date,
                            parent, false));

            return mDate;
        } else if (viewType == 2) {
            mLocation = new LocationHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_create_location,
                            parent, false));

            return mLocation;
        } else if (viewType == 3) {
            mDescription = new DescriptionHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_create_description,
                            parent, false));

            return mDescription;
        } else if (viewType == 4) {
            mCategory = new CategoryHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_create_category,
                            parent, false));

            return mCategory;
        } else if (viewType == 5) {
            mHashtags = new HashtagHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_hashtag,
                            parent, false));

            return mHashtags;
        } else if (viewType == 6) {

            return new CreateHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_create_create,
                            parent, false));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 7;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void notifyMoreSettings() {
        mDate.notifyMoreSettings();
        mLocation.notifyMoreSettings();

    }

    public void notifyAddedLocation(Place place) {
        final CharSequence name = place.getName();
        final CharSequence address = place.getAddress();


        mLocation.mLocation.setText(name);
        mLocation.mCoords = place.getLatLng();
        mLocation.mLabel.setText(address);

    }

    public class NameHolder extends RecyclerView.ViewHolder {

        EditText mTitle;

        public NameHolder(View itemView) {
            super(itemView);
            mTitle = (EditText) itemView.findViewById(R.id.event_name);
        }

    }

    public class HashtagHolder extends RecyclerView.ViewHolder {

        RecyclerView mList;
        EditText mHashtag;
        HashtagAdapter mAdapter;

        public HashtagHolder(View itemView) {
            super(itemView);

            mList = (RecyclerView) itemView.findViewById(R.id.list);
            mHashtag = (EditText) itemView.findViewById(R.id.hashtag);
            mAdapter = new HashtagAdapter(mActivity);

            mList.setLayoutManager(new GridLayoutManager(mActivity, 1));
            mList.setAdapter(mAdapter);


            mHashtag.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().endsWith(" ") || charSequence.toString().contains("\n")) {

                        if (charSequence.length() > 1) {
                            String s  = "#" + charSequence.toString().replace("#", "").trim();
                            mAdapter.add(s.replace(" ", "_"));
                            mHashtag.setText("");

                        }

                    } else if (charSequence.toString().contains(" ")){
                        mHashtag.setText(charSequence.toString().replace(" ", "_"));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }

    }

    public class DateHolder extends RecyclerView.ViewHolder {
        EditText mDate;
        EditText mTime;
        Switch mAllDay;
        TextView mLabel;
        Slider mDuration;
        Calendar mCalendar;
        RelativeLayout mContainer;
        View.OnClickListener mTimeListener;
        Slider.OnPositionChangeListener mDurationListener;

        public DateHolder(View itemView) {
            super(itemView);

            mCalendar = Calendar.getInstance();

            mDate = (EditText) itemView.findViewById(R.id.date);
            mTime = (EditText) itemView.findViewById(R.id.time);
            mLabel = (TextView) itemView.findViewById(R.id.label);
            mAllDay = (Switch) itemView.findViewById(R.id.all_day);
            mDuration = (Slider) itemView.findViewById(R.id.duration);
            mContainer = (RelativeLayout) itemView.findViewById(R.id.duration_container);

            mContainer.setVisibility(View.GONE);

            mAllDay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        mTime.setAlpha(.6f);
                        mTime.setOnClickListener(null);
                        mDuration.setAlpha(.6f);
                        mDuration.setOnPositionChangeListener(null);
                        mDuration.setClickable(false);
                        mDuration.setLongClickable(false);
                        mDuration.setFocusable(false);
                        mDuration.setActivated(false);
                        mDuration.setEnabled(false);
                    } else {
                        mTime.setAlpha(1f);
                        mTime.setOnClickListener(mTimeListener);
                        mDuration.setAlpha(1f);
                        mDuration.setOnPositionChangeListener(mDurationListener);
                        mDuration.setClickable(true);
                        mDuration.setLongClickable(true);
                        mDuration.setFocusable(true);
                        mDuration.setActivated(true);
                        mDuration.setEnabled(true);
                    }
                }
            });

            mDurationListener = new Slider.OnPositionChangeListener() {

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
                    mLabel.setText(newValue > 1 ? newValue + " Hours" : newValue + " Hour");

                    //I forget if you need to remove the old key if you're replacing the value with
                    // a new one but it can't hurt
                    if (mMap.containsKey("Duration"))
                        mMap.remove("Duration");

                    //New value for event's duration
                    mMap.put("Duration", newValue + "");

                }
            };

            mDuration.setOnPositionChangeListener(mDurationListener);

            mDate.setOnClickListener(new View.OnClickListener() {
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
                            mDate.setText(dialog.getFormattedDate(SimpleDateFormat.getDateInstance()));

                            //Value for the event's start date formatted for post parameters
                            mMap.put("Date", dialog.getFormattedDate(new SimpleDateFormat("MM-dd-yyyy")));

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
                    fragment.show(mActivity.getSupportFragmentManager(), "Select Date");
                }
            });

            mTimeListener = new View.OnClickListener() {
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
                            mTime.setText(dialog.getFormattedTime(new SimpleDateFormat("h:mm a")));

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
                    fragment.show(mActivity.getSupportFragmentManager(), "Select Time");
                }
            };

            mTime.setOnClickListener(mTimeListener);

        }

        public void notifyMoreSettings() {
            mContainer.setVisibility(View.VISIBLE);
        }
    }

    public class LocationHolder extends RecyclerView.ViewHolder {
        RelativeLayout mContainer;
        EditText mLocation;
        EditText mLabel;
        LatLng mCoords;

        public LocationHolder(View itemView) {
            super(itemView);
            mLocation = (EditText) itemView.findViewById(R.id.location);
            mLabel = (EditText) itemView.findViewById(R.id.location_label);
            mContainer = (RelativeLayout) itemView.findViewById(R.id.label_container);

            mContainer.setVisibility(View.GONE);

            mLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        PlacePicker.IntentBuilder intentBuilder =
                                new PlacePicker.IntentBuilder();
                        Intent intent = intentBuilder.build(mActivity);
                        // Start the intent by requesting a result,
                        // identified by a request code.
                        mActivity.startActivityForResult(intent, 0);

                    } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        // ...
                    }
                }
            });

        }

        public void notifyMoreSettings() {
            mContainer.setVisibility(View.VISIBLE);
        }
    }

    public class DescriptionHolder extends RecyclerView.ViewHolder {
        EditText mDescription;

        public DescriptionHolder(View itemView) {
            super(itemView);
            mDescription = (EditText) itemView.findViewById(R.id.description);
        }
    }

    public class CategoryHolder extends RecyclerView.ViewHolder {
        EditText mCategory;

        public CategoryHolder(View itemView) {
            super(itemView);
            mCategory = (EditText) itemView.findViewById(R.id.category);


            mCategory.setOnClickListener(new View.OnClickListener() {
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
                            mCategory.setText(getSelectedValue());

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
                    fragment.show(mActivity.getSupportFragmentManager(), "Select Category");

                }
            });

        }
    }

    public class CreateHolder extends RecyclerView.ViewHolder {
        Button mCreate;

        public CreateHolder(View itemView) {
            super(itemView);
            mCreate = (Button) itemView.findViewById(R.id.create);

            mCreate.setOnClickListener(new View.OnClickListener() {

                /**
                 * First it checks to see if all of the necessary information has been put in, if not
                 * then return and tell the user where they went wrong and if all of the information
                 * is there and valid, then set the post request to create the event and if it is
                 * successful, start an EventDetailActivity to show the user their new event
                 * @param view (Unused)
                 */
                @Override
                public void onClick(View view) {


                    boolean allDay = mDate.mAllDay.isChecked();

                    if (allDay) {
                        mMap.put("Duration", "24");
                        mMap.put("Start", "00:00:00");

                        mDate.mCalendar.set(Calendar.HOUR_OF_DAY, 0);
                        mDate.mCalendar.set(Calendar.MINUTE, 0);

                    }

                    final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setTitle("Well this is awkward...");

                    //Checks to see if a start time has been selected
                    if (mDate.mTime.getText().toString().equals("Select Time") && !allDay) {

                        builder.setMessage("As it turns out, you actually need to set a time for " +
                                "your event, so people, ya' know, can know when to show up.  Sooo go do that.");

                        builder.setPositiveButton("Okay", null);

                        builder.create().show();

                    } else if (mCategory.mCategory.getText().toString().equals("Category")) {
                        builder.setMessage("We love the fact you want to create an event for the " +
                                "world to see but we need you to choose a category.  If you don't " +
                                "thinks start to break, children cry and next thing you know you " +
                                "could end up naked in a tree (Probably not).");

                        builder.setPositiveButton("Okay", null);

                        builder.create().show();
                    } else if (mDate.mDate.getText().toString().equals("Select Date")) {
                        builder.setMessage("Okay so you managed to set a time for your event but " +
                                "you didn't seem to bother to set a date.  I'll just let you figure " +
                                "out why that may be problematic.");

                        builder.setPositiveButton("Okay", null);

                        builder.create().show();
                    } else if (mName.mTitle.getText().toString().equals("")) {
                        builder.setMessage("Hey man, great event you got here except... it " +
                                "doesn't have a name " + new String(Character.toChars(0x1F624)) +
                                ".  Let me ask you one thing, would you do this to your child?  " +
                                "No you wouldn't, because not naming your child would screw up " +
                                "your child's head forever so don't do it with your event either.");

                        builder.setPositiveButton("Okay", null);

                        builder.create().show();
                    } else if (mLocation.mCoords == null || mLocation.mLabel.getText().toString().equals("")) {
                        builder.setMessage("So you missed one minor detail, the event's location.  " +
                                "How are people going to know where to show up?  Is the location a secret?  " +
                                "If so that sounds creepy and we don't do that here, go be creepy " +
                                "on Tinder, it's what it's for.");

                        builder.setPositiveButton("Okay", null);

                        builder.create().show();

                    } else {
                        Calendar end = (Calendar) mDate.mCalendar.clone();
                        end.set(Calendar.HOUR_OF_DAY, end.get(Calendar.HOUR_OF_DAY) + Integer.parseInt(
                                mMap.get("Duration")));

                        mMap.put("Title", mName.mTitle.getText().toString());
                        mMap.put("Description", mDescription.mDescription.getText().toString());

                        mMap.put("End Time", end.getTimeInMillis() + "");
                        mMap.put("Start Time", mDate.mCalendar.getTimeInMillis() + "");

                        mMap.put("Latitude", mLocation.mCoords.latitude + "");
                        mMap.put("Longitude", mLocation.mCoords.longitude + "");

                        mMap.put("Map Title", mLocation.mLabel.getText().toString());

                        UserData u = new UserData(mActivity);


                        final ProgressDialog loading = ProgressDialog.show(mActivity, "",
                                "Creating event. Please wait...", true);

                        Calls.createEvent(mMap, u.ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                loading.hide();

                                try {
                                    String message = response.getString("error");

                                    builder.setMessage("So here's the thing, I personally love " +
                                            "this event you got here but... Our server monkeys had " +
                                            "this complaint:\n\n" + message + "\n\n And they won't " +
                                            "let you make the event until you fix it. ");

                                    builder.setPositiveButton("Okay", null);

                                    builder.create().show();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                                loading.hide();

                                try {

                                    int ID = response.getJSONObject(0).getInt("Event_ID");

                                    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
                                    DatabaseReference db2 = FirebaseDatabase.getInstance().getReference();

                                    ArrayList<String> tags = mHashtags.mAdapter.mList;

                                    for (String s: tags) {
                                        db.child("events/" + ID).push().setValue(s.replace("#", ""));
                                        db2.child("hashtags/" + s.replace("#", "")).push().setValue(ID);

                                    }


                                    Intent intent = new Intent(mActivity,
                                            EventDetailActivity.class);
                                    intent.putExtra("EventID", ID);

                                    mActivity.finish();
                                    mActivity.startActivity(intent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }


                        });

                    }


                }

            });
        }

    }

}
