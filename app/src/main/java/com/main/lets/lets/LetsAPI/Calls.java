package com.main.lets.lets.LetsAPI;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rey.material.app.Dialog;
import com.rey.material.widget.TextView;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jnovosel on 6/24/16.
 */
public class Calls {
    protected static final String GetEventById = "event/getEventById";
    protected static final String GetCloseEvents = "event/getCloseEvents";
    protected static final String GetProfileByID = "user/getProfileById";
    protected static final String GetGroups = "user/getGroups";
    protected static final String GetAttended = "user/getAttended";
    protected static AsyncHttpClient client = new AsyncHttpClient();
    protected static final String BASE_URL = "http://letsapi.azurewebsites.net/";

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }


    public static void getCloseEvents(int latitude, int longitude, int range,
                                      JsonHttpResponseHandler jsonHttpResponseHandler){

        RequestParams params = new RequestParams();
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("range", range);
        post(GetCloseEvents, params, jsonHttpResponseHandler);

    }


    public static void getEvent(int id, JsonHttpResponseHandler jsonHttpResponseHandler){

        RequestParams params = new RequestParams();
        params.put("event_id", id);
        post(GetEventById, params, jsonHttpResponseHandler);


    }

    public static void getProfileByID(int id, String token, JsonHttpResponseHandler jsonHttpResponseHandler){

        RequestParams params = new RequestParams();
        client.addHeader("Authorization", token);
        params.put("user_id", id);
        post(GetProfileByID, params, jsonHttpResponseHandler);

    }

    public static void getGroups(int id, String token, JsonHttpResponseHandler jsonHttpResponseHandler){

        RequestParams params = new RequestParams();
        params.put("user_id", id);
        client.addHeader("Authorization", token);
        post(GetGroups, params, jsonHttpResponseHandler);

    }

    public static void getAttended(int id, String token, JsonHttpResponseHandler jsonHttpResponseHandler){

        RequestParams params = new RequestParams();
        params.put("get_user_id", id);
        client.addHeader("Authorization", token);
        post(GetAttended, params, jsonHttpResponseHandler);

    }

    public static void getFriends(String token, JsonHttpResponseHandler jsonHttpResponseHandler){

        RequestParams params = new RequestParams();
        client.addHeader("Authorization", token);
        post("user/getFriends", params, jsonHttpResponseHandler);

    }

}
