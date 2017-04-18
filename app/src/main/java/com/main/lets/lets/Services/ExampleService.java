package com.main.lets.lets.Services;

import android.app.Service;

/**
 * Created by novosejr on 3/1/2017.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.main.lets.lets.LetsAPI.L;

public class ExampleService extends Service {

    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    try {

                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();

        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}