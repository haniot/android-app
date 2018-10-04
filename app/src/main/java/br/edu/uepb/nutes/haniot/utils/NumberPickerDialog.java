package br.edu.uepb.nutes.haniot.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatButton;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;

import br.edu.uepb.nutes.haniot.R;

public class NumberPickerDialog {

    private View mainView;
    private Context context;
    private LayoutInflater inflater;
    private AlertDialog.Builder builder;
    private DialogInterface.OnClickListener mListener;
    private LinearLayout mainLayout;
    private String dialogTitle;
    private String dialogMessage;
    private int dialogIcon = -1;
    private LinearLayout baseLayout;
    private AlertDialog dialog;
    private ArrayList<NumberPicker> pickers;
    private ArrayList<String> pickersTitles;
    private LinearLayout pickersTitlesLayout;
    private int orientation = -1;

    public NumberPickerDialog(Context context){

        this.pickers = new ArrayList<>();
        this.context = context;

    }

    public ArrayList<Float> create(Context context, DialogInterface.OnClickListener listener, int numberOfPickers){

        this.inflater = LayoutInflater.from(context);
        this.mainView = inflater.inflate(R.layout.clean_number_picker_dialog,null);

        this.mainLayout = (LinearLayout)this.mainView.findViewById(R.id.mainLayout);

        this.builder = new AlertDialog.Builder(context);
        this.builder.setView(this.mainView);

        if(this.dialogTitle != null){
            this.builder.setTitle(getDialogTitle());
        }else{
            this.builder.setTitle("");
        }

        if (this.dialogMessage != null){
            this.builder.setMessage(getDialogMessage());
        }else{
            this.builder.setMessage("");
        }

        if (this.dialogIcon != -1){
            this.builder.setIcon(getDialogIcon());
        }else{
            Log.d("TESTE","icnull");
        }

        this.builder.setNegativeButton(context.getResources().getString(R.string.bt_cancel), (v,d) ->{
            this.dialog.cancel();
        });

        this.builder.setPositiveButton(context.getResources().getString(R.string.bt_ok),listener);
        setmListener(listener);

        initBaseLayout(context);

        this.pickers.clear();
        for(int i = 0; i < numberOfPickers; i++){

            NumberPicker picker = new NumberPicker(context);
            picker.setDividerColor(context.getResources().getColor(R.color.colorPrimary));
            picker.setWrapSelectorWheel(true);
            picker.setDividerDistance(60);
            picker.setDividerThickness(5);
            picker.setMinimumWidth(100);
            if (this.orientation == -1 || orientation == LinearLayout.HORIZONTAL){
                picker.setOrientation(LinearLayout.VERTICAL);
            }else{
                picker.setOrientation(LinearLayout.HORIZONTAL);
            }

            this.pickers.add(picker);
        }

        addPickersOnLayout();

        this.dialog = builder.create();
        this.dialog.show();

        return null;
    }

    public ArrayList<Integer> getData(){
        if (!this.pickers.isEmpty()){
            ArrayList<Integer> selectedNumbers = new ArrayList<>();
            for (int i = 0; i < this.pickers.size(); i++){
                selectedNumbers.add(this.pickers.get(i).getValue());
            }
            return selectedNumbers;
        }
        return null;
    }

    public void addPickersOnLayout(){

        LinearLayout pickersLayout = new LinearLayout(this.context);
        pickersLayout.setGravity(Gravity.CENTER);
        if (this.orientation == -1 || orientation == LinearLayout.HORIZONTAL) {
            pickersLayout.setOrientation(LinearLayout.HORIZONTAL);
        }else{
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(100, 200);
            pickersLayout.setLayoutParams(lp);
            pickersLayout.setOrientation(LinearLayout.VERTICAL);
        }

        for (NumberPicker picker : this.pickers){
            pickersLayout.addView(picker);

            ImageView divider = new ImageView(context);
            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(25, LinearLayout.LayoutParams.MATCH_PARENT);
            divider.setLayoutParams(lp);
            divider.setBackgroundColor(Color.TRANSPARENT);

            pickersLayout.addView(divider);
        }

        addTitlesOnPickers();

        this.baseLayout.addView(pickersLayout);
        if (pickersTitlesLayout != null) this.baseLayout.addView(pickersTitlesLayout);
        this.mainLayout.removeAllViews();
        this.mainLayout.addView(this.baseLayout);
    }

    public void addTitlesOnPickers(){
        if (getPickersTitles() != null) {
            this.pickersTitlesLayout = new LinearLayout(this.context);
            if (this.orientation == -1 || orientation == LinearLayout.HORIZONTAL) {
                this.pickersTitlesLayout.setOrientation(LinearLayout.HORIZONTAL);
            }else{
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(100, 100);
                this.pickersTitlesLayout.setOrientation(LinearLayout.VERTICAL);
                this.pickersTitlesLayout.setLayoutParams(lp);
            }
            this.pickersTitlesLayout.setGravity(Gravity.CENTER);
//            this.pickersTitlesLayout.setPadding(10, 0, 0, 0);

            for (String pickerTitle : this.pickersTitles) {

                AppCompatButton bt = new AppCompatButton(context, null, R.style.Widget_AppCompat_Button_Colored);
                bt.setText(pickerTitle);
                bt.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                bt.setOnClickListener(c -> {

                });

                this.pickersTitlesLayout.addView(bt);

                ImageView divider = new ImageView(context);
                LinearLayout.LayoutParams lp =
                        new LinearLayout.LayoutParams(60, LinearLayout.LayoutParams.MATCH_PARENT);
                divider.setLayoutParams(lp);
                divider.setBackgroundColor(Color.TRANSPARENT);

                this.pickersTitlesLayout.addView(divider);
            }
        }
    }

    public void initBaseLayout(Context context){
        this.baseLayout = new LinearLayout(context);
        if (this.orientation == -1 || orientation == LinearLayout.HORIZONTAL){
            this.baseLayout.setOrientation(LinearLayout.VERTICAL );
        }else{
            this.baseLayout.setOrientation(LinearLayout.HORIZONTAL);
        }
        this.baseLayout.setGravity(Gravity.CENTER);
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void setCancelable(Boolean state){
        this.builder.setCancelable(state);
    }

    public DialogInterface.OnClickListener getmListener() {
        return mListener;
    }

    public ArrayList<String> getPickersTitles() {
        return pickersTitles;
    }

    public void setPickersTitles(ArrayList<String> pickersTitles) {
        this.pickersTitles = pickersTitles;
    }

    public void setmListener(DialogInterface.OnClickListener mListener) {
        this.mListener = mListener;
        this.builder.setPositiveButton(context.getResources().getString(R.string.bt_ok),
                this.mListener);
    }

    public String getDialogMessage() {
        return dialogMessage;
    }

    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }

    public String getDialogTitle() {
        return dialogTitle;
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public int getDialogIcon() {
        return dialogIcon;
    }

    public void setDialogIcon(int dialogIcon){

        this.dialogIcon = dialogIcon;
        Log.d("TESTE","icon setado");

    }

}
