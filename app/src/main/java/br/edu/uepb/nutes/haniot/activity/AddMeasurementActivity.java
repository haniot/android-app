package br.edu.uepb.nutes.haniot.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.PatientsType;
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.fragment.FragmentAnthropometrics;
import br.edu.uepb.nutes.haniot.fragment.FragmentHeartRate;
import br.edu.uepb.nutes.haniot.data.model.ItemGridType;
import br.edu.uepb.nutes.haniot.fragment.FragmentBloodPressure;
import br.edu.uepb.nutes.haniot.fragment.FragmentGlucose;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.HttpException;

public class AddMeasurementActivity extends AppCompatActivity {

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
        if (patient.getGender().equals(PatientsType.GenderType.MALE))
            genderIcon.setImageResource(R.drawable.x_boy);
        else genderIcon.setImageResource(R.drawable.x_girl);
        if (ItemGridType.typeSupported(type)) {
            replaceFragment(type);
        } else {
            finish();
        }

        saveMeasurement.setOnClickListener(v -> getMeasurement());
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
                typeMeasurement = "blood_pressure";
                messageInfo.setText(String.format(getResources().getString(R.string.add_measurement_message), measurementText, patientName));
                getSupportActionBar().setTitle("Inserir Pressão Arterial");
                textUnit.setText(getString(R.string.unit_pressure));
                boxMeasurement.setVisibility(View.GONE);
                myFragment = new FragmentBloodPressure();
                getSupportFragmentManager().beginTransaction().replace(R.id.extra,
                        myFragment).commit();
                break;

            case ItemGridType.ANTHROPOMETRIC:
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
     * Save measurement in server.
     */
    public void saveMeasurement(Measurement measurement) {
        finish();


//        if (textMeasurement.getText().toString().isEmpty()) {
//            messageError.setText("Insira o valor da medição para continuar!");
//            boxMessage.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
//            boxMessage.setVisibility(View.VISIBLE);
//        }
//        Measurement measurement = new Measurement();
//        measurement.setUser(user);
//        measurement.setValue(Double.valueOf(textMeasurement.getText().toString()));
//        measurement.setUnit(textUnit.getText().toString());
//        measurement.setRegistrationDate(myCalendar.getTimeInMillis());
//        switch (type) {
//            case ItemGridType.WEIGHT:
//                measurement.setTypeId(MeasurementType.BODY_MASS);
//                break;
//
//            case ItemGridType.BLOOD_GLUCOSE:
//                measurement.setTypeId(MeasurementType.BLOOD_GLUCOSE);
//                ContextMeasurement contextMeasurement = new ContextMeasurement();
//                contextMeasurement.setTypeId(ContextMeasurementType.GLUCOSE_MEAL);
//                contextMeasurement.setValueId(((FragmentGlucose) myFragment).getPeriod());
//                break;
//
//            case ItemGridType.HEART_RATE:
//                measurement.setTypeId(MeasurementType.HEART_RATE);
//                break;
//
//            case ItemGridType.BLOOD_PRESSURE:
//                measurement.setTypeId(MeasurementType.BLOOD_PRESSURE_SYSTOLIC);
//                ContextMeasurement contextMeasurement1 = new ContextMeasurement();
//                //contextMeasurement1.setTypeId(MeasurementType.HEART_RATE);
//                //contextMeasurement1.setValueId(ContextMeasurementType.);
//                break;
//
//            case ItemGridType.ANTHROPOMETRIC:
//                break;
//
//        }
//        haniotNetRepository.saveMeasurement(measurement)
//                .subscribe(new SingleObserver<Measurement>() {
//                    @Override
//                    public void onSubscribe(Disposable d) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(Measurement measurement) {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//                });
    }

    /**
     * Manipulates the error and displays message
     * according to the type of error.
     *
     * @param e {@link Throwable}
     */
    private void errorHandler(Throwable e) {
        Log.i("AAA", e.getMessage());
        if (e instanceof HttpException) {
            HttpException httpEx = ((HttpException) e);
            switch (httpEx.code()) {
                case 409:
                    //  showMessage(R.string.error_409_patient);
                    break;
                default:
                    //  showMessage(R.string.error_500);
                    break;
            }
        } else {
            //showMessage(R.string.error_500);
        }
    }

    /**
     * Setup date picker.
     */
    public void setupComponents() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        View.OnClickListener timeClick = v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(AddMeasurementActivity.this,
                    (view, hourOfDay, minute) -> {
                        textTime.setText(new StringBuilder().append(hourOfDay)
                                .append(":")
                                .append(minute).toString());
                        myCalendar.getTime().setHours(hourOfDay);
                        myCalendar.getTime().setMinutes(minute);
                    }, mHour, mMinute, false);
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

    private void prepareMeasurement(Measurement measurement) {
        if (measurement != null) {
            Log.i("AAA", "saving " + measurement.toJson());

            measurement.setUserId(patient.get_id());
            measurement.setTimestamp(DateUtils.convertDateTimeToUTC(myCalendar.getTime()));

            DisposableManager.add(haniotNetRepository
                    .saveMeasurement(measurement)
                    .subscribe(measurement1 -> {
                    }, this::errorHandler));

        } else showToast("Digite um valor para continuar!");
    }

    /**
     * Get measurement from Fragment.
     */
    public void getMeasurement() {
        if (myFragment instanceof FragmentAnthropometrics) {
            for (Measurement measurement : ((MeasurementCommunicator) myFragment).getMeasurements())
                prepareMeasurement(measurement);
        } else if (myFragment == null || myFragment instanceof FragmentGlucose) {
            Measurement measurement;
            if (myFragment instanceof FragmentGlucose)
                measurement = ((MeasurementCommunicator) myFragment).getMeasurement();
            else
                measurement = new Measurement();
            measurement.setValue(Double.valueOf(textMeasurement.getText().toString()));
            measurement.setUnit(textUnit.getText().toString());
            measurement.setType(typeMeasurement);
            prepareMeasurement(measurement);
        } else {
            Measurement measurement = ((MeasurementCommunicator) myFragment).getMeasurement();
            if (myFragment instanceof FragmentHeartRate) {
                measurement.getDataset().get(0)
                        .setTimestamp(DateUtils.convertDateTimeToUTC(myCalendar.getTime()));
            }
            prepareMeasurement(measurement);
        }
    }

    public interface MeasurementCommunicator {
        Measurement getMeasurement();

        List<Measurement> getMeasurements();
    }
}
