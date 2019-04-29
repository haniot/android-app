package br.edu.uepb.nutes.haniot.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ItemEvaluation implements Parcelable {
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
    private int typeEvaluation;

    public ItemEvaluation() {
    }

    protected ItemEvaluation(Parcel in) {
        icon = in.readInt();
        typeHeader = in.readInt();
        title = in.readString();
        quizText = in.readString();
        valueMeasurement = in.readString();
        unitMeasurement = in.readString();
        date = in.readString();
        time = in.readString();
        checked = in.readByte() != 0;
    }

    public static final Creator<ItemEvaluation> CREATOR = new Creator<ItemEvaluation>() {
        @Override
        public ItemEvaluation createFromParcel(Parcel in) {
            return new ItemEvaluation(in);
        }

        @Override
        public ItemEvaluation[] newArray(int size) {
            return new ItemEvaluation[size];
        }
    };

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

    public int getTypeEvaluation() {
        return typeEvaluation;
    }

    public void setTypeEvaluation(int typeEvaluation) {
        this.typeEvaluation = typeEvaluation;
    }

    public void setTypeHeader(int typeHeader) {
        this.typeHeader = typeHeader;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(icon);
        dest.writeInt(typeHeader);
        dest.writeString(title);
        dest.writeString(quizText);
        dest.writeString(valueMeasurement);
        dest.writeString(unitMeasurement);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeByte((byte) (checked ? 1 : 0));
    }
}
