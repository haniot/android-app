package br.edu.uepb.nutes.haniot.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.ContextMeasurementValueType;
import br.edu.uepb.nutes.haniot.utils.Log;
import br.edu.uepb.nutes.haniot.utils.NumberPickerDialog;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddBloodGlucoseManuallyFragment extends Fragment implements View.OnClickListener,
        DialogInterface.OnClickListener{

    @BindView(R.id.btnCancel)
    AppCompatButton btnCancel;
    @BindView(R.id.btnConfirm)
    AppCompatButton btnConfirm;
    @BindView(R.id.botGlucose)
    AppCompatButton botGlucose;
    @BindView(R.id.radioGlucoseStatus)
    RadioGroup radioGroup;
    @BindView(R.id.radioFasting)
    RadioButton radioFasting;
    @BindView(R.id.radioCasual)
    RadioButton radioCasual;
    @BindView(R.id.radioBedtime)
    RadioButton radioBedtime;
    @BindView(R.id.radioPrepandial)
    RadioButton radioPrepandial;
    @BindView(R.id.radioPostprandial)
    RadioButton radioPostprandial;
    @BindView(R.id.textChooseGlucose)
    TextView textChooseGlucose;
    @BindView(R.id.textStatus)
    TextView textChooseStatus;

    private SendMessageListener mListener;

    private NumberPickerDialog numberPicker;
    private Session session;
    private int glucoseValue = -1;
    private int type = -1;

    public AddBloodGlucoseManuallyFragment() {}

    @Override
    public void onClick(DialogInterface dialog, int which) {
        this.glucoseValue = this.numberPicker.getData().get(0);
        if (getGlucoseValue() != -1){
            this.textChooseGlucose.setTextColor(Color.BLACK);
            this.botGlucose.setTextColor(Color.BLACK);
            updateTextGlucose();
            session.putString("lastGlucose",String.valueOf(this.glucoseValue));
        }

    }

    public interface SendMessageListener{
        void onSendMessageGlucose(int glucoseValue, int type);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_blood_glucose_manually,
                container, false);
        ButterKnife.bind(this,view);

        initComponents();

        return view;
    }

    private void updateTextGlucose(){
        this.botGlucose.setText(this.glucoseValue+" "+getResources()
                    .getString(R.string.unit_glucose_mg_dL));
    }

    private void initComponents(){

        botGlucose.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);

        this.numberPicker = new NumberPickerDialog(getContext());
        this.session = new Session(getContext());
        this.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {

            switch (checkedId){
                case R.id.radioFasting:
                    this.type = ContextMeasurementValueType.GLUCOSE_MEAL_FASTING;
                    break;
                case R.id.radioCasual:
                    this.type = ContextMeasurementValueType.GLUCOSE_MEAL_CASUAL;
                    break;
                case R.id.radioBedtime:
                    this.type = ContextMeasurementValueType.GLUCOSE_MEAL_BEDTIME;
                    break;
                case R.id.radioPrepandial:
                    this.type = ContextMeasurementValueType.GLUCOSE_MEAL_PREPRANDIAL;
                    break;
                case R.id.radioPostprandial:
                    this.type = ContextMeasurementValueType.GLUCOSE_MEAL_POSTPRANDIAL;
                    break;
                default:
                    this.type = -1;
            }
            this.textChooseStatus.setTextColor(Color.BLACK);
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (SendMessageListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString() +
                    "must implement Glucose SendMessageListener");
        }

    }

    private void setupPicker(){

        try{
            int lastGlucose = Integer.valueOf(session.getString("lastGlucose"));
            this.numberPicker.setLastMeasurements(new ArrayList<Integer>(){{
                add(lastGlucose);
            }});
        }catch (Exception e){
            Log.d("TESTE",e.getMessage());
        }

        this.numberPicker.setDialogIcon(R.drawable.ic_blood_glucose);
        this.numberPicker.setDialogTitle(getResources().getString(R.string.choose_glucose));
        this.numberPicker.setBounds(new ArrayList<Pair<Integer, Integer>>(){{
            add(new Pair<>(1,720));
        }});
        this.numberPicker.setStartValue(100);
        this.numberPicker.setOrientation(LinearLayout.VERTICAL);
        this.numberPicker.create(getContext(), this, 1);

    }

    private void setupListener(){

        if (mListener != null){
            if (this.glucoseValue != -1 && type != -1){
                mListener.onSendMessageGlucose(this.glucoseValue, this.type);
                return;
            }else if (this.glucoseValue == -1){
                this.botGlucose.setTextColor(getResources()
                        .getColor(R.color.colorAlertDanger));
                this.textChooseGlucose.setTextColor(getResources()
                        .getColor(R.color.colorAlertDanger));
            }
            if (this.type == -1){
                this.textChooseStatus.setTextColor(getResources()
                        .getColor(R.color.colorAlertDanger));
            }
            mListener.onSendMessageGlucose(-1, -1);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.botGlucose:
                setupPicker();
                break;

            case R.id.btnCancel:
                getActivity().finish();
                break;

            case R.id.btnConfirm:
                setupListener();
                break;
        }

    }

    public SendMessageListener getmListener() {
        return mListener;
    }

    public void setmListener(SendMessageListener mListener) {
        this.mListener = mListener;
    }

    public int getGlucoseValue() {
        return glucoseValue;
    }

    public void setGlucoseValue(int glucoseValue) {
        this.glucoseValue = glucoseValue;
    }
}
