package br.edu.uepb.nutes.haniot.data.model;

import android.content.Context;

import androidx.annotation.NonNull;

import br.edu.uepb.nutes.haniot.R;

public class MeasurementMonitor {
    public static final int CONNECTED = 0;
    public static final int DISCONNECTED = 1;
    public static final int NO_REGISTERED = 2;
    public static final int RECEIVING = 3;
    private int icon;
    private String description;
    private String name;
    private String measurementValue;
    private String measurementInitials;
    private String time;
    private int type;
    private int status;
    //used to access strings
    private Context context;

    public MeasurementMonitor() {
    }

    public MeasurementMonitor(Context context, int icon, String description, String measurementValue, int type, String measurementInitials) {
        this.context = context;
        this.icon = icon;
        this.description = description;
        this.measurementValue = measurementValue;
        this.type = type;
        this.measurementInitials = measurementInitials;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getType() {
        return type;
    }

    //    To each new button on grid, add the type here
    public void setType(int type) {
        switch (type) {

            case ItemGridType.HEART_RATE:

                measurementInitials = context.getResources().getString(R.string.unit_pulse);
                this.type = ItemGridType.HEART_RATE;
                break;

            case ItemGridType.BLOOD_GLUCOSE:

                this.type = ItemGridType.BLOOD_GLUCOSE;
                measurementInitials = context.getResources().getString(R.string.unit_glucose_mg_dL);
                break;

            case ItemGridType.BLOOD_PRESSURE:

                this.type = ItemGridType.BLOOD_PRESSURE;
                measurementInitials = context.getResources().getString(R.string.unit_pressure);
                break;

            case ItemGridType.TEMPERATURE:

                this.type = ItemGridType.TEMPERATURE;
                measurementInitials = context.getResources()
                        .getString(R.string.unit_temperature);
                break;

            case ItemGridType.WEIGHT:

                this.type = ItemGridType.WEIGHT;
                measurementInitials = context.getResources().getString(R.string.unit_weight);
                break;

            case ItemGridType.SLEEP:

                this.type = ItemGridType.SLEEP;
                measurementInitials = context.getResources().getString(R.string.unit_hour);
                break;

            case ItemGridType.ACTIVITY:

                this.type = ItemGridType.ACTIVITY;
                measurementInitials = context.getResources().getString(R.string.unit_kilometer);
                break;

            case ItemGridType.ANTHROPOMETRIC:

                this.type = ItemGridType.ANTHROPOMETRIC;
                measurementInitials = context.getResources().getString(R.string.unit_meters);
                break;
        }
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeasurementValue() {
        return measurementValue;
    }

    public void setMeasurementValue(String measurementValue) {
        this.measurementValue = measurementValue;
    }

    public String getMeasurementInitials() {
        return measurementInitials;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setMeasurementInitials(String measurementInitials) {
        this.measurementInitials = measurementInitials;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @NonNull
    @Override
    public String toString() {
        return "MeasurementMonitor{" +
                "icon=" + icon +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                ", measurementValue='" + measurementValue + '\'' +
                ", measurementInitials='" + measurementInitials + '\'' +
                ", time='" + time + '\'' +
                ", type=" + type +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        return this.type == ((MeasurementMonitor) obj).type;
    }
}
