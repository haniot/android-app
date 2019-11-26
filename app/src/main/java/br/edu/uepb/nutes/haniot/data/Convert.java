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

    public static DeviceOB convertDevice(Device dP) {
        if (dP == null) return null;

        DeviceOB device = new DeviceOB();
        device.setId(dP.getId());
        device.set_id(dP.get_id());
        device.setName(dP.getName());
        device.setAddress(dP.getAddress());
        device.setType(dP.getType());
        device.setModelNumber(dP.getModelNumber());
        device.setManufacturer(dP.getManufacturer());
        device.setUser_id(dP.getUser_id());
        device.setUserId(dP.getUserId());
//        device.setUuid(dP.getUuid());
//        device.setImg(dP.getImg());
        return device;
    }

    public static Device convertDevice(DeviceOB dP) {
        if (dP == null) return null;

        Device device = new Device();
        device.setId(dP.getId());
        device.set_id(dP.get_id());
        device.setName(dP.getName());
        device.setAddress(dP.getAddress());
        device.setType(dP.getType());
        device.setModelNumber(dP.getModelNumber());
        device.setManufacturer(dP.getManufacturer());
        device.setUser_id(dP.getUser_id());
        device.setUserId(dP.getUserId());
//        device.setUuid(dP.getUuid());
//        device.setImg(dP.getImg());
        return device;
    }

    public static List<Device> listDeviceToModel(List<DeviceOB> devices) {
        List<Device> list = new ArrayList<>();
        if (devices == null || devices.isEmpty()) return list;

        for (DeviceOB d : devices) {
            list.add(convertDevice(d));
        }
        return list;
    }

    public static FeedingHabitsRecord feedingHabitsRecord(FeedingHabitsRecordOB fP) {
        if (fP == null) return null;

        FeedingHabitsRecord fh = new FeedingHabitsRecord();
        fh.setId(fP.getId());
        fh.set_id(fP.get_id());
        fh.setWeeklyFeedingHabits(convertListWeeklyFoodRecord(fP.getWeeklyFeedingHabits()));
        fh.setDailyWaterGlasses(fP.getDailyWaterGlasses());
        fh.setSixMonthBreastFeeding(fP.getSixMonthBreastFeeding());
        fh.setFoodAllergyIntolerance(fP.getFoodAllergyIntolerance());
        fh.setBreakfastDailyFrequency(fP.getBreakfastDailyFrequency());
        return fh;
    }

    public static FeedingHabitsRecordOB feedingHabitsRecord(FeedingHabitsRecord fP) {
        if (fP == null) return null;

        FeedingHabitsRecordOB fh = new FeedingHabitsRecordOB();
        fh.setId(fP.getId());
        fh.set_id(fP.get_id());
        fh.setDailyWaterGlasses(fP.getDailyWaterGlasses());
        fh.setSixMonthBreastFeeding(fP.getSixMonthBreastFeeding());
        fh.setFoodAllergyIntolerance(fP.getFoodAllergyIntolerance());
        fh.setBreakfastDailyFrequency(fP.getBreakfastDailyFrequency());
        fh.setWeeklyFeedingHabits(listWeeklyFoodRecordToObjectBox(fP.getWeeklyFeedingHabits()));
        return fh;
    }

    private static List<WeeklyFoodRecord> convertListWeeklyFoodRecord(List<WeeklyFoodRecordOB> listP) {
        List<WeeklyFoodRecord> list = new ArrayList<>();
        if (listP == null || listP.isEmpty()) return list;

        for (WeeklyFoodRecordOB aux : listP)
            list.add(convertWeeklyFoodRecord(aux));
        return list;
    }

    private static WeeklyFoodRecord convertWeeklyFoodRecord(WeeklyFoodRecordOB wP) {
        if (wP == null) return null;

        WeeklyFoodRecord w = new WeeklyFoodRecord();
        w.setId(wP.getId());
        w.setFood(wP.getFood());
        w.setSevenDaysFreq(wP.getSevenDaysFreq());
        return w;
    }

    private static List<WeeklyFoodRecordOB> listWeeklyFoodRecordToObjectBox(List<WeeklyFoodRecord> listP) {
        List<WeeklyFoodRecordOB> list = new ArrayList<>();
        if (listP == null || listP.isEmpty()) return list;

        for (WeeklyFoodRecord aux : listP)
            list.add(convertWeeklyFoodRecord(aux));
        return list;
    }

    private static WeeklyFoodRecordOB convertWeeklyFoodRecord(WeeklyFoodRecord wP) {
        if (wP == null) return null;

        WeeklyFoodRecordOB w = new WeeklyFoodRecordOB();
        w.setId(wP.getId());
        w.setFood(wP.getFood());
        w.setSevenDaysFreq(wP.getSevenDaysFreq());
        return w;
    }

    public static MeasurementOB convertMeasurement(Measurement mP) {
        if (mP == null) return null;

        MeasurementOB m = new MeasurementOB();
        m.set_id(mP.get_id());
        m.setId(mP.getId());
        m.setValue(mP.getValue());
        m.setUnit(mP.getUnit());
        m.setType(mP.getType());
        m.setTimestamp(mP.getTimestamp());

        m.setUser_id(mP.getUser_id());
        m.setUserId(mP.getUserId());

        m.setDeviceId(mP.getDeviceId());
        m.setSystolic(mP.getSystolic());
        m.setDiastolic(mP.getDiastolic());
        m.setPulse(mP.getPulse());
        m.setMeal(mP.getMeal());

        if (mP.getFat() != null)
            m.setFatModel(convertBodyFat(mP.getFat()));

        m.setBodyFat(convertListBodyFatToObjectBox(mP.getBodyFat()));
        m.setDataset(convertListHeartRateToObjectBox(mP.getDataset()));
        m.setSync(mP.isSync());
        return m;
    }

    private static Measurement convertMeasurement(MeasurementOB mP) {
        if (mP == null) return null;

        Measurement m = new Measurement();
        m.set_id(mP.get_id());
        m.setId(mP.getId());
        m.setValue(mP.getValue());
        m.setUnit(mP.getUnit());
        m.setType(mP.getType());
        m.setTimestamp(mP.getTimestamp());

        m.setUser_id(mP.getUser_id());
        m.setUserId(mP.getUserId());

        m.setDeviceId(mP.getDeviceId());
        m.setSystolic(mP.getSystolic());
        m.setDiastolic(mP.getDiastolic());
        m.setPulse(mP.getPulse());
        m.setMeal(mP.getMeal());

        if (mP.getFat() != null && mP.getFat().getTarget() != null)
            m.setFat(convertBodyFat(mP.getFat().getTarget()));

        m.setBodyFat(convertListBodyFatToModel(mP.getBodyFat()));
        m.setDataset(convertListHeartRateToModel(mP.getDataset()));
        m.setSync(mP.isSync());
        return m;
    }

    public static List<Measurement> listMeasurementsToModel(List<MeasurementOB> listP) {
        List<Measurement> list = new ArrayList<>();
        if (listP == null || listP.isEmpty()) return list;

        for (MeasurementOB aux : listP)
            list.add(convertMeasurement(aux));
        return list;
    }

    private static BodyFatOB convertBodyFat(BodyFat bP) {
        if (bP == null) return null;

        BodyFatOB b = new BodyFatOB();
        b.setId(bP.getId());
        b.setValue(bP.getValue());
        b.setUnit(bP.getUnit());
        b.setTimestamp(bP.getTimestamp());
        return b;
    }

    private static BodyFat convertBodyFat(BodyFatOB bP) {
        if (bP == null) return null;

        BodyFat b = new BodyFat();
        b.setId(bP.getId());
        b.setValue(bP.getValue());
        b.setUnit(bP.getUnit());
        b.setTimestamp(bP.getTimestamp());
        return b;
    }

    private static List<HeartRateItemOB> convertListHeartRateToObjectBox(List<HeartRateItem> listP) {
        List<HeartRateItemOB> list = new ArrayList<>();
        if (listP == null || listP.isEmpty()) return list;

        for (HeartRateItem aux : listP)
            list.add(convertHeartRateItem(aux));
        return list;
    }

    private static HeartRateItemOB convertHeartRateItem(HeartRateItem hP) {
        if (hP == null) return null;

        HeartRateItemOB h = new HeartRateItemOB();
        h.setId(hP.getId());
        h.setValue(hP.getValue());
        h.setTimestamp(hP.getTimestamp());
        return h;
    }

    private static List<HeartRateItem> convertListHeartRateToModel(List<HeartRateItemOB> listP) {
        List<HeartRateItem> list = new ArrayList<>();
        if (listP == null || listP.isEmpty()) return list;

        for (HeartRateItemOB aux : listP)
            list.add(convertHeartRateItem(aux));
        return list;
    }

    private static HeartRateItem convertHeartRateItem(HeartRateItemOB hP) {
        if (hP == null) return null;

        HeartRateItem h = new HeartRateItem();
        h.setId(hP.getId());
        h.setValue(hP.getValue());
        h.setTimestamp(hP.getTimestamp());
        return h;
    }

    public static MedicalRecord medicalRecord(MedicalRecordOB mP) {
        if (mP == null) return null;

        MedicalRecord m = new MedicalRecord();
        m.setId(mP.getId());
        m.set_id(mP.get_id());
        m.setChronicDiseases(listChronicDiseasesToModel(mP.getChronicDiseases()));
        return m;
    }

    public static MedicalRecordOB medicalRecord(MedicalRecord mP) {
        if (mP == null) return null;

        MedicalRecordOB m = new MedicalRecordOB();
        m.setId(mP.getId());
        m.set_id(mP.get_id());
        m.setChronicDiseases(listChronicsToObjectBox(mP.getChronicDiseases()));
        return m;
    }

    private static List<ChronicDisease> listChronicDiseasesToModel(List<ChronicDiseaseOB> listP) {
        List<ChronicDisease> list = new ArrayList<>();
        if (listP == null || listP.isEmpty()) return list;

        for (ChronicDiseaseOB aux : listP)
            list.add(convertChronicDisease(aux));
        return list;
    }

    private static ChronicDisease convertChronicDisease(ChronicDiseaseOB cP) {
        if (cP == null) return null;

        ChronicDisease c = new ChronicDisease();
        c.setId(cP.getId());
        c.setType(cP.getType());
        c.setDiseaseHistory(cP.getDiseaseHistory());
        return c;
    }

    private static List<ChronicDiseaseOB> listChronicsToObjectBox(List<ChronicDisease> listP) {
        List<ChronicDiseaseOB> list = new ArrayList<>();
        if (listP == null || listP.isEmpty()) return list;

        for (ChronicDisease aux : listP)
            list.add(convertChronicDisease(aux));
        return list;
    }

    private static ChronicDiseaseOB convertChronicDisease(ChronicDisease cP) {
        if (cP == null) return null;

        ChronicDiseaseOB c = new ChronicDiseaseOB();
        c.setId(cP.getId());
        c.setType(cP.getType());
        c.setDiseaseHistory(cP.getDiseaseHistory());
        return c;
    }

    public static Patient patient(PatientOB pP) {
        if (pP == null) return null;

        Patient p = new Patient();
        p.setId(pP.getId());
        p.set_id(pP.get_id());
        p.setEmail(pP.getEmail());
        p.setName(pP.getName());
        p.setBirthDate(pP.getBirthDate());
        p.setHealthArea(pP.getHealthArea());
        p.setPassword(pP.getPassword());
        p.setOldPassword(pP.getOldPassword());
        p.setNewPassword(pP.getNewPassword());
        p.setPhoneNumber(pP.getPhoneNumber());
        p.setLastLogin(pP.getLastLogin());
        p.setLastSync(pP.getLastSync());
        p.setLanguage(pP.getLanguage());
        p.setPilotStudyIDSelected(pP.getPilotStudyIDSelected());
        p.setUserType(pP.getUserType());
        p.setPilotId(pP.getPilotId());
        p.setGender(pP.getGender());
        p.setHealthProfessionalId(pP.getHealthProfessionalId());
        p.setCreatedAt(pP.getCreatedAt());
        p.setSync(pP.isSync());
        return p;
    }

    public static PatientOB patient(Patient pP) {
        if (pP == null) return null;

        PatientOB p = new PatientOB();
        p.setId(pP.getId());
        p.set_id(pP.get_id());
        p.setEmail(pP.getEmail());
        p.setName(pP.getName());
        p.setBirthDate(pP.getBirthDate());
        p.setHealthArea(pP.getHealthArea());
        p.setPassword(pP.getPassword());
        p.setOldPassword(pP.getOldPassword());
        p.setNewPassword(pP.getNewPassword());
        p.setPhoneNumber(pP.getPhoneNumber());
        p.setLastLogin(pP.getLastLogin());
        p.setLastSync(pP.getLastSync());
        p.setLanguage(pP.getLanguage());
        p.setPilotStudyIDSelected(pP.getPilotStudyIDSelected());
        p.setUserType(pP.getUserType());
        p.setPilotId(pP.getPilotId());
        p.setGender(pP.getGender());
        p.setHealthProfessionalId(pP.getHealthProfessionalId());
        p.setCreatedAt(pP.getCreatedAt());
        p.setSync(pP.isSync());
        return p;
    }

    public static List<Patient> listPatientsToModel(List<PatientOB> listP) {
        List<Patient> list = new ArrayList<>();
        if (listP == null || listP.isEmpty()) return list;

        for (PatientOB p : listP)
            list.add(patient(p));
        return list;
    }

    public static PhysicalActivityHabitOB physicalActivityHabit(PhysicalActivityHabit pP) {
        if (pP == null) return null;

        PhysicalActivityHabitOB p = new PhysicalActivityHabitOB();
        p.setId(pP.getId());
        p.set_id(pP.get_id());
        p.setSchoolActivityFreq(pP.getSchoolActivityFreq());
        p.setWeeklyActivities(pP.getWeeklyActivities());
        return p;
    }

    public static PhysicalActivityHabit physicalActivityHabit(PhysicalActivityHabitOB pP) {
        if (pP == null) return null;

        PhysicalActivityHabit p = new PhysicalActivityHabit();
        p.setId(pP.getId());
        p.set_id(pP.get_id());
        p.setSchoolActivityFreq(pP.getSchoolActivityFreq());
        p.setWeeklyActivities(pP.getWeeklyActivities());
        return p;
    }

    public static SleepHabitOB sleepHabit(SleepHabit sP) {
        if (sP == null) return null;

        SleepHabitOB s = new SleepHabitOB();
        s.setId(sP.getId());
        s.set_id(sP.get_id());
        s.setWeekDaySleep(sP.getWeekDaySleep());
        s.setWeekDayWakeUp(sP.getWeekDayWakeUp());
        return s;
    }

    public static SleepHabit sleepHabit(SleepHabitOB sP) {
        if (sP == null) return null;

        SleepHabit s = new SleepHabit();
        s.setId(sP.getId());
        s.set_id(sP.get_id());
        s.setWeekDaySleep(sP.getWeekDaySleep());
        s.setWeekDayWakeUp(sP.getWeekDayWakeUp());
        return s;
    }

    public static List<NutritionalQuestionnaire> listNutritionalQuestionnaireToModel(List<NutritionalQuestionnaireOB> listP) {
        List<NutritionalQuestionnaire> list = new ArrayList<>();
        if (listP == null || listP.isEmpty()) return list;

        for (NutritionalQuestionnaireOB questionnaire : listP)
            list.add(nutritionalQuestionnaire(questionnaire));
        return list;
    }

    private static NutritionalQuestionnaire nutritionalQuestionnaire(NutritionalQuestionnaireOB nP) {
        if (nP == null) return null;

        NutritionalQuestionnaire n = new NutritionalQuestionnaire();
        n.setSync(nP.isSync());
        n.setId(nP.getId());
        n.set_id(nP.get_id());
        n.setPatient_id(nP.getPatient_id());
        n.setPatientId(nP.getPatientId());
        n.setCreatedAt(nP.getCreatedAt());
        n.setSleepHabit(sleepHabit(nP.getSleepHabit()));
        n.setPhysicalActivityHabit(physicalActivityHabit(nP.getPhysicalActivityHabit()));
        n.setFeedingHabitsRecord(feedingHabitsRecord(nP.getFeedingHabitsRecord()));
        n.setMedicalRecord(medicalRecord(nP.getMedicalRecord()));
        return n;
    }

    public static NutritionalQuestionnaireOB nutritionalQuestionnaire(NutritionalQuestionnaire nP) {
        if (nP == null) return null;

        NutritionalQuestionnaireOB n = new NutritionalQuestionnaireOB();
        n.setSync(nP.isSync());
        n.setId(nP.getId());
        n.set_id(nP.get_id());
        n.setPatient_id(nP.getPatient_id());
        n.setPatientId(nP.getPatientId());
        n.setCreatedAt(nP.getCreatedAt());
        n.setSleepHabit(sleepHabit(nP.getSleepHabit()));
        n.setPhysicalActivityHabit(physicalActivityHabit(nP.getPhysicalActivityHabit()));
        n.setFeedingHabitsRecord(feedingHabitsRecord(nP.getFeedingHabitsRecord()));
        n.setMedicalRecord(medicalRecord(nP.getMedicalRecord()));
        return n;
    }

    public static SociodemographicRecordOB sociodemographicRecord(SociodemographicRecord sP) {
        if (sP == null) return null;

        SociodemographicRecordOB s = new SociodemographicRecordOB();
        s.setId(sP.getId());
        s.set_id(sP.get_id());
        s.setColorRace(sP.getColorRace());
        s.setMotherScholarity(sP.getMotherScholarity());
        s.setPeopleInHome(sP.getPeopleInHome());
        return s;
    }

    public static SociodemographicRecord sociodemographicRecord(SociodemographicRecordOB sP) {
        if (sP == null) return null;

        SociodemographicRecord s = new SociodemographicRecord();
        s.setId(sP.getId());
        s.set_id(sP.get_id());
        s.setColorRace(sP.getColorRace());
        s.setMotherScholarity(sP.getMotherScholarity());
        s.setPeopleInHome(sP.getPeopleInHome());
        return s;
    }

    public static List<OdontologicalQuestionnaire> listOdontologicalQuestionnaireToModel(List<OdontologicalQuestionnaireOB> listP) {
        List<OdontologicalQuestionnaire> list = new ArrayList<>();
        if (listP == null || listP.isEmpty()) return list;

        for (OdontologicalQuestionnaireOB questionnaire : listP)
            list.add(convertOdontologicalQuestionnaire(questionnaire));
        return list;
    }

    private static OdontologicalQuestionnaire convertOdontologicalQuestionnaire(OdontologicalQuestionnaireOB oP) {
        if (oP == null) return null;

        OdontologicalQuestionnaire o = new OdontologicalQuestionnaire();
        o.setId(oP.getId());
        o.set_id(oP.get_id());
        o.setCreatedAt(oP.getCreatedAt());
        o.setPatient_id(oP.getPatient_id());
        o.setPatientId(oP.getPatientId());
        o.setSociodemographicRecord(sociodemographicRecord(oP.getSociodemographicRecord()));
        o.setFamilyCohesionRecord(familyCohesionRecord(oP.getFamilyCohesionRecord()));
        o.setOralHealthRecord(oralHealthRecord(oP.getOralHealthRecord()));
        return o;
    }

    public static FamilyCohesionRecordOB familyCohesionRecord(FamilyCohesionRecord fP) {
        if (fP == null) return null;

        FamilyCohesionRecordOB f = new FamilyCohesionRecordOB();
        f.setId(fP.getId());
        f.set_id(fP.get_id());
        f.setFamilyMutualAidFreq(fP.getFamilyMutualAidFreq());
        f.setFriendshipApprovalFreq(fP.getFriendshipApprovalFreq());
        f.setFamilyOnlyTaskFreq(fP.getFamilyOnlyTaskFreq());
        f.setFamilyOnlyPreferenceFreq(fP.getFamilyOnlyPreferenceFreq());
        f.setFreeTimeTogetherFreq(fP.getFreeTimeTogetherFreq());
        f.setFamilyProximityPerceptionFreq(fP.getFamilyProximityPerceptionFreq());
        f.setAllFamilyTasksFreq(fP.getAllFamilyTasksFreq());
        f.setFamilyTasksOpportunityFreq(fP.getFamilyTasksOpportunityFreq());
        f.setFamilyDecisionSupportFreq(fP.getFamilyDecisionSupportFreq());
        f.setFamilyUnionRelevanceFreq(fP.getFamilyUnionRelevanceFreq());
        f.setFamilyCohesionResult(fP.getFamilyCohesionResult());
        return f;
    }

    public static FamilyCohesionRecord familyCohesionRecord(FamilyCohesionRecordOB fP) {
        if (fP == null) return null;

        FamilyCohesionRecord f = new FamilyCohesionRecord();
        f.setId(fP.getId());
        f.set_id(fP.get_id());
        f.setFamilyMutualAidFreq(fP.getFamilyMutualAidFreq());
        f.setFriendshipApprovalFreq(fP.getFriendshipApprovalFreq());
        f.setFamilyOnlyTaskFreq(fP.getFamilyOnlyTaskFreq());
        f.setFamilyOnlyPreferenceFreq(fP.getFamilyOnlyPreferenceFreq());
        f.setFreeTimeTogetherFreq(fP.getFreeTimeTogetherFreq());
        f.setFamilyProximityPerceptionFreq(fP.getFamilyProximityPerceptionFreq());
        f.setAllFamilyTasksFreq(fP.getAllFamilyTasksFreq());
        f.setFamilyTasksOpportunityFreq(fP.getFamilyTasksOpportunityFreq());
        f.setFamilyDecisionSupportFreq(fP.getFamilyDecisionSupportFreq());
        f.setFamilyUnionRelevanceFreq(fP.getFamilyUnionRelevanceFreq());
        f.setFamilyCohesionResult(fP.getFamilyCohesionResult());
        return f;
    }

    public static OralHealthRecordOB oralHealthRecord(OralHealthRecord oP) {
        if (oP == null) return null;

        OralHealthRecordOB o = new OralHealthRecordOB();
        o.setId(oP.getId());
        o.set_id(oP.get_id());
        o.setTeethBrushingFreq(oP.getTeethBrushingFreq());
        o.setToothLesions(listToothLesionsToObjectBox(oP.getToothLesions()));
        return o;
    }

    public static OralHealthRecord oralHealthRecord(OralHealthRecordOB oP) {
        if (oP == null) return null;

        OralHealthRecord o = new OralHealthRecord();
        o.setId(oP.getId());
        o.set_id(oP.get_id());
        o.setTeethBrushingFreq(oP.getTeethBrushingFreq());
        o.setToothLesions(listToothLesionsToModel(oP.getToothLesions()));
        return o;
    }

    private static List<ToothLesionOB> listToothLesionsToObjectBox(List<ToothLesion> listP) {
        List<ToothLesionOB> list = new ArrayList<>();
        if (listP == null || listP.isEmpty()) return list;

        for (ToothLesion t : listP)
            list.add(convertToothLesion(t));
        return list;
    }

    private static ToothLesionOB convertToothLesion(ToothLesion tP) {
        if (tP == null) return null;

        ToothLesionOB t = new ToothLesionOB();
        t.setId(tP.getId());
        t.setToothType(tP.getToothType());
        t.setLesionType(tP.getLesionType());
        return t;
    }

    private static List<ToothLesion> listToothLesionsToModel(List<ToothLesionOB> listP) {
        List<ToothLesion> list = new ArrayList<>();
        if (listP == null || listP.isEmpty()) return list;

        for (ToothLesionOB t : listP)
            list.add(convertToothLesion(t));
        return list;
    }

    private static ToothLesion convertToothLesion(ToothLesionOB tP) {
        if (tP == null) return null;

        ToothLesion t = new ToothLesion();
        t.setId(tP.getId());
        t.setToothType(tP.getToothType());
        t.setLesionType(tP.getLesionType());
        return t;
    }

    private static List<BodyFatOB> convertListBodyFatToObjectBox(List<BodyFat> listP) {
        List<BodyFatOB> list = new ArrayList<>();
        if (listP == null || listP.isEmpty()) return list;

        for (BodyFat bf : listP)
            list.add(convertBodyFat(bf));
        return list;
    }

    private static List<BodyFat> convertListBodyFatToModel(List<BodyFatOB> listP) {
        List<BodyFat> list = new ArrayList<>();
        if (listP == null || listP.isEmpty()) return list;

        for (BodyFatOB bf : listP)
            list.add(convertBodyFat(bf));
        return list;
    }

    public static OdontologicalQuestionnaireOB convertOdontologicalQuestionnaire(OdontologicalQuestionnaire oP) {
        if (oP == null) return null;

        OdontologicalQuestionnaireOB o = new OdontologicalQuestionnaireOB();
        o.setId(oP.getId());
        o.set_id(oP.get_id());
        o.setCreatedAt(oP.getCreatedAt());

        o.setPatient_id(oP.getPatient_id());
        o.setPatientId(oP.getPatientId());

        o.setSociodemographicRecord(sociodemographicRecord(oP.getSociodemographicRecord()));
        o.setFamilyCohesionRecord(familyCohesionRecord(oP.getFamilyCohesionRecord()));
        o.setOralHealthRecord(oralHealthRecord(oP.getOralHealthRecord()));
        return o;
    }
}