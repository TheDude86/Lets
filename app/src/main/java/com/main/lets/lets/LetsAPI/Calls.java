package com.main.lets.lets.LetsAPI;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.ImageButton;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.main.lets.lets.R;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

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
    protected static final String NOTIFY_URL = "http://lets-app-server.appspot.com/";
    protected static final String RespondToGroupInvite = "user/respondToGroupInvite";
    protected static final String TransferOwnership = "group/transferOwnership";
    protected static final String SendFriendRequest = "user/sendFriendRequest";
    protected static final String GetNotifications = "user/getNotifications";
    protected static final String InviteGroupToEvent = "event/inviteGroup";
    protected static final String GetCloseEvents = "event/getCloseEvents";
    protected static final String GetProfileByID = "user/getProfileById";
    protected static final String GetGroupComments = "group/getComments";
    protected static final String InviteUserToEvent = "event/inviteUser";
    protected static final String GetAdminGroups = "user/getAdminGroups";
    protected static final String AddGroupComment = "group/addComment";
    protected static final String GroupRemoveUser = "group/removeUser";
    protected static final String GroupInviteUser = "group/inviteUser";
    protected static final String EventAddComment = "event/addComment";
    protected static final String RemoveCohost = "event/removeCohost";
    protected static final String GetEventById = "event/getEventById";
    protected static final String GetMyProfile = "user/getMyProfile";
    protected static final String RemoveFriend = "user/removeFriend";
    protected static final String RemoveAdmin = "group/removeAdmin";
    protected static final String GetAttended = "user/getAttended";
    protected static final String LoginSecure = "user/loginSecure";
    protected static final String EditProfile = "user/editProfile";
    protected static final String UnattendEvent = "event/unattend";
    protected static final String RegisterToken = "push/register";
    protected static final String GetGroupInfo = "group/getInfo";
    protected static final String GetFriends = "user/getFriends";
    protected static final String AddCohost = "event/addCohost";
    protected static final String GetGroups = "user/getGroups";
    protected static final String CreateGroup = "group/create";
    protected static final String CreateEvent = "event/create";
    protected static final String GroupDelete = "group/delete";
    protected static final String AddAdmin = "group/addAdmin";
    protected static final String RemoveToken = "push/remove";
    protected static final String LeaveGroup = "group/leave";
    protected static final String SearchName = "search/name";
    protected static final String CreateUser = "user/create";
    protected static final String EditGroup = "group/edit";
    protected static final String EnterCode = "tools/registerEventCode";
    protected static final String DeleteEvent = "event/delete";
    protected static final String DeleteGroupComment = "group/deleteComment";



    protected static final String FCMFriendRequest = "friendRequest";
    protected static final String FCMEventInvite = "eventInvite";
    protected static final String FCMAddedComment = "addedComment";
    protected static final String FCMGroupInvite = "groupInvite";
    protected static final String FCMGroupCommentAdded = "groupCommentAdded";
    protected static final String FCMGroupInvitedToEvent = "groupInvited";



    public Calls() {

    }

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
     * Base method for making network calls.
     *
     * @param url             the extention to be placed on the end of the base URL
     * @param params          paramteters added to the post requests
     * @param responseHandler code to be executed for specific events during the call
     */
    private static void postFCM(String url, RequestParams params,
                             AsyncHttpResponseHandler responseHandler) {
        client.post(getNotifyAbsoluteUrl(url), params, responseHandler);
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
     * build the full URL from the extention.
     *
     * @param relativeUrl
     * @return
     */
    private static String getNotifyAbsoluteUrl(String relativeUrl) {
        return NOTIFY_URL + relativeUrl;
    }

    private static void searchByLocation(double latitude, double longitude, JsonHttpResponseHandler handler) {

        //TODO: add searching other db by location
        handler.onSuccess(0, null, new JSONArray());

    }

    /**
     * Gets all events within a certian range from the given coordinate points.
     *
     * @param latitude                user's latitude
     * @param longitude               user's longitude
     * @param range                   range to search for events
     * @param jsonHttpResponseHandler code to be executed
     */
    public static void getCloseEvents(double latitude, double longitude, int range,
                                      final JsonHttpResponseHandler jsonHttpResponseHandler) {

        searchByLocation(latitude, longitude, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                //TODO: convert JSON event array
                final ArrayList<Integer> events = new ArrayList<>();
                final JSONArray eventList = new JSONArray();
                events.add(224);
                events.add(227);


                Query ref = FirebaseDatabase.getInstance().getReference().child("events")
                        .orderByChild("ID");


                for (int i = 0; i < events.size(); i++) {
                    int id = events.get(i);

                    Query eventQuery = ref.equalTo(id);

                    eventQuery.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Event e = dataSnapshot.getValue(Event.class);
                            eventList.put(e.toJSON());
                            L.println(Calls.class, e.getID() + " TEst");

                            if (eventList.length() == events.size()) {
                                jsonHttpResponseHandler.onSuccess(200, null, eventList);
                            }

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }


            }
        });

    }

    public abstract class LetsResponseListener {

        public void onChildAdded() {}
        public void onChildChanged() {}
        public void onChildRemoved() {}
        public void onChildMoved() {}
        public void onChildCancelled() {}

    }

    /**
     * Get event's info with the given event ID.
     *
     * @param id                      event ID
     * @param jsonHttpResponseHandler code to be executed
     */
    public static void getEvent(int id, final JsonHttpResponseHandler jsonHttpResponseHandler) {

        Query ref = FirebaseDatabase.getInstance().getReference().child("events")
                .orderByChild("ID").equalTo(id);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Event e = dataSnapshot.getValue(Event.class);
                jsonHttpResponseHandler.onSuccess(200, null, e.toJSON());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
        params.put("include_counts", true);
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
        params.put("include_counts", true);
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

        SimpleDateFormat s = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        s.setTimeZone(TimeZone.getTimeZone("UTC"));

        RequestParams params = new RequestParams();
        params.put("bday", s.format(mUserInfo.get("birthday")));
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

        TimeZone tz = TimeZone.getTimeZone("GMT");

        SimpleDateFormat s = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.US);
        s.setTimeZone(tz);

        RequestParams params = new RequestParams();
        params.put("category", mMap.get("Category"));
        params.put("latitude", mMap.get("Latitude"));
        params.put("longitude", mMap.get("Longitude"));
        params.put("location_title", mMap.get("Map Title"));
        params.put("event_name", mMap.get("Title"));
        params.put("description", mMap.get("Description"));
        params.put("end_time", s.format(new Date(Long.parseLong(mMap.get("End Time")))));
        params.put("start_time", s.format(new Date(Long.parseLong(mMap.get("Start Time")))));
        params.put("hidden", mMap.get("Hidden"));

        client.addHeader("Authorization", token);

        post(CreateEvent, params, jsonHttpResponseHandler);

    }

    public static void getGroupComments(int id, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("group_id", id);
        client.addHeader("Authorization", token);

        post(GetGroupComments, params, jsonHttpResponseHandler);
    }

    public static void addGroupComment(int id, String s, UserData d, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("group_id", id);
        params.put("message", s);
        client.addHeader("Authorization", d.ShallonCreamerIsATwat);

        groupCommentFCM(d.ID, id, new JsonHttpResponseHandler());
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

    public static void joinGroup(int userID, int groupID, UserData d, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("invite_user", userID);
        params.put("group_id", groupID);
        client.addHeader("Authorization", d.ShallonCreamerIsATwat);

        if (d.ID != userID)
            groupInviteFCM(d.ID, userID, groupID, new JsonHttpResponseHandler());

        post(GroupInviteUser, params, jsonHttpResponseHandler);
    }

    public static void addComment(int eventID, UserData d, String message, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("event_id", eventID);
        params.put("message", message);
        client.addHeader("Authorization", d.ShallonCreamerIsATwat);

        addedCommentFCM(d.ID, eventID, new JsonHttpResponseHandler());
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

    public static void inviteGroupToEvent(int eventID, int groupID, UserData d, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("event_id", eventID);
        params.put("group_id", groupID);
        client.addHeader("Authorization", d.ShallonCreamerIsATwat);

        groupInvitedToEventFCM(d.ID, groupID, eventID, new JsonHttpResponseHandler());
        post(InviteGroupToEvent, params, jsonHttpResponseHandler);
    }

    public static void inviteUserToEvent(int eventID, int userID, UserData d, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("event_id", eventID);
        params.put("invite_user_id", userID);
        client.addHeader("Authorization", d.ShallonCreamerIsATwat);

        if (d.ID != userID)
            eventInviteFCM(d.ID, userID, eventID, new JsonHttpResponseHandler());

        post(InviteUserToEvent, params, jsonHttpResponseHandler);
    }

    public static void sendFriendRequest(int userID, UserData d, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("recieverId", userID);
        client.addHeader("Authorization", d.ShallonCreamerIsATwat);

        friendRequestFCM(d.ID, userID, new JsonHttpResponseHandler());
        post(SendFriendRequest, params, jsonHttpResponseHandler);
    }

    public static void addCohost(int eventID, int userID, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("new_cohost_id", userID);
        params.put("event_id", eventID);
        client.addHeader("Authorization", token);

        post(AddCohost, params, jsonHttpResponseHandler);
    }

    public static void removeFriend(int userID, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("remove_user_id", userID);
        client.addHeader("Authorization", token);

        post(RemoveFriend, params, jsonHttpResponseHandler);
    }

    public static void getAdminGroups(int userID, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("search_user_id", userID);
        client.addHeader("Authorization", token);

        post(GetAdminGroups, params, jsonHttpResponseHandler);
    }

    public static void removeCohost(int userID, int eventID, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("cohost_id", userID);
        params.put("event_id", eventID);
        client.addHeader("Authorization", token);

        post(RemoveCohost, params, jsonHttpResponseHandler);
    }

    public static void createUser(String email, String password, String name, String birthday, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", password);
        params.put("name", name);
        params.put("bday", birthday);
        params.put("pic_ref", "http://www.oldpotterybarn.co.uk/wp-content/uploads/2015/06/default-medium.png");

        post(CreateUser, params, jsonHttpResponseHandler);
    }

    public static void getNotifications(String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        client.addHeader("Authorization", token);

        post(GetNotifications, params, jsonHttpResponseHandler);
    }

    public static void leaveEvent(int eventID, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("event_id", eventID);
        client.addHeader("Authorization", token);

        post(UnattendEvent, params, jsonHttpResponseHandler);
    }

    public static void createGroup(String title, String bio, boolean any, String picRef, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("group_name", title);
        params.put("pic_ref", picRef);
        params.put("public", any);
        params.put("bio", (bio.equals("")) ? "Bio" : bio);
        client.addHeader("Authorization", token);

        post(CreateGroup, params, jsonHttpResponseHandler);
    }

    public static void editGroup(int groupID, String title, String bio, boolean any, boolean hidden, String picRef, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("group_name", title);
        params.put("group_id", groupID);
        params.put("pic_ref", picRef);
        params.put("hidden", hidden);
        params.put("settings", 0);
        params.put("public", any);
        params.put("bio", bio);
        client.addHeader("Authorization", token);

        post(EditGroup, params, jsonHttpResponseHandler);
    }

    public static void respondToGroupInvite(int groupID, boolean status, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("group_id", groupID);
        params.put("status", status);
        client.addHeader("Authorization", token);

        post(RespondToGroupInvite, params, jsonHttpResponseHandler);
    }

    public static void removeAdmin(int adminID, int groupID, String token, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("group_id", groupID);
        params.put("admin_id", adminID);
        client.addHeader("Authorization", token);

        post(RemoveAdmin, params, jsonHttpResponseHandler);
    }

    public static void registerToken(String FCMToken, UserData d, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("token", FCMToken);
        params.put("active", true);
        client.addHeader("Authorization", d.ShallonCreamerIsATwat);

        post(RegisterToken, params, jsonHttpResponseHandler);
    }

    public static void removeToken(String FCMToken, UserData d, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("token", FCMToken);
        client.addHeader("Authorization", d.ShallonCreamerIsATwat);

        post(RemoveToken, params, jsonHttpResponseHandler);
    }

    public static void enterCode(String code, UserData d, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("code", code);
        client.addHeader("Authorization", d.ShallonCreamerIsATwat);

        post(EnterCode, params, jsonHttpResponseHandler);
    }

    public static void deleteEvent(UserData d, int eventID, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("event_id", eventID);

        client.addHeader("Authorization", d.ShallonCreamerIsATwat);

        post(DeleteEvent, params, jsonHttpResponseHandler);
    }

    public static void deleteGroupComment(UserData d, int groupID, int commentID, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("comment_id", commentID);
        params.put("group_id", groupID);

        client.addHeader("Authorization", d.ShallonCreamerIsATwat);

        post(DeleteGroupComment, params, jsonHttpResponseHandler);
    }












    public static void friendRequestFCM(int sender, int reciever, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("sender", sender);
        params.put("reciever", reciever);

        postFCM(FCMFriendRequest, params, jsonHttpResponseHandler);
    }

    public static void eventInviteFCM(int sender, int reciever, int event, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("sender", sender);
        params.put("reciever", reciever);
        params.put("event", event);

        postFCM(FCMEventInvite, params, jsonHttpResponseHandler);
    }

    public static void addedCommentFCM(int sender, int event, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("sender", sender);
        params.put("event", event);

        postFCM(FCMAddedComment, params, jsonHttpResponseHandler);
    }

    public static void groupInviteFCM(int sender, int reciever, int groupID, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("sender", sender);
        params.put("reciever", reciever);
        params.put("group", groupID);

        postFCM(FCMGroupInvite, params, jsonHttpResponseHandler);
    }

    public static void groupCommentFCM(int sender, int groupID, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("sender", sender);
        params.put("group", groupID);

        postFCM(FCMGroupCommentAdded, params, jsonHttpResponseHandler);
    }

    public static void groupInvitedToEventFCM(int sender, int groupID, int eventID, JsonHttpResponseHandler jsonHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("sender", sender);
        params.put("group", groupID);
        params.put("event", eventID);

        postFCM(FCMGroupInvitedToEvent, params, jsonHttpResponseHandler);
    }













    public static final String storageConnectionString = "DefaultEndpointsProtocol=http;"
            + "AccountName=let;" + "AccountKey=F+XpQRVgmHRaS9daL1/0TH8e0n6jsHU2cdmdhr4PeL5ayYOspWS5VMHgdm3OjCUnWBr9KMfz07LyjHg2iBJcjw==";

    public static void uploadImage(Bitmap image, Context context, String s, UploadImage.onFinished finished){
        new UploadImage(context, image, s, finished).execute();

    }

    public static class UploadImage extends AsyncTask<Void, Void, Void>{
        public onFinished mOnFinished;
        Context mContext;
        String mFileName;
        Bitmap mImage;


        public UploadImage(Context c, Bitmap b, String name, onFinished finished){
            mOnFinished = finished;
            mFileName = name;
            mContext = c;
            mImage = b;

        }


        @Override
        protected Void doInBackground(Void... params) {
            try
            {
                // Retrieve storage account from connection-string.
                CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);

                // Create the blob client.
                CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

                // Get a reference to a container.
                // The container name must be lower case
                CloudBlobContainer container = blobClient.getContainerReference("mycontainer");

                CloudBlockBlob blob = container.getBlockBlobReference(mFileName);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                mImage.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmap = bos.toByteArray();
                ByteArrayInputStream bs = new ByteArrayInputStream(bitmap);


                blob.upload(bs, bitmap.length);

            }
            catch (Exception e)
            {
                // Output the stack trace.
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (mOnFinished != null) {
                mOnFinished.onFinished();
            }

            super.onPostExecute(result);
        }

        public interface onFinished {
            void onFinished();
        }

    }

    public static class Notify extends AsyncTask<Void, Void, Void>{
        String ShallonCreamerIsATwat;
        ImageButton mImageButton;
        boolean mNotify = false;
        Activity mActivity;


        public Notify(Activity a, ImageButton imageButton){
            mImageButton = imageButton;
            mActivity = a;

            final SharedPreferences preferences = PreferenceManager
                    .getDefaultSharedPreferences(mActivity);


            ShallonCreamerIsATwat = preferences.getString("Token", "");

            getNotifications(ShallonCreamerIsATwat, new JsonHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        JSONArray friends = response.getJSONArray("friends");
                        JSONArray events = response.getJSONArray("events");
                        JSONArray groups = response.getJSONArray("groups");



                        int id = preferences.getInt("UserID", -1);

                        for (int i = 0; i < friends.length(); i++) {
                            if(!friends.getJSONObject(i).getBoolean("status") && friends.getJSONObject(i).getInt("sender") != id) {
                                mImageButton.setImageResource(R.drawable.ic_notifications_black_24dp);
                                mNotify = true;
                            }
                        }

                        for (int i = 0; i < events.length(); i++){
                            if (!events.getJSONObject(i).getBoolean("status")) {
                                mImageButton.setImageResource(R.drawable.ic_notifications_black_24dp);
                                mNotify = true;
                            }
                        }

                        for (int i = 0; i < groups.length(); i++){
                            if (!groups.getJSONObject(i).getBoolean("status")) {
                                mImageButton.setImageResource(R.drawable.ic_notifications_black_24dp);
                                mNotify = true;
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });

        }


        @Override
        protected Void doInBackground(Void... params) {

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (!mNotify)
                new Notify(mActivity, mImageButton).execute();

            super.onPostExecute(result);
        }

        public interface onFinished {
            void onFinished();
        }

    }

}
