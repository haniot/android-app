package br.edu.uepb.nutes.haniot.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.devices.BloodPressureHDPActivity;
import br.edu.uepb.nutes.haniot.devices.BodyCompositionMonitorHDPActivity;
import br.edu.uepb.nutes.haniot.devices.GlucoseActivity;
import br.edu.uepb.nutes.haniot.devices.HeartRateActivity;
import br.edu.uepb.nutes.haniot.devices.ScaleActivity;
import br.edu.uepb.nutes.haniot.devices.ThermometerActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Fragments to list the devices supported by the app and thus establish a connection according
 * to the user's interest.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class ConnectDeviceFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.device_thermometer)
    ImageView imageViewThermometer;

    @BindView(R.id.device_glucose)
    ImageView imageViewGlucose;

    @BindView(R.id.device_blood_pressure_hdp)
    ImageView imageViewBloodPressureHDP;

    @BindView(R.id.device_body_composition_hdp)
    ImageView imageViewBodyCompositionHDP;

    @BindView(R.id.device_scale_ble)
    ImageView btScaleBle;

    @BindView(R.id.device_heart_rate_h10)
    ImageView btHeartRateH10;

    @BindView(R.id.device_heart_rate_h7)
    ImageView btHeartRateH7;

    public ConnectDeviceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_connect_device, container, false);
        ButterKnife.bind(this, view);

        getActivity().invalidateOptionsMenu();

        // Set a Toolbar to replace the ActionBar.
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setTitle(R.string.action_connect_devices);

        // Set events click
        imageViewThermometer.setOnClickListener(this);
        imageViewGlucose.setOnClickListener(this);
        //imageViewBloodPressure.setOnClickListener(this);
        imageViewBloodPressureHDP.setOnClickListener(this);
        imageViewBodyCompositionHDP.setOnClickListener(this);
        btScaleBle.setOnClickListener(this);
        btHeartRateH10.setOnClickListener(this);
        btHeartRateH7.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.device_thermometer:
                startActivity(new Intent(getContext(), ThermometerActivity.class));
                break;
            case R.id.device_glucose:
                startActivity(new Intent(getContext(), GlucoseActivity.class));
                break;
            case R.id.device_blood_pressure_hdp:
                startActivity(new Intent(getContext(), BloodPressureHDPActivity.class));
                break;
            case R.id.device_body_composition_hdp:
                startActivity(new Intent(getContext(), BodyCompositionMonitorHDPActivity.class));
                break;
            case R.id.device_scale_ble:
                startActivity(new Intent(getContext(), ScaleActivity.class));
                break;
            case R.id.device_heart_rate_h10:
                Intent intent = new Intent(getContext(), HeartRateActivity.class);
                intent.putExtra(HeartRateActivity.EXTRA_DEVICE_ADDRESS, "E9:50:60:1F:31:D2");
                intent.putExtra(HeartRateActivity.EXTRA_DEVICE_INFORMATIONS, new String[]{"POLAR", "H10"});
                startActivity(intent);
                break;
            case R.id.device_heart_rate_h7:
                Intent it = new Intent(getContext(), HeartRateActivity.class);
                it.putExtra(HeartRateActivity.EXTRA_DEVICE_ADDRESS, "00:22:D0:BA:95:80");
                it.putExtra(HeartRateActivity.EXTRA_DEVICE_INFORMATIONS, new String[]{"POLAR", "H7"});
                startActivity(it);
                break;
            default:
                break;

        }
    }
}
