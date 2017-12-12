package br.edu.uepb.nutes.haniot.model;

/**
 * Created by lucas on 07/10/17.
 */

public class MeasurementBodyCompositionMonitor {
    private double kg;
    private double cm;
    private double kg_m2;
    private double cal;
    private double y;
    private double porcent1;
    private double porcent2;
    private double porcent3;
    private double undefined;

    private String unitKg;
    private String unitCm;
    private String unitKg_m2;
    private String unitCal;
    private String unitY;
    private String unitPorcent1;
    private String unitPorcent2;
    private String unitPorcent3;

    private long registrationTime; // Date and Time of measurement in milliseconds
    private String deviceAddress; // MAC address of the device (Thermometer, Glucose...)
    private int hasSent; // Measurement sent?

    public double getKg() {
        return kg;
    }

    public void setKg(double kg) {
        this.kg = kg;
    }

    public double getCm() {
        return cm;
    }

    public void setCm(double cm) {
        this.cm = cm;
    }

    public double getKg_m2() {
        return kg_m2;
    }

    public void setKg_m2(double kg_m2) {
        this.kg_m2 = kg_m2;
    }

    public double getCal() {
        return cal;
    }

    public void setCal(double cal) {
        this.cal = cal;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getPorcent1() {
        return porcent1;
    }

    public void setPorcent1(double porcent1) {
        this.porcent1 = porcent1;
    }

    public double getPorcent2() {
        return porcent2;
    }

    public void setPorcent2(double porcent2) {
        this.porcent2 = porcent2;
    }

    public double getPorcent3() {
        return porcent3;
    }

    public void setPorcent3(double porcent3) {
        this.porcent3 = porcent3;
    }

    public double getUndefined() {
        return undefined;
    }

    public void setUndefined(double undefined) {
        this.undefined = undefined;
    }

    public String getUnitKg() {
        return unitKg;
    }

    public void setUnitKg(String unitKg) {
        this.unitKg = unitKg;
    }

    public String getUnitCm() {
        return unitCm;
    }

    public void setUnitCm(String unitCm) {
        this.unitCm = unitCm;
    }

    public String getUnitKg_m2() {
        return unitKg_m2;
    }

    public void setUnitKg_m2(String unitKg_m2) {
        this.unitKg_m2 = unitKg_m2;
    }

    public String getUnitCal() {
        return unitCal;
    }

    public void setUnitCal(String unitCal) {
        this.unitCal = unitCal;
    }

    public String getUnitY() {
        return unitY;
    }

    public void setUnitY(String unitY) {
        this.unitY = unitY;
    }

    public String getUnitPorcent1() {
        return unitPorcent1;
    }

    public void setUnitPorcent1(String unitPorcent1) {
        this.unitPorcent1 = unitPorcent1;
    }

    public String getUnitPorcent2() {
        return unitPorcent2;
    }

    public void setUnitPorcent2(String unitPorcent2) {
        this.unitPorcent2 = unitPorcent2;
    }

    public String getUnitPorcent3() {
        return unitPorcent3;
    }

    public void setUnitPorcent3(String unitPorcent3) {
        this.unitPorcent3 = unitPorcent3;
    }

    public long getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(long registrationTime) {
        this.registrationTime = registrationTime;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public int getHasSent() {
        return hasSent;
    }

    public void setHasSent(int hasSent) {
        this.hasSent = hasSent;
    }
}
