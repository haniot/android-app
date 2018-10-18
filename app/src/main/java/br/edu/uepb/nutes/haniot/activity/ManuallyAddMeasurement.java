package br.edu.uepb.nutes.haniot.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.fragment.AddBloodGlucoseManuallyFragment;
import br.edu.uepb.nutes.haniot.fragment.AddWeightManuallyFragment;
import br.edu.uepb.nutes.haniot.model.ContextMeasurementValueType;
import br.edu.uepb.nutes.haniot.model.ItemGridType;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.haniot.utils.Log;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManuallyAddMeasurement extends AppCompatActivity implements View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        AddWeightManuallyFragment.SendMessageListener,
        AddBloodGlucoseManuallyFragment.SendMessageListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btnCalendar)
    AppCompatButton btnCalendar;
    @BindView(R.id.btnClock)
    AppCompatButton btnClock;
//    @BindView(R.id.btnConfirm)
//    AppCompatButton btnConfirm;
//    @BindView(R.id.btnCancel)
//    AppCompatButton btnCancel;

    private TimePickerDialog timePicker;
    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    private Session session;
    private String dateTime;
    private String dateHour;
    private int yearSelected = -1;
    private int monthSelected = -1;
    private int daySelected = -1;

//    Fragment to be replaced
    private Fragment myFragment;
    private int type = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manually_add_measurement);
        ButterKnife.bind(this);

//        Default methods for toolbar, remember of use themes on layout xml
        toolbar.setTitle(getResources().getString(R.string.manually_add_measurement));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initComponents();


        final Intent intent = getIntent();
        intent.getExtras();
//        get the type of measurement to replace the fragment
        this.type = intent.getExtras().getInt(getResources().getString(R.string.measurementType));
        if (type == -1){
            finish();
        }else{
            if (ItemGridType.typeSupported(type)){
                replaceFragment(type);
            }else{
                finish();
            }
        }

    }

    private void initComponents(){

        calendar = Calendar.getInstance();
        session = new Session(getApplicationContext());

        datePickerDialog = new DatePickerDialog(getApplicationContext(),
                this,
                calendar.get(Calendar.YEAR),
                Calendar.MONTH,
                Calendar.DAY_OF_MONTH);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        timePicker = new TimePickerDialog(ManuallyAddMeasurement.this,
                (view, hourOfDay, minute1) -> {
            String selectedHour = String.valueOf(hourOfDay)+":"+String.valueOf(minute1);
            setDateHour(selectedHour);
            updateTextHour();
            Log.d("TESTE","Hora: "+hourOfDay+" "+minute1);
        },hour, minute, true);//Yes 24 hour time

        btnCalendar.setOnClickListener(this);
        btnClock.setOnClickListener(this);

        setDateTime(DateUtils.getCurrentDatetime(getResources().getString(R.string.date_format)));
        btnCalendar.setText(getDateTime());

        Date currentDate = calendar.getTime();
        DateFormat format = new SimpleDateFormat(
                getResources().getString(R.string.time_format_simple));
        String formatted = format.format(currentDate);
        setDateHour(formatted);
        btnClock.setText(getDateHour());

    }

    private void updateTextDate(){
        btnCalendar.setText(getDateTime());
    }

    private void updateTextHour(){
        btnClock.setText(getDateHour());
    }

    public void openTimePicker(){

        timePicker.show();
    }

    private void openDatePicker() {
        if (yearSelected == -1) {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            datePickerDialog = new DatePickerDialog(ManuallyAddMeasurement.this,
                    this, year, month, day);
        }else{
            datePickerDialog = new DatePickerDialog(ManuallyAddMeasurement.this,
                    this,yearSelected,monthSelected,daySelected);
        }

        //Essa parte precisa ser assim pois em algumas versões do android ao setar o max date ele
        //buga ao tentar selecionar o ultimo dia
        Calendar maxDate = Calendar.getInstance();
        maxDate.set(Calendar.HOUR_OF_DAY, 23);
        maxDate.set(Calendar.MINUTE, 59);
        maxDate.set(Calendar.SECOND, 59);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        datePickerDialog.show();
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDateHour() {
        return dateHour;
    }

    public void setDateHour(String dateHour) {
        this.dateHour = dateHour;
    }

    //    Here the fragment of the measurement is replaced;
    public void replaceFragment(int measurementType){
        switch (measurementType){
            case ItemGridType.WEIGHT:

                myFragment = new AddWeightManuallyFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_measurement,
                        myFragment)
                        .commit();
                break;

            case ItemGridType.BLOOD_GLUCOSE:

                myFragment = new AddBloodGlucoseManuallyFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_measurement,
                        myFragment)
                        .commit();
                break;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnConfirm:

                break;
            case R.id.btnClock:
                openTimePicker();
                break;
            case R.id.btnCalendar:
                openDatePicker();
                break;
            case R.id.btnCancel:
                finish();
                break;
        }
    }

    private void showToast(final String menssage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), menssage, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        String dateTime = String.valueOf(dayOfMonth)+"/"+String.valueOf(month+1)+"/"
                +String.valueOf(year);
        this.yearSelected = year;
        this.monthSelected = month;
        this.daySelected = dayOfMonth;
        setDateTime(dateTime);
        updateTextDate();
    }

    @Override
    public void onSendMessageWeight(Pair<String, String> data) {

            if (data != null) {
                Log.d("TESTE", "Data 1: " + data.first + " Data 2: " + data.second);
                //funcao de salvar aqui
            }else{
                showToast(getResources().getString(R.string.error_insering_measurement));
            }

    }

    @Override
    public void onSendMessageGlucose(int glucoseValue, int type) {
        Log.d("TESTE","Valor da glicose e tipo: "+glucoseValue+" "+type);
    }
}
