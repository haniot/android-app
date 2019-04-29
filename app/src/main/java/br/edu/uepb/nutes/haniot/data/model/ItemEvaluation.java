package br.edu.uepb.nutes.haniot.data.model;

public class ItemEvaluation {
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_MEASUREMENT = 2;
    public static final int TYPE_QUIZ = 3;
    public static final int TYPE_EMPTY = 4;

    private int icon;
    private int typeHeader;
    private String title;
    private String quizText;
    private String valueMeasurement;
    private String unitMeasurement;
    private String date;
    private String time;
    private boolean checked;

    public ItemEvaluation() {
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuizText() {
        return quizText;
    }

    public void setQuizText(String quizText) {
        this.quizText = quizText;
    }

    public String getValueMeasurement() {
        return valueMeasurement;
    }

    public void setValueMeasurement(String valueMeasurement) {
        this.valueMeasurement = valueMeasurement;
    }

    public String getUnitMeasurement() {
        return unitMeasurement;
    }

    public void setUnitMeasurement(String unitMeasurement) {
        this.unitMeasurement = unitMeasurement;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getType() {
        return typeHeader;
    }

    public void setTypeHeader(int typeHeader) {
        this.typeHeader = typeHeader;
    }
}
