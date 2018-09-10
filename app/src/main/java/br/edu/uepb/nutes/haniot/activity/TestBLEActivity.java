package br.edu.uepb.nutes.haniot.activity;

import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.List;

import br.edu.uepb.nutes.blesimpleconnect.BLEScanner;
import br.edu.uepb.nutes.blesimpleconnect.GattAttributes;
import br.edu.uepb.nutes.haniot.R;

public class TestBLEActivity extends AppCompatActivity {
    private final String TAG = "TESTE";
    BLEScanner bleScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste_ble);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bleScanner = new BLEScanner.Builder()
                        .addFilterServiceUuid(GattAttributes.SERVICE_HEART_RATE, GattAttributes.SERVICE_HEALTH_THERMOMETER, GattAttributes.SERVICE_SCALE)
                        .addScanPeriod(800000)
                        .build();
                bleScanner.startScan(scanCallback);
            }
        });
    }

    ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Log.i("BLE", "DESCOBERTO:" + result.getDevice().getName());
            //bleBroadcast.connect(result.getDevice().getAddress(), callbackBroadcast);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };
}
