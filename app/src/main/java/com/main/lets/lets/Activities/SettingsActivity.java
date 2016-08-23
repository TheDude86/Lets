package com.main.lets.lets.Activities;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.Login;
import com.main.lets.lets.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    private static final int CODE_LOGOUT = 0;
    private static boolean loggedOut = false;
    static String ShallonCreamerIsATwat;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
            new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    String stringValue = value.toString();

                    if (preference instanceof ListPreference) {
                        // For list preferences, look up the correct display value in
                        // the preference's 'entries' list.
                        ListPreference listPreference = (ListPreference) preference;
                        int index = listPreference.findIndexOfValue(stringValue);

                        // Set the summary to reflect the new value.
                        preference.setSummary(
                                index >= 0
                                        ? listPreference.getEntries()[index]
                                        : null);

                    } else if (preference instanceof RingtonePreference) {
                        // For ringtone preferences, look up the correct display value
                        // using RingtoneManager.
                        if (TextUtils.isEmpty(stringValue)) {
                            // Empty values correspond to 'silent' (no ringtone).
                            preference.setSummary(R.string.pref_ringtone_silent);

                        } else {
                            Ringtone ringtone = RingtoneManager.getRingtone(
                                    preference.getContext(), Uri.parse(stringValue));

                            if (ringtone == null) {
                                // Clear the summary if there was a lookup error.
                                preference.setSummary(null);
                            } else {
                                // Set the summary to reflect the new ringtone display
                                // name.
                                String name = ringtone.getTitle(preference.getContext());
                                preference.setSummary(name);
                            }
                        }

                    } else {
                        // For all other preferences, set the summary to the value's
                        // simple string representation.
                        preference.setSummary(stringValue);
                    }
                    return true;
                }
            };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                                                                 PreferenceManager
                                                                         .getDefaultSharedPreferences(
                                                                                 preference
                                                                                         .getContext())
                                                                         .getString(preference
                                                                                            .getKey(),
                                                                                    ""));
    }

    /**
     * Retrieves the access token passed through the intent and saves it locally intellectually as
     * ShallonCreamerIsATwat because that bitch is and every person should know.
     * FUCK YOU SHALLON!!!
     * <p/>
     * Hehe hopefully this app gets big and one day she downloads this app and it'll be like I'm
     * calling her a twat to her face every time she uses it.  Ah good thoughts... Oh also this is
     * where we set up the action bar but that code was auto generated.
     *
     * @param savedInstanceState for stuff and things, or was it things and stuff?  Fuck it, it goes
     *                           to the super method, that's all I know
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShallonCreamerIsATwat = getIntent().getStringExtra("token");  //  <-- Bitch
        // The promised method to set up the action bar, sorry for the rant but to be honest I'm
        // not that sorry...      ;)
        setupActionBar();


    }

    /**
     * Overriding this method so we can return with a result to let the main activity know the user
     * logged out and update the feeds
     */
    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        //If the user pressed the log out button, "loggedOut" will be true
        data.putExtra("LoggedOut", loggedOut);
        setResult(RESULT_OK, data);
        finish();

    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);

        }
    }

    /**
     * The user can also return to the main activity by pressing the back arrow button on the
     * action bar so we got to override that again and return with a result for logging purposes
     * again
     *
     * @param item item clicked in the toolbar
     * @return returns true always because I like to believe... Eh idk, I'm so fucking tired of
     * writing comments
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent data = new Intent();
                data.putExtra("LoggedOut", loggedOut);
                setResult(RESULT_OK, data);
                finish();

                break;
        }
        return true;
    }

    /**
     * I know I added this code because there are no comments but I have honestly no idea what it's
     * doing... Oh I see something about the home ID, must be doing something with pressing the
     * back arrow on the toolbar.
     *
     * @param featureId It is used for stuff and things
     * @param item      item clicked in the toolbar
     * @return
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    @Override
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || UserPreferenceFragment.class.getName().equals(fragmentName)
                || AppSettingPreferenceFragment.class.getName().equals(fragmentName)
                || SearchPreferenceFragment.class.getName().equals(fragmentName)
                || LogoutPreferenceFragment.class.getName().equals(fragmentName);
    }


    /**
     * When a preference is selected, it inflates a fragment and it's XML so we need to pass the
     * access token from the activity into the edit user fragment so it can load in the user's
     * current content
     *
     * @param header   is used by fragments and used to pass data from activity to fragment
     * @param position for the super method
     */
    @Override
    public void onHeaderClick(Header header, int position) {
        if (header.fragmentArguments == null) {
            header.fragmentArguments = new Bundle();
        }

        //Puts the access token into the bundle for the fragment to get
        header.fragmentArguments.putString("token", ShallonCreamerIsATwat);
        super.onHeaderClick(header, position);

        //Idk what this shit does, not mine
        if (header.fragment != null) {
            int titleRes = header.breadCrumbTitleRes;
            int shortTitleRes = header.breadCrumbShortTitleRes;
            if (titleRes == 0) {
                titleRes = header.titleRes;
                shortTitleRes = 0;
            }
            startWithFragment(header.fragment, header.fragmentArguments, null, 0,
                              titleRes, shortTitleRes);
        } else if (header.intent != null) {
            //I tried a thing here
            startActivityForResult(header.intent, CODE_LOGOUT);
        }

    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class UserPreferenceFragment extends Fragment {
        HashMap<String, Object> mUserInfo;
        boolean mPhotoChanged = false;
        int SELECT_PHOTO = 0;
        ImageView mProfile;
        Bitmap mImage;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);

            mUserInfo = new HashMap<>();

            Bundle b = getArguments();
            if (b != null)
                ShallonCreamerIsATwat = b.getString("token");

        }

        public void editProfile(final ProgressDialog dialog){
            Calls.editProfile(mUserInfo, ShallonCreamerIsATwat,
                              new JsonHttpResponseHandler() {

                                  @Override
                                  public void onFailure(int statusCode,
                                                        cz.msebera.android.httpclient
                                                                .Header[] headers,
                                                        Throwable throwable,
                                                        org.json.JSONArray errorResponse) {
                                      dialog.hide();

                                  }

                                  @Override
                                  public void onFailure(int statusCode,
                                                        cz.msebera.android.httpclient
                                                                .Header[] headers,
                                                        String s, Throwable throwable) {
                                      getActivity().finish();

                                      dialog.hide();

                                  }

                                  @Override
                                  public void onSuccess(int i,
                                                        cz.msebera.android.httpclient
                                                                .Header[] headers,
                                                        JSONObject json) {
                                      dialog.hide();
                                      getActivity().finish();

                                  }

                              });
        }

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState) {
            final View view = inflater.inflate(R.layout.fragment_edit_profile,
                                               container, false);

            final ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                                                              "Loading. Please wait...", true);

            mProfile = (ImageView) view.findViewById(R.id.pro_pic);
            mProfile.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(
                                getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    } else {

                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, SELECT_PHOTO);

                    }

                }
            });

            view.findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mUserInfo.put("name", ((EditText) view.findViewById(R.id.edit_name)).getText()
                            .toString());
                    mUserInfo.put("bio", ((EditText) view.findViewById(R.id.edit_bio)).getText()
                            .toString());

                    final ProgressDialog dialog = ProgressDialog.show(getActivity(), "",
                                                                      "Saving. Please wait...", true);

                    if (mPhotoChanged) {
                        SharedPreferences preferences = PreferenceManager
                                .getDefaultSharedPreferences(getActivity().getBaseContext());

                        ShallonCreamerIsATwat = preferences.getString("Token", "");
                        int id = preferences.getInt("UserID", -1);

                        long millis = System.currentTimeMillis();
                        String name = "user" + id + "-" + millis;

                        mUserInfo.put("picRef",  "https://let.blob.core.windows.net/mycontainer/" + name);

                        Calls.uploadImage(mImage, getActivity(), name,
                                          new Calls.UploadImage.onFinished() {
                                              @Override
                                              public void onFinished() {

                                                  editProfile(dialog);

                                              }
                                          });
                    } else {
                        editProfile(dialog);
                    }




                }

            });

            (view.findViewById(R.id.edit_birthday)).setOnClickListener(new View.OnClickListener() {
                /**
                 * When tue user confirm's the start date of the event, update the HashMap
                 * values and the EditText
                 * @param v (Unused)
                 */
                @Override
                public void onClick(View v) {
                    DialogFragment newFragment =
                            new DatePickerFragment((Date) mUserInfo.get("birthday"), mUserInfo,
                                                   (EditText) view
                                                           .findViewById(R.id.edit_birthday));
                    newFragment.show(getFragmentManager(), "datePicker");

                }

            });

            view.findViewById(R.id.edit_interests).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CharSequence[] items = {" Party ", " Eating & Drinking ", " Studying ",
                            " TV & Movies ", " Video Games ", " Sports ", " Music ", " Relax ",
                            " Other "};
                    // arraylist to keep the selected items
                    final ArrayList<Integer> seletedItems = new ArrayList<>();

                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setTitle("Select Interests")
                            .setCancelable(true)
                            .setMultiChoiceItems(items, null,
                                                 new DialogInterface.OnMultiChoiceClickListener() {
                                                     @Override
                                                     public void onClick(DialogInterface dialog,
                                                                         int indexSelected,
                                                                         boolean isChecked) {
                                                         if (isChecked) {
                                                             // If the user checked the item, add
                                                             // it to the selected items
                                                             seletedItems.add(indexSelected);
                                                         } else if (seletedItems
                                                                 .contains(indexSelected)) {
                                                             // Else, if the item is already in
                                                             // the array, remove it
                                                             seletedItems.remove(Integer.valueOf(
                                                                     indexSelected));
                                                         }
                                                     }
                                                 })
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    String rawString = MapListToString(seletedItems);

                                    ((EditText) view.findViewById(R.id.edit_interests)).setText(rawString);

                                    mUserInfo.put("interests",toInterestDataString(rawString));

                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Your code when user clicked on Cancel
                                }
                            }).create();
                    dialog.show();
                }
            });

            Calls.getMyProfile(ShallonCreamerIsATwat, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode,
                                      cz.msebera.android.httpclient.Header[] headers,
                                      org.json.JSONObject response) {
                    try {

                        mUserInfo.put("id", response.getJSONArray("info").getJSONObject(0)
                                .getInt("User_ID"));
                        mUserInfo
                                .put("email", response.getJSONArray("info").getJSONObject(0)
                                        .getString("Email_Address"));
                        mUserInfo.put("name", response.getJSONArray("info").getJSONObject(0)
                                .getString("User_Name"));

                        boolean b = TimeZone.getDefault().inDaylightTime(new Date());

                        Calendar cal = Calendar.getInstance();
                        long milliDiff = cal.get(Calendar.ZONE_OFFSET);


                        String birthdayString = response.getJSONArray("info").getJSONObject(0)
                                .getString("Birthday");
                        long birthday = Long.parseLong(
                                birthdayString.substring(6, birthdayString.length() - 2)) - milliDiff - (b ? 3600000 : 0);

                        mUserInfo.put("birthday", new Date(birthday));

                        mUserInfo.put("bio", response.getJSONArray("info").getJSONObject(0)
                                .getString("Biography"));
                        mUserInfo.put("interests", response.getJSONArray("info").getJSONObject(0)
                                .getInt("Interests"));
                        mUserInfo.put("gender", response.getJSONArray("info").getJSONObject(0)
                                .getInt("Gender"));
                        mUserInfo.put("privacy", response.getJSONArray("info").getJSONObject(0)
                                .getInt("Privacy"));
                        mUserInfo.put("picRef",
                                      response.getJSONArray("info").getJSONObject(0)
                                              .getString("Profile_Picture"));

                        Picasso.with(getActivity()).load(
                                (String) mUserInfo.get("picRef")).into(
                                (ImageView) view.findViewById(R.id.pro_pic));

                        ((EditText) (view.findViewById(R.id.edit_name)))
                                .setText((CharSequence) mUserInfo.get("name"));


                        ((EditText) (view.findViewById(R.id.edit_birthday)))
                                .setText(new SimpleDateFormat("MM-dd-yyyy")
                                                 .format(mUserInfo.get("birthday")));

                        ((EditText) (view.findViewById(R.id.edit_bio)))
                                .setText((CharSequence) mUserInfo.get("bio"));

                        JSONObject interests = response.getJSONObject("interests");
                        String interestString = "";
                        Iterator<String> keys = interests.keys();

                        while (keys.hasNext()){
                            String key = keys.next();
                            if (interests.getBoolean(key)) {
                                if (interestString.length() == 0)
                                    interestString = key;
                                else
                                    interestString += ", " + key;

                            }
                        }

                        ((EditText) (view.findViewById(R.id.edit_interests))).setText(interestString);
                        mUserInfo.put("interests", toInterestDataString(interestString));


                        if (mUserInfo.get("gender") == 0)
                            ((RadioButton) (view.findViewById(R.id.female))).setChecked(true);

                        if (mUserInfo.get("gender") == 1)
                            ((RadioButton) (view.findViewById(R.id.male))).setChecked(true);

                        if (mUserInfo.get("gender") == 2)
                            ((RadioButton) (view.findViewById(R.id.tranny))).setChecked(true);

                        if (mUserInfo.get("privacy") == 0)
                            ((RadioButton) (view.findViewById(R.id.chk_public))).setChecked(true);

                        if (mUserInfo.get("privacy") == 1)
                            ((RadioButton) (view.findViewById(R.id.chk_restricted)))
                                    .setChecked(true);

                        if (mUserInfo.get("privacy") == 2)
                            ((RadioButton) (view.findViewById(R.id.chk_pussy))).setChecked(true);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    dialog.hide();

                }

                @Override
                public void onFailure(int statusCode,
                                      cz.msebera.android.httpclient.Header[] headers,
                                      Throwable throwable,
                                      org.json.JSONArray errorResponse) {
                }

            });

            return view;
        }

        public String toInterestDataString(String rawString){
            rawString = rawString.replace("Eating & Drinking", "Eat/Drink");
            rawString = rawString.replace("TV & Movies", "TV/Movies");
            rawString = rawString.replace("Studying", "Study");
            rawString = rawString.replace("Relaxing", "Relax");
            rawString = rawString.replace(" ", "");
            rawString = rawString.replace("VideoGames", "Video Games");

            return rawString;
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode) {

                case 2:
                    if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                        photoPickerIntent.setType("image/*");
                        startActivityForResult(photoPickerIntent, SELECT_PHOTO);

                    }
                    break;

                default:
                    break;
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);


            if (requestCode == 0) {
                if (resultCode == RESULT_OK){

                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                        if (selectedImage != null) {
                            mPhotoChanged = true;
                            mProfile.setImageBitmap(selectedImage);
                            mImage = selectedImage;
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                }
            }

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                intent.putExtra("token", ShallonCreamerIsATwat);
                startActivity(intent);

                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        public static String MapListToString(ArrayList l) {
            StringBuilder s = new StringBuilder();

            for (Object i : l) {
                switch ((int) i) {
                    case 0:
                        if (s.length() > 0)
                            s.append(", ");
                        s.append("Party");

                        break;
                    case 1:
                        if (s.length() > 0)
                            s.append(", ");
                        s.append("Eating & Drinking");

                        break;
                    case 2:
                        if (s.length() > 0)
                            s.append(", ");
                        s.append("Studying");

                        break;
                    case 3:
                        if (s.length() > 0)
                            s.append(", ");
                        s.append("TV & Movies");

                        break;
                    case 4:
                        if (s.length() > 0)
                            s.append(", ");
                        s.append("Video Games");

                        break;
                    case 5:
                        if (s.length() > 0)
                            s.append(", ");
                        s.append("Sports");

                        break;
                    case 6:
                        if (s.length() > 0)
                            s.append(", ");
                        s.append("Music");

                        break;
                    case 7:
                        if (s.length() > 0)
                            s.append(", ");
                        s.append("Relaxing");

                        break;
                    case 8:
                        if (s.length() > 0)
                            s.append(", ");
                        s.append("Other");

                        break;

                }
            }


            return s.toString();
        }

    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SearchPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_search);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("search_radius"));

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AppSettingPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notifications);
            setHasOptionsMenu(true);

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class LogoutPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notifications);
            setHasOptionsMenu(true);

            loggedOut = true;
            Login.clearInfo(getActivity().getBaseContext());
            getActivity().setResult(RESULT_OK, new Intent(getActivity(), MainActivity.class));
            getActivity().finish();

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        private Calendar mStart;
        private HashMap<String, Object> mUserInfo;
        private EditText mBirthday;

        public DatePickerFragment(Date d, HashMap<String, Object> m, EditText day) {
            mStart = Calendar.getInstance();
            mUserInfo = m;
            mBirthday = day;
            mStart.setTime(d);

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
            mUserInfo.put("birthday", c.getTime());

            mBirthday.setText(new SimpleDateFormat("MM-dd-yyyy")
                                      .format(mUserInfo.get("birthday")));

        }
    }
}
