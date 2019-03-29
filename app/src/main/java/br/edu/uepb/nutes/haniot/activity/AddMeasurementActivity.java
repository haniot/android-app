package br.edu.uepb.nutes.haniot.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.data.model.ContextMeasurement;
import br.edu.uepb.nutes.haniot.data.model.ContextMeasurementType;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.MeasurementType;
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.fragment.FragmentAnthropometrics;
import br.edu.uepb.nutes.haniot.fragment.FragmentHeartRate;
import br.edu.uepb.nutes.haniot.data.model.ItemGridType;
import br.edu.uepb.nutes.haniot.fragment.FragmentBloodPressure;
import br.edu.uepb.nutes.haniot.fragment.FragmentGlucose;
import br.edu.uepb.nutes.haniot.server.SynchronizationServer;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

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

    @BindView(R.id.text_measurement)
    EditText textMeasurement;

    @BindView(R.id.text_unit)
    TextView textUnit;

    @BindView(R.id.saveMeasurement)
    FloatingActionButton saveMeasurement;

    private final Calendar myCalendar = Calendar.getInstance();
    private HaniotNetRepository haniotNetRepository;
    private AppPreferencesHelper appPreferencesHelper;
    private Fragment myFragment;
    private User user;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_measurement);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupDatePicker();
        haniotNetRepository = HaniotNetRepository.getInstance(this);
        appPreferencesHelper = AppPreferencesHelper.getInstance(this);
        user = appPreferencesHelper.getUserLogged();
        type = appPreferencesHelper.getInt(getResources().getString(R.string.measurementType));
        if (ItemGridType.typeSupported(type)) {
            replaceFragment(type);
        } else {
            finish();
        }
        saveMeasurement.setOnClickListener(v -> saveMeasurement());
    }

    /**
     * Set fragment.
     *
     * @param measurementType
     */
    public void replaceFragment(int measurementType) {
        switch (measurementType) {
            case ItemGridType.WEIGHT:
                getSupportActionBar().setTitle("Inserir Peso");
                textUnit.setText(getString(R.string.unit_kg));
                break;

            case ItemGridType.BLOOD_GLUCOSE:
                getSupportActionBar().setTitle("Inserir Glicose");
                textUnit.setText(getString(R.string.unit_glucose_mg_dL));
                myFragment = new FragmentGlucose();
                getSupportFragmentManager().beginTransaction().replace(R.id.extra,
                        myFragment)
                        .commit();
                break;

            case ItemGridType.HEART_RATE:
                getSupportActionBar().setTitle("Inserir Batimentos Cardíacos");
                textUnit.setText(getString(R.string.unit_heart_rate));
                myFragment = new FragmentHeartRate();
                getSupportFragmentManager().beginTransaction().replace(R.id.extra,
                        myFragment)
                        .commit();
                break;

            case ItemGridType.BLOOD_PRESSURE:
                getSupportActionBar().setTitle("Inserir Pressão Arterial");
                textUnit.setText(getString(R.string.unit_pressure));
                myFragment = new FragmentBloodPressure();
                getSupportFragmentManager().beginTransaction().replace(R.id.extra,
                        myFragment).commit();
                break;

            case ItemGridType.ANTHROPOMETRIC:
                getSupportActionBar().setTitle("Inserir Medidas Antropométricas");
                myFragment = new FragmentAnthropometrics();
                getSupportFragmentManager().beginTransaction().replace(R.id.extra,
                        myFragment).commit();
                break;
        }
    }

    /**
     * Save measurement in server.
     */
    public void saveMeasurement() {
        Measurement measurement = new Measurement();
        measurement.setUser(user);
        measurement.setValue(Double.valueOf(textMeasurement.getText().toString()));
        measurement.setUnit(textUnit.getText().toString());
        measurement.setRegistrationDate(myCalendar.getTimeInMillis());
        switch (type) {
            case ItemGridType.WEIGHT:
                measurement.setTypeId(MeasurementType.BODY_MASS);
                break;

            case ItemGridType.BLOOD_GLUCOSE:
                measurement.setTypeId(MeasurementType.BLOOD_GLUCOSE);
                ContextMeasurement contextMeasurement = new ContextMeasurement();
                contextMeasurement.setTypeId(ContextMeasurementType.GLUCOSE_MEAL);
                contextMeasurement.setValueId(((FragmentGlucose) myFragment).getPeriod());
                break;

            case ItemGridType.HEART_RATE:
                measurement.setTypeId(MeasurementType.HEART_RATE);
                break;

            case ItemGridType.BLOOD_PRESSURE:
                measurement.setTypeId(MeasurementType.BLOOD_PRESSURE_SYSTOLIC);
                ContextMeasurement contextMeasurement1 = new ContextMeasurement();
                //contextMeasurement1.setTypeId(MeasurementType.HEART_RATE);
                //contextMeasurement1.setValueId(ContextMeasurementType.);
                break;

            case ItemGridType.ANTHROPOMETRIC:
                break;

        }
        haniotNetRepository.saveMeasurement(measurement)
                .subscribe(new SingleObserver<Measurement>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Measurement measurement) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }
    
    /**
     * Setup date picker.
     */
    public void setupDatePicker() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        iconTime.setOnClickListener(v -> {

            TimePickerDialog timePickerDialog = new TimePickerDialog(AddMeasurementActivity.this,
                    (view, hourOfDay, minute) ->
                            textTime.setText(hourOfDay + ":" + minute), mHour, mMinute, false);
            timePickerDialog.show();
        });
        icon_calendar.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            new DatePickerDialog(AddMeasurementActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
        // TODO Auto-generated method stub
        myCalendar.set(Calendar.YEAR, year);
        myCalendar.set(Calendar.MONTH, monthOfYear);
        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateLabel();
    };

    private void updateLabel() {
        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        textDate.setText(sdf.format(myCalendar.getTime()));
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

    private void synchronizeWithServer() {
        SynchronizationServer.getInstance(this).run();
    }

}
