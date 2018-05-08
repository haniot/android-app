package br.edu.uepb.nutes.haniot.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.edu.uepb.nutes.haniot.R;
import butterknife.ButterKnife;

/**
 * Created by Fabio on 02/05/2018.
 */

public class RealTimeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_realtime_linechart, container, false);
        ButterKnife.bind(this, view);

        return view;
    }
}
