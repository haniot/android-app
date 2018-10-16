package br.edu.uepb.nutes.haniot.fragment;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import com.shawnlin.numberpicker.NumberPicker;
import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.utils.Log;
import br.edu.uepb.nutes.haniot.utils.NumberPickerDialog;
import butterknife.BindView;
import butterknife.ButterKnife;


public class AddWeightManuallyFragment extends Fragment implements View.OnClickListener,
        DialogInterface.OnClickListener{

    @BindView(R.id.botWeight)
    AppCompatButton botWeight;

//    NumberPicker things
    private LayoutInflater li;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    private ArrayList<Integer> data;
    private Session session;

    private NumberPickerDialog numberPicker;
    private final int NUMBEROFPICKERS = 2;

    // interface instance on sender fragment
    private SendMessageListener command;

    public AddWeightManuallyFragment() { }

    //Comunication interface with another fragments and activities
    public interface SendMessageListener {
        void onSendMessage(Pair<String,String> data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_weight_manually, container,
                false);
        ButterKnife.bind(this, view);

        numberPicker = new NumberPickerDialog(getContext());
        session = new Session(getContext());

        botWeight.setOnClickListener(this);
        botWeight.setText(getResources().getString(R.string.unit_kg));

        AppCompatButton confirm = (AppCompatButton)view.findViewById(R.id.btnConfirm);
        confirm.setOnClickListener(this);

        return view;
    }

    public void openNumberPicker(){

//        here the view is inflated
        this.li = LayoutInflater.from(getContext());
        View view = li.inflate(R.layout.number_picker_dialog, null);

//        the builder is created and view is setted up to this builder
        this.builder = new AlertDialog.Builder(getContext());
        this.builder.setView(view);
        this.builder.setTitle(getResources().getString(R.string.choose_weight));
        this.builder.setIcon(getResources().getDrawable(R.drawable.ic_balance_128));

//        get the refference of items of view
        TextView textPounds = (TextView)view.findViewById(R.id.textPounds);
        NumberPicker pickerPounds = (NumberPicker)view.findViewById(R.id.number_picker_pounds);
        NumberPicker pickerGrams = (NumberPicker)view.findViewById(R.id.number_picker_grams);
        pickerPounds.setFadingEdgeEnabled(true);
        pickerPounds.setWrapSelectorWheel(true);

        LinearLayout ll = (LinearLayout)view.findViewById(R.id.test);
        ll.setOrientation(LinearLayout.VERTICAL);

        textPounds.setOnClickListener(c ->{
//            InputMethodManager imm=(InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showSoftInput(textPounds, InputMethodManager.SHOW_IMPLICIT);
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .showSoftInput(textPounds,0);
//            pickerPounds.setValue(1);
        });
//        setting the listeners and basic configs
        builder.setCancelable(false)
                .setNegativeButton(getResources().getString(R.string.bt_cancel), (v,d) ->{
                    this.alertDialog.cancel();
                })
                .setPositiveButton(getResources().getString(R.string.bt_ok), (v,d) ->{
                    Toast.makeText(getContext(),"Number: "+pickerPounds.getValue()+"\n"+
                            pickerGrams.getValue(),Toast.LENGTH_SHORT).show();
                });

//        opening the numberpickerdialog
        this.alertDialog = builder.create();
        alertDialog.show();

    }

    public ArrayList<Integer> getData() {
        return data;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.botWeight:

                this.numberPicker.setDialogIcon(R.drawable.ic_balance_128);

                ArrayList<String> teste = new ArrayList<String>(){{
                    add("Quilos");
                    add("Gramas");
                }};
                this.numberPicker.setPickersTitles(teste);
                this.numberPicker.setOrientation(LinearLayout.HORIZONTAL);
                this.numberPicker.create(getContext(),this,NUMBEROFPICKERS);
                break;
            case R.id.btnConfirm:
                //Action for send message to activity that implements the interface
                if (command != null){
                    if (this.data != null) {
                        Pair<String, String> data = new Pair<>(
                                this.data.get(0).toString(), this.data.get(1).toString());
                        command.onSendMessage(data);
                    }else{
                        command.onSendMessage(null);
                    }
                }
                break;
        }
    }

    public void updateTextWeight(){
        if (this.data != null){
            String weight = String.valueOf(this.data.get(0))+"."+String.valueOf(this.data.get(1))
                    +" "+getResources().getString(R.string.unit_kg);
            botWeight.setText(weight);
        }else{
            botWeight.setText(getResources().getString(R.string.unit_kg));
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            command = (SendMessageListener)context;
        }catch (ClassCastException e){
            throw new ClassCastException(getActivity().toString()+"Must implement Sender interface");
        }

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        this.data = this.numberPicker.getData();
        updateTextWeight();

        session.putString("lastWeight1", this.data.get(0).toString());
        session.putString("lastWeight2", this.data.get(1).toString());
    }

}
