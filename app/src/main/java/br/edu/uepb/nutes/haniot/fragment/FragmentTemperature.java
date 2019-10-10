package br.edu.uepb.nutes.haniot.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.AddMeasurementActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.data.model.model.Measurement;
import br.edu.uepb.nutes.haniot.utils.NumberPickerDialog;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentTemperature extends Fragment implements View.OnClickListener,
        DialogInterface.OnClickListener, AddMeasurementActivity.MeasurementCommunicator {

    @BindView(R.id.botTemperature)
    AppCompatButton botTemperature;
    @BindView(R.id.btnConfirm)
    AppCompatButton btnConfirm;
    @BindView(R.id.btnCancel)
    AppCompatButton btnCancel;
    @BindView(R.id.textChooseTemperature)
    TextView textChooseTemperature;

    private SendMessageListener mListener;
    private NumberPickerDialog numberPickerDialog;
    private Session session;
    private int data = -1;

    public FragmentTemperature() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.botTemperature:
                setupPickers();
                break;
            case R.id.btnConfirm:

                setupListener();
                break;

            case R.id.btnCancel:
                getActivity().finish();
                break;
        }

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        this.data = this.numberPickerDialog.getData().get(0);
        session.putString("lastTemperature", String.valueOf(this.data));
        updateTextTemperature();
        this.botTemperature.setTextColor(Color.BLACK);
        this.textChooseTemperature.setTextColor(Color.BLACK);

    }

    public interface SendMessageListener {
        void onSendMessageTemperature(int temperature);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_temperature_manually,
                container, false);
        ButterKnife.bind(this, view);

        initComponents();

        return view;
    }

    private void initComponents() {

        this.botTemperature.setOnClickListener(this);
        this.btnCancel.setOnClickListener(this);
        this.btnConfirm.setOnClickListener(this);
        this.numberPickerDialog = new NumberPickerDialog(getContext());
        this.session = new Session(getContext());

    }

    private void updateTextTemperature() {
        if (this.data != -1) {
            this.botTemperature.setText(this.data + " " + getResources()
                    .getString(R.string.unit_temperature));
        }
    }

    private void setupListener() {

        if (mListener != null) {
            if (this.data != -1) {
                mListener.onSendMessageTemperature(this.data);
            } else {
                this.botTemperature.setTextColor(getResources().getColor(R.color.colorAlertDanger));
                this.textChooseTemperature.setTextColor(getResources().getColor(R.color.colorAlertDanger));
                mListener.onSendMessageTemperature(-1);
            }
        }

    }

    private void setupPickers() {
        try {
            int lastTemperature = Integer.valueOf(session.getString("lastTemperature"));
            this.numberPickerDialog.setLastMeasurements(new ArrayList<Integer>() {{
                add(lastTemperature);
            }});
        } catch (NumberFormatException e) {
            Log.d("TESTE", e.getMessage());
        }

        this.numberPickerDialog.setDialogTitle(getResources().getString(R.string.choose_temperature));
        this.numberPickerDialog.setBounds(new ArrayList<Pair<Integer, Integer>>() {{
            add(new Pair<>(0, 50));
        }});
        this.numberPickerDialog.create(getContext(), this, 1);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (SendMessageListener) context;
        } catch (ClassCastException e) {
            Log.d("TESTE", e.getMessage() + " must implement messageListener of temperature");
        }

    }

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    @Override
    public Measurement getMeasurement() {
        Measurement measurement = new Measurement();
        measurement.setValue(43.0f);
        measurement.setType("body_temperature");
        return measurement;
    }

    @Override
    public List<Measurement> getMeasurementList() {
        return null;
    }
}
