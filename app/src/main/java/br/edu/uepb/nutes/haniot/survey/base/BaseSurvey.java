package br.edu.uepb.nutes.haniot.survey.base;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.utils.Log;

public abstract class BaseSurvey extends AppIntro {
    private final String TAG = "BaseSurvey";
    protected IBasePage currentPage;
    protected Snackbar snackbarMessageBlockedPage;
    protected final int PAGE_END = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /**
         * Config pages.
         */
        setColorTransitionsEnabled(true);
        setFadeAnimation();
        showSeparator(false);
        showSkipButton(false);
        setNextPageSwipeLock(true);
        setImmersive(true);

        this.initView();

        setCustomTransformer(new ViewPager.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
//                final float rotation = -180f * position;
//
//                page.setVisibility(rotation > 90f || rotation < -90f ? View.INVISIBLE : View.VISIBLE);
//                page.setPivotX(page.getWidth() * 0.5f);
//                page.setPivotY(page.getHeight() * 0.5f);
//                page.setRotationX(rotation);
            }
        });
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

        if (snackbarMessageBlockedPage != null)
            snackbarMessageBlockedPage.dismiss();

        if (newFragment instanceof IBasePage) {
            currentPage = (IBasePage) newFragment;
            Log.d(TAG, "onSlideChanged() - isBlocked: " + currentPage.isBlocked() + " |  page: " + currentPage.getPageNumber());

            if (currentPage.getPageNumber() == PAGE_END) return;

            setNextPageSwipeLock(currentPage.isBlocked());

            // Capture event onSwipeLeft
            currentPage.getView().setOnTouchListener(new OnSwipePageTouchListener(this) {
                @Override
                public void onSwipeLeft() {
                    super.onSwipeLeft();
                    if (currentPage.isBlocked()) showMessageBlocked();
                }

                @Override
                public void onSwipeRight() {
                    super.onSwipeRight();
                    setNextPageSwipeLock(currentPage.isBlocked());
                }
            });
        }
    }

    /**
     * Show dialog mesage cancel.
     */
    protected void showMessageCancel() {
        runOnUiThread(() -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(getString(R.string.fall_risk_title_cancel));

            dialog.setPositiveButton(R.string.yes_text, (dialogInterface, which) -> {
                finish();
            });

            dialog.setNegativeButton(R.string.no_text, (dialogInterface, which) -> {
                currentPage.clearAnswer();
            });

            dialog.setOnCancelListener((dialogInterface) -> {
                currentPage.clearAnswer();
            });

            dialog.create().show();
        });
    }

    /**
     * Show message page blocked.
     */
    protected void showMessageBlocked() {
        runOnUiThread(() -> {
            /**
             * Create snackbar
             */
            snackbarMessageBlockedPage = Snackbar.make(currentPage.getView(),
                    R.string.fall_risk_message_blocked_page,
                    Snackbar.LENGTH_LONG);
            snackbarMessageBlockedPage.setAction(R.string.bt_ok, (v) -> {
                snackbarMessageBlockedPage.dismiss();
            });
            snackbarMessageBlockedPage.show();
        });
    }

    /**
     * Init view
     */
    protected abstract void initView();
}
