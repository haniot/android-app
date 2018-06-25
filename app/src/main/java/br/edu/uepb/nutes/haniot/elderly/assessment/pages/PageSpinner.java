package br.edu.uepb.nutes.haniot.elderly.assessment.pages;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroViewPager;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

import java.io.Serializable;
import java.util.List;

/**
 * PageSpinner implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class PageSpinner extends BasePage implements ISlideBackgroundColorHolder {
    private final String TAG = "PageSpinner";

    protected static final String ARG_CONFIGS_PAGE = "arg_configs_page";

    private OnAnswerSpinnerListener mListener;
    private String answerValue;
    private int indexAnswerValue, indexOldAnswer;
    private ConfigPage configPage;
    private ArrayAdapter<String> mAdapter;

    @BindView(R.id.answer_spinner)
    Spinner answerSpinner;

    public PageSpinner() {
    }

    /**
     * New PageRadio instance.
     *
     * @param configPage
     * @return PageSpinner
     */
    private static PageSpinner newInstance(ConfigPage configPage) {
        PageSpinner pageFragment = new PageSpinner();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONFIGS_PAGE, configPage);

        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting default values
        isBlocked = true;
        indexAnswerValue = -1;
        indexOldAnswer = -1;
        answerValue = null;

        // Retrieving arguments
        if (getArguments() != null && getArguments().size() != 0) {
            configPage = (ConfigPage) getArguments().getSerializable(ARG_CONFIGS_PAGE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        int layoutView = configPage.layout != 0 ? configPage.layout : R.layout.question_spinner_theme_dark;
        View view = inflater.inflate(layoutView, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (boxTitle != null && titleTextView != null) {
            if (configPage.title != 0) {
                titleTextView.setText(configPage.title);
                if (configPage.titleColor != 0) titleTextView.setTextColor(configPage.titleColor);
            } else {
                boxTitle.setVisibility(View.GONE);
            }
        }

        if (boxDescription != null && descTextView != null) {
            if (configPage.description != 0) {
                descTextView.setText(configPage.description);
                if (configPage.descriptionColor != 0) descTextView.setTextColor(configPage.descriptionColor);
            } else {
                boxDescription.setVisibility(View.GONE);
            }
        }

        if (closeImageButton != null) {
            if (configPage.drawableClose != 0) closeImageButton.setImageResource(configPage.drawableClose);
            else closeImageButton.setVisibility(View.GONE);
        }

        if (boxImage != null && imgTextView != null) {
            if (configPage.image != 0) imgTextView.setImageResource(configPage.image);
            else boxImage.setVisibility(View.GONE);
        }

        if (answerSpinner != null) {
            mAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, configPage.items);
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (answerSpinner == null) return;

        if (closeImageButton != null)
            closeImageButton.setOnClickListener(e -> mListener.onPageClose());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (answerSpinner != null) answerSpinner.setOnItemSelectedListener(null);
        if (closeImageButton != null) closeImageButton.setOnClickListener(null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAnswerSpinnerListener) {
            mListener = (OnAnswerSpinnerListener) context;
        } else {
            throw new ClassCastException();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public int getDefaultBackgroundColor() {
        return Color.parseColor("#000000");
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        if (configPage.backgroundColor != 0)
            getView().setBackgroundColor(configPage.backgroundColor);
    }

    /**
     * Next page.
     */
    public void nextPage() {
        final AppIntro appIntro = (AppIntro) getContext();
        final AppIntroViewPager page = ((AppIntro) getContext()).getPager();

        isBlocked = false;
        appIntro.setNextPageSwipeLock(isBlocked);
        new Handler().post(() -> {
            page.goToNextSlide();
            appIntro.setNextPageSwipeLock(!isBlocked);
        });
    }

    /**
     * Check if page is blocked.
     *
     * @return
     */
    public boolean isBlocked() {
        return isBlocked;
    }

    /**
     * Get component spinner.
     *
     * @return
     */
    public Spinner getSpinner() {
        return answerSpinner;
    }

    /**
     * Select value on Spinner.
     *
     * @param value {@link String}
     */
    public void setAnswer(String value) {

    }

    /**
     * Select the value in the spinner according to the index.
     *
     * @param index int
     */
    public void setAnswer(int index) {

    }

    /**
     * Old selected value index.
     * Default: -1
     *
     * @return
     */
    public int getOldAnswer() {
        return indexOldAnswer;
    }

    /**
     * Select page number.
     *
     * @return
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Class config page.
     */
    public static class ConfigPage extends BaseConfigPage<ConfigPage> implements Serializable {

        protected List<String> items;

        public ConfigPage() {
        }

        /**
         * Set items to the spinner.
         *
         * @param items {@link List<String>}
         * @return ConfigPage
         */
        public PageSpinner.ConfigPage items(List<String> items) {
            this.items = items;
            return this;
        }

        @Override
        public Fragment build() {
            return PageSpinner.newInstance(this);
        }
    }

    /**
     * Interface OnAnswerSpinnerListener.
     *
     * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
     * @version 1.0
     * @copyright Copyright (c) 2017, NUTES UEPB
     */
    public interface OnAnswerSpinnerListener extends OnPageCloseListener {
        void onAnswerSpinner(int page, String value, int indexValue);
    }
}
