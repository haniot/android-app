package br.edu.uepb.nutes.haniot.survey.pages;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import br.edu.uepb.nutes.haniot.utils.Log;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

import java.io.Serializable;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.survey.base.BaseConfigPage;
import br.edu.uepb.nutes.haniot.survey.base.BasePage;
import br.edu.uepb.nutes.haniot.survey.base.OnClosePageListener;
import butterknife.BindView;

/**
 * RadioPage implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class RadioPage extends BasePage<RadioPage.ConfigPage> implements ISlideBackgroundColorHolder {
    private final String TAG = "RadioPage";

    protected static final String ARG_CONFIGS_PAGE = "arg_configs_page";

    private OnRadioListener mListener;
    private boolean answerValue, actionClearCheck;
    private int oldAnswer;
    private RadioPage.ConfigPage configPage;

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
        super.blockPage();

        // Setting default values
        oldAnswer = -1;
        answerValue = false;
        actionClearCheck = false;

        // Retrieving arguments
        if (getArguments() != null && getArguments().size() != 0) {
            configPage = (ConfigPage) getArguments().getSerializable(ARG_CONFIGS_PAGE);
            super.pageNumber = configPage.pageNumber;
        }
    }

    @Override
    public void initView() {
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

            // init answer
            if (configPage.answerInit != -1)
                setAnswer(configPage.answerInit != 0);
        }
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

                setAnswer(false);
                mListener.onAnswerRadio(pageNumber, answerValue);
            } else if (checkedId == R.id.right_radioButton && oldAnswer != 1) {
                if (configPage.radioColorTextNormal != 0 && configPage.radioColorTextChecked != 0) {
                    radioRight.setTextColor(configPage.radioColorTextChecked);
                    radioLeft.setTextColor(configPage.radioColorTextNormal);
                }

                setAnswer(true);
                mListener.onAnswerRadio(pageNumber, answerValue);
            }
            Log.d(TAG, "OnTest() " + oldAnswer + " - " + answerValue);
        });
    }

    @Override
    public int getLayout() {
        return configPage.layout != 0 ? configPage.layout : R.layout.question_radio;
    }

    @Override
    public RadioPage.ConfigPage getConfigsPage() {
        return this.configPage;
    }

    @Override
    public View getComponentAnswer() {
        return radioGroup;
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
        if (context instanceof OnRadioListener) {
            mListener = (OnRadioListener) context;
        } else {
            throw new ClassCastException("You must implement the RadioPage.OnRadioListener!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public int getDefaultBackgroundColor() {
        return (configPage.backgroundColor != 0) ? configPage.backgroundColor : Color.GRAY;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        if (configPage.backgroundColor != 0)
            getView().setBackgroundColor(configPage.backgroundColor);
    }

    @Override
    public void clearAnswer() {
        actionClearCheck = true;
        radioGroup.clearCheck();
        actionClearCheck = false;
        oldAnswer = -1;

        // Block page
        super.blockPage();
    }

    /**
     * Set Answer.
     *
     * @param value boolean
     */
    private void setAnswer(boolean value) {
        super.unlockPage();
        answerValue = value;
        oldAnswer = !value ? 0 : 1;

        if (value) radioRight.setChecked(true);
        else radioLeft.setChecked(true);
    }

    /**
     * Class config page.
     */
    public static class ConfigPage extends BaseConfigPage<ConfigPage> implements Serializable {
        protected int radioLeftText,
                radioRightText,
                radioColorTextNormal,
                radioColorTextChecked,
                radioLeftBackground,
                radioRightBackground,
                answerInit;

        public ConfigPage() {
            this.radioLeftText = 0;
            this.radioRightText = 0;
            this.radioColorTextNormal = 0;
            this.radioColorTextChecked = 0;
            this.radioLeftBackground = 0;
            this.radioRightBackground = 0;
            this.answerInit = -1;
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

        /**
         * Set answer init.
         *
         * @param answerInit
         * @return ConfigPage
         */
        public ConfigPage answerInit(boolean answerInit) {
            if (answerInit)
                this.answerInit = 1;
            else if (!answerInit)
                this.answerInit = 0;
            else
                this.answerInit = -1;

            return this;
        }

        @Override
        public RadioPage build() {
            return RadioPage.newInstance(this);
        }
    }

    /**
     * Interface OnRadioListener.
     *
     * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
     * @version 1.0
     * @copyright Copyright (c) 2017, NUTES UEPB
     */
    public interface OnRadioListener extends OnClosePageListener {
        void onAnswerRadio(int page, boolean value);
    }
}
