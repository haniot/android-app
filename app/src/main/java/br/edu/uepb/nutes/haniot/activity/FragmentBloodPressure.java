package br.edu.uepb.nutes.haniot.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentBloodPressure extends Fragment {

    @BindView(R.id.seekBar)
    SeekBar seekBar;

    @BindView(R.id.value)
    TextView value;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_blood_pressure_manually, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //ButterKnife.bind(Objects.requireNonNull(getActivity()));
        super.onActivityCreated(savedInstanceState);
        //seekBar = getActivity().findViewById(R.id.seekBar);

        // initView();
        //resetPick();

    }

    public void init() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            //TODO Auto-generated method stub

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            //TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                value.setText(String.valueOf(progress));
            }
        });
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//             //   int x = seekBar.getThumb().getBounds().left;
//                //set the left value to textview x value
//                //seekBar.setX(x);
//             //   seekBar.setY(10);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
    }

//    public void initView() {
//
//        checkBeforeBedtime = getActivity().findViewById(R.id.check_before_bedtime);
//
//        checkFast = getActivity().findViewById(R.id.check_fast);
//
//        checkFasting = getActivity().findViewById(R.id.check_fasting);
//
//        checkPreMeal = getActivity().findViewById(R.id.check_pre_meal);
//        initChoose();
//    }

//    public class MySeekBar extends android.support.v7.widget.AppCompatSeekBar {
//
//        public MySeekBar(Context context, AttributeSet attrs) {
//            super(context, attrs);
//// TODO Auto-generated constructor stub
//        }
//
//        Drawable mThumb;
//
//        @Override
//        public void setThumb(Drawable thumb) {
//            super.setThumb(thumb);
//            mThumb = thumb;
//        }
//
//        public Drawable getSeekBarThumb() {
//            return mThumb;
//        }
//    }
}
