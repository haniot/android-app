package br.edu.uepb.nutes.haniot.elderly.assessment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(SliderPagerFragment.newInstance(R.layout.fragment_elderly_fall_risk_q1));
        addSlide(SliderPagerFragment.newInstance(R.layout.fragment_elderly_fall_risk_q2));
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
        if (oldFragment != null)
            Log.d(TAG, "onSlideChanged() - old: ");
    }

    @Override
    public void onResponse(boolean response, int layoutResId) {
        if (layoutResId == R.layout.fragment_elderly_fall_risk_q1)
            Log.d(TAG, "onResponse() - page1: " + response);
        else if (layoutResId == R.layout.fragment_elderly_fall_risk_q2)
            Log.d(TAG, "onResponse() - page2: " + response);
        else
            Log.d(TAG, "onResponse() - No page");


    }
}
