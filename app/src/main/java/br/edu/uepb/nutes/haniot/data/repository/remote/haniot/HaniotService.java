package br.edu.uepb.nutes.haniot.data.repository.remote.haniot;

import com.google.gson.JsonObject;

import java.util.List;

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
import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface for HANIoT API.
 *
 * @author Copyright (c) 2018, NUTES/UEPB
 */
public interface HaniotService {
    //    String BASE_URL_HANIOT = "http://192.168.0.119:8080/"; // API GATEWAY LOCAL
    String BASE_URL_HANIOT = "https://200.129.82.8:8081/v1/"; // API GATEWAY LOCAL

    // auth
    @POST("auth")
    Single<UserAccess> auth(@Body User user);

    // auth
    @POST("auth/forgot")
    Single<Object> forgotPassword(@Body JsonObject email);

    // user
    @DELETE("users/{user_id}")
    Completable deleteUser(@Path("user_id") String userId);

    @PATCH("auth/password")
    Completable changePassword(@Body User user);

    // users.healthprofessionals
    @GET("healthprofessionals/{healthprofessional_id}")
    Single<HealthProfessional> getHealthProfessional(
            @Path("healthprofessional_id") String healthProfessionalId
    );

    // users.admin
    @GET("admins/{admin_id}")
    Single<Admin> getAdmin(
            @Path("admin_id") String adminId
    );

    @PATCH("healthprofessionals/{healthprofessional_id}")
    Single<HealthProfessional> updateHealthProfissional(
            @Path("healthprofessional_id") String healthProfessionalId,
            @Body User healthProfissional
    );

    @PATCH("admins/{admin_id}")
    Single<Admin> updateAdmin(
            @Path("admin_id") String healthProfessionalId,
            @Body User healthProfissional
    );

    // pilotstudies
    @GET("healthprofessionals/{healthprofessional_id}/pilotstudies")
    Single<List<PilotStudy>> getAllUserPilotStudies(
            @Path("healthprofessional_id") String healthProfessionalId
    );

    @GET("pilotstudies")
    Single<List<PilotStudy>> getAllPilotStudies();

    @POST("pilotstudies/{pilotstudy_id}/patients/{patient_id}")
    Single<Response<Void>> associatePatientToPilotStudy(@Path("pilotstudy_id") String pilotStudyId, @Path("patient_id") String patientId);

    @GET("pilotstudies/{pilotstudy_id}")
    Single<PilotStudy> getPilotStudy(@Path("pilotstudy_id") String pilotId);

    // users.measurements
    @POST("patients/{patient_id}/measurements")
    Single<Measurement> addMeasurement(@Path("patient_id") String patientId,
                                       @Body Measurement measurement);

    @POST("patients/{patient_id}/measurements")
    Single<Object> addMeasurement(@Path("patient_id") String patientId,
                                  @Body List<Measurement> measurement);

    @GET("patients/{patient_id}/measurements")
    Single<List<Measurement>> getAllMeasurements(@Path("patient_id") String userId,
                                                 @Query("sort") String sort,
                                                 @Query("start_at") String dateStart,
                                                 @Query("end_at") String dateEnd,
                                                 @Query("page") int page,
                                                 @Query("limit") int limit);

    @GET("patients/{patient_id}/measurements")
    Single<List<Measurement>> getAllMeasurements(@Path("patient_id") String userId,
                                                 @Query("type") String type,
                                                 @Query("sort") String sort,
                                                 @Query("start_at") String dateStart,
                                                 @Query("end_at") String dateEnd,
                                                 @Query("page") int page,
                                                 @Query("limit") int limit);

    @GET("patients/{patient_id}/measurements")
    Single<List<Measurement>> getAllMeasurements(@Path("patient_id") String userId,
                                                 @Query("type") String type,
                                                 @Query("sort") String sort,
                                                 @Query("page") int page,
                                                 @Query("limit") int limit);

    @GET("patients/{patient_id}/measurements")
    Single<List<Measurement>> getAllMeasurements(@Path("patient_id") String userId,
                                                 @Query("page") int page,
                                                 @Query("limit") int limit,
                                                 @Query("sort") String sort);

    @GET("patients/{patient_id}/measurements/{measurement_id}")
    Single<Measurement> getMeasurement(@Path("patient_id") String userId,
                                       @Path("measurement_id") String measurementId);

    @DELETE("patients/{patient_id}/measurements/{measurement_id}")
    Completable deleteMeasurement(@Path("patient_id") String userId,
                                  @Path("measurement_id") String measurementId);

    // user.devices
    @POST("patients/{patient_id}/devices")
    Single<Device> addDevice(@Path("patient_id") String userId, @Body Device device);

    @GET("patients/{patient_id}/devices")
    Single<List<Device>> getAllDevices(@Path("patient_id") String userId);

    @GET("patients/{patient_id}/devices/{device_id}")
    Single<Device> getDevice(@Path("patient_id") String userId,
                             @Path("device_id") String deviceId);

    @DELETE("patients/{patient_id}/devices/{device_id}")
    Completable deleteDevice(@Path("patient_id") String userId,
                             @Path("device_id") String deviceId);

    // pilotstudies.patients
    @POST("patients")
    Single<Patient> addPatient(@Body Patient patient);

    @GET("pilotstudies/{pilotstudy_id}/patients")
    Single<List<Patient>> getAllPilotStudiesPatients(@Path("pilotstudy_id") String pilotId,
                                                     @Query("sort") String sort,
                                                     @Query("page") int page,
                                                     @Query("limit") int limit);

    @GET("patients/{patient_id}")
    Single<Patient> getPatient(@Path("patient_id") String patientId);

    @PATCH("patients/{patient_id}")
    Single<Patient> updatePatient(@Path("patient_id") String patientId,
                                  @Body Patient patient);

    @DELETE("users/{patient_id}")
    Completable deletePatient(@Path("patient_id") String patientId);

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

    // patients.familycohesionrecord
    @POST("patients/{patient_id}/familycohesionrecords")
    Single<FamilyCohesionRecord> addFamilyCohesionRecord(
            @Path("patient_id") String patientId,
            @Body FamilyCohesionRecord familyCohesionRecord
    );

    @DELETE("patients/{patient_id}/familycohesionrecords/{familycohesionrecord_id}")
    Completable deleteFamilyCohesionRecord(
            @Path("patient_id") String patientId,
            @Path("familycohesionrecord_id") String familyCohesionRecordId
    );

    // patients.oralhealthrecords
    @POST("patients/{patient_id}/oralhealthrecords")
    Single<OralHealthRecord> addOralHealthRecord(
            @Path("patient_id") String patientId,
            @Body OralHealthRecord oralHealthRecord
    );

    @DELETE("patients/{patient_id}/oralhealthrecords/{oralhealthrecord_id}")
    Completable deleteOralHealthRecord(
            @Path("patient_id") String patientId,
            @Path("oralhealthrecord_id") String oralhealthrecordId
    );

    // patients.sociodemographicrecord
    @POST("patients/{patient_id}/sociodemographicrecords")
    Single<SociodemographicRecord> addSociodemographicRecord(
            @Path("patient_id") String patientId,
            @Body SociodemographicRecord sociodemographicRecord
    );

    @DELETE("patients/{patient_id}/sociodemographicrecords/{sociodemographicrecord_id}")
    Completable deleteSociodemographicRecord(
            @Path("patient_id") String patientId,
            @Path("sociodemographicrecord_id") String sociodemographicRecordId
    );

    @GET("patients/{patient_id}/sleephabits")
    Single<List<SleepHabit>> getAllSleepHabits(@Path("patient_id") String patientId,
                                               @Query("page") int page,
                                               @Query("limit") int limit,
                                               @Query("sort") String sort);

    @GET("patients/{patient_id}/physicalactivityhabits")
    Single<List<PhysicalActivityHabit>> getAllPhysicalActivity(@Path("patient_id") String patientId,
                                                               @Query("page") int page,
                                                               @Query("limit") int limit,
                                                               @Query("sort") String sort);

    @GET("patients/{patient_id}/feedinghabitsrecords")
    Single<List<FeedingHabitsRecord>> getAllFeedingHabits(@Path("patient_id") String patientId,
                                                          @Query("page") int page,
                                                          @Query("limit") int limit,
                                                          @Query("sort") String sort);

    @GET("patients/{patient_id}/medicalrecords")
    Single<List<MedicalRecord>> getAllMedicalRecord(@Path("patient_id") String patientId,
                                                    @Query("page") int page,
                                                    @Query("limit") int limit,
                                                    @Query("sort") String sort);

    @GET("patients/{patient_id}/sociodemographicrecords")
    Single<List<SociodemographicRecord>> getAllSociodemographic(@Path("patient_id") String patientId,
                                                                @Query("page") int page,
                                                                @Query("limit") int limit,
                                                                @Query("sort") String sort);

    @GET("patients/{patient_id}/familycohesionrecords")
    Single<List<FamilyCohesionRecord>> getAllFamilyCohesion(@Path("patient_id") String patientId,
                                                            @Query("page") int page,
                                                            @Query("limit") int limit,
                                                            @Query("sort") String sort);

    @GET("patients/{patient_id}/familycohesionrecords")
    Single<List<OralHealthRecord>> getAllOralHealth(@Path("patient_id") String patientId,
                                                    @Query("page") int page,
                                                    @Query("limit") int limit,
                                                    @Query("sort") String sort);

    // users.measurements
    @POST("patients/{patient_id}/nutritional/evaluations")
    Single<NutritionalEvaluationResult> saveNutritionalEvaluation(@Path("patient_id") String patientId,
                                                                  @Body NutritionalEvaluation nutritionalEvaluation);


    @GET("patients/{patient_id}/nutritional/questionnaires")
    Single<List<NutritionalQuestionnaire>> getAllNutritionalQuestionnaires(@Path("patient_id") String patientId,
                                                                           @Query("page") int page,
                                                                           @Query("limit") int limit,
                                                                           @Query("sort") String sort);

    @GET("patients/{patient_id}/odontological/questionnaires")
    Single<List<OdontologicalQuestionnaire>> getAllOdontologicalQuestionnaires(@Path("patient_id") String patientId,
                                                                               @Query("page") int page,
                                                                               @Query("limit") int limit,
                                                                               @Query("sort") String sort);

    @GET("patients/{patient_id}/odontological/questionnaires/last")
    Single<OdontologicalQuestionnaire> getLastOdontologicalQuestionnaires(@Path("patient_id") String patientId);

    @GET("patients/{patient_id}/nutritional/questionnaires/last")
    Single<NutritionalQuestionnaire> getLastNutritionalQuestionnaire(@Path("patient_id") String patientId);

    @GET("patients/{patient_id}/measurements/last")
    Single<MeasurementLastResponse> getLastMeasurements(@Path("patient_id") String patientId);


    @PUT("patients/{patient_id}/nutritional/questionnaires/{questionnaire_id}/{resource_name}")
    Single<Object> updateNutritionalQuestionnaire(
            @Path("patient_id") String patientId,
            @Path("questionnaire_id") String questionnaireId,
            @Path("resource_name") String resourceName,
            @Body Object object
    );

    @PUT("patients/{patient_id}/odontological/questionnaires/{questionnaire_id}/{resource_name}")
    Single<Object> updateOdontologicalQuestionnaire(
            @Path("patient_id") String patientId,
            @Path("questionnaire_id") String questionnaireId,
            @Path("resource_name") String resourceName,
            @Body Object object
    );

    @POST("patients/{patient_id}/odontological/questionnaires")
    Single<OdontologicalQuestionnaire> saveOdontologicalQuestionnaire(
            @Path("patient_id") String patientId,
            @Body OdontologicalQuestionnaire odontologicalQuestionnaire
    );

    @POST("patients/{patient_id}/nutritional/questionnaires")
    Single<NutritionalQuestionnaire> saveNutritionalQuestionnaire(
            @Path("patient_id") String patientId,
            @Body NutritionalQuestionnaire nutritionalQuestionnaire
    );

}
