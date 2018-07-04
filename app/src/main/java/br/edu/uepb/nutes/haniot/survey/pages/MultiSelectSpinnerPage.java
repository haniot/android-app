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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.survey.base.BaseConfigPage;
import br.edu.uepb.nutes.haniot.survey.base.BasePage;
import br.edu.uepb.nutes.haniot.survey.base.OnPageListener;
import br.edu.uepb.nutes.haniot.ui.CustomMultiSelectSpinner;
import br.edu.uepb.nutes.haniot.utils.Log;
import butterknife.BindView;

/**
 * MultiSelectSpinnerPage implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class MultiSelectSpinnerPage extends BasePage<MultiSelectSpinnerPage.ConfigPage> implements ISlideBackgroundColorHolder {
    private final String TAG = "MultiSelectSpinnerPage";
    private String KEY_ITEMS_MULTI_SELECT_SPINNER;

    protected static final String ARG_CONFIGS_PAGE = "arg_configs_page";

    private OnMultiSelectSpinnerListener mListener;
    private List<Integer> oldIndexAnswerValue;
    private MultiSelectSpinnerPage.ConfigPage configPage;

    @BindView(R.id.answer_multi_select_spinner)
    CustomMultiSelectSpinner answerMultiSelectSpinner;

    public MultiSelectSpinnerPage() {
    }

    /**
     * New MultiSelectSpinnerPage instance.
     *
     * @param configPage
     * @return MultiSelectSpinnerPage
     */
    private static MultiSelectSpinnerPage newInstance(ConfigPage configPage) {
        MultiSelectSpinnerPage pageFragment = new MultiSelectSpinnerPage();
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
        oldIndexAnswerValue = new ArrayList<>();

        // Retrieving arguments
        if (getArguments() != null && getArguments().size() != 0) {
            this.configPage = (ConfigPage) getArguments().getSerializable(ARG_CONFIGS_PAGE);
            super.pageNumber = this.configPage.pageNumber;

            KEY_ITEMS_MULTI_SELECT_SPINNER = "answer_page"
                    .concat(String.valueOf(this.configPage.pageNumber))
                    .concat("_")
                    .concat(getClass().getName().toLowerCase());
        }
    }

    @Override
    public void initView() {
        this.answerMultiSelectSpinner.setItems(this.configPage.items);
        this.answerMultiSelectSpinner.setEnabledAddNewItem(this.configPage.enabledAdNewItem);

        if (this.configPage.hint != 0)
            this.answerMultiSelectSpinner.setHint(getContext().getResources().getString(this.configPage.hint));
        if (this.configPage.messageEmpty != 0)
            this.answerMultiSelectSpinner.setMessageEmpty(getContext().getResources()
                    .getString(this.configPage.messageEmpty));
        if (this.configPage.titleDialogAddNewItem != 0)
            this.answerMultiSelectSpinner.setTitleDialogAddNewItem(getContext().getResources()
                    .getString(this.configPage.titleDialogAddNewItem));
        if (this.configPage.colorSelectedText != 0)
            this.answerMultiSelectSpinner.setColorSelectedText(this.configPage.colorSelectedText);
        if (configPage.colorBackgroundTint != 0)
            this.answerMultiSelectSpinner.setColorBackgroundTint(this.configPage.colorBackgroundTint);

        Log.d(TAG, "VALUES: " + Arrays.toString(answerMultiSelectSpinner.getIndexSelectedItems().toArray()));

        // init answer
        if (!configPage.indexAnswerInit.isEmpty())
            setAnswer(configPage.indexAnswerInit);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this.answerMultiSelectSpinner == null) return;

        this.answerMultiSelectSpinner.setOnSpinnerListener(
                new CustomMultiSelectSpinner.OnSpinnerListener() {
                    @Override
                    public void onMultiItemSelected(List<String> items, List<Integer> indexItems) {
                        if (!indexItems.isEmpty() && !indexItems.equals(oldIndexAnswerValue)) {
                            oldIndexAnswerValue = indexItems;
                            mListener.onMultiSelectSpinner(pageNumber, items, indexItems);
                            MultiSelectSpinnerPage.super.unlockPage();
                        } else {
                            MultiSelectSpinnerPage.super.blockPage();
                        }
                    }

                    @Override
                    public void onAddNewItemSuccess(String item, int indexItem) {
                        MultiSelectSpinnerPage.super.saveItemExtraSharedPreferences(
                                KEY_ITEMS_MULTI_SELECT_SPINNER, item);
                    }

                    @Override
                    public void onAddNewItemCancel() {

                    }
                });
    }

    @Override
    public int getLayout() {
        return this.configPage.layout != 0 ? this.configPage.layout : R.layout.question_multi_select_spinner;
    }

    @Override
    public MultiSelectSpinnerPage.ConfigPage getConfigsPage() {
        return this.configPage;
    }

    @Override
    public View getComponentAnswer() {
        return this.answerMultiSelectSpinner;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (this.answerMultiSelectSpinner != null)
            this.answerMultiSelectSpinner.setOnSpinnerListener(null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMultiSelectSpinnerListener) {
            this.mListener = (OnMultiSelectSpinnerListener) context;
            super.mPageListener = this.mListener;
        } else {
            throw new ClassCastException("You must implement the MultiSelectSpinnerPage.OnMultiSelectSpinnerListener!");
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
        this.oldIndexAnswerValue = new ArrayList<>();
        this.answerMultiSelectSpinner.clear();

        // Block page
        super.blockPage();
    }

    /**
     * Set answer.
     *
     * @param indexItems
     */
    public void setAnswer(List<Integer> indexItems) {
        this.oldIndexAnswerValue = indexItems;
        this.answerMultiSelectSpinner.selection(indexItems);
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
        @StringRes
        protected int messageEmpty;
        @StringRes
        protected int titleDialogAddNewItem;
        protected List<String> items;
        protected List<Integer> indexAnswerInit;
        protected boolean enabledAdNewItem;

        public ConfigPage() {
            this.colorSelectedText = 0;
            this.colorBackgroundTint = 0;
            this.hint = 0;
            this.messageEmpty = 0;
            this.titleDialogAddNewItem = 0;
            this.indexAnswerInit = new ArrayList<>();
            this.enabledAdNewItem = true;
        }

        /**
         * Set items to the spinner.
         *
         * @param items {@link List<String>}
         * @return ConfigPage
         */
        public MultiSelectSpinnerPage.ConfigPage items(List<String> items) {
            this.items = items;
            return this;
        }

        /**
         * Set color item selected.
         *
         * @param colorSelectedText
         * @return ConfigPage
         */
        public MultiSelectSpinnerPage.ConfigPage colorSelectedText(@ColorInt int colorSelectedText) {
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
        public MultiSelectSpinnerPage.ConfigPage colorBackgroundTint(@ColorInt int colorBackgroundTint) {
            this.colorBackgroundTint = colorBackgroundTint;
            return this;
        }

        /**
         * Set hint message.
         *
         * @param hint
         * @return ConfigPage
         */
        public MultiSelectSpinnerPage.ConfigPage hint(@StringRes int hint) {
            this.hint = hint;
            return this;
        }

        /**
         * Set message dialog add new item.
         *
         * @param titleDialogAddNewItem
         * @return ConfigPage
         */
        public MultiSelectSpinnerPage.ConfigPage titleDialogAddNewItem(@StringRes int titleDialogAddNewItem) {
            this.titleDialogAddNewItem = titleDialogAddNewItem;
            return this;
        }

        /**
         * Set message empty.
         * Message that will be displayed in the selection dialog when there is no item.
         *
         * @param messageEmpty
         * @return ConfigPage
         */
        public MultiSelectSpinnerPage.ConfigPage messageEmpty(@StringRes int messageEmpty) {
            this.messageEmpty = messageEmpty;
            return this;
        }

        /**
         * Disable add new item.
         * The button to add new item will be removed from the layout.
         *
         * @return ConfigPage
         */
        public MultiSelectSpinnerPage.ConfigPage disableAddNewItem() {
            this.enabledAdNewItem = false;
            return this;
        }

        /**
         * Set answer init.
         *
         * @param indexAnswerInit
         * @return ConfigPage
         */
        public MultiSelectSpinnerPage.ConfigPage answerInit(List<Integer> indexAnswerInit) {
            this.indexAnswerInit = indexAnswerInit;
            return this;
        }

        @Override
        public String toString() {
            return "ConfigPage{" +
                    "colorSelectedText=" + colorSelectedText +
                    ", colorBackgroundTint=" + colorBackgroundTint +
                    ", hint=" + hint +
                    ", messageEmpty=" + messageEmpty +
                    ", items=" + items +
                    ", enabledAdNewItem=" + enabledAdNewItem +
                    ", indexAnswerInit=" + indexAnswerInit +
                    "} " + super.toString();
        }

        @Override
        public MultiSelectSpinnerPage build() {
            return MultiSelectSpinnerPage.newInstance(this);
        }
    }

    /**
     * Interface OnMultiSelectSpinnerListener.
     *
     * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
     * @version 1.0
     * @copyright Copyright (c) 2017, NUTES UEPB
     */
    public interface OnMultiSelectSpinnerListener extends OnPageListener {
        void onMultiSelectSpinner(int page, List<String> values, List<Integer> indexValues);
    }

}
