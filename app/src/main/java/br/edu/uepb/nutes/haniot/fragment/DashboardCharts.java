package br.edu.uepb.nutes.haniot.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TextView;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.MeasurementType;
import br.edu.uepb.nutes.haniot.server.historical.CallbackHistorical;
import br.edu.uepb.nutes.haniot.server.historical.Historical;
import br.edu.uepb.nutes.haniot.server.historical.HistoricalType;
import br.edu.uepb.nutes.haniot.server.historical.Params;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DashboardCharts extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    @BindView(R.id.textDate)
    TextView textDate;
    @BindView(R.id.textSteps)
    TextView textSteps;
    @BindView(R.id.textCalories)
    TextView textCalories;
    @BindView(R.id.textDistance)
    TextView textDistance;
    @BindView(R.id.buttonArrowLeft)
    AppCompatImageButton btnArrowLeft;
    @BindView(R.id.buttonArrowRight)
    AppCompatButton btnArrowRight;
    @BindView(R.id.stepsProgressBar)
    CircularProgressBar stepsProgressBar;
    @BindView(R.id.lightProgress1)
    CircularProgressBar caloriesProgressBar;
    @BindView(R.id.lightProgress2)
    CircularProgressBar distanceProgressBar;
    @BindView(R.id.loadingDataProgressBar)
    CircularProgressBar loadingDataProgressBar;

    private int highProgressBarGoal = 200;
    private int lightProgressBar1Goal = 300;
    private int lightProgressBar2Goal = 800;

    //Date part

    private DateUtils dateUtils;
    private Calendar calendar;
    private Calendar calendarAux;
    private SimpleDateFormat simpleDateFormat;
    private String date;
    private String today;
    private Animation scale;

    private DatePickerDialog datePickerDialog;
    private int year;
    private int month;
    private int day;

    //Server part
    private Params params;
    private Session session;
    private String childId = "";

    private String[] measurementTypeArray;

    public DashboardCharts() {
        // Required empty public constructor
    }

    public static DashboardCharts newInstance(String param1, String param2) {
        DashboardCharts fragment = new DashboardCharts();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Inicia a sessão com o ID logado e pega as medições do tipo STEPS
        session = new Session(getContext());
        params = new Params(session.get_idLogged(), MeasurementType.STEPS);

        //Formata a data para o dia / mes / ano
        simpleDateFormat = new SimpleDateFormat("dd / MM / yyyy");
        calendar = Calendar.getInstance();

        //Usado para quando virar a tela manter a data
        if (savedInstanceState != null) {
            this.date = savedInstanceState.getString("date");
        } else {
            this.date = simpleDateFormat.format(calendar.getTime());
        }
    }

    //This method is used when the user turn the screen
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putString("date", this.date);
        outState.putBoolean("change", true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_dash1, container, false);
        ButterKnife.bind(this, view);

        //Data é atual salva quando abre o aplicativo
        today = simpleDateFormat.format(calendar.getTime());
        initData();


        try {
            this.calendar.setTime(simpleDateFormat.parse(this.date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        updateTextDate(calendar.getTime());

        this.year = this.calendar.get(Calendar.YEAR);
        this.month = this.calendar.get(Calendar.MONTH);
        this.day = this.calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(getContext(), this, year, month, day);

        btnArrowLeft.setOnClickListener(this);
        btnArrowRight.setOnClickListener(this);
        textDate.setOnClickListener(this);

        getChildId();
        return view;
    }
    //Get the child id and enable/disable the control buttons of dashboard
    public void getChildId() {
        String lastId = session.getString(getResources().getString(R.string.id_last_child));
        if (!lastId.equals("")) {
            this.childId = lastId;
            if (this.date.equals(this.today)) {
                btnArrowRight.setEnabled(false);
                btnArrowRight.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_right_disabled));
            } else {
                btnArrowRight.setEnabled(true);
                btnArrowRight.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_right));
            }
            btnArrowLeft.setEnabled(true);
            btnArrowLeft.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_left));
            textDate.setEnabled(true);
            stepsProgressBar.setEnabled(true);
            caloriesProgressBar.setEnabled(true);
            distanceProgressBar.setEnabled(true);
            try {
                loadServerData();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            btnArrowRight.setEnabled(false);
            btnArrowRight.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_right_disabled));
            btnArrowLeft.setEnabled(false);
            btnArrowLeft.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_left_disabled));
            textDate.setEnabled(false);
            setDataProgress(0, 0, 0);
            stepsProgressBar.setEnabled(false);
            caloriesProgressBar.setEnabled(false);
            distanceProgressBar.setEnabled(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getChildId();
    }

    //Formatt the date of dashboard
    public void updateTextDate(Date dateToText) {
        Locale current = getResources().getConfiguration().locale;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd MMMM, yyyy", current);
        String dateFormatted = simpleDateFormat.format(dateToText);
        textDate.setText(dateFormatted);
    }

    //Get the data from server
    public void loadServerData() throws ParseException {

        //Converte a data para o formato que o servidor aceita
        SimpleDateFormat euSdf = new SimpleDateFormat("yyyy-MM-dd");
        String timeInit = euSdf.format(calendar.getTime());

        //Calendario auxiliar criado para pegar a data de amanhã, foi necessário para filtrar a data da requisição
        this.calendarAux.setTime(new SimpleDateFormat("dd / MM / yyyy").parse(this.date));
        this.calendarAux.add(Calendar.DATE, 1);
        String timeEnd = euSdf.format(calendarAux.getTime());

        //Prepara a querry e filtra a data para apenas UM dia, a paginação é 1 pois só é apresentado 1 medição por vez no dashboard
        Historical historical = new Historical.Query()
                .type(HistoricalType.MEASUREMENTS_TYPE_USER)
                .params(params) // Measurements of the temperature type, associated to the user
                .filterDate(timeInit, timeEnd)
                .pagination(0, 1)
                .build();


        historical.request(getContext(), new CallbackHistorical<Measurement>() {
            @Override
            public void onBeforeSend() { }

            @Override
            public void onError(JSONObject result) {

            }

            @Override
            public void onResult(List<Measurement> result) {

                if (result != null && result.size() > 0) {
                    Measurement measurementCurrent = result.get(0);
                    int steps = (int) measurementCurrent.getValue();
                    int distance = (int) measurementCurrent.getMeasurements().get(0).getValue();
                    int calories = (int) measurementCurrent.getMeasurements().get(1).getValue();

                    if (getActivity() == null) return;

                    if (getActivity() != null) {
                        new Handler(getContext().getMainLooper()).postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                setDataProgress(steps, calories, distance);
                            }
                        }, 200);
                    }
                } else {
                    try {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //No caso de não encontrar valores, o dashboard é zerado
                                setDataProgress(0, 0, 0);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onAfterSend() {

                if (getContext() != null) {
                    new Handler(getContext().getMainLooper()).postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            loadingDataProgressBar.setVisibility(View.INVISIBLE);
                        }
                    }, 200);
                }
            }
        });

    }

    //Sum a day
    public Date increaseDay(String date) throws ParseException {

        loadingDataProgressBar.setVisibility(View.VISIBLE);
        //Seta a data informada
        this.calendar.setTime(simpleDateFormat.parse(date));
        //Adiciona 1 dia
        this.calendar.add(Calendar.DATE, 1);
        this.date = simpleDateFormat.format(calendar.getTime());
        if (this.date.equals(this.today)) {
            btnArrowRight.setEnabled(false);
            btnArrowRight.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_right_disabled));
        }

        return calendar.getTime();
    }

    //Decrease a day
    public Date decreaseDay(String date) throws ParseException {

        loadingDataProgressBar.setVisibility(View.VISIBLE);
        btnArrowRight.setEnabled(true);
        btnArrowRight.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_right));
        //Pega a instancia do calendario
        calendar = Calendar.getInstance();
        //Seta a data informada
        calendar.setTime(simpleDateFormat.parse(date));
        //Remove 1 dia
        calendar.add(Calendar.DATE, -1);
        this.date = simpleDateFormat.format(calendar.getTime());

        return calendar.getTime();
    }

    public void initData() {

        measurementTypeArray = getResources().getStringArray(R.array.measurement_types_array);

        if (this.date.equals(this.today)) {
            btnArrowRight.setEnabled(false);
            btnArrowRight.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_right_disabled));
        } else {
            btnArrowRight.setEnabled(true);
        }
        scale = AnimationUtils.loadAnimation(getContext(), R.anim.click);

        dateUtils = new DateUtils();
        calendarAux = Calendar.getInstance();

        //Seta o progresso máximo
        stepsProgressBar.setProgressMax(highProgressBarGoal);
        caloriesProgressBar.setProgressMax(lightProgressBar1Goal);
        distanceProgressBar.setProgressMax(lightProgressBar2Goal);

    }

    //Update the data of progressbar of dashboard
    public void setDataProgress(int numberOfSteps, int numberOfCalories, int distance) {

        stepsProgressBar.setProgressWithAnimation(numberOfSteps, 2500);
        caloriesProgressBar.setProgressWithAnimation(numberOfCalories, 3000);
        distanceProgressBar.setProgressWithAnimation(distance, 3000);

        //Seta os dados nos textos abaixo da progressbar
        textSteps.setText(numberOfSteps + " " + measurementTypeArray[12]);
        textCalories.setText(numberOfCalories + " " + measurementTypeArray[14]);
        if (distance < 1000) {
            textDistance.setText(distance + getResources().getString(R.string.unit_meters));
        } else {
            float distanceKm = distance / 1000;
            textDistance.setText(distanceKm + getResources().getString(R.string.unit_kilometer));
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    //Anims for buttons and textview of date
    public void animLeftBtn(){
        try {
            btnArrowLeft.startAnimation(scale);
            updateTextDate(decreaseDay(this.date));
            loadServerData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void animRightBtn(){
        try {
            btnArrowRight.startAnimation(scale);
            updateTextDate(increaseDay(this.date));
            loadServerData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void animTextDate(){
        textDate.startAnimation(scale);
        try {
            openDatePicker();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            // Button that decrease the days of dashboard
            case R.id.buttonArrowLeft:
                animLeftBtn();
                break;

            // Button that increase the days of dashboard
            case R.id.buttonArrowRight:
                animRightBtn();
                break;
            // TextView of date
            case R.id.textDate:
                animTextDate();
                break;
        }
    }

    /**
     * Open datepicker.
     */
    private void openDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);

        //Essa parte precisa ser assim pois em algumas versões do android ao setar o max date ele
        //buga ao tentar selecionar o ultimo dia
        Calendar maxDate = Calendar.getInstance();
        maxDate.set(Calendar.HOUR_OF_DAY, 23);
        maxDate.set(Calendar.MINUTE, 59);
        maxDate.set(Calendar.SECOND, 59);
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());
        datePickerDialog.show();

        /**
         * Close keyboard
         */
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(textDate.getWindowToken(), 0);
    }

    public String getData() {
        return this.date;
    }

    public String getToday() {
        return this.today;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.calendar.set(year, month, dayOfMonth);
        this.date = simpleDateFormat.format(calendar.getTime());

        updateTextDate(this.calendar.getTime());
        if (!this.date.equals(this.today)) {
            btnArrowRight.setEnabled(true);
            btnArrowRight.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_right));
        } else {
            btnArrowRight.setEnabled(false);
            btnArrowRight.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_right_disabled));
        }
        loadingDataProgressBar.setVisibility(View.VISIBLE);
        try {
            loadServerData();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        /**
         * open keyboard
         */
        ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                .showSoftInput(textDate, InputMethodManager.SHOW_IMPLICIT);
    }

}
