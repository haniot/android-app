package br.edu.uepb.nutes.haniot.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

import br.edu.uepb.nutes.haniot.R;
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
public class FragmentAnthropometrics extends Fragment {

    @BindView(R.id.height)
    SeekBar height;
   @BindView(R.id.height2)
    SeekBar cintura;

    @BindView(R.id.meters)
    EditText meters;

    @BindView(R.id.meters2)
    EditText cm;

    @BindView(R.id.patient_icon)
    ImageView patientIcon;

    private double heightValue;

    private AppPreferencesHelper appPreferencesHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anthropometrics_measurement,
                container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    /**
     * Get height value.
     *
     * @return
     */
    public double getHeightValue() {
        return heightValue;
    }

    /**
     * Init view.
     */
    private void initView() {
        appPreferencesHelper = AppPreferencesHelper.getInstance(getContext());
        if (appPreferencesHelper.getLastPatient().getGender().equals(getResources().getString(R.string.gender_male)))
            patientIcon.setImageResource(R.drawable.boy);
        else patientIcon.setImageResource(R.drawable.girl);

        height.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                meters.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        cintura.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                cm.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
