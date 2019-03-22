package br.edu.uepb.nutes.haniot.data.repository.remote.haniot;

import android.content.Context;
import android.util.Log;

import com.auth0.android.jwt.JWT;

import java.util.List;

import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.BaseNetRepository;
import br.edu.uepb.nutes.haniot.data.model.Device;
import br.edu.uepb.nutes.haniot.data.model.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.MedicalRecord;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.PhysicalActivityHabit;
import br.edu.uepb.nutes.haniot.data.model.PilotStudy;
import br.edu.uepb.nutes.haniot.data.model.SleepHabit;
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.model.UserAccess;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.Request;

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

        super.addInterceptor(provideInterceptor());
        haniotService = super.provideRetrofit(HaniotService.BASE_URL_HANIOT)
                .create(HaniotService.class);
    }

    public static HaniotNetRepository getInstance(Context context) {
        return new HaniotNetRepository(context);
    }

    /**
     * Provide intercept with header according to OCARioT API Service.
     *
     * @return Interceptor
     */
    private Interceptor provideInterceptor() {
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
            Log.w("InterceptorHANIOT", requestBuilder.build().headers().toString());
            Log.w("InterceptorHANIOT", "| REQUEST: " + requestBuilder.build().method() + " "
                    + requestBuilder.build().url().toString());
            return chain.proceed(requestBuilder.build());
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
                    }
                    return userAccess;
                })
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
        return haniotService.changePassword(user.get_id(), user)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // pilotstudies
    public Observable<List<PilotStudy>> getAllPilotStudies(String healthProfessionalId) {
        return haniotService.getAllPilotStudies(healthProfessionalId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<PilotStudy> getPilotStudy(String pilotId) {
        return haniotService.getPilotStudy(pilotId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // users.healthprofessionals
    public Observable<User> getHealthProfissional(String healthProfessionalId) {
        return haniotService.getHealthProfessional(healthProfessionalId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<User> updateHealthProfissional(User healthProfissional) {
        return haniotService.updateHealthProfissional(healthProfissional.get_id(), healthProfissional)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // users.measurements
    public Observable<Measurement> saveMeasurement(Measurement measurement) {
        return haniotService.addMeasurement(measurement.getUserObj().get_id(), measurement)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Measurement>> getAllMeasurements(String userId, String type, String sort, int page, int limit) {
        return haniotService.getAllMeasurements(userId, type, sort, page, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Measurement> getMeasurement(String userId, String measurementId) {
        return haniotService.getMeasurement(userId, measurementId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Measurement> updateMeasurement(Measurement measurement) {
        return haniotService.updateMeasurement(measurement.getUserObj().get_id(),
                measurement.get_id(), measurement)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteMeasurement(String userId, String measurementId) {
        return haniotService.deleteMeasurement(userId, measurementId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // users.devices
    public Observable<Device> saveDevice(Device device) {
        return haniotService.addDevice(device.getUserObj().get_id(), device)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Device>> getAllDevices(String userId) {
        return haniotService.getAllDevices(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Device> getDevice(String userId, String deviceId) {
        return haniotService.getDevice(userId, deviceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Device> updateDevice(Device device) {
        return haniotService.updateDevice(device.getUserObj().get_id(), device.get_id(), device)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteDevice(String userId, String deviceId) {
        return haniotService.deleteDevice(userId, deviceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // pilotstudies.patients
    public Observable<Patient> savePatient(Patient patient) {
        return haniotService.addPatient(patient.getPilotId(), patient)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Patient>> getAllPatients(String pilotId, String sort, int page, int limit) {
        return haniotService.getAllPatients(pilotId, sort, page, limit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Patient> getPatient(String pilotId, String patientId) {
        return haniotService.getPatient(pilotId, patientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Patient> updatePatient(Patient patient) {
        return haniotService.updatePatient(patient.getPilotId(), patient.get_id(), patient)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deletePatient(String pilotId, String patientId) {
        return haniotService.deletePatient(pilotId, patientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // pilotstudies.patients.sleephabits
    public Observable<SleepHabit> saveSleepHabit(
            String pilotId,
            SleepHabit sleepHabit) {
        return haniotService.addSleepHabit(pilotId, sleepHabit.getPatientId(), sleepHabit)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteSleepHabit(String pilotId, String patientId, String sleepHabitId) {
        return haniotService.deleteSleepHabit(pilotId, patientId, sleepHabitId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // pilotstudies.patients.physicalactivityhabits
    public Observable<PhysicalActivityHabit> savePhysicalActivityHabit(
            String pilotId, PhysicalActivityHabit physicalActivityHabits) {
        return haniotService.addPhysicalActivityHabit(pilotId,
                physicalActivityHabits.getPatientId(),
                physicalActivityHabits)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deletePhysicalActivityHabit(
            String pilotId, String patientId, String physicalActivityHabitId) {
        return haniotService.deletePhysicalActivityHabit(pilotId, patientId, physicalActivityHabitId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // pilotstudies.patients.feedinghabitsrecords
    public Observable<FeedingHabitsRecord> saveFeedingHabitsRecord(
            String pilotId, FeedingHabitsRecord feedingHabitsRecord) {
        return haniotService.addFeedingHabitsRecord(pilotId,
                feedingHabitsRecord.getPatientId(),
                feedingHabitsRecord)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteFeedingHabitsRecord(
            String pilotId, String patientId, String feedingHabitsRecordId) {
        return haniotService.deleteFeedingHabitsRecord(pilotId, patientId, feedingHabitsRecordId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // pilotstudies.patients.medicalrecords
    public Observable<MedicalRecord> saveMedicalRecord(
            String pilotId, MedicalRecord medicalRecord) {
        return haniotService.addMedicalRecord(pilotId,
                medicalRecord.getPatientId(),
                medicalRecord)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable deleteMedicalRecord(
            String pilotId, String patientId, String medicalRecordId) {
        return haniotService.deleteMedicalRecord(pilotId, patientId, medicalRecordId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
