package br.edu.uepb.nutes.haniot.elderly;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.ui.MultiSelectSpinner;
import br.edu.uepb.nutes.haniot.utils.Log;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ElderlyFormFragment extends Fragment {

    @BindView(R.id.marital_status_spinner)
    Spinner maritalStatusSpinner;

    @BindView(R.id.education_spinner)
    Spinner educationSpinner;

    @BindView(R.id.medications_multiSelectSpinner)
    MultiSelectSpinner medicationsSpinner;

    @BindView(R.id.accessories_multiSelectSpinner)
    MultiSelectSpinner accessoriesSpinner;

    @BindView(R.id.new_medications_imageButton)
    ImageButton medicationsButton;

    @BindView(R.id.new_accessories_imageButton)
    ImageButton accessoriesButton;

    public ElderlyFormFragment() {
        // Required empty public constructor
    }

    public static ElderlyFormFragment newInstance() {
        ElderlyFormFragment fragment = new ElderlyFormFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_elderly_form, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initUI();
    }

    private void initUI() {
        // MultiSpinner medications
        medicationsSpinner
                .title(getString(R.string.elderly_select_medications))
                .hint(getResources().getString(R.string.elderly_select_medications))
                .messageEmpty("Nenhuma medicamento foi criado ainda...")
                .build();

        // MultiSpinner accessories
        accessoriesSpinner
                .title(getString(R.string.elderly_select_accessories))
                .hint(getString(R.string.elderly_select_accessories))
                .items(getResources().getStringArray(R.array.elderly_accessories_array))
                .build();

        medicationsButton.setOnClickListener(e -> {
            medicationsSpinner.item(new String("Novo Item adicionado"));
        });

        Log.d("TEST", medicationsSpinner.isEmpty() + "");


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
