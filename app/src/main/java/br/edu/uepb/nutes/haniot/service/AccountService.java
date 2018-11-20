package br.edu.uepb.nutes.haniot.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import br.edu.uepb.nutes.haniot.activity.account.LoginActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;


public class AccountService extends Service {

    //TODO Mudar nome do serviço
    private final String TAG = "Account Service";
    private Handler handler;
    private Runnable runnable;
    private Context context;

    @Override
    public void onCreate() {
        Log.i(TAG, "Token Service - onCreate()");
        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventReceiver(String event) {
        Log.i(TAG, event);

        switch (event) {
            case "Unauthorized":
                tokenExpired();
                break;
            case "403":
                changePassword();
                break;
            default:
                break;
        }

    }

    /**
     * Need change password of account.
     */
    private void changePassword() {
        Log.i(TAG, "Change password");

    }

    /**
     * Start the monitor for token expiration
     */
    private void initTokenMonitor() {
        Log.i(TAG, "Initializing Token Monitor");

    }

    /**
     * When token is expired.
     */
    private void tokenExpired() {
        Log.i(TAG, "Token Expired");
        Session session = new Session(this);
        if (session.removeLogged()) {
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

}
