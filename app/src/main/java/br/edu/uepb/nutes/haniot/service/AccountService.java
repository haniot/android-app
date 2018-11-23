package br.edu.uepb.nutes.haniot.service;

import android.app.ActivityManager;
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
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.account.ChangePasswordActivity;
import br.edu.uepb.nutes.haniot.activity.account.LoginActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;


public class AccountService extends Service {
    //TODO Mudar nome do serviço
    private final String TAG = "Account Service";
    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onCreate() {
        Log.i(TAG, "Token Service - onCreate()");
        EventBus.getDefault().register(this);
        //Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy()");
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void eventReceiver(String event) {
        Log.i(TAG, "eventReceiver()");
        Log.i(TAG, event);
        if (event.equals("unauthorized")) redirectToLogin();
        else if (event.equals("403")) {
            changePassword();
        } else if (event.equals("Token Expired")) {
            redirectToLogin();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "Register event bus");
        return START_STICKY;
    }

    public class LocalBinder extends Binder {
        public AccountService getService() {
            return AccountService.this;
        }
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
     * Need change password of account.
     */
    private void changePassword() {
        Log.i(TAG, "Change password");
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        intent.putExtra(String.valueOf(R.string.first_login), true);
        startActivity(intent);
    }

    /**
     *
     */
    private boolean isExpired(long expiresAt) {
        Log.i(TAG, "Token expires in: " + expiresAt);
        long timeActual = System.currentTimeMillis();
        return timeActual >= expiresAt;
    }

    /**
     * Start the monitor for token expiration
     */
    public void initTokenMonitor() {
        //TODO Remover log
        Log.i(TAG, "Initializing Token Monitor");
        if (!EventBus.getDefault().isRegistered(this)) Log.i("JWT", "Event is registered");
        Session session = new Session(this);
        JWT jwt = new JWT(session.getTokenLogged());
        Log.i(TAG, "token: " + session.getTokenLogged());
        long expiresAt = jwt.getExpiresAt().getTime();
        if (isExpired(expiresAt)) redirectToLogin();
        else createScheduler(jwt.getExpiresAt());
    }

    private static final int RQ_SYNC_SERVICE = 1101;

    private void createScheduler(Date expireAt) {
        Log.i(TAG, "Verificador de token agendado para: " + expireAt.toString());
        Intent it = new Intent(this, TokenAlarmReceiver.class);
        PendingIntent p = PendingIntent.getBroadcast(this, 0, it, 0);

        //Teste
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        Log.i("JWT", "Tempo atual: " + c.getTime().toString());
        c.add(Calendar.SECOND, 20);
        Log.i("JWT", "Alarme: " + c.getTime().toString());
        long time = c.getTimeInMillis();


        //long time = expireAt.getTime();


        AlarmManager alarme = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarme.set(AlarmManager.RTC_WAKEUP, time, p);

    }

    /**
     * When token is expired.
     */
    private void redirectToLogin() {
        Log.i(TAG, "Token Expired");
        Session session = new Session(this);
        if (session.removeLogged()) {
            Log.i(TAG, "Deslogando");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}
