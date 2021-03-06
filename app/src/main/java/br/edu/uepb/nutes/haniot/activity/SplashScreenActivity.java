package br.edu.uepb.nutes.haniot.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import org.json.JSONObject;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.account.LoginActivity;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.server.SynchronizationServer;
import br.edu.uepb.nutes.haniot.service.TokenExpirationService;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.edu.uepb.nutes.haniot.data.model.UserType.ADMIN;
import static br.edu.uepb.nutes.haniot.data.model.UserType.NUTRITION;
import static br.edu.uepb.nutes.haniot.data.model.UserType.PATIENT;

/**
 * Activity SplashScreenActivity.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class SplashScreenActivity extends AppCompatActivity {
    @BindView(R.id.text_view_app_name)
    TextView appNameTextView;

    @BindView(R.id.title_desc_textview)
    TextView appNameDescTextView;

    private AppPreferencesHelper appPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);

        appPreference = AppPreferencesHelper.getInstance(this);

        // TODO for test
//        appPreference.removeLastPilotStudy();
//        appPreference.getUserLogged().setUserType(PATIENT);
        //
        startService(new Intent(this, TokenExpirationService.class));

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        appNameTextView.startAnimation(animation);
        appNameDescTextView.startAnimation(animation);
    }

    @Override
    protected void onStart() {
        super.onStart();

        openActivity();
//        SynchronizationServer.getInstance(this).run(new SynchronizationServer.Callback() {
//            @Override
//            public void onError(JSONObject result) {
//            }
//
//            @Override
//            public void onSuccess(JSONObject result) {
//                openActivity();
//            }
//        });
    }

    private void openActivity() {
        if (appPreference.getUserLogged() == null) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        } else {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }
}
