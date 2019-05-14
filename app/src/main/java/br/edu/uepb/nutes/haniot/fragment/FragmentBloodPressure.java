package br.edu.uepb.nutes.haniot.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * FragmentBloodPressure implementation.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2019, NUTES UEPB
 */
public class FragmentBloodPressure extends Fragment {

    final private int MIN_HEART_RATE = 60;

    @BindView(R.id.heart_rate_control)
    SeekBar seekBar;

    @BindView(R.id.value)
    TextView value;

    private int valueMeasurement;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blood_pressure_measurement, container, false);
        ButterKnife.bind(this, view);
        valueMeasurement = MIN_HEART_RATE + 28;
        initView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    /**
     * Get value measurement.
     * @return
     */
    public int getValueMeasurement() {
        return valueMeasurement;
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
                valueMeasurement = progress + MIN_HEART_RATE;
            }
        });
    }
}
