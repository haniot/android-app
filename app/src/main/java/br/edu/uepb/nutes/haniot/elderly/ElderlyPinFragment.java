package br.edu.uepb.nutes.haniot.elderly;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ElderlyPinFragment implementation.
 *
 * @author Douglas Rafael <douglas.rafael@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class ElderlyPinFragment extends Fragment {

    private OnNextPageSelectedListener mListener;

    @BindView(R.id.next_page_button)
    Button nextPageButton;

    public ElderlyPinFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ElderlyPinFragment.
     */
    public static ElderlyPinFragment newInstance() {
        ElderlyPinFragment fragment = new ElderlyPinFragment();

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
        View view = inflater.inflate(R.layout.fragment_elderly_pin, container, false);
        ButterKnife.bind(this, view);

        nextPageButton.setOnClickListener(v -> mListener.onNextPageSelected());
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNextPageSelectedListener) {
            mListener = (OnNextPageSelectedListener) context;
        } else {
            throw new ClassCastException();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnNextPageSelectedListener {
        void onNextPageSelected();
    }
}
