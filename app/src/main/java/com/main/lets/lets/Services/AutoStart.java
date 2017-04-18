package com.main.lets.lets.Services;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.main.lets.lets.Activities.MainActivity;
import com.main.lets.lets.LetsAPI.L;
import com.main.lets.lets.LetsAPI.Reminder;

/**
 * Created by novosejr on 3/1/2017.
 */

public class AutoStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            if (!isRemindersRunning(context)) {
                Intent i = new Intent(context, ReminderService.class);
                context.startService(i);

            }

        }
    }

    public boolean isRemindersRunning(Context c) {
        ActivityManager manager = (ActivityManager) c.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ReminderService.class.getName().equals(service.service.getClassName())) {
                return true;

            }
        }

        return false;
    }
}