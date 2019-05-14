package br.edu.uepb.nutes.haniot.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;

import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * FragmentHeartRate implementation.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2019, NUTES UEPB
 */
public class FragmentHeartRate extends Fragment {

    final private int MIN_HEART_RATE = 60;

    @BindView(R.id.seekBar)
    SeekBar seekBar;

    EditText value;

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

        value = getActivity().findViewById(R.id.text_measurement);
        value.setText(String.valueOf(MIN_HEART_RATE + 28));
        init();
    }

    public void init() {
        seekBar.setProgress(MIN_HEART_RATE + 28);
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
                value.setText(String.valueOf(progress + MIN_HEART_RATE));
            }
        });
    }
}