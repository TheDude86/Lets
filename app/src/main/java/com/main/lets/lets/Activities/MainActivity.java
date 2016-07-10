package com.main.lets.lets.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.main.lets.lets.LetsAPI.Login;
import com.main.lets.lets.R;
import com.main.lets.lets.Visualizers.GlobalFeed;
import com.main.lets.lets.Visualizers.ProfileFeed;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    public static final int SELECT_PICTURE = 2;
    public LocationManager mLocationManager;
    public static final int SETTINGS = 0;
    public ProfileFeed mProfileFeed;
    public GlobalFeed mGlobalFeed;
    HashMap<String, Object> mMap;
    public String mActive;
    public Login mLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Object used to store useful information about the user e.g. coordinates, access token, etc.
        mMap = new HashMap<>();

        //Gets the location manager to get the phone's location
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Creates the GlobalFeed object which handles the main event feed and adjusts sorting and
        // reloading events
        mGlobalFeed = new GlobalFeed(this, (UltimateRecyclerView) findViewById(R.id.list), GlobalFeed.Sort.DIST);

        //Creates the User's profile feed if they're logged in, if they're not.  It will load a login
        //screen
        mProfileFeed = new ProfileFeed(this, (UltimateRecyclerView) findViewById(R.id.list), mMap);

        //Login object used for auto login for returning users
        mLogin = new Login(this, new JsonHttpResponseHandler() {

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
                        mMap.put("token", response.getString("accessToken"));
                        mMap.put("userID", response.getInt("user_id"));
                        mProfileFeed.updateToken("Bearer " + response.getString("accessToken"));
                        mGlobalFeed.update(response.getInt("user_id"),
                                "Bearer " + response.getString("accessToken"));

                    } else {
                        Login.clearInfo(MainActivity.this);

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
            mMap.put("latitude", mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER).getLatitude());
            mMap.put("longitude", mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER).getLongitude());

            //Draw the local event feed
            mGlobalFeed.draw(new JSONObject(mMap));

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

        //Set OnClickListener for the Search Icon in the top toolbar
        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("token", "Bearer " + mMap.get("token"));
                intent.putExtra("userID", mMap.containsKey("userID") ? (int) mMap.get("userID") : -1);
                startActivity(intent);
            }
        });

        //Set OnClickListener for the Settings Icon in the top toolbar
        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra("token", "Bearer " + mMap.get("token"));
                startActivityForResult(intent, SETTINGS);
            }
        });

        //Set the recycler view to display the local feed
        findViewById(R.id.btn_feed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.btn_add).setVisibility(View.VISIBLE);
                mGlobalFeed.draw(new JSONObject(mMap));

                //Saving the active visualizer so the activity knows which feed to draw when
                //onResume is called
                mActive = mGlobalFeed.getClass().toString();

            }
        });

        //Set the recycler view to display the user's profile
        findViewById(R.id.btn_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.btn_add).setVisibility(View.INVISIBLE);
                mProfileFeed.draw(null);

                //Saving the active visualizer so the activity knows which feed to draw when
                //onResume is called
                mActive = mProfileFeed.getClass().toString();

            }
        });

        //Set the recycler view to display the user's notifications
        findViewById(R.id.btn_notifications).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.btn_add).setVisibility(View.INVISIBLE);

            }
        });

        //Start the EventCreateActivity
        findViewById(R.id.create_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, EventCreateActivity.class);
                i.putExtra("token", "Bearer " + mMap.get("token"));
                startActivity(i);

            }
        });

        //Start the GroupCreateActivity
        findViewById(R.id.create_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, GroupCreateActivity.class);
                i.putExtra("token", (String) mMap.get("token"));
                startActivity(i);

            }
        });

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
                mMap.put("latitude", mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER).getLatitude());
                mMap.put("longitude", mLocationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER).getLongitude());

                //Draw the GlobalFeed just as if the user perviously accepted the permissions
                mGlobalFeed.draw(new JSONObject(mMap));


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

                if (mActive.equals(mGlobalFeed.getClass().toString())) {
                    mGlobalFeed.draw(new JSONObject(mMap));

                } else if (mActive.equals(mProfileFeed.getClass().toString())) {
                    mProfileFeed.draw(null);

                }
            }
        }
    }

}
