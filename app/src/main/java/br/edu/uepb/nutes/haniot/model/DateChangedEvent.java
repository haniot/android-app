package br.edu.uepb.nutes.haniot.model;

import br.edu.uepb.nutes.haniot.utils.Log;

public class DateChangedEvent {

    private String activity = "--";
    private String sleep = "--";
    private String glucose = "--";
    private String pressure = "--";
    private String temperature = "--";
    private String weight = "--";
    private String heartRate = "--";
    private String height = "--";
    private String circumference = "--";

    public DateChangedEvent(){}

    public void printValues(){
        if (!this.activity.equals("--")){
            Log.d("TESTE","Valor da medição activity: "+this.activity);
        }
        if (!this.sleep.equals("--")){
            Log.d("TESTE","Valor da medição sleep: "+this.sleep);
        }
        if (!this.glucose.equals("--")){
            Log.d("TESTE","Valor da medição glicose: "+this.glucose);
        }
        if (!this.pressure.equals("--")){
            Log.d("TESTE","Valor da medição pressão: "+this.pressure);
        }
        if (!this.temperature.equals("--")){
            Log.d("TESTE","Valor da medição temperatura: "+this.temperature);
        }
        if (!this.weight.equals("--")){
            Log.d("TESTE","Valor da medição peso: "+this.weight);
        }
        if (!this.heartRate.equals("--")){
            Log.d("TESTE","Valor da medição batimento cardiaco: "+this.heartRate);
        }
        if (!this.height.equals("--")){
            Log.d("TESTE","Valor da medição altura: "+this.height);
        }
        if (!this.circumference.equals("--")){
            Log.d("TESTE","Valor da medição circunferencia: "+this.circumference);
        }
    }

    public void resetAllValues(){
        this.activity = "--";
        this.sleep = "--";
        this.glucose = "--";
        this.pressure = "--";
        this.temperature = "--";
        this.weight = "--";
        this.heartRate = "--";
        this.circumference = "--";
        this.height = "--";
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getCircumference() {
        return circumference;
    }

    public void setCircumference(String circumference) {
        this.circumference = circumference;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getSleep() {
        return sleep;
    }

    public void setSleep(String sleep) {
        this.sleep = sleep;
    }

    public String getGlucose() {
        return glucose;
    }

    public void setGlucose(String glucose) {
        this.glucose = glucose;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

}
