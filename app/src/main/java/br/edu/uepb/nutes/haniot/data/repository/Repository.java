package br.edu.uepb.nutes.haniot.data.repository;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.List;

import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.data.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.data.dao.PatientDAO;
import br.edu.uepb.nutes.haniot.data.dao.PilotStudyDAO;
import br.edu.uepb.nutes.haniot.data.dao.UserDAO;
import br.edu.uepb.nutes.haniot.data.dao.nutritionalDao.FeedingHabitsDAO;
import br.edu.uepb.nutes.haniot.data.dao.nutritionalDao.MedicalRecordDAO;
import br.edu.uepb.nutes.haniot.data.dao.nutritionalDao.PhysicalActivityHabitsDAO;
import br.edu.uepb.nutes.haniot.data.dao.nutritionalDao.SleepHabitsDAO;
import br.edu.uepb.nutes.haniot.data.model.Admin;
import br.edu.uepb.nutes.haniot.data.model.Device;
import br.edu.uepb.nutes.haniot.data.model.HealthProfessional;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.MeasurementLastResponse;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.PilotStudy;
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.model.UserAccess;
import br.edu.uepb.nutes.haniot.data.model.nutritional.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.nutritional.MedicalRecord;
import br.edu.uepb.nutes.haniot.data.model.nutritional.NutritionalEvaluation;
import br.edu.uepb.nutes.haniot.data.model.nutritional.NutritionalEvaluationResult;
import br.edu.uepb.nutes.haniot.data.model.nutritional.NutritionalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.model.nutritional.PhysicalActivityHabit;
import br.edu.uepb.nutes.haniot.data.model.nutritional.SleepHabit;
import br.edu.uepb.nutes.haniot.data.model.odontological.OdontologicalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
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

        this.mContext = context;
    }

    private HaniotNetRepository haniotNetRepository;

    private DeviceDAO deviceDAO;
    private FeedingHabitsDAO feedingHabitsDAO;
    private MeasurementDAO measurementDAO;
    private MedicalRecordDAO medicalRecordDAO;
    private PatientDAO patientDAO;
    private PhysicalActivityHabitsDAO physicalActivityHabitsDAO;
    private PilotStudyDAO pilotStudyDAO;
    private SleepHabitsDAO sleepHabitsDAO;
    private UserDAO userDAO;

    private Context mContext;

    public void syncronize() {
        if (ConnectionUtils.internetIsEnabled(this.mContext)) {
            List<Device> devices = deviceDAO.getAllNotSync();
            for (Device aux : devices) {
                DisposableManager.add(
                        haniotNetRepository.saveDevice(aux)
                                .subscribe(device -> {
                                    device.setSync(true);
                                    deviceDAO.save(device);
                                })
                );
            }
            // varrer todos os DAOs em busca de dados não sincronizados

            // for em todos os devices
            // if (device.isSync == false) envia pro haniotNetRepository
            // device.setSync(true)

//            List<User> users = userDAO.get
//
//            DisposableManager.add(
//                    haniotNetRepository.getAllDevices()
//            );

            // baixar os mais atuais para o repositorio local
            deviceDAO.removeSyncronized();
//            DisposableManager.add(
//                    haniotNetRepository.getAllDevices()
//            );
        }
    }

    // ------------- DEVICE DAO --------------------------

    /**
     * Adds a new device to the database.
     *
     * @param device DeviceOB
     * @return boolean
     */
    public Single<Device> saveDevice(@NonNull Device device) {
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.saveDevice(device)
                    .map(device1 -> {
                        deviceDAO.save(device1);
                        return device1;
                    });
        } else {
            deviceDAO.save(device);
            return Single.just(device);
//            if (deviceDAO.save(device)) { // salvo com sucesso
//                return Single.just(device);
//            } else {
//                return Single.error();
//            }
        }
    }

    public Single<List<Device>> getAllDevices(String patientId) {
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.getAllDevices(patientId)
                    .map(devices -> {
                        deviceDAO.save(devices);
                        return devices;
                    });
        } else {
            List<Device> dev = deviceDAO.getAllDevices(patientId);
            return Single.just(dev);
        }
    }

    /**
     * Retrieves all device according to userId and type.
     *
     * @param userId {@link String}
     * @param type   {@link String}
     * @return DeviceOB
     */
    public Device getDeviceByType(@NonNull String userId, String type) {
        syncronize(); // Download devices to local repository
        return deviceDAO.getByType(userId, type);
    }

    /**
     * Removes device passed as parameter.
     *
     * @param userId   String
     * @param deviceId String
     * @return boolean
     */
    public Completable deleteDevice(@NonNull String userId, @NonNull String deviceId) {
        return haniotNetRepository.deleteDevice(userId, deviceId)
                .doOnComplete(() -> {
                            deviceDAO.remove(deviceId);
                        }
                );
    }

    /**
     * Removes all devices.
     * According to userId user.
     *
     * @param userId {@link String}
     * @return long - Total number of itemsList removed
     */
    public boolean removeAllDevicesLocal(@NonNull String userId) {
        return deviceDAO.removeAll(userId);
    }

    // ----------- MEASUREMENT DAO -----------------------

    /**
     * Adds a new measurement to the database.
     *
     * @param measurement
     * @return boolean
     */
    public Single<Measurement> saveMeasurement(@NonNull Measurement measurement) {
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.saveMeasurement(measurement)
                    .map(measurement1 -> {
                        measurement1.setSync(true);
                        measurementDAO.save(measurement1);
                        return measurement1;
                    });
        } else {
            measurementDAO.save(measurement);
            return Single.just(measurement);

        }
    }

    public Single<Object> saveMeasurement(List<Measurement> measurements) {
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.saveMeasurement(measurements)
                    .map(measurements1 -> {
                        for (Measurement m : (List<Measurement>) measurements1)
                            m.setSync(true);
                        measurementDAO.saveAll(measurements);
                        return measurements1;
                    });
        } else {
            measurementDAO.saveAll(measurements);
            return Single.just(measurements);
        }
    }

    /**
     * Remove measurement.
     *
     * @param measurementId
     * @return boolean
     */
    public Completable deleteMeasurement(@NonNull String patientId, @NonNull String measurementId) {
        return haniotNetRepository.deleteMeasurement(patientId, measurementId)
                .doOnComplete(() -> {
                    measurementDAO.remove(patientId, measurementId);
                });
    }

    /**
     * Select a measurement.
     *
     * @param userId        String
     * @param measurementId String
     * @return Object
     */
    public Single<Measurement> getMeasurement(@NonNull String userId, @NonNull String measurementId) {
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.getMeasurement(userId, measurementId);
        } else {
            return Single.just(measurementDAO.get(userId, measurementId));
        }
    }

    public Single<List<Measurement>> getAllMeasurementsByType(String userId, String typeMeasurement,
                                                              String sort, String dateStart,
                                                              String dateEnd, int page, int limit) {
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.getAllMeasurementsByType(
                    userId, typeMeasurement, sort, dateStart, dateEnd, page, limit);
        } else {
            List<Measurement> aux = measurementDAO.getMeasurementsByType(userId, typeMeasurement, sort, dateStart, dateEnd, page, limit);
            return Single.just(aux);
        }
    }

    // --------- PATIENT DAO --------------

    public Single<Patient> getPatient(@NonNull String _id) {
        Patient p = patientDAO.get(_id);

        if (p != null) {
            return Single.just(p);
        } else {
            return haniotNetRepository.getPatient(_id);
        }
    }

    public Single<Patient> savePatient(@NonNull Patient patient) {
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.savePatient(patient)
                    .map(patient1 -> {
                        patient1.setSync(true);
                        patientDAO.save(patient1);
                        return patient1;
                    });
        } else {
            patientDAO.save(patient);
            return Single.just(patient);
        }
    }

    public Single<Patient> updatePatient(@NonNull Patient patient) {
        patientDAO.update(patient);
        return haniotNetRepository.updatePatient(patient);
    }

    public Single<List<Patient>> getAllPatients(String pilotStudyId, String sort, int page, int limit) {
        return haniotNetRepository.getAllPatients(pilotStudyId, sort, page, limit);
    }

    public Completable deletePatient(String patientId) {
        return haniotNetRepository.deletePatient(patientId)
                .doOnComplete(() -> {
                    patientDAO.remove(patientId);
                });
    }

    public Single<Response<Void>> associatePatientToPilotStudy(String pilotStudyId, String patientId) {
        return haniotNetRepository.associatePatientToPilotStudy(pilotStudyId, patientId);
    }

    // ------------ NUTRITIONAL DAO -----------

    public Single<List<NutritionalQuestionnaire>> getAllNutritionalQuestionnaires(String patientId, int page, int limit, String sort) {
        return haniotNetRepository.getAllNutritionalQuestionnaires(patientId, page, limit, sort);
    }

    public Single<NutritionalQuestionnaire> getLastNutritionalQuestionnaire(String patientId) {
        return haniotNetRepository.getLastNutritionalQuestionnaire(patientId);
    }

    /**
     * Select Last measurements of a patient
     * Necessary connection
     *
     * @param patientId String
     * @return
     */
    public Single<MeasurementLastResponse> getLastMeasurements(String patientId) {
        return haniotNetRepository.getLastMeasurements(patientId);
    }

    public Single<NutritionalEvaluationResult> saveNutritionalEvaluation(NutritionalEvaluation nutritionalEvaluation) {
        return haniotNetRepository.saveNutritionalEvaluation(nutritionalEvaluation);
    }

    public Single<NutritionalQuestionnaire> saveNutritionalQuestionnaire(String patientId, NutritionalQuestionnaire nutritionalQuestionnaire) {
        this.savePhysicalActivityHabit(nutritionalQuestionnaire.getPhysicalActivityHabit());
        this.saveSleepHabits(nutritionalQuestionnaire.getSleepHabit());
        this.saveMedicalRecord(nutritionalQuestionnaire.getMedicalRecord());
        this.saveFeedingHabitsRecord(nutritionalQuestionnaire.getFeedingHabitsRecord());
        return haniotNetRepository.saveNutritionalQuestionnaire(patientId, nutritionalQuestionnaire);
    }

    public Single<Object> updateNutritionalQuestionnaire(String patientId, String questionnaireId, String resourceName, Object object) {
        return haniotNetRepository.updateNutritionalQuestionnaire(patientId, questionnaireId, resourceName, object);
    }

    private boolean savePhysicalActivityHabit(@NonNull PhysicalActivityHabit physicalActivityHabit) {
        return physicalActivityHabitsDAO.save(Convert.convertPhysicalActivityHabit(physicalActivityHabit));
    }

    private boolean saveSleepHabits(@NonNull SleepHabit sleepHabit) {
        return sleepHabitsDAO.save(sleepHabit);
    }

    private boolean saveMedicalRecord(@NonNull MedicalRecord medicalRecord) {
        return medicalRecordDAO.save(medicalRecord);
    }

    private boolean saveFeedingHabitsRecord(@NonNull FeedingHabitsRecord feedingHabitsRecord) {
        return feedingHabitsDAO.save(Convert.convertFeedingHabitsRecord(feedingHabitsRecord));
    }

    // --------------- Health Professional ---------------------------------

    public Single<HealthProfessional> updateHealthProfissional(HealthProfessional healthProfessional) {
        return haniotNetRepository.updateHealthProfissional(healthProfessional);
    }

    public Single<HealthProfessional> getHealthProfissional(String healthProfessionalId) {
        return haniotNetRepository.getHealthProfissional(healthProfessionalId);
    }

    // ----------------- PILOT STUDY DAO -------------------------

    public Single<List<PilotStudy>> getAllPilotStudies() {
        return haniotNetRepository.getAllPilotStudies();
    }

    public Single<List<PilotStudy>> getAllUserPilotStudies(String userId) {
        return haniotNetRepository.getAllUserPilotStudies(userId);
    }

    // ---------- USER DAO ---------------

    public Completable deleteUserById(String userId) {
        return haniotNetRepository.deleteUserById(userId);
    }

    // ------------ admin and password ------------

    public Single<Admin> updateAdmin(Admin admin) {
        return haniotNetRepository.updateAdmin(admin);
    }

    public Single<Admin> getAdmin(String _id) {
        return haniotNetRepository.getAdmin(_id);
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

    // --------------- Odontological Questionnaire

    public Single<List<OdontologicalQuestionnaire>> getAllOdontologicalQuestionnaires(String patientId, int page, int limit, String sort) {
        return haniotNetRepository.getAllOdontologicalQuestionnaires(patientId, page, limit, sort);
    }

    public Single<OdontologicalQuestionnaire> saveOdontologicalQuestionnaire(String patientId, OdontologicalQuestionnaire odontologicalQuestionnaire) {
        return haniotNetRepository.saveOdontologicalQuestionnaire(patientId, odontologicalQuestionnaire);
    }

    public Single<Object> updateOdontologicalQuestionnaire(String patientId, String questionnaireId, String resourceName, Object object) {
        return haniotNetRepository.updateOdontologicalQuestionnaire(patientId, questionnaireId, resourceName, object);
    }

    // -------- UNUSED ------------------------

    /**
     * Selects user based on local id
     *
     * @param _id String
     * @return UserOB
     */
    public User getUser(@NonNull String _id) {
        return Convert.convertUser(userDAO.get(_id));
    }
//
//    /**
//     * Remove all measurements associated with user.
//     *
//     * @param userId long
//     * @return boolean
//     */
//    public boolean removeAllMeasurements(@NonNull String userId) {
//        return measurementDAO.removeAll(userId);
//    }
//
//    /**
//     * Select all measurements of a type associated with the user.
//     *
//     * @param type   {@link String}
//     * @param userId long
//     * @param offset int
//     * @param limit  int
//     * @return List<MeasurementOB>
//     */
//    public List<Measurement> listMeasurements(@NonNull String type, @NonNull String userId, @NonNull int offset, @NonNull int limit) {
//        return Convert.listMeasurementsToModel(measurementDAO.list(type, userId, offset, limit));
//    }

//    public boolean removeAllPilotStudiesy(@NonNull String userId) {
//        return pilotStudyDAO.removeAll(userId);
//    }
//    //    Search FeedingHabitsRecordOB by id
//    public SleepHabit getSleepHabitsFromPatientId(@NonNull String _id) {
//        return Convert.convertSleepHabit(sleepHabitsDAO.getFromPatientId(_id));
//    }
//
//    //    get all FeedingHabitsRecordOB on database
//    public List<SleepHabit> getSleepHabits() {
//        return Convert.listSleepHabitsToModel(sleepHabitsDAO.get());
//    }

//    //    update FeedingHabitsRecordOB
//    public boolean updateSleepHabits(@NonNull SleepHabit sleepHabit) {
//        return sleepHabitsDAO.update(Convert.convertSleepHabit(sleepHabit));
//    }
//
//    //    remove FeedingHabitsRecordOB
//    public boolean removeSleepHabits(@NonNull SleepHabit sleepHabit) {
//        return sleepHabitsDAO.remove(Convert.convertSleepHabit(sleepHabit));
//    }

//    //    Search FeedingHabitsRecordOB by id
//    public PhysicalActivityHabit getPhysicalActivityHabitFromPatientId(@NonNull String _id) {
//        return Convert.convertPhysicalActivityHabit(physicalActivityHabitsDAO.getFromPatientId(_id));
//    }
//
//    //    get all FeedingHabitsRecordOB on database
//    public List<PhysicalActivityHabit> getPhysicalActivityHabits() {
//        return Convert.convertListPhysicalActivityHabit(physicalActivityHabitsDAO.get());
//    }

//    //    update FeedingHabitsRecordOB
//    public boolean updatePhysicalActivityHabit(@NonNull PhysicalActivityHabit physicalActivityHabit) {
//        return physicalActivityHabitsDAO.update(Convert.convertPhysicalActivityHabit(physicalActivityHabit));
//    }
//
//    //    remove FeedingHabitsRecordOB
//    public boolean removePhysicalActivityHabit(@NonNull PhysicalActivityHabit physicalActivityHabit) {
//        return physicalActivityHabitsDAO.remove(Convert.convertPhysicalActivityHabit(physicalActivityHabit));
//    }
//    //    Search FeedingHabitsRecordOB by id
//    public MedicalRecord getMedicalRecordFromPatientId(@NonNull String _id) {
//        return Convert.convertMedicalRecord(medicalRecordDAO.getFromPatientId(_id));
//    }
//
//    //    get all FeedingHabitsRecordOB on database
//    public List<MedicalRecord> getMedicalRecord() {
//        return Convert.listMedicalRecordsToModel(medicalRecordDAO.get());
//    }

//    //    update FeedingHabitsRecordOB
//    public boolean updateMedicalRecord(@NonNull MedicalRecord medicalRecord) {
//        return medicalRecordDAO.update(Convert.convertMedicalRecord(medicalRecord));
//    }
//
//    //    remove FeedingHabitsRecordOB
//    public boolean removeMedicalRecord(@NonNull MedicalRecord medicalRecord) {
//        return medicalRecordDAO.remove(Convert.convertMedicalRecord(medicalRecord));
//    }
//    Search FeedingHabitsRecordOB by id
//public FeedingHabitsRecord getFeedingHabitsRecordFromPatientId(@NonNull String _id) {
//    return Convert.convertFeedingHabitsRecord(feedingHabitsDAO.getFromPatientId(_id));
//}
//
//    //    get all FeedingHabitsRecordOB on database
//    public List<FeedingHabitsRecord> getFeedingHabitsRecord() {
//        return Convert.listFeedingHabitsRecordToModel(feedingHabitsDAO.get());
//    }
//
//    //    update FeedingHabitsRecordOB
//    public boolean updateFeedingHabitsRecord(@NonNull FeedingHabitsRecord feedingHabitsRecord) {
//        return feedingHabitsDAO.update(Convert.convertFeedingHabitsRecord(feedingHabitsRecord));
//    }
//
//    //    remove FeedingHabitsRecordOB
//    public boolean removeFeedingHabitsRecord(@NonNull FeedingHabitsRecord feedingHabitsRecord) {
//        return feedingHabitsDAO.remove(Convert.convertFeedingHabitsRecord(feedingHabitsRecord));
//    }
    //    public List<PilotStudy> getPilotStudiesByUserId(String userId) {
//        return Convert.listPilotStudiesToModel(pilotStudyDAO.list(userId));
//    }
//    /**
//     * Select all measurements of a user that were not sent to the remote server.
//     *
//     * @param userId long
//     * @return List<MeasurementOB>
//     */
//    public List<Measurement> getNotSentMeasurement(@NonNull long userId) {
//        return Convert.listMeasurementsToModel(measurementDAO.getNotSent(userId));
//    }
}
