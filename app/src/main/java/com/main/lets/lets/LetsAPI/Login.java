package com.main.lets.lets.LetsAPI;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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

        try {
            try {
                //Opening the input stream and looks for the file, if it is not there, it will
                //throw a FileNotFoundException
                FileInputStream fis = mActivity.openFileInput(FILENAME);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;

                String[] cred;
                //Reads each line from the file and sets the line string equal to the line  then and
                //appends it onto the string builder
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                //If the file just reads blank, that means the app has previously tried to log in
                // but failed so it created the new file and wrote "blank"

                //If the file does not read blank, then the user's email and password have been
                // saved so it will split the string into a string array containing a separate
                // string for the email and password which then makes the call to log in
                if (!sb.toString().equals("blank")) {
                    cred = sb.toString().split(":");
                    Calls.login(cred[0], cred[1], jsonHttpResponseHandler);

                }

            } catch (FileNotFoundException e) {
                //If no file exists, this code runs creating a new file and writing "blank"
                FileOutputStream fos;
                String string = "blank";
                fos = mActivity.openFileOutput(FILENAME, Context.MODE_PRIVATE);
                fos.write(string.getBytes());
                fos.close();

            }


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Saves user's email and password as a string on the phone so the app can access that data if
     * the app is closed and auto login.  The string is saved in the format: "email:password"
     *
     * @param email    the user's email
     * @param password the user's password
     * @param a        activity used to save the user's credentials to the phone
     */
    public static void saveInfo(String email, String password, Activity a) {
        //putting the two strings together as one
        String string = email + ":" + password;
        FileOutputStream fos;
        try {
            //Writing the string to the file
            fos = a.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        SharedPreferences preferences = a.getPreferences(Context.MODE_PRIVATE);
        preferences.edit().putString("email", email);
        preferences.edit().putString("password", password);
        preferences.edit().commit();

    }

    /**
     * Clears the user's email and password saved on the phone, used for logging out.
     * The file is cleared and replaced with the new text "blank"
     *
     * @param a activity used to write to the phone's data
     */
    public static void clearInfo(Activity a) {
        String string = "blank";
        FileOutputStream fos;
        try {
            fos = a.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        SharedPreferences preferences = a.getPreferences(Context.MODE_PRIVATE);
        preferences.edit().remove("password");
        preferences.edit().remove("email");
        preferences.edit().commit();
    }

}
