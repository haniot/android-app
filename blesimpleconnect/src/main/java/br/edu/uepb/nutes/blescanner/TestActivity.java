package br.edu.uepb.nutes.blescanner;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.List;


/**
 * Test actvity.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.edu.uepb.br>
 * @author Arthur Stevam <arthurstevam.ac@gmail.com>
 * @version 1.0
 * @copyright Copyright (c) 2018
 */

public class TestActivity extends AppCompatActivity {


    private final String TAG = "TESTE";
    ///private static final int RQS_ENABLE_BLUETOOTH = 1;
    BLEScanner bleScanner;
    BLEBroadcast bleBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //bleScanner = BLEScanner.getInstance(TestActivity.this);
        bleBroadcast = new BLEBroadcast(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bleScanner = new BLEScanner.Filter(TestActivity.this)
                        .addFilterServiceUuid(GattAttributes.SERVICE_HEART_RATE, GattAttributes.SERVICE_HEALTH_THERMOMETER)
//                        .addFilterAdress("A")
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
            bleBroadcast.connect(result.getDevice().getAddress(), callbackBroadcast);
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

    CallbackBroadcast callbackBroadcast = new CallbackBroadcast() {
        @Override
        public void onStateConnected(BluetoothDevice device) {
            //Toast.makeText(TestActivity.this, "Connected " + device.getName(), Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Connected " + device.getName());

        }


        @Override
        public void onStateDisconnected() {
            // Toast.makeText(TestActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Disconnected");

        }

        @Override
        public void onServiceDiscovered(List<BluetoothGattService> gattServices) {
            Log.i(TAG, gattServices.toString());
        }

        @Override
        public void onDataAvailable() {

            Log.i(TAG, "Data available");

        }
    };

//    CallbackScan callbackScan = new CallbackScan() {
//        @Override
//        public void onResult(BluetoothDevice device) {
//            Toast.makeText(TestActivity.this, "Descoberto: " + device.getAddress() + " - " + device.getName(), Toast.LENGTH_SHORT);
//            Log.i("BLE", "DESCOBERTO:" + device.getName());
//            bleScanner.startScan(callbackScan);
//            bleBroadcast.connect(device.getAddress(), callbackBroadcast);
//
//        }
//
//        @Override
//        public void onListResult(List<BluetoothDevice> device) {
//
//        }
//
//        @Override
//        public void onError(int Error) {
//            Toast.makeText(TestActivity.this, "Erro!", Toast.LENGTH_SHORT);
//        }
//    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

    //
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//    //    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//       // startActivityForResult(enableBtIntent, RQS_ENABLE_BLUETOOTH);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if (requestCode == RQS_ENABLE_BLUETOOTH && resultCode == Activity.RESULT_CANCELED) {
//            finish();
//            return;
//        }
//
//        super.onActivityResult(requestCode, resultCode, data);
//    }

}
