package br.edu.uepb.nutes.haniot.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.fragment.AddAnthropometricsFragment;
import br.edu.uepb.nutes.haniot.fragment.AddHeartRateManuallyFragment;
import br.edu.uepb.nutes.haniot.model.ItemGridType;
import br.edu.uepb.nutes.haniot.server.SynchronizationServer;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManuallyAddMeasurement extends AppCompatActivity
//        implements View.OnClickListener,
//        DatePickerDialog.OnDateSetListener,
//        AddWeightManuallyFragment.SendMessageListener,
//        AddBloodGlucoseManuallyFragment.SendMessageListener,
//        AddHeartRateManuallyFragment.SendMessageListener,
//        AddTemperatureManuallyFragment.SendMessageListener,
//        AddBloodPressureManuallyFragment.SendMessageListener,
//        AddAnthropometricsFragment.SendMessageListener
{

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

    final Calendar myCalendar = Calendar.getInstance();

    //    @BindView(R.id.btnCalendar)
//    AppCompatButton btnCalendar;
//    @BindView(R.id.btnClock)
//    AppCompatButton btnClock;
//
//    private TimePickerDialog timePicker;
//    private DatePickerDialog datePickerDialog;
//    private Calendar calendar;
//    private Session session;
//    private String dateTime = "";
//    private String dateHour = "";
//    private int yearSelected = -1;
//    private int monthSelected = -1;
//    private int daySelected = -1;
//
//    private MeasurementDAO measurementDAO;
//
//    //    Fragment to be replaced
    private Fragment myFragment;
    private int type = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manually_add_measurement);
        ButterKnife.bind(this);

//        Default methods for toolbar, remember of use themes on layout xml
        toolbar.setTitle("Adicionar glicose");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        openDataPicker();
        textMeasurement.setSelection(textMeasurement.getText().length());
//        FragmentManager fm = getSupportFragmentManager();
//// Substitui um Fragment
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.add(R.id.extra, new FragmentGlucose());
//        ft.commit();
        // openMeasurementPicker();
//        Spinner picker = new Spinner(this);
//        ArrayList<String> strings = new ArrayList<>();
//        for (int i = 0; i <= 200; i++)
//            strings.add(String.valueOf(i));
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(ManuallyAddMeasurement.this, android.R.layout.simple_spinner_item, strings);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        picker.setAdapter(adapter);
//        initComponents();
//

        final Intent intent = getIntent();
        intent.getExtras();
//        get the type of measurement to replace the fragment
        this.type = intent.getExtras().getInt(getResources().getString(R.string.measurementType));
        if (type == -1) {
            finish();
        } else {
            if (ItemGridType.typeSupported(type)) {
                replaceFragment(type);
            } else {
                finish();
            }
        }

    }

    //    private void initComponents() {
//
//        calendar = Calendar.getInstance();
//        session = new Session(getApplicationContext());
//
//        datePickerDialog = new DatePickerDialog(getApplicationContext(),
//                this,
//                calendar.get(Calendar.YEAR),
//                Calendar.MONTH,
//                Calendar.DAY_OF_MONTH);
//
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int minute = calendar.get(Calendar.MINUTE);
//        timePicker = new TimePickerDialog(ManuallyAddMeasurement.this,
//                (view, hourOfDay, minute1) -> {
//                    String selectedHour = String.valueOf(hourOfDay) + ":" + String.valueOf(minute1);
//                    String secondChar = String.valueOf(selectedHour.charAt(1));
////            adiciona o 0 para datas com 1 casa decimal
//                    if (secondChar.equals(":")) {
//                        selectedHour = "0" + selectedHour;
//                    }
//                    if (selectedHour.length() == 4) {
//                        String firstPart = selectedHour.substring(0, 3);
//                        firstPart = firstPart + "0";
//                        String secondPart = selectedHour.substring(3, 4);
//                        selectedHour = firstPart + secondPart;
//                        Log.d("TESTE", "Hora tratada: " + selectedHour);
//                    }
//                    selectedHour = selectedHour + ":00";
//                    Log.d("TESTE","Hora setada : "+selectedHour);
//                    setDateHour(selectedHour);
//                    updateTextHour();
//                }, hour, minute, true);//Yes 24 hour time
//
//        btnCalendar.setOnClickListener(this);
//        btnClock.setOnClickListener(this);
//
//        btnCalendar.setText(DateUtils.getCurrentDatetime(getResources()
//                .getString(R.string.date_format)));
//
//        Date currentDate = calendar.getTime();
//        DateFormat format = new SimpleDateFormat(
//                getResources().getString(R.string.time_format));
//        String formatted = format.format(currentDate);
//        btnClock.setText(formatted);
//        measurementDAO = MeasurementDAO.getInstance(this);
//
//    }
//
//    private void updateTextDate() {
//        btnCalendar.setText(getDateTime());
//    }
//
//    private void updateTextHour() {
//        btnClock.setText(getDateHour());
//    }
//
//    public void openTimePicker() {
//
//        timePicker.show();
//    }
//
//    private void openDatePicker() {
//        if (yearSelected == -1) {
//            int year = calendar.get(Calendar.YEAR);
//            int month = calendar.get(Calendar.MONTH);
//            int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//            datePickerDialog = new DatePickerDialog(ManuallyAddMeasurement.this,
//                    this, year, month, day);
//        } else {
//            datePickerDialog = new DatePickerDialog(ManuallyAddMeasurement.this,
//                    this, yearSelected, monthSelected, daySelected);
//        }
//
//        //Essa parte precisa ser assim pois em algumas versões do android ao setar o max date ele
//        //buga ao tentar selecionar o ultimo dia
//        Calendar maxDate = Calendar.getInstance();
//        maxDate.set(Calendar.HOUR_OF_DAY, 23);
//        maxDate.set(Calendar.MINUTE, 59);
//        maxDate.set(Calendar.SECOND, 59);
//        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
//        datePickerDialog.show();
//    }
//
//    public String getDateTime() {
//        return dateTime;
//    }
//
//    public void setDateTime(String dateTime) {
//        this.dateTime = dateTime;
//    }
//
//    public String getDateHour() {
//        return dateHour;
//    }
//
//    public void setDateHour(String dateHour) {
//        this.dateHour = dateHour;
//    }
//
    //    Here the fragment of the measurement is replaced;
    public void replaceFragment(int measurementType) {
        switch (measurementType) {
            case ItemGridType.WEIGHT:
                getSupportActionBar().setTitle("Inserir Peso");
                textUnit.setText(getString(R.string.unit_kg));
//                myFragment = new Add();
//                getSupportFragmentManager().beginTransaction().replace(R.id.extra,
//                        myFragment)
//                        .commit();
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
                myFragment = new AddHeartRateManuallyFragment();
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
                myFragment = new AddAnthropometricsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.extra,
                        myFragment).commit();
                break;
        }
    }

    public void openMeasurementPicker() {

        final Dialog d = new Dialog(ManuallyAddMeasurement.this);
        d.setTitle("Choose value of measurement:");
        d.setContentView(R.layout.measurement_picker_dialog);
        Button b1 = (Button) d.findViewById(R.id.button1);
        Button b2 = (Button) d.findViewById(R.id.button2);
        final NumberPicker np = d.findViewById(R.id.numberPicker1);
        np.setMaxValue(100); // max value 100
        np.setMinValue(0);   // min value 0
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener((picker, oldVal, newVal) -> {

        });
        b1.setOnClickListener(v -> {
            textMeasurement.setText(String.valueOf(np.getValue())); //set the value to textview
            d.dismiss();
        });
        b2.setOnClickListener(v -> {
            d.dismiss(); // dismiss the dialog
        });
        d.show();


//        final AlertDialog.Builder d = new AlertDialog.Builder(ManuallyAddMeasurement.this);
//        LayoutInflater inflater = this.getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
//        d.setTitle("Title");
//        d.setMessage("Message");
//        d.setView(dialogView);
//        final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker);
//        numberPicker.setMaxValue(50);
//        numberPicker.setMinValue(1);
//        numberPicker.setWrapSelectorWheel(false);
//        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
//                Log.d(TAG, "onValueChange: ");
//            }
//        });
//        d.setPositiveButton("Done", (dialogInterface, i) -> Log.d(TAG, "onClick: " + numberPicker.getValue()));
//        d.setNegativeButton("Cancel", (dialogInterface, i) -> {
//        });
//        AlertDialog alertDialog = d.create();
//        alertDialog.show();
    }

    public void openDataPicker() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        iconTime.setOnClickListener(v -> {

            TimePickerDialog timePickerDialog = new TimePickerDialog(ManuallyAddMeasurement.this,
                    (view, hourOfDay, minute) ->
                            textTime.setText(hourOfDay + ":" + minute), mHour, mMinute, false);
            timePickerDialog.show();
        });
        icon_calendar.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            new DatePickerDialog(ManuallyAddMeasurement.this, date, myCalendar
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
//
//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.btnConfirm:
//
//                break;
//            case R.id.btnClock:
//                openTimePicker();
//                break;
//            case R.id.btnCalendar:
//                openDatePicker();
//                break;
//            case R.id.btnCancel:
//                finish();
//                break;
//        }
//    }

    private void showToast(final String menssage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), menssage, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

//    @Override
//    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
//        String dateTime = String.valueOf(dayOfMonth) + "/" + String.valueOf(month + 1) + "/"
//                + String.valueOf(year);
//        this.yearSelected = year;
//        this.monthSelected = month;
//        this.daySelected = dayOfMonth;
//        setDateTime(dateTime);
//        updateTextDate();
//    }

    private void synchronizeWithServer() {
        SynchronizationServer.getInstance(this).run();
    }

//    private void addMeasurement(Measurement measurement) {
//
//        Long dateServer = null;
//        String dateTimeSelected = "";
//
////                quando o usuário não modificou data nem hora
//        if (this.dateTime.equals("") && this.dateHour.equals("")) {
//            dateServer = DateUtils.getCurrentDatetime();
//
////                    quando o usuário modificou apenas a hora
//        } else if (this.dateTime.equals("") && !this.dateHour.equals("")) {
//
//            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
//            Date currentDate = new Date();
//            String current = format.format(currentDate);
//
//            dateTimeSelected = current + " " + this.dateHour;
//            Log.d("TESTE","Modificou a hora para : "+this.dateHour);
//            dateServer = DateUtils.getDateStringInMillis(dateTimeSelected, getResources().getString(R.string.datetime_format));
//
////                quando o usuário modificou apenas a data
//        } else if (!this.dateTime.equals("") && this.dateHour.equals("")) {
//
//            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
//            Date currentDate = new Date();
//            String currentHour = format.format(currentDate);
//            dateTimeSelected = this.dateTime + " " + currentHour;
//            dateServer = DateUtils.getDateStringInMillis(dateTimeSelected,
//                    getResources().getString(R.string.datetime_format));
//
//
////                                        quando o usuário modificou data e hora
//        } else if (!this.dateTime.equals("") && !this.dateHour.equals("")) {
//
//            dateTimeSelected = this.dateTime + " " + this.dateHour;
//            dateServer = DateUtils.getDateStringInMillis(dateTimeSelected, getResources().getString(R.string.datetime_format));
//
//        }
//
//        if (dateServer == null && dateTimeSelected.equals("")) return;
//
//        measurement.setRegistrationDate(dateServer);
//
//        if (this.measurementDAO.save(measurement)) {
//            Log.d("TESTE", "Salvo no banco o valor: "+measurement.getValue()
//                    +" com a data: "+DateUtils.formatDate(measurement.getRegistrationDate(),getResources().getString(R.string.datetime_format)));
//            Log.d("TESTE","\n "+measurement.toString());
//            synchronizeWithServer();
//        }
//    }
//
//    @Override
//    public void onSendMessageWeight(Pair<String, String> data) {
//
//        if (data != null) {
//            //funcao de salvar aqui
//            String weight = data.first + "." + data.second;
//            double value = Double.valueOf(weight);
//
//            Measurement weightMeasurement = new Measurement();
//            weightMeasurement.setUser(session.getUserLogged());
//            weightMeasurement.setValue(value);
//            weightMeasurement.setTypeId(MeasurementType.BODY_MASS);
//            weightMeasurement.setUnit(getResources().getString(R.string.unit_weight));
//
//            addMeasurement(weightMeasurement);
//            finish();
//        } else {
//            showToast(getResources().getString(R.string.error_insering_measurement));
//        }
//
//    }
//
//    @Override
//    public void onSendMessageGlucose(int glucoseValue, int type) {
//        if (glucoseValue != -1) {
//            Log.d("TESTE", "Valor da glicose e tipo: " + glucoseValue + " " + type);
//            double value = Double.valueOf(glucoseValue);
//
//            Measurement glucose = new Measurement();
//            glucose.setUser(session.getUserLogged());
//            glucose.setValue(value);
//            glucose.setTypeId(MeasurementType.BLOOD_GLUCOSE);
//            glucose.setUnit(getResources().getString(R.string.unit_glucose_mg_dL));
//            if (type == -1) return;
//            glucose.addContext(new ContextMeasurement(type, ContextMeasurementType.GLUCOSE_MEAL));
//
//            Log.d("TESTE",glucose.toString());
//            addMeasurement(glucose);
//            finish();
//        } else {
//            showToast(getResources().getString(R.string.error_insering_measurement));
//        }
//    }
//
//    @Override
//    public void onSendMessageHeartRate(int heartBeat) {
//        if (heartBeat != -1) {
//            Log.d("TESTE", "Valor do bpm: " + heartBeat);
//            double value = Double.valueOf(heartBeat);
//
//            Measurement heartMeasurement = new Measurement();
//            heartMeasurement.setUser(session.getUserLogged());
//            heartMeasurement.setValue(heartBeat);
//            heartMeasurement.setTypeId(MeasurementType.HEART_RATE);
//            heartMeasurement.setUnit(getResources().getString(R.string.unit_heart_rate));
//
//            addMeasurement(heartMeasurement);
//            finish();
//        } else {
//            showToast(getResources().getString(R.string.error_insering_measurement));
//        }
//    }
//
//    @Override
//    public void onSendMessageTemperature(int temperature) {
//        if (temperature != -1) {
//            Log.d("TESTE", "Valor da temperatura: " + temperature);
//            double value = Double.valueOf(temperature);
//
//            Measurement temp = new Measurement();
//            temp.setUser(session.getUserLogged());
//            temp.setTypeId(MeasurementType.TEMPERATURE);
//            temp.setValue(temperature);
//            temp.setUnit(getResources().getString(R.string.unit_temperature));
//
//            addMeasurement(temp);
//            finish();
//        } else {
//            showToast(getResources().getString(R.string.error_insering_measurement));
//        }
//    }
//
//    @Override
//    public void onSendMessagePressure(Pair<Integer, Integer> pressure, int pulse) {
//        if (pressure != null && pulse != -1) {
//            Log.d("TESTE", "Valor da pressao: " + pressure.first + "/" + pressure.second
//                    + " pulse: " + pulse);
//
//            Measurement systolic = new Measurement();
//            systolic.setUser(session.getUserLogged());
//            systolic.setValue(pressure.first);
//            systolic.setTypeId(MeasurementType.BLOOD_PRESSURE_SYSTOLIC);
//            systolic.setUnit(getResources().getString(R.string.unit_pressure));
//
//            Measurement diastolic = new Measurement();
//            diastolic.setUser(session.getUserLogged());
//            diastolic.setValue(pressure.second);
//            diastolic.setTypeId(MeasurementType.BLOOD_PRESSURE_DIASTOLIC);
//
//            Measurement heartRate = new Measurement();
//            heartRate.setValue(pulse);
//            heartRate.setUser(session.getUserLogged());
//            heartRate.setTypeId(MeasurementType.HEART_RATE);
//
//            systolic.addMeasurement(diastolic, heartRate);
//            addMeasurement(systolic);
//            finish();
//            return;
//        }
//        showToast(getResources().getString(R.string.error_insering_measurement));
//    }
//
//    @Override
//    public void onSendMessageAnthropometric(double height, double circumference) {
//
//        if (height != -1 && circumference != -1) {
//            if (height != -1) {
//                Measurement measurementHeight = new Measurement();
//                measurementHeight.setUser(session.getUserLogged());
//                measurementHeight.setValue(height);
//                measurementHeight.setTypeId(MeasurementType.HEIGHT);
//                measurementHeight.setUnit(getResources().getString(R.string.unit_meters));
//
//                addMeasurement(measurementHeight);
//            }
//            if (circumference != -1) {
//                Measurement measurementCircumference = new Measurement();
//                measurementCircumference.setUser(session.getUserLogged());
//                measurementCircumference.setValue(circumference);
//                measurementCircumference.setTypeId(MeasurementType.CIRCUMFERENCE);
//                measurementCircumference.setUnit(getResources().getString(R.string.unit_meters));
//
//                addMeasurement(measurementCircumference);
//            }
//            finish();
//            return;
//        }
//        showToast(getResources().getString(R.string.error_insering_measurement));
//
//    }
}
