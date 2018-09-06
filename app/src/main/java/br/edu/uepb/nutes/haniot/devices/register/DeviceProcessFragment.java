package br.edu.uepb.nutes.haniot.devices.register;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.edu.uepb.nutes.haniot.R;

public class DeviceProcessFragment extends Fragment {

    private OnDeviceRegisterListener mListener;

    public DeviceProcessFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DeviceProcessFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DeviceProcessFragment newInstance() {
        DeviceProcessFragment fragment = new DeviceProcessFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_device_process, container, false);
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
        // TODO: Update argument type and name
    }
}
