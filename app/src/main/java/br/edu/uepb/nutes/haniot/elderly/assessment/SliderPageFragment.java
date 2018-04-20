package br.edu.uepb.nutes.haniot.elderly.assessment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroViewPager;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * SliderPageFragment implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class SliderPageFragment extends Fragment implements ISlideBackgroundColorHolder {
    private final String TAG = "SliderPageFragment";

    protected static final String ARG_LAYOUT = "arg_layout";
    protected static final String ARG_TITLE = "arg_title";
    protected static final String ARG_DESC = "arg_desc";
    protected static final String ARG_DRAWABLE = "arg_drawable";
    protected static final String ARG_BG_COLOR = "arg_bg_color";
    protected static final String ARG_TITLE_COLOR = "arg_title_color";
    protected static final String ARG_DESC_COLOR = "arg_desc_color";
    protected static final String ARG_PAGE_NUMBER = "arg_page_number";

    private OnAnswerListener mListener;
    private boolean isBlocked, answerValue;

    private int drawable, bgColor, titleColor, descColor, layoutId, pageNumber, oldCheckedRadio;
    private String title, description;

    @BindView(R.id.question_title)
    TextView titleTextView;

    @BindView(R.id.question_description)
    TextView descTextView;

    @BindView(R.id.question_image)
    ImageView imgTextView;

    @BindView(R.id.question_radioGroup)
    RadioGroup radioGroup;

    public SliderPageFragment() {
    }

    /**
     * New instance the {@link SliderPageFragment}.
     *
     * @param layoutId
     * @param title
     * @param description
     * @param imageDrawable
     * @param bgColor
     * @param numberPage
     * @return {@link SliderPageFragment}
     */
    public static SliderPageFragment newInstance(@LayoutRes int layoutId,
                                                 String title, String description,
                                                 @DrawableRes int imageDrawable,
                                                 @ColorInt int bgColor, int numberPage) {
        SliderPageFragment sliderPageFragment = new SliderPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT, layoutId);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESC, description);
        args.putInt(ARG_DRAWABLE, imageDrawable);
        args.putInt(ARG_BG_COLOR, bgColor);
        args.putInt(ARG_PAGE_NUMBER, numberPage);
        sliderPageFragment.setArguments(args);

        return sliderPageFragment;
    }

    /**
     * New instance the {@link SliderPageFragment}.
     *
     * @param layoutId
     * @param title
     * @param description
     * @param bgColor
     * @param numberPage
     * @return {@link SliderPageFragment}
     */
    public static SliderPageFragment newInstance(@LayoutRes int layoutId,
                                                 String title, String description,
                                                 @ColorInt int bgColor, int numberPage) {
        SliderPageFragment sliderPageFragment = new SliderPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT, layoutId);
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESC, description);
        args.putInt(ARG_BG_COLOR, bgColor);
        args.putInt(ARG_PAGE_NUMBER, numberPage);
        sliderPageFragment.setArguments(args);

        return sliderPageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setting default values
        oldCheckedRadio = -1;
        isBlocked = true;
        answerValue = false;

        // Retrieving arguments
        if (getArguments() != null && getArguments().size() != 0) {
            layoutId = getArguments().getInt(ARG_LAYOUT);
            title = getArguments().getString(ARG_TITLE);
            description = getArguments().getString(ARG_DESC);
            bgColor = getArguments().getInt(ARG_BG_COLOR);
            titleColor = getArguments().containsKey(ARG_TITLE_COLOR) ?
                    getArguments().getInt(ARG_TITLE_COLOR) : 0;
            descColor = getArguments().containsKey(ARG_DESC_COLOR) ?
                    getArguments().getInt(ARG_DESC_COLOR) : 0;
            drawable = getArguments().containsKey(ARG_DRAWABLE) ?
                    getArguments().getInt(ARG_DRAWABLE) : 0;
            pageNumber = getArguments().getInt(ARG_PAGE_NUMBER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layoutId, container, false);
        ButterKnife.bind(this, view);

        titleTextView.setText(title);
        if (titleColor != 0) titleTextView.setTextColor(titleColor);

        descTextView.setText(description);
        if (descColor != 0) descTextView.setTextColor(descColor);

        if (drawable != 0) imgTextView.setImageResource(drawable);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.yes_radioButton && oldCheckedRadio != 1) {
                oldCheckedRadio = 1;
                answerValue = true;

                mListener.onAnswer(getView(), answerValue, pageNumber);
                nextPage();
            } else if (checkedId == R.id.no_radioButton && oldCheckedRadio != 0) {
                oldCheckedRadio = 0;
                answerValue = false;

                mListener.onAnswer(getView(), answerValue, pageNumber);
                nextPage();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        radioGroup.setOnCheckedChangeListener(null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAnswerListener) {
            mListener = (OnAnswerListener) context;
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
        if (bgColor != 0)
            getView().setBackgroundColor(bgColor);
    }

    private void nextPage() {
        final AppIntro appIntro = (AppIntro) getContext();
        final AppIntroViewPager page = ((AppIntro) getContext()).getPager();

        isBlocked = false;
        appIntro.setNextPageSwipeLock(isBlocked);
        new Handler().post(() -> {
            page.goToNextSlide();
            appIntro.setNextPageSwipeLock(!isBlocked);
        });
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public RadioGroup getRadioGroup() {
        return radioGroup;
    }

    public int getOldCheckedRadio() {
        return oldCheckedRadio;
    }

    public int getPageNumber() {
        return pageNumber;
    }
}
