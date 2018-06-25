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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroViewPager;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * PageSpinner implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2018, NUTES UEPB
 */
public class PageSpinner extends Fragment implements ISlideBackgroundColorHolder {
    private final String TAG = "PageSpinner";

    protected static final String ARG_LAYOUT = "arg_layout";
    protected static final String ARG_TITLE = "arg_title";
    protected static final String ARG_DESC = "arg_desc";
    protected static final String ARG_DRAWABLE = "arg_drawable";
    protected static final String ARG_BG_COLOR = "arg_bg_color";
    protected static final String ARG_TITLE_COLOR = "arg_title_color";
    protected static final String ARG_DESC_COLOR = "arg_desc_color";
    protected static final String ARG_PAGE_NUMBER = "arg_page_number";
    protected static final String ARG_DRAWABLE_CLOSE = "arg_drawable_close";
    protected static final String ARG_ITEM_LAYOUT = "arg_item_layout";
    protected static final String ARG_ITEMS = "arg_items";

    private Unbinder unbinder;
    private OnAnswerSpinnerListener mListener;
    private boolean isBlocked;
    private String answerValue;
    private int answerValueIndex;

    private int layout,
            itemLayout,
            drawable,
            backgroundColor,
            titleColor,
            descriptionColor,
            drawableClose,
            pageNumber;
    private List<String> items;

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

    @Nullable
    @BindView(R.id.close_imageButton)
    ImageButton closeImageButton;

    @BindView(R.id.answer_spinner)
    Spinner answerSpinner;

    public PageSpinner() {
    }

    /**
     * New PageRadio instance.
     *
     * @param ConfigPage
     * @return PageRadio
     */
    private static PageSpinner newInstance(ConfigPage ConfigPage) {
        PageSpinner pageFragment = new PageSpinner();
        Bundle args = new Bundle();

        args.putInt(ARG_LAYOUT, ConfigPage.layout);
        args.putInt(ARG_ITEM_LAYOUT, ConfigPage.itemLayout);
        args.putInt(ARG_TITLE, ConfigPage.title);
        args.putInt(ARG_DESC, ConfigPage.description);
        args.putInt(ARG_DRAWABLE, ConfigPage.drawable);
        args.putInt(ARG_BG_COLOR, ConfigPage.backgroundColor);
        args.putInt(ARG_TITLE_COLOR, ConfigPage.titleColor);
        args.putInt(ARG_DESC_COLOR, ConfigPage.descriptionColor);
        args.putInt(ARG_DRAWABLE_CLOSE, ConfigPage.drawableClose);
        args.putInt(ARG_PAGE_NUMBER, ConfigPage.pageNumber);
        args.putStringArrayList(ARG_ITEMS, (ArrayList<String>) ConfigPage.items);
        pageFragment.setArguments(args);

        return pageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting default values
        isBlocked = true;

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
            drawableClose = getArguments().getInt(ARG_DRAWABLE_CLOSE);
            itemLayout = getArguments().getInt(ARG_ITEM_LAYOUT);
            pageNumber = getArguments().getInt(ARG_PAGE_NUMBER);
            items = getArguments().getStringArrayList(ARG_ITEMS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (items != null) {
//            ArrayAdapter<String> mAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, items);
//            // Drop down layout style - list view with radio button
//            mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            answerSpinner.setAdapter(mAdapter);


            CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(getContext(), items);
            answerSpinner.setAdapter(customSpinnerAdapter);
        }

        if (titleTextView != null) {
            titleTextView.setText(title);
            if (titleColor != 0) titleTextView.setTextColor(titleColor);
        }

        if (descTextView != null) {
            descTextView.setText(description);
            if (descriptionColor != 0) descTextView.setTextColor(descriptionColor);
        }

        if (closeImageButton != null) {
            if (drawableClose != 0) closeImageButton.setImageResource(drawableClose);
            else closeImageButton.setVisibility(View.GONE);
        }

        if (imgTextView != null && this.drawable != 0) imgTextView.setImageResource(drawable);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        answerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View view, int pos, long id) {
                Log.d(TAG, "value: " + parentView.getItemAtPosition(pos) + " pos: " + pos);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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
     * Set Answer.
     *
     * @param value boolean
     */
    public void setAnswer(boolean value) {

    }

    /**
     * Clear radiogroup checked.
     */
    public void clearAnswer() {
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
     * Remove button close.
     */
    public void removeButtonClose() {
        if (closeImageButton != null) {
            closeImageButton.setVisibility(View.GONE);
        }
    }

    /**
     * Class config page.
     */
    public static class ConfigPage {
        private int layout,
                itemLayout,
                title,
                description,
                drawable,
                backgroundColor,
                titleColor,
                descriptionColor,
                pageNumber,
                drawableClose;

        private List<String> items;

        public ConfigPage() {
            this.layout = R.layout.question_spinner_default;
            this.itemLayout = R.layout.item_spinner_white;
            this.title = 0;
            this.description = 0;
            this.drawable = 0;
            this.backgroundColor = 0;
            this.titleColor = 0;
            this.descriptionColor = 0;
            this.drawableClose = 0;
            this.items = null;
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
         * Set layout item.
         *
         * @param itemLayout
         * @return ConfigPage
         */
        public ConfigPage itemLayout(int itemLayout) {
            this.itemLayout = itemLayout;
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
         * Set page number.
         *
         * @param pageNumber
         * @return ConfigPage
         */
        public ConfigPage pageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
            return this;
        }

        /**
         * Set drawable button close.
         *
         * @param drawableClose
         * @return ConfigPage
         */
        public ConfigPage drawableButtonClose(int drawableClose) {
            this.drawableClose = drawableClose;
            return this;
        }

        /**
         * Set list items.
         *
         * @param items
         * @return ConfigPage
         */
        public ConfigPage addItems(List<String> items) {
            this.items = items;
            return this;
        }

        public Fragment build() {
            return PageSpinner.newInstance(this);
        }
    }

    public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

        private final Context context;
        private List<String> asr;

        public CustomSpinnerAdapter(Context context, List<String> asr) {
            this.asr = asr;
            this.context = context;
        }

        public int getCount() {
            return asr.size();
        }

        public Object getItem(int i) {
            return asr.get(i);
        }

        public long getItemId(int i) {
            return (long) i;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(context);
            txt.setPadding(25, 25, 25, 25);
            txt.setTextSize(16);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(asr.get(position));
            txt.setTextColor(Color.BLACK);
            return txt;
        }

        public View getView(int i, View view, ViewGroup viewgroup) {
            TextView txt = new TextView(context);
            txt.setGravity(Gravity.CENTER);
            txt.setPadding(25, 25, 25, 25);
            txt.setTextSize(16);
            txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_action_next_down, 0);
            txt.setText(asr.get(i));
            txt.setTextColor(Color.BLACK);
            return txt;
        }

    }


    /**
     * Interface OnAnswerRadioListener.
     *
     * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
     * @version 1.0
     * @copyright Copyright (c) 2017, NUTES UEPB
     */
    public interface OnAnswerSpinnerListener extends OnPageCloseListener {
        void onAnswerSpinner(int page, boolean value);
    }
}
