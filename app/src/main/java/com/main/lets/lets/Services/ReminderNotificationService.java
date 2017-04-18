package com.main.lets.lets.Services;

import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.main.lets.lets.LetsAPI.EventEntity;
import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.LetsAPI.User;
import com.main.lets.lets.LetsAPI.UserData;
import com.main.lets.lets.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by novosejr on 3/5/2017.
 */

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class ReminderNotificationService extends NotificationListenerService {

    UserData mUserData;
    int ID = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {

        }

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mUserData = new UserData(getBaseContext());

        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getBaseContext());


        if (mUserData.reminder.mID == 0) {

            final User u = new User(mUserData.ID);
            u.load(getApplicationContext(), new User.OnLoadListener() {
                @Override
                public void update() {

                    if (u.mEvents.size() > 0) {
                        EventEntity entity = u.mEvents.get(0);
                        Date current = Calendar.getInstance().getTime();

                        for (EventEntity e: u.mEvents) {
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

        } else {
            run();
        }


        return START_STICKY;
    }

    public void run() {
        new Thread(new Runnable() {
            public void run() {
                try {

                    mUserData = new UserData(getBaseContext());

                    Calendar current = Calendar.getInstance();
                    long dist = mUserData.reminder.mStart.getTime() - current.getTimeInMillis();

                    if (dist < mUserData.reminderLength && dist > 0 && ID != mUserData.reminder.mID) {

                        current.set(Calendar.HOUR_OF_DAY, 0);
                        current.set(Calendar.MINUTE, 0);
                        current.set(Calendar.SECOND, 0);
                        ID = mUserData.reminder.mID;


                        Date timer = new Date(current.getTimeInMillis() + dist);


                        String hours = new SimpleDateFormat("H").format(timer);
                        String minutes = new SimpleDateFormat("m").format(timer);
                        String s;
                        String title = mUserData.reminder.mName.equals("Untitled Event") ? "You have an event that" : mUserData.reminder.mName;

                        if (hours.equals("0"))
                            s = String.format("%s starts in %s minutes", title, minutes);
                        else
                            s = String.format("%s starts in %s hours and %s minutes", title, hours, minutes);


                        String uriBegin = "geo:" +
                                mUserData.reminder.coords.latitude + "," + mUserData.reminder.coords.longitude;
                        String query = mUserData.reminder.coords.latitude + "," + mUserData.reminder.coords.longitude
                                + "(" + mUserData.reminder.mName + ")";
                        String encodedQuery = Uri.encode(query);
                        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                        Uri uri = Uri.parse(uriString);
                        Intent intent1 = new Intent(android.content.Intent.ACTION_VIEW, uri);

                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(getApplicationContext())
                                        .setSmallIcon(R.mipmap.logo)
                                        .setContentTitle("Upcoming Event")
                                        .setContentText(s)
                                        .addAction(R.drawable.ic_place_black_24dp1, "Get Directions", null);


                        // Sets an ID for the notification
                        int mNotificationId = mUserData.reminder.mID;
                        // Gets an instance of the NotificationManager service
                        NotificationManager mNotifyMgr =
                                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                        // Builds the notification and issues it.
                        mNotifyMgr.notify(mNotificationId, mBuilder.build());

                    } else if (dist <= 0) {

                        SharedPreferences p = PreferenceManager
                                .getDefaultSharedPreferences(getBaseContext());

                        SharedPreferences.Editor edit = p.edit();

                        String s = "{}";

                        try {
                            JSONObject j = new JSONObject(s);
                            edit.putString("Reminder", j.toString());
                            edit.apply();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    Thread.sleep(3000);

                    Intent intent = new Intent(getBaseContext(), ReminderService.class);
                    startService(intent);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
