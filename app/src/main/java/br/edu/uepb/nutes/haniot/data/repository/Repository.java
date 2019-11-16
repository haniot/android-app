package br.edu.uepb.nutes.haniot.data.repository;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import br.edu.uepb.nutes.haniot.data.dao.DeviceDAO;
import br.edu.uepb.nutes.haniot.data.dao.MeasurementDAO;
import br.edu.uepb.nutes.haniot.data.dao.NutritionalQuestionnaireDAO;
import br.edu.uepb.nutes.haniot.data.dao.OdontologicalQuestionnaireDAO;
import br.edu.uepb.nutes.haniot.data.dao.PatientDAO;
import br.edu.uepb.nutes.haniot.data.model.Device;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.model.nutritional.NutritionalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.model.odontological.OdontologicalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.DisposableManager;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.Response;

/**
 * Classe responsável por gerenciar o repositório local e o repositório remoto
 */
public class Repository extends RepositoryOn {

    private static Repository instance;

    public static Repository getInstance(Context context) {
        if (instance == null)
            instance = new Repository(context);
        return instance;
    }

    private Repository(Context context) {
        super(context);
        this.appPreferencesHelper = AppPreferencesHelper.getInstance(context);
        this.deviceDAO = DeviceDAO.getInstance(context);
        this.measurementDAO = MeasurementDAO.getInstance(context);
        this.patientDAO = PatientDAO.getInstance(context);
        this.nutritionalQuestionnaireDAO = NutritionalQuestionnaireDAO.getInstance(context);
        this.odontologicalQuestionnaireDAO = OdontologicalQuestionnaireDAO.getInstance(context);
    }

    private AppPreferencesHelper appPreferencesHelper;
    private DeviceDAO deviceDAO;
    private MeasurementDAO measurementDAO;
    private PatientDAO patientDAO;
    private NutritionalQuestionnaireDAO nutritionalQuestionnaireDAO;
    private OdontologicalQuestionnaireDAO odontologicalQuestionnaireDAO;

    private boolean sendUnsynchronized() {
        List<Device> devices = deviceDAO.getAllNotSync();
        List<Patient> patients = patientDAO.getAllNotSync();

        if (devices.isEmpty() && patients.isEmpty() && measurementDAO.getAllNotSync().isEmpty() &&
                nutritionalQuestionnaireDAO.getAllNotSync().isEmpty() && odontologicalQuestionnaireDAO.getAllNotSync().isEmpty()) {
            Log.i(TAG, "sendUnsynchronized: NADA PARA ENVIAR");
            return false;
        }

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
                            .doAfterSuccess(patientServer -> { // patiente com _id, posso enviar as measurements, questionnaires
                                haniotNetRepository.associatePatientToPilotStudy(patient.getPilotId(), patientServer.get_id()).subscribe();
                                if (patient.getId() == appPreferencesHelper.getLastPatient().getId())
                                    appPreferencesHelper.saveLastPatient(patientServer);
                                patientDAO.markAsSync(patient.getId());

                                //pegar a lista de measurements com o id(long) dele
                                List<Measurement> mAux = measurementDAO.getAllNotSync(patient.getId());
                                List<NutritionalQuestionnaire> nAux = nutritionalQuestionnaireDAO.getAllNotSync(patient.getId());
                                List<OdontologicalQuestionnaire> oAux = odontologicalQuestionnaireDAO.getAllNotSync(patient.getId());

                                for (Measurement m : mAux) {
                                    m.setUser_id(patientServer.get_id()); // seta com o _id recebido agora
                                    haniotNetRepository.saveMeasurement(m)
                                            .doAfterSuccess(measurement -> measurementDAO.markAsSync(m.getId()))
                                            .subscribe();
                                }

                                for (NutritionalQuestionnaire n : nAux) {
                                    n.setPatient_id(patientServer.get_id());
                                    haniotNetRepository.saveNutritionalQuestionnaire(n)
                                            .doAfterSuccess(nutritionalQuestionnaire -> nutritionalQuestionnaireDAO.markAsSync(n.getId()))
                                            .subscribe();
                                }

                                for (OdontologicalQuestionnaire o : oAux) {
                                    o.setPatient_id(patientServer.get_id());
                                    haniotNetRepository.saveOdontologicalQuestionnaire(o)
                                            .doAfterSuccess(odontologicalQuestionnaire -> odontologicalQuestionnaireDAO.markAsSync(o.getId()))
                                            .subscribe();
                                }
                                // enviar para o servidor
                            })
                            .subscribe()
            );
        }

        List<Measurement> measurements = measurementDAO.getAllNotSync();
        List<NutritionalQuestionnaire> nutritionalQuestionnaires = nutritionalQuestionnaireDAO.getAllNotSync();
        List<OdontologicalQuestionnaire> odontologicalQuestionnaires = odontologicalQuestionnaireDAO.getAllNotSync();

        for (Measurement m : measurements) {
            if (m.getUser_id() != null)
                DisposableManager.add(
                        haniotNetRepository.saveMeasurement(m)
                                .doAfterSuccess(measurement1 -> measurementDAO.markAsSync(m.getId()))
                                .subscribe()
                );
        }

        for (NutritionalQuestionnaire n : nutritionalQuestionnaires) {
            if (n.getPatient_id() != null)
                DisposableManager.add(
                        haniotNetRepository.saveNutritionalQuestionnaire(n)
                                .doAfterSuccess(nutritionalQuestionnaire1 -> nutritionalQuestionnaireDAO.markAsSync(n.getId()))
                                .subscribe()
                );
        }

        for (OdontologicalQuestionnaire o : odontologicalQuestionnaires) {
            if (o.getPatient_id() != null)
                DisposableManager.add(
                        haniotNetRepository.saveOdontologicalQuestionnaire(o)
                                .doAfterSuccess(odontologicalQuestionnaire1 -> odontologicalQuestionnaireDAO.markAsSync(o.getId()))
                                .subscribe()
                );
        }
        Log.i(TAG, "sendUnsynchronized: DADOS OFF ENVIADOS");
        return true;
    }

    public synchronized void syncronize() {

        if (ConnectionUtils.internetIsEnabled(this.mContext)) {
            Log.i(TAG, "syncronize: COM INTERNET");
            sendUnsynchronized();
            // ------------------ Baixa os mais recentes do servidor

            String pilotStudyId;
            if (appPreferencesHelper.getLastPilotStudy() != null) {
                pilotStudyId = appPreferencesHelper.getLastPilotStudy().get_id();
            } else {
                pilotStudyId = appPreferencesHelper.getUserLogged().getPilotStudyIDSelected();
            }
            String pilotId = pilotStudyId;
            Log.i(TAG, "syncronize: Agora vamos baixar os pacientes....");

            DisposableManager.add(haniotNetRepository
                    .getAllPatients(pilotStudyId, "created_at", 1, 100)
                    .doAfterSuccess(patients1 -> {
                        patientDAO.removeSyncronized();

                        for (Patient p : patients1) {
                            p.setSync(true);
                            p.setPilotId(pilotId);

                            long patientId = patientDAO.save(p);

                            if (patientId > 0) {
                                downloadMeasurements(p.get_id(), patientId);
                                downloadNutritionalQuestionnaires(p.get_id(), patientId);
                                downloadOdontologicalQuestionnaires(p.get_id(), patientId);
                            }
                        }
                        Log.i(TAG, "syncronize: pacientes baixados: " + patientDAO.getAllPatients(pilotId, null, 1, 100).toString());
                    }).subscribe((patients -> Log.i(TAG, "syncronize: subscribe executado com sucesso...")),
                            throwable -> Log.i(TAG, "syncronize: Error ao baixar, não remover..."))
            );

            // recupera todos os dipositivos de todos os pacientes - verificar se é o paciente ou o profissional que tem
//            DisposableManager.add(
//                    haniotNetRepository.getAllDevices(user.get_id())
//                            .subscribe(devices1 -> {
//                                for (Device d : devices1) {
//                                    d.setSync(true);
//                                    d.setUser_id(user.get_id());
//                                    deviceDAO.save(d);
//                                }
//                            })
//            );
        } else {
            Log.i(TAG, "syncronize: SEM INTERNET medições salvas: " + measurementDAO.getAllMeasurements(null, null, null, null, 1, 100).toString());
        }
    }

    private void downloadMeasurements(String patient_id, long patientId) {
        measurementDAO = MeasurementDAO.getInstance(mContext);
        DisposableManager.add(
                haniotNetRepository.getAllMeasurements(patient_id, 1, 100, "-timestamp")
                        .doAfterSuccess(measurements -> {
                            measurementDAO.removeSyncronized();

                            for (Measurement m : measurements) {
                                m.setSync(true);
                                m.setUser_id(patient_id);
                                m.setUserId(patientId);
                                measurementDAO.save(m);
                            }
                            Log.i(TAG, "syncronize: Baixados (" + patientDAO.get(patient_id).getName() + "):" + measurementDAO.getAllMeasurements(patient_id, null, null, null, 1, 100));
                        }).subscribe()
        );
    }

    private void downloadOdontologicalQuestionnaires(String patient_id, long patientId) {
        DisposableManager.add(
                haniotNetRepository.getAllOdontologicalQuestionnaires(patient_id, 1, 100, "created_at")
                        .doAfterSuccess(odontologicalQuestionnaires -> {
                            odontologicalQuestionnaireDAO.removeSyncronized();

                            for (OdontologicalQuestionnaire o : odontologicalQuestionnaires) {
                                o.setSync(true);
                                o.setPatient_id(patient_id);
                                o.setPatientId(patientId);
                                odontologicalQuestionnaireDAO.save(o);
                            }
//                            DisposableManager.clear();
                        }).subscribe()
        );
    }

    private void downloadNutritionalQuestionnaires(String patient_id, long patientId) {
        DisposableManager.add(
                haniotNetRepository.getAllNutritionalQuestionnaires(patient_id, 1, 100, "created_at")
                        .doAfterSuccess(nutritionalQuestionnaires -> {
                            nutritionalQuestionnaireDAO.removeSyncronized();

                            for (NutritionalQuestionnaire n : nutritionalQuestionnaires) {
                                n.setSync(true);
                                n.setPatient_id(patient_id);
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
        measurement.setSync(false);
        long id = measurementDAO.save(measurement);
        measurement.setId(id);

        if (ConnectionUtils.internetIsEnabled(mContext)) {
            syncronize();
        }
        return Single.just(measurement);
    }

    public Single<Object> saveMeasurement(List<Measurement> measurements) {
        Log.i(TAG, "saveMeasurements: ");
        for (Measurement m : measurements) {
            m.setSync(false);
            long id = measurementDAO.save(m);
            m.setId(id);
        }
        if (ConnectionUtils.internetIsEnabled(mContext))
            syncronize();

        return Single.just(measurements);
    }

    /**
     * Remove measurement.
     *
     * @param measurement
     * @return boolean
     */
    public Completable deleteMeasurement(@NonNull Measurement measurement) {
        Log.i(TAG, "deleteMeasurement: ");
        if (measurement.get_id() == null && !measurement.isSync()) { // não foi sincronizada ainda, removo localmente
            measurementDAO.remove(measurement.getId());
            return Completable.complete();
        } else {
            return haniotNetRepository.deleteMeasurement(measurement.getUser_id(), measurement.get_id())
                    .doOnComplete(this::syncronize);
        }
    }

    public Single<List<Measurement>> getAllMeasurementsByType(Patient patient, String typeMeasurement,
                                                              String sort, String dateStart,
                                                              String dateEnd, int page, int limit) {

        List<Measurement> aux = measurementDAO.getMeasurementsByType(patient, typeMeasurement, sort, dateStart, dateEnd, page, limit);
        if (ConnectionUtils.internetIsEnabled(mContext)) {

            Log.i(TAG, "getAllMeasurementsByType: COM INTERNET - page[" + page + "] -> " + aux.toString());

            return haniotNetRepository.getAllMeasurementsByType(
                    patient.get_id(), typeMeasurement, sort, dateStart, dateEnd, page, limit)
                    .map(measurements -> {
                        if (page == 1) syncronize();
                        return measurements;
                    });
        } else {
            Log.i(TAG, "getAllMeasurementsByType: SEM INTERNET - page[" + page + "] -> " + aux.toString());
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

        if (patient.get_id() == null && !patient.isSync()) {
            patient.setSync(false);
            patientDAO.update(patient);
            return Single.just(patient);
        } else {
            return haniotNetRepository.updatePatient(patient)
                    .map(patient1 -> {
                        syncronize();
                        return patient1;
                    });
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

    public Completable deletePatient(@NonNull Patient patient) {
        Log.i(TAG, "deletePatient: ");
        if (patient.get_id() == null) {
            patientDAO.remove(patient.getId());
            return Completable.complete();
        } else {
            return haniotNetRepository.deletePatient(patient.get_id())
                    .doOnComplete(() -> syncronize());
        }
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

    public Single<List<NutritionalQuestionnaire>> getAllNutritionalQuestionnaires(Patient patient, int page, int limit, String sort) {
        Log.i(TAG, "getAllNutritionalQuestionnaires: ");
        List<NutritionalQuestionnaire> aux = nutritionalQuestionnaireDAO.getAll(patient, page, limit, sort);

        if (ConnectionUtils.internetIsEnabled(mContext) && patient.get_id() != null) {
            syncronize();
            Log.i(TAG, "getAllNutritionalQuestionnaires: ON: " + aux.toString());

            return haniotNetRepository.getAllNutritionalQuestionnaires(patient.get_id(), page, limit, sort)
                    .map(nutritionalQuestionnaires -> {
                        for (NutritionalQuestionnaire n : nutritionalQuestionnaires) {
                            n.setPatient_id(patient.get_id());
                        }
                        return nutritionalQuestionnaires;
                    });
        } else {
            Log.i(TAG, "getAllNutritionalQuestionnaires: OFF: " + aux.toString());
            return Single.just(aux);
        }
    }

    public Single<NutritionalQuestionnaire> saveNutritionalQuestionnaire(NutritionalQuestionnaire n) {
        Log.i(TAG, "saveNutritionalQuestionnaire: ");

        n.setSync(false);
        long id = nutritionalQuestionnaireDAO.save(n);
        n.setId(id);

        if (ConnectionUtils.internetIsEnabled(mContext)) {
            syncronize();
        }
        return Single.just(n);
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

    // --------------- Odontological Questionnaire -------------------------------

    public Single<List<OdontologicalQuestionnaire>> getAllOdontologicalQuestionnaires(String patientId, int page, int limit, String sort) {
        Log.i(TAG, "getAllOdontologicalQuestionnaires: ");

        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.getAllOdontologicalQuestionnaires(patientId, page, limit, sort)
                    .map(odontologicalQuestionnaires -> {
                        syncronize();
                        for (OdontologicalQuestionnaire o : odontologicalQuestionnaires) {
                            o.setPatient_id(patientId);
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

        odontologicalQuestionnaire.setSync(false);
        long id = odontologicalQuestionnaireDAO.save(odontologicalQuestionnaire);
        odontologicalQuestionnaire.setId(id);

        if (ConnectionUtils.internetIsEnabled(mContext)) {
            syncronize();
        }
        return Single.just(odontologicalQuestionnaire);
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
        return null;
    }
}