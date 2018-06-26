package br.edu.uepb.nutes.haniot.elderly.assessment.pages;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

import java.io.Serializable;

/**
 * RadioPage implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class RadioPage extends BasePage implements ISlideBackgroundColorHolder {
    private final String TAG = "RadioPage";

    protected static final String ARG_CONFIGS_PAGE = "arg_configs_page";

    private OnAnswerRadioListener mListener;
    private boolean answerValue, actionClearCheck;
    private int oldAnswer;
    private ConfigPage configPage;

    @BindView(R.id.answer_radioGroup)
    RadioGroup radioGroup;

    @BindView(R.id.left_radioButton)
    RadioButton radioLeft;

    @BindView(R.id.right_radioButton)
    RadioButton radioRight;

    public RadioPage() {
    }

    /**
     * New RadioPage instance.
     *
     * @param configPage
     * @return RadioPage
     */
    private static RadioPage newInstance(ConfigPage configPage) {
        RadioPage pageFragment = new RadioPage();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONFIGS_PAGE, configPage);

        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting default values
        super.isBlocked = true;
        oldAnswer = -1;
        answerValue = false;
        actionClearCheck = false;

        // Retrieving arguments
        if (getArguments() != null && getArguments().size() != 0) {
            configPage = (ConfigPage) getArguments().getSerializable(ARG_CONFIGS_PAGE);
            pageNumber = configPage.pageNumber;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        int layoutView = configPage.layout != 0 ? configPage.layout : R.layout.question_radio_theme_dark;
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
                if (configPage.descriptionColor != 0)
                    descTextView.setTextColor(configPage.descriptionColor);
            } else {
                boxDescription.setVisibility(View.GONE);
            }
        }

        if (closeImageButton != null) {
            if (configPage.drawableClose != 0)
                closeImageButton.setImageResource(configPage.drawableClose);
            else closeImageButton.setVisibility(View.GONE);
        }

        if (boxImage != null && imgTextView != null) {
            if (configPage.image != 0) imgTextView.setImageResource(configPage.image);
            else boxImage.setVisibility(View.GONE);
        }

        if (radioGroup != null) {
            if (configPage.radioLeftText != 0)
                radioLeft.setText(configPage.radioLeftText);

            if (configPage.radioRightText != 0)
                radioRight.setText(configPage.radioRightText);

            if (configPage.radioLeftBackground != 0)
                radioLeft.setBackgroundResource(configPage.radioLeftBackground);

            if (configPage.radioRightBackground != 0)
                radioRight.setBackgroundResource(configPage.radioRightBackground);

            if (configPage.radioColorTextNormal != 0) {
                radioLeft.setTextColor(configPage.radioColorTextNormal);
                radioRight.setTextColor(configPage.radioColorTextNormal);
            }
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (radioGroup == null) return;

        if (closeImageButton != null)
            closeImageButton.setOnClickListener(e -> mListener.onClosePage());

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (actionClearCheck) return;

            if (checkedId == R.id.left_radioButton && oldAnswer != 0) {
                if (configPage.radioColorTextNormal != 0 && configPage.radioColorTextChecked != 0) {
                    radioLeft.setTextColor(configPage.radioColorTextChecked);
                    radioRight.setTextColor(configPage.radioColorTextNormal);
                }

                oldAnswer = 0;
                answerValue = false;

                mListener.onAnswerRadio(pageNumber, answerValue);
                nextPage();
            } else if (checkedId == R.id.right_radioButton && oldAnswer != 1) {
                if (configPage.radioColorTextNormal != 0 && configPage.radioColorTextChecked != 0) {
                    radioRight.setTextColor(configPage.radioColorTextChecked);
                    radioLeft.setTextColor(configPage.radioColorTextNormal);
                }

                oldAnswer = 1;
                answerValue = true;

                mListener.onAnswerRadio(pageNumber, answerValue);
                nextPage();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (radioGroup != null) radioGroup.setOnCheckedChangeListener(null);
        if (closeImageButton != null) closeImageButton.setOnClickListener(null);
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
        return (configPage.backgroundColor != 0) ? configPage.backgroundColor : Color.BLACK;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        if (configPage.backgroundColor != 0)
            getView().setBackgroundColor(configPage.backgroundColor);
    }

    /**
     * Set Answer.
     *
     * @param value boolean
     */
    public void setAnswerRadio(boolean value) {
        super.isBlocked = false;
        if (value) radioRight.setChecked(true);
        else radioLeft.setChecked(true);
    }

    /**
     * Radio selected in response old.
     * Default: -1
     *
     * @return
     */
    public boolean getAnswerRadio() {
        return answerValue;
    }

    /**
     * Clear radiogroup checked.
     */
    public void clearAnswer() {
        super.isBlocked = true;
        actionClearCheck = true;
        radioGroup.clearCheck();
        actionClearCheck = false;
        oldAnswer = -1;
    }

    /**
     * Class config page.
     */
    public static class ConfigPage extends BaseConfigPage<RadioPage.ConfigPage> implements Serializable {
        protected int radioLeftText,
                radioRightText,
                radioColorTextNormal,
                radioColorTextChecked,
                radioLeftBackground,
                radioRightBackground;

        public ConfigPage() {
            this.radioLeftText = 0;
            this.radioRightText = 0;
            this.radioColorTextNormal = 0;
            this.radioColorTextChecked = 0;
            this.radioLeftBackground = 0;
            this.radioRightBackground = 0;
        }

        /**
         * Set left radio text.
         *
         * @param radioLeftText
         * @return ConfigPage
         */
        public ConfigPage radioLeftText(@StringRes int radioLeftText) {
            this.radioLeftText = radioLeftText;
            return this;
        }

        /**
         * Set right radio text.
         *
         * @param radioRightText
         * @return ConfigPage
         */
        public ConfigPage radioRightText(@StringRes int radioRightText) {
            this.radioRightText = radioRightText;
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
            this.radioLeftBackground = backgroundLeftRadio;
            this.radioRightBackground = backgroundRightRadio;
            this.radioColorTextNormal = textColorRadioNormal;
            this.radioColorTextChecked = textColorRadioChecked;
            return this;
        }

        @Override
        public RadioPage build() {
            return RadioPage.newInstance(this);
        }
    }

    /**
     * Interface OnAnswerRadioListener.
     *
     * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
     * @version 1.0
     * @copyright Copyright (c) 2017, NUTES UEPB
     */
    public interface OnAnswerRadioListener extends OnClosePageListener {
        void onAnswerRadio(int page, boolean value);
    }
}
