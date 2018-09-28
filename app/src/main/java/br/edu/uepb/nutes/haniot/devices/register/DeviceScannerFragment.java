package br.edu.uepb.nutes.haniot.devices.register;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.simplebleconnect.scanner.SimpleBleScanner;
import br.edu.uepb.nutes.simplebleconnect.utils.GattAttributes;
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
        mScanner = new SimpleBleScanner.Builder()
                .addFilterServiceUuid(getServiceUuidDevice(mDevice.getName()))
                .addScanPeriod(15000) // 15s
                .build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_scanner, container, false);
        ButterKnife.bind(this, view);


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        populateView();
    }

    private void populateView() {
        if (mDevice == null) return;
//        mNameDeviceFragment.setText(mDevice.getName());
//        mImgDeviceFragment.setImageResource(mDevice.getImg());
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
                service = GattAttributes.SERVICE_SCALE;
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
