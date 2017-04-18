package com.main.lets.lets.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.main.lets.lets.Activities.ChatActivity;
import com.main.lets.lets.Activities.MainActivity;
import com.main.lets.lets.LetsAPI.Chat;
import com.main.lets.lets.LetsAPI.EventEntity;
import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.LetsAPI.Reminder;
import com.main.lets.lets.LetsAPI.User;
import com.main.lets.lets.LetsAPI.UserData;
import com.main.lets.lets.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by novosejr on 3/2/2017.
 */

public class ReminderService extends Service {
    HashMap<String, Query> mListeners = new HashMap<>();
    ArrayList<String> blacklist = new ArrayList<>();
    UserData mUserData;
    int ID = 0;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mUserData = new UserData(getBaseContext());

        final User u = new User(mUserData.ID);
        u.load(getApplicationContext(), new User.OnLoadListener() {
            @Override
            public void update() {

                if (u.mEvents.size() > 0) {
                    EventEntity entity = u.mEvents.get(0);
                    Date current = Calendar.getInstance().getTime();

                    for (EventEntity e : u.mEvents) {
                        if (e.mStart.before(entity.mStart) && e.mStart.after(current)) {
                            entity = e;

                        }

                    }

                    if (entity.mStart.after(current)) {
                        SharedPreferences p = PreferenceManager
                                .getDefaultSharedPreferences(getBaseContext());

                        SharedPreferences.Editor edit = p.edit();

                        String s = "{\"Event ID\": %d, \"Event Name\": \"%s\", \"Event Start\": \"%s\", \"Latitude\": -87.10101, \"Longitude\": 39.1003}";
                        s = String.format(s, entity.mID, entity.mText, new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").format(entity.mStart));

                        try {
                            JSONObject j = new JSONObject(s);
                            edit.putString("Reminder", j.toString());
                            edit.apply();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }


                run();

            }
        });

        startChatListener();

        return START_STICKY;
    }

    public void startChatListener() {
        FirebaseApp.initializeApp(getBaseContext());

        String s = String.format("users/%d/chats", new UserData(getBaseContext()).ID);
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child(s);

        ChildEventListener c = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String path = dataSnapshot.getValue(String.class);

                if (blacklist.contains(path))
                    blacklist.remove(path);
                else
                    setNotificationListener(path);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String path = dataSnapshot.getValue(String.class);
                blacklist.add(path);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        db.addChildEventListener(c);

    }

    public void setNotificationListener(final String s) {
        final String[] comps = s.split("/");

        Query q = FirebaseDatabase.getInstance().getReference().child(s)
                .orderByChild("timestamp").startAt(Calendar.getInstance().getTimeInMillis());

        mListeners.put(s, q);

        q.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String str) {

                if (!blacklist.contains(s)) {
                    Chat c = dataSnapshot.getValue(Chat.class);

                    if (mUserData.ID != c.getId()) {

                        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon(R.mipmap.logo)
                                        .setSound(defaultSoundUri)
                                        .setContentTitle("New message in group chat")
                                        .setAutoCancel(true)
                                        .setContentText(c.getAuthor() + ": " + c.getMessage());


                        // Sets an ID for the notification
                        int mNotificationId = Integer.parseInt(comps[1]);
                        // Gets an instance of the NotificationManager service
                        NotificationManager mNotifyMgr =
                                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        Intent intent = new Intent(getBaseContext(), ChatActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("Path", s);

                        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0,
                                intent, PendingIntent.FLAG_ONE_SHOT);

                        mBuilder.setContentIntent(contentIntent);

                        // Builds the notification and issues it.
                        mNotifyMgr.notify(mNotificationId, mBuilder.build());

                    }

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

    public void run() {
        new Thread(new Runnable() {
            public void run() {
                try {

                    mUserData = new UserData(getBaseContext());

                    Calendar current = Calendar.getInstance();
                    long dist = mUserData.reminder.mStart.getTime() - current.getTimeInMillis();

                    current.set(Calendar.HOUR_OF_DAY, 0);
                    current.set(Calendar.MINUTE, 0);
                    current.set(Calendar.SECOND, 0);

                    Date timer = new Date(current.getTimeInMillis() + dist);

                    String hours = new SimpleDateFormat("H").format(timer);
                    String minutes = new SimpleDateFormat("m").format(timer);
                    String s;
                    String title = mUserData.reminder.mName.equals("Untitled Event") ? "You have an event that" : mUserData.reminder.mName;
                    String min = minutes.equals("1") ? "minute" : "minutes";

                    if (hours.equals("0")) {
                        s = String.format("%s starts in %s %s", title, minutes, min);
                    } else {
                        s = String.format("%s starts in %s hours and %s %s", title, hours, minutes, min);
                    }

                    if (dist < mUserData.reminderLength && dist > 0 && ID != mUserData.reminder.mID) {

                        ID = mUserData.reminder.mID;
                        displayNotification(s);

                    } else if (dist <= 0) {

                        SharedPreferences p = PreferenceManager
                                .getDefaultSharedPreferences(getBaseContext());

                        SharedPreferences.Editor edit = p.edit();

                        s = "{}";

                        try {
                            JSONObject j = new JSONObject(s);
                            edit.putString("Reminder", j.toString());
                            edit.apply();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else if (ID == mUserData.reminder.mID && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        NotificationManager mNotifyMgr =
                                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        StatusBarNotification[] notifications = mNotifyMgr.getActiveNotifications();

                        for (StatusBarNotification n : notifications) {
                            if (n.getId() == ID) {

                                displayNotification(s);

                            }
                        }

                    }

                    Thread.sleep(60000);

                    Intent intent = new Intent(getBaseContext(), ReminderService.class);
                    startService(intent);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    public void displayNotification(String s) {

        String uriBegin = "geo:" +
                mUserData.reminder.coords.latitude + "," + mUserData.reminder.coords.longitude;
        String query = mUserData.reminder.coords.latitude + "," + mUserData.reminder.coords.longitude
                + "(" + mUserData.reminder.mName + ")";
        String encodedQuery = Uri.encode(query);
        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
        Uri uri = Uri.parse(uriString);
        Intent intent1 = new Intent(android.content.Intent.ACTION_VIEW, uri);
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0,
                intent1, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.mipmap.logo)
                        .setContentTitle("Upcoming Event")
                        .setContentText(s)
                        .addAction(R.drawable.ic_place_black_24dp1, "Get Directions", contentIntent);


        // Sets an ID for the notification
        int mNotificationId = mUserData.reminder.mID;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


//        mBuilder.setContentIntent(contentIntent);

        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
