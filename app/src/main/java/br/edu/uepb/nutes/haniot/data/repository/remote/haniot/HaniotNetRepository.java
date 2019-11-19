package br.edu.uepb.nutes.haniot.data.repository.remote.haniot;

import android.content.Context;
import android.util.Log;

import com.auth0.android.jwt.JWT;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import br.edu.uepb.nutes.haniot.data.model.MeasurementLastResponse;
import br.edu.uepb.nutes.haniot.data.model.nutritional.MedicalRecord;
import br.edu.uepb.nutes.haniot.data.model.nutritional.NutritionalEvaluation;
import br.edu.uepb.nutes.haniot.data.model.nutritional.NutritionalEvaluationResult;
import br.edu.uepb.nutes.haniot.data.model.nutritional.NutritionalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.model.odontological.OdontologicalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.model.odontological.OralHealthRecord;
import br.edu.uepb.nutes.haniot.data.model.odontological.SociodemographicRecord;
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.model.UserAccess;
import br.edu.uepb.nutes.haniot.data.model.Admin;
import br.edu.uepb.nutes.haniot.data.model.Device;
import br.edu.uepb.nutes.haniot.data.model.odontological.FamilyCohesionRecord;
import br.edu.uepb.nutes.haniot.data.model.nutritional.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.HealthProfessional;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.nutritional.PhysicalActivityHabit;
import br.edu.uepb.nutes.haniot.data.model.PilotStudy;
import br.edu.uepb.nutes.haniot.data.model.nutritional.SleepHabit;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.BaseNetRepository;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Repository to consume the OCARIoT API.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public class HaniotNetRepository extends BaseNetRepository {
    private HaniotService haniotService;
    private final Context mContext;

    private HaniotNetRepository(Context context) {
        super(context);
        this.mContext = context;

        super.addInterceptor(requestInterceptor());
        super.addInterceptor(responseInterceptor());
        haniotService = super.provideRetrofit(HaniotService.BASE_URL_HANIOT)
                .create(HaniotService.class);
    }

    public static HaniotNetRepository getInstance(Context context) {
        return new HaniotNetRepository(context);
    }

    /**
     * Provide intercept with header according to HANIoT API Service.
     *
     * @return Interceptor
     */
    private Interceptor requestInterceptor() {
        return chain -> {
            Request request = chain.request();
            final Request.Builder requestBuilder = request.newBuilder()
                    .header("Accept", "application/json")
                    .header("Content-type", "application/json")
                    .method(request.method(), request.body());

            UserAccess userAccess = AppPreferencesHelper
                    .getInstance(mContext)
                    .getUserAccessHaniot();

            if (userAccess != null) {
                requestBuilder.header(
                        "Authorization",
                        "Bearer ".concat(userAccess.getAccessToken())
                );
            }
            Log.w("InterceptorHANIOT", "| REQUEST: " + requestBuilder.build().method() + " "
                    + requestBuilder.build().url().toString());
            return chain.proceed(requestBuilder.build());
        };
    }

    /**
     * Provide intercept with to request response.
     *
     * @return Interceptor
     */
    private Interceptor responseInterceptor() {
        return chain -> {
            Response response = chain.proceed(chain.request());

            if (response.code() == 401 && AppPreferencesHelper
                    .getInstance(mContext).getUserLogged() != null) {
                EventBus.getDefault().post("unauthorized");
            }
            return response;
        };
    }

    // auth
    public Single<UserAccess> auth(String username, String password) {
        return haniotService.auth(new User(username, password))
                .map(userAccess -> {
                    if (userAccess != null && userAccess.getAccessToken() != null) {
                        JWT jwt = new JWT(userAccess.getAccessToken());
                        userAccess.setSubject(jwt.getSubject());
                        userAccess.setExpirationDate(jwt.getExpiresAt().getTime());
                        userAccess.setScopes(jwt.getClaim(UserAccess.KEY_SCOPES).asString());
                        userAccess.setTokenType(jwt.getClaim(UserAccess.SUB_TYPE).asString());
                    }
                    return userAccess;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // forgot password
    public Single<Object> forgotPassword(JsonObject email) {
        return haniotService.forgotPassword(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // user
    public Completable deleteUserById(String userId) {
        return haniotService.deleteUser(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable changePassword(User user) {
        return haniotService.changePassword(user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // users.healthprofessionals
    public Single<HealthProfessional> getHealthProfissional(String healthProfessionalId) {
        return haniotService.getHealthProfessional(healthProfessionalId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // users.healthprofessionals
    public Single<Admin> getAdmin(String adminId) {
        return haniotService.getAdmin(adminId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<HealthProfessional> updateHealthProfissional(HealthProfessional healthProfissional) {
        return haniotService.updateHealthProfissional(healthProfissional.get_id(), healthProfissional)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Admin> updateAdmin(Admin admin) {
        return haniotService.updateAdmin(admin.get_id(), admin)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // pilotstudies
    public Single<List<PilotStudy>> getAllUserPilotStudies(String healthProfessionalId) {
        return haniotService.getAllUserPilotStudies(healthProfessionalId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<PilotStudy>> getAllPilotStudies() {
        return haniotService.getAllPilotStudies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<retrofit2.Response<Void>> associatePatientToPilotStudy(String pilotStudyId, String patient_id) {
        return haniotService.associatePatientToPilotStudy(pilotStudyId, patient_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<PilotStudy> getPilotStudy(String pilotId) {
        return haniotService.getPilotStudy(pilotId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // users.measurements
    public Single<Measurement> saveMeasurement(Measurement measurement) {
        return haniotService.addMeasurement(measurement.getUser_id(), measurement)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Object> saveMeasurement(List<Measurement> measurement) {
        return haniotService.addMeasurement(measurement.get(0).getUser_id(), measurement)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Measurement>> getAllMeasurementsByType(String userId, String type,
                                                              String sort, String dateStart,
                                                              String dateEnd, int page, int limit) {
        return haniotService.getAllMeasurements(userId, type, sort, dateStart, dateEnd, page, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Measurement>> getAllMeasurementsByType(String userId, String type,
                                                              String sort, int page, int limit) {
        return haniotService.getAllMeasurements(userId, type, sort, page, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Measurement>> getAllMeasurements(String userId, String sort,
                                                        String dateStart, String dateEnd,
                                                        int page, int limit) {
        return haniotService.getAllMeasurements(userId, sort, dateStart, dateEnd, page, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Measurement>> getAllMeasurements(String userId, int page,
                                                        int limit, String sort) {
        return haniotService.getAllMeasurements(userId, page, limit, sort)
                .map(measurements -> {
                    for (Measurement m : measurements) {
                        m.setSync(true);
                        m.setUser_id(userId);
                    }
                    return measurements;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Measurement> getMeasurement(String userId, String measurementId) {
        return haniotService.getMeasurement(userId, measurementId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteMeasurement(String userId, String measurementId) {
        return haniotService.deleteMeasurement(userId, measurementId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // users.devices
    public Single<Device> saveDevice(Device device) {
        return haniotService.addDevice(device.getUserId(), device)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Device>> getAllDevices(String userId) {
        return haniotService.getAllDevices(userId)
                .map(devices -> {
                    for (Device d : devices) {
                        d.setUserId(userId);
                    }
                    return devices;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Device> getDevice(String userId, String deviceId) {
        return haniotService.getDevice(userId, deviceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteDevice(String userId, String deviceId) {
        return haniotService.deleteDevice(userId, deviceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // pilotstudies.patients
    public Single<Patient> savePatient(Patient patient) {
        return haniotService.addPatient(patient)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Patient>> getAllPatients(String pilotId, String sort, int page, int limit) {
        return haniotService.getAllPilotStudiesPatients(pilotId, sort, page, limit)
                .map(patients -> {
                    for (Patient p : patients) {
                        p.setSync(true);
                        p.setPilotId(pilotId);
                    }
                    return patients;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Patient> getPatient(String patient_id) {
        return haniotService.getPatient(patient_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Patient> updatePatient(Patient patient) {
        patient.setPilotId(null);
        return haniotService.updatePatient(patient.get_id(), patient)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deletePatient(String patient_id) {
        return haniotService.deletePatient(patient_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // pilotstudies.patients.sleephabits
//    public Single<SleepHabit> saveSleepHabit(SleepHabit sleepHabit) {
//        return haniotService.addSleepHabit(sleepHabit.getPatient_id(), sleepHabit)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }

    public Completable deleteSleepHabit(String patient_id, String sleepHabitId) {
        return haniotService.deleteSleepHabit(patient_id, sleepHabitId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // pilotstudies.patients.physicalactivityhabits
//    public Single<PhysicalActivityHabit> savePhysicalActivityHabit(
//            PhysicalActivityHabit physicalActivityHabits) {
//        return haniotService
//                .addPhysicalActivityHabit(
//                        physicalActivityHabits.getPatient_id(),
//                        physicalActivityHabits
//                )
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }

    public Completable deletePhysicalActivityHabit(String patient_id, String physicalActivityHabitId) {
        return haniotService.deletePhysicalActivityHabit(patient_id, physicalActivityHabitId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // pilotstudies.patients.feedinghabitsrecords
//    public Single<FeedingHabitsRecord> saveFeedingHabitsRecord(FeedingHabitsRecord feedingHabitsRecord) {
//        return haniotService.addFeedingHabitsRecord(feedingHabitsRecord.getPatient_id(),
//                feedingHabitsRecord)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }

    public Completable deleteFeedingHabitsRecord(String patient_id, String feedingHabitsRecordId) {
        return haniotService.deleteFeedingHabitsRecord(patient_id, feedingHabitsRecordId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // pilotstudies.patients.medicalrecords
//    public Single<MedicalRecord> saveMedicalRecord(MedicalRecord medicalRecord) {
//        return haniotService.addMedicalRecord(medicalRecord.getPatient_id(), medicalRecord)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }

    public Completable deleteMedicalRecord(String patient_id, String medicalRecordId) {
        return haniotService.deleteMedicalRecord(patient_id, medicalRecordId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // pilotstudies.patients.familycohesionrecord
//    public Single<FamilyCohesionRecord> saveFamilyCohesionRecord(
//            FamilyCohesionRecord familyCohesionRecord) {
//        return haniotService
//                .addFamilyCohesionRecord(
//                        familyCohesionRecord.getpatient_id(),
//                        familyCohesionRecord
//                )
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }

    public Completable deleteFamilyCohesionRecord(String patient_id, String familyCohesionRecordId) {
        return haniotService.deleteFamilyCohesionRecord(patient_id, familyCohesionRecordId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // pilotstudies.patients.oralhealthrecords
//    public Single<OralHealthRecord> saveOralHealthRecord(
//            OralHealthRecord oralhealthrecord) {
//        return haniotService
//                .addOralHealthRecord(
//                        oralhealthrecord.getpatient_id(),
//                        oralhealthrecord
//                )
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }

    public Completable deleteOralHealthRecord(String patient_id, String oralHealthRecordsId) {
        return haniotService.deleteOralHealthRecord(patient_id, oralHealthRecordsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // pilotstudies.patients.sociodemographicrecord
//    public Single<SociodemographicRecord> saveSociodemographicRecord(
//            SociodemographicRecord sociodemographicrecord) {
//        return haniotService
//                .addSociodemographicRecord(
//                        sociodemographicrecord.getpatient_id(),
//                        sociodemographicrecord
//                )
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread());
//    }

    public Completable deleteSociodemographicRecord(String patient_id, String sociodemographicRecordId) {
        return haniotService.deleteSociodemographicRecord(patient_id, sociodemographicRecordId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<SleepHabit>> getAllSleepHabits(String patient_id, int page, int limit, String sort) {
        return haniotService.getAllSleepHabits(patient_id, page, limit, sort)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<PhysicalActivityHabit>> getAllPhysicalActivity(String patient_id, int page, int limit, String sort) {
        return haniotService.getAllPhysicalActivity(patient_id, page, limit, sort)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<FeedingHabitsRecord>> getAllFeedingHabits(String patient_id, int page, int limit, String sort) {
        return haniotService.getAllFeedingHabits(patient_id, page, limit, sort)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<MedicalRecord>> getAllMedicalRecord(String patient_id, int page, int limit, String sort) {
        return haniotService.getAllMedicalRecord(patient_id, page, limit, sort)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<SociodemographicRecord>> getAllSociodemographic(String patient_id, int page, int limit, String sort) {
        return haniotService.getAllSociodemographic(patient_id, page, limit, sort)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<FamilyCohesionRecord>> getAllFamilyCohesion(String patient_id, int page, int limit, String sort) {
        return haniotService.getAllFamilyCohesion(patient_id, page, limit, sort)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<OralHealthRecord>> getAllOralHealth(String patient_id, int page, int limit, String sort) {
        return haniotService.getAllOralHealth(patient_id, page, limit, sort)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<NutritionalQuestionnaire>> getAllNutritionalQuestionnaires(String patient_id, int page, int limit, String sort) {
        return haniotService.getAllNutritionalQuestionnaires(patient_id, page, limit, sort)
                .map(nutritionalQuestionnaires -> {
                    for (NutritionalQuestionnaire n : nutritionalQuestionnaires) {
                        n.setSync(true);
                        n.setPatient_id(patient_id);
                    }
                    return nutritionalQuestionnaires;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<OdontologicalQuestionnaire>> getAllOdontologicalQuestionnaires(String patient_id, int page, int limit, String sort) {
        return haniotService.getAllOdontologicalQuestionnaires(patient_id, page, limit, sort)
                .map(odontologicalQuestionnaires -> {
                    for (OdontologicalQuestionnaire o : odontologicalQuestionnaires) {
                        o.setSync(true);
                        o.setPatient_id(patient_id);
                    }
                    return odontologicalQuestionnaires;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<NutritionalQuestionnaire> getLastNutritionalQuestionnaire(String patient_id) {
        return haniotService.getLastNutritionalQuestionnaire(patient_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<OdontologicalQuestionnaire> getLastOdontologicalQuestionnaires(String patient_id) {
        return haniotService.getLastOdontologicalQuestionnaires(patient_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<MeasurementLastResponse> getLastMeasurements(String patient_id) {
        return haniotService.getLastMeasurements(patient_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Object> updateNutritionalQuestionnaire(String patient_id, String questionnaireId, String resourceName, Object object) {
        return haniotService.updateNutritionalQuestionnaire(patient_id, questionnaireId, resourceName, object)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Object> updateOdontologicalQuestionnaire(String patient_id, String questionnaireId, String resourceName, Object object) {
        return haniotService.updateOdontologicalQuestionnaire(patient_id, questionnaireId, resourceName, object)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<NutritionalQuestionnaire> saveNutritionalQuestionnaire(NutritionalQuestionnaire nutritionalQuestionnaire) {
        return haniotService.saveNutritionalQuestionnaire(
                nutritionalQuestionnaire.getPatient_id(),
                nutritionalQuestionnaire)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<OdontologicalQuestionnaire> saveOdontologicalQuestionnaire(OdontologicalQuestionnaire odontologicalQuestionnaire) {
        return haniotService.saveOdontologicalQuestionnaire(
                odontologicalQuestionnaire.getPatient_id(),
                odontologicalQuestionnaire)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // patients.nutritionalevaluation
    public Single<NutritionalEvaluationResult> saveNutritionalEvaluation(
            NutritionalEvaluation nutritionalEvaluation) {
        return haniotService
                .saveNutritionalEvaluation(
                        nutritionalEvaluation.getPatient().get_id(),
                        nutritionalEvaluation)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
