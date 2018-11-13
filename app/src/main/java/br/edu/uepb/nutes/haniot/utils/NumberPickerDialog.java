package br.edu.uepb.nutes.haniot.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.widget.AppCompatButton;
import android.text.method.DigitsKeyListener;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.shawnlin.numberpicker.NumberPicker;

import java.util.ArrayList;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.Session;

public class NumberPickerDialog {
    //    haniot specific
    private Session session;

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
    private ArrayList<Pair<Integer, Integer>> bounds;
    private ArrayList<Integer> lastMeasurements;
    private int startValue = -1;

    public NumberPickerDialog(Context context) {

        this.pickers = new ArrayList<>();
        this.context = context;
        this.session = new Session(context);

    }

    // Create and configure the dialog
    public ArrayList<Float> create(Context context, DialogInterface.OnClickListener listener, int numberOfPickers) {

//        Dialog configuration
        this.inflater = LayoutInflater.from(context);
        this.mainView = inflater.inflate(R.layout.clean_number_picker_dialog, null);
        this.mainLayout = (LinearLayout) this.mainView.findViewById(R.id.mainLayout);
        this.builder = new AlertDialog.Builder(context);
        this.builder.setView(this.mainView);

        if (this.dialogTitle != null) {
            this.builder.setTitle(getDialogTitle());
        } else {
            this.builder.setTitle("");
        }

        if (this.dialogMessage != null) {
            this.builder.setMessage(getDialogMessage());
        } else {
            this.builder.setMessage("");
        }

        if (this.dialogIcon != -1) {
            this.builder.setIcon(getDialogIcon());
        }

//        Cancel listener will ever close the dialog window
        this.builder.setNegativeButton(context.getResources().getString(R.string.bt_cancel), (v, d) -> {
            this.dialog.cancel();
        });

//        Positive listener is setted on the class that have a instance of this class
        this.builder.setPositiveButton(context.getResources().getString(R.string.bt_ok), listener);
        setmListener(listener);

        initBaseLayout(context);

        this.pickers.clear();

//        this for create the pickers and customize
        for (int i = 0; i < numberOfPickers; i++) {

            NumberPicker picker = new NumberPicker(context);
            picker.setDividerColor(context.getResources().getColor(R.color.colorPrimary));
            picker.setWrapSelectorWheel(true);
            picker.setDividerDistance(50);
            picker.setDividerThickness(5);
            picker.setMinimumWidth(100);

//            Adjust the orientation of pickers
            if (this.orientation == -1 || orientation == LinearLayout.HORIZONTAL) {
                picker.setOrientation(LinearLayout.VERTICAL);
            } else {
                picker.setOrientation(LinearLayout.HORIZONTAL);
            }

//            Set bounds of each picker, the bounds count must be the same of number of pickers
            if (getBounds() != null) {
                picker.setMinValue(this.bounds.get(i).first);
                picker.setMaxValue(this.bounds.get(i).second);
            }

            if (startValue != -1){
                picker.setValue(this.startValue);
            }

//            Set value to each picker of last measurement, the last measurement count must be
// the same of number of pickers
            if (getLastMeasurements() != null) {
                picker.setValue(this.getLastMeasurements().get(i));
            }

            this.pickers.add(picker);
        }

//        Add the pickers on layout
        addPickersOnLayout();

//        Create the dialog
        this.dialog = builder.create();
        this.dialog.show();

        return null;
    }

    //    Haniot specific
    public ArrayList<Integer> getLastMeasurements() {
        return lastMeasurements;
    }

    public void setLastMeasurements(ArrayList<Integer> lastMeasurements) {
        this.lastMeasurements = lastMeasurements;
    }

    public ArrayList<Integer> getData() {
        if (!this.pickers.isEmpty()) {
            ArrayList<Integer> selectedNumbers = new ArrayList<>();
            for (int i = 0; i < this.pickers.size(); i++) {
                selectedNumbers.add(this.pickers.get(i).getValue());
            }
            return selectedNumbers;
        }
        return null;
    }

    private void addPickersOnLayout() {

        LinearLayout pickersLayout = new LinearLayout(this.context);
        pickersLayout.setGravity(Gravity.CENTER);
        if (this.orientation == -1 || orientation == LinearLayout.HORIZONTAL) {
            pickersLayout.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT);
            pickersLayout.setLayoutParams(lp);
            pickersLayout.setOrientation(LinearLayout.VERTICAL);
        }

        for (NumberPicker picker : this.pickers) {
            pickersLayout.addView(picker);

            ImageView divider = new ImageView(context);
            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(30, 10);
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

    private void addTitlesOnPickers() {
        if (getPickersTitles() != null) {
            this.pickersTitlesLayout = new LinearLayout(this.context);

            if (this.orientation == -1 || orientation == LinearLayout.HORIZONTAL) {

                this.pickersTitlesLayout.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                this.pickersTitlesLayout.setLayoutParams(lp);

            } else {

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        150,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                this.pickersTitlesLayout.setOrientation(LinearLayout.VERTICAL);
                this.pickersTitlesLayout.setLayoutParams(lp);

            }
            this.pickersTitlesLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            for (String pickerTitle : this.pickersTitles) {

                AppCompatButton bt = new AppCompatButton(context, null, R.style.Widget_AppCompat_Button_Colored);
                bt.setText(pickerTitle);
                bt.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                bt.setOnClickListener(c -> {

                });

                ImageView divider = new ImageView(context);

                if (this.orientation == -1 || orientation == LinearLayout.HORIZONTAL) {
                    LinearLayout.LayoutParams lp =
                            new LinearLayout.LayoutParams(50, ViewGroup.LayoutParams.WRAP_CONTENT);
                    divider.setLayoutParams(lp);
                    divider.setBackgroundColor(Color.TRANSPARENT);
                    this.pickersTitlesLayout.addView(bt);
                    this.pickersTitlesLayout.addView(divider);
                } else {
                    LinearLayout.LayoutParams lp =
                            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 50);
                    divider.setLayoutParams(lp);
                    divider.setBackgroundColor(Color.TRANSPARENT);
                    this.pickersTitlesLayout.addView(divider);
                    this.pickersTitlesLayout.addView(bt);
                }
            }
        }
    }

    private void initBaseLayout(Context context) {
        this.baseLayout = new LinearLayout(context);
        if (this.orientation == -1 || orientation == LinearLayout.HORIZONTAL) {
            this.baseLayout.setOrientation(LinearLayout.VERTICAL);
        } else {
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

    public void setCancelable(Boolean state) {
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

    public void setDialogIcon(int dialogIcon) {
        this.dialogIcon = dialogIcon;

    }

    public ArrayList<Pair<Integer, Integer>> getBounds() {
        return bounds;
    }

    public void setBounds(ArrayList<Pair<Integer, Integer>> bounds) {
        this.bounds = bounds;
    }

    public int getStartValue() {
        return startValue;
    }

    public void setStartValue(int startValue) {
        this.startValue = startValue;
    }

}
