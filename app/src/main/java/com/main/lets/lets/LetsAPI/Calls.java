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
    /**
     * This class doesn't have any constructors and only has static methods.  It is used to shorten
     * code in other classes and also place all of the URLs in the same place in the code so if any
     * links get updated then it is easy to update the client.
     */
    protected static final String BASE_URL = "http://letsapi.azurewebsites.net/";
    protected static final String GetCloseEvents = "event/getCloseEvents";
    protected static final String GetProfileByID = "user/getProfileById";
    protected static final String GetEventById = "event/getEventById";
    protected static final String GetMyProfile = "user/getMyProfile";
    protected static final String GetAttended = "user/getAttended";
    protected static final String LoginSecure = "user/loginSecure";
    protected static final String GetGroupInfo = "group/getInfo";
    protected static final String GetFriends = "user/getFriends";

    protected static AsyncHttpClient client = new AsyncHttpClient();
    protected static final String GetGroups = "user/getGroups";

    /**
     * Base method for making network calls.
     *
     * @param url             the extention to be placed on the end of the base URL
     * @param params          paramteters added to the post requests
     * @param responseHandler code to be executed for specific events during the call
     */
    private static void post(String url, RequestParams params,
                             AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    /**
     * build the full URL from the extention.
     *
     * @param relativeUrl
     * @return
     */
    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }

    /**
     * Gets all events within a certian range from the given coordinate points.
     *
     * @param latitude                user's latitude
     * @param longitude               user's longitude
     * @param range                   range to search for events
     * @param jsonHttpResponseHandler code to be executed
     */
    public static void getCloseEvents(int latitude, int longitude, int range,
                                      JsonHttpResponseHandler jsonHttpResponseHandler) {

        RequestParams params = new RequestParams();
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("range", range);
        post(GetCloseEvents, params, jsonHttpResponseHandler);

    }

    /**
     * Get event's info with the given event ID.
     *
     * @param id                      event ID
     * @param jsonHttpResponseHandler code to be executed
     */
    public static void getEvent(int id, JsonHttpResponseHandler jsonHttpResponseHandler) {

        RequestParams params = new RequestParams();
        params.put("event_id", id);
        post(GetEventById, params, jsonHttpResponseHandler);


    }

    /**
     * Gets a user's info with a given user ID.
     *
     * @param id                      user ID
     * @param token                   access token
     * @param jsonHttpResponseHandler code to be executed
     */
    public static void getProfileByID(int id, String token,
                                      JsonHttpResponseHandler jsonHttpResponseHandler) {

        RequestParams params = new RequestParams();
        client.addHeader("Authorization", token);
        params.put("user_id", id);
        post(GetProfileByID, params, jsonHttpResponseHandler);

    }

    /**
     * Get a list of group short hands that the user is a part of.
     *
     * @param id                      user ID
     * @param token                   access token
     * @param jsonHttpResponseHandler code to be executed
     */
    public static void getGroups(int id, String token,
                                 JsonHttpResponseHandler jsonHttpResponseHandler) {

        RequestParams params = new RequestParams();
        params.put("user_id", id);
        client.addHeader("Authorization", token);
        post(GetGroups, params, jsonHttpResponseHandler);

    }

    /**
     * Get a list of event short hands that the user is attending or has attended.
     *
     * @param id                      user ID
     * @param token                   access token
     * @param jsonHttpResponseHandler code to be executed
     */
    public static void getAttended(int id, String token,
                                   JsonHttpResponseHandler jsonHttpResponseHandler) {

        RequestParams params = new RequestParams();
        params.put("get_user_id", id);
        client.addHeader("Authorization", token);
        post(GetAttended, params, jsonHttpResponseHandler);

    }

    /**
     * Get a list of friends short hands that the user is friends with.
     *
     * @param token                   access token
     * @param jsonHttpResponseHandler code to be executed
     */
    public static void getFriends(String token, JsonHttpResponseHandler jsonHttpResponseHandler) {

        RequestParams params = new RequestParams();
        client.addHeader("Authorization", token);
        post(GetFriends, params, jsonHttpResponseHandler);

    }

    /**
     * Log in to server to recieve an access token.
     *
     * @param email
     * @param password
     * @param jsonHttpResponseHandler
     */
    public static void login(String email, String password,
                             JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", password);
        post(LoginSecure, params, jsonHttpResponseHandler);

    }

    /**
     * Get the logged in user's profile info
     *
     * @param token                   access token
     * @param jsonHttpResponseHandler code to be executed
     */
    public static void getMyProfile(String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        client.addHeader("Authorization", token);
        post(GetMyProfile, params, jsonHttpResponseHandler);
    }

    /**
     * Get a group's info from its ID.
     *
     * @param id                      group ID
     * @param jsonHttpResponseHandler code to be executed
     */
    public static void getGroupInfo(int id, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("group_id", id);
        post(GetGroupInfo, params, jsonHttpResponseHandler);

    }

}
