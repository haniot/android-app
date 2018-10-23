package br.edu.uepb.nutes.haniot.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.utils.Log;
import br.edu.uepb.nutes.haniot.utils.NumberPickerDialog;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddBloodPressureManuallyFragment extends Fragment implements View.OnClickListener,
        DialogInterface.OnClickListener{

    @BindView(R.id.textChooseBloodPressure)
    TextView textPressure;
    @BindView(R.id.botBloodPressure)
    AppCompatButton botPressure;
    @BindView(R.id.textChoosePulse)
    TextView textPulse;
    @BindView(R.id.botPulse)
    AppCompatButton botPulse;
    @BindView(R.id.btnCancel)
    AppCompatButton btnCancel;
    @BindView(R.id.btnConfirm)
    AppCompatButton btnConfirm;

    private Pair<Integer,Integer> pressure;
    private int pulse = -1;

    private SendMessageListener mListener;
    private Session session;
    private NumberPickerDialog numberPickerDialogPressure;
    private NumberPickerDialog numberPickerDialogPulse;

    public AddBloodPressureManuallyFragment() {}

    public static AddBloodPressureManuallyFragment newInstance(String param1, String param2) {
        AddBloodPressureManuallyFragment fragment = new AddBloodPressureManuallyFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        try {
            this.pressure = new Pair<Integer, Integer>(this.numberPickerDialogPressure.getData().get(0),
                    this.numberPickerDialogPressure.getData().get(1));
            this.botPressure.setTextColor(Color.BLACK );
            this.textPressure.setTextColor(Color.BLACK );
            session.putString("lastPressure1",String.valueOf(this.pressure.first));
            session.putString("lastPressure2",String.valueOf(this.pressure.second));
            updateTextPressure();
        }catch (NullPointerException e){
            Log.d("TESTE",e.getMessage());
        }
        try{
            this.pulse = this.numberPickerDialogPulse.getData().get(0);
            this.textPulse.setTextColor(Color.BLACK );
            this.botPulse.setTextColor(Color.BLACK );
            updateTextPulse();
            session.putString("lastPulse",String.valueOf(this.pulse));
        }catch (NullPointerException e){
            this.pulse = -1;
            Log.d("TESTE",e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.botBloodPressure:
                setupPickerPressure();
                break;

            case R.id.botPulse:
                setupPickerPulse();
                break;

            case R.id.btnConfirm:

                setupListener();
                break;

            case R.id.btnCancel:
                getActivity().finish();
                break;
        }
    }

    private void setupListener(){

        if (mListener != null){
            if (this.pressure != null && this.pulse != -1){
                mListener.onSendMessagePressure(this.pressure,this.pulse);
                return;
            }
            if (this.pulse == -1){
                this.textPulse.setTextColor(getResources().getColor(R.color.colorAlertDanger));
                this.botPulse.setTextColor(getResources().getColor(R.color.colorAlertDanger));
                mListener.onSendMessagePressure(this.pressure,this.pulse);
            }
            if (this.pressure == null){
                this.textPressure.setTextColor(getResources().getColor(R.color.colorAlertDanger));
                this.botPressure.setTextColor(getResources().getColor(R.color.colorAlertDanger));
                mListener.onSendMessagePressure(this.pressure,this.pulse);
            }
        }

    }

    public interface SendMessageListener{
        void onSendMessagePressure(Pair<Integer,Integer> pressure, int pulse);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_blood_pressure_manually,
                container, false);
        ButterKnife.bind(this,view);

        initComponents();

        return view;
    }

    private void initComponents(){

        this.session = new Session(getContext());
        this.btnCancel.setOnClickListener(this);
        this.btnConfirm.setOnClickListener(this);
        this.botPressure.setOnClickListener(this);
        this.botPulse.setOnClickListener(this);

        this.numberPickerDialogPressure = new NumberPickerDialog(getContext());
        this.numberPickerDialogPulse = new NumberPickerDialog(getContext());
    }

    private void setupPickerPressure(){

        try{
            Pair<Integer,Integer> lastPressure = new Pair<>(Integer.valueOf(session.getString("lastPressure1")),
                    Integer.valueOf(session.getString("lastPressure2")));
            this.numberPickerDialogPressure.setLastMeasurements(new ArrayList<Integer>(){{
                add(lastPressure.first);
                add(lastPressure.second);
            }});
        }catch (Exception e){
            Log.d("TESTE",e.getMessage());
        }

        this.numberPickerDialogPressure.setDialogTitle(getResources()
                .getString(R.string.choose_blood_pressure)+" "+
                getResources().getString(R.string.unit_glucose_mg_dL));
        this.numberPickerDialogPressure.create(getContext(),this,2);
    }

    private void setupPickerPulse(){

        try{
            int lastPulse = Integer.valueOf(session.getString("lastPulse"));
            this.numberPickerDialogPulse.setLastMeasurements(new ArrayList<Integer>(){{
                add(lastPulse);
            }});
        }catch (Exception e){
            Log.d("TESTE",e.getMessage());
        }

        this.numberPickerDialogPulse.setDialogTitle(getResources().getString(R.string.choose_pulse));
        this.numberPickerDialogPulse.create(getContext(),this,1);
    }

    private void updateTextPressure(){
        this.botPressure.setText(this.pressure.first+"/"+this.pressure.second+" "+
            getResources().getString(R.string.unit_pressure));
    }

    private void updateTextPulse(){
        this.botPulse.setText(this.pulse+" "+getResources().getString(R.string.unit_min));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (SendMessageListener)context;
        }catch (ClassCastException e){
            Log.d("TESTE",e.getMessage()+" must implement sendMessageListener on Pressure");
        }
    }

    public Pair<Integer, Integer> getPressure() {
        return pressure;
    }

    public void setPressure(Pair<Integer, Integer> pressure) {
        this.pressure = pressure;
    }

    public int getPulse() {
        return pulse;
    }

    public void setPulse(int pulse) {
        this.pulse = pulse;
    }
}
