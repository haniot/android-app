package br.edu.uepb.nutes.haniot.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.model.DateChangedEvent;
import butterknife.BindView;
import butterknife.ButterKnife;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

public class DashboardChartsFragment extends Fragment
//        implements View.OnClickListener,
//        DatePickerDialog.OnDateSetListener
{
//
//    @BindView(R.id.textDate)
//    TextView textDate;
//    @BindView(R.id.textSteps)
//    TextView textSteps;
//    @BindView(R.id.textCalories)
//    TextView textCalories;
//    @BindView(R.id.textDistance)
//    TextView textDistance;
//    @BindView(R.id.buttonArrowLeft)
//    AppCompatImageButton btnArrowLeft;
//    @BindView(R.id.buttonArrowRight)
//    AppCompatButton btnArrowRight;
//   // @BindView(R.id.lightProgress)
//   // CircularProgressBar//stepsProgressBar;
//    @BindView(R.id.lightProgress1)
//    CircularProgressBar caloriesProgressBar;
//    @BindView(R.id.lightProgress2)
//    CircularProgressBar distanceProgressBar;
//    @BindView(R.id.loadingDataProgressBar)
//    CircularProgressBar loadingDataProgressBar;
//
////    default values of goals
//    private int highProgressBarGoal = 200;
//    private int lightProgressBar1Goal = 300;
//    private int lightProgressBar2Goal = 800;
//
//    private final int DIALOG_TYPE_STEPS = 1;
//    private final int DIALOG_TYPE_CALORIES = 2;
//    private final int DIALOG_TYPE_DISTANCE = 3;
//
//    private String textDialogTitle = "";
//
//    //Date part
//    private Calendar calendar;
//    private Calendar calendarAux;
//    private SimpleDateFormat simpleDateFormat;
//    private String date;
//    private String today;
//
////    Annimation part
//    private Animation scale;
//    private AlphaAnimation alphaAnimation;
//    private Boolean animSteps = false;
//    private Boolean animCalories = false;
//    private Boolean animDistance = false;
//
//    private DatePickerDialog datePickerDialog;
//    private int year;
//    private int month;
//    private int day;
//
//    //Server part
//    private Params params;
//    private Session session;
//    private String patientId = "";
//
//    private String[] measurementTypeArray;
//
//    private EventBus _eventBus;
//
//    private boolean measurementSteps = false;
//    private boolean measurementHeartRate = false;
//    private boolean measurementSleep = false;
//    private boolean measurementTemperature = false;
//    private boolean measurementWeight = false;
//    private boolean measurementBloodPressure = false;
//    private boolean measurementBloodGlucose = false;
//    private boolean measurementActivity = false;

//    private DateChangedEvent eventMeasurement;
//
//    private float steps;
//    private float calories;
//    private float distance;

    @BindView(R.id.patientName)
    TextView patientName;

    @BindView(R.id.textDate)
    TextView textDate;

    @BindView(R.id.textValueMeasurement)
    TextView textValueMeasurement;

    @BindView(R.id.textIMC)
    TextView textIMC;

    @BindView(R.id.pulsator)
    PulsatorLayout pulsatorLayout;

    public DashboardChartsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


//
//        _eventBus = EventBus.getDefault();


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    //This method is used when the user turn the screen
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);
//        outState.putString("date", this.date);

    }

    private double calcIMC(double weight) {
        double altura = 1.9;
        double imc = weight / (altura * altura);

        return imc;
    }

    public void updateValueMeasurement(String valueMeasurement) {
        textValueMeasurement.setText(valueMeasurement);
        textIMC.setText(String.format("%.2f", calcIMC(Double.parseDouble(valueMeasurement))));
    }

    public void updateNamePatient(String name) {
        patientName.setText(name);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_charts_dashboard, container, false);
        ButterKnife.bind(this, view);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getResources().getString(R.string.date_format2));
        textDate.setText(simpleDateFormat.format(calendar.getTime()));
        pulsatorLayout.start();
//
//        //Data é atual salva quando abre o aplicativo
//        today = DateUtils.getCurrentDatetime(getResources().getString(R.string.date_format));
////        initData();
//
//        try {
//            this.calendar.setTime(simpleDateFormat.parse(this.date));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
////        updateTextDate(calendar.getTime());
//
//        this.year = this.calendar.get(Calendar.YEAR);
//        this.month = this.calendar.get(Calendar.MONTH);
//        this.day = this.calendar.get(Calendar.DAY_OF_MONTH);
//
//        datePickerDialog = new DatePickerDialog(getContext(), this, year, month, day);
//
//        btnArrowLeft.setOnClickListener(this);
//        btnArrowRight.setOnClickListener(this);
//        textDate.setOnClickListener(this);
//       //stepsProgressBar.setOnClickListener(this);
//        caloriesProgressBar.setOnClickListener(this);
//        distanceProgressBar.setOnClickListener(this);

//        getPatientId();
        return view;
    }

    //    //Get the patient id and enable/disable the control buttons of dashboard
//    public void getPatientId() {
//        String lastId = session.getString(getResources().getString(R.string.id_last_patient));
//        if (!lastId.equals("")) {
//            this.patientId = lastId;
//            if (this.date.equals(this.today)) {
//                btnArrowRight.setEnabled(false);
//                btnArrowRight.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_right_disabled));
//            } else {
//                btnArrowRight.setEnabled(true);
//                btnArrowRight.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_right));
//            }
//            btnArrowLeft.setEnabled(true);
//            btnArrowLeft.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_left));
//            textDate.setEnabled(true);
//           ////stepsProgressBar.setEnabled(true);
//            caloriesProgressBar.setEnabled(true);
//            distanceProgressBar.setEnabled(true);
//            try {
//                loadServerData();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//        } else {
//            btnArrowRight.setEnabled(false);
//            btnArrowRight.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_right_disabled));
//            btnArrowLeft.setEnabled(false);
//            btnArrowLeft.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_left_disabled));
//            textDate.setEnabled(false);
//            try {
//                setDataProgress(0, 0, 0);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            //stepsProgressBar.setEnabled(false);
//            caloriesProgressBar.setEnabled(false);
//            distanceProgressBar.setEnabled(false);
//        }
//    }

    @Override
    public void onStart() {
        super.onStart();
//        _eventBus.register(this);
//        try {
//            loadServerData();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        _eventBus.unregister(this);
    }

    @Subscribe
    public void onDateChanged(DateChangedEvent e) {
    }

    @Override
    public void onResume() {
        super.onResume();
//        getPatientId();
    }

    //    //Formatt the date of dashboard
//    public void updateTextDate(Date dateToText) {
//        Locale current = getResources().getConfiguration().locale;
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(getResources().getString(R.string.date_format_week_day_year), current);
//        String dateFormatted = simpleDateFormat.format(dateToText);
//        textDate.setText(dateFormatted);
//    }
//
//    private void resetMeasurementStatus(){
//        measurementSteps = false;
//        measurementHeartRate = false;
//        measurementSleep = false;
//        measurementTemperature = false;
//        measurementWeight = false;
//        measurementBloodPressure = false;
//        measurementBloodGlucose = false;
//        measurementActivity = false;
//    }
//
//    private void setupEvent(int type, String value){
//
//        if (type == -1){
//            this.eventMeasurement.setHeartRate("--");
//            this.eventMeasurement.setGlucose("--");
//            this.eventMeasurement.setPressure("--");
//            this.eventMeasurement.setTemperature("--");
//            this.eventMeasurement.setWeight("--");
//            this.eventMeasurement.setSleep("--");
//            this.eventMeasurement.setActivity("--");
//            resetMeasurementStatus();
//            return;
//        }
//
//        if (type == MeasurementType.HEART_RATE && !measurementHeartRate){
//            this.eventMeasurement.setHeartRate(value);
//            this.measurementHeartRate = true;
//        }else if (type == MeasurementType.BODY_MASS && !measurementWeight){
//            this.eventMeasurement.setWeight(value);
//            this.measurementWeight = true;
//        }else if (type == MeasurementType.TEMPERATURE && !measurementTemperature) {
//            this.eventMeasurement.setTemperature(value);
//            this.measurementTemperature = true;
//        }else if ((type == MeasurementType.BLOOD_PRESSURE_SYSTOLIC ||
//                type == MeasurementType.BLOOD_PRESSURE_DIASTOLIC)
//                && !measurementBloodPressure){
//            this.eventMeasurement.setPressure(value);
//            this.measurementBloodPressure = true;
//        }else if(type == MeasurementType.BLOOD_GLUCOSE && !measurementBloodGlucose){
//            this.eventMeasurement.setGlucose(value);
//            this.measurementBloodGlucose = true;
//        }
//    }
//
//    //Get the data from server
//    public void loadServerData() throws ParseException {
//
//        //Converte a data para o formato que o servidor aceita
//        String timeInit = DateUtils.calendarToString(this.calendar,getResources().getString(R.string.date_format_server));
//        //Calendario auxiliar criado para pegar a data de amanhã, foi necessário para filtrar a data da requisição
//        String patt = getResources().getString(R.string.date_format);
//        this.calendarAux.setTime(new SimpleDateFormat(patt).parse(this.date));
//        this.calendarAux.add(Calendar.DATE, 1);
//        String timeEnd = DateUtils.calendarToString(calendarAux,getResources().getString(R.string.date_format_server));
//
//        //Prepara a querry e filtra a data para apenas UM dia, a paginação é 1 pois só é apresentado 1 medição por vez no dashboard
//        Historical historical = new Historical.Query()
//                .type(HistoricalType.MEASUREMENTS_USER)
//                .params(params) // Measurements of the temperature type, associated to the user
//                .filterDate(timeInit, timeEnd)
//                .pagination(0, 500)
//                .build();
//
//        historical.request(getContext(), new CallbackHistorical<Measurement>() {
//            @Override
//            public void onBeforeSend() {
//            }
//
//            @Override
//            public void onError(JSONObject result) {
//                Log.d("TESTE","Erro ao solicitar dados ao servidor");
//            }
//
//            @Override
//            public void onResult(List<Measurement> result) {
//
//                if (result != null && result.size() > 0) {
//
//                    float steps = 0;
//                    float calories = 0;
//                    float distance = 0;
//
//                    for(Measurement measurement : result){
//                        int type = measurement.getTypeId();
//                        String value = "";
//                        if (type == MeasurementType.HEART_RATE && !measurementHeartRate) {
//                            value = String.format("%.0f",measurement.getValue());
//                        }else if (type == MeasurementType.BODY_MASS && !measurementWeight){
//                            value = String.format("%.2f",measurement.getValue());
//                        }else if (type == MeasurementType.TEMPERATURE && !measurementTemperature){
//                            value = String.format("%.0f",measurement.getValue());
//                        }else if ((type == MeasurementType.BLOOD_PRESSURE_SYSTOLIC ||
//                                type == MeasurementType.BLOOD_PRESSURE_DIASTOLIC)
//                                && !measurementBloodPressure){
//                            value = String.format("%.0f",measurement.getValue());
//                            value = value+" / "+ String
//                                    .format("%.0f",measurement.getMeasurements().get(0).getValue());
//                        }else if (type == MeasurementType.BLOOD_GLUCOSE && !measurementBloodGlucose){
//                            value = String.format("%.2f",measurement.getValue());
//                        }else if (type == MeasurementType.STEPS && !measurementSteps){
//                            steps = (float)measurement.getValue();
//                            calories = (float) measurement.getMeasurements().get(0)
//                                    .getValue();
//                            distance = (float) measurement.getMeasurements().get(1)
//                                    .getValue();
//                            measurementSteps = true;
//                        }
//                        setupEvent(type,value);
//                    }
//                    if (getActivity() != null){
//                        updateCharts(calories,steps,distance);
//                        setChartsVariables(steps,calories,distance);
//                    }
//                    postEvent();
//
//                    if (getActivity() == null) return;
//
//                } else {
//                    setupEvent(-1,"");
//                    postEvent();
//                    updateCharts(0,0,0);
//                    setChartsVariables(0,0,0);
//                }
//            }
//
//            @Override
//            public void onAfterSend() {
//                if (getContext() != null) {
//                    resetValues();
//                    resetMeasurementStatus();
//                    new Handler(getContext().getMainLooper()).postDelayed(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            loadingDataProgressBar.setVisibility(View.INVISIBLE);
//                        }
//                    }, 200);
//                }
//            }
//
//        });
//
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void swipeUpdate(SwipeEvent event){
//        try {
//            loadServerData();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void updateCharts(float calories, float steps, float distance){
//        try {
//            getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        setDataProgress( steps, calories, distance);
//                        measurementSteps = true;
//                    } catch (Exception e) {
//                        Log.d("TESTE",e.getMessage());
//                        e.printStackTrace();
//                    }
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.d("TESTE",e.getMessage());
//        }
//    }
//
//    //Sum a day
//    public Date increaseDay(String date) throws ParseException {
//
//        loadingDataProgressBar.setVisibility(View.VISIBLE);
//        //Seta a data informada
//        this.calendar.setTime(simpleDateFormat.parse(date));
//        //Adiciona 1 dia
//        this.calendar.add(Calendar.DATE, 1);
//        this.date = simpleDateFormat.format(calendar.getTime());
//        if (this.date.equals(this.today)) {
//            btnArrowRight.setEnabled(false);
//            btnArrowRight.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_right_disabled));
//        }
//
//        return calendar.getTime();
//    }
//
//    //Decrease a day
//    public Date decreaseDay(String date) throws ParseException {
//
//        loadingDataProgressBar.setVisibility(View.VISIBLE);
//        btnArrowRight.setEnabled(true);
//        btnArrowRight.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_right));
//        //Pega a instancia do calendario
//        calendar = Calendar.getInstance();
//        //Seta a data informada
//        calendar.setTime(simpleDateFormat.parse(date));
//        //Remove 1 dia
//        calendar.add(Calendar.DATE, -1);
//        this.date = simpleDateFormat.format(calendar.getTime());
//
//        return calendar.getTime();
//    }
//
//    public void initData() {
//
//        measurementTypeArray = getResources().getStringArray(R.array.measurement_types_array);
//
//        if (this.date.equals(this.today)) {
//            btnArrowRight.setEnabled(false);
//            btnArrowRight.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_right_disabled));
//        } else {
//            btnArrowRight.setEnabled(true);
//        }
//        scale = AnimationUtils.loadAnimation(getContext(), R.anim.click);
//        this.alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
//        this.alphaAnimation.setDuration(500);
//        this.alphaAnimation.setRepeatMode(Animation.REVERSE);
//
//
//        calendarAux = Calendar.getInstance();
//        eventMeasurement = new DateChangedEvent();
//
//        String goalSteps = session.getString("goalSteps");
//        if (!goalSteps.equals(""))
//            highProgressBarGoal = Integer.parseInt(goalSteps);
//        String goalCalories = session.getString("goalCalories");
//        if (!goalCalories.equals(""))
//            lightProgressBar1Goal = Integer.parseInt(goalCalories);
//        String goalDistance = session.getString("goalDistance");
//        if (!goalDistance.equals(""))
//            lightProgressBar2Goal = Integer.parseInt(goalDistance);
//
//        //Seta o progresso máximo
//       //stepsProgressBar.setProgressMax(highProgressBarGoal);
//        caloriesProgressBar.setProgressMax(lightProgressBar1Goal);
//        distanceProgressBar.setProgressMax(lightProgressBar2Goal);
//
//    }
//
////    blink the progressbar and change icon to green flag
//    private void updateChartIcon(boolean steps, boolean calories, boolean distance){
//
//        this.alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {}
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                if (steps && !animSteps) {
//                   //stepsProgressBar.setBackground(
//                   //         getResources().getDrawable(R.drawable.ic_green_flag));
//                    animSteps = !animSteps;
//                }
//                if (calories && !animCalories) {
//                    caloriesProgressBar.setBackground(
//                            getResources().getDrawable(R.drawable.ic_green_flag));
//                    animCalories = !animCalories;
//                }
//                if (distance && !animDistance) {
//                    distanceProgressBar.setBackground(
//                            getResources().getDrawable(R.drawable.ic_green_flag));
//                    animDistance = !animDistance;
//                }
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {}
//        });
//
//        new Handler(getContext().getMainLooper()).postDelayed(() -> {
//            if (steps && !animSteps)
//               //stepsProgressBar.startAnimation(alphaAnimation);
//            if (calories && !animCalories)
//                caloriesProgressBar.startAnimation(alphaAnimation);
//            if (distance && !animDistance)
//                distanceProgressBar.startAnimation(alphaAnimation);
//        }, 3000);
//
//    }
//
//    //Update the data of progressbar of dashboard
//    public void setDataProgress(float numberOfSteps, float numberOfCalories,
//                                float distance) throws Exception{
//
//        int steps = (int) numberOfSteps;
//        int calories = (int) numberOfCalories;
//        int dist = (int) distance;
//
//       //stepsProgressBar.setProgressWithAnimation(steps, 2500);
//
//        boolean stepsAchieved = false;
//        boolean caloriesAchieved = false;
//        boolean distanceAchieved = false;
//
//        if (steps >= this.highProgressBarGoal){
//            stepsAchieved = true;
//        }else{
//           //stepsProgressBar.setBackground(getResources().getDrawable(R.drawable.ic_feet));
//        }
//
//        caloriesProgressBar.setProgressWithAnimation(calories, 3000);
//        if (calories >= this.lightProgressBar1Goal ){
//            caloriesAchieved = true;
//        }else{
//            caloriesProgressBar.setBackground(getResources().getDrawable(R.drawable.ic_calories));
//        }
//
//        distanceProgressBar.setProgressWithAnimation(dist, 3000);
//        if (distance >= this.lightProgressBar2Goal ){
//            distanceAchieved = true;
//        }else{
//            distanceProgressBar.setBackground(getResources().getDrawable(R.drawable.ic_distance));
//        }
//
//        updateChartIcon(stepsAchieved,caloriesAchieved,distanceAchieved);
//
//        //Seta os dados nos textos abaixo da progressbar
//        textSteps.setText(steps + " " + measurementTypeArray[12]);
//        textCalories.setText(calories + " " + measurementTypeArray[14]);
//        if (dist < 1000) {
//            textDistance.setText(dist+ getResources().getString(R.string.unit_meters));
//        } else {
//            float distanceKm = dist / 1000;
//            textDistance.setText(distanceKm + getResources().getString(R.string.unit_kilometer));
//        }
//
//    }
//
//    private void setChartsVariables(float steps, float calories, float distance){
//        this.steps = steps;
//        this.calories = calories;
//        this.distance = distance;
//    }
//
//    //Anims for buttons and textview of date
//    private void animLeftBtn(){
//        try {
//            btnArrowLeft.startAnimation(scale);
//            animSteps = false;
//            animCalories = false;
//            animDistance = false;
//            updateTextDate(decreaseDay(this.date));
//            loadServerData();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void animRightBtn(){
//        try {
//            btnArrowRight.startAnimation(scale);
//            animSteps = false;
//            animCalories = false;
//            animDistance = false;
//            updateTextDate(increaseDay(this.date));
//            loadServerData();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void animTextDate(){
//        textDate.startAnimation(scale);
//        try {
//            openDatePicker();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void createGoalsDialog(int dialogType){
//
//        LayoutInflater li = LayoutInflater.from(getContext());
//        View promptView = li.inflate(R.layout.dialog_dashboard_goals, null);
//
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
//        alertDialogBuilder.setView(promptView);
//
//        final TextInputEditText userInput = (TextInputEditText) promptView.findViewById(
//                R.id.goalsInput);
//
//        TextView dialogTitle = (TextView) promptView.findViewById(R.id.textGoals);
//
//        switch (dialogType) {
//            case DIALOG_TYPE_STEPS:
//                this.textDialogTitle = getResources().getString(R.string.goals_steps);
//                userInput.setHint("/"+String.valueOf(this.highProgressBarGoal));
//                break;
//            case DIALOG_TYPE_CALORIES:
//                this.textDialogTitle = getResources().getString(R.string.goals_calories);
//                userInput.setHint("/"+String.valueOf(this.lightProgressBar1Goal));
//                break;
//            case DIALOG_TYPE_DISTANCE:
//                this.textDialogTitle = getResources().getString(R.string.goals_distance);
//                userInput.setHint("/"+String.valueOf(this.lightProgressBar2Goal));
//                break;
//        }
//
//        alertDialogBuilder
//                .setCancelable(false)
//                .setPositiveButton(getResources().getString(R.string.bt_ok), (dialogInterface, i) -> {
//
//                    String value = userInput.getText().toString();
//                    switch (dialogType){
//
//                        case DIALOG_TYPE_STEPS:
//                            session.putString("goalSteps",value);
//                            if (value!= null && !value.equals("")) {
//                                float maxProgress = Float.parseFloat(value);
//                                this.highProgressBarGoal = Integer.parseInt(value);
//                               //stepsProgressBar.setProgressMax(maxProgress);
//                                if (maxProgress > this.steps){
//                                    this.animSteps = false;
//                                }
//                                try {
//                                    setDataProgress(this.steps,this.calories,this.distance);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                            break;
//                        case DIALOG_TYPE_CALORIES:
//                            session.putString("goalCalories",value);
//                            if (value!= null && !value.equals("")) {
//                                float maxProgress = Float.parseFloat(value);
//                                this.lightProgressBar1Goal = Integer.parseInt(value);
//                                caloriesProgressBar.setProgressMax(maxProgress);
//                                if (maxProgress > this.calories){
//                                    this.animCalories = false;
//                                }
//                                try {
//                                    setDataProgress(this.steps,this.calories,this.distance);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                            break;
//                        case DIALOG_TYPE_DISTANCE:
//                            session.putString("goalDistance",value);
//                            if (value!= null && !value.equals("")) {
//                                float maxProgress = Float.parseFloat(value);
//                                this.lightProgressBar2Goal = Integer.parseInt(value);
//                                distanceProgressBar.setProgressMax(maxProgress);
//                                if (maxProgress > this.distance){
//                                    this.animDistance = false;
//                                }
//                                try {
//                                    setDataProgress(this.steps,this.calories,this.distance);
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                            break;
//                    }
//                })
//                .setNegativeButton(getResources().getString(R.string.bt_cancel), (dialogInterface, i) -> {
//                    dialogInterface.cancel();
//                });
//        dialogTitle.setText(this.textDialogTitle);
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//
//            // Button that decrease the days of dashboard
//            case R.id.buttonArrowLeft:
//                animLeftBtn();
//                break;
//
//            // Button that increase the days of dashboard
//            case R.id.buttonArrowRight:
//                animRightBtn();
//                break;
//            // TextView of date
//            case R.id.textDate:
//                animTextDate();
//                break;
//
//            //case R.id.lightProgress:
//               //stepsProgressBar.startAnimation(scale);
//            //    createGoalsDialog(DIALOG_TYPE_STEPS);
//             //   break;
//
//            case R.id.lightProgress1:
//                caloriesProgressBar.startAnimation(scale);
//                createGoalsDialog(DIALOG_TYPE_CALORIES);
//                break;
//
//            case R.id.lightProgress2:
//                distanceProgressBar.startAnimation(scale);
//                createGoalsDialog(DIALOG_TYPE_DISTANCE);
//                break;
//        }
//    }
//
//    /**
//     * Open datepicker.
//     */
//    private void openDatePicker() {
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//        datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
//
//        //Essa parte precisa ser assim pois em algumas versões do android ao setar o max date ele
//        //buga ao tentar selecionar o ultimo dia
//        Calendar maxDate = Calendar.getInstance();
//        maxDate.set(Calendar.HOUR_OF_DAY, 23);
//        maxDate.set(Calendar.MINUTE, 59);
//        maxDate.set(Calendar.SECOND, 59);
//        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
//        datePickerDialog.show();
//
//        /**
//         * Close keyboard
//         */
//        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
//                .hideSoftInputFromWindow(textDate.getWindowToken(), 0);
//    }
//
//    public String getData() {
//        return this.date;
//    }
//
//    public String getToday() {
//        return this.today;
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    private void postEvent(){
//        DateChangedEvent event = new DateChangedEvent();
//        event = this.eventMeasurement;
//        EventBus.getDefault().post(event);
//    }
//
//    private void resetValues(){
//        this.eventMeasurement.resetAllValues();
//    }
//
//    private String getSelectedData(){
//        SimpleDateFormat spn = new SimpleDateFormat(getResources()
//                .getString(R.string.date_format));
//        return spn.format(this.calendar.getTime());
//    }
//
//    @Override
//    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//        this.calendar.set(year, month, dayOfMonth);
//        this.date = simpleDateFormat.format(calendar.getTime());
//        updateTextDate(this.calendar.getTime());
//        if (!this.date.equals(this.today)) {
//            btnArrowRight.setEnabled(true);
//            btnArrowRight.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_right));
//        } else {
//            btnArrowRight.setEnabled(false);
//            btnArrowRight.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_right_disabled));
//        }
//        loadingDataProgressBar.setVisibility(View.VISIBLE);
//        animSteps = false;
//        animCalories = false;
//        animDistance = false;
//        try {
//            loadServerData();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        /**
//         * open keyboard
//         */
//        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
//                .showSoftInput(textDate, InputMethodManager.SHOW_IMPLICIT);
//
//    }
// Dispara o metodo implementado pela Activity

    public interface Communicator {
        void respond(String data);
    }
}
