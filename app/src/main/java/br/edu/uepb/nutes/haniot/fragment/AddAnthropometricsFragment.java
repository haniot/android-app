package br.edu.uepb.nutes.haniot.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.edu.uepb.nutes.haniot.R;

public class AddAnthropometricsFragment extends Fragment {

    public AddAnthropometricsFragment() {}

    public static AddAnthropometricsFragment newInstance(String param1, String param2) {
        AddAnthropometricsFragment fragment = new AddAnthropometricsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_anthropometrics, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
