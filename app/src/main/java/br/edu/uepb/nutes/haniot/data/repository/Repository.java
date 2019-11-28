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
import br.edu.uepb.nutes.haniot.data.model.User;
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

    public Single<Device> saveDevice(Device device) {
        Log.i(TAG, "saveDevice: " + device.toString());

        long id = deviceDAO.save(device);
        device.setId(id);

        synchronize.synchronize();

        return Single.just(device);
    }

    public Single<List<Device>> getAllDevices(String user_id) {
        Log.i(TAG, "getAllDevices: ");
        if (isConnected()) {
            return haniotNetRepository.getAllDevices(user_id)
                    .map(devices -> {
                        devices.addAll(deviceDAO.getAllNotSync());
                        return devices;
                    });
        } else {
            List<Device> devices = deviceDAO.getAllDevices(user_id);
            Log.i(TAG, "getAllDevices: LOCAL -> " + devices.toString());
            return Single.just(devices);
        }
    }

    /**
     * Retrieves all device according to userId and type.
     *
     * @param user User
     * @param type String
     * @return DeviceOB
     */
    public Single<Device> getDeviceByType(@NonNull User user, @NonNull String type) {

        if (user.get_id() != null && isConnected()) {
            return haniotNetRepository.getAllDevices(user.get_id())
                    .map(devices -> {
                        for (Device d : devices) {
                            if (type.equals(d.getType()))
                                return d;
                        }
                        return null;
                    });
        } else {
            Device d = deviceDAO.getByType(user, type);
            if (d != null)
                return Single.just(d);
            else
                return haniotNetRepository.getAllDevices(user.get_id())
                        .map(devices -> {
                            for (Device e : devices) {
                                if (type.equals(e.getType()))
                                    return e;
                            }
                            return null;
                        });
        }
    }

    /**
     * Removes device passed as parameter.
     *
     * @param userId String
     * @param device Device
     * @return boolean
     */
    public Completable deleteDevice(@NonNull String userId, @NonNull Device device) {
        Log.i(TAG, "deleteDevice: ");

        if (device.get_id() == null) {
            deviceDAO.remove(device.getId());

            synchronize.synchronize();
            return Completable.complete();
        } else {
            return haniotNetRepository.deleteDevice(userId, device.get_id())
                    .doOnComplete(() -> synchronize.synchronize());
        }
    }

    /**
     * Adds a new measurement to the database
     *
     * @param measurement Measurement
     * @return boolean
     */
    public Single<Measurement> saveMeasurement(@NonNull Measurement measurement) {
        Log.i(TAG, "saveMeasurement: ");
        measurement.setSync(false);
        long id = measurementDAO.save(measurement);
        measurement.setId(id);

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
        synchronize.synchronize();
        return Single.just(measurements);
    }

    /**
     * Remove measurement
     *
     * @param measurement Measurement
     * @return boolean
     */
    public Completable deleteMeasurement(@NonNull Measurement measurement) {
        Log.i(TAG, "deleteMeasurement: ");
        if (measurement.get_id() == null) { // não foi sincronizada ainda, removo localmente
            measurementDAO.remove(measurement.getId());

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

        if (patient.get_id() != null && isConnected()) {
            if (!measurementDAO.isSync()) synchronize.synchronize();

            return haniotNetRepository.getAllMeasurementsByType(
                    patient.get_id(), type, sort, dateStart, dateEnd, page, limit);
        } else {
            List<Measurement> aux = measurementDAO.getMeasurementsByType(patient, type, sort, dateStart, dateEnd, page, limit);
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
            if (!patientDAO.isSync()) synchronize.synchronize();

            return haniotNetRepository.getAllPatients(pilotStudyId, sort, page, limit);
        } else {
            List<Patient> aux = patientDAO.getAllPatients(pilotStudyId, sort, page, limit);
            Log.i(TAG, "getAllPatients(LOCAL): " + aux.toString());
            return Single.just(aux);
        }
    }

    public Completable deletePatient(@NonNull Patient patient) {
        Log.i(TAG, "deletePatient: ");

        if (patient.get_id() == null) { // ta salvo local
            if (patientDAO.remove(patient.getId())) {
                measurementDAO.removeByPatientId(patient.getId());
                nutritionalQuestionnaireDAO.removeByPatientId(patient.getId());
                odontologicalQuestionnaireDAO.removeByPatientId(patient.getId());
            }
            synchronize.synchronize();
            return Completable.complete();
        } else {
            return haniotNetRepository.deletePatient(patient.get_id())
                    .doOnComplete(() -> synchronize.synchronize());
        }
    }

    public Single<List<NutritionalQuestionnaire>> getAllNutritionalQuestionnaires(Patient patient, int page, int limit, String sort) {
        Log.i(TAG, "getAllNutritionalQuestionnaires (" + patient.getName() + "):");

        if (patient.get_id() != null && isConnected()) {
            if (!nutritionalQuestionnaireDAO.isSync()) synchronize.synchronize();

            return haniotNetRepository.getAllNutritionalQuestionnaires(patient.get_id(), page, limit, sort);
        } else {
            List<NutritionalQuestionnaire> aux = nutritionalQuestionnaireDAO.getAll(patient, page, limit, sort);
            return Single.just(aux);
        }
    }

    public Single<NutritionalQuestionnaire> saveNutritionalQuestionnaire(NutritionalQuestionnaire n) {
        Log.i(TAG, "saveNutritionalQuestionnaire: ");

        n.setSync(false);
        long id = nutritionalQuestionnaireDAO.save(n);
        n.setId(id);

        synchronize.synchronize();
        return Single.just(n);
    }

    public Single<Object> updateNutritionalQuestionnaire(Patient patient, NutritionalQuestionnaire questionnaire, String question, ActivityHabitsRecord newValue) {
        Log.i(TAG, "updateNutritionalQuestionnaire: ");

        if (questionnaire.get_id() == null) { // ainda tá off
            nutritionalQuestionnaireDAO.update(questionnaire.getId(), question, newValue); // pra atualizar OFF tem que ser id long

            synchronize.synchronize();
            return Single.just(newValue);
        } else { // so on
            return haniotNetRepository.updateNutritionalQuestionnaire(patient.get_id(), questionnaire.get_id(), question, newValue)
                    .doAfterSuccess(o -> synchronize.synchronize());
        }
    }

    public Single<List<OdontologicalQuestionnaire>> getAllOdontologicalQuestionnaires(Patient patient, int page, int limit, String sort) {
        Log.i(TAG, "getAllOdontologicalQuestionnaires (" + patient.getName() + "):");

        if (patient.get_id() != null && isConnected()) {
            if (!odontologicalQuestionnaireDAO.isSync()) synchronize.synchronize();

            return haniotNetRepository.getAllOdontologicalQuestionnaires(patient.get_id(), page, limit, sort);
        } else {
            List<OdontologicalQuestionnaire> aux = odontologicalQuestionnaireDAO.getAll(patient, page, limit, sort);
            return Single.just(aux);
        }
    }

    public Single<OdontologicalQuestionnaire> saveOdontologicalQuestionnaire(OdontologicalQuestionnaire odontologicalQuestionnaire) {
        Log.i(TAG, "saveOdontologicalQuestionnaire: ");

        odontologicalQuestionnaire.setSync(false);
        long id = odontologicalQuestionnaireDAO.save(odontologicalQuestionnaire);
        odontologicalQuestionnaire.setId(id);

        synchronize.synchronize();
        return Single.just(odontologicalQuestionnaire);
    }

    public Single<Object> updateOdontologicalQuestionnaire(Patient patient, OdontologicalQuestionnaire questionnaire, String question, ActivityHabitsRecord newValue) {
        Log.i(TAG, "updateOdontologicalQuestionnaire: ");

        if (questionnaire.get_id() == null) {
            odontologicalQuestionnaireDAO.update(questionnaire.getId(), question, newValue);

            synchronize.synchronize();
            return Single.just(newValue);
        } else {
            return haniotNetRepository.updateOdontologicalQuestionnaire(patient.get_id(), questionnaire.get_id(), question, newValue)
                    .doAfterSuccess(o -> synchronize.synchronize());
        }
    }
}