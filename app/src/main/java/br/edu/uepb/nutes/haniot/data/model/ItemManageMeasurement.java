package br.edu.uepb.nutes.haniot.data.model;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemManageMeasurement {

    private long id;
    private ImageView icon;
    private TextView       name;
    private SwitchCompat switchButton;
    private Boolean switchState;
    private Context context;

    public ItemManageMeasurement(Context context){
        this.context = context;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Boolean getSwitchState() {
        return switchState;
    }

    public void setSwitchState(Boolean switchState) {
        this.switchState = switchState;
    }

    public ImageView getIcon() {
        return icon;
    }

    public void setIcon(ImageView icon){
        this.icon = icon;
    }

    public String getName() {
        return this.name.getText().toString();
    }

    public void setName(String name){
        if(this.name == null){
            this.name = new TextView(context);
            this.name.setText(name);
        }
        this.name.setText(name);
    }

    public SwitchCompat getSwitchButton() {
        return switchButton;
    }

    public void setSwitchButton(SwitchCompat switchButton){
        this.switchButton = switchButton;
    }

    public Context getContext() {
        return context;
    }

}
