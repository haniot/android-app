package br.edu.uepb.nutes.haniot.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.MainActivity;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

/**
 * DashboardChartsFragment implementation.
 *
 * @author Fábio Júnior <fabio.pequeno@nutes.uepb.edu.br>
 * @version 1.0
 * @copyright Copyright (c) 2019, NUTES UEPB
 */
public class DashboardChartsFragment extends Fragment {
    @BindView(R.id.patientName)
    TextView patientName;
    @BindView(R.id.patientSex)
    ImageView patientSex;
    @BindView(R.id.textDate)
    TextView textDate;
    @BindView(R.id.textValueMeasurement)
    TextView textValueMeasurement;
    @BindView(R.id.textIMC)
    TextView textIMC;
    @BindView(R.id.pulsator)
    PulsatorLayout pulsatorLayout;
    Communicator communicator;

    public DashboardChartsFragment() {
        // Required empty public constructor
    }

    public static DashboardChartsFragment newInstance() {
        return new DashboardChartsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charts_dashboard, container, false);
        ButterKnife.bind(this, view);


        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getResources().getString(R.string.date_format2));
        textDate.setText(simpleDateFormat.format(calendar.getTime()));
        pulsatorLayout.start();
        updateNamePatient(((MainActivity) getActivity()).getPatientSelected());
        return view;
    }

    /**
     * On attach.
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            communicator = (DashboardChartsFragment.Communicator) context;
        } catch (ClassCastException castException) {
        }
    }

    /**
     * On start.
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * On stop.
     */
    @Override
    public void onStop() {
        super.onStop();
    }

    /**
     * On resume.
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Update value of measurement in screen.
     *
     * @param valueMeasurement
     */
    public void updateValueMeasurement(String valueMeasurement) {

        textValueMeasurement.setText(valueMeasurement);
        //textIMC.setText(String.format("%.2f", calcIMC(Double.parseDouble(valueMeasurement))));
    }

    /**
     * Update name patient selected.
     *
     * @param patient
     */
    public void updateNamePatient(Patient patient) {
        if (patient != null) {
            patientName.setText(patient.getFirstName());
            if (patient.getGender().equals("male"))
                patientSex.setImageResource(R.drawable.x_boy);
            else
                patientSex.setImageResource(R.drawable.x_girl);
        }
    }

    /**
     * Calculate patient IMC.
     *
     * @param weight
     * @return
     */
    private double calcIMC(double weight) {
        double altura = 1.9;
        double imc = weight / (altura * altura);
        return imc;
    }

    /**
     * Interface for communication with scale data exchange.
     */
    public interface Communicator {
        void notifyNewMeasurement(String data);
    }
}
