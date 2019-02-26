package br.edu.uepb.nutes.haniot.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentGlucose extends Fragment {

    @BindView(R.id.check_before_bedtime)
    ImageView checkBeforeBedtime;

    @BindView(R.id.check_fast)
    ImageView checkFast;

    @BindView(R.id.check_fasting)
    ImageView checkFasting;

    @BindView(R.id.check_pre_meal)
    ImageView checkPreMeal;

    @BindView(R.id.before_bedtime)
    ImageView beforeBedtime;

    @BindView(R.id.fast)
    ImageView fast;

    @BindView(R.id.fasting)
    ImageView fasting;

    @BindView(R.id.pre_meal)
    ImageView preMeal;

    @BindView(R.id.title_before_bedtime)
    TextView titleBeforeBedtime;

    @BindView(R.id.title_fast)
    TextView titleFast;

    @BindView(R.id.title_fasting)
    TextView titleFasting;

    @BindView(R.id.title_pre_meal)
    TextView titlePreMeal;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_glucose_measurement, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //ButterKnife.bind(Objects.requireNonNull(getActivity()));
        super.onActivityCreated(savedInstanceState);
        initChoose();
       // initView();
        //resetPick();

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

    public void resetPick() {
        fasting.setScaleX(0.9f);
        fasting.setScaleY(0.9f);
        titleFasting.setTextColor(getContext().getResources().getColor(R.color.colorSubmenu));
        checkFasting.setVisibility(View.INVISIBLE);

        fast.setScaleX(0.9f);
        fast.setScaleY(0.9f);
        titleFast.setTextColor(getContext().getResources().getColor(R.color.colorSubmenu));
        checkFast.setVisibility(View.INVISIBLE);

        beforeBedtime.setScaleX(0.9f);
        beforeBedtime.setScaleY(0.9f);
        titleBeforeBedtime.setTextColor(getContext().getResources().getColor(R.color.colorSubmenu));
        checkBeforeBedtime.setVisibility(View.INVISIBLE);

        preMeal.setScaleX(0.9f);
        preMeal.setScaleY(0.9f);
        titlePreMeal.setTextColor(getContext().getResources().getColor(R.color.colorSubmenu));
        checkPreMeal.setVisibility(View.INVISIBLE);
    }

    public void choose(ImageView choose) {
        resetPick();
        choose.setVisibility(View.VISIBLE);
    }

    public void initChoose() {
        preMeal.setOnClickListener(v -> {
            Log.i("Glucose", "cliquei");
            resetPick();
            preMeal.setScaleX(1);
            preMeal.setScaleY(1);
            titlePreMeal.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            checkPreMeal.setVisibility(View.VISIBLE);
        });
        beforeBedtime.setOnClickListener(v -> {
            resetPick();
            beforeBedtime.setScaleX(1);
            beforeBedtime.setScaleY(1);
            titleBeforeBedtime.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            checkBeforeBedtime.setVisibility(View.VISIBLE);
        });
        fast.setOnClickListener(v -> {
            resetPick();
            fast.setScaleX(1);
            fast.setScaleY(1);
            titleFast.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            checkFast.setVisibility(View.VISIBLE);
        });
        fasting.setOnClickListener(v -> {
            resetPick();
            fasting.setScaleX(1);
            fasting.setScaleY(1);
            titleFasting.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            checkFasting.setVisibility(View.VISIBLE);
        });
    }
}
