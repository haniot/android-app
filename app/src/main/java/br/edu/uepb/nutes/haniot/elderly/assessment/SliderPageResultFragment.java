package br.edu.uepb.nutes.haniot.elderly.assessment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * SliderPageResultFragment implementation.
 * Represents the final screen to display the evaluation result.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class SliderPageResultFragment extends Fragment implements ISlideBackgroundColorHolder {
    private final String TAG = "SliderPageFragment";

    protected static final String ARG_LAYOUT = "arg_layout";
    protected static final String ARG_BG_COLOR = "arg_bg_color";
    protected static final String ARG_PAGE_NUMBER = "arg_page_number";

    private OnAnswerListener mListener;
    private int bgColor, layoutId, pageNumber;

    @BindView(R.id.cancel_button)
    Button cancelButton;

    @BindView(R.id.ok_button)
    Button okButton;

    @BindView(R.id.fall_risk_end_progressBar)
    ProgressBar progressBar;

    public SliderPageResultFragment() {
    }

    public static SliderPageResultFragment newInstance(@LayoutRes int layoutId, @ColorInt int bgColor, int numberPage) {
        SliderPageResultFragment sliderPageFragment = new SliderPageResultFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT, layoutId);
        args.putInt(ARG_BG_COLOR, bgColor);
        args.putInt(ARG_PAGE_NUMBER, numberPage);
        sliderPageFragment.setArguments(args);

        return sliderPageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().size() != 0) {
            layoutId = getArguments().getInt(ARG_LAYOUT);
            bgColor = getArguments().getInt(ARG_BG_COLOR);
            pageNumber = getArguments().getInt(ARG_PAGE_NUMBER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layoutId, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        cancelButton.setOnClickListener((v)-> {
            mListener.onAnswer(getView(), false, pageNumber);
        });

        okButton.setOnClickListener((v)-> {
            mListener.onAnswer(getView(), true, pageNumber);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelButton.setOnClickListener(null);
        okButton.setOnClickListener(null);
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
        return bgColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        if (bgColor != 0)
            getView().setBackgroundColor(bgColor);
    }
}
