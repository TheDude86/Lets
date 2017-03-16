package com.main.lets.lets.Services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.main.lets.lets.LetsAPI.Reminder;
import com.main.lets.lets.R;

/**
 * Created by novosejr on 3/4/2017.
 */

public class AlarmService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

//        Reminder r = intent.getParcelableExtra("Reminder");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.logo)
                        .setContentTitle("My notification")
                        .setContentText("TEST");


        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
