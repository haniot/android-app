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
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
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

        this.mCompositeDisposable = new CompositeDisposable();
    }

    private AppPreferencesHelper appPreferencesHelper;
    private DeviceDAO deviceDAO;
    private MeasurementDAO measurementDAO;
    private PatientDAO patientDAO;
    private NutritionalQuestionnaireDAO nutritionalQuestionnaireDAO;
    private OdontologicalQuestionnaireDAO odontologicalQuestionnaireDAO;

    private CompositeDisposable mCompositeDisposable;

    private void sendUnsynchronized() {
        List<Device> devices = deviceDAO.getAllNotSync();
        List<Patient> patients = patientDAO.getAllNotSync();
        List<Measurement> measurements = measurementDAO.getAllNotSync();
        List<NutritionalQuestionnaire> nutritionalQuestionnaires = nutritionalQuestionnaireDAO.getAllNotSync();
        List<OdontologicalQuestionnaire> odontologicalQuestionnaires = odontologicalQuestionnaireDAO.getAllNotSync();

        if (devices.isEmpty() && patients.isEmpty() && measurements.isEmpty() && nutritionalQuestionnaires.isEmpty() && odontologicalQuestionnaires.isEmpty()) {
            Log.i(TAG, "sendUnsynchronized: NADA PARA ENVIAR");
            return;
        }

        for (Device device : devices)
            mCompositeDisposable.add(
                    haniotNetRepository.saveDevice(device)
                            .doAfterSuccess(device1 -> deviceDAO.markAsSync(device.getId()))
                            .subscribe()
            );
        // primeiro envia os pacientes não sincronizados, juntamente com os seus dados (medições e questionários) / ainda não tem o _id
        for (Patient patientLocal : patients) {
            mCompositeDisposable.add(
                    haniotNetRepository.savePatient(patientLocal)
                            .doAfterSuccess(patientServer -> { // patiente com _id, posso enviar as measurements, questionnaires
                                haniotNetRepository.associatePatientToPilotStudy(patientLocal.getPilotId(), patientServer.get_id()).subscribe();

                                if (patientLocal.getId() == appPreferencesHelper.getLastPatient().getId())
                                    appPreferencesHelper.saveLastPatient(patientServer);
                                long patientId = patientLocal.getId();
                                patientDAO.markAsSync(patientId);
                                String patient_id = patientServer.get_id();

                                //pegar a lista de measurements com o id(long) dele
                                List<Measurement> mAux = measurementDAO.getAllNotSync(patientId);
                                List<NutritionalQuestionnaire> nAux = nutritionalQuestionnaireDAO.getAllNotSync(patientId);
                                List<OdontologicalQuestionnaire> oAux = odontologicalQuestionnaireDAO.getAllNotSync(patientId);

                                for (Measurement m : mAux) {
                                    m.setUser_id(patient_id); // seta com o _id recebido agora
                                    mCompositeDisposable.add(
                                            haniotNetRepository.saveMeasurement(m)
                                                    .doAfterSuccess(measurement -> measurementDAO.markAsSync(m.getId()))
                                                    .subscribe()
                                    );
                                }

                                for (NutritionalQuestionnaire n : nAux) {
                                    n.setPatient_id(patient_id);
                                    mCompositeDisposable.add(
                                            haniotNetRepository.saveNutritionalQuestionnaire(n)
                                                    .doAfterSuccess(nutritionalQuestionnaire -> nutritionalQuestionnaireDAO.markAsSync(n.getId()))
                                                    .subscribe()
                                    );
                                }

                                for (OdontologicalQuestionnaire o : oAux) {
                                    o.setPatient_id(patient_id);
                                    mCompositeDisposable.add(
                                            haniotNetRepository.saveOdontologicalQuestionnaire(o)
                                                    .doAfterSuccess(odontologicalQuestionnaire -> odontologicalQuestionnaireDAO.markAsSync(o.getId()))
                                                    .subscribe()
                                    );
                                }
                            })
                            .subscribe()
            );
        }
        // Segundo envia os dados dos pacientes já sincronizados / já tem o _id
        measurements = measurementDAO.getAllNotSync();
        nutritionalQuestionnaires = nutritionalQuestionnaireDAO.getAllNotSync();
        odontologicalQuestionnaires = odontologicalQuestionnaireDAO.getAllNotSync();

        for (Measurement m : measurements) {
            Log.i(TAG, "sendUnsynchronized: PATIENT SYNC");
            if (m.getUser_id() != null)
                mCompositeDisposable.add(
                        haniotNetRepository.saveMeasurement(m)
                                .doAfterSuccess(measurement1 -> measurementDAO.markAsSync(m.getId()))
                                .subscribe()
                );
        }

        for (NutritionalQuestionnaire n : nutritionalQuestionnaires) {
            if (n.getPatient_id() != null)
                mCompositeDisposable.add(
                        haniotNetRepository.saveNutritionalQuestionnaire(n)
                                .doAfterSuccess(nutritionalQuestionnaire1 -> nutritionalQuestionnaireDAO.markAsSync(n.getId()))
                                .subscribe()
                );
        }

        for (OdontologicalQuestionnaire o : odontologicalQuestionnaires) {
            if (o.getPatient_id() != null)
                mCompositeDisposable.add(
                        haniotNetRepository.saveOdontologicalQuestionnaire(o)
                                .doAfterSuccess(odontologicalQuestionnaire1 -> odontologicalQuestionnaireDAO.markAsSync(o.getId()))
                                .subscribe()
                );
        }
        Log.i(TAG, "sendUnsynchronized: DADOS OFF ENVIADOS");
    }

    public synchronized void syncronize() {

        if (ConnectionUtils.internetIsEnabled(this.mContext)) {
            sendUnsynchronized(); // envia os dados não sincronizados

            // Baixa os mais recentes do servidor

            String pilotStudyId = getPilotStudyId();
            if (pilotStudyId == null || "".equals(pilotStudyId)) return;

            Log.i(TAG, "syncronize: Agora vamos baixar os pacientes....");

            mCompositeDisposable.add(haniotNetRepository
                    .getAllPatients(pilotStudyId, "created_at", 1, 100)
                    .doAfterSuccess(patients1 -> {
                        Log.i(TAG, "syncronize: CLEAR");
                        mCompositeDisposable.clear();
                        patientDAO.removeSyncronized();

                        for (Patient patientServer : patients1) {
                            long patientId = patientDAO.save(patientServer);
                            if (patientId == 0) continue;

                            Single<List<Measurement>> measurements =
                                    haniotNetRepository.getAllMeasurements(patientServer.get_id(), 1, 100, "-timestamp");
                            Single<List<NutritionalQuestionnaire>> nutritionalQuestionnaires =
                                    haniotNetRepository.getAllNutritionalQuestionnaires(patientServer.get_id(), 1, 100, "-created_at");
                            Single<List<OdontologicalQuestionnaire>> odontologicalQuestionnaires =
                                    haniotNetRepository.getAllOdontologicalQuestionnaires(patientServer.get_id(), 1, 100, "-created_at");

                            mCompositeDisposable.add(
                                    Single.zip(measurements, nutritionalQuestionnaires, odontologicalQuestionnaires, GetResponseList::new)
                                            .doAfterSuccess(o -> {
                                                Log.i(TAG, "syncronize: doAfterSuccess");
                                            })
                                            .subscribe(getResponse -> {

                                                if (getResponse.measurements != null && !getResponse.measurements.isEmpty()) {
                                                    measurementDAO.removeSyncronized(patientServer);

                                                    for (Measurement m : getResponse.measurements) {
                                                        m.setUserId(patientId);
                                                        long idMe = measurementDAO.save(m);
                                                        if (idMe > 0) {
                                                            m.setId(idMe);
                                                            Log.i(TAG, "measurement salva de: " + m.toString());
                                                        }
                                                    }
                                                }

                                                if (getResponse.nutritionalQuestionnaires != null && !getResponse.nutritionalQuestionnaires.isEmpty()) {
                                                    nutritionalQuestionnaireDAO.removeSyncronized(patientServer);

                                                    for (NutritionalQuestionnaire n : getResponse.nutritionalQuestionnaires) {
                                                        n.setPatientId(patientId);
                                                        nutritionalQuestionnaireDAO.save(n);
                                                    }
                                                }

                                                if (getResponse.odontologicalQuestionnaires != null && !getResponse.odontologicalQuestionnaires.isEmpty()) {
                                                    odontologicalQuestionnaireDAO.removeSyncronized(patientServer);

                                                    for (OdontologicalQuestionnaire o : getResponse.odontologicalQuestionnaires) {
                                                        o.setPatientId(patientId);
                                                        odontologicalQuestionnaireDAO.save(o);
                                                    }
                                                }
                                                Log.i(TAG, "syncronize: Baixados (" + patientDAO.getBy_id(patientServer.get_id()).getName() + "): " +
                                                        measurementDAO.getAllMeasurements(patientServer.get_id(), null, null, null, 1, 100));
                                            })
                            );
                        }
                        Log.i(TAG, "pacientes salvos localmente: " + patientDAO.getAllPatients(pilotStudyId, null, 1, 100).toString());
                    })
                    .subscribe((patients -> Log.i(TAG, "subscribe executado com sucesso...")),
                            throwable -> Log.i(TAG, "Error ao baixar, não remover..."))
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

    private String getPilotStudyId() {
        if (appPreferencesHelper.getLastPilotStudy() != null) {
            return appPreferencesHelper.getLastPilotStudy().get_id();
        }
        if (appPreferencesHelper.getUserLogged() != null) {
            return appPreferencesHelper.getUserLogged().getPilotStudyIDSelected();
        }
        return null;
    }

    private class GetResponseList {
        List<Measurement> measurements;
        List<NutritionalQuestionnaire> nutritionalQuestionnaires;
        List<OdontologicalQuestionnaire> odontologicalQuestionnaires;

        public GetResponseList(List<Measurement> measurements, List<NutritionalQuestionnaire> nutritionalQuestionnaires, List<OdontologicalQuestionnaire> odontologicalQuestionnaires) {
            this.measurements = measurements;
            this.nutritionalQuestionnaires = nutritionalQuestionnaires;
            this.odontologicalQuestionnaires = odontologicalQuestionnaires;
        }
    }

//    private void downloadMeasurements(String patient_id, long patientId) {
//        mCompositeDisposable.add(
//                haniotNetRepository.getAllMeasurements(patient_id, 1, 100, "-timestamp")
//                        .doAfterSuccess(measurements -> {
//                            measurementDAO.removeSyncronized();
//
//                            for (Measurement m : measurements) {
//                                m.setUserId(patientId);
//                                measurementDAO.save(m);
//                            }
//                            Log.i(TAG, "syncronize: Baixados (" + patientDAO.getBy_id(patient_id).getName() + "):" + measurementDAO.getAllMeasurements(patient_id, null, null, null, 1, 100));
//                        }).subscribe()
//        );
//    }

//    private void downloadOdontologicalQuestionnaires(String patient_id, long patientId) {
//        mCompositeDisposable.add(
//                haniotNetRepository.getAllOdontologicalQuestionnaires(patient_id, 1, 100, "created_at")
//                        .doAfterSuccess(odontologicalQuestionnaires -> {
//
////                            DisposableManager.clear();
//                        }).subscribe()
//        );
//    }

//    private void downloadNutritionalQuestionnaires(String patient_id, long patientId) {
//        mCompositeDisposable.add(
//                haniotNetRepository.getAllNutritionalQuestionnaires(patient_id, 1, 100, "created_at")
//                        .doAfterSuccess(nutritionalQuestionnaires -> {
//                            nutritionalQuestionnaireDAO.removeSyncronized();
//
//                            for (NutritionalQuestionnaire n : nutritionalQuestionnaires) {
//                                n.setSync(true);
//                                n.setPatient_id(patient_id);
//                                n.setPatientId(patientId);
//                                nutritionalQuestionnaireDAO.save(n);
//                            }
//                        }).subscribe()
//        );
//    }

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
            return haniotNetRepository.getAllDevices(userId);
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

        if (ConnectionUtils.internetIsEnabled(mContext))
            syncronize();

        return Single.just(measurement)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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
        Log.i(TAG, "Measurements (" + typeMeasurement + ") de: " + patient.getName());
        List<Measurement> aux = measurementDAO.getMeasurementsByType(patient, typeMeasurement, sort, dateStart, dateEnd, page, limit);

        if (ConnectionUtils.internetIsEnabled(mContext)) {
            Log.i(TAG, "getAllMeasurementsByType: COM INTERNET - page[" + page + "] -> " + aux.toString());

            return haniotNetRepository.getAllMeasurementsByType(
                    patient.get_id(), typeMeasurement, sort, dateStart, dateEnd, page, limit);
        } else {
            Log.i(TAG, "getAllMeasurementsByType: SEM INTERNET - page[" + page + "] -> " + aux.toString());
            return Single.just(aux);
        }
    }

    // --------- PATIENT DAO --------------

    public Single<Patient> getPatientBy_id(@NonNull String _id) {
        Log.i(TAG, "getPatientBy_id: ");
        if (ConnectionUtils.internetIsEnabled(mContext)) {
            return haniotNetRepository.getPatient(_id);
        } else {
            Patient p = patientDAO.getBy_id(_id);

            if (p != null) {
                return Single.just(p);
            } else {
                return haniotNetRepository.getPatient(_id);
            }
        }
    }

    public Single<Patient> getPatient(@NonNull Patient patient) {
        Log.i(TAG, "getPatientBy_id: ");
        if (ConnectionUtils.internetIsEnabled(mContext) && patient.get_id() != null) {
            return haniotNetRepository.getPatient(patient.get_id());
        } else {
            Patient p = patientDAO.get(patient);

            if (p != null) {
                return Single.just(p);
            } else {
                return haniotNetRepository.getPatient(patient.get_id());
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

        if (patient.get_id() == null) {
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
            return haniotNetRepository.getAllPatients(pilotStudyId, sort, page, limit);
//                    .map(patients -> {
//                        Log.i(TAG, "getAllPatients: " + patients.toString());
////                        patients.addAll(patientDAO.getAllNotSync());
//                        return patients;
//                    });
        } else {
            List<Patient> aux = patientDAO.getAllPatients(pilotStudyId, sort, page, limit);
            Log.i(TAG, "getAllPatients(LOCAL): " + aux.toString());
            return Single.just(aux);
        }
    }

    public Completable deletePatient(@NonNull Patient patient) {
        Log.i(TAG, "deletePatient: ");
        if (patient.get_id() == null) {

            if (patientDAO.remove(patient.getId())) {
                measurementDAO.removeByPatientId(patient.getId());
                nutritionalQuestionnaireDAO.removeByPatienId(patient.getId());
                odontologicalQuestionnaireDAO.removeByPatientId(patient.getId());
            }
            return Completable.complete();
        } else {
            return haniotNetRepository.deletePatient(patient.get_id())
                    .doOnComplete(this::syncronize);
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
            Log.i(TAG, "getAllNutritionalQuestionnaires: ON: " + aux.toString());
            return haniotNetRepository.getAllNutritionalQuestionnaires(patient.get_id(), page, limit, sort);
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

        if (ConnectionUtils.internetIsEnabled(mContext))
            syncronize();
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
            return haniotNetRepository.getAllOdontologicalQuestionnaires(patientId, page, limit, sort);
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

        if (ConnectionUtils.internetIsEnabled(mContext))
            syncronize();

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