package br.edu.uepb.nutes.haniot.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.AddMeasurementActivity;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * FragmentAnthropometrics implementation.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2019, NUTES UEPB
 */
public class FragmentAnthropometrics extends Fragment implements AddMeasurementActivity.MeasurementCommunicator {

    private final int MIN_WEASY = 50;
    private final int MIN_HEIGHT = 80;
    @BindView(R.id.height)
    SeekBar heightSeek;

    @BindView(R.id.waist)
    SeekBar waistSeek;

    @BindView(R.id.heightText)
    TextView heightText;

    @BindView(R.id.waistText)
    TextView waistText;

    @BindView(R.id.heigthIcon)
    ImageView patientIcon;

    private int waist;
    private int height;

    private AppPreferencesHelper appPreferencesHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anthropometrics_measurement,
                container, false);
        ButterKnife.bind(this, view);
        initView();
        waist = MIN_WEASY;
        height = MIN_HEIGHT;
        return view;
    }

    /**
     * Init view.
     */
    private void initView() {
        appPreferencesHelper = AppPreferencesHelper.getInstance(getContext());
        if (appPreferencesHelper.getLastPatient().getGender().equals("male"))
            patientIcon.setImageResource(R.drawable.boy);
        else patientIcon.setImageResource(R.drawable.girl);

        waistSeek.setProgress(MIN_HEIGHT);
        heightSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                height = progress + MIN_HEIGHT;
                heightText.setText(String.valueOf(height));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        waistSeek.setProgress(MIN_WEASY);
        waistSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                waist = progress + MIN_WEASY;
                waistText.setText(String.valueOf(waist));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public Measurement getMeasurement() {
        return null;
    }

    @Override
    public List<Measurement> getMeasurements() {
        List<Measurement> measurements = new ArrayList<>();
        Measurement measurement = new Measurement();
        measurement.setValue(waist);
        measurement.setUnit(getContext().getResources().getString(R.string.cm));
        measurement.setType("waist_circumference");
        measurements.add(measurement);

        Measurement measurement2 = new Measurement();
        measurement2.setValue(height);
        measurement2.setUnit(getContext().getResources().getString(R.string.cm));
        measurement2.setType("height");
        measurements.add(measurement2);

        return measurements;
    }
}
