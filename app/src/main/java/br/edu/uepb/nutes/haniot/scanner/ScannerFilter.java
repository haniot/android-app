package br.edu.uepb.nutes.haniot.scanner;

import android.bluetooth.le.ScanFilter;
import android.os.Build;
import android.os.ParcelUuid;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import java.util.UUID;

/**
 * ScannerFilter implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public final class ScannerFilter {
    @Nullable
    private final String mDeviceName;

    @Nullable
    private final String mDeviceAddress;

    @Nullable
    private final ParcelUuid mServiceUuid;

    private ScannerFilter(String mDeviceName, String mDeviceAddress, ParcelUuid mServiceUuid) {
        this.mDeviceName = mDeviceName;
        this.mDeviceAddress = mDeviceAddress;
        this.mServiceUuid = mServiceUuid;
    }

    /**
     * get {@link ScanFilter}.
     *
     * @return ScanFilter
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ScanFilter getScanFilter() {
        if (mDeviceName == null && mDeviceAddress == null && mServiceUuid == null) return null;

        ScanFilter filter = new ScanFilter.Builder()
                .setDeviceName(mDeviceName)
                .setDeviceAddress(mDeviceAddress)
                .setServiceUuid(mServiceUuid).build();

        return filter;
    }

    /**
     * get {@link UUID}.
     *
     * @return UUID
     */
    public UUID getUUID() {
        if (mServiceUuid == null) return null;

        return mServiceUuid.getUuid();
    }

    /**
     * Builder class for {@link ScannerFilter}.
     */
    public static final class Builder {
        private String mDeviceName;
        private String mDeviceAddress;
        private ParcelUuid mServiceUuid;

        public Builder() {
        }

        public Builder setDeviceName(String mDeviceName) {
            this.mDeviceName = mDeviceName;
            return this;
        }

        public Builder setDeviceAddress(String mDeviceAddress) {
            this.mDeviceAddress = mDeviceAddress;
            return this;
        }

        public Builder setServiceUuid(ParcelUuid mServiceUuid) {
            this.mServiceUuid = mServiceUuid;
            return this;
        }

        /**
         * Build {@link ScannerFilter}.
         *
         * @return ScannerFilter
         */
        public ScannerFilter build() {
            return new ScannerFilter(mDeviceName, mDeviceAddress, mServiceUuid);
        }
    }
}
