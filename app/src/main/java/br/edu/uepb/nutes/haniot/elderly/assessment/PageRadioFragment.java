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
import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroViewPager;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

/**
 * PageRadioFragment implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class PageRadioFragment extends Fragment implements ISlideBackgroundColorHolder {
    private final String TAG = "PageRadioFragment";

    protected static final String ARG_LAYOUT = "arg_layout";
    protected static final String ARG_TITLE = "arg_title";
    protected static final String ARG_DESC = "arg_desc";
    protected static final String ARG_DRAWABLE = "arg_drawable";
    protected static final String ARG_BG_COLOR = "arg_bg_color";
    protected static final String ARG_TITLE_COLOR = "arg_title_color";
    protected static final String ARG_DESC_COLOR = "arg_desc_color";
    protected static final String ARG_PAGE_NUMBER = "arg_page_number";

    private Unbinder unbinder;
    private OnAnswerRadioListener mListener;
    private boolean isBlocked, answerValue, actionClearCheck;

    private int drawable, bgColor, titleColor, descColor, layoutId, pageNumber, oldCheckedRadio;
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
    @BindView(R.id.question_radioGroup)
    RadioGroup radioGroup;

    public PageRadioFragment() {
    }

    /**
     * New instance the {@link PageRadioFragment}.
     *
     * @param layoutId
     * @param numberPage
     * @return {@link PageRadioFragment}
     */
    public static PageRadioFragment newInstance(@LayoutRes int layoutId, int numberPage) {
        PageRadioFragment sliderPageFragment = new PageRadioFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT, layoutId);
        args.putInt(ARG_PAGE_NUMBER, numberPage);
        sliderPageFragment.setArguments(args);

        return sliderPageFragment;
    }

    /**
     * New instance the {@link PageRadioFragment}.
     *
     * @param title
     * @param description
     * @param imageDrawable
     * @param bgColor
     * @param numberPage
     * @return {@link PageRadioFragment}
     */
    public static PageRadioFragment newInstance(String title,
                                                String description,
                                                @DrawableRes int imageDrawable,
                                                @ColorInt int bgColor,
                                                int numberPage) {
        PageRadioFragment sliderPageFragment = new PageRadioFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESC, description);
        args.putInt(ARG_DRAWABLE, imageDrawable);
        args.putInt(ARG_BG_COLOR, bgColor);
        args.putInt(ARG_PAGE_NUMBER, numberPage);
        sliderPageFragment.setArguments(args);

        return sliderPageFragment;
    }

    /**
     * New instance the {@link PageRadioFragment}.
     *
     * @param layoutId
     * @param description
     * @param bgColor
     * @param numberPage
     * @return {@link PageRadioFragment}
     */
    public static PageRadioFragment newInstance(@LayoutRes int layoutId,
                                                String description,
                                                @DrawableRes int imageDrawable,
                                                @ColorInt int bgColor,
                                                int numberPage) {
        PageRadioFragment sliderPageFragment = new PageRadioFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT, layoutId);
        args.putString(ARG_DESC, description);
        args.putInt(ARG_DRAWABLE, imageDrawable);
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
        actionClearCheck = false;

        // Retrieving arguments
        if (getArguments() != null && getArguments().size() != 0) {
            layoutId = getArguments().containsKey(ARG_LAYOUT) ? getArguments().getInt(ARG_LAYOUT) :
                    R.layout.fragment_radio_question;
            title = getArguments().containsKey(ARG_TITLE) ? getArguments().getString(ARG_TITLE) : "";
            description = getArguments().containsKey(ARG_DESC) ? getArguments().getString(ARG_DESC) : "";
            bgColor = getArguments().getInt(ARG_BG_COLOR);
            titleColor = getArguments().containsKey(ARG_TITLE_COLOR) ? getArguments().getInt(ARG_TITLE_COLOR) : 0;
            descColor = getArguments().containsKey(ARG_DESC_COLOR) ? getArguments().getInt(ARG_DESC_COLOR) : 0;
            drawable = getArguments().containsKey(ARG_DRAWABLE) ? getArguments().getInt(ARG_DRAWABLE) : 0;
            pageNumber = getArguments().getInt(ARG_PAGE_NUMBER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layoutId, container, false);
        unbinder = ButterKnife.bind(this, view);

        if (titleTextView != null) {
            titleTextView.setText(title);
            if (titleColor != 0) titleTextView.setTextColor(titleColor);
        }

        if (descTextView != null) {
            descTextView.setText(description);
            if (descColor != 0) descTextView.setTextColor(descColor);
        }

        if (imgTextView != null && drawable != 0) imgTextView.setImageResource(drawable);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (radioGroup == null) return;

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (actionClearCheck) return;

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
        unbinder.unbind();
        if (radioGroup != null) radioGroup.setOnCheckedChangeListener(null);
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
        return Color.parseColor("#000000");
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        if (bgColor != 0)
            getView().setBackgroundColor(bgColor);
    }

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

    public boolean isBlocked() {
        return isBlocked;
    }

    public RadioGroup getRadioGroup() {
        return radioGroup;
    }

    public void selectAnswerTrue() {
        getRadioGroup().check(R.id.yes_radioButton);
    }

    public void selectAnswerFalse() {
        getRadioGroup().check(R.id.no_radioButton);
    }

    public void clearCheck() {
        actionClearCheck = true;
        radioGroup.clearCheck();
        actionClearCheck = false;
    }

    public int getOldCheckedRadio() {
        return oldCheckedRadio;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Interface OnAnswerRadioListener.
     *
     * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
     * @version 1.0
     * @copyright Copyright (c) 2017, NUTES UEPB
     */
    public interface OnAnswerRadioListener {
        void onAnswer(View view, boolean value, int page);
    }
}
