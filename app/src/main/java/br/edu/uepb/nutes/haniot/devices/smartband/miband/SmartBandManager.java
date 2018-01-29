/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package br.edu.uepb.nutes.haniot.devices.smartband.miband;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import org.json.JSONException;

import java.util.Deque;
import java.util.LinkedList;
import java.util.UUID;

import br.edu.uepb.nutes.haniot.parse.GattHTParser;

/**
 * ThermometerManager class performs BluetoothGatt operations for connection, service discovery, enabling indication and reading characteristics. All operations required to connect to device with BLE HT
 * Service and reading health thermometer values are performed here. HTSActivity implements ThermometerManagerCallbacks in order to receive callbacks of BluetoothGatt operations
 */
public class SmartBandManager {
    private static final String TAG = "ThermometerManager";

    public final static UUID MI_BAND_SERVICE_UUID = UUID.fromString("0000fee0-0000-1000-8000-00805f9b34fb");

    public final static UUID MI_BAND_CHARACTERISTIC_DEVICE_INFO_UUID = UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb");
    public final static UUID MI_BAND_CHARACTERISTIC_DEVICE_NAME_UUID = UUID.fromString("0000ff02-0000-1000-8000-00805f9b34fb");
    public final static UUID MI_BAND_CHARACTERISTIC_NOTIFICATION_UUID = UUID.fromString("0000ff03-0000-1000-8000-00805f9b34fb");
    public final static UUID MI_BAND_CHARACTERISTIC_USER_INFO_UUID = UUID.fromString("0000ff04-0000-1000-8000-00805f9b34fb");
    public final static UUID MI_BAND_CHARACTERISTIC_CONTROL_POINT_UUID = UUID.fromString("0000ff05-0000-1000-8000-00805f9b34fb");
    public final static UUID MI_BAND_CHARACTERISTIC_REALTIME_STEPS_UUID = UUID.fromString("0000ff06-0000-1000-8000-00805f9b34fb");
    public final static UUID MI_BAND_CHARACTERISTIC_ACTIVITY_DATA_UUID = UUID.fromString("0000ff07-0000-1000-8000-00805f9b34fb");
    public final static UUID MI_BAND_CHARACTERISTIC_FIRMWARE_DATA_UUID = UUID.fromString("0000ff08-0000-1000-8000-00805f9b34fb");
    public final static UUID MI_BAND_CHARACTERISTIC_LE_PARAMS_UUID = UUID.fromString("0000ff08-0000-1000-8000-00805f9b34fb");
    public final static UUID MI_BAND_CHARACTERISTIC_DATE_TIME_UUID = UUID.fromString("0000ff0a-0000-1000-8000-00805f9b34fb");
    public final static UUID MI_BAND_CHARACTERISTIC_STATISTICS_UUID = UUID.fromString("0000ff0b-0000-1000-8000-00805f9b34fb");
    public final static UUID MI_BAND_CHARACTERISTIC_BATTERY_UUID = UUID.fromString("0000ff0c-0000-1000-8000-00805f9b34fb");
    public final static UUID MI_BAND_CHARACTERISTIC_TEST_UUID = UUID.fromString("0000ff0d-0000-1000-8000-00805f9b34fb");
    public final static UUID MI_BAND_CHARACTERISTIC_SENSOR_DATA_UUID = UUID.fromString("0000ff0e-0000-1000-8000-00805f9b34fb");


    private BluetoothGattCharacteristic mCharacteristic;

    public SmartBandManager(final Context context) {
    }
}
