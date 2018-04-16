package br.edu.uepb.nutes.haniot.elderly.assessment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.github.paolorotolo.appintro.AppIntro;

import br.edu.uepb.nutes.haniot.R;

/**
 * FallRiskActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class FallRiskActivity extends AppIntro implements SliderPagerFragment.OnResponseListener {
    private final String TAG = "FallRiskActivity";
    private Boolean responseQ1;
    private Boolean responseQ2;
    private Boolean responseQ3;
    private Boolean responseQ4;
    private Boolean responseQ5;
    private Boolean responseQ6;
    private Boolean responseQ7;
    private Boolean responseQ8;
    private Boolean responseQ9;
    private Boolean responseQ10;
    private int currentQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setFadeAnimation();
        showSkipButton(true);
        setColorTransitionsEnabled(true);
        setSkipText("Cancelar");

        setImmersiveMode(true);
        addSlide(SliderPagerFragment.newInstance(this, R.layout.fragment_elderly_fall_risk_q1));
        addSlide(SliderPagerFragment.newInstance(this, R.layout.fragment_elderly_fall_risk_q2));
        addSlide(SliderPagerFragment.newInstance(this, R.layout.fragment_elderly_fall_risk_q3));
        addSlide(SliderPagerFragment.newInstance(this, R.layout.fragment_elderly_fall_risk_q4));
        addSlide(SliderPagerFragment.newInstance(this, R.layout.fragment_elderly_fall_risk_q4));
        addSlide(SliderPagerFragment.newInstance(this, R.layout.fragment_elderly_fall_risk_q4));
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentQuestion = 1;
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Log.d(TAG, "onSkipPressed()" + currentFragment.getClass().getName());
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Log.d(TAG, "onDonePressed()" + currentFragment.getClass().getName());
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        if (oldFragment != null) {
            SliderPagerFragment page = (SliderPagerFragment) oldFragment;
            Log.d(TAG, "currentQuestion " + currentQuestion + " values: " + responseQ1 + " | " + responseQ2);
        }
    }

    @Override
    public void onResponse(boolean response, int layoutResId) {
        switch (layoutResId) {
            case R.layout.fragment_elderly_fall_risk_q1:
                Log.d(TAG, "onResponse() - page1: " + response);
                currentQuestion = 1;
                responseQ1 = response;
                break;
            case R.layout.fragment_elderly_fall_risk_q2:
                Log.d(TAG, "onResponse() - page2: " + response);
                currentQuestion = 2;
                responseQ2 = response;
                break;
            case R.layout.fragment_elderly_fall_risk_q3:
                Log.d(TAG, "onResponse() - page3: " + response);
                currentQuestion = 3;
                responseQ3 = response;
                break;
            default:
                break;
        }
    }

    @Override
    public void nextBlockPage(boolean block) {
//        setNextPageSwipeLock(block);
    }

    // TODO TESTAR!!!
//    public void toggleNextPageSwipeLock(View v) {
//        AppIntroViewPager pager = getPager();
//        boolean pagingState = pager.isNextPagingEnabled();
//        setNextPageSwipeLock(pagingState);
//    }

//    public void toggleSwipeLock(View v) {
//        AppIntroViewPager pager = getPager();
//        boolean pagingState = pager.isPagingEnabled();
//        setSwipeLock(pagingState);
//    }
//
//    public void toggleProgressButton(View v) {
//        boolean progressButtonState = isProgressButtonEnabled();
//        progressButtonState = !progressButtonState;
//        setProgressButtonEnabled(progressButtonState);
//    }
//
//    public void toggleSkipButton(View v) {
//        boolean skipButtonState = isSkipButtonEnabled();
//        skipButtonState = !skipButtonState;
//        showSkipButton(skipButtonState);
//    }
}
