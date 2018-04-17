package br.edu.uepb.nutes.haniot.elderly.assessment;

import android.content.Context;
import android.content.SharedPreferences;
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
    private static final String ARG_IS_BLOCKED = "arg_is_blocked";
    private static Context mContext;

    private int currenValue;
    private int layoutResId;
    private int backColorResId;
    private OnResponseListener mListener;
    private View view;

    SharedPreferences preferences;
    SharedPreferences.Editor preferencesEditor;

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
        Log.d("TEST", "SliderPagerFragment()");
        SliderPagerFragment sliderPager = new SliderPagerFragment();
        mContext = context;

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        args.putBoolean(ARG_IS_BLOCKED, false);
        sliderPager.setArguments(args);

        return sliderPager;
    }

    /**
     * Get instance fragment.
     *
     * @param layoutResId
     * @param backColorResId
     * @return SliderPagerFragment
     */
    public static SliderPagerFragment newInstance(Context context, int layoutResId, int backColorResId) {
        SliderPagerFragment sliderPager = new SliderPagerFragment();
        mContext = context;

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        args.putInt(ARG_BACK_COLOR_RES_ID, backColorResId);
        args.putBoolean(ARG_IS_BLOCKED, false);
        sliderPager.setArguments(args);

        return sliderPager;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        initSharedPreferences();
        currenValue = -1;

        Log.d("TEST", "onCreate()");
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
        Log.d("TEST", "onCreateView()");

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("TEST", "onActivityCreated()");

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.yes_radioButton && currenValue != 1) {
                currenValue = 1;
                mListener.onResponse(true, layoutResId);
                nextPage();
            } else if (checkedId == R.id.no_radioButton && currenValue != 0) {
                currenValue = 0;
                mListener.onResponse(false, layoutResId);
                nextPage();
            }

//                switch (checkedId) {
//                    case R.id.yes_radioButton:
//                        Log.d(TAG, "yes_radioButton()" + layoutResId);
//                        mListener.onResponse(true, layoutResId);
//                        break;
//                    case R.id.no_radioButton:
//                        Log.d(TAG, "no_radioButton()" + layoutResId);
//                        mListener.onResponse(false, layoutResId);
//                        break;
//                    default:
//                        Log.d(TAG, "default()" + layoutResId);
//                        break;
//                }
            /**
             * Save in shared preferences as answered question
             */
//            preferencesEditor.putBoolean(String.valueOf(layoutResId), true).commit();

        });
    }

    @Override
    public void onDestroyView() {
        Log.d("TEST", "onDestroyView()");
        super.onDestroyView();
        radioGroup.setOnCheckedChangeListener(null);
    }

    @Override
    public void onAttach(Context context) {
        Log.d("TEST", "onAttach()");
        super.onAttach(context);
        if (context instanceof OnResponseListener) {
            mListener = (OnResponseListener) context;
        } else {
            throw new ClassCastException();
        }
    }

    @Override
    public void onDetach() {
        Log.d("TEST", "onDetach()");
        super.onDetach();
        mListener = null;
    }

    @Override
    public int getDefaultBackgroundColor() {
        return backColorResId;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        Log.d("TEST", "setBackgroundColor()");
        if (backColorResId != 0)
            view.setBackgroundColor(backColorResId);
    }

    public interface OnResponseListener {
        void onResponse(boolean response, int layoutResId);

        void nextBlockPage(boolean block);
    }

//    public void nextPageBlock(boolean block) {
//        mListener.nextBlockPage(block);
//        if (mContext instanceof AppIntro) {
//            AppIntro appIntro = (AppIntro) mContext;
//            appIntro.onSlideChanged(this, null);
//            appIntro.setSwipeLock(block);
//            appIntro.setNextPageSwipeLock(block);
//        }
//    }

    private void nextPage() {
        final AppIntro appIntro = (AppIntro) mContext;
        final AppIntroViewPager page = ((AppIntro) mContext).getPager();

        appIntro.setNextPageSwipeLock(false);
        new Handler().post(() -> {
            page.goToNextSlide();
            appIntro.setNextPageSwipeLock(true);
        });
    }

    private void initSharedPreferences() {
        if (preferences == null) {
            preferences = this.getActivity().getSharedPreferences("assessment_pref", Context.MODE_PRIVATE);
        } else {
            preferencesEditor.clear().commit();
        }
        preferencesEditor = preferences.edit();
    }
}
