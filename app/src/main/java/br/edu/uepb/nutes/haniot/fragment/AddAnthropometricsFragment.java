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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.SplittableRandom;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.utils.Log;
import br.edu.uepb.nutes.haniot.utils.NumberPickerDialog;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AddAnthropometricsFragment extends Fragment implements View.OnClickListener,
        DialogInterface.OnClickListener {

    @BindView(R.id.textChooseHeight)
    TextView textHeight;
    @BindView(R.id.botHeight)
    AppCompatButton botHeight;
    @BindView(R.id.textChooseCircumference)
    TextView textCircumference;
    @BindView(R.id.botCircumference)
    AppCompatButton botCircumference;
    @BindView(R.id.btnCancel)
    AppCompatButton btnCancel;
    @BindView(R.id.btnConfirm)
    AppCompatButton btnConfirm;

    private double height = -1;
    private double circumference = -1;

    private SendMessageListener mListener;
    private Session session;
    private NumberPickerDialog pickerHeight;
    private NumberPickerDialog pickerCircumference;

    public AddAnthropometricsFragment() {}

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {

        try{

            String height = String.valueOf(this.pickerHeight.getData().get(0))+"."+String.valueOf(
                    this.pickerHeight.getData().get(1));
            setHeight(Double.valueOf(height));
            this.textHeight.setTextColor(Color.BLACK );
            this.botHeight.setTextColor(Color.BLACK );
            session.putString("lastHeight1",String.valueOf(this.pickerHeight.getData().get(0)));
            session.putString("lastHeight2",String.valueOf(this.pickerHeight.getData().get(1)));
            updateTextHeight();
        }catch (Exception e){
            Log.d("TESTE",e.getMessage());
        }

        try{

            String circumference = String.valueOf(this.pickerCircumference.getData().get(0))+"."+
                    String.valueOf(this.pickerCircumference.getData().get(1));
            setCircumference(Double.valueOf(circumference));
            this.textCircumference.setTextColor(Color.BLACK);
            this.botCircumference.setTextColor(Color.BLACK );
            session.putString("lastCircumference1",String.valueOf(
                    this.pickerCircumference.getData().get(0)));
            session.putString("lastCircumference2",String.valueOf(
                    this.pickerCircumference.getData().get(1)));
            updateTextCircumference();
        }catch (Exception e){
            Log.d("TESTE",e.getMessage());
        }

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.botHeight:
                setupPickerHeight();
                break;
            case R.id.botCircumference:
                setupPickerCircumference();
                break;
            case R.id.btnConfirm:
                setupListener();
                break;
            case R.id.btnCancel:
                getActivity().finish();
                break;
        }

    }

    public interface SendMessageListener{
        void onSendMessageAnthropometric(double height, double circumference);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_anthropometrics,
                container, false);
        ButterKnife.bind(this,view);
        initComponents();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (SendMessageListener)context;
        }catch (ClassCastException e){
            Log.d("TESTE",e.getMessage()+" must implement SendMessageListener " +
                    "of anthropometrics fragment");
        }
    }

    private void initComponents(){

        this.session = new Session(getContext());
        botHeight.setOnClickListener(this);
        botCircumference.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        pickerHeight = new NumberPickerDialog(getContext());
        pickerCircumference = new NumberPickerDialog(getContext());

    }

    private void setupPickerHeight(){

        try {
            Pair<Integer, Integer> height = new Pair<>(Integer.valueOf(session
                    .getString("lastHeight1")), Integer.valueOf(session
                    .getString("lastHeight2")));

            this.pickerHeight.setLastMeasurements(new ArrayList<Integer>() {{
                add(height.first);
                add(height.second);
            }});
        }catch (Exception e){
            Log.d("TESTE","Exeção em setupPickerHeight "+e.getMessage());
        }
        this.pickerHeight.setDialogTitle(getResources().getString(R.string.choose_height));
        this.pickerHeight.setDialogIcon(R.drawable.ic_ruler_64);
        this.pickerHeight.setBounds(new ArrayList<Pair<Integer, Integer>>(){{
            add(new Pair<>(0,2));
            add(new Pair<>(0,99));
        }});
        this.pickerHeight.create(getContext(),this,2);

    }

    private void setupPickerCircumference(){

        try{

            Pair<Integer,Integer> circumference = new Pair<>(Integer.valueOf(session
                .getString("lastCircumference1")),Integer.valueOf(session
                .getString("lastCircumference2")));
            this.pickerCircumference.setLastMeasurements(new ArrayList<Integer>(){{
                add(circumference.first);
                add(circumference.second);
            }});
        }catch (Exception e){
            Log.d("TESTE",e.getMessage());
        }
        this.pickerCircumference.setDialogTitle(getResources()
                .getString(R.string.choose_circumference));
        this.pickerCircumference.setDialogIcon(R.drawable.ic_ruler_64);
        this.pickerCircumference.setBounds(new ArrayList<Pair<Integer,Integer>>(){{
            add(new Pair<>(0,2));
            add(new Pair<>(0,99));
        }});
        this.pickerCircumference.create(getContext(),this,2);

    }

    private void setupListener(){

        if (mListener != null){

            if (this.height != -1 && this.circumference != -1){
                mListener.onSendMessageAnthropometric(this.height,this.circumference);
            }
            if (this.height == -1){
                this.textHeight.setTextColor(getResources().getColor(R.color.colorAlertDanger));
                this.botHeight.setTextColor(getResources()
                        .getColor(R.color.colorAlertDanger));
                mListener.onSendMessageAnthropometric(this.height,this.circumference);
            }
            if (this.circumference == -1){
                this.textCircumference.setTextColor(getResources()
                        .getColor(R.color.colorAlertDanger));
                this.botCircumference.setTextColor(getResources()
                        .getColor(R.color.colorAlertDanger));
                mListener.onSendMessageAnthropometric(this.height,this.circumference);
            }

        }

    }

    private void updateTextHeight(){
        this.botHeight.setText(String.valueOf(getHeight())+" "+getResources()
                .getString(R.string.unit_meters));
    }

    private void updateTextCircumference(){
        this.botCircumference.setText(String.valueOf(getCircumference())+" "+getResources()
            .getString(R.string.unit_meters));
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getCircumference() {
        return circumference;
    }

    public void setCircumference(double circumference) {
        this.circumference = circumference;
    }
}
