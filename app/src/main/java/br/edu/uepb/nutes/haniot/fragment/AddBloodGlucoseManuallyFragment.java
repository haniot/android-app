package br.edu.uepb.nutes.haniot.fragment;

import android.content.Context;
import android.content.DialogInterface;
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

    private SendMessageListener mListener;

    private NumberPickerDialog numberPicker;
    private Session session;
    private int glucoseValue;
    private int type = -1;

    public AddBloodGlucoseManuallyFragment() {}

    @Override
    public void onClick(DialogInterface dialog, int which) {
        this.glucoseValue = this.numberPicker.getData().get(0);
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

    private void initComponents(){

        botGlucose.setOnClickListener(this);

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
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (SendMessageListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString() +
                    "must implement SendMessageListener");
        }

    }

    private void setupPicker(){

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
                Log.d("TESTE","deu certo");
                return;
            }else if (this.glucoseValue == -1){
                this.botGlucose.setTextColor(getResources().getColor(R.color.colorAlertDanger));
                Log.d("TESTE","falta glucose");
            }else if (this.type == -1){
                this.radioGroup.setBackgroundColor(getResources().getColor(R.color.colorAlertDanger));
                Log.d("TESTE","falta status");
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
                Log.d("TESTE","click confirm");
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
