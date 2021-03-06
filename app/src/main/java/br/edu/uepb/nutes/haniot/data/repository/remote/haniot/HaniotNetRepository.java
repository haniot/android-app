package br.edu.uepb.nutes.haniot.data.repository.remote.haniot;

import android.content.Context;
import android.util.Log;

import com.auth0.android.jwt.JWT;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Objects;

import br.edu.uepb.nutes.haniot.data.model.Admin;
import br.edu.uepb.nutes.haniot.data.model.Device;
import br.edu.uepb.nutes.haniot.data.model.FamilyCohesionRecord;
import br.edu.uepb.nutes.haniot.data.model.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.HealthProfessional;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.MeasurementLastResponse;
import br.edu.uepb.nutes.haniot.data.model.MedicalRecord;
import br.edu.uepb.nutes.haniot.data.model.NutritionalEvaluation;
import br.edu.uepb.nutes.haniot.data.model.NutritionalEvaluationResult;
import br.edu.uepb.nutes.haniot.data.model.NutritionalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.model.OdontologicalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.model.OralHealthRecord;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.PhysicalActivityHabit;
import br.edu.uepb.nutes.haniot.data.model.PilotStudy;
import br.edu.uepb.nutes.haniot.data.model.SleepHabit;
import br.edu.uepb.nutes.haniot.data.model.SociodemographicRecord;
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.model.UserAccess;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.BaseNetRepository;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
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

    public Single<retrofit2.Response<Void>> associatePatientToPilotStudy(String pilotStudyId, String patientId) {
        return haniotService.associatePatientToPilotStudy(pilotStudyId, patientId)
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
        return haniotService.addMeasurement(measurement.getUserId(), measurement)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Object> saveMeasurement(List<Measurement> measurement) {
        return haniotService.addMeasurement(measurement.get(0).getUserId(), measurement)
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Patient> getPatient(String patientId) {
        return haniotService.getPatient(patientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Patient> updatePatient(Patient patient) {
        patient.setPilotId(null);
        return haniotService.updatePatient(patient.get_id(), patient)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deletePatient(String patientId) {
        return haniotService.deletePatient(patientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // pilotstudies.patients.sleephabits
    public Single<SleepHabit> saveSleepHabit(SleepHabit sleepHabit) {
        return haniotService.addSleepHabit(sleepHabit.getPatientId(), sleepHabit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteSleepHabit(String patientId, String sleepHabitId) {
        return haniotService.deleteSleepHabit(patientId, sleepHabitId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // pilotstudies.patients.physicalactivityhabits
    public Single<PhysicalActivityHabit> savePhysicalActivityHabit(
            PhysicalActivityHabit physicalActivityHabits) {
        return haniotService
                .addPhysicalActivityHabit(
                        physicalActivityHabits.getPatientId(),
                        physicalActivityHabits
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deletePhysicalActivityHabit(String patientId, String physicalActivityHabitId) {
        return haniotService.deletePhysicalActivityHabit(patientId, physicalActivityHabitId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // pilotstudies.patients.feedinghabitsrecords
    public Single<FeedingHabitsRecord> saveFeedingHabitsRecord(FeedingHabitsRecord feedingHabitsRecord) {
        return haniotService.addFeedingHabitsRecord(feedingHabitsRecord.getPatientId(),
                feedingHabitsRecord)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteFeedingHabitsRecord(String patientId, String feedingHabitsRecordId) {
        return haniotService.deleteFeedingHabitsRecord(patientId, feedingHabitsRecordId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // pilotstudies.patients.medicalrecords
    public Single<MedicalRecord> saveMedicalRecord(MedicalRecord medicalRecord) {
        return haniotService.addMedicalRecord(medicalRecord.getPatientId(), medicalRecord)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteMedicalRecord(String patientId, String medicalRecordId) {
        return haniotService.deleteMedicalRecord(patientId, medicalRecordId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // pilotstudies.patients.familycohesionrecord
    public Single<FamilyCohesionRecord> saveFamilyCohesionRecord(
            FamilyCohesionRecord familyCohesionRecord) {
        return haniotService
                .addFamilyCohesionRecord(
                        familyCohesionRecord.getPatientId(),
                        familyCohesionRecord
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteFamilyCohesionRecord(String patientId, String familyCohesionRecordId) {
        return haniotService.deleteFamilyCohesionRecord(patientId, familyCohesionRecordId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // pilotstudies.patients.oralhealthrecords
    public Single<OralHealthRecord> saveOralHealthRecord(
            OralHealthRecord oralhealthrecord) {
        return haniotService
                .addOralHealthRecord(
                        oralhealthrecord.getPatientId(),
                        oralhealthrecord
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteOralHealthRecord(String patientId, String oralHealthRecordsId) {
        return haniotService.deleteOralHealthRecord(patientId, oralHealthRecordsId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // pilotstudies.patients.sociodemographicrecord
    public Single<SociodemographicRecord> saveSociodemographicRecord(
            SociodemographicRecord sociodemographicrecord) {
        return haniotService
                .addSociodemographicRecord(
                        sociodemographicrecord.getPatientId(),
                        sociodemographicrecord
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteSociodemographicRecord(String patientId, String sociodemographicRecordId) {
        return haniotService.deleteSociodemographicRecord(patientId, sociodemographicRecordId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<SleepHabit>> getAllSleepHabits(String patientId, int page, int limit, String sort) {
        return haniotService.getAllSleepHabits(patientId, page, limit, sort)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<PhysicalActivityHabit>> getAllPhysicalActivity(String patientId, int page, int limit, String sort) {
        return haniotService.getAllPhysicalActivity(patientId, page, limit, sort)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<FeedingHabitsRecord>> getAllFeedingHabits(String patientId, int page, int limit, String sort) {
        return haniotService.getAllFeedingHabits(patientId, page, limit, sort)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<MedicalRecord>> getAllMedicalRecord(String patientId, int page, int limit, String sort) {
        return haniotService.getAllMedicalRecord(patientId, page, limit, sort)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<SociodemographicRecord>> getAllSociodemographic(String patientId, int page, int limit, String sort) {
        return haniotService.getAllSociodemographic(patientId, page, limit, sort)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<FamilyCohesionRecord>> getAllFamilyCohesion(String patientId, int page, int limit, String sort) {
        return haniotService.getAllFamilyCohesion(patientId, page, limit, sort)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<OralHealthRecord>> getAllOralHealth(String patientId, int page, int limit, String sort) {
        return haniotService.getAllOralHealth(patientId, page, limit, sort)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<NutritionalQuestionnaire>> getAllNutritionalQuestionnaires(String patientId, int page, int limit, String sort) {
        return haniotService.getAllNutritionalQuestionnaires(patientId, page, limit, sort)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<OdontologicalQuestionnaire>> getAllOdontologicalQuestionnaires(String patientId, int page, int limit, String sort) {
        return haniotService.getAllOdontologicalQuestionnaires(patientId, page, limit, sort)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<NutritionalQuestionnaire> getLastNutritionalQuestionnaire(String patientId) {
        return haniotService.getLastNutritionalQuestionnaire(patientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<OdontologicalQuestionnaire> getLastOdontologicalQuestionnaires(String patientId) {
        return haniotService.getLastOdontologicalQuestionnaires(patientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<MeasurementLastResponse> getLastMeasurements(String patientId) {
        return haniotService.getLastMeasurements(patientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Object> updateNutritionalQuestionnaire(String patientId, String questionnaireId, String resourceName, Object object) {
        return haniotService.updateNutritionalQuestionnaire(patientId, questionnaireId, resourceName, object)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<Object> updateOdontologicalQuestionnaire(String patientId, String questionnaireId, String resourceName, Object object) {
        return haniotService.updateOdontologicalQuestionnaire(patientId, questionnaireId, resourceName, object)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<NutritionalQuestionnaire> saveNutritionalQuestionnaire(String patientId, NutritionalQuestionnaire nutritionalQuestionnaire) {
        return haniotService.saveNutritionalQuestionnaire(
                patientId,
                nutritionalQuestionnaire)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<OdontologicalQuestionnaire> saveOdontologicalQuestionnaire(String patientId, OdontologicalQuestionnaire odontologicalQuestionnaire) {
        return haniotService.saveOdontologicalQuestionnaire(
                patientId,
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
