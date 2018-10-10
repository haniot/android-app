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
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;

import java.util.Calendar;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.fragment.AddWeightManuallyFragment;
import br.edu.uepb.nutes.haniot.model.ItemGridType;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import br.edu.uepb.nutes.haniot.utils.Log;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManuallyAddMeasurement extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btnCalendar)
    AppCompatButton btnCalendar;
    @BindView(R.id.btnClock)
    AppCompatButton btnClock;
    @BindView(R.id.btnConfirm)
    AppCompatButton btnConfirm;
    @BindView(R.id.btnCancel)
    AppCompatButton btnCancel;

    private TimePickerDialog timePicker;
    private DatePickerDialog datePickerDialog;
    private Calendar calendar;
    private Session session;

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
        calendar = Calendar.getInstance();
        session = new Session(getApplicationContext());

        datePickerDialog = new DatePickerDialog(getApplicationContext(),
                this,
                calendar.get(Calendar.YEAR),
                Calendar.MONTH,
                Calendar.DAY_OF_MONTH);

        final Intent intent = getIntent();
        intent.getExtras();
        btnCalendar.setOnClickListener(this);
        btnClock.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);

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

    public void openTimePicker(){

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        timePicker = new TimePickerDialog(ManuallyAddMeasurement.this, (view, hourOfDay, minute1) -> {

        }, hour, minute, true);//Yes 24 hour time
        timePicker.show();
    }

    private void openDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(ManuallyAddMeasurement.this, this, year, month, day);

        //Essa parte precisa ser assim pois em algumas versões do android ao setar o max date ele
        //buga ao tentar selecionar o ultimo dia
        Calendar maxDate = Calendar.getInstance();
        maxDate.set(Calendar.HOUR_OF_DAY, 23);
        maxDate.set(Calendar.MINUTE, 59);
        maxDate.set(Calendar.SECOND, 59);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        datePickerDialog.show();
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
                Log.d("TESTE","DADOS: "+session.getString("lastWeight1") + " "+session.getString("lastWeight2"));
                break;
            case R.id.btnClock:
                openTimePicker();
                break;
            case R.id.btnCalendar:
                openDatePicker();
                break;
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

    }
}
