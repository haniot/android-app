package br.edu.uepb.nutes.haniot.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.AddMeasurementActivity;
import br.edu.uepb.nutes.haniot.data.model.MealGlucoseType;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * FragmentBloodPressure implementation.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2019, NUTES UEPB
 */
public class FragmentGlucose extends Fragment implements AddMeasurementActivity.MeasurementCommunicator {

    @BindView(R.id.check_before_bedtime)
    ImageView checkBeforeBedtime;

    @BindView(R.id.check_fast)
    ImageView checkFast;

    @BindView(R.id.check_fasting)
    ImageView checkFasting;

    @BindView(R.id.check_pre_meal)
    ImageView checkPreMeal;

    @BindView(R.id.check_post_meal)
    ImageView checkPostMeal;

    @BindView(R.id.before_bedtime)
    ImageView beforeBedtime;

    @BindView(R.id.fast)
    ImageView fast;

    @BindView(R.id.fasting)
    ImageView fasting;

    @BindView(R.id.pre_meal)
    ImageView preMeal;

    @BindView(R.id.post_meal)
    ImageView postMeal;

    @BindView(R.id.title_before_bedtime)
    TextView titleBeforeBedtime;

    @BindView(R.id.title_fast)
    TextView titleFast;

    @BindView(R.id.title_fasting)
    TextView titleFasting;

    @BindView(R.id.title_pre_meal)
    TextView titlePreMeal;

    @BindView(R.id.title_post_meal)
    TextView titlePostMeal;

    private String period;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_glucose_measurement, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        period = MealGlucoseType.FASTING;
    }

    /**
     * Update view on choose option.
     *
     * @param choose
     * @param chooseText
     * @param check
     */
    public void choose(ImageView choose, TextView chooseText, ImageView check) {
        resetPick();
        choose.setScaleX(1);
        choose.setScaleY(1);
        chooseText.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
        check.setVisibility(View.VISIBLE);

    }

    /**
     * Init views.
     */
    public void initViews() {
        preMeal.setOnClickListener(v -> {
            choose(preMeal, titlePreMeal, checkPreMeal);
            period = MealGlucoseType.PREPRANDIAL;
        });
        postMeal.setOnClickListener(v -> {
            choose(postMeal, titlePostMeal, checkPostMeal);
            period = MealGlucoseType.POSTPRANDIAL;
        });
        beforeBedtime.setOnClickListener(v -> {
            choose(beforeBedtime, titleBeforeBedtime, checkBeforeBedtime);
            period = MealGlucoseType.BEDTIME;
        });
        fast.setOnClickListener(v -> {
            choose(fast, titleFast, checkFast);
            period = MealGlucoseType.CASUAL;
        });
        fasting.setOnClickListener(v -> {
            period = MealGlucoseType.FASTING;
            choose(fasting, titleFasting, checkFasting);
        });
    }

    /**
     * Reset view of options.
     */
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

        postMeal.setScaleX(0.9f);
        postMeal.setScaleY(0.9f);
        titlePostMeal.setTextColor(getContext().getResources().getColor(R.color.colorSubmenu));
        checkPostMeal.setVisibility(View.INVISIBLE);
    }

    @Override
    public Measurement getMeasurement() {
        Measurement measurement = new Measurement();
        measurement.setMeal(period);
        measurement.setType("blood_glucose");
        measurement.setUnit(getString(R.string.unit_glucose_mg_dL));
        return measurement;
    }

    @Override
    public List<Measurement> getMeasurements() {
        return null;
    }
}
