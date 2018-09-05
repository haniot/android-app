package br.edu.uepb.nutes.blescanner;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;

import java.util.List;

public interface CallbackBroadcast {

    void onStateConnected(BluetoothDevice device);
    void onStateDisconnected();
    void onServiceDiscovered(List<BluetoothGattService> gattServices);
    void onDataAvailable();

}
