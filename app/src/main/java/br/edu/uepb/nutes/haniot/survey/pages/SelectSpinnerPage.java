package br.edu.uepb.nutes.haniot.survey.pages;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

import java.io.Serializable;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.survey.base.BaseConfigPage;
import br.edu.uepb.nutes.haniot.survey.base.BasePage;
import br.edu.uepb.nutes.haniot.survey.base.OnPageListener;
import br.edu.uepb.nutes.haniot.ui.CustomSelectSpinner;
import br.edu.uepb.nutes.haniot.utils.Log;
import butterknife.BindView;

/**
 * SelectSpinnerPage implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class SelectSpinnerPage extends BasePage<SelectSpinnerPage.ConfigPage> implements ISlideBackgroundColorHolder {
    private final String TAG = "SelectSpinnerPage";

    protected static final String ARG_CONFIGS_PAGE = "arg_configs_page";

    private OnSpinnerListener mListener;
    private int oldIndexAnswerValue;
    private SelectSpinnerPage.ConfigPage configPage;

    @BindView(R.id.answer_spinner)
    CustomSelectSpinner answerSelectSpinner;

    public SelectSpinnerPage() {
    }

    /**
     * New RadioPage instance.
     *
     * @param configPage
     * @return SelectSpinnerPage
     */
    private static SelectSpinnerPage newInstance(ConfigPage configPage) {
        SelectSpinnerPage pageFragment = new SelectSpinnerPage();
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
        super.isBlocked = true;
        this.oldIndexAnswerValue = 0;

        // Retrieving arguments
        if (getArguments() != null && getArguments().size() != 0) {
            this.configPage = (ConfigPage) getArguments().getSerializable(ARG_CONFIGS_PAGE);
            super.pageNumber = this.configPage.pageNumber;

            // set hint
            configPage.items.add(0, getContext().getResources().getString(this.configPage.hint));
        }
    }

    @Override
    public void initView() {
        this.answerSelectSpinner.setItems(this.configPage.items);
        this.answerSelectSpinner.setEnabledAddNewItem(this.configPage.enabledAdNewItem);

        if (this.configPage.hint != 0)
            this.answerSelectSpinner.setHint(getContext().getResources().getString(this.configPage.hint));
        if (this.configPage.colorSelectedText != 0)
            this.answerSelectSpinner.setColorSelectedText(this.configPage.colorSelectedText);
        if (configPage.colorBackgroundTint != 0)
            this.answerSelectSpinner.setColorBackgroundTint(this.configPage.colorBackgroundTint);

        // init answer
        if (this.configPage.indexAnswerInit != -1)
            this.setAnswer(this.configPage.indexAnswerInit);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this.answerSelectSpinner == null) return;

        this.answerSelectSpinner.setOnSpinnerListener(new CustomSelectSpinner.OnSpinnerListener() {
            @Override
            public void onItemSelected(String item, int indexItem) {
                Log.d(TAG, "index: " + indexItem + " oldIndex: " + oldIndexAnswerValue);
                if (indexItem != oldIndexAnswerValue) {
                    oldIndexAnswerValue = indexItem;
                    mListener.onAnswerSpinner(SelectSpinnerPage.super.pageNumber, item, indexItem);
                    SelectSpinnerPage.super.unlockPage();
                }
            }

            @Override
            public void onAddNewItemSuccess(String item, int indexItem) {

            }

            @Override
            public void onAddNewItemCancel() {

            }
        });
    }

    @Override
    public int getLayout() {
        return this.configPage.layout != 0 ? this.configPage.layout : R.layout.question_select_spinner;
    }

    @Override
    public SelectSpinnerPage.ConfigPage getConfigsPage() {
        return this.configPage;
    }

    @Override
    public View getComponentAnswer() {
        return this.answerSelectSpinner;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        this.unbinder.unbind();
        if (this.answerSelectSpinner != null) this.answerSelectSpinner.setOnSpinnerListener(null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSpinnerListener) {
            this.mListener = (OnSpinnerListener) context;
            super.mPageListener = this.mListener;
        } else {
            throw new ClassCastException();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mListener = null;
    }

    @Override
    public int getDefaultBackgroundColor() {
        return (this.configPage.colorBackground != 0) ? this.configPage.colorBackground : Color.GRAY;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        if (this.configPage.colorBackground != 0)
            getView().setBackgroundColor(this.configPage.colorBackground);
    }

    @Override
    public void clearAnswer() {
        this.oldIndexAnswerValue = 0;
        this.answerSelectSpinner.clear();

        // Block page
        super.blockPage();
    }

    /**
     * Set answer.
     *
     * @param indexValue
     */
    public void setAnswer(int indexValue) {
        this.answerSelectSpinner.selection(indexValue);
        this.oldIndexAnswerValue = this.answerSelectSpinner.getIndexItemSelected(); // position 0 hint

        super.unlockPage();
    }

    /**
     * Class config page.
     */
    public static class ConfigPage extends BaseConfigPage<ConfigPage> implements Serializable {

        @ColorInt
        protected int colorSelectedText;
        @ColorInt
        protected int colorBackgroundTint;
        @StringRes
        protected int hint;
        protected List<String> items;
        protected int indexAnswerInit;
        protected boolean enabledAdNewItem;

        public ConfigPage() {
            this.colorSelectedText = 0;
            this.colorBackgroundTint = 0;
            this.hint = R.string.survey_select_an_answer;
            this.indexAnswerInit = -1;
            this.enabledAdNewItem = true;
        }

        /**
         * Set items to the spinner.
         *
         * @param items {@link List<String>}
         * @return ConfigPage
         */
        public SelectSpinnerPage.ConfigPage items(List<String> items) {
            this.items = items;
            return this;
        }

        /**
         * Set color item selected.
         *
         * @param colorSelectedText
         * @return ConfigPage
         */
        public SelectSpinnerPage.ConfigPage colorSelectedText(@ColorInt int colorSelectedText) {
            this.colorSelectedText = colorSelectedText;
            return this;
        }

        /**
         * Set color background tint.
         * The spinner line and the add new item image will receive this color.
         *
         * @param colorBackgroundTint
         * @return ConfigPage
         */
        public SelectSpinnerPage.ConfigPage colorBackgroundTint(@ColorInt int colorBackgroundTint) {
            this.colorBackgroundTint = colorBackgroundTint;
            return this;
        }

        /**
         * Set hint message.
         *
         * @param hint
         * @return ConfigPage
         */
        public SelectSpinnerPage.ConfigPage hint(@ColorInt int hint) {
            this.hint = hint;
            return this;
        }

        /**
         * Set answer init.
         *
         * @param indexAnswerInit
         * @return ConfigPage
         */
        public SelectSpinnerPage.ConfigPage answerInit(int indexAnswerInit) {
            this.indexAnswerInit = indexAnswerInit;
            return this;
        }

        /**
         * Disable add new item.
         * The button to add new item will be removed from the layout.
         *
         * @return ConfigPage
         */
        public SelectSpinnerPage.ConfigPage disableAddNewItem() {
            this.enabledAdNewItem = false;
            return this;
        }

        @Override
        public String toString() {
            return "ConfigPage{" +
                    "colorSelectedText=" + colorSelectedText +
                    ", colorBackgroundTint=" + colorBackgroundTint +
                    ", hint=" + hint +
                    ", items=" + items +
                    ", indexAnswerInit=" + indexAnswerInit +
                    "} " + super.toString();
        }

        @Override
        public SelectSpinnerPage build() {
            return SelectSpinnerPage.newInstance(this);
        }
    }

    /**
     * Interface OnSpinnerListener.
     *
     * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
     * @version 1.0
     * @copyright Copyright (c) 2017, NUTES UEPB
     */
    public interface OnSpinnerListener extends OnPageListener {
        void onAnswerSpinner(int page, String value, int indexValue);
    }
}
