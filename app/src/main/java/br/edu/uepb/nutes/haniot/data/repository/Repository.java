package br.edu.uepb.nutes.haniot.data.repository;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.List;

import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.FeedingHabitsDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.MedicalRecordDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.PatientDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.PhysicalActivityHabitsDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.PilotStudyDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.SleepHabitsDAO;
import br.edu.uepb.nutes.haniot.data.model.dao.UserDAO;
import br.edu.uepb.nutes.haniot.data.model.model.Admin;
import br.edu.uepb.nutes.haniot.data.model.model.Device;
import br.edu.uepb.nutes.haniot.data.model.model.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.model.HealthProfessional;
import br.edu.uepb.nutes.haniot.data.model.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.model.MeasurementLastResponse;
import br.edu.uepb.nutes.haniot.data.model.model.MedicalRecord;
import br.edu.uepb.nutes.haniot.data.model.model.NutritionalEvaluation;
import br.edu.uepb.nutes.haniot.data.model.model.NutritionalEvaluationResult;
import br.edu.uepb.nutes.haniot.data.model.model.NutritionalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.model.model.OdontologicalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.model.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.model.PhysicalActivityHabit;
import br.edu.uepb.nutes.haniot.data.model.model.PilotStudy;
import br.edu.uepb.nutes.haniot.data.model.model.SleepHabit;
import br.edu.uepb.nutes.haniot.data.model.model.User;
import br.edu.uepb.nutes.haniot.data.model.model.UserAccess;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.Response;

/**
 * Classe responsável por gerenciar o repositório local e o repositório remoto
 */
public class Repository {

    private static Repository instance;

    public static Repository getInstance(Context context) {
        if (instance == null)
            instance = new Repository(context);
        return instance;
    }

    private Repository(Context context) {
        this.haniotNetRepository = HaniotNetRepository.getInstance(context);

        this.deviceDAO = DeviceDAO.getInstance(context);
        this.feedingHabitsDAO = FeedingHabitsDAO.getInstance(context);
        this.measurementDAO = MeasurementDAO.getInstance(context);
        this.medicalRecordDAO = MedicalRecordDAO.getInstance(context);
        this.patientDAO = PatientDAO.getInstance(context);
        this.physicalActivityHabitsDAO = PhysicalActivityHabitsDAO.getInstance(context);
        this.pilotStudyDAO = PilotStudyDAO.getInstance(context);
        this.sleepHabitsDAO = SleepHabitsDAO.getInstance(context);
        this.userDAO = UserDAO.getInstance(context);
    }

    private HaniotNetRepository haniotNetRepository; // Repository remote
    // repositories local
    private DeviceDAO deviceDAO;
    private FeedingHabitsDAO feedingHabitsDAO;
    private MeasurementDAO measurementDAO;
    private MedicalRecordDAO medicalRecordDAO;
    private PatientDAO patientDAO;
    private PhysicalActivityHabitsDAO physicalActivityHabitsDAO;
    private PilotStudyDAO pilotStudyDAO;
    private SleepHabitsDAO sleepHabitsDAO;
    private UserDAO userDAO;

    // ------------- DEVICE DAO --------------------------

    /**
     * Adds a new device to the database.
     *
     * @param device DeviceOB
     * @return boolean
     */
    public Single<Device> saveDevice(@NonNull Device device) {
        return haniotNetRepository.saveDevice(device);
//        return deviceDAO.save(Convert.convertDevice(device));
    }


    /**
     * Select a DeviceOB according to id.
     *
     * @param id long
     * @return Object
     */
    public Device getDevice(long id) {
        return Convert.convertDevice(deviceDAO.get(id));
    }

    /**
     * Select a DeviceOB according to address and userId.
     *
     * @param address String
     * @param userId  long
     * @return Object
     */
    public Device getDevice(@NonNull String address, String userId) {
        return Convert.convertDevice(deviceDAO.get(address, userId));
    }

    /**
     * Retrieves all device according to userId and type.
     *
     * @param userId {@link String}
     * @param type   {@link String}
     * @return DeviceOB
     */
    public Device getDeviceByType(@NonNull String userId, String type) {
//        return haniotNetRepository.getDevice()
        return Convert.convertDevice(deviceDAO.getByType(userId, type));
    }

    /**
     * Update device.
     * According to your _id and _id user provided by the remote server.
     *
     * @param device DeviceOB
     * @return boolean
     */
    public boolean updateDevice(@NonNull Device device) {
        return deviceDAO.update(Convert.convertDevice(device));
    }

    /**
     * Removes device passed as parameter.
     *
     * @param device DeviceOB
     * @return boolean
     */
    public boolean removeDevice(@NonNull Device device) {
        return deviceDAO.remove(Convert.convertDevice(device));
    }

    public boolean removeDevice(String userId, String idDevice) {
        DisposableManager.add(haniotNetRepository
                .deleteDevice(userId, idDevice).subscribe(() -> {

                }));
        return deviceDAO.remove(idDevice);
    }

    /**
     * Removes device passed as parameter.
     *
     * @param userId   String
     * @param deviceId String
     * @return boolean
     */
    public Completable deleteDevice(@NonNull String userId, @NonNull String deviceId) {
        deviceDAO.remove(deviceId);
        return haniotNetRepository.deleteDevice(userId, deviceId);
    }

    /**
     * Removes all devices.
     * According to userId user.
     *
     * @param userId long
     * @return long - Total number of itemsList removed
     */
    public boolean removeAllDevices(@NonNull long userId) {
        return deviceDAO.removeAll(userId);
    }

    /**
     * Removes all devices.
     * According to userId user.
     *
     * @param userId {@link String}
     * @return long - Total number of itemsList removed
     */
    public boolean removeAllDevices(@NonNull String userId) {
        return deviceDAO.removeAll(userId);
    }

    // ------------- Feeding Habits DAO ---------------------


    //    Search FeedingHabitsRecordOB by id
    public FeedingHabitsRecord getFeedingHabitsRecordFromPatientId(@NonNull String _id) {
        return Convert.convertFeedingHabitsRecord(feedingHabitsDAO.getFromPatientId(_id));
    }

    //    get all FeedingHabitsRecordOB on database
    public List<FeedingHabitsRecord> getFeedingHabitsRecord() {
        return Convert.listFeedingHabitsRecordToModel(feedingHabitsDAO.get());
    }

    //    save FeedingHabitsRecordOB
    public boolean saveFeedingHabitsRecord(@NonNull FeedingHabitsRecord feedingHabitsRecord) {
        return feedingHabitsDAO.save(Convert.convertFeedingHabitsRecord(feedingHabitsRecord));
    }

    //    update FeedingHabitsRecordOB
    public boolean updateFeedingHabitsRecord(@NonNull FeedingHabitsRecord feedingHabitsRecord) {
        return feedingHabitsDAO.update(Convert.convertFeedingHabitsRecord(feedingHabitsRecord));
    }

    //    remove FeedingHabitsRecordOB
    public boolean removeFeedingHabitsRecord(@NonNull FeedingHabitsRecord feedingHabitsRecord) {
        return feedingHabitsDAO.remove(Convert.convertFeedingHabitsRecord(feedingHabitsRecord));
    }


    // ----------- MEASUREMENT DAO -----------------------

    /**
     * Adds a new measurement to the database.
     *
     * @param measurement
     * @return boolean
     */
    public Single<Measurement> saveMeasurement(@NonNull Measurement measurement) {
        return haniotNetRepository.saveMeasurement(measurement);
//        return measurementDAO.save(Convert.convertMeasurement(measurement));
    }

    public Single<Object> saveMeasurement(List<Measurement> measurements) {
        return haniotNetRepository.saveMeasurement(measurements);
    }

    /**
     * Remove measurement.
     *
     * @param measurementId
     * @return boolean
     */
    public Completable deleteMeasurement(@NonNull String patientId, @NonNull String measurementId) {
        measurementDAO.remove(patientId, measurementId);
        return haniotNetRepository.deleteMeasurement(patientId, measurementId);
    }

    /**
     * Remove all measurements associated with user.
     *
     * @param userId long
     * @return boolean
     */
    public boolean removeAllMeasurements(@NonNull String userId) {
        return measurementDAO.removeAll(userId);
    }

    /**
     * Select a measurement.
     *
     * @param userId        String
     * @param measurementId String
     * @return Object
     */
    public Single<Measurement> getMeasurement(@NonNull String userId, @NonNull String measurementId) {
        return haniotNetRepository.getMeasurement(userId, measurementId);
//        return Convert.convertMeasurement(measurementDAO.get(id));
    }

    public Single<List<Measurement>> getMeasurements(String patientId, String measurementType, String sort, String dateStart, String dateEnd, int page, int limitPerPage) {
        return haniotNetRepository.getAllMeasurementsByType(patientId, measurementType, sort, dateStart, dateEnd, page, limitPerPage);
    }

    /**
     * Select all measurements associated with the user.
     *
     * @param userId long
     * @param offset int
     * @param limit  int
     * @return List<MeasurementOB>
     */
    public List<Measurement> listMeasurements(@NonNull long userId, @NonNull int offset, @NonNull int limit) {
        return Convert.listMeasurementsToModel(measurementDAO.list(userId, offset, limit));
    }

    /**
     * Select all measurements of a type associated with the user.
     *
     * @param type   {@link String}
     * @param userId long
     * @param offset int
     * @param limit  int
     * @return List<MeasurementOB>
     */
    public List<Measurement> listMeasurements(@NonNull String type, @NonNull String userId, @NonNull int offset, @NonNull int limit) {
        return Convert.listMeasurementsToModel(measurementDAO.list(type, userId, offset, limit));
    }

    /**
     * Select all measurements of a user that were not sent to the remote server.
     *
     * @param userId long
     * @return List<MeasurementOB>
     */
    public List<Measurement> getNotSentMeasurement(@NonNull long userId) {
        return Convert.listMeasurementsToModel(measurementDAO.getNotSent(userId));
    }

    // ---------- MEDICAL RECORD DAO ----------------

    //    Search FeedingHabitsRecordOB by id
    public MedicalRecord getMedicalRecordFromPatientId(@NonNull String _id) {
        return Convert.convertMedicalRecord(medicalRecordDAO.getFromPatientId(_id));
    }

    //    get all FeedingHabitsRecordOB on database
    public List<MedicalRecord> getMedicalRecord() {
        return Convert.listMedicalRecordsToModel(medicalRecordDAO.get());
    }

    //    save FeedingHabitsRecordOB
    public boolean saveMedicalRecord(@NonNull MedicalRecord medicalRecord) {
        return medicalRecordDAO.save(Convert.convertMedicalRecord(medicalRecord));
    }

    //    update FeedingHabitsRecordOB
    public boolean updateMedicalRecord(@NonNull MedicalRecord medicalRecord) {
        return medicalRecordDAO.update(Convert.convertMedicalRecord(medicalRecord));
    }

    //    remove FeedingHabitsRecordOB
    public boolean removeMedicalRecord(@NonNull MedicalRecord medicalRecord) {
        return medicalRecordDAO.remove(Convert.convertMedicalRecord(medicalRecord));
    }

    // --------- PATIENT DAO --------------

    public Single<Patient> getPatient(@NonNull String _id) {
        return haniotNetRepository.getPatient(_id);
//        return Convert.convertPatient(patientDAO.get(_id));
    }

    public Single<Patient> savePatient(@NonNull Patient patient) {
        return haniotNetRepository.savePatient(patient);

//        return patientDAO.save(Convert.convertPatient(patient));
    }

    public Single<Patient> updatePatient(@NonNull Patient patient) {
        return haniotNetRepository.updatePatient(patient);

//        return patientDAO.update(Convert.convertPatient(patient));
    }

    // ------------ PHYSICAL ACTIVITY HABITS DAO -----------


    //    Search FeedingHabitsRecordOB by id
    public PhysicalActivityHabit getPhysicalActivityHabitFromPatientId(@NonNull String _id) {
        return Convert.convertPhysicalActivityHabit(physicalActivityHabitsDAO.getFromPatientId(_id));
    }

    //    get all FeedingHabitsRecordOB on database
    public List<PhysicalActivityHabit> getPhysicalActivityHabits() {
        return Convert.convertListPhysicalActivityHabit(physicalActivityHabitsDAO.get());
    }

    //    save FeedingHabitsRecordOB
    public boolean savePhysicalActivityHabit(@NonNull PhysicalActivityHabit physicalActivityHabit) {
        return physicalActivityHabitsDAO.save(Convert.convertPhysicalActivityHabit(physicalActivityHabit));
    }

    //    update FeedingHabitsRecordOB
    public boolean updatePhysicalActivityHabit(@NonNull PhysicalActivityHabit physicalActivityHabit) {
        return physicalActivityHabitsDAO.update(Convert.convertPhysicalActivityHabit(physicalActivityHabit));
    }

    //    remove FeedingHabitsRecordOB
    public boolean removePhysicalActivityHabit(@NonNull PhysicalActivityHabit physicalActivityHabit) {
        return physicalActivityHabitsDAO.remove(Convert.convertPhysicalActivityHabit(physicalActivityHabit));
    }

    // ----------------- PILOT STUDY DAO -------------------------

    public List<PilotStudy> getAllPilotStudies() {
        return Convert.listPilotStudiesToModel(pilotStudyDAO.list(null));
    }

    public List<PilotStudy> getPilotStudiesByUserId(String userId) {
        return Convert.listPilotStudiesToModel(pilotStudyDAO.list(userId));
    }

    public boolean removeAllPilotStudiesy(@NonNull String userId) {
        return pilotStudyDAO.removeAll(userId);
    }

    // --------------- SLEEP HABITS DAO --------------------

    //    Search FeedingHabitsRecordOB by id
    public SleepHabit getSleepHabitsFromPatientId(@NonNull String _id) {
        return Convert.convertSleepHabit(sleepHabitsDAO.getFromPatientId(_id));
    }

    //    get all FeedingHabitsRecordOB on database
    public List<SleepHabit> getSleepHabits() {
        return Convert.listSleepHabitsToModel(sleepHabitsDAO.get());
    }

    //    save FeedingHabitsRecordOB
    public boolean saveSleepHabits(@NonNull SleepHabit sleepHabit) {
        return sleepHabitsDAO.save(Convert.convertSleepHabit(sleepHabit));
    }

    //    update FeedingHabitsRecordOB
    public boolean updateSleepHabits(@NonNull SleepHabit sleepHabit) {
        return sleepHabitsDAO.update(Convert.convertSleepHabit(sleepHabit));
    }

    //    remove FeedingHabitsRecordOB
    public boolean removeSleepHabits(@NonNull SleepHabit sleepHabit) {
        return sleepHabitsDAO.remove(Convert.convertSleepHabit(sleepHabit));
    }

    // ---------- USER DAO ---------------

    /**
     * Selects user based on local id
     *
     * @param id long
     * @return UserOB
     */
    public User getUser(@NonNull long id) {
        return Convert.convertUser(userDAO.get(id));
    }

    public Single<Admin> updateAdmin(Admin admin) {
        return haniotNetRepository.updateAdmin(admin);
    }

    public Single<HealthProfessional> updateHealthProfissional(HealthProfessional healthProfessional) {
        return haniotNetRepository.updateHealthProfissional(healthProfessional);
    }

    public Single<Response<Void>> associatePatientToPilotStudy(String pilotStudyId, String patientId) {
        return haniotNetRepository.associatePatientToPilotStudy(pilotStudyId, patientId);
    }

    public Single<List<Measurement>> getAllMeasurementsByType(String userId, String typeMeasurement,
                                                              String timestamp, String dateStart, String dateEnd, int page, int limit) {
        return haniotNetRepository.getAllMeasurementsByType(
                userId, typeMeasurement, timestamp, dateStart, dateEnd, page, limit);
    }

    public Single<HealthProfessional> getHealthProfissional(String _id) {
        return haniotNetRepository.getHealthProfissional(_id);
    }

    public Single<Admin> getAdmin(String _id) {
        return haniotNetRepository.getAdmin(_id);
    }

    public Single<List<Device>> getAllDevices(String patientId) {
        return haniotNetRepository.getAllDevices(patientId);
    }

    public Single<UserAccess> auth(String username, String password) {
        return haniotNetRepository.auth(username, password);
    }

    public Completable changePassword(User user) {
        return haniotNetRepository.changePassword(user);
    }

    public Single<Object> forgotPassword(JsonObject email) {
        return haniotNetRepository.forgotPassword(email);
    }

    public Single<List<NutritionalQuestionnaire>> getAllNutritionalQuestionnaires(String patientId, int page, int limit, String sort) {
        return haniotNetRepository.getAllNutritionalQuestionnaires(patientId, page, limit, sort);
    }

    public Single<List<OdontologicalQuestionnaire>> getAllOdontologicalQuestionnaires(String patientId, int page, int limit, String sort) {
        return haniotNetRepository.getAllOdontologicalQuestionnaires(patientId, page, limit, sort);
    }

    public Completable deleteUserById(String userId) {
        return haniotNetRepository.deleteUserById(userId);
    }

    public Single<List<Patient>> getAllPatients(String pilotStudyId, String sort, int page, int limit) {
        return haniotNetRepository.getAllPatients(pilotStudyId, sort, page, limit);
    }

    public Completable deletePatient(String patientId) {
        return haniotNetRepository.deletePatient(patientId);
    }

    public Single<NutritionalQuestionnaire> getLastNutritionalQuestionnaire(String patientId) {
        return haniotNetRepository.getLastNutritionalQuestionnaire(patientId);
    }

    public Single<MeasurementLastResponse> getLastMeasurements(String patientId) {
        return haniotNetRepository.getLastMeasurements(patientId);
    }

    public Single<NutritionalEvaluationResult> saveNutritionalEvaluation(NutritionalEvaluation nutritionalEvaluation) {
        return haniotNetRepository.saveNutritionalEvaluation(nutritionalEvaluation);
    }

    public Single<NutritionalQuestionnaire> saveNutritionalQuestionnaire(String patientId, NutritionalQuestionnaire nutritionalQuestionnaire) {
        return haniotNetRepository.saveNutritionalQuestionnaire(patientId, nutritionalQuestionnaire);
    }

    public Single<Object> updateNutritionalQuestionnaire(String patientId, String questionnaireId, String resourceName, Object object) {
        return haniotNetRepository.updateNutritionalQuestionnaire(patientId, questionnaireId, resourceName, object);
    }

    public Single<OdontologicalQuestionnaire> saveOdontologicalQuestionnaire(String patientId, OdontologicalQuestionnaire odontologicalQuestionnaire) {
        return haniotNetRepository.saveOdontologicalQuestionnaire(patientId, odontologicalQuestionnaire);
    }

    public Single<Object> updateOdontologicalQuestionnaire(String patientId, String questionnaireId, String resourceName, Object object) {
        return haniotNetRepository.updateOdontologicalQuestionnaire(patientId, questionnaireId, resourceName, object);
    }
}
