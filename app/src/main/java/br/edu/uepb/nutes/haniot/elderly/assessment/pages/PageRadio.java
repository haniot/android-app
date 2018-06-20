package br.edu.uepb.nutes.haniot.elderly.assessment.pages;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroViewPager;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * PageRadio implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class PageRadio extends Fragment implements ISlideBackgroundColorHolder {
    private final String TAG = "PageRadio";

    protected static final String ARG_LAYOUT = "arg_layout";
    protected static final String ARG_TITLE = "arg_title";
    protected static final String ARG_DESC = "arg_desc";
    protected static final String ARG_DRAWABLE = "arg_drawable";
    protected static final String ARG_BG_COLOR = "arg_bg_color";
    protected static final String ARG_TITLE_COLOR = "arg_title_color";
    protected static final String ARG_DESC_COLOR = "arg_desc_color";
    protected static final String ARG_TEXT_LEFT_RADIO = "arg_text_left_radio";
    protected static final String ARG_TEXT_RIGHT_RADIO = "arg_text_right_radio";
    protected static final String ARG_TEXT_COLOR_RADIO_NORMAL = "arg_text_color_radio_normal";
    protected static final String ARG_TEXT_COLOR_RADIO_CHECKED = "arg_text_color_radio_checked";
    protected static final String ARG_BG_LEFT_RADIO = "arg_bg_color_left_radio";
    protected static final String ARG_BG_RIGHT_RADIO = "arg_bg_color_right_radio";
    protected static final String ARG_PAGE_NUMBER = "arg_page_number";

    private Unbinder unbinder;
    private OnAnswerRadioListener mListener;
    private boolean isBlocked, answerValue, actionClearCheck;

    private int layout,
            drawable,
            backgroundColor,
            titleColor,
            descriptionColor,
            leftRadioText,
            rightRadioText,
            textColorRadioNormal,
            textColorRadioChecked,
            backgroundLeftRadio,
            backgroundRightRadio,
            pageNumber,
            oldCheckedRadio;

    private String title, description;

    @Nullable
    @BindView(R.id.question_title)
    TextView titleTextView;

    @Nullable
    @BindView(R.id.question_description)
    TextView descTextView;

    @Nullable
    @BindView(R.id.question_image)
    ImageView imgTextView;

    @BindView(R.id.question_radioGroup)
    RadioGroup radioGroup;

    @BindView(R.id.left_radioButton)
    RadioButton radioLeft;

    @BindView(R.id.right_radioButton)
    RadioButton radioRight;

    public PageRadio() {
    }

    /**
     * New PageRadio instance.
     *
     * @param ConfigPage
     * @return PageRadio
     */
    private static PageRadio newInstance(ConfigPage ConfigPage) {
        PageRadio pageFragment = new PageRadio();
        Bundle args = new Bundle();

        args.putInt(ARG_LAYOUT, ConfigPage.layout);
        args.putInt(ARG_TITLE, ConfigPage.title);
        args.putInt(ARG_DESC, ConfigPage.description);
        args.putInt(ARG_DRAWABLE, ConfigPage.drawable);
        args.putInt(ARG_BG_COLOR, ConfigPage.backgroundColor);
        args.putInt(ARG_TITLE_COLOR, ConfigPage.titleColor);
        args.putInt(ARG_DESC_COLOR, ConfigPage.descriptionColor);
        args.putInt(ARG_TEXT_LEFT_RADIO, ConfigPage.leftRadioText);
        args.putInt(ARG_TEXT_RIGHT_RADIO, ConfigPage.rightRadioText);
        args.putInt(ARG_TEXT_COLOR_RADIO_NORMAL, ConfigPage.textColorRadioNormal);
        args.putInt(ARG_TEXT_COLOR_RADIO_CHECKED, ConfigPage.textColorRadioChecked);
        args.putInt(ARG_BG_LEFT_RADIO, ConfigPage.backgroundLeftRadio);
        args.putInt(ARG_BG_RIGHT_RADIO, ConfigPage.backgroundRightRadio);
        args.putInt(ARG_PAGE_NUMBER, ConfigPage.pageNumber);
        pageFragment.setArguments(args);

        return pageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting default values
        oldCheckedRadio = -1;
        isBlocked = true;
        answerValue = false;
        actionClearCheck = false;

        // Retrieving arguments
        if (getArguments() != null && getArguments().size() != 0) {
            layout = getArguments().getInt(ARG_LAYOUT);
            title = getArguments().getInt(ARG_TITLE) != 0 ?
                    getContext().getResources().getString(getArguments().getInt(ARG_TITLE)) : "";
            description = getArguments().getInt(ARG_DESC) != 0 ?
                    getContext().getResources().getString(getArguments().getInt(ARG_DESC)) : "";
            drawable = getArguments().getInt(ARG_DRAWABLE);
            backgroundColor = getArguments().getInt(ARG_BG_COLOR);
            titleColor = getArguments().getInt(ARG_TITLE_COLOR);
            descriptionColor = getArguments().getInt(ARG_DESC_COLOR);
            leftRadioText = getArguments().getInt(ARG_TEXT_LEFT_RADIO);
            rightRadioText = getArguments().getInt(ARG_TEXT_RIGHT_RADIO);
            textColorRadioNormal = getArguments().getInt(ARG_TEXT_COLOR_RADIO_NORMAL);
            textColorRadioChecked = getArguments().getInt(ARG_TEXT_COLOR_RADIO_CHECKED);
            backgroundLeftRadio = getArguments().getInt(ARG_BG_LEFT_RADIO);
            backgroundRightRadio = getArguments().getInt(ARG_BG_RIGHT_RADIO);
            pageNumber = getArguments().getInt(ARG_PAGE_NUMBER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (titleTextView != null) {
            titleTextView.setText(title);
            if (titleColor != 0) titleTextView.setTextColor(titleColor);
        }

        if (descTextView != null) {
            descTextView.setText(description);
            if (descriptionColor != 0) descTextView.setTextColor(descriptionColor);
        }

        if (imgTextView != null && this.drawable != 0) imgTextView.setImageResource(drawable);

        if (radioGroup != null) {
            if (leftRadioText != 0)
                radioLeft.setText(getContext().getResources().getString(leftRadioText));

            if (rightRadioText != 0)
                radioRight.setText(getContext().getResources().getString(rightRadioText));

            if (backgroundLeftRadio != 0)
                radioLeft.setBackgroundResource(backgroundLeftRadio);
            if (backgroundRightRadio != 0)
                radioRight.setBackgroundResource(backgroundRightRadio);
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (radioGroup == null) return;

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (actionClearCheck) return;

            if (checkedId == R.id.left_radioButton && oldCheckedRadio != 0) {
                if (textColorRadioNormal != 0 && textColorRadioChecked != 0) {
                    radioLeft.setTextColor(textColorRadioChecked);
                    radioRight.setTextColor(textColorRadioNormal);
                }

                oldCheckedRadio = 0;
                answerValue = false;

                mListener.onAnswerRadio(getView(), answerValue, pageNumber);
                nextPage();
            } else if (checkedId == R.id.right_radioButton && oldCheckedRadio != 1) {
                if (textColorRadioNormal != 0 && textColorRadioChecked != 0) {
                    radioRight.setTextColor(textColorRadioChecked);
                    radioLeft.setTextColor(textColorRadioNormal);
                }

                oldCheckedRadio = 1;
                answerValue = true;

                mListener.onAnswerRadio(getView(), answerValue, pageNumber);
                nextPage();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (radioGroup != null) radioGroup.setOnCheckedChangeListener(null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAnswerRadioListener) {
            mListener = (OnAnswerRadioListener) context;
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
        if (this.backgroundColor != 0)
            getView().setBackgroundColor(this.backgroundColor);
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
     * Get component radiogroup.
     *
     * @return
     */
    public RadioGroup getRadioGroup() {
        return radioGroup;
    }

    /**
     * Set Answer.
     *
     * @param value boolean
     */
    public void setAnswer(boolean value) {
        if(value) radioRight.setChecked(true);
        else radioLeft.setChecked(true);
    }

    /**
     * Clear radiogroup checked.
     */
    public void clearCheck() {
        actionClearCheck = true;
        radioGroup.clearCheck();
        actionClearCheck = false;
    }

    /**
     * Radio selected in response.
     * Default: -1
     *
     * @return
     */
    public int getOldCheckedRadio() {
        return oldCheckedRadio;
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
    public static class ConfigPage {
        private int layout,
                title,
                description,
                drawable,
                backgroundColor,
                titleColor,
                descriptionColor,
                leftRadioText,
                rightRadioText,
                textColorRadioNormal,
                textColorRadioChecked,
                backgroundLeftRadio,
                backgroundRightRadio,
                pageNumber;

        public ConfigPage() {
            this.layout = R.layout.fragment_radio_question_default;
            this.title = 0;
            this.description = 0;
            this.drawable = 0;
            this.backgroundColor = 0;
            this.titleColor = 0;
            this.descriptionColor = 0;
            this.leftRadioText = 0;
            this.rightRadioText = 0;
            this.textColorRadioNormal = 0;
            this.textColorRadioChecked = 0;
            this.backgroundLeftRadio = 0;
            this.backgroundRightRadio = 0;
        }

        /**
         * Set resource layout.
         *
         * @param layout
         * @return ConfigPage
         */
        public ConfigPage layout(@LayoutRes int layout) {
            this.layout = layout;
            return this;
        }

        /**
         * Set title.
         *
         * @param title
         * @return ConfigPage
         */
        public ConfigPage title(@StringRes int title) {
            this.title = title;
            return this;
        }

        /**
         * Set description.
         *
         * @param description
         * @return ConfigPage
         */
        public ConfigPage description(@StringRes int description) {
            this.description = description;
            return this;
        }

        /**
         * Set title and color.
         *
         * @param title
         * @param titleColor
         * @return ConfigPage
         */
        public ConfigPage title(@StringRes int title, @ColorInt int titleColor) {
            this.title = title;
            this.titleColor = titleColor;
            return this;
        }

        /**
         * Set description and color.
         *
         * @param description
         * @param descriptionColor
         * @return ConfigPage
         */
        public ConfigPage description(@StringRes int description, @ColorInt int descriptionColor) {
            this.description = description;
            this.descriptionColor = descriptionColor;
            return this;
        }

        /**
         * Set drawable image.
         *
         * @param drawable
         * @return ConfigPage
         */
        public ConfigPage drawable(@DrawableRes int drawable) {
            this.drawable = drawable;
            return this;
        }

        /**
         * Set background color
         *
         * @param backgroundColor
         * @return
         */
        public ConfigPage backgroundColor(@ColorInt int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        /**
         * Set title color.
         *
         * @param titleColor
         * @return ConfigPage
         */
        public ConfigPage titleColor(@ColorInt int titleColor) {
            this.titleColor = titleColor;
            return this;
        }

        /**
         * Set description color.
         *
         * @param descriptionColor
         * @return ConfigPage
         */
        public ConfigPage descriptionColor(@ColorInt int descriptionColor) {
            this.descriptionColor = descriptionColor;
            return this;
        }

        /**
         * Set left radio text.
         *
         * @param leftRadioText
         * @return ConfigPage
         */
        public ConfigPage leftRadioText(@StringRes int leftRadioText) {
            this.leftRadioText = leftRadioText;
            return this;
        }

        /**
         * Set right radio text.
         *
         * @param rightRadioText
         * @return ConfigPage
         */
        public ConfigPage rightRadioText(@StringRes int rightRadioText) {
            this.rightRadioText = rightRadioText;
            return this;
        }

        /**
         * Set style radio.
         *
         * @param backgroundLeftRadio
         * @param backgroundRightRadio
         * @param textColorRadioNormal
         * @param textColorRadioChecked
         * @return ConfigPage
         */
        public ConfigPage radioStyle(@DrawableRes int backgroundLeftRadio,
                                     @DrawableRes int backgroundRightRadio,
                                     @ColorInt int textColorRadioNormal,
                                     @ColorInt int textColorRadioChecked) {
            this.backgroundLeftRadio = backgroundLeftRadio;
            this.backgroundRightRadio = backgroundRightRadio;
            this.textColorRadioNormal = textColorRadioNormal;
            this.textColorRadioChecked = textColorRadioChecked;
            return this;
        }

        /**
         * Set page number.
         *
         * @param pageNumber
         * @return ConfigPage
         */
        public ConfigPage pageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
            return this;
        }

        public Fragment build() {
            return PageRadio.newInstance(this);
        }
    }

    /**
     * Interface OnAnswerRadioListener.
     *
     * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
     * @version 1.0
     * @copyright Copyright (c) 2017, NUTES UEPB
     */
    public interface OnAnswerRadioListener {
        void onAnswerRadio(View view, boolean value, int page);
    }
}
