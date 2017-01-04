package com.main.lets.lets.Services;

import android.content.Intent;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceIdService;

public class EventReminders extends FirebaseInstanceIdService {



    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }


    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

}
