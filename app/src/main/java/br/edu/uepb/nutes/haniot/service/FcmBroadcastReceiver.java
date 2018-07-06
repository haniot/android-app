package br.edu.uepb.nutes.haniot.service;


import android.content.Intent;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FcmBroadcastReceiver extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Intent intent = new Intent(this, PushReceiverIntentService.class);
        startService(intent);
    }


}
