package br.edu.uepb.nutes.haniot.data.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Result {

    @SerializedName("success")
    @Expose()
    private List<Success> success;

    @SerializedName("error")
    @Expose()
    private List<Erro> erro;

    public List<Success> getSuccess() {
        return success;
    }

    public List<Erro> getErro() {
        return erro;
    }

    @Override
    public String toString() {
        return "Result{" +
                "success=" + success.toString() +
                ", erro=" + erro.toString() +
                '}';
    }

    public class Success {
        @SerializedName("code")
        @Expose()
        private int code;

        @SerializedName("item")
        @Expose()
        private Item item;

        public int getCode() {
            return code;
        }

        public Item getItem() {
            return item;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "code=" + code +
                    ", item=" + item.toString() +
                    '}';
        }
    }

    public class Erro {
        @SerializedName("code")
        @Expose()
        public int code;

        @SerializedName("message")
        @Expose()
        public String message;

        @SerializedName("description")
        @Expose()
        public String description;

        @SerializedName("item")
        @Expose()
        public Item item;

        @Override
        public String toString() {
            return "Erro{" +
                    "code=" + code +
                    ", message='" + message + '\'' +
                    ", description='" + description + '\'' +
                    ", item=" + item.toString() +
                    '}';
        }
    }

    public class Item {

        @SerializedName("value")
        @Expose()
        private double value;

        @SerializedName("unit")
        @Expose()
        private String unit;

        @SerializedName("type")
        @Expose()
        private String type;

        @SerializedName("timestamp")
        @Expose()
        private String timestamp;

        @SerializedName("patient_id")
        @Expose()
        private String patient_id;

        @SerializedName("device_id")
        @Expose()
        private String deviceId;

        @SerializedName("fat")
        @Expose()
        private BodyFat fat;

        @SerializedName("dataset")
        @Expose()
        private List<HeartRateItem> dataset;

        @SerializedName("bodyfat")
        @Expose()
        private List<BodyFat> bodyFat;

        @SerializedName("systolic")
        @Expose()
        private int systolic;

        @SerializedName("diastolic")
        @Expose()
        private int diastolic;

        @SerializedName("pulse")
        @Expose()
        private int pulse;

        @SerializedName("meal")
        @Expose()
        private String meal;

        @Override
        public String toString() {
            return "Item{" +
                    "value=" + value +
                    ", unit='" + unit + '\'' +
                    ", type='" + type + '\'' +
                    ", timestamp='" + timestamp + '\'' +
                    ", patient_id='" + patient_id + '\'' +
                    ", deviceId='" + deviceId + '\'' +
                    ", fat=" + fat +
                    ", dataset=" + dataset +
                    ", bodyFat=" + bodyFat +
                    ", systolic=" + systolic +
                    ", diastolic=" + diastolic +
                    ", pulse=" + pulse +
                    ", meal='" + meal + '\'' +
                    '}';
        }

        public double getValue() {
            return value;
        }

        public String getUnit() {
            return unit;
        }

        public String getType() {
            return type;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public String getPatient_id() {
            return patient_id;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public BodyFat getFat() {
            return fat;
        }

        public List<HeartRateItem> getDataset() {
            return dataset;
        }

        public List<BodyFat> getBodyFat() {
            return bodyFat;
        }

        public int getSystolic() {
            return systolic;
        }

        public int getDiastolic() {
            return diastolic;
        }

        public int getPulse() {
            return pulse;
        }

        public String getMeal() {
            return meal;
        }
    }
}
