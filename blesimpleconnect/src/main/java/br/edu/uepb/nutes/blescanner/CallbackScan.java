package br.edu.uepb.nutes.blescanner;

import android.bluetooth.BluetoothDevice;

import java.util.List;

public interface CallbackScan {

        void onResult(BluetoothDevice device);
        void onListResult(List<BluetoothDevice> device);
        void onError(int Error);

    }
