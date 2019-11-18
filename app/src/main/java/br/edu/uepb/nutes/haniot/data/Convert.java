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

public class Convert {

    // ------------- DEVICE ---------------------

    public static DeviceOB convertDevice(Device d) {
        if (d == null) return null;

        DeviceOB device = new DeviceOB();
        device.setId(d.getId());
        device.set_id(d.get_id());
        device.setName(d.getName());
        device.setAddress(d.getAddress());
        device.setType(d.getType());
        device.setModelNumber(d.getModelNumber());
        device.setManufacturer(d.getManufacturer());
        device.setUserId(d.getUserId());
        device.setUuid(d.getUuid());
        device.setImg(d.getImg());
        return device;
    }

    public static Device convertDevice(DeviceOB d) {
        if (d == null) return null;

        Device device = new Device();
        device.setId(d.getId());
        device.set_id(d.get_id());
        device.setName(d.getName());
        device.setAddress(d.getAddress());
        device.setType(d.getType());
        device.setModelNumber(d.getModelNumber());
        device.setManufacturer(d.getManufacturer());
        device.setUserId(d.getUserId());
        device.setUuid(d.getUuid());
        device.setImg(d.getImg());
        return device;
    }

    public static List<Device> listDeviceToModel(List<DeviceOB> devices) {
        List<Device> list = new ArrayList<>();
        if (devices == null || devices.isEmpty()) return list;

        for (DeviceOB d : devices) {
            list.add(Convert.convertDevice(d));
        }
        return list;
    }

    // ------------- FEEDING HABITS RECORD ----------------------------

    public static FeedingHabitsRecord convertFeedingHabitsRecord(FeedingHabitsRecordOB f) {
        if (f == null) return null;

        FeedingHabitsRecord fh = new FeedingHabitsRecord();
        fh.setId(f.getId());
        fh.set_id(f.get_id());
        fh.setWeeklyFeedingHabits(Convert.convertListWeeklyFoodRecord(f.getWeeklyFeedingHabits()));
        fh.setDailyWaterGlasses(f.getDailyWaterGlasses());
        fh.setSixMonthBreastFeeding(f.getSixMonthBreastFeeding());
        fh.setFoodAllergyIntolerance(f.getFoodAllergyIntolerance());
        fh.setBreakfastDailyFrequency(f.getBreakfastDailyFrequency());
        return fh;
    }

    public static FeedingHabitsRecordOB convertFeedingHabitsRecord(FeedingHabitsRecord f) {
        if (f == null) return null;

        FeedingHabitsRecordOB fh = new FeedingHabitsRecordOB();
        fh.setId(f.getId());
        fh.set_id(f.get_id());
        fh.setDailyWaterGlasses(f.getDailyWaterGlasses());
        fh.setSixMonthBreastFeeding(f.getSixMonthBreastFeeding());
        fh.setFoodAllergyIntolerance(f.getFoodAllergyIntolerance());
        fh.setBreakfastDailyFrequency(f.getBreakfastDailyFrequency());
        fh.setWeeklyFeedingHabits((
                br.edu.uepb.nutes.haniot.data.Convert.listWeeklyFoodRecordToObjectBox(f.getWeeklyFeedingHabits())));
        return fh;
    }

    public static List<FeedingHabitsRecord> listFeedingHabitsRecordToModel(List<FeedingHabitsRecordOB> feedingHabitsRecords) {
        List<FeedingHabitsRecord> lista = new ArrayList<>();
        if (feedingHabitsRecords == null || feedingHabitsRecords.isEmpty()) return lista;

        for (FeedingHabitsRecordOB aux : feedingHabitsRecords) {
            lista.add(Convert.convertFeedingHabitsRecord(aux));
        }
        return lista;
    }

    public static List<WeeklyFoodRecord> convertListWeeklyFoodRecord(List<WeeklyFoodRecordOB> list) {
        List<WeeklyFoodRecord> lista = new ArrayList<>();
        if (list == null || list.isEmpty()) return lista;

        for (WeeklyFoodRecordOB aux : list) {
            lista.add(Convert.convertWeeklyFoodRecord(aux));
        }
        return lista;
    }

    public static WeeklyFoodRecord convertWeeklyFoodRecord(WeeklyFoodRecordOB wf) {
        if (wf == null) return null;

        WeeklyFoodRecord w = new WeeklyFoodRecord();
        w.setId(wf.getId());
        w.setFood(wf.getFood());
        w.setSevenDaysFreq(wf.getSevenDaysFreq());
        return w;
    }

    public static List<WeeklyFoodRecordOB> listWeeklyFoodRecordToObjectBox(List<WeeklyFoodRecord> list) {
        List<WeeklyFoodRecordOB> lista = new ArrayList<>();
        if (list == null || list.isEmpty()) return lista;

        for (WeeklyFoodRecord aux : list) {
            lista.add(Convert.convertWeeklyFoodRecord(aux));
        }
        return lista;
    }

    public static WeeklyFoodRecordOB convertWeeklyFoodRecord(WeeklyFoodRecord wf) {
        if (wf == null) return null;

        WeeklyFoodRecordOB w = new WeeklyFoodRecordOB();
        w.setId(wf.getId());
        w.setFood(wf.getFood());
        w.setSevenDaysFreq(wf.getSevenDaysFreq());
        return w;
    }

    // ----------- MEASUREMENT ---------------------

    public static MeasurementOB convertMeasurement(Measurement measurement) {
        if (measurement == null) return null;

        MeasurementOB m = new MeasurementOB();
        m.set_id(measurement.get_id());
        m.setId(measurement.getId());
        m.setValue(measurement.getValue());
        m.setUnit(measurement.getUnit());
        m.setType(measurement.getType());
        m.setTimestamp(measurement.getTimestamp());

        m.setUser_id(measurement.getUser_id());
        m.setUserId(measurement.getUserId());

        m.setDeviceId(measurement.getDeviceId());
        m.setSystolic(measurement.getSystolic());
        m.setDiastolic(measurement.getDiastolic());
        m.setPulse(measurement.getPulse());
        m.setMeal(measurement.getMeal());

        if (measurement.getFat() != null)
            m.setFatModel(Convert.convertBodyFat(measurement.getFat()));

        m.setBodyFat(Convert.convertListBodyFatToObjectBox(measurement.getBodyFat()));
        m.setDataset(Convert.convertListHeartRateToObjectBox(measurement.getDataset()));
        m.setSync(measurement.isSync());
        return m;
    }

    public static Measurement convertMeasurement(MeasurementOB measurement) {
        if (measurement == null) return null;

        Measurement m = new Measurement();
        m.set_id(measurement.get_id());
        m.setId(measurement.getId());
        m.setValue(measurement.getValue());
        m.setUnit(measurement.getUnit());
        m.setType(measurement.getType());
        m.setTimestamp(measurement.getTimestamp());

        m.setUser_id(measurement.getUser_id());
        m.setUserId(measurement.getUserId());

        m.setDeviceId(measurement.getDeviceId());
        m.setSystolic(measurement.getSystolic());
        m.setDiastolic(measurement.getDiastolic());
        m.setPulse(measurement.getPulse());
        m.setMeal(measurement.getMeal());

        if (measurement.getFat() != null && measurement.getFat().getTarget() != null)
            m.setFat(Convert.convertBodyFat(measurement.getFat().getTarget()));

        m.setBodyFat(Convert.convertListBodyFatToModel(measurement.getBodyFat()));
        m.setDataset(Convert.convertListHeartRateToModel(measurement.getDataset()));
        m.setSync(measurement.isSync());
        return m;
    }

    public static List<Measurement> listMeasurementsToModel(List<MeasurementOB> list) {
        List<Measurement> lista = new ArrayList<>();
        if (list == null || list.isEmpty()) return lista;

        for (MeasurementOB aux : list) {
            lista.add(Convert.convertMeasurement(aux));
        }
        return lista;
    }

    public static List<MeasurementOB> listMeasurementsToObjectBox(List<Measurement> list) {
        List<MeasurementOB> lista = new ArrayList<>();
        if (list == null || list.isEmpty()) return lista;

        for (Measurement aux : list) {
            lista.add(Convert.convertMeasurement(aux));
        }
        return lista;
    }

    public static BodyFatOB convertBodyFat(BodyFat bf) {
        if (bf == null) return null;

        BodyFatOB b = new BodyFatOB();
        b.setId(bf.getId());
        b.setValue(bf.getValue());
        b.setUnit(bf.getUnit());
        b.setTimestamp(bf.getTimestamp());
        return b;
    }

    public static BodyFat convertBodyFat(BodyFatOB bf) {
        if (bf == null) return null;

        BodyFat b = new BodyFat();
        b.setId(bf.getId());
        b.setValue(bf.getValue());
        b.setUnit(bf.getUnit());
        b.setTimestamp(bf.getTimestamp());
        return b;
    }

    public static List<HeartRateItemOB> convertListHeartRateToObjectBox(List<HeartRateItem> list) {
        List<HeartRateItemOB> lista = new ArrayList<>();
        if (list == null || list.isEmpty()) return lista;

        for (HeartRateItem aux : list) {
            lista.add(Convert.convertHeartRateItem(aux));
        }
        return lista;
    }

    private static HeartRateItemOB convertHeartRateItem(HeartRateItem h) {
        if (h == null) return null;

        HeartRateItemOB hr = new HeartRateItemOB();
        hr.setId(h.getId());
        hr.setValue(h.getValue());
        hr.setTimestamp(h.getTimestamp());
        return hr;
    }

    public static List<HeartRateItem> convertListHeartRateToModel(List<HeartRateItemOB> list) {
        List<HeartRateItem> lista = new ArrayList<>();
        if (list == null || list.isEmpty()) return lista;

        for (HeartRateItemOB aux : list) {
            lista.add(Convert.convertHeartRateItem(aux));
        }
        return lista;
    }

    public static HeartRateItem convertHeartRateItem(HeartRateItemOB h) {
        if (h == null) return null;

        HeartRateItem hr = new HeartRateItem();
        hr.setId(h.getId());
        hr.setValue(h.getValue());
        hr.setTimestamp(h.getTimestamp());
        return hr;
    }

    // --------------- MEDICAL RECORDS ------------------------

    public static MedicalRecord convertMedicalRecord(MedicalRecordOB medicalRecord) {
        if (medicalRecord == null) return null;

        MedicalRecord m = new MedicalRecord();
        m.setId(medicalRecord.getId());
        m.set_id(medicalRecord.get_id());
        m.setChronicDiseases(Convert.listChronicsToModel(medicalRecord.getChronicDiseases()));
        return m;
    }

    public static List<MedicalRecord> listMedicalRecordsToModel(List<MedicalRecordOB> medicalRecords) {
        List<MedicalRecord> lista = new ArrayList<>();
        if (medicalRecords == null || medicalRecords.isEmpty()) return lista;

        for (MedicalRecordOB aux : medicalRecords) {
            lista.add(Convert.convertMedicalRecord(aux));
        }
        return lista;
    }

    public static MedicalRecordOB convertMedicalRecord(MedicalRecord medicalRecord) {
        if (medicalRecord == null) return null;

        MedicalRecordOB m = new MedicalRecordOB();
        m.setId(medicalRecord.getId());
        m.set_id(medicalRecord.get_id());
        m.setChronicDiseases(Convert.listChronicsToObjectBox(medicalRecord.getChronicDiseases()));
        return m;
    }

    public static List<ChronicDisease> listChronicsToModel(List<ChronicDiseaseOB> c) {
        List<ChronicDisease> list = new ArrayList<>();
        if (c == null || c.isEmpty()) return null;

        for (ChronicDiseaseOB aux : c) {
            list.add(Convert.convertChronicDisease(aux));
        }
        return list;
    }

    public static ChronicDisease convertChronicDisease(ChronicDiseaseOB cd) {
        if (cd == null) return null;

        ChronicDisease c = new ChronicDisease();
        c.setId(c.getId());
        c.setType(c.getType());
        c.setDiseaseHistory(c.getDiseaseHistory());
        return c;
    }

    public static List<ChronicDiseaseOB> listChronicsToObjectBox(List<ChronicDisease> c) {
        List<ChronicDiseaseOB> lista = new ArrayList<>();
        if (c == null || c.isEmpty()) return lista;

        for (ChronicDisease aux : c) {
            lista.add(Convert.convertChronicDisease(aux));
        }
        return lista;
    }

    public static ChronicDiseaseOB convertChronicDisease(ChronicDisease cd) {
        if (cd == null) return null;

        ChronicDiseaseOB c = new ChronicDiseaseOB();
        c.setId(cd.getId());
        c.setType(cd.getType());
        c.setDiseaseHistory(cd.getDiseaseHistory());
        return c;
    }

    // ------- PATIENT --------------

    public static Patient convertPatient(PatientOB patientOB) {
        if (patientOB == null) return null;

        Patient p = new Patient();
        p.setId(patientOB.getId());
        p.set_id(patientOB.get_id());
        p.setEmail(patientOB.getEmail());
        p.setName(patientOB.getName());
        p.setBirthDate(patientOB.getBirthDate());
        p.setHealthArea(patientOB.getHealthArea());
        p.setPassword(patientOB.getPassword());
        p.setOldPassword(patientOB.getOldPassword());
        p.setNewPassword(patientOB.getNewPassword());
        p.setPhoneNumber(patientOB.getPhoneNumber());
        p.setLastLogin(patientOB.getLastLogin());
        p.setLastSync(patientOB.getLastSync());
        p.setLanguage(patientOB.getLanguage());
        p.setPilotStudyIDSelected(patientOB.getPilotStudyIDSelected());
        p.setUserType(patientOB.getUserType());
        p.setPilotId(patientOB.getPilotId());
        p.setGender(patientOB.getGender());
        p.setHealthProfessionalId(patientOB.getHealthProfessionalId());
        p.setCreatedAt(patientOB.getCreatedAt());
        p.setSync(patientOB.isSync());

        return p;
    }

    public static PatientOB convertPatient(Patient patient) {
        if (patient == null) return null;

        PatientOB p = new PatientOB();
        p.setId(patient.getId());
        p.set_id(patient.get_id());
        p.setEmail(patient.getEmail());
        p.setName(patient.getName());
        p.setBirthDate(patient.getBirthDate());
        p.setHealthArea(patient.getHealthArea());
        p.setPassword(patient.getPassword());
        p.setOldPassword(patient.getOldPassword());
        p.setNewPassword(patient.getNewPassword());
        p.setPhoneNumber(patient.getPhoneNumber());
        p.setLastLogin(patient.getLastLogin());
        p.setLastSync(patient.getLastSync());
        p.setLanguage(patient.getLanguage());
        p.setPilotStudyIDSelected(patient.getPilotStudyIDSelected());
        p.setUserType(patient.getUserType());
        p.setPilotId(patient.getPilotId());
        p.setGender(patient.getGender());
        p.setHealthProfessionalId(patient.getHealthProfessionalId());
        p.setCreatedAt(patient.getCreatedAt());
        p.setSync(patient.isSync());

        return p;
    }

    public static List<Patient> listPatientsToModel(List<PatientOB> patientsOB) {
        List<Patient> list = new ArrayList<>();
        if (patientsOB == null || patientsOB.isEmpty()) return list;

        for (PatientOB p : patientsOB) {
            list.add(Convert.convertPatient(p));
        }
        return list;
    }

    // ---------- PHYSICAL ACTIVITY HABIT -----------------

    public static PhysicalActivityHabitOB convertPhysicalActivityHabit(PhysicalActivityHabit p) {
        if (p == null) return null;

        PhysicalActivityHabitOB pa = new PhysicalActivityHabitOB();
        pa.setId(p.getId());
        pa.set_id(p.get_id());
        pa.setSchoolActivityFreq(p.getSchoolActivityFreq());
        pa.setWeeklyActivities(p.getWeeklyActivities());
        return pa;
    }

    public static PhysicalActivityHabit convertPhysicalActivityHabit(PhysicalActivityHabitOB p) {
        if (p == null) return null;

        PhysicalActivityHabit pa = new PhysicalActivityHabit();
        pa.setId(p.getId());
        pa.set_id(p.get_id());
        pa.setSchoolActivityFreq(p.getSchoolActivityFreq());
        pa.setWeeklyActivities(p.getWeeklyActivities());
        return pa;
    }

    public static List<PhysicalActivityHabit> convertListPhysicalActivityHabit(List<PhysicalActivityHabitOB> physicalActivityHabits) {
        List<PhysicalActivityHabit> lista = new ArrayList<>();
        if (physicalActivityHabits == null || physicalActivityHabits.isEmpty()) return lista;

        for (PhysicalActivityHabitOB aux : physicalActivityHabits) {
            lista.add(Convert.convertPhysicalActivityHabit(aux));
        }
        return lista;
    }

    // -------------- SLEEP HABITS DAO ---------------------

    public static SleepHabitOB convertSleepHabit(SleepHabit s) {
        if (s == null) return null;

        SleepHabitOB sh = new SleepHabitOB();
        sh.setId(s.getId());
        sh.set_id(s.get_id());
        sh.setWeekDaySleep(s.getWeekDaySleep());
        sh.setWeekDayWakeUp(s.getWeekDayWakeUp());
        return sh;
    }

    public static SleepHabit convertSleepHabit(SleepHabitOB s) {
        if (s == null) return null;

        SleepHabit sh = new SleepHabit();
        sh.setId(s.getId());
        sh.set_id(s.get_id());
        sh.setWeekDaySleep(s.getWeekDaySleep());
        sh.setWeekDayWakeUp(s.getWeekDayWakeUp());
        return sh;
    }

    public static List<SleepHabit> listSleepHabitsToModel(List<SleepHabitOB> sleepHabits) {
        List<SleepHabit> lista = new ArrayList<>();
        if (sleepHabits == null || sleepHabits.isEmpty()) return lista;

        for (SleepHabitOB aux : sleepHabits) {
            lista.add(Convert.convertSleepHabit(aux));
        }
        return lista;
    }

    public static List<NutritionalQuestionnaire> listNutritionalQuestionnaireToModel(List<NutritionalQuestionnaireOB> n) {
        List<NutritionalQuestionnaire> aux = new ArrayList<>();
        if (n == null || n.isEmpty()) return aux;

        for (NutritionalQuestionnaireOB questionnaire : n) {
            aux.add(convertNutritionalQuestionnaire(questionnaire));
        }
        return aux;
    }

    public static NutritionalQuestionnaire convertNutritionalQuestionnaire(NutritionalQuestionnaireOB n) {
        if (n == null) return null;

        NutritionalQuestionnaire nq = new NutritionalQuestionnaire();
        nq.setSync(n.isSync());
        nq.setId(n.getId());
        nq.set_id(n.get_id());

        nq.setPatient_id(n.getPatient_id());
        nq.setPatientId(n.getPatientId());

        nq.setCreatedAt(n.getCreatedAt());
        nq.setSleepHabit(Convert.convertSleepHabit(n.getSleepHabit()));
        nq.setPhysicalActivityHabit(Convert.convertPhysicalActivityHabit(n.getPhysicalActivityHabit()));
        nq.setFeedingHabitsRecord(Convert.convertFeedingHabitsRecord(n.getFeedingHabitsRecord()));
        nq.setMedicalRecord(Convert.convertMedicalRecord(n.getMedicalRecord()));
        return nq;
    }

    public static NutritionalQuestionnaireOB convertNutritionalQuestionnaire(NutritionalQuestionnaire n) {
        if (n == null) return null;

        NutritionalQuestionnaireOB nq = new NutritionalQuestionnaireOB();
        nq.setSync(n.isSync());
        nq.setId(n.getId());
        nq.set_id(n.get_id());

        nq.setPatient_id(n.getPatient_id());
        nq.setPatientId(n.getPatientId());

        nq.setCreatedAt(n.getCreatedAt());
        nq.setSleepHabit(Convert.convertSleepHabit(n.getSleepHabit()));
        nq.setPhysicalActivityHabit(Convert.convertPhysicalActivityHabit(n.getPhysicalActivityHabit()));
        nq.setFeedingHabitsRecord(Convert.convertFeedingHabitsRecord(n.getFeedingHabitsRecord()));
        nq.setMedicalRecord(Convert.convertMedicalRecord(n.getMedicalRecord()));
        return nq;
    }

    public static SociodemographicRecordOB convertSociodemographicRecord(SociodemographicRecord s) {
        if (s == null) return null;

        SociodemographicRecordOB sr = new SociodemographicRecordOB();
        sr.setId(s.getId());
        sr.set_id(s.get_id());
        sr.setColorRace(s.getColorRace());
        sr.setMotherScholarity(s.getMotherScholarity());
        sr.setPeopleInHome(s.getPeopleInHome());
        return sr;
    }

    public static SociodemographicRecord convertSociodemographicRecord(SociodemographicRecordOB s) {
        if (s == null) return null;

        SociodemographicRecord sr = new SociodemographicRecord();
        sr.setId(s.getId());
        sr.set_id(s.get_id());
        sr.setColorRace(s.getColorRace());
        sr.setMotherScholarity(s.getMotherScholarity());
        sr.setPeopleInHome(s.getPeopleInHome());
        return sr;
    }

    public static List<OdontologicalQuestionnaire> listOdontologicalQuestionnaireToModel(List<OdontologicalQuestionnaireOB> n) {
        List<OdontologicalQuestionnaire> aux = new ArrayList<>();
        if (n == null || n.isEmpty()) return aux;

        for (OdontologicalQuestionnaireOB questionnaire : n)
            aux.add(convertOdontologicalQuestionnaire(questionnaire));
        return aux;
    }

    public static OdontologicalQuestionnaire convertOdontologicalQuestionnaire(OdontologicalQuestionnaireOB o) {
        if (o == null) return null;

        OdontologicalQuestionnaire oq = new OdontologicalQuestionnaire();
        oq.setId(o.getId());
        oq.set_id(o.get_id());
        oq.setCreatedAt(o.getCreatedAt());

        oq.setPatient_id(o.getPatient_id());
        oq.setPatientId(o.getPatientId());

        oq.setSociodemographicRecord(Convert.convertSociodemographicRecord(o.getSociodemographicRecord()));
        oq.setFamilyCohesionRecord(Convert.convertFamilyCohesionRecord(o.getFamilyCohesionRecord()));
        oq.setOralHealthRecord(Convert.convertOralHealthRecord(o.getOralHealthRecord()));
        return oq;
    }

    public static FamilyCohesionRecordOB convertFamilyCohesionRecord(FamilyCohesionRecord f) {
        if (f == null) return null;

        FamilyCohesionRecordOB fc = new FamilyCohesionRecordOB();
        fc.setId(f.getId());
        fc.set_id(f.get_id());

        fc.setFamilyMutualAidFreq(f.getFamilyMutualAidFreq());
        fc.setFriendshipApprovalFreq(f.getFriendshipApprovalFreq());
        fc.setFamilyOnlyTaskFreq(f.getFamilyOnlyTaskFreq());
        fc.setFamilyOnlyPreferenceFreq(f.getFamilyOnlyPreferenceFreq());
        fc.setFreeTimeTogetherFreq(f.getFreeTimeTogetherFreq());
        fc.setFamilyProximityPerceptionFreq(f.getFamilyProximityPerceptionFreq());
        fc.setAllFamilyTasksFreq(f.getAllFamilyTasksFreq());
        fc.setFamilyTasksOpportunityFreq(f.getFamilyTasksOpportunityFreq());
        fc.setFamilyDecisionSupportFreq(f.getFamilyDecisionSupportFreq());
        fc.setFamilyUnionRelevanceFreq(f.getFamilyUnionRelevanceFreq());
        fc.setFamilyCohesionResult(f.getFamilyCohesionResult());
        return fc;
    }

    public static FamilyCohesionRecord convertFamilyCohesionRecord(FamilyCohesionRecordOB f) {
        if (f == null) return null;

        FamilyCohesionRecord fc = new FamilyCohesionRecord();
        fc.setId(f.getId());
        fc.set_id(f.get_id());

        fc.setFamilyMutualAidFreq(f.getFamilyMutualAidFreq());
        fc.setFriendshipApprovalFreq(f.getFriendshipApprovalFreq());
        fc.setFamilyOnlyTaskFreq(f.getFamilyOnlyTaskFreq());
        fc.setFamilyOnlyPreferenceFreq(f.getFamilyOnlyPreferenceFreq());
        fc.setFreeTimeTogetherFreq(f.getFreeTimeTogetherFreq());
        fc.setFamilyProximityPerceptionFreq(f.getFamilyProximityPerceptionFreq());
        fc.setAllFamilyTasksFreq(f.getAllFamilyTasksFreq());
        fc.setFamilyTasksOpportunityFreq(f.getFamilyTasksOpportunityFreq());
        fc.setFamilyDecisionSupportFreq(f.getFamilyDecisionSupportFreq());
        fc.setFamilyUnionRelevanceFreq(f.getFamilyUnionRelevanceFreq());
        fc.setFamilyCohesionResult(f.getFamilyCohesionResult());
        return fc;
    }

    public static OralHealthRecordOB convertOralHealthRecord(OralHealthRecord o) {
        if (o == null) return null;

        OralHealthRecordOB oh = new OralHealthRecordOB();
        oh.setId(o.getId());
        oh.set_id(o.get_id());
        oh.setTeethBrushingFreq(o.getTeethBrushingFreq());
        oh.setToothLesions(Convert.listToothLesionsToObjectBox(o.getToothLesions()));
        return oh;
    }

    public static OralHealthRecord convertOralHealthRecord(OralHealthRecordOB o) {
        if (o == null) return null;

        OralHealthRecord oh = new OralHealthRecord();
        oh.setId(o.getId());
        oh.set_id(o.get_id());
        oh.setTeethBrushingFreq(o.getTeethBrushingFreq());
        oh.setToothLesions(Convert.listToothLesionsToModel(o.getToothLesions()));
        return oh;
    }

    public static List<ToothLesionOB> listToothLesionsToObjectBox(List<ToothLesion> toothLesions) {
        List<ToothLesionOB> aux = new ArrayList<>();
        if (toothLesions == null || toothLesions.isEmpty()) return aux;

        for (ToothLesion t : toothLesions) {
            aux.add(Convert.convertToothLesion(t));
        }
        return aux;
    }

    public static ToothLesionOB convertToothLesion(ToothLesion t) {
        if (t == null) return null;

        ToothLesionOB tl = new ToothLesionOB();
        tl.setId(t.getId());
        tl.setToothType(t.getToothType());
        tl.setLesionType(t.getLesionType());
        return tl;
    }

    public static List<ToothLesion> listToothLesionsToModel(List<ToothLesionOB> toothLesions) {
        List<ToothLesion> aux = new ArrayList<>();
        if (toothLesions == null || toothLesions.isEmpty()) return aux;

        for (ToothLesionOB t : toothLesions) {
            aux.add(Convert.convertToothLesion(t));
        }
        return aux;
    }

    public static ToothLesion convertToothLesion(ToothLesionOB t) {
        if (t == null) return null;

        ToothLesion tl = new ToothLesion();
        tl.setId(t.getId());
        tl.setToothType(t.getToothType());
        tl.setLesionType(t.getLesionType());
        return tl;
    }

    public static List<BodyFatOB> convertListBodyFatToObjectBox(List<BodyFat> bodyFat) {
        List<BodyFatOB> aux = new ArrayList<>();
        if (bodyFat == null || bodyFat.isEmpty()) return aux;

        for (BodyFat bf : bodyFat) {
            aux.add(Convert.convertBodyFat(bf));
        }
        return aux;
    }

    public static List<BodyFat> convertListBodyFatToModel(List<BodyFatOB> bodyFat) {
        List<BodyFat> aux = new ArrayList<>();
        if (bodyFat == null || bodyFat.isEmpty()) return aux;

        for (BodyFatOB bf : bodyFat)
            aux.add(Convert.convertBodyFat(bf));
        return aux;
    }

    public static OdontologicalQuestionnaireOB convertOdontologicalQuestionnaire(OdontologicalQuestionnaire o) {
        if (o == null) return null;

        OdontologicalQuestionnaireOB oq = new OdontologicalQuestionnaireOB();
        oq.setId(o.getId());
        oq.set_id(o.get_id());
        oq.setCreatedAt(o.getCreatedAt());

        oq.setPatient_id(o.getPatient_id());
        oq.setPatientId(o.getPatientId());

        oq.setSociodemographicRecord(Convert.convertSociodemographicRecord(o.getSociodemographicRecord()));
        oq.setFamilyCohesionRecord(Convert.convertFamilyCohesionRecord(o.getFamilyCohesionRecord()));
        oq.setOralHealthRecord(Convert.convertOralHealthRecord(o.getOralHealthRecord()));
        return oq;
    }
}
