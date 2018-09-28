package br.edu.uepb.nutes.haniot.devices.register;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.simplebleconnect.scanner.SimpleBleScanner;
import br.edu.uepb.nutes.simplebleconnect.scanner.SimpleScanCallback;
import br.edu.uepb.nutes.simplebleconnect.utils.GattAttributes;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceScannerFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private final String TAG = "DeviceScannerFragment";

    private final String NAME_DEVICE_THERM_DL8740 = "Ear Thermometer DL8740";
    private final String NAME_DEVICE_GLUCOMETER_PERFORMA = "Accu-Chek Performa Connect";
    private final String NAME_DEVICE_SCALE_1501 = "Scale YUNMAI Mini 1501";
    private final String NAME_DEVICE_SCALE_HBF206IT = "Scale OMRON HB-F206IT";
    private final String NAME_DEVICE_HEART_RATE_H7 = "Heart Rate Sensor H7";
    private final String NAME_DEVICE_HEART_RATE_H10 = "Heart Rate Sensor H10";
    private final String NAME_DEVICE_SMARTBAND_MI2 = "Smartband MI Band 2";
    private final String NAME_DEVICE_PRESSURE_BP792IT = "Blood Pressure Monitor BP792IT";
    private final String SERVICE_SCALE_1501 = "00001310-0000-1000-8000-00805f9b34fb";

    @BindView(R.id.bnt_start_device_scanner)
    Button startDeviceFragment;

    @BindView(R.id.name_device_fragment_scanner)
    TextView nameDeviceFragmentScanner;

    @BindView(R.id.img_device_fragment_scanner)
    ImageView imgDeviceFragmentScanner;

    @BindView(R.id.mac_device_fragment)
    TextView macDeviceFragment;

    private Device mDevice;
    private SimpleBleScanner mScanner;

    public DeviceScannerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DeviceScannerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeviceScannerFragment newInstance() {
        DeviceScannerFragment fragment = new DeviceScannerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle.containsKey(DeviceRegisterActivity.EXTRA_SERVICE_UUID)) {
            mDevice = bundle.getParcelable(DeviceRegisterActivity.EXTRA_SERVICE_UUID);
        }
        //Initialize scanner settings
        Log.d(TAG, "onCreate: "+getServiceUuidDevice(mDevice.getName()));
        mScanner = new SimpleBleScanner.Builder()
                .addFilterServiceUuid(getServiceUuidDevice(mDevice.getName()))
                .addScanPeriod(15000) // 15s
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_scanner, container, false);
        ButterKnife.bind(this, view);

        startDeviceFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.bnt_start_device_scanner){
                    if (mScanner != null) {
                        mScanner.stopScan();
                        mScanner.startScan(mScanCallback);
                    }
                }
            }
        });

        return view;
    }
    public final SimpleScanCallback mScanCallback = new SimpleScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScanResult(int callbackType, ScanResult scanResult) {
            BluetoothDevice device = scanResult.getDevice();
            if (device == null) return;
            Log.d(TAG, "onScanResult: " + device);


                if (device == null) return;
                nameDeviceFragmentScanner.setText(device.getName());
                macDeviceFragment.setText(device.getAddress());
                imgDeviceFragmentScanner.setImageResource(mDevice.getImg());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> scanResults) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void onFinish() {
            Log.d("MainActivity", "onFinish()");
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.d("MainActivity", "onScanFailed() " + errorCode);
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        populateView();
    }

    private void populateView() {
//        if (mDevice == null) return;
//        nameDeviceFragmentScanner.setText(mDevice.getName());
//        imgDeviceFragmentScanner.setImageResource(mDevice.getImg());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public String getServiceUuidDevice(String nameDevice) {
        String service = null;

        if (!nameDevice.isEmpty()) {
            if (nameDevice.equals(NAME_DEVICE_THERM_DL8740)) {//
                service = GattAttributes.SERVICE_HEALTH_THERMOMETER;
            } else if (nameDevice.equals(NAME_DEVICE_GLUCOMETER_PERFORMA)) {//
                service = GattAttributes.SERVICE_GLUCOSE;
            } else if (nameDevice.equals(NAME_DEVICE_SCALE_1501)) {
                service = SERVICE_SCALE_1501;
            } else if (nameDevice.equals(NAME_DEVICE_HEART_RATE_H7)) {//
                service = GattAttributes.SERVICE_HEART_RATE;
            } else if (nameDevice.equals(NAME_DEVICE_HEART_RATE_H10)) {//
                service = GattAttributes.SERVICE_HEART_RATE;
            } else if (nameDevice.equals(NAME_DEVICE_SMARTBAND_MI2)) {//
                service = GattAttributes.SERVICE_STEPS_DISTANCE_CALORIES;
            }
        }
        return service;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
