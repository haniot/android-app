package br.edu.uepb.nutes.haniot.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;


public class TokenAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("JWT", "Intent recebida");
        EventBus.getDefault().post("Token Expired");
    }
}
