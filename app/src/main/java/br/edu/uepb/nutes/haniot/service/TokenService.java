package br.edu.uepb.nutes.haniot.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class TokenService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Subscribe
    public void tokenExpired(String event) {
        Log.i("JWT", event);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        EventBus.getDefault().register(this);
        return START_STICKY;
    }
}
