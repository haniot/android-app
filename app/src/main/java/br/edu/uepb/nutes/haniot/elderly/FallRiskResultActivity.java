package br.edu.uepb.nutes.haniot.elderly;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.server.Server;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * FallRiskResultActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class FallRiskResultActivity extends AppCompatActivity {
    private final String TAG = "FallRiskResultActivity";

    private final int LOW_RISK = 1;
    private final int MODERATE_RISK = 2;
    private final int HIGH_RISK = 3;

    @BindView(R.id.result_close_button)
    Button mButton;

    @BindView(R.id.fall_risk_result_progressBar)
    ProgressBar mProgressBar;

    @BindView(R.id.loading_send_progressBar)
    ProgressBar mProgressbarBarSend;

    @BindView(R.id.box_layout_fall_risk)
    ConstraintLayout mBoxFallRisk;

    @BindView(R.id.box_loading_fall_risk_result)
    LinearLayout mBoxLoading;

    @BindView(R.id.box_fall_risk_result)
    LinearLayout mBoxResult;

    @BindView(R.id.fall_risk_laoding_status)
    TextView mProgressbarStatus;

    @BindView(R.id.image_fall_risk_result)
    ImageView mImageResult;

    @BindView(R.id.title_fall_risk_result)
    TextView mTitleResult;

    private int progressStatus = 0;
    private Handler handler = new Handler();
    private int assessmentResult;
    private Server server;
    private String elderlyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_fall_risk_survey_result);
        ButterKnife.bind(this);

        server = Server.getInstance(this);

        /**
         * Get data
         */
        Intent it = getIntent();
        String questions[] = it.getStringArrayExtra(FallRiskActivity.EXTRA_QUESTIONS);
        boolean answers[] = it.getBooleanArrayExtra(FallRiskActivity.EXTRA_ANSWERS);
        elderlyId = it.getStringExtra(FallRiskActivity.EXTRA_ELDERLY_ID);

        assessmentResult = calculateResult(answers); // calculate result

        mButton.setOnClickListener((v) -> saveAssessment(questions, answers));

        loading();
    }

    /**
     * Save in server and close actividty.
     *
     * @param questions
     * @param answers
     */
    public void saveAssessment(String[] questions, boolean[] answers) {
        // TODO Relizar proceso de salvar - PERGUNTAS, RESPOSTAS E RESULTADO
        if (elderlyId == null) {
            startActivity(new Intent(getApplicationContext(), ElderlyMonitoredActivity.class));
            finish();
            return;
        }

        mProgressBar.setVisibility(View.VISIBLE);

        // Save in remote server.
        String path = "users/"
                .concat(new Session(this).get_idLogged())
                .concat("/elderlies/")
                .concat(elderlyId)
                .concat("/assessments");
        server.post(path, "{ \"value\": " + assessmentResult + "}", // json
                new Server.Callback() {
                    @Override
                    public void onError(JSONObject result) {
                        runOnUiThread(() -> mProgressBar.setVisibility(View.GONE));
                    }

                    @Override
                    public void onSuccess(JSONObject result) {
                        startActivity(new Intent(getApplicationContext(), ElderlyMonitoredActivity.class));
                        finish();
                    }
                });
    }

    /**
     * Loading result.
     */
    private void loading() {
        int duration = 40; // milliseconds
        int max = 100;

        animateBackgroud((duration * max) + 1000);

        new Thread(() -> {
            while (progressStatus < max) {
                progressStatus += 1;
                // Update the progress bar and display the
                //current value in the text view
                handler.post(() -> {
                    mProgressBar.setProgress(progressStatus);
                    mProgressbarStatus.setText(String.valueOf(progressStatus).concat("%"));
                });

                try {
                    // Sleep for 40 milliseconds x 100 equivalent to 4s of duration.
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Display result
            if (progressStatus >= 100) showDisplayResult();
        }).start();

    }

    /**
     * Duration for animation.
     *
     * @param duration
     */
    private void animateBackgroud(int duration) {
        int colorFrom = ContextCompat.getColor(this, R.color.colorDeepPurple);
        int colorTo = 0;

        if (assessmentResult == LOW_RISK)
            colorTo = ContextCompat.getColor(this, R.color.colorGreen);
        else if (assessmentResult == MODERATE_RISK)
            colorTo = ContextCompat.getColor(this, R.color.colorAmber);
        else
            colorTo = ContextCompat.getColor(this, R.color.colorRed);

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(duration); // milliseconds
        colorAnimation.addUpdateListener(animator -> mBoxFallRisk.setBackgroundColor((int) animator.getAnimatedValue()));
        colorAnimation.start();
    }

    /**
     * Show result
     */
    private void showDisplayResult() {
        runOnUiThread(() -> {
            mBoxLoading.setVisibility(View.GONE);
            Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);

            if (assessmentResult == LOW_RISK) {
                mTitleResult.setText(getString(R.string.risk_fall_title_low));
                mImageResult.setImageResource(R.drawable.elderly_happy);
            } else if (assessmentResult == MODERATE_RISK) {
                mTitleResult.setText(getString(R.string.risk_fall_title_moderate));
                mImageResult.setImageResource(R.drawable.elderly_normal);
            } else {
                mTitleResult.setText(getString(R.string.risk_fall_title_high));
                mImageResult.setImageResource(R.drawable.elderly_sad);
            }

            mBoxResult.setVisibility(View.VISIBLE);
            mBoxResult.startAnimation(animation);
        });
    }

    /**
     * Each positive answer equals 1 point.
     * Only the index 8 response the negative response scores.
     *
     * @param answers
     * @return int
     */
    private int calculateResult(boolean[] answers) {
        int result = 0;

        for (int i = 0; i < answers.length; i++) {
            if (answers[i] && i != 8) {
                result += 1;
            } else if (!answers[i] && i == 8) {
                result += 1;
            }
        }

        /**
         * CLASSIFICATION OF THE RISK:
         *      0 to 2 points - Low Risk of Falls
         *      3 points - Moderate Risk of Falls
         *      4 or more points - High Risk of Falls
         */
        if (result <= 2) return LOW_RISK;
        else if (result == 3) return MODERATE_RISK;
        else return HIGH_RISK;
    }
}
