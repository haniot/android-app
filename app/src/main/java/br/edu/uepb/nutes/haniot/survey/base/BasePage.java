package br.edu.uepb.nutes.haniot.survey.base;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroViewPager;

/**
 * BasePage implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public abstract class BasePage<T extends BaseConfigPage> extends Fragment implements IBasePage<T> {
    protected Unbinder unbinder;
    protected boolean isBlocked;
    protected int pageNumber;
    protected OnPageListener mPageListener;

    @Nullable
    @BindView(R.id.question_title)
    public TextView titleTextView;

    @Nullable
    @BindView(R.id.question_description)
    public TextView descTextView;

    @Nullable
    @BindView(R.id.question_image)
    public ImageView questionImageView;

    @Nullable
    @BindView(R.id.close_imageButton)
    public ImageButton closeImageButton;

    @Nullable
    @BindView(R.id.box_title)
    public LinearLayout boxTitle;

    @Nullable
    @BindView(R.id.box_description)
    public LinearLayout boxDescription;

    @Nullable
    @BindView(R.id.box_image)
    public LinearLayout boxImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        unbinder = ButterKnife.bind(this, view);

        if (boxTitle != null && titleTextView != null) {
            if (getConfigsPage().title != 0) {
                titleTextView.setText(getConfigsPage().title);
                if (getConfigsPage().titleColor != 0)
                    titleTextView.setTextColor(getConfigsPage().titleColor);
            } else {
                boxTitle.setVisibility(View.GONE);
            }
        }

        if (boxDescription != null && descTextView != null) {
            if (getConfigsPage().description != 0) {
                descTextView.setText(getConfigsPage().description);
                if (getConfigsPage().descriptionColor != 0)
                    descTextView.setTextColor(getConfigsPage().descriptionColor);
            } else {
                boxDescription.setVisibility(View.GONE);
            }
        }

        if (closeImageButton != null) {
            if (getConfigsPage().drawableClose != 0) {
                closeImageButton.setImageResource(getConfigsPage().drawableClose);
                closeImageButton.setOnClickListener(e -> mPageListener.onClosePage());
            } else closeImageButton.setVisibility(View.GONE);
        }

        if (boxImage != null && questionImageView != null) {
            if (getConfigsPage().image != 0) {
                questionImageView.setImageResource(getConfigsPage().image);
                questionImageView.setOnClickListener(v -> mPageListener.onQuestionImageClick(getConfigsPage().image));
            } else boxImage.setVisibility(View.GONE);
        }

        initView();

        return view;
    }

    /**
     * Get instance the library app intro.
     *
     * @return AppIntro
     */
    private AppIntro getAppIntroInstance() {
        return (AppIntro) getContext();
    }

    /**
     * Get instance current page.
     *
     * @return AppIntroViewPager
     */
    private AppIntroViewPager getPageInstance() {
        return getAppIntroInstance().getPager();
    }

    @Override
    public void blockPage() {
        this.isBlocked = true;
        getAppIntroInstance().setNextPageSwipeLock(this.isBlocked);
    }

    @Override
    public void unlockPage() {
        this.isBlocked = false;
        getAppIntroInstance().setNextPageSwipeLock(this.isBlocked);
    }

    /**
     * Next page.
     */
    @Override
    public void nextPage() {
        unlockPage();
        new Handler().post(() -> getPageInstance().goToNextSlide());
    }

    /**
     * Check if page is blocked.
     *
     * @return boolean
     */
    @Override
    public boolean isBlocked() {
        return isBlocked;
    }

    /**
     * Select page number.
     *
     * @return int
     */
    @Override
    public int getPageNumber() {
        return pageNumber;
    }
}
