package br.edu.uepb.nutes.haniot.data.repository;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.JsonObject;

import java.util.List;

import br.edu.uepb.nutes.haniot.data.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.data.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.data.dao.NutritionalQuestionnaireDAO;
import br.edu.uepb.nutes.haniot.data.dao.OdontologicalQuestionnaireDAO;
import br.edu.uepb.nutes.haniot.data.dao.PatientDAO;
import br.edu.uepb.nutes.haniot.data.dao.del.UserDAO;
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
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
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
    String TAG = "REPOSITORY";

    private static Repository instance;

    public static Repository getInstance(Context context) {
        if (instance == null)
            instance = new Repository(context);
        return instance;
    }

    private Repository(Context context) {
        this.mContext = context;
        this.haniotNetRepository = HaniotNetRepository.getInstance(context);
        this.appPreferencesHelper = AppPreferencesHelper.getInstance(context);
        this.deviceDAO = DeviceDAO.getInstance(context);
        this.measurementDAO = MeasurementDAO.getInstance(context);
        this.patientDAO = PatientDAO.getInstance(context);
        this.userDAO = UserDAO.getInstance(context);
        this.nutritionalQuestionnaireDAO = NutritionalQuestionnaireDAO.getInstance(context);
        this.odontologicalQuestionnaireDAO = OdontologicalQuestionnaireDAO.getInstance(context);
    }

    private HaniotNetRepository haniotNetRepository;
    private AppPreferencesHelper appPreferencesHelper;
    private DeviceDAO deviceDAO;
    private MeasurementDAO measurementDAO;
    private UserDAO userDAO;
    private PatientDAO patientDAO;
    private NutritionalQuestionnaireDAO nutritionalQuestionnaireDAO;
    private OdontologicalQuestionnaireDAO odontologicalQuestionnaireDAO;
    private Context mContext;

    private void sendUnsynchronized() {
        List<Device> devices = deviceDAO.getAllNotSync();
        List<Patient> patients = patientDAO.getAllNotSync();
        List<Measurement> measurements = measurementDAO.getAllNotSync();
        List<NutritionalQuestionnaire> nutritionalQuestionnaires = nutritionalQuestionnaireDAO.getAllNotSync();
        List<OdontologicalQuestionnaire> odontologicalQuestionnaires = odontologicalQuestionnaireDAO.getAllNotSync();

        for (Device device : devices) {
            DisposableManager.add(
                    haniotNetRepository.saveDevice(device)
                            .doAfterSuccess(device1 -> deviceDAO.markAsSync(device.getId()))
                            .subscribe()
            );
        }

        for (Patient patient : patients) {
            DisposableManager.add(
                    haniotNetRepository.savePatient(patient)
                            .doAfterSuccess(patient1 -> {
                                haniotNetRepository.associatePatientToPilotStudy(patient.getPilotId(), patient1.get_id()).subscribe();
                                patientDAO.markAsSync(patient.getId());
                            })
                            .subscribe()
            );
        }

        for (Measurement measurement : measurements) {
            DisposableManager.add(
                    haniotNetRepository.saveMeasurement(measurement)
                            .doAfterSuccess(measurement1 -> measurementDAO.markAsSync(measurement.getId()))
                            .subscribe()
            );
        }

        for (NutritionalQuestionnaire nutritionalQuestionnaire : nutritionalQuestionnaires) {
            DisposableManager.add(
                    haniotNetRepository.saveNutritionalQuestionnaire(nutritionalQuestionnaire)
                            .doAfterSuccess(nutritionalQuestionnaire1 -> nutritionalQuestionnaireDAO.markAsSync(nutritionalQuestionnaire.getId()))
                            .subscribe()
            );
        }

        for (OdontologicalQuestionnaire odontologicalQuestionnaire : odontologicalQuestionnaires) {
            DisposableManager.add(
                    haniotNetRepository.saveOdontologicalQuestionnaire(odontologicalQuestionnaire)
                            .doAfterSuccess(odontologicalQuestionnaire1 -> odontologicalQuestionnaireDAO.markAsSync(odontologicalQuestionnaire.getId()))
                            .subscribe()
            );
        }
    }

    private void removeSync() {
        Log.i(TAG, "removeSync: removendo os sincronizados...");
        patientDAO.removeSyncronized();
//        deviceDAO.removeSyncronized();
        measurementDAO.removeSyncronized();
        nutritionalQuestionnaireDAO.removeSyncronized();
        odontologicalQuestionnaireDAO.removeSyncronized();
    }

    public synchronized void syncronize() {

        if (ConnectionUtils.internetIsEnabled(this.mContext)) {
            Log.i(TAG, "syncronize: COM INTERNET");
            sendUnsynchronized();
            removeSync();
            Log.i(TAG, "syncronize: Depois de excluido: " + measurementDAO.getAllMeasurements(null, "", "", "", 1, 100));

            // ------------------ Baixa os mais recentes do servidor

            User user = appPreferencesHelper.getUserLogged();

            String pilotStudyId;
            if (appPreferencesHelper.getLastPilotStudy() != null) {
                pilotStudyId = appPreferencesHelper.getLastPilotStudy().get_id();
            } else {
                pilotStudyId = user.getPilotStudyIDSelected();
            }
            String pilotId = pilotStudyId;
            Log.i(TAG, "syncronize: Agora vamos baixar os pacientes....");

            // recupera todos os pacientes do piloto estudo setado atualmente
            DisposableManager.add(
                    haniotNetRepository.getAllPatients(pilotStudyId, null, 1, 100)
                            .doAfterSuccess(patients1 -> {
                                Log.i(TAG, "syncronize: DoAfterSuccess...");
                                for (Patient p : patients1) {
                                    p.setSync(true);
                                    p.setPilotId(pilotId);
                                    patientDAO.save(p);

                                    downloadMeasurements(p.get_id());
                                    downloadNutritionalQuestionnaires(p.get_id());
                                    downloadOdontologicalQuestionnaires(p.get_id());
                                }
                                Log.i(TAG, "syncronize: pacientes baixados: " + patientDAO.getAllPatients(pilotId, null, 1, 100).toString());
                            }).subscribe((patients -> {
                        Log.i(TAG, "syncronize: subscribe executado com sucesso...");
                    }), throwable -> {
                        Log.i(TAG, "syncronize: Error ao baixar, não remover...");
                    })
            );
//            DisposableManager.clear();

            // recupera todos os dipositivos de todos os pacientes - verificar se é o paciente ou o profissional que tem
//            DisposableManager.add(
//                    haniotNetRepository.getAllDevices(user.get_id())
//                            .subscribe(devices1 -> {
//                                for (Device d : devices1) {
//                                    d.setSync(true);
//                                    d.setUserId(user.get_id());
//                                    deviceDAO.save(d);
//                                }
//                            })
//            );
        } else {
            Log.i(TAG, "syncronize: SEM INTERNET medições salvas: " + measurementDAO.getAllMeasurements(null, null, null, null, 1, 100).toString());
        }
    }

    private void downloadMeasurements(String patientId) {
        measurementDAO = MeasurementDAO.getInstance(mContext);
        DisposableManager.add(
                haniotNetRepository.getAllMeasurements(patientId, 1, 100, null)
                        .doAfterSuccess(measurements -> {

                            for (Measurement m : measurements) {
                                m.setSync(true);
                                m.setUserId(patientId);
                                measurementDAO.save(m);
                            }
                            Log.i(TAG, "syncronize: Depois de baixado: " + measurementDAO.getAllMeasurements(patientId, "", null, null, 1, 100));
                        }).subscribe()
        );
    }

    private void downloadOdontologicalQuestionnaires(String patientId) {
        DisposableManager.add(
                haniotNetRepository.getAllOdontologicalQuestionnaires(patientId, 1, 100, null)
                        .doAfterSuccess(odontologicalQuestionnaires -> {
                            for (OdontologicalQuestionnaire o : odontologicalQuestionnaires) {
                                o.setSync(true);
                                o.setPatientId(patientId);
                                odontologicalQuestionnaireDAO.save(o);
                            }
                            DisposableManager.clear();
                        }).subscribe()
        );
    }

    private void downloadNutritionalQuestionnaires(String patientId) {
        DisposableManager.add(
                haniotNetRepository.getAllNutritionalQuestionnaires(patientId, 1, 100, null)
                        .doAfterSuccess(nutritionalQuestionnaires -> {
                            for (NutritionalQuestionnaire n : nutritionalQuestionnaires) {
                                n.setSync(true);
                                n.setPatientId(patientId);
                                nutritionalQuestionnaireDAO.save(n);
                            }
                        }).subscribe()
        );
    }

    public Single<Device> saveDeviceRemote(Device device) {
        Log.i(TAG, "saveDeviceRemote: " + device.toString());
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.saveDevice(device)
                    .map(device1 -> {
                        syncronize();
                        return device1;
                    });
        } else {
            device.setSync(false);
            return this.saveDeviceLocal(device);
        }
    }

    /**
     * Adds a new device to the database.
     *
     * @param device DeviceOB
     * @return boolean
     */
    public Single<Device> saveDeviceLocal(@NonNull Device device) {
        Log.i(TAG, "saveDeviceLocal: " + device.toString());
        long id = deviceDAO.save(device);
        device.setId(id);
        return Single.just(device);
    }

    public Single<List<Device>> getAllDevices(String userId) {
        Log.i(TAG, "getAllDevices: ");
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.getAllDevices(userId)
                    .map(devices -> {
                        for (Device d : devices) {
                            d.setUserId(userId);
                        }
                        Log.i(TAG, "getAllDevices: LOCAL -> " + deviceDAO.getAllDevices(userId).toString());
                        return devices;
                    });
        } else {
            List<Device> devices = deviceDAO.getAllDevices(userId);
            Log.i(TAG, "getAllDevices: LOCAL -> " + devices.toString());
            return Single.just(devices);
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
//        syncronize(); // Download devices to local repository
        Log.i(TAG, "getDeviceByType: LOCAL -> " + deviceDAO.getByType(userId, type));
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
        Log.i(TAG, "deleteDevice: ");
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
        Log.i(TAG, "removeAllDevicesLocal: ");
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
        Log.i(TAG, "saveMeasurement: ");
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.saveMeasurement(measurement)
                    .map(measurement1 -> {
                        syncronize();
                        return measurement1;
                    });
        } else {
            measurement.setSync(false);
            long id = measurementDAO.save(measurement);
            measurement.setId(id);
            return Single.just(measurement);
        }
    }

    public Single<Object> saveMeasurement(List<Measurement> measurements) {
        Log.i(TAG, "saveMeasurements: ");
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.saveMeasurement(measurements)
                    .map(measurements1 -> {
                        syncronize();
                        return measurements1;
                    });
        } else {
            for (Measurement m : measurements) {
                m.setSync(false);
                long id = measurementDAO.save(m);
                m.setId(id);
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
        Log.i(TAG, "deleteMeasurement: ");
        return haniotNetRepository.deleteMeasurement(patientId, measurementId);
//                .doOnComplete(() -> syncronize());
    }

    public Single<List<Measurement>> getAllMeasurementsByType(String userId, String typeMeasurement,
                                                              String sort, String dateStart,
                                                              String dateEnd, int page, int limit) {
        Log.i(TAG, "getAllMeasurementsByType: ");
        if (ConnectionUtils.internetIsEnabled(mContext)) {

            List<Measurement> aux = measurementDAO.getMeasurementsByType(userId, typeMeasurement, sort, dateStart, dateEnd, page, limit);
            Log.i(TAG, "getAllMeasurementsByType: COM INTERNET - OFF -> " + aux.toString());

            return haniotNetRepository.getAllMeasurementsByType(
                    userId, typeMeasurement, sort, dateStart, dateEnd, page, limit);
        } else {
            List<Measurement> aux = measurementDAO.getMeasurementsByType(userId, typeMeasurement, sort, dateStart, dateEnd, page, limit);
            Log.i(TAG, "getAllMeasurementsByType: SEM INTERNET - OFF -> " + aux.toString());
            return Single.just(aux);
        }
    }

    // --------- PATIENT DAO --------------

    public Single<Patient> getPatient(@NonNull String _id) {
        Log.i(TAG, "getPatient: ");
        if (ConnectionUtils.internetIsEnabled(mContext)) {
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
        Log.i(TAG, "savePatient: " + patient.toString());
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            Log.i(TAG, "savePatient: " + patient.toString());
            return haniotNetRepository.savePatient(patient)
                    .map(patient1 -> {
                        syncronize();
                        return patient1;
                    });
        } else {
            patient.setSync(false);
            long id = patientDAO.save(patient);
            patient.setId(id);
            Log.i(TAG, "Salvo localmente");
            return Single.just(patient);
        }
    }

    public Single<Patient> updatePatient(@NonNull Patient patient) {
        Log.i(TAG, "updatePatient: ");
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
        Log.i(TAG, "getAllPatients: ");
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.getAllPatients(pilotStudyId, sort, page, limit)
                    .map(patients -> {
                        for (Patient p : patients)
                            p.setPilotId(pilotStudyId);
                        Log.i(TAG, "getAllPatients: " + patients.toString());
                        patients.addAll(patientDAO.getAllNotSync());
                        return patients;
                    });
        } else {
            List<Patient> aux = patientDAO.getAllPatients(pilotStudyId, sort, page, limit);
            Log.i(TAG, "getAllPatients(LOCAL): " + aux.toString());
            return Single.just(aux);
        }
    }

    public Completable deletePatient(String patientId) {
        Log.i(TAG, "deletePatient: ");
        return haniotNetRepository.deletePatient(patientId)
                .doOnComplete(() -> syncronize());
    }

    public Single<Response<Void>> associatePatientToPilotStudy(String pilotStudyId, Patient patient) {
        Log.i(TAG, "associatePatientToPilotStudy: ");
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.associatePatientToPilotStudy(pilotStudyId, patient.get_id())
                    .doAfterSuccess(voidResponse -> syncronize());
        } else {
            patient.setPilotId(pilotStudyId);
            patient.setSync(false);
            if (patientDAO.save(patient) > 0) {
                return Single.just(Response.success(null));
            } else {
                return Single.just(Response.error(404, null));
            }
        }
    }

    // ------------ NUTRITIONAL DAO -----------

    public Single<List<NutritionalQuestionnaire>> getAllNutritionalQuestionnaires(String patientId, int page, int limit, String sort) {
        Log.i(TAG, "getAllNutritionalQuestionnaires: ");
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.getAllNutritionalQuestionnaires(patientId, page, limit, sort)
                    .map(nutritionalQuestionnaires -> {
                        for (NutritionalQuestionnaire n : nutritionalQuestionnaires) {
                            n.setPatientId(patientId);
                        }
                        return nutritionalQuestionnaires;
                    });
        } else {
            List<NutritionalQuestionnaire> aux = nutritionalQuestionnaireDAO.getAll(patientId, page, limit, sort);
            return Single.just(aux);
        }
    }

    /**
     * Utilizado para avaliação nutricional
     *
     * @param patientId String
     * @return NutritionalQuestionnaire
     */
    public Single<NutritionalQuestionnaire> getLastNutritionalQuestionnaire(String patientId) {
        Log.i(TAG, "getLastNutritionalQuestionnaire: ");
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
        Log.i(TAG, "getLastMeasurements: ");
        return haniotNetRepository.getLastMeasurements(patientId);
    }

    public Single<NutritionalEvaluationResult> saveNutritionalEvaluation(NutritionalEvaluation nutritionalEvaluation) {
        Log.i(TAG, "saveNutritionalEvaluation: ");
        return haniotNetRepository.saveNutritionalEvaluation(nutritionalEvaluation);
    }

    public Single<NutritionalQuestionnaire> saveNutritionalQuestionnaire(NutritionalQuestionnaire nutritionalQuestionnaire) {
        Log.i(TAG, "saveNutritionalQuestionnaire: ");
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.saveNutritionalQuestionnaire(nutritionalQuestionnaire)
                    .map(nutritionalQuestionnaire1 -> {
                        syncronize();
                        return nutritionalQuestionnaire1;
                    });
        } else {
            nutritionalQuestionnaire.setSync(false);
            long id = nutritionalQuestionnaireDAO.save(nutritionalQuestionnaire);
            nutritionalQuestionnaire.setId(id);
            return Single.just(nutritionalQuestionnaire);
        }
//        this.savePhysicalActivityHabit(nutritionalQuestionnaire.getPhysicalActivityHabit());
//        this.saveSleepHabits(nutritionalQuestionnaire.getSleepHabit());
//        this.saveMedicalRecord(nutritionalQuestionnaire.getMedicalRecord());
//        this.saveFeedingHabitsRecord(nutritionalQuestionnaire.getFeedingHabitsRecord());
    }

    public Single<Object> updateNutritionalQuestionnaire(String patientId, String questionnaireId, String resourceName, Object object) {
        Log.i(TAG, "updateNutritionalQuestionnaire: ");
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
        Log.i(TAG, "updateHealthProfissional: ");
        return haniotNetRepository.updateHealthProfissional(healthProfessional);
    }

    public Single<HealthProfessional> getHealthProfissional(String healthProfessionalId) {
        Log.i(TAG, "getHealthProfissional: ");
        return haniotNetRepository.getHealthProfissional(healthProfessionalId);
    }

    // ----------------- PILOT STUDY DAO -------------------------

    public Single<List<PilotStudy>> getAllPilotStudies() {
        Log.i(TAG, "getAllPilotStudies: ");
        return haniotNetRepository.getAllPilotStudies();
    }

    public Single<List<PilotStudy>> getAllUserPilotStudies(String userId) {
        Log.i(TAG, "getAllUserPilotStudies: ");
        return haniotNetRepository.getAllUserPilotStudies(userId);
    }

    // ---------- USER DAO ---------------

    public Completable deleteUserById(String userId) {
        Log.i(TAG, "deleteUserById: ");
        return haniotNetRepository.deleteUserById(userId);
    }

    // ------------ admin and password ------------

    public Single<Admin> updateAdmin(Admin admin) {
        Log.i(TAG, "updateAdmin: ");
        return haniotNetRepository.updateAdmin(admin);
    }

    public Single<Admin> getAdmin(String _id) {
        Log.i(TAG, "getAdmin: ");
        return haniotNetRepository.getAdmin(_id);
    }

    public Single<UserAccess> auth(String username, String password) {
        Log.i(TAG, "auth: ");
        return haniotNetRepository.auth(username, password);
    }

    public Completable changePassword(User user) {
        Log.i(TAG, "changePassword: ");
        return haniotNetRepository.changePassword(user);
    }

    public Single<Object> forgotPassword(JsonObject email) {
        Log.i(TAG, "forgotPassword: ");
        return haniotNetRepository.forgotPassword(email);
    }

    // --------------- Odontological Questionnaire -------------------------------

    public Single<List<OdontologicalQuestionnaire>> getAllOdontologicalQuestionnaires(String patientId, int page, int limit, String sort) {
        Log.i(TAG, "getAllOdontologicalQuestionnaires: ");
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.getAllOdontologicalQuestionnaires(patientId, page, limit, sort)
                    .map(odontologicalQuestionnaires -> {
                        for (OdontologicalQuestionnaire o : odontologicalQuestionnaires) {
                            o.setPatientId(patientId);
                        }
                        return odontologicalQuestionnaires;
                    });
        } else {
            List<OdontologicalQuestionnaire> aux = odontologicalQuestionnaireDAO.getAll(patientId, page, limit, sort);
            return Single.just(aux);
        }
    }

    public Single<OdontologicalQuestionnaire> saveOdontologicalQuestionnaire(OdontologicalQuestionnaire odontologicalQuestionnaire) {
        Log.i(TAG, "saveOdontologicalQuestionnaire: ");
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.saveOdontologicalQuestionnaire(odontologicalQuestionnaire)
                    .map(odontologicalQuestionnaire1 -> {
                        syncronize();
                        return odontologicalQuestionnaire;
                    });
        } else {
            odontologicalQuestionnaire.setSync(false);
            long id = odontologicalQuestionnaireDAO.save(odontologicalQuestionnaire);
            odontologicalQuestionnaire.setId(id);
            return Single.just(odontologicalQuestionnaire);
        }
    }

    public Single<Object> updateOdontologicalQuestionnaire(String patientId, String questionnaireId, String resourceName, Object object) {
        Log.i(TAG, "updateOdontologicalQuestionnaire: ");
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