package br.edu.uepb.nutes.haniot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import org.json.JSONObject;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.account.LoginActivity;
import br.edu.uepb.nutes.haniot.model.dao.SynchronizationServer;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity SplashScreenActivity.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class SplashScreenActivity extends AppCompatActivity {
    private final String TAG = "SplashScreenActivity";

    @BindView(R.id.text_view_app_name)
    TextView appNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);

        Animation animationStart = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        appNameTextView.startAnimation(animationStart);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new SynchronizationServer(this).run(new SynchronizationServer.Callback() {
            @Override
            public void onError(JSONObject result) {
                Log.i(TAG, "onError()");
                if (result != null) Log.i(TAG, result.toString());
                openLoginActivity();
            }

            @Override
            public void onSuccess(JSONObject result) {
                if (result != null) Log.i(TAG, "onSuccess()" + result.toString());
                openLoginActivity();
            }
        });
    }

    private void openLoginActivity() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
}
