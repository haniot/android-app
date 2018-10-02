package br.edu.uepb.nutes.haniot.fragment;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.Calendar;

import com.shawnlin.numberpicker.NumberPicker;
import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.ButterKnife;


public class AddWeightManuallyFragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.botClock)
    Button botClock;
    @BindView(R.id.botWeight)
    AppCompatButton botWeight;

//    NumberPicker things
    private LayoutInflater li;
    private AlertDialog.Builder builder;
    private AlertDialog alertDialog;

    private TimePickerDialog timePicker;

    public AddWeightManuallyFragment() { }

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

        botWeight.setOnClickListener(this);
        botClock.setOnClickListener(this);

        return view;
    }

    public void openTimePicker(){

        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        timePicker = new TimePickerDialog(getContext(), (view, hourOfDay, minute1) -> {

        }, hour, minute, true);//Yes 24 hour time
        timePicker.show();
    }

    public void openNumberPicker(){

//        here the view is inflated
        this.li = LayoutInflater.from(getContext());
        View view = li.inflate(R.layout.number_picker_dialog, null);

//        the builder is created and view is setted up to this builder
        this.builder = new AlertDialog.Builder(getContext());
        this.builder.setView(view);
        this.builder.setTitle(getResources().getString(R.string.choose_weight));
        this.builder.setIcon(getResources().getDrawable(R.drawable.ic_balance));

//        get the refference of items of view
        NumberPicker pickerPounds = (NumberPicker)view.findViewById(R.id.number_picker_pounds);
        NumberPicker pickerGrams = (NumberPicker)view.findViewById(R.id.number_picker_grams);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.botClock:
                openTimePicker();
                break;

            case R.id.botWeight:
                openNumberPicker();
                break;
        }
    }


}
