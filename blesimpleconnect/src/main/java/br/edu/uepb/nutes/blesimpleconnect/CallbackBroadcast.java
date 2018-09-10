package br.edu.uepb.nutes.blesimpleconnect;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;

import java.util.List;

public interface CallbackBroadcast {

    void onStateConnected(BluetoothDevice device);
    void onStateDisconnected();
    void onServiceDiscovered(List<BluetoothGattService> gattServices);
    void onDataAvailable();

}
