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
import br.edu.uepb.nutes.haniot.data.model.ActivityHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.Device;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.nutritional.NutritionalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.model.odontological.OdontologicalQuestionnaire;
import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.Response;

/**
 * Classe responsável por gerenciar o repositório local e o repositório remoto
 */
public class Repository extends RepositoryOn {

    public static Repository getInstance(Context context) {
        return new Repository(context);
    }

    private Repository(Context context) {
        super(context);
        this.deviceDAO = DeviceDAO.getInstance(context);
        this.measurementDAO = MeasurementDAO.getInstance(context);
        this.patientDAO = PatientDAO.getInstance(context);
        this.nutritionalQuestionnaireDAO = NutritionalQuestionnaireDAO.getInstance(context);
        this.odontologicalQuestionnaireDAO = OdontologicalQuestionnaireDAO.getInstance(context);

        this.synchronize = Synchronize.getInstance(context);
    }

    private DeviceDAO deviceDAO;
    private MeasurementDAO measurementDAO;
    private PatientDAO patientDAO;
    private NutritionalQuestionnaireDAO nutritionalQuestionnaireDAO;
    private OdontologicalQuestionnaireDAO odontologicalQuestionnaireDAO;
    private Synchronize synchronize;

    public Single<Device> saveDeviceRemote(Device device) {
        Log.i(TAG, "saveDeviceRemote: " + device.toString());
        if (isConnected()) {
            return haniotNetRepository.saveDevice(device)
                    .map(device1 -> {
                        synchronize.synchronize();
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
        if (isConnected()) {
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
                .doOnComplete(() -> synchronize.synchronize());
    }

    /**
     * Removes all devices.
     * According to userId user.
     * @param userId {@link String}
     * @return boolean - True if removed or False if not removed
     */
    public boolean removeAllDevicesLocal(@NonNull String userId) {
        Log.i(TAG, "removeAllDevicesLocal: ");
        return deviceDAO.removeAll(userId);
    }

    /**
     * Adds a new measurement to the database
     * @param measurement Measurement
     * @return boolean
     */
    public Single<Measurement> saveMeasurement(@NonNull Measurement measurement) {
        Log.i(TAG, "saveMeasurement: ");
        measurement.setSync(false);
        long id = measurementDAO.save(measurement);
        measurement.setId(id);

        if (isConnected())
            synchronize.synchronize();
        return Single.just(measurement);
    }

    public Single<Object> saveMeasurement(List<Measurement> measurements) {
        Log.i(TAG, "saveMeasurements: ");
        for (Measurement m : measurements) {
            m.setSync(false);
            long id = measurementDAO.save(m);
            m.setId(id);
        }
        if (isConnected())
            synchronize.synchronize();
        return Single.just(measurements);
    }

    /**
     * Remove measurement
     * @param measurement Measurement
     * @return boolean
     */
    public Completable deleteMeasurement(@NonNull Measurement measurement) {
        Log.i(TAG, "deleteMeasurement: ");
        if (measurement.get_id() == null) { // não foi sincronizada ainda, removo localmente
            measurementDAO.remove(measurement.getId());

            if (isConnected())
                synchronize.synchronize();
            return Completable.complete();
        } else {
            return haniotNetRepository.deleteMeasurement(measurement.getUser_id(), measurement.get_id())
                    .doOnComplete(() -> synchronize.synchronize());
        }
    }

    public Single<List<Measurement>> getAllMeasurementsByType(Patient patient, String type,
                                                              String sort, String dateStart,
                                                              String dateEnd, int page, int limit) {
        Log.i(TAG, "Measurements (" + type + ") de: " + patient.getName());
        List<Measurement> aux = measurementDAO.getMeasurementsByType(patient, type, sort, dateStart, dateEnd, page, limit);

        if (patient.get_id() != null && isConnected()) {
            Log.i(TAG, "getAllMeasurementsByType: ON - page[" + page + "] -> " + aux.toString());

            return haniotNetRepository.getAllMeasurementsByType(
                    patient.get_id(), type, sort, dateStart, dateEnd, page, limit);
        } else {
            Log.i(TAG, "getAllMeasurementsByType: OFF - page[" + page + "] -> " + aux.toString());
            return Single.just(aux);
        }
    }

    public Single<Patient> getPatient(@NonNull Patient patient) {
        Log.i(TAG, "getPatientBy_id: ");

        if (patient.get_id() == null) {
            Patient p = patientDAO.get(patient);
            return Single.just(p);
        } else {
            return haniotNetRepository.getPatient(patient.get_id());
        }
    }

    public Single<Patient> savePatient(@NonNull Patient patient) {
        Log.i(TAG, "savePatient: " + patient.toString());

        if (isConnected()) {
            return haniotNetRepository.savePatient(patient);
        } else {
            patient.setSync(false);
            long id = patientDAO.save(patient);
            patient.setId(id);
            Log.i(TAG, "Salvo localmente");
            return Single.just(patient);
        }
    }

    public Single<Response<Void>> associatePatientToPilotStudy(String pilotStudyId, Patient patient) {
        Log.i(TAG, "associatePatientToPilotStudy: ");

        if (isConnected() && patient.get_id() != null) {
            return haniotNetRepository.associatePatientToPilotStudy(pilotStudyId, patient.get_id())
                    .doAfterSuccess(a -> synchronize.synchronize());
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

    public Single<Patient> updatePatient(@NonNull Patient patient) {
        Log.i(TAG, "updatePatient: ");

        if (patient.get_id() == null) {
            patient.setSync(false);
            patientDAO.update(patient);

            if (isConnected())
                synchronize.synchronize();
            return Single.just(patient);
        } else {
            return haniotNetRepository.updatePatient(patient)
                    .doAfterSuccess(patient1 -> synchronize.synchronize());
        }
    }

    public Single<List<Patient>> getAllPatients(String pilotStudyId, String sort, int page, int limit) {
        Log.i(TAG, "getAllPatients: ");
        if (isConnected()) {
            return haniotNetRepository.getAllPatients(pilotStudyId, sort, page, limit)
                    .map(patients -> {
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

        if (patient.get_id() == null) { // ta salvo apenas off
            if (patientDAO.remove(patient.getId())) {
                measurementDAO.removeByPatientId(patient.getId());
                nutritionalQuestionnaireDAO.removeByPatienId(patient.getId());
                odontologicalQuestionnaireDAO.removeByPatientId(patient.getId());
            }
            if (isConnected())
                synchronize.synchronize();
            return Completable.complete();
        } else {
            return haniotNetRepository.deletePatient(patient.get_id())
                    .doOnComplete(() -> synchronize.synchronize());
        }
    }

    public Single<List<NutritionalQuestionnaire>> getAllNutritionalQuestionnaires(Patient patient, int page, int limit, String sort) {
        List<NutritionalQuestionnaire> aux = nutritionalQuestionnaireDAO.getAll(patient, page, limit, sort);

        if (isConnected() && patient.get_id() != null) {
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

        if (isConnected())
            synchronize.synchronize();
        return Single.just(n);
    }

    public Single<Object> updateNutritionalQuestionnaire(Patient patient, NutritionalQuestionnaire questionnaire, String question, ActivityHabitsRecord newValue) {
        Log.i(TAG, "updateNutritionalQuestionnaire: ");

        if (questionnaire.get_id() == null) { // ainda tá off
            nutritionalQuestionnaireDAO.update(questionnaire.getId(), question, newValue); // pra atualizar OFF tem que ser id long

            if (isConnected())
                synchronize.synchronize();
            return Single.just(newValue);
        } else { // so on
            return haniotNetRepository.updateNutritionalQuestionnaire(patient.get_id(), questionnaire.get_id(), question, newValue)
                    .doAfterSuccess(o -> synchronize.synchronize());
        }
    }

    public Single<List<OdontologicalQuestionnaire>> getAllOdontologicalQuestionnaires(Patient patient, int page, int limit, String sort) {
        List<OdontologicalQuestionnaire> aux = odontologicalQuestionnaireDAO.getAll(patient, page, limit, sort);

        if (isConnected() && patient.get_id() != null) {
            Log.i(TAG, "getAllOdontologicalQuestionnaires: ON: " + aux.toString());
            return haniotNetRepository.getAllOdontologicalQuestionnaires(patient.get_id(), page, limit, sort);
        } else {
            Log.i(TAG, "getAllOdontologicalQuestionnaires: OFF: " + aux.toString());
            return Single.just(aux);
        }
    }

    public Single<OdontologicalQuestionnaire> saveOdontologicalQuestionnaire(OdontologicalQuestionnaire odontologicalQuestionnaire) {
        Log.i(TAG, "saveOdontologicalQuestionnaire: ");

        odontologicalQuestionnaire.setSync(false);
        long id = odontologicalQuestionnaireDAO.save(odontologicalQuestionnaire);
        odontologicalQuestionnaire.setId(id);

        if (isConnected())
            synchronize.synchronize();
        return Single.just(odontologicalQuestionnaire);
    }

    public Single<Object> updateOdontologicalQuestionnaire(Patient patient, OdontologicalQuestionnaire questionnaire, String question, ActivityHabitsRecord newValue) {
        Log.i(TAG, "updateOdontologicalQuestionnaire: ");

        if (questionnaire.get_id() == null) {
            odontologicalQuestionnaireDAO.update(questionnaire.getId(), question, newValue);

            if (isConnected())
                synchronize.synchronize();
            return Single.just(newValue);
        } else {
            return haniotNetRepository.updateOdontologicalQuestionnaire(patient.get_id(), questionnaire.get_id(), question, newValue)
                    .doAfterSuccess(o -> synchronize.synchronize());
        }
    }
}