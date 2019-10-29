package br.edu.uepb.nutes.haniot.data.repository;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.JsonObject;

import java.util.List;

import br.edu.uepb.nutes.haniot.data.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.data.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.data.dao.OdontologicalQuestionnaireDAO;
import br.edu.uepb.nutes.haniot.data.dao.PatientDAO;
import br.edu.uepb.nutes.haniot.data.dao.del.PilotStudyDAO;
import br.edu.uepb.nutes.haniot.data.dao.del.UserDAO;
import br.edu.uepb.nutes.haniot.data.dao.del.FeedingHabitsDAO;
import br.edu.uepb.nutes.haniot.data.dao.del.MedicalRecordDAO;
import br.edu.uepb.nutes.haniot.data.dao.NutritionalQuestionnaireDAO;
import br.edu.uepb.nutes.haniot.data.dao.del.PhysicalActivityHabitsDAO;
import br.edu.uepb.nutes.haniot.data.dao.del.SleepHabitsDAO;
import br.edu.uepb.nutes.haniot.data.model.Admin;
import br.edu.uepb.nutes.haniot.data.model.Device;
import br.edu.uepb.nutes.haniot.data.model.HealthProfessional;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.MeasurementLastResponse;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.PilotStudy;
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.model.UserAccess;
import br.edu.uepb.nutes.haniot.data.model.nutritional.NutritionalEvaluation;
import br.edu.uepb.nutes.haniot.data.model.nutritional.NutritionalEvaluationResult;
import br.edu.uepb.nutes.haniot.data.model.nutritional.NutritionalQuestionnaire;
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
        this.mContext = context;

        this.haniotNetRepository = HaniotNetRepository.getInstance(context);

        this.deviceDAO = DeviceDAO.getInstance(context);
        this.measurementDAO = MeasurementDAO.getInstance(context);

        this.patientDAO = PatientDAO.getInstance(context);
        this.userDAO = UserDAO.getInstance(context);

        this.pilotStudyDAO = PilotStudyDAO.getInstance(context);

        this.physicalActivityHabitsDAO = PhysicalActivityHabitsDAO.getInstance(context);
        this.sleepHabitsDAO = SleepHabitsDAO.getInstance(context);
        this.feedingHabitsDAO = FeedingHabitsDAO.getInstance(context);
        this.medicalRecordDAO = MedicalRecordDAO.getInstance(context);

        this.nutritionalQuestionnaireDAO = NutritionalQuestionnaireDAO.getInstance(context);
        this.odontologicalQuestionnaireDAO = OdontologicalQuestionnaireDAO.getInstance(context);
    }

    private HaniotNetRepository haniotNetRepository;

    private DeviceDAO deviceDAO;

    private MeasurementDAO measurementDAO;

    private UserDAO userDAO;
    private PatientDAO patientDAO;

    private PilotStudyDAO pilotStudyDAO;

    private SleepHabitsDAO sleepHabitsDAO;
    private FeedingHabitsDAO feedingHabitsDAO;
    private PhysicalActivityHabitsDAO physicalActivityHabitsDAO;
    private MedicalRecordDAO medicalRecordDAO;

    private NutritionalQuestionnaireDAO nutritionalQuestionnaireDAO;
    private OdontologicalQuestionnaireDAO odontologicalQuestionnaireDAO;

    private Context mContext;

    public void syncronize() {
        // dispositivos (do user)
        // pacientes (se der todos)
        // medicoes
        // questionarios

        List<Device> devices = deviceDAO.getAllNotSync();
        List<Patient> patients = patientDAO.getAllNotSync();
        List<Measurement> measurements = measurementDAO.getAllNotSync();
        List<NutritionalQuestionnaire> nutritionalQuestionnaires = nutritionalQuestionnaireDAO.getAllNotSync();
        List<OdontologicalQuestionnaire> odontologicalQuestionnaires = odontologicalQuestionnaireDAO.getAllNotSync();

        if (ConnectionUtils.internetIsEnabled(this.mContext)) {


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
                        syncronize();
                        return device1;
                    });
        } else {
            device.setSync(false);
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
                        syncronize();
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
                .doOnComplete(() -> syncronize());
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
                        syncronize();
                        return measurement1;
                    });
        } else {
            measurement.setSync(false);
            measurementDAO.save(measurement);
            return Single.just(measurement);

        }
    }

    public Single<Object> saveMeasurement(List<Measurement> measurements) {
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.saveMeasurement(measurements)
                    .map(measurements1 -> {
                        syncronize();
                        return measurements1;
                    });
        } else {
            for (Measurement m : measurements) {
                m.setSync(false);
                measurementDAO.save(m);
            }
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
                .doOnComplete(() -> syncronize());
    }

    public Single<List<Measurement>> getAllMeasurementsByType(String userId, String typeMeasurement,
                                                              String sort, String dateStart,
                                                              String dateEnd, int page, int limit) {
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            syncronize();
            return haniotNetRepository.getAllMeasurementsByType(
                    userId, typeMeasurement, sort, dateStart, dateEnd, page, limit);
        } else {
            List<Measurement> aux = measurementDAO.getMeasurementsByType(userId, typeMeasurement, sort, dateStart, dateEnd, page, limit);
            return Single.just(aux);
        }
    }

    // --------- PATIENT DAO --------------

    public Single<Patient> getPatient(@NonNull String _id) {
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            syncronize();
            return haniotNetRepository.getPatient(_id);
        } else {
            Patient p = patientDAO.get(_id);

            if (p != null) {
                return Single.just(p);
            } else {
                return haniotNetRepository.getPatient(_id);
            }
        }
    }

    public Single<Patient> savePatient(@NonNull Patient patient) {
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.savePatient(patient)
                    .map(patient1 -> {
                        syncronize();
                        return patient1;
                    });
        } else {
            patient.setSync(false);
            patientDAO.save(patient);
            return Single.just(patient);
        }
    }

    public Single<Patient> updatePatient(@NonNull Patient patient) {
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.updatePatient(patient)
                    .map(patient1 -> {
                        syncronize();
                        return patient1;
                    });
        } else {
            patient.setSync(false);
            patientDAO.update(patient);
            return Single.just(patient);
        }
    }

    public Single<List<Patient>> getAllPatients(String pilotStudyId, String sort, int page, int limit) {
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            syncronize();
            return haniotNetRepository.getAllPatients(pilotStudyId, sort, page, limit);
        } else {
            List<Patient> aux = patientDAO.getAllPatients(pilotStudyId, sort, page, limit);
            return Single.just(aux);
        }

    }

    public Completable deletePatient(String patientId) {
        return haniotNetRepository.deletePatient(patientId)
                .doOnComplete(() -> syncronize());
    }

    public Single<Response<Void>> associatePatientToPilotStudy(String pilotStudyId, String patientId) {
        return haniotNetRepository.associatePatientToPilotStudy(pilotStudyId, patientId);
    }

    // ------------ NUTRITIONAL DAO -----------

    public Single<List<NutritionalQuestionnaire>> getAllNutritionalQuestionnaires(String patientId, int page, int limit, String sort) {
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.getAllNutritionalQuestionnaires(patientId, page, limit, sort)
                    .map(nutritionalQuestionnaires -> {
                        for (NutritionalQuestionnaire n : nutritionalQuestionnaires) {
                            n.setPatientId(patientId);
                        }
                        syncronize();
                        return nutritionalQuestionnaires;
                    });
        } else {
            List<NutritionalQuestionnaire> aux = nutritionalQuestionnaireDAO.getAll(patientId, page, limit, sort);
            return Single.just(aux);
        }
    }

    /**
     * Utilizado para avaliação nutricional
     * @param patientId String
     * @return NutritionalQuestionnaire
     */
    public Single<NutritionalQuestionnaire> getLastNutritionalQuestionnaire(String patientId) {
        return haniotNetRepository.getLastNutritionalQuestionnaire(patientId);
    }

    /**
     * Select Last measurements of a patient
     * Necessary connection
     *
     * @param patientId String
     * @return MeasurementLasResponse
     */
    public Single<MeasurementLastResponse> getLastMeasurements(String patientId) {
        return haniotNetRepository.getLastMeasurements(patientId);
    }

    public Single<NutritionalEvaluationResult> saveNutritionalEvaluation(NutritionalEvaluation nutritionalEvaluation) {
        return haniotNetRepository.saveNutritionalEvaluation(nutritionalEvaluation);
    }

    public Single<NutritionalQuestionnaire> saveNutritionalQuestionnaire(NutritionalQuestionnaire nutritionalQuestionnaire) {
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.saveNutritionalQuestionnaire(nutritionalQuestionnaire.getPatientId(), nutritionalQuestionnaire)
                    .map(nutritionalQuestionnaire1 -> {
                        syncronize();
                        return nutritionalQuestionnaire1;
                    });
        } else {
            nutritionalQuestionnaire.setSync(false);
            nutritionalQuestionnaireDAO.save(nutritionalQuestionnaire);
            return Single.just(nutritionalQuestionnaire);
        }
//        this.savePhysicalActivityHabit(nutritionalQuestionnaire.getPhysicalActivityHabit());
//        this.saveSleepHabits(nutritionalQuestionnaire.getSleepHabit());
//        this.saveMedicalRecord(nutritionalQuestionnaire.getMedicalRecord());
//        this.saveFeedingHabitsRecord(nutritionalQuestionnaire.getFeedingHabitsRecord());
    }

    public Single<Object> updateNutritionalQuestionnaire(String patientId, String questionnaireId, String resourceName, Object object) {
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.updateNutritionalQuestionnaire(patientId, questionnaireId, resourceName, object)
                    .map(o -> {
                        syncronize();
                        return o;
                    });
        } else {
            nutritionalQuestionnaireDAO.update(patientId, questionnaireId, resourceName, object);
            return Single.just(null); // VERIFICAR
        }
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

    // --------------- Odontological Questionnaire -------------------------------

    public Single<List<OdontologicalQuestionnaire>> getAllOdontologicalQuestionnaires(String patientId, int page, int limit, String sort) {
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.getAllOdontologicalQuestionnaires(patientId, page, limit, sort)
                    .map(odontologicalQuestionnaires -> {
                        syncronize();
                        return odontologicalQuestionnaires;
                    });
        } else {
            List<OdontologicalQuestionnaire> aux = odontologicalQuestionnaireDAO.getAll(patientId, page, limit, sort);
            return Single.just(aux);
        }

    }

    public Single<OdontologicalQuestionnaire> saveOdontologicalQuestionnaire(OdontologicalQuestionnaire odontologicalQuestionnaire) {
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.saveOdontologicalQuestionnaire(odontologicalQuestionnaire.getPatientId(), odontologicalQuestionnaire)
                    .map(odontologicalQuestionnaire1 -> {
                        syncronize();
                        return odontologicalQuestionnaire;
                    });
        } else {
            odontologicalQuestionnaire.setSync(false);
            odontologicalQuestionnaireDAO.save(odontologicalQuestionnaire);
            return Single.just(odontologicalQuestionnaire);
        }
    }

    public Single<Object> updateOdontologicalQuestionnaire(String patientId, String questionnaireId, String resourceName, Object object) {
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.updateOdontologicalQuestionnaire(patientId, questionnaireId, resourceName, object)
                    .map(o -> {
                        syncronize();
                        return o;
                    });
        } else {
            odontologicalQuestionnaireDAO.update(patientId, questionnaireId, resourceName, object);
            return (null); // VERIFICAR
        }
    }

    // -------- UNUSED ------------------------

    /**
     * Selects user based on local id
     *
     * @param _id String
     * @return UserOB
     */
    public User getUser(@NonNull String _id) {
        return new User(userDAO.get(_id));
    }

}
