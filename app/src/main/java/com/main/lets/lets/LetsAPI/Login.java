package com.main.lets.lets.LetsAPI;

import android.app.Activity;
import android.content.Context;
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
    protected static AsyncHttpClient client = new AsyncHttpClient();
    protected static final String BASE_URL = "http://letsapi.azurewebsites.net/";
    private HashMap<String, String> mInfo;
    private Activity mActivity;

    public Login(Activity a, JsonHttpResponseHandler jsonHttpResponseHandler) {
        super();
        mActivity = a;
        mInfo = new HashMap<>();

        try {
            try {
                FileInputStream fis = mActivity.openFileInput(FILENAME);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;

                String[] cred;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                if (!sb.toString().equals("blank")) {
                    cred = sb.toString().split(":");
                    Calls.login(cred[0], cred[1], jsonHttpResponseHandler);
                    mInfo.put("password", cred[1]);
                    mInfo.put("email", cred[0]);

                }

            } catch (FileNotFoundException e) {
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

    public static void saveInfo(String email, String password, Activity a) {
        String string = email + ":" + password;
        FileOutputStream fos;
        try {
            fos = a.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void clearInfo(Activity a){
        String string = "blank";
        FileOutputStream fos;
        try {
            fos = a.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(string.getBytes());
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getInfo() {
        return mInfo;
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

}
