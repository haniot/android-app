package br.edu.uepb.nutes.haniot.elderly.assessment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroViewPager;

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

    SharedPreferences preferences;
    SharedPreferences.Editor preferencesEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setColorTransitionsEnabled(true);
        setFadeAnimation();
        showSeparator(false);
        showSkipButton(false);

        setScrollDurationFactor(3);
        setNextPageSwipeLock(true);
        setImmersiveMode(true);
        addSlide(SliderPagerFragment.newInstance(this, R.layout.fragment_elderly_fall_risk_q1));
        addSlide(SliderPagerFragment.newInstance(this, R.layout.fragment_elderly_fall_risk_q2));
        addSlide(SliderPagerFragment.newInstance(this, R.layout.fragment_elderly_fall_risk_q3));
        addSlide(SliderPagerFragment.newInstance(this, R.layout.fragment_elderly_fall_risk_q4));
        addSlide(SliderPagerFragment.newInstance(this, R.layout.fragment_elderly_fall_risk_q4));
        addSlide(SliderPagerFragment.newInstance(this, R.layout.fragment_elderly_fall_risk_q4));

        initSharedPreferences();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        preferencesEditor.clear().commit();
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
//            Log.d(TAG, "currentQuestion " + currentQuestion + " values: " + responseQ1 + " | " + responseQ2);
        }
        Log.d(TAG, "onSlideChanged(): isBlocked: " + preferences.getBoolean("isBlocked", true));
//        if (getPager().isNextPagingEnabled()) {
//            setNextPageSwipeLock(true);
//        } else {
//            setNextPageSwipeLock(false);
//        }
    }

    @Override
    public void onResponse(boolean response, int layoutResId) {
        Log.d(TAG, "onResponse()");
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
    public void toggleNextPageSwipeLock(View v) {
        AppIntroViewPager pager = getPager();
        boolean pagingState = pager.isNextPagingEnabled();
        setNextPageSwipeLock(pagingState);
    }

    private void initSharedPreferences() {
        if (preferences == null) {
            preferences = getSharedPreferences("assessment_pref", Context.MODE_PRIVATE);
            preferencesEditor = preferences.edit();
        }
    }

    private void nextPage() {
        setNextPageSwipeLock(false);
        final AppIntroViewPager page = getPager();
        new Handler().post(() -> {
            page.goToNextSlide();
            setNextPageSwipeLock(true);
        });
    }
}
