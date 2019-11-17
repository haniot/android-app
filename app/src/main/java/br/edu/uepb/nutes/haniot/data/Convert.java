package br.edu.uepb.nutes.haniot.data;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.data.model.BodyFat;
import br.edu.uepb.nutes.haniot.data.model.Device;
import br.edu.uepb.nutes.haniot.data.model.HeartRateItem;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.nutritional.ChronicDisease;
import br.edu.uepb.nutes.haniot.data.model.nutritional.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.nutritional.MedicalRecord;
import br.edu.uepb.nutes.haniot.data.model.nutritional.NutritionalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.model.nutritional.PhysicalActivityHabit;
import br.edu.uepb.nutes.haniot.data.model.nutritional.SleepHabit;
import br.edu.uepb.nutes.haniot.data.model.nutritional.WeeklyFoodRecord;
import br.edu.uepb.nutes.haniot.data.model.odontological.FamilyCohesionRecord;
import br.edu.uepb.nutes.haniot.data.model.odontological.OdontologicalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.model.odontological.OralHealthRecord;
import br.edu.uepb.nutes.haniot.data.model.odontological.SociodemographicRecord;
import br.edu.uepb.nutes.haniot.data.model.odontological.ToothLesion;
import br.edu.uepb.nutes.haniot.data.objectbox.BodyFatOB;
import br.edu.uepb.nutes.haniot.data.objectbox.DeviceOB;
import br.edu.uepb.nutes.haniot.data.objectbox.HeartRateItemOB;
import br.edu.uepb.nutes.haniot.data.objectbox.MeasurementOB;
import br.edu.uepb.nutes.haniot.data.objectbox.PatientOB;
import br.edu.uepb.nutes.haniot.data.objectbox.nutritional.ChronicDiseaseOB;
import br.edu.uepb.nutes.haniot.data.objectbox.nutritional.FeedingHabitsRecordOB;
import br.edu.uepb.nutes.haniot.data.objectbox.nutritional.MedicalRecordOB;
import br.edu.uepb.nutes.haniot.data.objectbox.nutritional.NutritionalQuestionnaireOB;
import br.edu.uepb.nutes.haniot.data.objectbox.nutritional.PhysicalActivityHabitOB;
import br.edu.uepb.nutes.haniot.data.objectbox.nutritional.SleepHabitOB;
import br.edu.uepb.nutes.haniot.data.objectbox.nutritional.WeeklyFoodRecordOB;
import br.edu.uepb.nutes.haniot.data.objectbox.odontological.FamilyCohesionRecordOB;
import br.edu.uepb.nutes.haniot.data.objectbox.odontological.OdontologicalQuestionnaireOB;
import br.edu.uepb.nutes.haniot.data.objectbox.odontological.OralHealthRecordOB;
import br.edu.uepb.nutes.haniot.data.objectbox.odontological.SociodemographicRecordOB;
import br.edu.uepb.nutes.haniot.data.objectbox.odontological.ToothLesionOB;
import io.objectbox.relation.ToMany;

public class Convert {

    // ------------- DEVICE ---------------------

    public static DeviceOB convertDevice(Device d) {
        if (d == null) return null;
        return new DeviceOB(d);
    }

    public static Device convertDevice(DeviceOB d) {
        if (d == null)
            return null;
        return new Device(d);
    }

    public static List<Device> listDeviceToModel(List<DeviceOB> devices) {
        List<Device> list = new ArrayList<>();
        for (DeviceOB d : devices) {
            list.add(Convert.convertDevice(d));
        }
        return list;
    }

    // ------------- FEEDING HABITS RECORD ----------------------------

    public static FeedingHabitsRecord convertFeedingHabitsRecord(FeedingHabitsRecordOB f) {
        if (f == null) return null;
        return new FeedingHabitsRecord(f);
    }

    public static FeedingHabitsRecordOB convertFeedingHabitsRecord(FeedingHabitsRecord feedingHabitsRecord) {
        if (feedingHabitsRecord == null) return null;
        return new FeedingHabitsRecordOB(feedingHabitsRecord);
    }

    public static List<FeedingHabitsRecord> listFeedingHabitsRecordToModel(List<FeedingHabitsRecordOB> feedingHabitsRecords) {
        List<FeedingHabitsRecord> lista = new ArrayList<>();
        for (FeedingHabitsRecordOB aux : feedingHabitsRecords) {
            lista.add(new FeedingHabitsRecord(aux));
        }
        return lista;
    }

    public static List<WeeklyFoodRecord> convertListWeeklyFoodRecord(List<WeeklyFoodRecordOB> list) {
        List<WeeklyFoodRecord> lista = new ArrayList<>();
        for (WeeklyFoodRecordOB aux : list) {
            lista.add(new WeeklyFoodRecord(aux));
        }
        return lista;
    }

    public static List<WeeklyFoodRecordOB> listWeeklyFoodRecordToObjectBox(List<WeeklyFoodRecord> list) {
        List<WeeklyFoodRecordOB> lista = new ArrayList<>();
        for (WeeklyFoodRecord aux : list) {
            lista.add(new WeeklyFoodRecordOB(aux));
        }
        return lista;
    }

    // ----------- MEASUREMENT ---------------------

    public static MeasurementOB convertMeasurement(Measurement measurement) {
        if (measurement == null) return null;
        return new MeasurementOB(measurement);
    }

    public static Measurement convertMeasurement(MeasurementOB measurement) {
        return new Measurement(measurement);
    }

    public static List<Measurement> listMeasurementsToModel(List<MeasurementOB> list) {
        List<Measurement> lista = new ArrayList<>();
        for (MeasurementOB aux : list) {
            lista.add(new Measurement(aux));
        }
        return lista;
    }

    public static List<MeasurementOB> listMeasurementsToObjectBox(List<Measurement> list) {
        List<MeasurementOB> lista = new ArrayList<>();
        for (Measurement aux : list) {
            lista.add(new MeasurementOB(aux));
        }
        return lista;
    }

    public static BodyFatOB convertBodyFat(BodyFat bf) {
        if (bf == null) return null;
        return new BodyFatOB(bf);
    }

    public static BodyFat convertBodyFat(BodyFatOB bf) {
        if (bf == null) return null;
        return new BodyFat(bf);
    }

    public static List<HeartRateItemOB> convertListHeartRate(List<HeartRateItem> list) {
        List<HeartRateItemOB> lista = new ArrayList<>();
        if (list == null || list.isEmpty()) return lista;

        for (HeartRateItem aux : list) {
            lista.add(new HeartRateItemOB(aux));
        }
        return lista;
    }

    public static List<HeartRateItem> convertListHeartRateToModel(List<HeartRateItemOB> list) {
        List<HeartRateItem> lista = new ArrayList<>();
        if (list == null || list.isEmpty()) return lista;

        for (HeartRateItemOB aux : list) {
            lista.add(new HeartRateItem(aux));
        }
        return lista;
    }

    // --------------- MEDICAL RECORDS ------------------------

    public static MedicalRecord convertMedicalRecord(MedicalRecordOB medicalRecord) {
        return new MedicalRecord(medicalRecord);
    }

    public static List<MedicalRecord> listMedicalRecordsToModel(List<MedicalRecordOB> medicalRecords) {
        List<MedicalRecord> lista = new ArrayList<>();
        for (MedicalRecordOB aux : medicalRecords) {
            lista.add(new MedicalRecord(aux));
        }
        return lista;
    }

    public static MedicalRecordOB convertMedicalRecord(MedicalRecord medicalRecord) {
        return new MedicalRecordOB(medicalRecord);
    }

    public static ChronicDisease chronicDiseaseToModel(ChronicDiseaseOB chronicDisease) {
        return new ChronicDisease(chronicDisease);
    }

    public static List<ChronicDisease> listChronicsToModel(List<ChronicDiseaseOB> c) {
        List<ChronicDisease> list = new ArrayList<>();
        for (ChronicDiseaseOB aux : c) {
            list.add(new ChronicDisease(aux));
        }
        return list;
    }

    public static List<ChronicDiseaseOB> listChronicsToObjectBox(List<ChronicDisease> c) {
        List<ChronicDiseaseOB> lista = new ArrayList<>();
        for (ChronicDisease aux : c) {
            lista.add(new ChronicDiseaseOB(aux));
        }
        return lista;
    }

    // ------- PATIENT --------------

    public static Patient convertPatient(PatientOB patientOB) {
        if (patientOB == null) return null;
        return new Patient(patientOB);
    }

    public static PatientOB convertPatient(Patient patient) {
        return new PatientOB(patient);
    }

    public static List<Patient> listPatientsToModel(List<PatientOB> patientsOB) {
        List<Patient> list = new ArrayList<>();
        for (PatientOB p : patientsOB) {
            list.add(new Patient(p));
        }
        return list;
    }

    // ---------- PHYSICAL ACTIVITY HABIT -----------------

    public static PhysicalActivityHabitOB convertPhysicalActivityHabit(PhysicalActivityHabit physicalActivityHabit) {
        return new PhysicalActivityHabitOB(physicalActivityHabit);
    }

    public static PhysicalActivityHabit convertPhysicalActivityHabit(PhysicalActivityHabitOB physicalActivityHabit) {
        return new PhysicalActivityHabit(physicalActivityHabit);
    }

    public static List<PhysicalActivityHabit> convertListPhysicalActivityHabit(List<PhysicalActivityHabitOB> physicalActivityHabits) {
        List<PhysicalActivityHabit> lista = new ArrayList<>();
        for (PhysicalActivityHabitOB aux : physicalActivityHabits) {
            lista.add(new PhysicalActivityHabit(aux));
        }
        return lista;
    }

    // -------------- SLEEP HABITS DAO ---------------------

    public static SleepHabitOB convertSleepHabit(SleepHabit sleepHabit) {
        return new SleepHabitOB(sleepHabit);
    }

    public static SleepHabit convertSleepHabit(SleepHabitOB sleepHabit) {
        return new SleepHabit(sleepHabit);
    }

    public static List<SleepHabit> listSleepHabitsToModel(List<SleepHabitOB> sleepHabits) {
        List<SleepHabit> lista = new ArrayList<>();
        for (SleepHabitOB aux : sleepHabits) {
            lista.add(new SleepHabit(aux));
        }
        return lista;
    }

    public static List<NutritionalQuestionnaire> listNutritionalQuestionnaireToModel(List<NutritionalQuestionnaireOB> n) {
        List<NutritionalQuestionnaire> aux = new ArrayList<>();
        for (NutritionalQuestionnaireOB questionnaire : n) {
            aux.add(convertNutritionalQuestionnaire(questionnaire));
        }
        return aux;
    }

    public static NutritionalQuestionnaire convertNutritionalQuestionnaire(NutritionalQuestionnaireOB questionnaire) {
        if (questionnaire == null)
            return null;
        return new NutritionalQuestionnaire(questionnaire);
    }

    public static NutritionalQuestionnaireOB convertNutritionalQuestionnaire(NutritionalQuestionnaire questionnaire) {
        if (questionnaire == null)
            return null;
        return new NutritionalQuestionnaireOB(questionnaire);
    }

    public static SociodemographicRecordOB convertSociodemographicRecord(SociodemographicRecord sociodemographicRecord) {
        return new SociodemographicRecordOB(sociodemographicRecord);
    }

    public static SociodemographicRecord convertSociodemographicRecord(SociodemographicRecordOB sociodemographicRecord) {
        if (sociodemographicRecord == null) return null;
        return new SociodemographicRecord(sociodemographicRecord);
    }

    public static List<OdontologicalQuestionnaire> listOdontologicalQuestionnaireToModel(List<OdontologicalQuestionnaireOB> n) {
        List<OdontologicalQuestionnaire> aux = new ArrayList<>();
        if (n == null || n.isEmpty()) return aux;

        for (OdontologicalQuestionnaireOB questionnaire : n)
            aux.add(convertOdontologicalQuestionnaire(questionnaire));
        return aux;
    }

    private static OdontologicalQuestionnaire convertOdontologicalQuestionnaire(OdontologicalQuestionnaireOB questionnaire) {
        if (questionnaire == null) return null;
        return new OdontologicalQuestionnaire(questionnaire);
    }

    public static FamilyCohesionRecordOB convertFamilyCohesionRecord(FamilyCohesionRecord f) {
        if (f == null) return null;
        return new FamilyCohesionRecordOB(f);
    }

    public static FamilyCohesionRecord convertFamilyCohesionRecord(FamilyCohesionRecordOB f) {
        if (f == null) return null;
        return new FamilyCohesionRecord(f);
    }

    public static OralHealthRecordOB convertOralHealthRecord(OralHealthRecord o) {
        if (o == null) return null;
        return new OralHealthRecordOB(o);
    }

    public static OralHealthRecord convertOralHealthRecord(OralHealthRecordOB o) {
        if (o == null) return null;
        return new OralHealthRecord(o);
    }

    public static List<ToothLesionOB> listToothLesionsToObjectBox(List<ToothLesion> toothLesions) {
        List<ToothLesionOB> aux = new ArrayList<>();
        if (toothLesions == null || toothLesions.isEmpty()) return aux;

        for (ToothLesion t : toothLesions) {
            aux.add(new ToothLesionOB(t));
        }
        return aux;
    }

    public static List<ToothLesion> convertListToothLesionToModel(List<ToothLesionOB> toothLesions) {
        List<ToothLesion> aux = new ArrayList<>();
        if (toothLesions == null || toothLesions.isEmpty()) return aux;

        for (ToothLesionOB t : toothLesions) {
            aux.add(new ToothLesion(t));
        }
        return aux;
    }

    public static List<BodyFatOB> convertListBodyFatToObjectBox(List<BodyFat> bodyFat) {
        List<BodyFatOB> aux = new ArrayList<>();
        if (bodyFat == null || bodyFat.isEmpty()) return aux;

        for (BodyFat bf : bodyFat) {
            aux.add(new BodyFatOB(bf));
        }
        return aux;
    }

    public static List<BodyFat> convertListBodyFatToModel(List<BodyFatOB> bodyFat) {
        List<BodyFat> aux = new ArrayList<>();
        if (bodyFat == null || bodyFat.isEmpty()) return aux;

        for (BodyFatOB bf : bodyFat)
            aux.add(new BodyFat(bf));
        return aux;
    }
}
