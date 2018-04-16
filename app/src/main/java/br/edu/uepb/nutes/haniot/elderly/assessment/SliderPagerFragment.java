package br.edu.uepb.nutes.haniot.elderly.assessment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroBase;
import com.github.paolorotolo.appintro.AppIntroViewPager;
import com.github.paolorotolo.appintro.ISlideBackgroundColorHolder;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.utils.Log;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * SliderPagerFragment implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class SliderPagerFragment extends Fragment implements ISlideBackgroundColorHolder {
    private final String TAG = "SliderPagerFragment";
    private static final String ARG_LAYOUT_RES_ID = "arg_layout_id";
    private static final String ARG_BACK_COLOR_RES_ID = "arg_back_color_id";
    private static Context mContext;

    private int layoutResId;
    private int backColorResId;
    private OnResponseListener mListener;
    private View view;
    private boolean isBlocked;

    @BindView(R.id.question_radioGroup)
    RadioGroup radioGroup;

    @BindView(R.id.yes_radioButton)
    RadioButton radioYes;

    @BindView(R.id.no_radioButton)
    RadioButton radioNo;

    public SliderPagerFragment() {
    }

    /**
     * Get instance fragment.
     *
     * @param layoutResId
     * @return SliderPagerFragment
     */
    public static SliderPagerFragment newInstance(Context context, int layoutResId) {
        SliderPagerFragment sampleSlide = new SliderPagerFragment();
        mContext = context;

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    /**
     * Get instance fragment.
     *
     * @param layoutResId
     * @param backColorResId
     * @return SliderPagerFragment
     */
    public static SliderPagerFragment newInstance(Context context, int layoutResId, int backColorResId) {
        SliderPagerFragment sampleSlide = new SliderPagerFragment();
        mContext = context;

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        args.putInt(ARG_BACK_COLOR_RES_ID, backColorResId);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TEST", "onCreate() isBlocked: " + isBlocked);
        nextPageBlock(true);

        if (getArguments() != null) {
            if (getArguments().containsKey(ARG_LAYOUT_RES_ID))
                layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
            if (getArguments().containsKey(ARG_BACK_COLOR_RES_ID))
                backColorResId = getArguments().getInt(ARG_BACK_COLOR_RES_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(layoutResId, container, false);
        ButterKnife.bind(this, view);
        Log.d("TEST", "onCreateView()" + "isBlockeed:" + isBlocked);

        if (radioGroup.getCheckedRadioButtonId() == -1) // no checked
            nextPageBlock(true);
        else
            nextPageBlock(false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Log.d(TAG, "setOnCheckedChangeListener()");
            switch (checkedId) {
                case R.id.yes_radioButton:
                    Log.d(TAG, "yes_radioButton()" + layoutResId);
                    mListener.onResponse(true, layoutResId);
                    break;
                case R.id.no_radioButton:
                    Log.d(TAG, "no_radioButton()" + layoutResId);
                    mListener.onResponse(false, layoutResId);
                    break;
                default:
                    Log.d(TAG, "default()" + layoutResId);
                    break;
            }
            nextPageBlock(false);
        });
    }

    private void nextPage() {
        final AppIntroViewPager page = ((AppIntro) mContext).getPager();
        new Handler().post(() -> {
            page.goToNextSlide();
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnResponseListener) {
            mListener = (OnResponseListener) context;
        } else {
            throw new ClassCastException();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.d("TEST", "onDetach()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("TEST", "onDestroy()");
    }

    @Override
    public int getDefaultBackgroundColor() {
        return backColorResId;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        if (backColorResId != 0)
            view.setBackgroundColor(backColorResId);
    }

    public interface OnResponseListener {
        void onResponse(boolean response, int layoutResId);

        void nextBlockPage(boolean block);
    }

    public void nextPageBlock(boolean block) {
        mListener.nextBlockPage(block);
        isBlocked = block;
        if (mContext instanceof AppIntro) {
            AppIntro appIntro = (AppIntro) mContext;
            appIntro.setNextPageSwipeLock(block);
//            appIntro.setBackButtonVisibilityWithDone(block);
        }
    }
}
