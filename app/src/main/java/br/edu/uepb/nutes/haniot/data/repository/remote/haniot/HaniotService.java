package br.edu.uepb.nutes.haniot.data.repository.remote.haniot;

import java.util.List;

import br.edu.uepb.nutes.haniot.model.Device;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.Patient;
import br.edu.uepb.nutes.haniot.model.PhysicalActivityHabits;
import br.edu.uepb.nutes.haniot.model.SleepHabit;
import br.edu.uepb.nutes.haniot.model.User;
import br.edu.uepb.nutes.haniot.model.UserAccess;
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
    Observable<Void> deleteUser(@Path("user_id") String userId);

    @PATCH("users/{user_id}/password")
    Observable<Void> changePassword(@Path("user_id") String userId);

    // users.measurements
    @POST("users/{user_id}/measurements")
    Observable<Measurement> saveMeasurement(@Path("user_id") String userId,
                                            @Body Measurement measurement);

    @GET("users/{user_id}/measurements")
    Observable<List<Measurement>> getAllMeasurements(@Path("user_id") String userId,
                                                     @Query("sort") String sort,
                                                     @Query("page") int page,
                                                     @Query("limit") int limit);

    @GET("users/{user_id}/measurements/{measurement_id}")
    Observable<Measurement> getMeasurement(@Path("user_id") String userId,
                                           @Path("measurement_id") Measurement measurementId);

    @PATCH("users/{user_id}/measurements/{measurement_id}")
    Observable<Measurement> updateMeasurement(@Path("user_id") String userId,
                                              @Path("measurement_id") String measurementId,
                                              @Body Measurement measurement);

    @DELETE("users/{user_id}/measurements/{measurement_id}")
    Observable<Void> deleteMeasurement(@Path("user_id") String userId,
                                       @Path("measurement_id") String measurementId);

    //user.devices
    @POST("users/{user_id}/devices")
    Observable<Device> saveDevice(@Path("user_id") String userId, @Body Device device);

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
    Observable<Void> deleteDevice(@Path("user_id") String userId,
                                  @Path("device_id") String deviceId);

    //pilotstudies.patients
    @POST("pilotstudies/{pilotstudy_id}/patients")
    Observable<Patient> addPatient(@Path("pilotstudy_id") String pilotstudyId,
                                   @Body SleepHabit sleepHabit);

    @GET("pilotstudies/{pilotstudy_id}/patients")
    Observable<List<Patient>> getAllPatients(@Path("pilotstudy_id") String pilotstudyId,
                                             @Query("sort") String sort,
                                             @Query("page") int page,
                                             @Query("limit") int limit);

    @GET("pilotstudies/{pilotstudy_id}/patients/{patient_id}")
    Observable<Patient> getPatientById(@Path("pilotstudy_id") String pilotstudyId,
                                       @Path("patient_id") String patitentId);

    @PATCH("pilotstudies/{pilotstudy_id}/patients/{patient_id}")
    Observable<Patient> updatePatient(@Path("pilotstudy_id") String pilotstudyId,
                                      @Path("patient_id") String patitentId,
                                      @Body Patient patient);

    @DELETE("pilotstudies/{pilotstudy_id}/patients/{patient_id}")
    Observable<Void> deletePatient(@Path("pilotstudy_id") String pilotstudyId,
                                   @Path("patient_id") String patitentId);


    //pilotstudies.patients.sleephabits
    @POST("pilotstudies/{pilotstudy_id}/patients/{patient_id}/sleephabits")
    Observable<SleepHabit> addSleepHabit(@Path("pilotstudy_id") String pilotstudyId,
                                         @Path("pilotstudy_id") String patientId,
                                         @Body SleepHabit sleepHabit);

    @GET("pilotstudies/{pilotstudy_id}/patients/{patient_id}/sleephabits")
    Observable<List<SleepHabit>> getAllSleepHabits(@Path("pilotstudy_id") String pilotstudyId,
                                                   @Path("patient_id") String patitentId,
                                                   @Query("sort") String sort,
                                                   @Query("page") int page,
                                                   @Query("limit") int limit);

    @GET("pilotstudies/{pilotstudy_id}/patients/{patient_id}/sleephabits/{sleephabit_id}")
    Observable<SleepHabit> getSleepDataFromPatient(@Path("pilotstudy_id") String pilotstudyId,
                                                   @Path("patient_id") String patitentId,
                                                   @Path("sleephabit_id") String sleephabitId);

    @PATCH("pilotstudies/{pilotstudy_id}/patients/{patient_id}/sleephabits/{sleephabit_id}")
    Observable<SleepHabit> updateSleepHabit(@Path("pilotstudy_id") String pilotstudyId,
                                            @Path("patient_id") String patitentId,
                                            @Path("sleephabit_id") String sleephabitId,
                                            @Body SleepHabit sleepHabit);

    @DELETE("pilotstudies/{pilotstudy_id}/patients/{patient_id}/sleephabits/{sleephabit_id}")
    Observable<Void> deletePatient(@Path("pilotstudy_id") String pilotstudyId,
                                   @Path("patient_id") String patitentId,
                                   @Path("sleephabit_id") String sleephabitId);

    //pilotstudies.patients.physicalactivityhabits
    @POST("pilotstudies/{pilotstudy_id}/patients/{patient_id}/physicalactivityhabits")
    Observable<PhysicalActivityHabits> addPhysicalActivityHabit(@Path("pilotstudy_id") String pilotstudyId,
                                                                @Path("pilotstudy_id") String patientId,
                                                                @Body PhysicalActivityHabits physicalActivityHabits);

    @GET("pilotstudies/{pilotstudy_id}/patients/{patient_id}/physicalactivityhabits")
    Observable<List<PhysicalActivityHabits>> getAllPhysicalActivityHabits(@Path("pilotstudy_id") String pilotstudyId,
                                                                          @Path("patient_id") String patitentId,
                                                                          @Query("sort") String sort,
                                                                          @Query("page") int page,
                                                                          @Query("limit") int limit);

    @GET("pilotstudies/{pilotstudy_id}/patients/{patient_id}/physicalactivityhabits/{physicalactivityhabits_id}")
    Observable<PhysicalActivityHabits> getPhysicalActivityHabits(@Path("pilotstudy_id") String pilotstudyId,
                                                                 @Path("patient_id") String patitentId,
                                                                 @Path("physicalactivityhabits_id") String physicalactivityhabitsId);

}
