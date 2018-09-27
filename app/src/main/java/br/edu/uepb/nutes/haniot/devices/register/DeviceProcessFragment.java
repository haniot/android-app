package br.edu.uepb.nutes.haniot.devices.register;

import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.model.Device;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceProcessFragment extends Fragment {
    private OnDeviceRegisterListener mListener;

    @BindView(R.id.name_device_fragment)
    TextView mNameDeviceFragment;

    @BindView(R.id.img_device_fragment)
    ImageView mImgDeviceFragment;

    @BindView(R.id.bnt_find_device_fragment)
    Button bntFindDevice;

    private Device mDevice;
    private DeviceRegisterActivity mDeviceRegisterActivity;


    public DeviceProcessFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DeviceProcessFragment.
     */
    public static DeviceProcessFragment newInstance() {
        DeviceProcessFragment fragment = new DeviceProcessFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();

        if (bundle.containsKey(DeviceRegisterActivity.EXTRA_DEVICE)) {
            mDevice = bundle.getParcelable(DeviceRegisterActivity.EXTRA_DEVICE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_device_process, container, false);
        ButterKnife.bind(this, view);

        //passes instance of button clicked
        bntFindDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener == null) return;
                mListener.onClickStartScan(mDevice);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        populateView();
    }

    private void populateView() {
        if(mDevice == null) return;
        mNameDeviceFragment.setText(mDevice.getName());
        mImgDeviceFragment.setImageResource(mDevice.getImg());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDeviceRegisterListener) {
            mListener = (OnDeviceRegisterListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDeviceRegisterListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnDeviceRegisterListener {
        void onClickStartScan(Device device);
    }
}
