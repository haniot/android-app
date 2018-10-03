package br.edu.uepb.nutes.haniot.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

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
    private int dialogIcon;
    private LinearLayout baseLayout;
    private AlertDialog dialog;
    private ArrayList<NumberPicker> pickers;

    public NumberPickerDialog(Context context){

        this.context = context;
        this.pickers = new ArrayList<>();

    }

    public ArrayList<Float> create(Context context, DialogInterface.OnClickListener listener, int numberOfPickers){

        this.inflater = LayoutInflater.from(context);
        this.mainView = inflater.inflate(R.layout.clean_number_picker_dialog,null);

        this.mainLayout = (LinearLayout)this.mainView.findViewById(R.id.mainLayout);

        this.builder = new AlertDialog.Builder(context);
        this.builder.setView(this.mainView);
        this.builder.setTitle("a");
        this.builder.setMessage("b");
        this.builder.setNegativeButton(context.getResources().getString(R.string.bt_cancel), (v,d) ->{
            this.dialog.cancel();
        });
        this.builder.setPositiveButton(context.getResources().getString(R.string.bt_ok), (v,d) -> {

        });

        this.mainLayout.removeAllViews();
        initBaseLayout(context);

        this.pickers.clear();
        for(int i = 0; i < numberOfPickers; i++){
            this.pickers.add(new NumberPicker(context));
        }

        for (NumberPicker picker : this.pickers){
            this.baseLayout.addView(picker);
            this.baseLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_BEGINNING);

            ImageView divider = new ImageView(context);
            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(20, LinearLayout.LayoutParams.MATCH_PARENT);
            divider.setLayoutParams(lp);
            divider.setBackgroundColor(Color.TRANSPARENT);

            this.baseLayout.addView(divider);
        }
        this.mainLayout.removeAllViews();
        this.mainLayout.addView(this.baseLayout);

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

    public void initBaseLayout(Context context){
        this.baseLayout = new LinearLayout(context);
        this.baseLayout.setOrientation(LinearLayout.HORIZONTAL);
        this.baseLayout.setGravity(Gravity.CENTER);
    }

    public void setCancelable(Boolean state){
        this.builder.setCancelable(state);
    }

    public DialogInterface.OnClickListener getmListener() {
        return mListener;
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
        this.builder.setMessage(dialogMessage);
    }

    public String getDialogTitle() {
        return dialogTitle;
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
        this.builder.setTitle(dialogTitle);
    }

    public int getDialogIcon() {
        return dialogIcon;
    }

    public void setDialogIcon(int dialogIcon) throws Exception{
        this.dialogIcon = dialogIcon;
        this.builder.setIcon(dialogIcon);
    }
}
