package br.edu.uepb.nutes.haniot.fragment;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.MeasurementType;
import br.edu.uepb.nutes.haniot.server.historical.CallbackHistorical;
import br.edu.uepb.nutes.haniot.server.historical.Historical;
import br.edu.uepb.nutes.haniot.server.historical.HistoricalType;
import br.edu.uepb.nutes.haniot.server.historical.Params;
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
public class FragmentDash1 extends Fragment implements View.OnClickListener {
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

    private int    numberOfSteps = 0;
    private int numberOfCalories = 0;
    private float       distance = 0;
    private int      stepsGoal = 200;
    private int   caloriesGoal = 300;
    private int     distanceGoal = 4;

    //Date part
    private Calendar                   calendar;
    private SimpleDateFormat   simpleDateFormat;
    private String                    date = "";
    private boolean changeDateFirstTime = false;
    private String                        today;
    private Animation                     scale;

    //Server part
    private Params params;
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

        session = new Session(getContext());
        params = new Params(session.get_idLogged(), MeasurementType.STEPS);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_dash1, container, false);
        ButterKnife.bind(this,view);
        initData();
        today = simpleDateFormat.format(calendar.getTime());

        updateTextDate(calendar.getTime());

        btnArrowLeft.setOnClickListener(this);
        btnArrowRight.setOnClickListener(this);

        return view;
    }

    public void updateTextDate(Date dateToText){
        String formattedDate = new SimpleDateFormat("EEEE / dd / yyyy",Locale.US).format(dateToText);
        textDate.setText(formattedDate);
    }

    public void loadServerData() throws ParseException {

        String formattedDate = this.date;
        formattedDate = formattedDate.replace(" ","");
        formattedDate = formattedDate.replace("/","-");
        SimpleDateFormat euSpn = new SimpleDateFormat("yyyy-MM-dd");
        String t  = euSpn.format(calendar.getTime());

        Historical historical = new Historical.Query()
                .type(HistoricalType.MEASUREMENTS_TYPE_USER)
                .params(params) // Measurements of the temperature type, associated to the user
                .pagination(0, 1)
                .filterDate(t,t)
                .build();

        historical.request(getContext(), new CallbackHistorical() {
            @Override
            public void onBeforeSend() {
                System.out.println("Loading data");
            }

            @Override
            public void onError(JSONObject result) {
                System.out.println("Error on request of data of progress bar on dashboard");
            }

            @Override
            public void onResult(List result) {

                System.out.println("Response find!");
                if(result != null && result.size() > 0 ){
                    List a = new ArrayList();
                    a = result;
                    System.out.println(a.get(0));
                }

            }

            @Override
            public void onAfterSend() {

            }
        });

    }

    public Date increaseDay(String date) throws ParseException {

        //Seta a data informada
        this.calendar.setTime(simpleDateFormat.parse(date));
        //Adiciona 1 dia
        this.calendar.add(Calendar.DATE,1);
        this.date = simpleDateFormat.format(calendar.getTime());
        System.out.println("========== Date: "+this.date);
        System.out.println("========== Today: "+this.today);
        if (this.date.equals(this.today)){
            btnArrowRight.setEnabled(false);
            btnArrowRight.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_right_disabled));
        }

        return calendar.getTime();
    }

    public Date decreaseDay(String date) throws ParseException {
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
        btnArrowRight.setEnabled(false);
        scale = AnimationUtils.loadAnimation(getContext(), R.anim.click);
        btnArrowRight.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_right_disabled));
        btnArrowLeft.setBackground(getResources().getDrawable(R.mipmap.ic_arrow_left));
        simpleDateFormat = new SimpleDateFormat("dd / MM / yyyy");
        calendar = Calendar.getInstance();
        this.date = simpleDateFormat.format(calendar.getTime());

        //Quantidade de passos, calorias e distancia; Estes dados devem vim do servidor
        this.numberOfSteps = 70;
        this.numberOfCalories = 120;
        this.distance = 2.8f;

        //Seta os dados nos textos abaixo da progressbar
        textSteps.setText(numberOfSteps+" steps");
        textCalories.setText(numberOfCalories+" calories");
        textDistance.setText(distance+" KM");

        //Seta o progresso máximo
        stepsProgressBar.setProgressMax(stepsGoal);
        caloriesProgressBar.setProgressMax(caloriesGoal);
        distanceProgressBar.setProgressMax(distanceGoal);

        setDataProgress(this.numberOfSteps,this.numberOfCalories,this.distance);

        stepsProgressBar.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        stepsProgressBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAlertDanger));
        caloriesProgressBar.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        caloriesProgressBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorGrey));
        distanceProgressBar.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        distanceProgressBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAlertDanger));

    }

    public void setDataProgress (int numberOfSteps, int numberOfCalories, float distance){

        stepsProgressBar.setProgressWithAnimation(numberOfSteps,2500);
        caloriesProgressBar.setProgressWithAnimation(numberOfCalories,3500);
        distanceProgressBar.setProgressWithAnimation(distance,3500);

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
                    changeDateFirstTime = true;
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
                    changeDateFirstTime = true;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

        }
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
