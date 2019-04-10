package br.edu.uepb.nutes.haniot.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.MainActivity;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

/**
 * DashboardChartsFragment implementation.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
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
    @BindView(R.id.text_pilot_study)
    TextView textPilotStudy;
    @BindView(R.id.text_professional)
    TextView textProfessional;
    Communicator communicator;
    @BindView(R.id.box_message_error)
    LinearLayout boxMessage;
    @BindView(R.id.message_error)
    TextView messageError;

    private AppPreferencesHelper helper;

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
        helper = AppPreferencesHelper.getInstance(getContext());
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getResources().getString(R.string.date_format2));
        textDate.setText(simpleDateFormat.format(calendar.getTime()));
        updateNamePatient(((MainActivity) getActivity()).getPatientSelected());
        textPilotStudy.setText(helper.getLastPilotStudy().getName());
        textProfessional.setText(helper.getUserLogged().getName());
        messageError.setOnClickListener(v -> ((MainActivity) getActivity()).checkPermissions());
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
        boxMessage.setVisibility(View.GONE);
    }

    /**
     * Update value of measurement in screen.
     *
     * @param valueMeasurement
     */
    public void updateValueMeasurement(String valueMeasurement) {
        textValueMeasurement.setText(valueMeasurement);
    }

    /**
     * Displays message.
     *
     * @param str @StringRes message.
     */
    public void showMessage(@StringRes int str) {
        if (getContext() != null) {
            if (str == -1) {
                boxMessage.setVisibility(View.GONE);
                return;
            }

            String message = getContext().getResources().getString(str);
            messageError.setText(message);
            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                boxMessage.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
                boxMessage.setVisibility(View.VISIBLE);
            });
        }
    }

    /**
     * Update name patient selected.
     *
     * @param patient
     */
    public void updateNamePatient(Patient patient) {
        if (patient != null) {
            patientName.setText(String.format("%s %s", patient.getFirstName(), patient.getLastName()));
            if (patient.getGender().equals("male"))
                patientSex.setImageResource(R.drawable.x_boy);
            else
                patientSex.setImageResource(R.drawable.x_girl);
        }
    }

    /**
     * Interface for communication with scale data exchange.
     */
    public interface Communicator {
        void notifyNewMeasurement(String data);

        void showMessage(int message);
    }
}