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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import br.edu.uepb.nutes.haniot.activity.account.LoginActivity;
import br.edu.uepb.nutes.haniot.data.model.UserAccess;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;

/**
 * Service for token expiration.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class TokenExpirationService extends Service {
    private final String TAG = "TokenExpirationService";
    private final IBinder mBinder = new LocalBinder();
    private AppPreferencesHelper appPreferences;

    @Override
    public void onCreate() {
        appPreferences = AppPreferencesHelper.getInstance(getApplicationContext());

        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void eventReceiver(String event) {
        Log.w("TokenExpirationService", "eventReceiver() - " + event);
        if (!event.equalsIgnoreCase("unauthorized")) return;

        if (appPreferences.getUserLogged() != null) appPreferences.removeUserLogged();
        redirectToLogin();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    /**
     * Verify if token is expired.
     *
     * @param expiresAt in milliseconds
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
        UserAccess userAccess = appPreferences.getUserAccessHaniot();
        setScheduler(userAccess.getExpirationDate());
    }

    /**
     * Set scheduler for verify token expiration.
     *
     * @param expireAt in milliseconds
     */
    private void setScheduler(long expireAt) {
        Intent it = new Intent(this, TokenAlarmReceiver.class);
        PendingIntent p = PendingIntent.getBroadcast(this, 0, it, 0);
        AlarmManager tokenAlarm = (AlarmManager) getSystemService(ALARM_SERVICE);
        tokenAlarm.set(AlarmManager.RTC_WAKEUP, expireAt, p);
    }

    /**
     * Redirect to login.
     */
    private void redirectToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getApplicationContext().startActivity(intent);
    }

    /**
     * Class of Local Binder of Token Expiration Service.
     */
    public class LocalBinder extends Binder {
        public TokenExpirationService getService() {
            return TokenExpirationService.this;
        }
    }

    /**
     * Class of Broadcast Receiver of Token Alarm.
     */
    public class TokenAlarmReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            EventBus.getDefault().post("unauthorized");
        }
    }
}
