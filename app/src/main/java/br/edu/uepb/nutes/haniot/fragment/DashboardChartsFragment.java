package br.edu.uepb.nutes.haniot.fragment;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.MainActivity;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import butterknife.BindView;
import butterknife.ButterKnife;

import static br.edu.uepb.nutes.haniot.data.model.UserType.ADMIN;
import static br.edu.uepb.nutes.haniot.data.model.UserType.PATIENT;

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
    @BindView(R.id.title_pilot_study)
    TextView titlePilotStudy;
    @BindView(R.id.title_professional)
    TextView titleProfessional;
    Communicator communicator;
    @BindView(R.id.box_message_error)
    LinearLayout boxMessage;
    @BindView(R.id.message_error)
    TextView messageError;
    String typeMessageError;
    boolean wifiRequest;
    boolean bluetoothRequest;
    User user;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charts_dashboard, container, false);
        ButterKnife.bind(this, view);
        AppPreferencesHelper preferencesHelper = AppPreferencesHelper.getInstance(getContext());

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                getResources().getString(R.string.date_format2),
                Locale.getDefault()
        );

        textDate.setText(simpleDateFormat.format(calendar.getTime()));
        updateNamePatient(preferencesHelper.getLastPatient());
        if (preferencesHelper.getUserLogged().getUserType().equals(PATIENT)) {
            textPilotStudy.setVisibility(View.INVISIBLE);
            textProfessional.setVisibility(View.INVISIBLE);
            titlePilotStudy.setVisibility(View.INVISIBLE);
            titleProfessional.setVisibility(View.INVISIBLE);
        } else if (preferencesHelper.getUserLogged().getUserType().equals(ADMIN)) {
            titleProfessional.setVisibility(View.INVISIBLE);
            textProfessional.setVisibility(View.INVISIBLE);
        }
        if (preferencesHelper.getLastPilotStudy() != null)
            textPilotStudy.setText(preferencesHelper.getLastPilotStudy().getName());
        if (preferencesHelper.getUserLogged().getName() != null)
            textProfessional.setText(preferencesHelper.getUserLogged().getName());

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            communicator = (DashboardChartsFragment.Communicator) context;
        } catch (ClassCastException castException) {
            throw new ClassCastException();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!wifiRequest && !bluetoothRequest)
            boxMessage.setVisibility(View.GONE);

        if (BluetoothAdapter.getDefaultAdapter() != null &&
                !BluetoothAdapter.getDefaultAdapter().isEnabled())
            showMessageConnection("bluetooth", true);
    }

    /**
     * Update value of measurement in screen.
     *
     * @param valueMeasurement {@link String}
     */
    public void updateValueMeasurement(String valueMeasurement) {
        textValueMeasurement.setText(valueMeasurement);
    }

    /**
     * Displays message.
     */
    public void showMessageConnection(String typeMessageError, boolean show) {
        Log.w("MainActivity", "show message: " + typeMessageError);
        if (getContext() != null) {
            if (typeMessageError.equals("wifi")) {
                if (show) {
                    wifiRequest = true;
                    messageError.setOnClickListener(null);
                    messageError.setText(getString(R.string.wifi_disabled));
                } else {
                    wifiRequest = false;
                    if (bluetoothRequest) {
                        showMessageConnection("bluetooth", true);
                    }
                }
            } else if (typeMessageError.equals("bluetooth")) {
                if (show) {
                    bluetoothRequest = true;
                    messageError.setText(getString(R.string.bluetooth_disabled));
                    messageError.setOnClickListener(v -> {
                        AppPreferencesHelper.getInstance(getContext()).saveBluetoothMode(true);
                        ((MainActivity) Objects.requireNonNull(getActivity())).checkPermissions();
                    });
                } else {
                    bluetoothRequest = false;
                    if (wifiRequest) {
                        showMessageConnection("wifi", true);
                    }
                }
            }

            if (wifiRequest || bluetoothRequest) {
                Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                    boxMessage.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
                    boxMessage.setVisibility(View.VISIBLE);
                });
            } else {
                boxMessage.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
                boxMessage.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Displays message.
     *
     * @param str @StringRes message.
     */
    public void showMessage(@StringRes int str) {
        Log.w("MainActivity", "show message: " + str);
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
     * @param patient {@link Patient}
     */
    public void updateNamePatient(Patient patient) {
        if (patient != null) {
            String namePatient = patient.getName().split("\\s+")[0];
            patientName.setText(namePatient);
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