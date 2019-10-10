package br.edu.uepb.nutes.haniot.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.data.model.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.model.User;
import br.edu.uepb.nutes.haniot.data.model.type.ItemGridType;
import br.edu.uepb.nutes.haniot.data.model.type.MeasurementType;
import br.edu.uepb.nutes.haniot.data.model.type.PatientsType;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.ErrorHandler;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.fragment.FragmentAnthropometrics;
import br.edu.uepb.nutes.haniot.fragment.FragmentBloodPressure;
import br.edu.uepb.nutes.haniot.fragment.FragmentGlucose;
import br.edu.uepb.nutes.haniot.fragment.FragmentHeartRate;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.haniot.utils.NetworkUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddMeasurementActivity extends AppCompatActivity {

    private final String TAG = "AddMeasurementActivity";
    private final String WIRELESS = "wifi";
    private final String CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    private boolean wifiRequest;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.icon_calendar)
    ImageView icon_calendar;

    @BindView(R.id.icon_time)
    ImageView iconTime;

    @BindView(R.id.text_date)
    TextView textDate;

    @BindView(R.id.text_time)
    TextView textTime;

    @BindView(R.id.text_systolic)
    EditText textMeasurement;

    @BindView(R.id.text_unit)
    TextView textUnit;

    @BindView(R.id.message_add)
    TextView messageInfo;

    @BindView(R.id.box_measurement)
    RelativeLayout boxMeasurement;

    @BindView(R.id.saveMeasurement)
    FloatingActionButton saveMeasurement;

    @BindView(R.id.gender_icon)
    ImageView genderIcon;

    @BindView(R.id.box_message_error)
    LinearLayout boxMessage;

    @BindView(R.id.message_error)
    TextView messageError;

    private final Calendar myCalendar = Calendar.getInstance();
    private HaniotNetRepository haniotNetRepository;
    private AppPreferencesHelper appPreferencesHelper;
    private Fragment myFragment;
    private User user;
    private Patient patient;
    private int type;
    private String patientName;
    private String typeMeasurement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_measurement);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupComponents();
        haniotNetRepository = HaniotNetRepository.getInstance(this);
        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        user = appPreferencesHelper.getUserLogged();
        patient = appPreferencesHelper.getLastPatient();
        type = appPreferencesHelper.getInt(getResources().getString(R.string.measurementType));
        patientName = patient.getName();

        if (patient.getGender().equals(PatientsType.GenderType.MALE)) {
            genderIcon.setImageResource(R.drawable.x_boy);
        } else {
            genderIcon.setImageResource(R.drawable.x_girl);
        }

        if (ItemGridType.typeSupported(type)) {
            replaceFragment(type);
        } else {
            finish();
        }
        IntentFilter filterInternet = new IntentFilter(CONNECTIVITY_CHANGE);
        registerReceiver(mReceiver, filterInternet);
        saveMeasurement.setOnClickListener(v -> prepareMeasurement());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            int status = NetworkUtil.getConnectivityStatusString(context);

            if (CONNECTIVITY_CHANGE.equals(action)) {
                if (status == NetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                    showMessageConnection(WIRELESS, true);
                } else {
                    showMessageConnection(WIRELESS, false);
                }
            }
        }
    };

    /**
     * Displays message.
     */
    public void showMessageConnection(String typeMessageError, boolean show) {
        Log.w("MainActivity", "show message: " + typeMessageError);

        if (typeMessageError.equals(WIRELESS)) {
            if (show) {
                wifiRequest = true;
                messageError.setOnClickListener(null);
                messageError.setText(getString(R.string.wifi_disabled));
                runOnUiThread(() -> {
                    boxMessage.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
                    boxMessage.setVisibility(View.VISIBLE);
                });
            } else {
                wifiRequest = false;
                boxMessage.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Set fragment.
     *
     * @param measurementType
     */
    public void replaceFragment(int measurementType) {
        String measurementText;
        switch (measurementType) {
            case ItemGridType.WEIGHT:
                measurementText = getResources().getString(R.string.weight);
                typeMeasurement = "weight";
                messageInfo.setText(String.format(getResources().getString(R.string.add_measurement_message), measurementText, patientName));
                getSupportActionBar().setTitle("Inserir Peso");
                textUnit.setText(getString(R.string.unit_kg));
                break;
            case ItemGridType.TEMPERATURE:
                measurementText = getResources().getString(R.string.temperature);
                typeMeasurement = "body_temperature";
                messageInfo.setText(String.format(getResources().getString(R.string.add_measurement_message), measurementText, patientName));
                getSupportActionBar().setTitle("Inserir Temperatura");
                textUnit.setText(getString(R.string.unit_celsius));
                break;

            case ItemGridType.BLOOD_GLUCOSE:
                measurementText = getResources().getString(R.string.blood_glucose);
                typeMeasurement = "blood_glucose";
                messageInfo.setText(String.format(getResources().getString(R.string.add_measurement_message), measurementText, patientName));
                getSupportActionBar().setTitle("Inserir Glicose");
                textUnit.setText(getString(R.string.unit_glucose_mg_dL));
                myFragment = new FragmentGlucose();
                getSupportFragmentManager().beginTransaction().replace(R.id.extra,
                        myFragment)
                        .commit();
                break;

            case ItemGridType.HEART_RATE:
                measurementText = getResources().getString(R.string.heart_rate);
                typeMeasurement = "heart_rate";
                messageInfo.setText(String.format(getResources().getString(R.string.add_measurement_message), measurementText, patientName));
                getSupportActionBar().setTitle("Inserir Batimentos Cardíacos");
                textUnit.setText(getString(R.string.unit_heart_rate));
                textMeasurement.setClickable(false);
                textMeasurement.setFocusable(false);
                myFragment = new FragmentHeartRate();
                getSupportFragmentManager().beginTransaction().replace(R.id.extra,
                        myFragment)
                        .commit();
                break;

            case ItemGridType.BLOOD_PRESSURE:
                measurementText = getResources().getString(R.string.blood_pressure);
                typeMeasurement = MeasurementType.BLOOD_PRESSURE;
                messageInfo.setText(String.format(getResources().getString(R.string.add_measurement_message), measurementText, patientName));
                getSupportActionBar().setTitle("Inserir Pressão Arterial");
                textUnit.setText(getString(R.string.unit_pressure));
                boxMeasurement.setVisibility(View.GONE);
                myFragment = new FragmentBloodPressure();
                getSupportFragmentManager().beginTransaction().replace(R.id.extra,
                        myFragment).commit();
                break;

            case ItemGridType.ANTHROPOMETRIC:
                typeMeasurement = "anthropometric";
                measurementText = getResources().getString(R.string.anthropometric);
                messageInfo.setText(String.format(getResources().getString(R.string.add_measurement_message), measurementText, patientName));
                getSupportActionBar().setTitle("Inserir Medidas Antropométricas");
                boxMeasurement.setVisibility(View.GONE);
                myFragment = new FragmentAnthropometrics();
                getSupportFragmentManager().beginTransaction().replace(R.id.extra,
                        myFragment).commit();
                break;
        }
    }

    /**
     * Manipulates the error and displays message
     * according to the type of error.
     *
     * @param e {@link Throwable}
     */
    private void errorHandler(Throwable e) {
        if (!checkConnectivity())
            showMessage(R.string.no_internet_conection);
        else
            ErrorHandler.showMessage(this, e);
    }

    /**
     * Check if you have connectivity.
     * If it does not, the elements in the view mounted to notify the user.
     *
     * @return boolean
     */
    private boolean checkConnectivity() {
        if (!ConnectionUtils.internetIsEnabled(this)) {
            return false;
        }
        return true;
    }


    /**
     * Setup date picker.
     */
    public void setupComponents() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);
        myCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        myCalendar.set(Calendar.MINUTE, mMinute);

        updateLabel();

        View.OnClickListener timeClick = v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(AddMeasurementActivity.this,
                    (view, hourOfDay, minute) -> {
                        textTime.setText(new StringBuilder().append(hourOfDay)
                                .append(":")
                                .append(String.format("%02d", minute)).toString());
                        myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        myCalendar.set(Calendar.MINUTE, minute);
                    }, mHour, mMinute, true);
            timePickerDialog.show();
        };
        // Launch Time Picker Dialog
        iconTime.setOnClickListener(timeClick);
        textTime.setOnClickListener(timeClick);

        View.OnClickListener dateClick = (v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddMeasurementActivity.this, date,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
        icon_calendar.setOnClickListener(dateClick);
        textDate.setOnClickListener(dateClick);
    }

    DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateLabel();

        textMeasurement.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) boxMessage.setVisibility(View.GONE);
            }
        });
    };

    private void updateLabel() {
        if (DateUtils.isToday(myCalendar.getTimeInMillis()))
            textDate.setText(getResources().getString(R.string.today_text));
        else {
            String myFormat = "dd/MM/yy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            textDate.setText(sdf.format(myCalendar.getTime()));
        }

        Log.w(TAG, "Tempo selecionado do myCalendar (Update label): " + myCalendar.getTime());
        textTime.setText(new StringBuilder().append(myCalendar.getTime().getHours())
                .append(":")
                .append(String.format("%02d", myCalendar.getTime().getMinutes())));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showToast(final String menssage) {
        runOnUiThread(() -> {
            Toast toast = Toast.makeText(getApplicationContext(), menssage, Toast.LENGTH_LONG);
            toast.show();
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        boxMessage.setVisibility(View.GONE);
    }

    private void saveMeasurements(List<Measurement> measurements) {
        if (measurements == null || measurements.isEmpty()) {
            showToast(getString(R.string.value_empty));
            return;
        }
        for (Measurement measurement : measurements) {
            measurement.setUserId(patient.get_id());
            Log.w(TAG, "Tempo selecionado do myCalendar: " + myCalendar.getTime());
            measurement.setTimestamp(DateUtils.convertDateTimeToUTC(myCalendar.getTime()));
            Log.i("AAA", "saving " + measurement.toJson());

        }

        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.confirm_save_measurement))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes_text), (dialog, id) -> {
                    DisposableManager.add(haniotNetRepository
                            .saveMeasurement(measurements)
                            .doAfterSuccess(measurement1 -> {
                                showToast(getString(R.string.measurement_save));
                                finish();
                            })
                            .subscribe(measurement1 -> {
                            }, this::errorHandler));
                })
                .setNegativeButton(getString(R.string.no_text), null)
                .show();
    }

    private void saveMeasurement(Measurement measurement) {
        if (measurement != null) {
            Log.w(TAG, measurement.getType() + ": salvando");
            measurement.setUserId(patient.get_id());
            measurement.setTimestamp(DateUtils.convertDateTimeToUTC(myCalendar.getTime()));
            Log.w(TAG, "Tempo selecionado do myCalendar: " + myCalendar.getTime());
            Log.w(TAG, "JSON: " + measurement.toJson());

            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.confirm_save_measurement))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes_text), (dialog, id) -> {
                        DisposableManager.add(haniotNetRepository
                                .saveMeasurement(measurement)
                                .doAfterSuccess(measurement1 -> {
                                    showToast(getString(R.string.measurement_save));
                                    finish();
                                })
                                .subscribe(measurement1 -> {
                                }, this::errorHandler));
                    })
                    .setNegativeButton(getString(R.string.no_text), null)
                    .show();


        } else showToast(getString(R.string.value_empty));
    }


    /**
     * Displays message.
     *
     * @param str @StringRes message.
     */
    public void showMessage(@StringRes int str) {
        if (str == -1) {
            boxMessage.setVisibility(View.GONE);
            return;
        }

        String message = getResources().getString(str);
        messageError.setText(message);
        runOnUiThread(() -> {
            boxMessage.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
            boxMessage.setVisibility(View.VISIBLE);
        });
    }

    /**
     * Get measurement from Fragment.
     */
    public void prepareMeasurement() {
        Log.w(TAG, typeMeasurement + ": setando valores");

        Measurement measurement = new Measurement();
        MeasurementCommunicator communicator = null;
        if (myFragment != null) communicator = ((MeasurementCommunicator) myFragment);

        measurement.setType(typeMeasurement);

        if (typeMeasurement.equals("anthropometric")) {
            saveMeasurements(communicator.getMeasurementList());

        } else {
            switch (typeMeasurement) {
                case "blood_glucose":
                    if (textMeasurement.getText().toString().isEmpty()) {
                        showMessage(R.string.measurement_invalid);
                        return;
                    }
                    measurement = communicator.getMeasurement();
                    measurement.setValue(Double.valueOf(textMeasurement.getText().toString()));
                    break;
                case "weight":
                case "body_temperature":
                    if (textMeasurement.getText().toString().isEmpty()) {
                        showMessage(R.string.measurement_invalid);
                        return;
                    }
                    measurement.setValue(Double.valueOf(textMeasurement.getText().toString()));
                    measurement.setUnit(textUnit.getText().toString());
                    break;
                case "heart_rate":
                    measurement = communicator.getMeasurement();
                    measurement.getDataset().get(0)
                            .setTimestamp(DateUtils.convertDateTimeToUTC(myCalendar.getTime()));
                    break;
                case "blood_pressure":
                    measurement = communicator.getMeasurement();
                    break;
            }
            saveMeasurement(measurement);
        }
    }

    public interface MeasurementCommunicator {
        Measurement getMeasurement();

        List<Measurement> getMeasurementList();
    }
}
