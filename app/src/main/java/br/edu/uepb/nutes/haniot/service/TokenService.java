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

import br.edu.uepb.nutes.haniot.activity.account.ChangePasswordActivity;
import br.edu.uepb.nutes.haniot.activity.account.LoginActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;


public class TokenService extends Service {

    //TODO Mudar nome do serviço
    private Handler handler;
    private Runnable runnable;
    private Context context;

    @Override
    public void onCreate() {
        Log.i("JWT", "Token Service - onCreate()");
        Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();

//        context = this;
//        handler = new Handler();
//        runnable = new Runnable() {
//            public void run() {
//                Toast.makeText(context, "Service is still running", Toast.LENGTH_LONG).show();
//                handler.postDelayed(runnable, 10000);
//            }
//        };
    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        //handler.removeCallbacks(runnable);
        EventBus.getDefault().unregister(this);

        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void tokenExpired(String event) {
        Log.i("JWT", event);
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
        Log.i("JWT", "Register event bus");
        EventBus.getDefault().register(this);
        return START_STICKY;
    }

    public void startTokenMonitor(String token){
        Log.i("JWT", "Monitorando o token: "+token);
    }

    public class LocalBinder extends Binder {
        public TokenService getService() {
            return TokenService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
