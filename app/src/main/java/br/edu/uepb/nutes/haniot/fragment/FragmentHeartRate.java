package br.edu.uepb.nutes.haniot.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.AddMeasurementActivity;
import br.edu.uepb.nutes.haniot.data.model.HeartRateItem;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * FragmentHeartRate implementation.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2019, NUTES UEPB
 */
public class FragmentHeartRate extends Fragment implements AddMeasurementActivity.MeasurementCommunicator {

    final private int MIN_HEART_RATE = 60;

    @BindView(R.id.seekBar)
    SeekBar seekBar;

    EditText value;

    int heartValue;

    public FragmentHeartRate() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_heart_rate_measurement,
                container, false);
        ButterKnife.bind(this, view);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        value = getActivity().findViewById(R.id.text_systolic);
        value.setText(String.valueOf(MIN_HEART_RATE + 28));
        heartValue = MIN_HEART_RATE + 28;
        init();
    }

    public void init() {
        seekBar.setProgress(MIN_HEART_RATE + 28);
        value.setText(String.valueOf(heartValue+28));

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
                heartValue = progress + MIN_HEART_RATE;
                value.setText(String.valueOf(heartValue));
            }
        });
    }

    @Override
    public Measurement getMeasurement() {
        Measurement measurement = new Measurement();
        HeartRateItem heartRateItem = new HeartRateItem();

        heartRateItem.setValue(heartValue);
        List<HeartRateItem> heartRateItems = new ArrayList<>();
        heartRateItems.add(heartRateItem);

        measurement.setDataset(heartRateItems);
        measurement.setType("heart_rate");
        measurement.setUnit(getContext().getResources().getString(R.string.unit_heart_rate));
        return measurement;
    }

    @Override
    public List<Measurement> getMeasurementList() {
        return null;
    }
}