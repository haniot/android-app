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
import android.widget.TextView;

import java.util.ArrayList;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.ManuallyAddMeasurement;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.MeasurementType;
import br.edu.uepb.nutes.haniot.server.SynchronizationServer;
import br.edu.uepb.nutes.haniot.utils.Log;
import br.edu.uepb.nutes.haniot.utils.NumberPickerDialog;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddHeartRateManuallyFragment extends Fragment implements View.OnClickListener,
        DialogInterface.OnClickListener{

    @BindView(R.id.btnCancel)
    AppCompatButton btnCancel;
    @BindView(R.id.btnConfirm)
    AppCompatButton btnConfirm;
    @BindView(R.id.botHeartRate)
    AppCompatButton botHeartRate;
    @BindView(R.id.textChooseHeartRate)
    TextView textChooseHeartRate;

    private NumberPickerDialog numberPickerDialog;
    private int heartBeat = -1;
    private SendMessageListener mListener;
    private Session session;
    private String date;

    public AddHeartRateManuallyFragment() {}

    @Override
    public void onClick(DialogInterface dialog, int which) {

        this.heartBeat = this.numberPickerDialog.getData().get(0);

        if (this.heartBeat != -1){
            this.textChooseHeartRate.setTextColor(Color.BLACK);
            this.botHeartRate.setTextColor(Color.BLACK);
            updateTextHeartRate();
            session.putString("lastHeartRate",String.valueOf(this.heartBeat));

        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btnCancel:
                getActivity().finish();
                break;

            case R.id.btnConfirm:
                setupListener();
                break;

            case R.id.botHeartRate:
                setupPicker();
                break;
        }

    }

    private void setupListener(){

        if (mListener != null){
            if (this.heartBeat == -1){
                this.textChooseHeartRate.setTextColor(getResources().getColor(R.color.colorAlertDanger));
                this.botHeartRate.setTextColor(getResources().getColor(R.color.colorAlertDanger));
            }
            mListener.onSendMessageHeartRate(this.heartBeat);
        }

    }

    private void setupPicker(){

        try{
            int lastHeartBeat = Integer.valueOf(session.getString("lastHeartRate"));
            this.numberPickerDialog.setLastMeasurements(new ArrayList<Integer>(){{
                add(lastHeartBeat);
            }});
        }catch (Exception e){
            Log.d("TESTE",e.getMessage());
        }

        this.numberPickerDialog.setDialogIcon(R.drawable.ic_heart_rate_64);
        this.numberPickerDialog.setDialogTitle(getResources().getString(R.string.choose_heart_rate));
        this.numberPickerDialog.setBounds(new ArrayList<Pair<Integer, Integer>>(){{
            add(new Pair<>(0,250));
//            perguntar depois o valor
        }});
        this.numberPickerDialog.setStartValue(100);
        this.numberPickerDialog.setOrientation(LinearLayout.VERTICAL);
        this.numberPickerDialog.create(getContext(), this, 1);

    }

    public interface SendMessageListener{
        void onSendMessageHeartRate(int heartBeat);
    }

    private void updateTextHeartRate(){
        this.botHeartRate.setText(this.heartBeat + " " +
            getResources().getString(R.string.unit_heart_rate));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_heart_rate_manually,
                container, false);
        ButterKnife.bind(this,view);

        initComponents();

        return view;
    }

    private void initComponents(){

        this.botHeartRate.setOnClickListener(this);
        this.btnCancel.setOnClickListener(this);
        this.btnConfirm.setOnClickListener(this);
        this.numberPickerDialog = new NumberPickerDialog(getContext());
        this.session = new Session(getContext());

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mListener = (SendMessageListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString() +
                    "must implement HeartBeat MessageListener");
        }

    }

    public int getHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(int heartBeat) {
        this.heartBeat = heartBeat;
    }

    public NumberPickerDialog getNumberPickerDialog() {
        return numberPickerDialog;
    }

    public void setNumberPickerDialog(NumberPickerDialog numberPickerDialog) {
        this.numberPickerDialog = numberPickerDialog;
    }

}
