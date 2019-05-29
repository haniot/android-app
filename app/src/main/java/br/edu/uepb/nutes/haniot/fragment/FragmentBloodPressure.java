package br.edu.uepb.nutes.haniot.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.AddMeasurementActivity;
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
public class FragmentBloodPressure extends Fragment implements AddMeasurementActivity.MeasurementCommunicator {

    final private int MIN_HEART_RATE = 60;

    @BindView(R.id.heart_rate_control)
    SeekBar seekBar;

    @BindView(R.id.value)
    TextView value;

    @BindView(R.id.text_systolic)
    EditText systolic;

    @BindView(R.id.text_diastolic)
    EditText diastolic;

    private int pulseValue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blood_pressure_measurement, container, false);
        ButterKnife.bind(this, view);
        pulseValue = MIN_HEART_RATE + 28;
        initView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    /**
     * Init views.
     */
    public void initView() {
        seekBar.setProgress(MIN_HEART_RATE + 28);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                value.setText(String.valueOf(progress + MIN_HEART_RATE));
                pulseValue = progress + MIN_HEART_RATE;
            }
        });
    }

    private boolean validate() {
        boolean validated = false;

        if (systolic.getText() != null) {
            validated = true;
        }

        if (diastolic.getText() != null) {
            validated = true;
        }

        return validated;
    }

    @Override
    public Measurement getMeasurement() {
        if (validate()) {
            Measurement measurement = new Measurement();
            measurement.setValue(pulseValue);
            measurement.setSystolic(Integer.parseInt(systolic.getText().toString()));
            measurement.setDiastolic(Integer.parseInt(diastolic.getText().toString()));
            measurement.setPulse(pulseValue);
            measurement.setUnit(getContext().getResources().getString(R.string.unit_glucose_mg_dL));
            measurement.setType("blood_pressure");
            return measurement;
        } else {
            return null;
        }
    }

    @Override
    public List<Measurement> getMeasurements() {
        return null;
    }
}
