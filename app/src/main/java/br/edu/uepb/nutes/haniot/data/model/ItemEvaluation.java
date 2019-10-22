package br.edu.uepb.nutes.haniot.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import br.edu.uepb.nutes.haniot.data.model.nutritional.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.nutritional.MedicalRecord;
import br.edu.uepb.nutes.haniot.data.model.nutritional.PhysicalActivityHabit;
import br.edu.uepb.nutes.haniot.data.model.nutritional.SleepHabit;
import br.edu.uepb.nutes.haniot.data.model.odontological.FamilyCohesionRecord;
import br.edu.uepb.nutes.haniot.data.model.odontological.OralHealthRecord;
import br.edu.uepb.nutes.haniot.data.model.odontological.SociodemographicRecord;

public class ItemEvaluation implements Parcelable, Cloneable {
    public static final int TYPE_MEASUREMENT = 2;
    public static final int TYPE_QUIZ = 3;
    public static final int TYPE_EMPTY = 4;
    public static final int TYPE_LOADING = 5;
    public static final int TYPE_EMPTY_REQUIRED = 6;
    public static final int TYPE_ERROR = 7;

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

    private Measurement measurement;
    private FeedingHabitsRecord feedingHabitsRecord;
    private MedicalRecord medicalRecord;
    private PhysicalActivityHabit physicalActivityHabit;
    private SleepHabit sleepHabit;

    private OralHealthRecord oralHealthRecord;
    private FamilyCohesionRecord familyCohesionRecord;
    private SociodemographicRecord sociodemographicRecord;
    private List<HeartRateItem> dataset;

    public ItemEvaluation() { }

    public ItemEvaluation(int icon, int typeHeader, String title, String quizText, int typeEvaluation) {
        this.icon = icon;
        this.typeHeader = typeHeader;
        this.title = title;
        this.quizText = quizText;
        this.typeEvaluation = typeEvaluation;
    }

    public ItemEvaluation(int icon, int typeHeader, String title, String valueMeasurement, String unitMeasurement, String time, int typeEvaluation) {
        this.icon = icon;
        this.typeHeader = typeHeader;
        this.title = title;
        this.valueMeasurement = valueMeasurement;
        this.unitMeasurement = unitMeasurement;
        this.time = time;
        this.typeEvaluation = typeEvaluation;
    }

    public ItemEvaluation(int icon, int typeHeader, String title, Measurement measurement, int typeEvaluation) {
        this.icon = icon;
        this.typeHeader = typeHeader;
        this.title = title;
        this.measurement = measurement;
        this.typeEvaluation = typeEvaluation;
    }

    public ItemEvaluation(int icon, int typeHeader, String title, String quizText, String time, int typeEvaluation) {
        this.icon = icon;
        this.typeHeader = typeHeader;
        this.title = title;
        this.quizText = quizText;
        this.time = time;
        this.typeEvaluation = typeEvaluation;
    }

    public ItemEvaluation(int icon, int typeHeader, String title, int typeEvaluation) {
        this.icon = icon;
        this.typeHeader = typeHeader;
        this.title = title;
        this.typeEvaluation = typeEvaluation;
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
        typeEvaluation = in.readInt();
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

    public FeedingHabitsRecord getFeedingHabitsRecord() {
        return feedingHabitsRecord;
    }

    public void setFeedingHabitsRecord(FeedingHabitsRecord feedingHabitsRecord) {
        this.feedingHabitsRecord = feedingHabitsRecord;
    }

    public MedicalRecord getMedicalRecord() {
        return medicalRecord;
    }

    public void setMedicalRecord(MedicalRecord medicalRecord) {
        this.medicalRecord = medicalRecord;
    }

    public PhysicalActivityHabit getPhysicalActivityHabit() {
        return physicalActivityHabit;
    }

    public void setPhysicalActivityHabit(PhysicalActivityHabit physicalActivityHabit) {
        this.physicalActivityHabit = physicalActivityHabit;
    }

    public SleepHabit getSleepHabit() {
        return sleepHabit;
    }

    public void setSleepHabit(SleepHabit sleepHabit) {
        this.sleepHabit = sleepHabit;
    }

    public OralHealthRecord getOralHealthRecord() {
        return oralHealthRecord;
    }

    public void setOralHealthRecord(OralHealthRecord oralHealthRecord) {
        this.oralHealthRecord = oralHealthRecord;
    }

    public FamilyCohesionRecord getFamilyCohesionRecord() {
        return familyCohesionRecord;
    }

    public void setFamilyCohesionRecord(FamilyCohesionRecord familyCohesionRecord) {
        this.familyCohesionRecord = familyCohesionRecord;
    }

    public SociodemographicRecord getSociodemographicRecord() {
        return sociodemographicRecord;
    }

    public void setSociodemographicRecord(SociodemographicRecord sociodemographicRecord) {
        this.sociodemographicRecord = sociodemographicRecord;
    }

    public int getTypeHeader() {
        return typeHeader;
    }

    public void setTypeHeader(int typeHeader) {
        this.typeHeader = typeHeader;
    }

    public int getTypeEvaluation() {
        return typeEvaluation;
    }

    public void setTypeEvaluation(int typeEvaluation) {
        this.typeEvaluation = typeEvaluation;
    }

    public List<HeartRateItem> getDataset() {
        return dataset;
    }

    public void setDataset(List<HeartRateItem> dataset) {
        this.dataset = dataset;
    }

    public Measurement getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Measurement measurement) {
        this.measurement = measurement;
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
        dest.writeInt(typeEvaluation);
    }

    @Override
    public Object clone() {
        ItemEvaluation c = null; //Chama o clone de Object. Isso copia automaticamente todos os atributos.
        try {
            c = (ItemEvaluation) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        //Como a lista de lojas é mutável, o ideal é copia-la também. Senão a alteração da lista num cliente afetará o outro.
        return c;        //String é um objeto, mas é imutável. Podemos compartilha-lo.
    }
}
