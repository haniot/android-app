package br.edu.uepb.nutes.haniot.scanner;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.support.annotation.RequiresPermission;

import java.util.List;
import java.util.UUID;

/**
 * BLE Scanner implementation JellyBean.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class BLEScannerJellyBean extends BLEScanner implements BluetoothAdapter.LeScanCallback {

    private BluetoothAdapter mBluetoothAdapter;
    private ScanCallback mScanCallback;

    public BLEScannerJellyBean() {
        super();
        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    @RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH})
    void startScanInternal(long scanPeriod, List<ScannerFilter> filters, ScanSettings settings, ScanCallback callback) {
        this.mScanCallback = callback;

        if (filters != null) mBluetoothAdapter.startLeScan(
                filtersToUUID(filters), BLEScannerJellyBean.this);
        else mBluetoothAdapter.startLeScan(BLEScannerJellyBean.this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BLEScannerJellyBean.this.stopScan(mScanCallback);
            }
        }, scanPeriod);
    }

    @Override
    @RequiresPermission(Manifest.permission.BLUETOOTH)
    public void stopScan(ScanCallback callback) {
        if (mBluetoothAdapter == null) return;

        mBluetoothAdapter.stopLeScan(this);
        mScanCallback = null;
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        mScanCallback.onScanResult(ScanSettings.CALLBACK_TYPE_ALL_MATCHES, device);
    }

    /**
     * Convert List<UUID> in UUID[].
     *
     * @param filtersSevices List<UUID>
     * @return UUID[]
     */
    private UUID[] filtersToUUID(List<ScannerFilter> filtersSevices) {
        UUID[] uuids = new UUID[filtersSevices.size()];

        for (int i = 0; i < filtersSevices.size(); i++) {
            UUID uuid = filtersSevices.get(i).getUUID();
            if (uuid != null) uuids[i] = uuid;
        }

        return uuids;
    }
}
