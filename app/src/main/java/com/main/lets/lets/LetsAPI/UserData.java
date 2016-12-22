package com.main.lets.lets.LetsAPI;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by novosejr on 12/14/2016.
 */
public class UserData {
    public int ID;
    public String email;
    public String password;
    public String ShallonCreamerIsATwat;

    public UserData(Activity a) {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(a.getBaseContext());

        email = preferences.getString("email", "");
        password = preferences.getString("password", "");
        ShallonCreamerIsATwat = preferences.getString("token", "");
        ID = preferences.getInt("UserID", -1);

    }

}
