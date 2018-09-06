package br.edu.uepb.nutes.haniot.survey.pages;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

import java.io.Serializable;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.survey.base.BaseConfigPage;
import br.edu.uepb.nutes.haniot.survey.base.BasePage;
import br.edu.uepb.nutes.haniot.survey.base.OnPageListener;
import butterknife.BindView;

/**
 * ButtonPage implementation.
 * Useful for use as the last screen for thanks for participating in the assessment.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class ButtonPage extends BasePage<ButtonPage.ConfigPage> implements ISlideBackgroundColorHolder {
    private final String TAG = "ButtonPage";

    protected static final String ARG_CONFIGS_PAGE = "arg_configs_page";

    private OnButtonListener mListener;
    private ButtonPage.ConfigPage configPage;

    @BindView(R.id.answer_button)
    AppCompatButton button;

    public ButtonPage() {
    }

    /**
     * New ButtonPage instance.
     *
     * @param configPage
     * @return ButtonPage
     */
    private static ButtonPage newInstance(ConfigPage configPage) {
        ButtonPage pageFragment = new ButtonPage();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONFIGS_PAGE, configPage);

        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.blockPage();

        // Retrieving arguments
        if (getArguments() != null && getArguments().size() != 0) {
            configPage = (ConfigPage) getArguments().getSerializable(ARG_CONFIGS_PAGE);
            super.pageNumber = configPage.pageNumber;
        }
    }

    @Override
    public void initView() {
        if (this.configPage.buttonBackground != 0)
            this.button.setBackgroundResource(this.configPage.buttonBackground);
        if (this.configPage.buttonText != 0)
            this.button.setText(this.configPage.buttonText);
        if (configPage.buttonColorText != 0)
            this.button.setTextColor(this.configPage.buttonColorText);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (button == null) return;

        button.setOnClickListener(v -> mListener.onAnswerButton(super.pageNumber));
    }

    @Override
    public int getLayout() {
        return configPage.layout != 0 ? configPage.layout : R.layout.question_button;
    }

    @Override
    public ButtonPage.ConfigPage getConfigsPage() {
        return this.configPage;
    }

    @Override
    public View getComponentAnswer() {
        return button;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (button != null) button.setOnClickListener(null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnButtonListener) {
            mListener = (OnButtonListener) context;
            super.mPageListener = mListener;
        } else {
            throw new ClassCastException("You must implement the ButtonPage.OnButtonListener!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public int getDefaultBackgroundColor() {
        return (configPage.colorBackground != 0) ? configPage.colorBackground : Color.GRAY;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        if (configPage.colorBackground != 0)
            getView().setBackgroundColor(configPage.colorBackground);
    }

    @Override
    public void clearAnswer() {
    }

    /**
     * Class config page.
     */
    public static class ConfigPage extends BaseConfigPage<ConfigPage> implements Serializable {
        protected int buttonText,
                buttonColorText,
                buttonBackground;

        public ConfigPage() {
            this.buttonText = 0;
            this.buttonColorText = 0;
            this.buttonBackground = 0;
        }

        /**
         * Set button text.
         *
         * @param buttonText
         * @return ConfigPage
         */
        public ConfigPage buttonText(@StringRes int buttonText) {
            this.buttonText = buttonText;
            return this;
        }

        /**
         * Set button text color.
         *
         * @param buttonColorText
         * @return ConfigPage
         */
        public ConfigPage buttonColorText(@ColorInt int buttonColorText) {
            this.buttonColorText = buttonColorText;
            return this;
        }

        /**
         * Set button background.
         *
         * @param buttonBackground
         * @return ConfigPage
         */
        public ConfigPage buttonBackground(@DrawableRes int buttonBackground) {
            this.buttonBackground = buttonBackground;
            return this;
        }

        @Override
        public ButtonPage build() {
            return ButtonPage.newInstance(this);
        }
    }

    /**
     * Interface OnRadioListener.
     *
     * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
     * @version 1.0
     * @copyright Copyright (c) 2017, NUTES UEPB
     */
    public interface OnButtonListener extends OnPageListener {
        void onAnswerButton(int page);
    }
}
