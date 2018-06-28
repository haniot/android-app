package br.edu.uepb.nutes.haniot.survey.pages;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.survey.base.BaseConfigPage;
import br.edu.uepb.nutes.haniot.survey.base.BasePage;
import br.edu.uepb.nutes.haniot.survey.base.OnClosePageListener;
import butterknife.BindView;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

import java.io.Serializable;
import java.util.List;

/**
 * SpinnerPage implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class SpinnerPage extends BasePage<SpinnerPage.ConfigPage> implements ISlideBackgroundColorHolder {
    private final String TAG = "SpinnerPage";

    protected static final String ARG_CONFIGS_PAGE = "arg_configs_page";

    private OnSpinnerListener mListener;
    private String answerValue;
    private int indexAnswerValue, oldIndexAnswerValue;
    private SpinnerPage.ConfigPage configPage;
    private CustomSpinnerAdapter mAdapter;

    @BindView(R.id.answer_spinner)
    AppCompatSpinner answerSpinner;

    public SpinnerPage() {
    }

    /**
     * New RadioPage instance.
     *
     * @param configPage
     * @return SpinnerPage
     */
    private static SpinnerPage newInstance(ConfigPage configPage) {
        SpinnerPage pageFragment = new SpinnerPage();
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
        answerValue = "";
        indexAnswerValue = 0;
        oldIndexAnswerValue = 0;

        // Retrieving arguments
        if (getArguments() != null && getArguments().size() != 0) {
            configPage = (ConfigPage) getArguments().getSerializable(ARG_CONFIGS_PAGE);
            super.pageNumber = configPage.pageNumber;

            // set hint
            configPage.items.add(0, getContext().getResources().getString(configPage.hint));
        }
    }

    @Override
    public void initView() {
        // Config spinner
        if (configPage.colorTextItemSelected != 0)
            ViewCompat.setBackgroundTintList(answerSpinner, ColorStateList.valueOf(configPage.colorTextItemSelected));

        mAdapter = new CustomSpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, configPage.items);
        answerSpinner.setAdapter(mAdapter);

        // init answer
        if (configPage.indexAnswerInit != -1)
            setAnswer(configPage.indexAnswerInit);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (answerSpinner == null) return;

        if (closeImageButton != null)
            closeImageButton.setOnClickListener(e -> mListener.onClosePage());


        answerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "setOnItemSelectedListener(): old: " + oldIndexAnswerValue + " | indexAnswerValue: " + position);

                if (position == oldIndexAnswerValue) return;

                answerValue = String.valueOf(parent.getItemAtPosition(position));

                // Como o primeiro item é a dica, subtraímos 1
                indexAnswerValue = position - 1;
                oldIndexAnswerValue = position;

                mListener.onAnswerSpinner(pageNumber, answerValue, indexAnswerValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public int getLayout() {
        return configPage.layout != 0 ? configPage.layout : R.layout.question_spinner;
    }

    @Override
    public SpinnerPage.ConfigPage getConfigsPage() {
        return this.configPage;
    }

    @Override
    public View getComponentAnswer() {
        return answerSpinner;
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
        if (context instanceof OnSpinnerListener) {
            mListener = (OnSpinnerListener) context;
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
        answerValue = "";
        indexAnswerValue = 0;
        oldIndexAnswerValue = 0;
        answerSpinner.setSelection(0);

        // Block page
        ((AppIntro) getContext()).setNextPageSwipeLock(super.isBlocked);
    }

    /**
     * Set answer.
     *
     * @param indexValue
     */
    public void setAnswer(int indexValue) {
        super.isBlocked = false;
        if (indexValue != -1) {
            indexAnswerValue = indexValue;
            oldIndexAnswerValue = indexValue + 1; // position 0 hint
            answerSpinner.setSelection(oldIndexAnswerValue);
        }
    }

    /**
     * Class config page.
     */
    public static class ConfigPage extends BaseConfigPage<ConfigPage> implements Serializable {

        protected List<String> items;
        protected int colorTextItemSelected;
        protected int hint;
        protected int indexAnswerInit;

        public ConfigPage() {
            this.colorTextItemSelected = 0;
            this.hint = R.string.survey_select_an_answer;
            this.indexAnswerInit = -1;
        }

        /**
         * Set items to the spinner.
         *
         * @param items {@link List<String>}
         * @return ConfigPage
         */
        public SpinnerPage.ConfigPage items(List<String> items) {
            this.items = items;
            return this;
        }

        /**
         * Set color item selected.
         *
         * @param colorTextItemSelected
         * @return ConfigPage
         */
        public SpinnerPage.ConfigPage colorTextItemSelected(@ColorInt int colorTextItemSelected) {
            this.colorTextItemSelected = colorTextItemSelected;
            return this;
        }

        /**
         * Set hint message.
         *
         * @param hint
         * @return ConfigPage
         */
        public SpinnerPage.ConfigPage hint(@ColorInt int hint) {
            this.hint = hint;
            return this;
        }

        /**
         * Set answer init.
         *
         * @param indexAnswerInit
         * @return ConfigPage
         */
        public SpinnerPage.ConfigPage answerInit(int indexAnswerInit) {
            this.indexAnswerInit = indexAnswerInit;
            return this;
        }

        @Override
        public String toString() {
            return "ConfigPage{" +
                    "items=" + items +
                    ", colorTextItemSelected=" + colorTextItemSelected +
                    ", hint=" + hint +
                    ", indexAnswerInit=" + indexAnswerInit +
                    "} " + super.toString();
        }

        @Override
        public SpinnerPage build() {
            return SpinnerPage.newInstance(this);
        }
    }

    /**
     * Class Custom SpinnerAdapter.
     */
    public class CustomSpinnerAdapter extends ArrayAdapter<String> implements SpinnerAdapter {

        private final Context context;
        private List<String> _items;

        public CustomSpinnerAdapter(@NonNull Context context, int textViewResourceId, List<String> _items) {
            super(context, textViewResourceId, _items);
            this.context = context;
            this._items = _items;
        }

        public int getCount() {
            return _items.size();
        }

        public String getItem(int i) {
            return _items.get(i);
        }

        public long getItemId(int i) {
            return (long) i;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            TextView txt = new TextView(context);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setPadding(50, 30, 30, 30);
//            txt.setTextSize(15);
            txt.setText(_items.get(position));
            txt.setTextColor(Color.BLACK);
            TextViewCompat.setTextAppearance(txt, android.R.style.TextAppearance_Medium);

            // Set color hint message
            if (configPage.hint != 0 && position == 0) {
                txt.setPadding(40, 30, 30, 0);
                txt.setTextColor(Color.GRAY);
                ViewCompat.setBackgroundTintList(txt, ColorStateList.valueOf(Color.BLACK));
            }

            return txt;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewgroup) {
            TextView txt = new TextView(context);
            txt.setGravity(Gravity.CENTER);
            txt.setText(_items.get(position));
            txt.setTextSize(16);
            txt.setTextColor(configPage.colorTextItemSelected != 0 ? configPage.colorTextItemSelected : Color.BLACK);

            return txt;
        }

        @Override
        public boolean isEnabled(int position) {
            // If the spinner has hint, disable it
            return (configPage.hint != 0 && position == 0) ? false : true;
        }
    }

    /**
     * Interface OnSpinnerListener.
     *
     * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
     * @version 1.0
     * @copyright Copyright (c) 2017, NUTES UEPB
     */
    public interface OnSpinnerListener extends OnClosePageListener {
        void onAnswerSpinner(int page, String value, int indexValue);
    }
}
