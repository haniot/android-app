package br.edu.uepb.nutes.haniot.data;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.data.model.model.BodyFat;
import br.edu.uepb.nutes.haniot.data.model.model.ChronicDisease;
import br.edu.uepb.nutes.haniot.data.model.model.Device;
import br.edu.uepb.nutes.haniot.data.model.model.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.model.HeartRateItem;
import br.edu.uepb.nutes.haniot.data.model.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.model.MedicalRecord;
import br.edu.uepb.nutes.haniot.data.model.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.model.PhysicalActivityHabit;
import br.edu.uepb.nutes.haniot.data.model.model.PilotStudy;
import br.edu.uepb.nutes.haniot.data.model.model.SleepHabit;
import br.edu.uepb.nutes.haniot.data.model.model.User;
import br.edu.uepb.nutes.haniot.data.model.objectbox.BodyFatOB;
import br.edu.uepb.nutes.haniot.data.model.objectbox.ChronicDiseaseOB;
import br.edu.uepb.nutes.haniot.data.model.objectbox.DeviceOB;
import br.edu.uepb.nutes.haniot.data.model.objectbox.FeedingHabitsRecordOB;
import br.edu.uepb.nutes.haniot.data.model.objectbox.HeartRateItemOB;
import br.edu.uepb.nutes.haniot.data.model.objectbox.MeasurementOB;
import br.edu.uepb.nutes.haniot.data.model.objectbox.MedicalRecordOB;
import br.edu.uepb.nutes.haniot.data.model.objectbox.MedicalRecordOB_;
import br.edu.uepb.nutes.haniot.data.model.objectbox.PatientOB;
import br.edu.uepb.nutes.haniot.data.model.objectbox.PhysicalActivityHabitOB;
import br.edu.uepb.nutes.haniot.data.model.objectbox.PilotStudyOB;
import br.edu.uepb.nutes.haniot.data.model.objectbox.SleepHabitOB;
import br.edu.uepb.nutes.haniot.data.model.objectbox.UserOB;
import io.objectbox.relation.ToMany;

public class Convert {

    // ------------- DEVICE ---------------------

    public static DeviceOB deviceToObjectBox(Device d) {
        return new DeviceOB(d);
    }

    public static Device deviceToModel(DeviceOB d) {
        return new Device(d);
    }

    public static List<Device> listDeviceToModel(List<DeviceOB> devices) {
        List<Device> list = new ArrayList<>();
        for (DeviceOB d : devices) {
            list.add(Convert.deviceToModel(d));
        }
        return list;
    }

    // ------------- FEEDING HABITS RECORD ----------------------------

    public static FeedingHabitsRecord feedingHabitsRecordToModel(FeedingHabitsRecordOB f) {
        return null;
    }

    public static List<FeedingHabitsRecord> listFeedingHabitsRecordToModel(List<FeedingHabitsRecordOB> feedingHabitsRecords) {
        return null;
    }

    public static FeedingHabitsRecordOB feedingHabitsRecordToObjectBox(FeedingHabitsRecord feedingHabitsRecord) {
        return null;
    }

    // ----------- MEASUREMENT ---------------------

    public static MeasurementOB measurementToObjectBox(Measurement measurement) {
        return new MeasurementOB(measurement);
    }

    public static Measurement measurementToModel(MeasurementOB measurement) {
        return new Measurement(measurement);
    }

    public static List<Measurement> listMeasurementsToModel(List<MeasurementOB> list) {
        List<Measurement> lista = new ArrayList<>();
        for(MeasurementOB aux : list) {
            lista.add(new Measurement(aux));
        }
        return lista;
    }

    public static BodyFatOB bodyFatToObjectBox(BodyFat bf) {
        return new BodyFatOB(bf);
    }

    public static List<HeartRateItemOB> listHeartRateToObjectBox(List<HeartRateItem> dataset) {
        List<HeartRateItemOB> lista = new ArrayList<>();
        for (HeartRateItem aux : dataset) {
            lista.add(new HeartRateItemOB(aux));
        }
        return lista;
    }

    // --------------- MEDICAL RECORDS ------------------------

    public static MedicalRecord medicalRecordToModel(MedicalRecordOB medicalRecord) {
        return new MedicalRecord(medicalRecord);
    }

    public static List<MedicalRecord> listMedicalRecordsToModel(List<MedicalRecordOB> medicalRecords) {
        return null;
    }

    public static MedicalRecordOB medicalRecordToObjectBox(MedicalRecord medicalRecord) {
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

    public static ToMany<ChronicDiseaseOB> listChronicsToObjectBox(MedicalRecordOB medicalRecordOB, List<ChronicDisease> c) {
        ToMany<ChronicDiseaseOB> lista = new ToMany<>(medicalRecordOB, MedicalRecordOB_.chronicDiseases);
        for (ChronicDisease aux: c) {
            lista.add(new ChronicDiseaseOB(aux));
        }
        return lista;
    }

    // ------- PATIENT --------------

    public static Patient patientToModel(PatientOB patientOB) {
        return new Patient(patientOB);
    }

    public static PatientOB patientToObjectBox(Patient patient) {
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

    public static PhysicalActivityHabitOB physicalActivityHabitToObjectBox(PhysicalActivityHabit physicalActivityHabit) {
        return null;
    }

    public static PhysicalActivityHabit physicalActivityHabitToModel(PhysicalActivityHabitOB fromPatientId) {
        return null;
    }

    public static List<PhysicalActivityHabit> listPhysicalActivityHabitToModel(List<PhysicalActivityHabitOB> physicalActivityHabits) {
        return null;
    }

    // ----------- PILOT STUDY ---------------------------

    public static PilotStudyOB pilotStudyToObjectBox(PilotStudy pilotStudy) {
        return new PilotStudyOB(pilotStudy);
    }

    public static PilotStudy pilotStudyToModel(PilotStudyOB pilotStudy) {
        return new PilotStudy(pilotStudy);
    }

    public static List<PilotStudy> listPilotStudiesToModel(List<PilotStudyOB> list) {
        List<PilotStudy> lista = new ArrayList<>();
        for (PilotStudyOB aux : list) {
            lista.add(new PilotStudy(aux));
        }
        return lista;
    }

    // -------------- SLEEP HABITS DAO ---------------------

    public static SleepHabitOB sleepHabitsToObjectBox(SleepHabit sleepHabit) {
        return new SleepHabitOB(sleepHabit);
    }

    public static List<SleepHabit> listSleepHabitsToModel(List<SleepHabitOB> sleepHabits) {
        return null;
    }

    public static SleepHabit sleepHabitsToModel(SleepHabitOB fromPatientId) {
        return null;
    }

    public static UserOB userToObjectBox(User user) {
        return null;
    }

    public static User userToModel(UserOB user) {
        return null;
    }
}
