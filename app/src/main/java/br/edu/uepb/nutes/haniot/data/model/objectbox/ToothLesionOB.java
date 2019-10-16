package br.edu.uepb.nutes.haniot.data.model.objectbox;

import java.util.Objects;

import io.objectbox.relation.ToOne;

public class ToothLesionOB {

    private String toothType;

    private String lesionType;

    private ToOne<OralHealthRecordOB> oralHealthRecord;

    public ToothLesionOB(String toothType, String lesionType) {
        this.toothType = toothType;
        this.lesionType = lesionType;
    }

    public String getToothType() {
        return toothType;
    }

    public void setToothType(String toothType) {
        this.toothType = toothType;
    }

    public String getLesionType() {
        return lesionType;
    }

    public void setLesionType(String lesionType) {
        this.lesionType = lesionType;
    }

    public ToOne<OralHealthRecordOB> getOralHealthRecord() {
        return oralHealthRecord;
    }

    public void setOralHealthRecord(ToOne<OralHealthRecordOB> oralHealthRecord) {
        this.oralHealthRecord = oralHealthRecord;
    }

//    /**
//     * Convert object to json format.
//     *
//     * @return String
//     */
//    public String toJson() {
//        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
//        String a = gson.toJson(this);
//        Log.i("AAAAAAAAAA", a);
//        return a;
//    }

    @Override
    public String toString() {
        return "ToothLesionOB{" +
                "toothType='" + toothType + '\'' +
                ", lesionType='" + lesionType + '\'' +
                ", oralHealthRecord=" + oralHealthRecord +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToothLesionOB that = (ToothLesionOB) o;
        return toothType.equals(that.toothType) &&
                lesionType.equals(that.lesionType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toothType, lesionType);
    }
}
