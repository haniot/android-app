package br.edu.uepb.nutes.haniot.data;

//import br.edu.uepb.nutes.haniot.data.model.Device;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.data.model.objectbox.Patient;
import br.edu.uepb.nutes.haniot.data.model.objectbox.MedicalRecord;
import br.edu.uepb.nutes.haniot.data.model.objectbox.Measurement;
import br.edu.uepb.nutes.haniot.data.model.objectbox.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.objectbox.Device;
import br.edu.uepb.nutes.haniot.data.model.objectbox.PhysicalActivityHabit;
import br.edu.uepb.nutes.haniot.data.model.objectbox.PilotStudy;
import br.edu.uepb.nutes.haniot.data.model.objectbox.SleepHabit;
import br.edu.uepb.nutes.haniot.data.model.objectbox.User;

public class Convert {

    // ------------- DEVICE ---------------------

    public static Device deviceToObjectBox(br.edu.uepb.nutes.haniot.data.model.model.Device d) {
        return new Device(d);
    }

    public static br.edu.uepb.nutes.haniot.data.model.model.Device deviceToModel(Device d) {
        return new br.edu.uepb.nutes.haniot.data.model.model.Device(d);
    }

    public static List<br.edu.uepb.nutes.haniot.data.model.model.Device> listDeviceToModel(List<Device> devices) {
        List<br.edu.uepb.nutes.haniot.data.model.model.Device> list = new ArrayList<>();
        for (Device d : devices) {
            list.add(Convert.deviceToModel(d));
        }
        return list;
    }

    // ------------- FEEDING HABITS RECORD ----------------------------

    public static br.edu.uepb.nutes.haniot.data.model.model.FeedingHabitsRecord feedingHabitsRecordToModel(FeedingHabitsRecord f) {
        return null;
    }

    public static List<br.edu.uepb.nutes.haniot.data.model.model.FeedingHabitsRecord> listFeedingHabitsRecordToModel(List<FeedingHabitsRecord> feedingHabitsRecords) {
        return null;
    }

    public static FeedingHabitsRecord feedingHabitsRecordToObjectBox(br.edu.uepb.nutes.haniot.data.model.model.FeedingHabitsRecord feedingHabitsRecord) {
        return null;
    }

    // ----------- MEASUREMENT ---------------------

    public static Measurement measurementToObjectBox(br.edu.uepb.nutes.haniot.data.model.model.Measurement measurement) {
        return null;
    }

    public static br.edu.uepb.nutes.haniot.data.model.model.Measurement measurementToModel(Measurement measurement) {
        return null;
    }

    public static List<br.edu.uepb.nutes.haniot.data.model.model.Measurement> listMeasurementsToModel(List<Measurement> list) {
        return null;
    }

    // --------------- MEDICAL RECORDS ------------------------

    public static br.edu.uepb.nutes.haniot.data.model.model.MedicalRecord medicalRecordToModel(MedicalRecord m) {
        return null;
    }

    public static List<br.edu.uepb.nutes.haniot.data.model.model.MedicalRecord> listMedicalRecordsToModel(List<MedicalRecord> medicalRecords) {
        return null;
    }

    public static MedicalRecord medicalRecordToObjectBox(br.edu.uepb.nutes.haniot.data.model.model.MedicalRecord medicalRecord) {
        return null;
    }

    // ------- PATIENT --------------

    public static br.edu.uepb.nutes.haniot.data.model.model.Patient patientToModel(Patient patient) {
        return null;
    }

    public static Patient patientToObjectBox(br.edu.uepb.nutes.haniot.data.model.model.Patient patient) {
        return null;
    }

    public static List<br.edu.uepb.nutes.haniot.data.model.model.Patient> listPatientsToModel(List<Patient> list) {
        return null;
    }

    // ---------- PHYSICAL ACTIVITY HABIT -----------------

    public static PhysicalActivityHabit physicalActivityHabitToObjectBox(br.edu.uepb.nutes.haniot.data.model.model.PhysicalActivityHabit physicalActivityHabit) {
        return null;
    }

    public static br.edu.uepb.nutes.haniot.data.model.model.PhysicalActivityHabit physicalActivityHabitToModel(PhysicalActivityHabit fromPatientId) {
        return null;
    }

    public static List<br.edu.uepb.nutes.haniot.data.model.model.PhysicalActivityHabit> listPhysicalActivityHabitToModel(List<PhysicalActivityHabit> physicalActivityHabits) {
        return null;
    }

    // ----------- PILOT STUDY ---------------------------

    public static PilotStudy pilotStudyToObjectBox(br.edu.uepb.nutes.haniot.data.model.model.PilotStudy pilotStudy) {
        return null;
    }

    public static br.edu.uepb.nutes.haniot.data.model.model.PilotStudy pilotStudyToModel(PilotStudy pilotStudy) {
        return null;
    }

    public static List<br.edu.uepb.nutes.haniot.data.model.model.PilotStudy> listPilotStudiesToModel(List<PilotStudy> list) {
        return null;
    }

    // -------------- SLEEP HABITS DAO ---------------------

    public static SleepHabit sleepHabitsToObjectBox(br.edu.uepb.nutes.haniot.data.model.model.SleepHabit sleepHabit) {
        return null;
    }

    public static List<br.edu.uepb.nutes.haniot.data.model.model.SleepHabit> listSleepHabitsToModel(List<SleepHabit> sleepHabits) {
        return null;
    }

    public static br.edu.uepb.nutes.haniot.data.model.model.SleepHabit sleepHabitsToModel(SleepHabit fromPatientId) {
        return null;
    }

    public static User userToObjectBox(br.edu.uepb.nutes.haniot.data.model.model.User user) {
        return null;
    }

    public static br.edu.uepb.nutes.haniot.data.model.model.User userToModel(User user) {
        return null;
    }
}
