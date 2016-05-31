package com.main.lets.lets.Activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.main.lets.lets.R;
import com.main.lets.lets.Visulizers.GlobalFeed;
import com.main.lets.lets.Visulizers.ProfileFeed;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;

import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    public static final int SELECT_PICTURE = 2;
    public LocationManager mLocationManager;
    public ProfileFeed mProfileFeed;
    public GlobalFeed mGlobalFeed;
    HashMap<String, Object> mMap;

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

        mProfileFeed = new ProfileFeed(this, (UltimateRecyclerView) findViewById(R.id.list), mMap);

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

                switch (position) {
                    case 0:

                        break;
                    case 1:

                        break;

                    case 2:

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

            }
        });

        //Set OnClickListener for the Settings Icon in the top toolbar
        findViewById(R.id.settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //Set the recycler view to display the local feed
        findViewById(R.id.btn_feed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.btn_add).setVisibility(View.VISIBLE);
                mGlobalFeed.draw(new JSONObject(mMap));


            }
        });

        //Set the recycler view to display the user's profile
        findViewById(R.id.btn_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.btn_add).setVisibility(View.INVISIBLE);
                mProfileFeed.draw(null);

            }
        });

        //Set the recycler view to display the user's notifications
        findViewById(R.id.btn_notifications).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.btn_add).setVisibility(View.INVISIBLE);

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

}
