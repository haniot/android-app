package br.edu.uepb.nutes.haniot.survey.pages;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.survey.base.BaseConfigPage;
import br.edu.uepb.nutes.haniot.survey.base.BasePage;
import br.edu.uepb.nutes.haniot.survey.base.OnPageListener;
import br.edu.uepb.nutes.haniot.ui.MultiSelectSpinner;
import butterknife.BindView;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SpinnerPage implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class MultiSelectSpinnerPage extends BasePage<MultiSelectSpinnerPage.ConfigPage> implements ISlideBackgroundColorHolder {
    private final String TAG = "MultiSelectSpinnerPage";

    protected static final String ARG_CONFIGS_PAGE = "arg_configs_page";

    private OnMultiSelectSpinnerListener mListener;
    private List<Integer> oldIndexAnswerValue;
    private MultiSelectSpinnerPage.ConfigPage configPage;

    @BindView(R.id.answer_multi_select_spinner)
    MultiSelectSpinner answerMultiSelectSpinner;

    @Nullable
    @BindView(R.id.new_item_imageButton)
    ImageButton addItemImageButton;

    @Nullable
    @BindView(R.id.box_add_item)
    LinearLayout boxAddItem;

    public MultiSelectSpinnerPage() {
    }

    /**
     * New RadioPage instance.
     *
     * @param configPage
     * @return SpinnerPage
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
            configPage = (ConfigPage) getArguments().getSerializable(ARG_CONFIGS_PAGE);
            super.pageNumber = configPage.pageNumber;
        }
    }

    @Override
    public void initView() {
        if (boxAddItem != null && addItemImageButton != null) {
            if (configPage.disableAddNewItem)
                boxAddItem.setVisibility(View.GONE);
        }

        if (configPage.colorTextItemSelected != 0) {
            ViewCompat.setBackgroundTintList(answerMultiSelectSpinner, ColorStateList.valueOf(configPage.colorTextItemSelected));
            if (addItemImageButton != null)
                ViewCompat.setBackgroundTintList(addItemImageButton, ColorStateList.valueOf(configPage.colorTextItemSelected));
        }

        answerMultiSelectSpinner
                .title(getString(configPage.hint))
                .hint(getString(configPage.hint))
                .items(configPage.items)
                .colorTextItemSelected(configPage.colorTextItemSelected)
                .build();

        // init answer
        if (!configPage.indexAnswerInit.isEmpty())
            setAnswer(configPage.indexAnswerInit);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (answerMultiSelectSpinner == null) return;

        if (addItemImageButton != null)
            addItemImageButton.setOnClickListener(mListenerNewItem);

        answerMultiSelectSpinner.setOnMultiSelectedListener((items, indexItems) -> {
            if (!indexItems.equals(oldIndexAnswerValue)) {
                oldIndexAnswerValue = indexItems;
                mListener.onMultiSelectSpinner(pageNumber, items, indexItems);
            }
        });
    }

    @Override
    public int getLayout() {
        return configPage.layout != 0 ? configPage.layout : R.layout.question_multi_select_spinner;
    }

    @Override
    public MultiSelectSpinnerPage.ConfigPage getConfigsPage() {
        return this.configPage;
    }

    @Override
    public View getComponentAnswer() {
        return answerMultiSelectSpinner;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (answerMultiSelectSpinner != null)
            answerMultiSelectSpinner.setOnItemSelectedListener(null);
        if (super.closeImageButton != null) super.closeImageButton.setOnClickListener(null);
        if (addItemImageButton != null) addItemImageButton.setOnClickListener(null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMultiSelectSpinnerListener) {
            mListener = (OnMultiSelectSpinnerListener) context;
            super.mPageListener = mListener;
        } else {
            throw new ClassCastException("You must implement the MultiSelectSpinnerPage.OnMultiSelectSpinnerListener!");
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
        super.isBlocked = true;
        oldIndexAnswerValue = new ArrayList<>();
        answerMultiSelectSpinner.clear();

        // Block page
        ((AppIntro) getContext()).setNextPageSwipeLock(super.isBlocked);
    }

    /**
     * Open dialog to add new item medication,
     */
    private View.OnClickListener mListenerNewItem = (v -> {
        answerMultiSelectSpinner.addItemDialog(
                getString(R.string.survey_add_new_answer),
                new MultiSelectSpinner.OnItemAddCallback() {
                    @Override
                    public void onSuccess(String item) {
                        Log.d(TAG, "onSuccess() " + item);
                    }

                    @Override
                    public void onCancel() {
                        Log.d(TAG, "onCancel()");
                    }
                });
    });

    /**
     * Set answer.
     *
     * @param indexItems
     */
    public void setAnswer(List<Integer> indexItems) {
        super.isBlocked = false;
        oldIndexAnswerValue = indexItems;
        answerMultiSelectSpinner.selection(indexItems);
    }

    /**
     * Class config page.
     */
    public static class ConfigPage extends BaseConfigPage<ConfigPage> implements Serializable {

        protected List<String> items;
        protected int colorTextItemSelected;
        protected int hint;
        protected boolean disableAddNewItem;
        protected List<Integer> indexAnswerInit;

        public ConfigPage() {
            this.colorTextItemSelected = 0;
            this.hint = R.string.survey_select_the_answers;
            this.disableAddNewItem = false;
            this.indexAnswerInit = new ArrayList<>();
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
         * @param colorTextItemSelected
         * @return ConfigPage
         */
        public MultiSelectSpinnerPage.ConfigPage colorTextItemSelected(@ColorInt int colorTextItemSelected) {
            this.colorTextItemSelected = colorTextItemSelected;
            return this;
        }

        /**
         * Set hint message.
         *
         * @param hint
         * @return ConfigPage
         */
        public MultiSelectSpinnerPage.ConfigPage hint(@ColorInt int hint) {
            this.hint = hint;
            return this;
        }

        /**
         * Disable add new item.
         * The button to add new item will be removed from the layout.
         *
         * @return ConfigPage
         */
        public MultiSelectSpinnerPage.ConfigPage disableAddNewItem() {
            this.disableAddNewItem = true;
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
                    "items=" + items +
                    ", colorTextItemSelected=" + colorTextItemSelected +
                    ", hint=" + hint +
                    ", indexAnswerInit=" + Arrays.toString(indexAnswerInit.toArray()) +
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
