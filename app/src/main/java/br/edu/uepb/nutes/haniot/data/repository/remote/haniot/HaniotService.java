package br.edu.uepb.nutes.haniot.data.repository.remote.haniot;

import java.util.List;

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
    String BASE_URL_HANIOT = "http://HOST_IP/"; // API GATEWAY LOCAL

    // auth
    @POST("auth")
    Single<UserAccess> auth(@Body User user);

    // user
    @DELETE("users/{user_id}")
    Completable deleteUser(@Path("user_id") String userId);

    @PATCH("users/{user_id}/password")
    Completable changePassword(@Path("user_id") String userId, @Body User user);

    // users.healthprofessionals
    @GET("users/healthprofessionals/{healthprofessional_id}")
    Single<User> getHealthProfessional(
            @Path("healthprofessional_id") String healthProfessionalId
    );

    @PATCH("users/healthprofessionals/{healthprofessional_id}")
    Single<User> updateHealthProfissional(
            @Path("healthprofessional_id") String healthProfessionalId,
            @Body User healthProfissional
    );

    // pilotstudies
    @GET("users/healthprofessionals/{healthprofessional_id}/pilotstudies")
    Single<List<PilotStudy>> getAllPilotStudies(
            @Path("healthprofessional_id") String healthProfessionalId
    );

    @GET("pilotstudies/{pilotstudy_id}")
    Single<PilotStudy> getPilotStudy(@Path("pilotstudy_id") String pilotId);

    // users.measurements
    @POST("users/{user_id}/measurements")
    Single<Measurement> addMeasurement(@Path("user_id") String userId,
                                       @Body Measurement measurement);

    @GET("users/{user_id}/measurements")
    Single<List<Measurement>> getAllMeasurements(@Path("user_id") String userId,
                                                 @Query("type") String type,
                                                 @Query("sort") String sort,
                                                 @Query("page") int page,
                                                 @Query("limit") int limit);

    @GET("users/{user_id}/measurements/{measurement_id}")
    Single<Measurement> getMeasurement(@Path("user_id") String userId,
                                       @Path("measurement_id") String measurementId);

    @PATCH("users/{user_id}/measurements/{measurement_id}")
    Single<Measurement> updateMeasurement(@Path("user_id") String userId,
                                          @Path("measurement_id") String measurementId,
                                          @Body Measurement measurement);

    @DELETE("users/{user_id}/measurements/{measurement_id}")
    Completable deleteMeasurement(@Path("user_id") String userId,
                                  @Path("measurement_id") String measurementId);

    // user.devices
    @POST("users/{user_id}/devices")
    Single<Device> addDevice(@Path("user_id") String userId, @Body Device device);

    @GET("users/healthprofessionals/{user_id}/devices")
    Single<List<Device>> getAllDevices(@Path("user_id") String userId);

    @GET("users/{user_id}/devices/{device_id}")
    Single<Device> getDevice(@Path("user_id") String userId,
                             @Path("device_id") String deviceId);

    @PATCH("users/{user_id}/devices/{device_id}")
    Single<Device> updateDevice(@Path("user_id") String userId,
                                @Path("device_id") String deviceId,
                                @Body Device device);

    @DELETE("users/{user_id}/devices/{device_id}")
    Completable deleteDevice(@Path("user_id") String userId,
                             @Path("device_id") String deviceId);

    // pilotstudies.patients
    @POST("pilotstudies/{pilotstudy_id}/patients")
    Single<Patient> addPatient(@Path("pilotstudy_id") String pilotId,
                               @Body Patient patient);

    @GET("pilotstudies/{pilotstudy_id}/patients")
    Single<List<Patient>> getAllPatients(@Path("pilotstudy_id") String pilotId,
                                         @Query("sort") String sort,
                                         @Query("page") int page,
                                         @Query("limit") int limit);

    @GET("pilotstudies/{pilotstudy_id}/patients/{patient_id}")
    Single<Patient> getPatient(@Path("pilotstudy_id") String pilotId,
                               @Path("patient_id") String patientId);

    @PATCH("pilotstudies/{pilotstudy_id}/patients/{patient_id}")
    Single<Patient> updatePatient(@Path("pilotstudy_id") String pilotId,
                                  @Path("patient_id") String patientId,
                                  @Body Patient patient);

    @DELETE("pilotstudies/{pilotstudy_id}/patients/{patient_id}")
    Completable deletePatient(@Path("pilotstudy_id") String pilotId,
                              @Path("patient_id") String patientId);

    // patients.sleephabits
    @POST("patients/{patient_id}/sleephabits")
    Single<SleepHabit> addSleepHabit(@Path("patient_id") String patientId,
                                     @Body SleepHabit sleepHabit);

    @DELETE("patients/{patient_id}/sleephabits/{sleephabit_id}")
    Completable deleteSleepHabit(@Path("patient_id") String patientId,
                                 @Path("sleephabit_id") String sleepHabitId);

    // patients.physicalactivityhabits
    @POST("patients/{patient_id}/physicalactivityhabits")
    Single<PhysicalActivityHabit> addPhysicalActivityHabit(
            @Path("patient_id") String patientId,
            @Body PhysicalActivityHabit physicalActivityHabits
    );

    @DELETE("patients/{patient_id}/physicalactivityhabits/{physicalactivityhabits_id}")
    Completable deletePhysicalActivityHabit(
            @Path("patient_id") String patientId,
            @Path("physicalactivityhabits_id") String physicalActivityHabitId
    );

    // patients.feedinghabitsrecords
    @POST("patients/{patient_id}/feedinghabitsrecords")
    Single<FeedingHabitsRecord> addFeedingHabitsRecord(
            @Path("patient_id") String patientId,
            @Body FeedingHabitsRecord feedingHabitsRecord
    );

    @DELETE("patients/{patient_id}/feedinghabitsrecords/{feedinghabitsrecord_id}")
    Completable deleteFeedingHabitsRecord(
            @Path("patient_id") String patientId,
            @Path("feedinghabitsrecord_id") String feedingHabitsRecordId
    );

    // patients.medicalrecords
    @POST("patients/{patient_id}/medicalrecords")
    Single<MedicalRecord> addMedicalRecord(
            @Path("patient_id") String patientId,
            @Body MedicalRecord medicalRecord
    );

    @DELETE("patients/{patient_id}/medicalrecords/{medicalrecord_id}")
    Completable deleteMedicalRecord(
            @Path("patient_id") String patientId,
            @Path("medicalrecord_id") String medicalRecordId
    );
}
