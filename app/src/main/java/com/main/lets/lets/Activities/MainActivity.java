package com.main.lets.lets.Activities;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.firebase.iid.FirebaseInstanceId;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.Adapters.ProfilePreviewAdapter;
import com.main.lets.lets.LetsAPI.Calls;
import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.LetsAPI.Login;
import com.main.lets.lets.LetsAPI.UserData;
import com.main.lets.lets.R;
import com.main.lets.lets.Services.ReminderService;
import com.main.lets.lets.Visualizers.GlobalFeed;
import com.main.lets.lets.Visualizers.NotificationFeed;
import com.main.lets.lets.Visualizers.ProfileFeed;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    public NotificationFeed mNotificationFeed;
    public UltimateRecyclerView mRecyclerView;
    public LocationManager mLocationManager;
    public static final int SETTINGS = 0;
    public static final int WELCOME = 69;
    public boolean fromWelcome = false;
    public ProfileFeed mProfileFeed;
    public GlobalFeed mGlobalFeed;
    public String mActive = "";
    int mAction = 0;
    boolean DeveloperMode = false;

    public static MainActivity mainActivity;
    public static Boolean isVisible = false;
    private static final String TAG = "MainActivity";

    GoogleCloudMessaging gcm;


    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }

    public boolean isRemindersRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ReminderService.class.getName().equals(service.service.getClassName())) {
                return true;

            }
        }

        return false;
    }


    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (DeveloperMode) {
            Toast.makeText(this, "DEVELOPER MODE ACTIVATED", Toast.LENGTH_LONG).show();


            Intent i = new Intent(this, ChatActivity.class);
            i.putExtra("Path", "events/227/chat");
            startActivity(i);
        }


        if (!isRemindersRunning()) {
            Intent intent = new Intent(MainActivity.this, ReminderService.class);
            startService(intent);

        }

        mainActivity = this;

        gcm = GoogleCloudMessaging.getInstance(this);

        mRecyclerView = (UltimateRecyclerView) findViewById(R.id.list);

        mRecyclerView.setBackgroundResource(R.color.relax);

        //Gets the location manager to get the phone's location
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Creates the GlobalFeed object which handles the main event feed and adjusts sorting and
        // reloading events
        mGlobalFeed = new GlobalFeed(this, (UltimateRecyclerView) findViewById(R.id.list), GlobalFeed.Sort.DIST);

        //Creates the User's profile feed if they're logged in, if they're not.  It will load a login
        //screen
        mProfileFeed = new ProfileFeed(this, (UltimateRecyclerView) findViewById(R.id.list));

        //Login object used for auto login for returning users
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        if (!new UserData(this).isLoggedIn() && !fromWelcome) {
            Intent i = new Intent(this, WelcomeActivity.class);
            startActivityForResult(i, WELCOME);

        }

        Calls.login(preferences.getString("email", ""), preferences.getString("password", ""),
                new JsonHttpResponseHandler() {

                    /**
                     * The login object will attempt to login automatically if there are saved credentials
                     * on the device already, if it succeeds, it will update the ProfileFeed object and if
                     * it fails, it will clear the saved credentials
                     *
                     * @param statusCode (Unused)
                     * @param headers (Unused)
                     * @param response contains the access token for the user if successfully logged in and
                     *                 returns an error message of login failed
                     */
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, org.json.JSONObject response) {

                        try {
                            if (response.has("accessToken")) {
                                new Calls.Notify(MainActivity.this, (ImageButton) findViewById(R.id.btn_notifications)).execute();

                                SharedPreferences preferences = PreferenceManager
                                        .getDefaultSharedPreferences(getBaseContext());

                                SharedPreferences.Editor editor = preferences.edit();

                                String s = "{\"Event ID\": 200, \"Event Name\": \"Test\", \"Event Start\": \"03/05/2017 12:00:00\"}";

                                JSONObject j = new JSONObject(s);

                                editor.putString("Reminders", j.toString());
                                editor.putInt("UserID", response.getInt("user_id"));
                                editor.putString("Token", "Bearer " + response.getString("accessToken"));
                                editor.apply();

                                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                                Log.println(Log.ASSERT, TAG, "Refreshed token: " + refreshedToken);
                                Calls.registerToken(refreshedToken, new UserData(MainActivity.this), new JsonHttpResponseHandler() {

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                                    }
                                });

                                mGlobalFeed.update(response.getInt("user_id"),
                                        "Bearer " + response.getString("accessToken"));

                                mProfileFeed = new ProfileFeed(MainActivity.this, (UltimateRecyclerView) findViewById(R.id.list));


                            } else {
                                Login.clearInfo(getBaseContext());

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                });


        //Checks to see if the user has granted the app permission to get the device's location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //If the user has not, request permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

        } else {
            //If the user has, create a hashmap with the coordinates stored and convert into a JSON object


            SharedPreferences.Editor editor = preferences.edit();
            editor.putFloat("latitude",
                    (float) mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER).getLatitude());

            editor.putFloat("longitude",
                    (float) mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER).getLongitude());
            editor.apply();

            //Draw the local event feed
            if (fromWelcome && mAction == 0) {
                findViewById(R.id.btn_add).setVisibility(View.INVISIBLE);

                mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
                mRecyclerView.setAdapter(new ProfilePreviewAdapter(MainActivity.this, (new UserData(MainActivity.this)).ID));

            } else {
                mGlobalFeed.draw(null);

            }

            //Saving the active visualizer so the activity knows which feed to draw when
            //onResume is called
            mActive = mGlobalFeed.getClass().toString();

        }

        //Populating the spinner in the top (Yellow) toolbar
        //The Spinner chooses the sorting algorithm for the events in the feed displayed
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        //What to do when an item is chosen in the spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Once the user has selected an item from the spinner, the position will be returned here
                //0 = Closest
                //1 = Soonest
                //2 = Most newly created event
                //3 = Most people attending

                switch (position) {
                    case 0:

                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                    default:

                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinner.setVisibility(View.GONE);

        //Set OnClickListener for the Share Icon in the top toolbar, opens a share intent
        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hey you with the phone, you should download this app and here's the link:\n iOS: goo.gl/xH9AGr \n\n Android: goo.gl/Bevgh0";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out \"Lets\"!");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Invite your friends to Lets!"));

            }
        });

        //Set OnClickListener for the Search Icon in the top toolbar
        findViewById(R.id.rotate_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewSearchActivity.class);
                startActivity(intent);
            }
        });

        //Set OnClickListener for the Settings Icon in the top toolbar
        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewSettingsActivity.class);
                startActivityForResult(intent, SETTINGS);
            }
        });

        //Set the recycler view to display the local feed
        findViewById(R.id.btn_feed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.btn_add).setVisibility(View.VISIBLE);

                //Saving the active visualizer so the activity knows which feed to draw when
                //onResume is called
                mActive = mGlobalFeed.getClass().toString();

                mGlobalFeed.draw(null);

            }
        });

        //Set the recycler view to display the user's profile
        findViewById(R.id.btn_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.btn_add).setVisibility(View.INVISIBLE);

                mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
                mRecyclerView.setAdapter(new ProfilePreviewAdapter(MainActivity.this, (new UserData(MainActivity.this)).ID));

                //Saving the active visualizer so the activity knows which feed to draw when
                //onResume is called
                mActive = mProfileFeed.getClass().toString();

            }
        });

        //Set the recycler view to display the user's notifications
        findViewById(R.id.btn_notifications).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ImageButton) findViewById(R.id.btn_notifications)).setImageResource(R.drawable.ic_notifications_none_black_24dp);
                findViewById(R.id.btn_add).setVisibility(View.INVISIBLE);

                if (!new UserData(MainActivity.this).isLoggedIn()) {
                    mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
                    mRecyclerView.setAdapter(new ProfilePreviewAdapter(MainActivity.this, (new UserData(MainActivity.this)).ID));

                } else {
                    mNotificationFeed = new NotificationFeed(MainActivity.this, (UltimateRecyclerView) findViewById(R.id.list));
                    mActive = mNotificationFeed.getClass().toString();
                    mNotificationFeed.draw(null);

                }
            }
        });

        //Start the EventCreateActivity
        findViewById(R.id.create_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, NewEventActivity.class);
                startActivity(i);

            }
        });

        //Start the GroupCreateActivity
        findViewById(R.id.create_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, NewGroupActivity.class);
                startActivity(i);

            }
        });

        findViewById(R.id.show_on_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(i);
            }
        });

    }


    @Override
    protected void onRestart() {
        super.onRestart();

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());

        String ShallonCreamerIsATwat = preferences.getString("Token", "");

        if (!ShallonCreamerIsATwat.equals("") && !mActive.equals(NotificationFeed.class.toString()))
            new Calls.Notify(this, (ImageButton) findViewById(R.id.btn_notifications)).execute();


        if (mActive.equals(mGlobalFeed.getClass().toString())) {
            mGlobalFeed.draw(null);

        } else if (mActive.equals(mProfileFeed.getClass().toString()) || mActive.equals("PROFILE")) {
            findViewById(R.id.btn_add).setVisibility(View.INVISIBLE);

            mRecyclerView.setBackgroundResource(R.color.relax);
            mRecyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 1));
            mRecyclerView.setAdapter(new ProfilePreviewAdapter(MainActivity.this, (new UserData(MainActivity.this)).ID));

        }

    }

    @Override
    public void onRequestPermissionsResult
            (int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {
            //requestCode == 1 -> requests access to device's location
            case 1:

                //Still have to check if permissions have been granted
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                        (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    //If the user still hasn't given permissions, give up
                    return;
                }

                //If they allow permissions then create the same hashmap as in onCreate
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

                SharedPreferences.Editor editor = preferences.edit();
                editor.putFloat("latitude",
                        (float) mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER).getLatitude());

                editor.putFloat("longitude",
                        (float) mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER).getLongitude());
                editor.apply();

                //Draw the GlobalFeed just as if the user perviously accepted the permissions
                mGlobalFeed.draw(null);


                break;

            default:

                break;
        }

    }

    /**
     * Need this to make the application multidexed, with all of the libraries, there are more than
     * 4K functions which is the max for a single dex file in Android
     * <p/>
     * Why does this have to be done manually and this doesn't come standard in Android?  Who the fuck
     * knows, Google is just full of lazy bastards.
     *
     * @param base (for the super method)
     */
    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS) {
            if (resultCode == RESULT_OK) {
                if (data.getBooleanExtra("LoggedOut", false))
                    mProfileFeed.updateToken("Bearer ");

                else if (data.getBooleanExtra("Edit", false)) {
                    Intent i = new Intent(this, UserDetailActivity.class);
                    i.putExtra("UserID", (new UserData(this)).ID);
                    i.putExtra("create", true);
                    startActivity(i);

                }

                if (mActive.equals(mGlobalFeed.getClass().toString())) {
                    mGlobalFeed.draw(null);

                } else if (mActive.equals(mProfileFeed.getClass().toString())) {
//                    mProfileFeed.draw(null);

                } else if (mActive.equals(mNotificationFeed.getClass().toString())) {
                    mNotificationFeed.draw(null);
                }
            }
        } else if (requestCode == WELCOME) {
            mAction = data.getIntExtra("action", 0);
            fromWelcome = true;

            if (mAction == 1) {
                mActive = "PROFILE";

            } else if (mAction == 2) {
                Intent i = new Intent(this, RegisterActivity.class);
                startActivity(i);
            }


        }
    }

}
