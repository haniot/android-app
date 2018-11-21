package br.edu.uepb.nutes.haniot.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.auth0.android.jwt.JWT;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.account.ChangePasswordActivity;
import br.edu.uepb.nutes.haniot.activity.account.LoginActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;


public class AccountService extends Service {
    //TODO Mudar nome do serviço
    private final String TAG = "Account Service";

    @Override
    public void onCreate() {
        Log.i(TAG, "Token Service - onCreate()");
        //Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventReceiver(String event) {
        Log.i(TAG, "eventReceiver()");
        Log.i(TAG, event);
        if (event.equals("token expired")) tokenExpired();
        else if (event.equals("403")) changePassword();
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
     * Start the monitor for token expiration
     */
    private void initTokenMonitor() {
        Log.i(TAG, "Initializing Token Monitor");
        Session session = new Session(this);
        JWT jwt = new JWT(session.getTokenLogged());
        Log.i(TAG, "token: "+session.getTokenLogged());
        if (jwt.getExpiresAt() == null) Log.i(TAG, "token expiration null");
        long expiresAt = jwt.getExpiresAt().getTime();
        Log.i(TAG, "Token expires in: "+expiresAt);
        long timeActual = System.currentTimeMillis();

        if (timeActual >= expiresAt) tokenExpired();
        createScheduler(timeActual, expiresAt);
    }

    private void createScheduler(long actual, long expireAt) {

    }

    /**
     * When token is expired.
     */
    private void tokenExpired() {
        Log.i(TAG, "Token Expired");
        Session session = new Session(this);
        if (session.removeLogged()) {
            Log.i(TAG, "Deslogando");
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "Register event bus");
        EventBus.getDefault().register(this);
        //initTokenMonitor();
        return START_STICKY;
    }

    public void startTokenMonitor(String token) {
        Log.i(TAG, "Monitorando o token: " + token);
    }

    public class LocalBinder extends Binder {
        public AccountService getService() {
            return AccountService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }
}
