package br.edu.uepb.nutes.haniot.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.auth0.android.jwt.JWT;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;

import br.edu.uepb.nutes.haniot.activity.account.LoginActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;

/**
 * Service for token expiration.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.6
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class TokenExpirationService extends Service {
    private final String TAG = "TokenExpirationService";
    private final IBinder mBinder = new LocalBinder();
    private Session session;

    /**
     *  OnCreate of service.
     */
    @Override
    public void onCreate() {
        Log.i(TAG, "Token Service - onCreate()");
        session = new Session(this);
        EventBus.getDefault().register(this);
    }

    /**
     *  OnStartCommand of Service.
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "Register event bus");
        return START_STICKY;
    }

    /**
     *  OnDestroy of Service.
     */
    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        EventBus.getDefault().unregister(this);
    }

    /**
     *  Event receiver from Event Bus.
     * @param event
     */
    @Subscribe
    public void eventReceiver(String event) {
        Log.i(TAG, "eventReceiver()");
        Log.i(TAG, event);
        if (session.isLogged()) redirectToLogin();
    }

    /**
     *  onBind service.
     * @param intent
     * @return
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     *  Unbind service.
     * @param intent
     * @return
     */
    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    /**
     *  Verify if token is expired.
     * @param expiresAt
     * @return {@link boolean}
     */
    private boolean isExpired(long expiresAt) {
        long timeActual = System.currentTimeMillis();
        return timeActual >= expiresAt;
    }

    /**
     * Start the monitor for token expiration.
     */
    public void initTokenMonitor() {
        Log.i(TAG, "Initializing Token Monitor");
        Session session = new Session(this);
        JWT jwt = new JWT(session.getTokenLogged());
        long expiresAt = jwt.getExpiresAt().getTime();
        setScheduler(jwt.getExpiresAt());
    }

    /**
     *  Set scheduler for verify token expiration.
     * @param expireAt
     */
    private void setScheduler(Date expireAt) {
        Log.i(TAG, "Verificador de token agendado para: " + expireAt.toString());
        Intent it = new Intent(this, TokenAlarmReceiver.class);
        PendingIntent p = PendingIntent.getBroadcast(this, 0, it, 0);
        long time = expireAt.getTime();
        AlarmManager tokenAlarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        tokenAlarm.set(AlarmManager.RTC_WAKEUP, time, p);
    }

    /**
     *  Redirect to login.
     */
    private void redirectToLogin() {
        Log.i(TAG, "Token Expired");
        if (session.removeLogged()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     *  Class of Local Binder of Token Expiration Service.
     */
    public class LocalBinder extends Binder {
        public TokenExpirationService getService() {
            return TokenExpirationService.this;
        }
    }

    /**
     *  Class of Broadcast Receiver of Token Alarm.
     */
    public class TokenAlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            EventBus.getDefault().post("unauthorized");
        }
    }
}
