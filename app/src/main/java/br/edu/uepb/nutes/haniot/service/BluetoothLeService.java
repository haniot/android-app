/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.edu.uepb.nutes.haniot.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONException;

import java.util.List;
import java.util.UUID;

import br.edu.uepb.nutes.haniot.parse.GattGlucoseParser;
import br.edu.uepb.nutes.haniot.parse.GattHRParser;
import br.edu.uepb.nutes.haniot.parse.GattHTParser;
import br.edu.uepb.nutes.haniot.parse.MiBand2Parser;
import br.edu.uepb.nutes.haniot.parse.YunmaiParser;
import br.edu.uepb.nutes.haniot.utils.GattAttributes;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
    private final String LOG = "BluetoothLeService";

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED = "br.edu.uepb.nutes.ocariot.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "br.edu.uepb.nutes.ocariot.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "br.edu.uepb.nutes.ocariot.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE = "br.edu.uepb.nutes.ocariot.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA = "br.edu.uepb.nutes.ocariot.EXTRA_DATA";
    public final static String EXTRA_DATA_CONTEXT = "br.edu.uepb.nutes.ocariot.EXTRA_DATA_CONTEXT";

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(LOG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(LOG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(LOG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.w(LOG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);

                Log.i(LOG, "onCharacteristicRead()");
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i(LOG, "onCharacteristicChanged()");
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
    };

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        Log.i(LOG, "broadcastUpdate()");
        if (characteristic.getUuid().equals(UUID.fromString(GattAttributes.CHARACTERISTIC_TEMPERATURE_MEASUREMENT))) {
            try {
                intent.putExtra(EXTRA_DATA, GattHTParser.parse(characteristic).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (characteristic.getUuid().equals(UUID.fromString(GattAttributes.CHARACTERISTIC_GLUSOSE_MEASUREMENT))) {
            Log.i(LOG, "broadcastUpdate() - CHARACTERISTIC_GLUSOSE_MEASUREMENT");
            try {
                intent.putExtra(EXTRA_DATA, GattGlucoseParser.parse(characteristic).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (characteristic.getUuid().equals(UUID.fromString(GattAttributes.CHARACTERISTIC_GLUSOSE_MEASUREMENT_CONTEXT))) {
            Log.i(LOG, "broadcastUpdate() - CHARACTERISTIC_GLUSOSE_MEASUREMENT_CONTEXT");
            try {
                intent.putExtra(EXTRA_DATA_CONTEXT, GattGlucoseParser.contextParse(characteristic).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (characteristic.getUuid().equals(UUID.fromString(GattAttributes.CHARACTERISTIC_SCALE_MEASUREMENT))) {
            try {
                intent.putExtra(EXTRA_DATA, YunmaiParser.parse(characteristic).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (characteristic.getUuid().equals(UUID.fromString(GattAttributes.CHARACTERISTIC_HEART_RATE_MEASUREMENT))) {
            try {
                intent.putExtra(EXTRA_DATA, GattHRParser.parse(characteristic).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (characteristic.getUuid().equals(UUID.fromString(GattAttributes.CHARACTERISTIC_STEPS_DISTANCE_CALORIES))) {
            Log.i(LOG, "broadcastUpdate() - CHARACTERISTIC_STEPS_DISTANCE_CALORIES");
            try {
                intent.putExtra(EXTRA_DATA, MiBand2Parser.parse(characteristic).toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.w(LOG, "broadcastUpdate() - OTHER");
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for (byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + stringBuilder.toString());
            }
        }

        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(LOG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(LOG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGCallback#onConnectionStateChange(
     * android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) return false;

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress) && mBluetoothGatt != null) {
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) return false;

        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, true, mGattCallback);
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGCallback#onConnectionStateChange(
     * android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) return;

        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) return;

        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGCallback#onCharacteristicRead(
     * android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) return;
//PIN
//        if (mBluetoothGatt.getDevice().getBondState() == BluetoothDevice.BOND_NONE) {
//            mBluetoothGatt.getDevice().createBond();
//        }

        mBluetoothGatt.readCharacteristic(characteristic);
    }

    public void writeCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) return;

        mBluetoothGatt.writeCharacteristic(characteristic);
    }

    public boolean writeDescriptor(BluetoothGattDescriptor descriptor) {
        if (descriptor == null) return false;

        return mBluetoothGatt.writeDescriptor(descriptor);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic  haracteristic to act on.
     * @param descriptorValue BluetoothGattDescriptor.ENABLE_INDICATION_VALUE || BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
     * @param enabled         If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, byte[] descriptorValue, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) return;

        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        if (characteristic.getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG)) != null) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(descriptorValue);

            writeDescriptor(descriptor);
        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }

    public BluetoothGattService getGattService(UUID uuid) {
        if (mBluetoothGatt != null) {
            for (BluetoothGattService gattService : mBluetoothGatt.getServices()) {
                if (gattService.getUuid().equals(uuid)) return gattService;
            }
        }
        return null;
    }


    public BluetoothGatt getBluetoothGatt() {
        return mBluetoothGatt;
    }
}
