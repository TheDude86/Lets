package com.main.lets.lets.LetsAPI;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.View;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rey.material.app.Dialog;
import com.rey.material.widget.TextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;

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
    protected static final String TransferOwnership = "group/transferOwnership";
    protected static final String SendFriendRequest = "user/sendFriendRequest";
    protected static final String InviteGroupToEvent = "event/inviteGroup";
    protected static final String GetCloseEvents = "event/getCloseEvents";
    protected static final String GetProfileByID = "user/getProfileById";
    protected static final String GetGroupComments = "group/getComments";
    protected static final String InviteUserToEvent = "event/inviteUser";
    protected static final String AddGroupComment = "group/addComment";
    protected static final String GroupRemoveUser = "group/removeUser";
    protected static final String GroupInviteUser = "group/inviteUser";
    protected static final String EventAddComment = "event/addComment";
    protected static final String GetEventById = "event/getEventById";
    protected static final String GetMyProfile = "user/getMyProfile";
    protected static final String GetAttended = "user/getAttended";
    protected static final String LoginSecure = "user/loginSecure";
    protected static final String EditProfile = "user/editProfile";
    protected static final String GetGroupInfo = "group/getInfo";
    protected static final String GetFriends = "user/getFriends";
    protected static final String AddCohost = "event/addCohost";
    protected static final String GetGroups = "user/getGroups";
    protected static final String CreateEvent = "event/create";
    protected static final String GroupDelete = "group/delete";
    protected static final String AddAdmin = "group/addAdmin";
    protected static final String LeaveGroup = "group/leave";
    protected static final String SearchName = "search/name";

    protected static AsyncHttpClient client = new AsyncHttpClient();

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
        params.put("search_user_id", id);
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
     * @param id                      user to get friends of
     * @param jsonHttpResponseHandler code to be executed
     */
    public static void getFriends(int id, JsonHttpResponseHandler jsonHttpResponseHandler) {

        RequestParams params = new RequestParams();
        params.put("user_id", id);
        post(GetFriends, params, jsonHttpResponseHandler);

    }

    /**
     * Log in to server to receive an access token.
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

    public static void editProfile(HashMap<String, Object> mUserInfo, String token,
                                   JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("bday", new SimpleDateFormat("MM-dd-yyyy").format(mUserInfo.get("birthday")));
        params.put("interests", mUserInfo.get("interests"));
        params.put("edit_user_id", mUserInfo.get("id"));
        params.put("privacy", mUserInfo.get("privacy"));
        params.put("pic_ref", mUserInfo.get("picRef"));
        params.put("gender", mUserInfo.get("gender"));
        params.put("name", mUserInfo.get("name"));
        params.put("bio", mUserInfo.get("bio"));
        client.addHeader("Authorization", token);
        post(EditProfile, params, jsonHttpResponseHandler);
    }

    public static void createEvent(HashMap<String, String> mMap, String token,
                                   JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("category", mMap.get("Category"));
        params.put("latitude", mMap.get("Latitude"));
        params.put("longitude", mMap.get("Longitude"));
        params.put("location_title", mMap.get("Map Title"));
        params.put("event_name", mMap.get("Title"));
        params.put("description", mMap.get("Description"));
        params.put("end_time", mMap.get("End Time"));
        params.put("start_time", mMap.get("Start Time"));
        client.addHeader("Authorization", token);

        post(CreateEvent, params, jsonHttpResponseHandler);

    }

    public static void getGroupComments(int id, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("group_id", id);
        client.addHeader("Authorization", token);

        post(GetGroupComments, params, jsonHttpResponseHandler);
    }

    public static void addGroupComment(int id, String s, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("group_id", id);
        params.put("message", s);
        client.addHeader("Authorization", token);

        post(AddGroupComment, params, jsonHttpResponseHandler);
    }

    public static void removeUserFromGroup(int userID, int groupID, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("user_to_remove_id", userID);
        params.put("group_id", groupID);
        client.addHeader("Authorization", token);

        post(GroupRemoveUser, params, jsonHttpResponseHandler);
    }

    public static void leaveGroup(int groupID, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("group_id", groupID);
        client.addHeader("Authorization", token);

        post(LeaveGroup, params, jsonHttpResponseHandler);
    }

    public static void transferOwner(int groupID, int newOwnerID, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("group_id", groupID);
        params.put("new_owner_id", newOwnerID);
        client.addHeader("Authorization", token);

        post(TransferOwnership, params, jsonHttpResponseHandler);
    }

    public static void addAdmin(int userID, int groupID, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("admin_id", userID);
        params.put("group_id", groupID);
        client.addHeader("Authorization", token);

        post(AddAdmin, params, jsonHttpResponseHandler);
    }

    public static void joinGroup(int userID, int groupID, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("invite_user", userID);
        params.put("group_id", groupID);
        client.addHeader("Authorization", token);

        post(GroupInviteUser, params, jsonHttpResponseHandler);
    }

    public static void addComment(int eventID, String token, String message, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("event_id", eventID);
        params.put("message", message);
        client.addHeader("Authorization", token);

        post(EventAddComment, params, jsonHttpResponseHandler);
    }

    public static void deleteGroup(int groupID, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("group_id", groupID);
        client.addHeader("Authorization", token);

        post(GroupDelete, params, jsonHttpResponseHandler);
    }

    public static void search(String text, int limit, int offset, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("search_term", text);
        params.put("limit", limit);
        params.put("offset", offset);

        post(SearchName, params, jsonHttpResponseHandler);
    }

    public static void inviteGroupToEvent(int eventID, int groupID, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("event_id", eventID);
        params.put("group_id", groupID);
        client.addHeader("Authorization", token);

        post(InviteGroupToEvent, params, jsonHttpResponseHandler);
    }

    public static void inviteUserToEvent(int eventID, int userID, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("event_id", eventID);
        params.put("invite_user_id", userID);
        client.addHeader("Authorization", token);

        post(InviteUserToEvent, params, jsonHttpResponseHandler);
    }

    public static void sendFriendRequest(int userID, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("reciever_id", userID);
        client.addHeader("Authorization", token);

        post(SendFriendRequest, params, jsonHttpResponseHandler);
    }

    public static void addCohost(int eventID, int userID, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("new_cohost_id", userID);
        params.put("event_id", eventID);
        client.addHeader("Authorization", token);

        post(AddCohost, params, jsonHttpResponseHandler);
    }

    public static void loadImage(String url, FileAsyncHttpResponseHandler fileAsyncHttpResponseHandler) {
        client.get(url, fileAsyncHttpResponseHandler);

    }
}
