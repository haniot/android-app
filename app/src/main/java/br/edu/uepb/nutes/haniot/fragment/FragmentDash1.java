package br.edu.uepb.nutes.haniot.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.account.SignupActivity;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentDash1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentDash1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDash1 extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

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

    private int      stepsGoal = 200;
    private int   caloriesGoal = 300;
    private int     distanceGoal = 800;

    //Date part
    private Calendar                   calendar;
    private Calendar                calendarAux;
    private SimpleDateFormat   simpleDateFormat;
    private String                         date;
    private String                        today;
    private Animation                     scale;

    private DatePickerDialog datePickerDialog;
    private int  year;
    private int month;
    private int   day;

    //Server part
    private Params   params;
    private Session session;

    public FragmentDash1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentDash1.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentDash1 newInstance(String param1, String param2) {
        FragmentDash1 fragment = new FragmentDash1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        //Inicia a sessão com o ID logado e pega as medições do tipo STEPS
        session = new Session(getContext());
        params = new Params(session.get_idLogged(), MeasurementType.STEPS);

        //Formata a data para o dia / mes / ano
        simpleDateFormat = new SimpleDateFormat("dd / MM / yyyy");
        calendar = Calendar.getInstance();

        //Usado para quando virar a tela manter a data
        if (savedInstanceState != null) {
            this.date = savedInstanceState.getString("date");
        }else{
            this.date = simpleDateFormat.format(calendar.getTime());
        }

    }

    //Método usado para quando rotacionar/rodar/girar a tela
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putString("date",this.date);
        outState.putBoolean("change",true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_dash1, container, false);
        ButterKnife.bind(this,view);

        //Data é atual salva quando abre o aplicativo
        today = simpleDateFormat.format(calendar.getTime());
        initData();


        try {
            this.calendar.setTime(simpleDateFormat.parse(this.date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        updateTextDate(calendar.getTime());

        this.year  = this.calendar.get(Calendar.YEAR);
        this.month = this.calendar.get(Calendar.MONTH);
        this.day   = this.calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(getContext(), this, year, month, day);

        btnArrowLeft.setOnClickListener(this);
        btnArrowRight.setOnClickListener(this);
        textDate.setOnClickListener(this);

        try {
            loadServerData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return view;
    }

    //Formata a data para aparecer o nome por extenso, dia, nome do mês e ano; Seta a data no textview
    public void updateTextDate(Date dateToText){
        String formattedDate = new SimpleDateFormat("EEEE, dd MMMM, yyyy",Locale.US).format(dateToText);
        textDate.setText(formattedDate);
    }

    //Carrega os dados do servidor
    public void loadServerData() throws ParseException {

        //Converte a data para o formato que o servidor aceita
        SimpleDateFormat euSdf = new SimpleDateFormat("yyyy-MM-dd");
        String timeInit  = euSdf.format(calendar.getTime());

        //Calendario auxiliar criado para pegar a data de amanhã, foi necessário para filtrar a data da requisição
        this.calendarAux.setTime(new SimpleDateFormat("dd / MM / yyyy").parse(this.date));
        this.calendarAux.add(Calendar.DATE,1);
        String timeEnd  = euSdf.format(calendarAux.getTime());

        //Prepara a querry e filtra a data para apenas UM dia, a paginação é 1 pois só é apresentado 1 medição por vez no dashboard
        Historical historical = new Historical.Query()
                .type(HistoricalType.MEASUREMENTS_TYPE_USER)
                .params(params) // Measurements of the temperature type, associated to the user
                .filterDate(timeInit,timeEnd)
                .pagination(0, 1)
                .build();


        historical.request(getContext(), new CallbackHistorical<Measurement>() {
            @Override
            public void onBeforeSend() {


            }

            @Override
            public void onError(JSONObject result) {
                System.out.println("==Error on request of data of progress bar on dashboard");
                new Handler(getContext().getMainLooper()).postDelayed(new Runnable(){

                    @Override
                    public void run() {
                        Snackbar.make(getActivity().findViewById(android.R.id.content), "Algo deu errado, por favor tente mais tarde", Snackbar.LENGTH_LONG)
                                .show();
                    }
                },200);

            }

            @Override
            public void onResult(List<Measurement> result) {

                if(result != null && result.size() > 0 ){
                    Measurement measurementCurrent =result.get(0);
                    int steps = (int) measurementCurrent.getValue();
                    int distance = (int) measurementCurrent.getMeasurements().get(0).getValue();
                    int calories = (int) measurementCurrent.getMeasurements().get(1).getValue();

                    if(getActivity() == null)return;

                    if (getActivity() != null) {
                        new Handler(getContext().getMainLooper()).postDelayed(new Runnable(){

                            @Override
                            public void run() {
                                setDataProgress(steps, calories, distance);
                            }
                        },200);
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//
//                            }
//                        });
                    }}else{
                        try {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //No caso de não encontrar valores, o dashboard é zerado
                                    setDataProgress(0, 0, 0);
                                }
                            });
                        }catch (Exception e){
                            System.out.println("Erro ==");
                            System.out.println(e.getMessage());
                        }
                    }
            }

            @Override
            public void onAfterSend() {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        loadingDataProgressBar.setVisibility(View.INVISIBLE);
//                    }
//                });
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

    //Adiciona um dia na data selecionada
    public Date increaseDay(String date) throws ParseException {

        loadingDataProgressBar.setVisibility(View.VISIBLE);
        //Seta a data informada
        this.calendar.setTime(simpleDateFormat.parse(date));
        //Adiciona 1 dia
        this.calendar.add(Calendar.DATE,1);
        this.date = simpleDateFormat.format(calendar.getTime());
        if (this.date.equals(this.today)){
            btnArrowRight.setEnabled(false);
            btnArrowRight.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_right_disabled));
        }

        return calendar.getTime();
    }

    //Remove um dia na data atual
    public Date decreaseDay(String date) throws ParseException {

        loadingDataProgressBar.setVisibility(View.VISIBLE);
        btnArrowRight.setEnabled(true);
        btnArrowRight.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_right));
        //Pega a instancia do calendario
        calendar = Calendar.getInstance();
        //Seta a data informada
        calendar.setTime(simpleDateFormat.parse(date));
        //Remove 1 dia
        calendar.add(Calendar.DATE,-1);
        this.date = simpleDateFormat.format(calendar.getTime());

        return calendar.getTime();
    }

    public void initData(){
        if (this.date.equals(this.today)){
            btnArrowRight.setEnabled(false);
            btnArrowRight.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_right_disabled));
        }else{
            btnArrowRight.setEnabled(true);
        }
        scale = AnimationUtils.loadAnimation(getContext(), R.anim.click);

        calendarAux = Calendar.getInstance();

        //Seta o progresso máximo
        stepsProgressBar.setProgressMax(stepsGoal);
        caloriesProgressBar.setProgressMax(caloriesGoal);
        distanceProgressBar.setProgressMax(distanceGoal);

    }

    //Atualiza as circular progress bars do dashboard
    public void setDataProgress (int numberOfSteps, int numberOfCalories, int distance){

        stepsProgressBar.setProgressWithAnimation(numberOfSteps,2500);
        caloriesProgressBar.setProgressWithAnimation(numberOfCalories,3000);
        distanceProgressBar.setProgressWithAnimation(distance,3000);
//        loadingDataProgressBar.setVisibility(View.VISIBLE);

        //Seta os dados nos textos abaixo da progressbar
        textSteps.setText(numberOfSteps+" steps");
        textCalories.setText(numberOfCalories+" calories");
        if (distance < 1000){
            textDistance.setText(distance+" m");
        }
        else{
            float distanceKm = distance/1000;
            textDistance.setText(distanceKm+ " KM");
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            //Botão que volta os dias
            case R.id.buttonArrowLeft:
                try {
                    btnArrowLeft.startAnimation(scale);
                    updateTextDate(decreaseDay(this.date));
                    loadServerData();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

            // Botão que acrescenta os dias
            case R.id.buttonArrowRight:
                try {
                    btnArrowRight.startAnimation(scale);
                    updateTextDate(increaseDay(this.date));
                    loadServerData();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.textDate:
                textDate.startAnimation(scale);
                try {
                    openDatePicker();
                } catch (Exception e) {
                    System.out.println("Exeption in openDatePicker(): "+e.getMessage());
                    e.printStackTrace();
                }
                break;

        }
    }

    /**
     * Open datepicker.
     */
    private void openDatePicker(){
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

    public String getData(){
        return this.date;
    }

    public String getToday(){
        return this.today;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        this.calendar.set(year, month, dayOfMonth);
        this.date = simpleDateFormat.format(calendar.getTime());

        updateTextDate(this.calendar.getTime());
        if (!this.date.equals(this.today)){
            btnArrowRight.setEnabled(true);
            btnArrowRight.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_right));
        }else{
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
