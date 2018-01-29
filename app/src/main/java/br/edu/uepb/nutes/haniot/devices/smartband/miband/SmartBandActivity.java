package br.edu.uepb.nutes.haniot.devices.smartband.miband;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.ButterKnife;


public class SmartBandActivity extends AppCompatActivity {
    private final String TAG = "SmartBandActivity";

    @BindView(R.id.name_device)
    TextView mDeviceName;

    @BindView(R.id.batery)
    TextView mBateryLevel;

    @BindView(R.id.steps)
    TextView mSteps;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_band);

        ButterKnife.bind(this);
    }


    @Override
    protected void onStart() {
        Log.i(TAG, "onStart()");
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
