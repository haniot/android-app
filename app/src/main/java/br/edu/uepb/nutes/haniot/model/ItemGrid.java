package br.edu.uepb.nutes.haniot.model;

import android.content.Context;

import br.edu.uepb.nutes.haniot.R;

public class ItemGrid {

    private int icon;
    private String description;
    private String name;
    private String measurementValue;
    private String measurementInitials;
    private int type;
    //used to access strings
    private Context context;

    public ItemGrid() {
    }

    public ItemGrid(Context context,
                    int icon,
                    String description,
                    String name,
                    String measurementValue,
                    int type) {

        this.context = context;
        this.icon = icon;
        this.description = description;
        this.name = name;
        this.measurementValue = measurementValue;
        switch (type){

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
        }
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

    public void setType(int type) {
        switch (type){

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

    public void setMeasurementInitials(String measurementInitials) {
        this.measurementInitials = measurementInitials;
    }

    @Override
    public String toString() {
        return "ItemGrid{" +
                "icon=" + icon +
                ", description='" + description + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
