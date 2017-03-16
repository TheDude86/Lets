package com.main.lets.lets.LetsAPI;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by novosejr on 12/14/2016.
 */
public class UserData {
    public int ID;
    public String email;
    public String password;
    public AppCompatActivity mActivity;
    public Activity mMapsActivity;
    public String ShallonCreamerIsATwat;
    public LatLng coords;
    public Reminder reminder;
    public long reminderLength;

    public UserData(AppCompatActivity a) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(a.getBaseContext());

        mActivity = a;
        email = preferences.getString("email", "");
        password = preferences.getString("password", "");
        ShallonCreamerIsATwat = preferences.getString("Token", "");
        ID = preferences.getInt("UserID", -1);

        coords = new LatLng(preferences.getFloat("latitude", 0f), preferences.getFloat("longitude", 0f));
        reminderLength = preferences.getLong("Reminder Length", 1000 * 60 * 30);

        try {
            JSONObject j = new JSONObject(preferences.getString("Reminder", "{}"));
            reminder = new Reminder(j);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public UserData(Activity a) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(a.getBaseContext());

        mMapsActivity = a;
        email = preferences.getString("email", "");
        password = preferences.getString("password", "");
        ShallonCreamerIsATwat = preferences.getString("Token", "");
        ID = preferences.getInt("UserID", -1);

        coords = new LatLng(preferences.getFloat("latitude", 0f), preferences.getFloat("longitude", 0f));
        reminderLength = preferences.getLong("Reminder Length", 1000 * 60 * 30);

        try {
            JSONObject j = new JSONObject(preferences.getString("Reminder", "{}"));
            reminder = new Reminder(j);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public UserData(Context c) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(c);

        email = preferences.getString("email", "");
        password = preferences.getString("password", "");
        ShallonCreamerIsATwat = preferences.getString("Token", "");
        ID = preferences.getInt("UserID", -1);

        coords = new LatLng(preferences.getFloat("latitude", 0f), preferences.getFloat("longitude", 0f));
        reminderLength = preferences.getLong("Reminder Length", 1000 * 60 * 30);

        try {
            L.println(UserData.class, preferences.getString("Reminder", "{s : 0}"));
            JSONObject j = new JSONObject(preferences.getString("Reminder", "{s: 0}"));
            reminder = new Reminder(j);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean isLoggedIn() {
        L.println(getClass(), "ID: " + ID);
        return !(ID == -1);
    }

    public void clear() {

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(mActivity.getBaseContext());


        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();

        editor.apply();

    }

}
