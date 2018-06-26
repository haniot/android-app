package br.edu.uepb.nutes.haniot.elderly.assessment.pages;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * SpinnerPage implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class SpinnerPage extends BasePage implements ISlideBackgroundColorHolder {
    private final String TAG = "SpinnerPage";

    protected static final String ARG_CONFIGS_PAGE = "arg_configs_page";

    private OnAnswerSpinnerListener mListener;
    private String answerValue;
    private int indexAnswerValue;
    private ConfigPage configPage;
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

        // Setting default values
        super.isBlocked = true;
        answerValue = "";
        indexAnswerValue = -1;

        // Retrieving arguments
        if (getArguments() != null && getArguments().size() != 0) {
            configPage = (ConfigPage) getArguments().getSerializable(ARG_CONFIGS_PAGE);
            pageNumber = configPage.pageNumber;
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

        // Config spinner
        if (configPage.colorTextItemSelected != 0)
            ViewCompat.setBackgroundTintList(answerSpinner, ColorStateList.valueOf(configPage.colorTextItemSelected));

        List<String> items_temp = new ArrayList<>();
        if (indexAnswerValue == -1 && configPage.hint != 0) { // set hint
            items_temp.add(getContext().getResources().getString(configPage.hint));
            items_temp.addAll(configPage.items);
        } else {
            items_temp = configPage.items;
        }

        mAdapter = new CustomSpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, items_temp);
        answerSpinner.setAdapter(mAdapter);

        return view;
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
                answerValue = String.valueOf(parent.getItemAtPosition(position));

                // If have hint, it means that the first spinner item should be disregarded. Therefore, -1.
                if (configPage.hint != 0) indexAnswerValue = position - 1;
                else indexAnswerValue = position;

                mListener.onAnswerSpinner(pageNumber, answerValue, indexAnswerValue);

                // It only goes to the next page if the selected item is valid.
                if (indexAnswerValue != -1) nextPage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        return (configPage.backgroundColor != 0) ? configPage.backgroundColor : Color.BLACK;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        if (configPage.backgroundColor != 0)
            getView().setBackgroundColor(configPage.backgroundColor);
    }

    /**
     * Set answer.
     *
     * @param value
     */
    public void setAnswer(String value) {
        super.isBlocked = false;
        int index = mAdapter.getPosition(value);
        if (index != -1) {
            indexAnswerValue = index;
            answerSpinner.setSelection(indexAnswerValue);
        }
    }

    /**
     * Get answer.
     *
     * @return
     */
    public String getAnswer() {
        return answerValue;
    }

    /**
     * Clear radiogroup checked.
     */
    public void clearAnswer() {
        super.isBlocked = true;
        answerValue = "";
        indexAnswerValue = -1;

        answerSpinner.setSelection(0);
    }

    /**
     * Class config page.
     */
    public static class ConfigPage extends BaseConfigPage<SpinnerPage.ConfigPage> implements Serializable {

        protected List<String> items;
        protected int colorTextItemSelected;
        protected int hint;

        public ConfigPage() {
            this.colorTextItemSelected = 0;
            this.hint = 0;
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

        @Override
        public String toString() {
            return "ConfigPage{" +
                    "items=" + items +
                    ", colorTextItemSelected=" + colorTextItemSelected +
                    ", hint=" + hint +
                    "} " + super.toString();
        }

        @Override
        public BasePage build() {
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
            txt.setPadding(35, 30, 35, 30);
            txt.setTextSize(15);
            txt.setText(_items.get(position));
            txt.setTextColor(Color.BLACK);

            // Set color hint message
            if (configPage.hint != 0 && position == 0) {
                txt.setPadding(25, 30, 35, 0);
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
     * Interface OnAnswerSpinnerListener.
     *
     * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
     * @version 1.0
     * @copyright Copyright (c) 2017, NUTES UEPB
     */
    public interface OnAnswerSpinnerListener extends OnClosePageListener {
        void onAnswerSpinner(int page, String value, int indexValue);
    }
}
