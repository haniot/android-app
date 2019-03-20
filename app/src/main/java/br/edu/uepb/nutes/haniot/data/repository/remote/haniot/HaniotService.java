package br.edu.uepb.nutes.haniot.data.repository.remote.haniot;

import java.util.List;

import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.haniot.model.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.MedicalRecord;
import br.edu.uepb.nutes.haniot.model.Patient;
import br.edu.uepb.nutes.haniot.model.PhysicalActivityHabits;
import br.edu.uepb.nutes.haniot.model.PilotStudy;
import br.edu.uepb.nutes.haniot.model.SleepHabit;
import br.edu.uepb.nutes.haniot.model.User;
import br.edu.uepb.nutes.haniot.model.UserAccess;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface for OCARIoT API.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public interface HaniotService {
    String BASE_URL_HANIOT = "https://192.168.0.120/"; // API GATEWAY LOCAL

    // auth
    @POST("auth")
    Single<UserAccess> auth(@Body User user);

    // user
    @DELETE("users/{user_id}")
    Completable deleteUser(@Path("user_id") String userId);

    @PATCH("users/{user_id}/password")
    Completable changePassword(@Path("user_id") String userId);

    // pilotstudies
    @GET("pilotstudies/{pilotstudy_id}")
    Observable<PilotStudy> getPilotStudy(
            @Path("pilotstudy_id") String pilotStudyId
    );

    // users.healthprofessionals
    @GET("users/healthprofessionals/{healthprofessional_id}")
    Observable<User> getHealthProfissional(
            @Path("healthprofessional_id") String healthProfessionalId
    );

    @PATCH("users/healthprofessionals/{healthprofessional_id}")
    Observable<User> updateHealthProfissional(
            @Path("healthprofessional_id") String healthProfessionalId,
            @Body User healthProfissional
    );

    // users.measurements
    @POST("users/{user_id}/measurements")
    Observable<Measurement> addMeasurement(@Path("user_id") String userId,
                                           @Body Measurement measurement);

    @GET("users/{user_id}/measurements")
    Observable<List<Measurement>> getAllMeasurements(@Path("user_id") String userId,
                                                     @Query("sort") String sort,
                                                     @Query("page") int page,
                                                     @Query("limit") int limit);

    @GET("users/{user_id}/measurements/{measurement_id}")
    Observable<Measurement> getMeasurement(@Path("user_id") String userId,
                                           @Path("measurement_id") String measurementId);

    @PATCH("users/{user_id}/measurements/{measurement_id}")
    Observable<Measurement> updateMeasurement(@Path("user_id") String userId,
                                              @Path("measurement_id") String measurementId,
                                              @Body Measurement measurement);

    @DELETE("users/{user_id}/measurements/{measurement_id}")
    Completable deleteMeasurement(@Path("user_id") String userId,
                                  @Path("measurement_id") String measurementId);

    // user.devices
    @POST("users/{user_id}/devices")
    Observable<Device> addDevice(@Path("user_id") String userId, @Body Device device);

    @GET("users/{user_id}/devices")
    Observable<List<Device>> getAllDevices(@Path("user_id") String userId,
                                           @Query("sort") String sort,
                                           @Query("page") int page,
                                           @Query("limit") int limit);

    @GET("users/{user_id}/devices/{device_id}")
    Observable<Device> getDevice(@Path("user_id") String userId,
                                 @Path("device_id") String deviceId);

    @PATCH("users/{user_id}/devices/{device_id}")
    Observable<Device> updateDevice(@Path("user_id") String userId,
                                    @Path("device_id") String deviceId,
                                    @Body Device device);

    @DELETE("users/{user_id}/devices/{device_id}")
    Completable deleteDevice(@Path("user_id") String userId,
                             @Path("device_id") String deviceId);

    // pilotstudies.patients
    @POST("pilotstudies/{pilotstudy_id}/patients")
    Observable<Patient> addPatient(@Path("pilotstudy_id") String pilotStudyId,
                                   @Body Patient patient);

    @GET("pilotstudies/{pilotstudy_id}/patients")
    Observable<List<Patient>> getAllPatients(@Path("pilotstudy_id") String pilotStudyId,
                                             @Query("sort") String sort,
                                             @Query("page") int page,
                                             @Query("limit") int limit);

    @GET("pilotstudies/{pilotstudy_id}/patients/{patient_id}")
    Observable<Patient> getPatient(@Path("pilotstudy_id") String pilotStudyId,
                                   @Path("patient_id") String patientId);

    @PATCH("pilotstudies/{pilotstudy_id}/patients/{patient_id}")
    Observable<Patient> updatePatient(@Path("pilotstudy_id") String pilotStudyId,
                                      @Path("patient_id") String patientId,
                                      @Body Patient patient);

    @DELETE("pilotstudies/{pilotstudy_id}/patients/{patient_id}")
    Completable deletePatient(@Path("pilotstudy_id") String pilotStudyId,
                              @Path("patient_id") String patientId);

    // pilotstudies.patients.sleephabits
    @POST("pilotstudies/{pilotstudy_id}/patients/{patient_id}/sleephabits")
    Observable<SleepHabit> addSleepHabit(@Path("pilotstudy_id") String pilotStudyId,
                                         @Path("patient_id") String patientId,
                                         @Body SleepHabit sleepHabit);

    @GET("pilotstudies/{pilotstudy_id}/patients/{patient_id}/sleephabits")
    Observable<List<SleepHabit>> getAllSleepHabits(@Path("pilotstudy_id") String pilotStudyId,
                                                   @Path("patient_id") String patientId,
                                                   @Query("sort") String sort,
                                                   @Query("page") int page,
                                                   @Query("limit") int limit);

    @GET("pilotstudies/{pilotstudy_id}/patients/{patient_id}/sleephabits/{sleephabit_id}")
    Observable<SleepHabit> getSleepHabit(@Path("pilotstudy_id") String pilotStudyId,
                                         @Path("patient_id") String patientId,
                                         @Path("sleephabit_id") String sleepHabitId);

    @DELETE("pilotstudies/{pilotstudy_id}/patients/{patient_id}/sleephabits/{sleephabit_id}")
    Completable deleteSleepHabit(@Path("pilotstudy_id") String pilotStudyId,
                                 @Path("patient_id") String patientId,
                                 @Path("sleephabit_id") String sleepHabitId);

    // pilotstudies.patients.physicalactivityhabits
    @POST("pilotstudies/{pilotstudy_id}/patients/{patient_id}/physicalactivityhabits")
    Observable<PhysicalActivityHabits> addPhysicalActivityHabit(
            @Path("pilotstudy_id") String pilotStudyId,
            @Path("patient_id") String patientId,
            @Body PhysicalActivityHabits physicalActivityHabits
    );

    @GET("pilotstudies/{pilotstudy_id}/patients/{patient_id}/physicalactivityhabits")
    Observable<List<PhysicalActivityHabits>> getAllPhysicalActivityHabits(
            @Path("pilotstudy_id") String pilotStudyId,
            @Path("patient_id") String patientId,
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @DELETE("pilotstudies/{pilotstudy_id}/patients/{patient_id}/physicalactivityhabits/{physicalactivityhabits_id}")
    Completable deletePhysicalActivityHabit(
            @Path("pilotstudy_id") String pilotStudyId,
            @Path("patient_id") String patientId,
            @Path("physicalactivityhabits_id") String physicalActivityHabitsId
    );

    // pilotstudies.patients.feedinghabitsrecords
    @POST("pilotstudies/{pilotstudy_id}/patients/{patient_id}/feedinghabitsrecords")
    Observable<FeedingHabitsRecord> addFeedingHabitsRecord(
            @Path("pilotstudy_id") String pilotStudyId,
            @Path("patient_id") String patientId,
            @Body FeedingHabitsRecord feedingHabitsRecord
    );

    @GET("pilotstudies/{pilotstudy_id}/patients/{patient_id}/feedinghabitsrecords")
    Observable<List<FeedingHabitsRecord>> getAllFeedingHabitsRecords(
            @Path("pilotstudy_id") String pilotStudyId,
            @Path("patient_id") String patientId,
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @DELETE("/pilotstudies/{pilotstudy_id}/patients/{patient_id}/feedinghabitsrecords/{feedinghabitsrecord_id}")
    Completable deleteFeedingHabitsRecord(
            @Path("pilotstudy_id") String pilotStudyId,
            @Path("patient_id") String patientId,
            @Path("feedinghabitsrecord_id") String feedingHabitsRecordId
    );

    // pilotstudies.patients.medicalrecords
    @POST("pilotstudies/{pilotstudy_id}/patients/{patient_id}/medicalrecords")
    Observable<MedicalRecord> addMedicalRecord(
            @Path("pilotstudy_id") String pilotStudyId,
            @Path("patient_id") String patientId,
            @Body MedicalRecord medicalRecord
    );

    @GET("pilotstudies/{pilotstudy_id}/patients/{patient_id}/medicalrecords")
    Observable<List<MedicalRecord>> getAllMedicalRecords(
            @Path("pilotstudy_id") String pilotStudyId,
            @Path("patient_id") String patientId,
            @Query("sort") String sort,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @DELETE("pilotstudies/{pilotstudy_id}/patients/{patient_id}/medicalrecords/{medicalrecord_id}")
    Completable deleteMedicalRecord(
            @Path("pilotstudy_id") String pilotStudyId,
            @Path("patient_id") String patientId,
            @Path("medicalrecord_id") String medicalRecordId
    );
}
