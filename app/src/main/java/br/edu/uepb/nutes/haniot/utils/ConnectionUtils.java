package br.edu.uepb.nutes.haniot.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * Provides routines for checking connection with internet and bluetooth.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 0.1
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public final class ConnectionUtils {

    /**
     * Checks if the device supports bluetooth.
     *
     * @return true for device supports bluetooth or false otherwise.
     */
    public static boolean isSupportedBluetooth() {
        return (BluetoothAdapter.getDefaultAdapter() == null) ? false : true;
    }

    /**
     * Checks if BLE is supported on the device.
     *
     * @param context
     * @return
     */
    public static boolean isSupportedBluetoothLE(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * Checks if bluetooth is enabled.
     *
     * @return true to enabled or false otherwise.
     */
    public static boolean bluetoothIsEnabled() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    /**
     * Checks if the device has an internet connection.
     *
     * @param context
     * @return
     */
    public static boolean internetIsEnabled(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnectedOrConnecting());
    }

    /**
     * Checks if the wifi is connected.
     *
     * @param context
     * @return
     */
    public static boolean wifiIsEnabled(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected());
    }

    /**
     * Recover mac address from wifi card.
     *
     * @param context
     * @return
     */
    public static String getMacAddressWifi(Context context) {
        if (!internetIsEnabled(context)) return null;

        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress();
    }
}
