package br.edu.uepb.nutes.haniot.elderly.assessment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.elderly.ElderlyRegisterActivity;
import com.github.paolorotolo.appintro.AppIntro;

/**
 * FallCharacterizationActivity implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class FallCharacterizationActivity extends AppIntro implements OnAnswerListener {
    private final String TAG = "FallCharacterization";

    public static final String EXTRA_QUESTIONS = "extra_questions";
    public static final String EXTRA_ANSWERS = "extra_answers";
    public static final String EXTRA_ELDERLY_ID = "extra_elderly_id";

    private final int PAGE_1 = 0;
    private final int PAGE_2 = 1;
    private final int PAGE_3 = 2;
    private final int PAGE_4 = 3;
    private final int PAGE_5 = 4;
    private final int PAGE_6 = 5;
    private final int PAGE_7 = 6;
    private final int PAGE_8 = 7;
    private final int PAGE_9 = 8;
    private final int PAGE_10 = 9;
    private final int PAGE_END = 10;

    private String[] questions;
    private boolean[] answers;
    private SliderPageFragment currentPage;
    private Snackbar snackbarMessageBlockedPage;
    private String elderlyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        questions = getResources().getStringArray(R.array.risk_questions_array);
        answers = new boolean[10];

        Intent it = getIntent();
        elderlyId = it.getStringExtra(ElderlyRegisterActivity.EXTRA_ELDERLY_ID);

        initComponents();
    }

    /**
     * Initialize components.
     */
    private void initComponents() {
        addPages();
    }

    /**
     * Add the slides.
     */
    private void addPages() {
        /**
         * Config pages.
         */
        setColorTransitionsEnabled(true);
        setFadeAnimation();
        showSeparator(false);
        showSkipButton(false);
        setNextPageSwipeLock(false);
        setImmersive(true);

        addSlide(SliderPageFragment.newInstance(
                R.layout.fragment_elderly_fall_characterization1, PAGE_1));

        addSlide(SliderPageFragment.newInstance(
                R.layout.fragment_elderly_fall_characterization2, PAGE_2));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);

        Log.d(TAG, "onSlideChanged() " + newFragment.getContext().getClass().getName());

        if (newFragment instanceof SliderPageFragment) {
            currentPage = (SliderPageFragment) newFragment;
        }
    }

    @Override
    public void onAnswer(View view, boolean value, int page) {
        Log.w(TAG, "onAnswer() " + " page: " + page + " answer: " + value);
        if(page == 1 && value == false) {
            addSlide(SliderPageFragment.newInstance(
                    R.layout.fragment_elderly_fall_characterization1, PAGE_3));
            Log.w(TAG, "entrou next()");
            currentPage.nextPage();
        }
    }

    /**
     * Show message page blocked.
     */
    private void showMessageBlocked() {
        runOnUiThread(() -> {
            /**
             * Create snackbar
             */
            snackbarMessageBlockedPage = Snackbar.make(currentPage.getView(),
                    R.string.risk_fall_message_blocked_page,
                    Snackbar.LENGTH_LONG);
            snackbarMessageBlockedPage.setAction(R.string.bt_ok, (v) -> {
                snackbarMessageBlockedPage.dismiss();
            });
            snackbarMessageBlockedPage.show();
        });
    }
}

