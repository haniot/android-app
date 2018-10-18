package br.edu.uepb.nutes.haniot.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.edu.uepb.nutes.haniot.R;
import butterknife.ButterKnife;

public class AddTemperatureManuallyFragment extends Fragment {

    public AddTemperatureManuallyFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_temperature_manually,
                container, false);
        ButterKnife.bind(this,view);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

}
