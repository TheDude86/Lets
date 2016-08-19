package com.main.lets.lets.LetsAPI;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jnovosel on 6/17/16.
 */
public class Login {
    public static final String FILENAME = "userInfo";
    private Activity mActivity;

    /**
     * The login class is used to automatically login when the user opens the app and save user
     * credentials to their phone if they log in successfully.
     *
     * @param a                       Activity used for reading and writing to files on the phone
     * @param jsonHttpResponseHandler code to be executed once the login call is made
     */
    public Login(Activity a, JsonHttpResponseHandler jsonHttpResponseHandler) {
        super();
        //Initializing global variable
        mActivity = a;



    }


    /**
     * Clears the user's email and password saved on the phone, used for logging out.
     * The file is cleared and replaced with the new text "blank"
     *
     * @param a activity used to write to the phone's data
     */
    public static void clearInfo(Context a) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(a);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

}
