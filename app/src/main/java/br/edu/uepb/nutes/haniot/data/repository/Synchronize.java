package br.edu.uepb.nutes.haniot.data.repository;

import android.content.Context;
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
import br.edu.uepb.nutes.haniot.data.model.Result;
import br.edu.uepb.nutes.haniot.data.model.nutritional.NutritionalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.model.odontological.OdontologicalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.repository.local.pref.AppPreferencesHelper;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import br.edu.uepb.nutes.haniot.utils.ConnectionUtils;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;

public class Synchronize {

    public static Synchronize getInstance(Context context) {
        return new Synchronize(context);
    }

    private final static String TAG = "SYNCHRONIZE";

    private Context mContext;
    private DeviceDAO deviceDAO;
    private PatientDAO patientDAO;
    private MeasurementDAO measurementDAO;
    private NutritionalQuestionnaireDAO nutritionalQuestionnaireDAO;
    private OdontologicalQuestionnaireDAO odontologicalQuestionnaireDAO;
    private HaniotNetRepository haniotNetRepository;
    private AppPreferencesHelper appPreferencesHelper;
    private CompositeDisposable mDisposable;

    private Synchronize(Context context) {
        this.mContext = context;
        this.deviceDAO = DeviceDAO.getInstance(context);
        this.patientDAO = PatientDAO.getInstance(context);
        this.measurementDAO = MeasurementDAO.getInstance(context);
        this.nutritionalQuestionnaireDAO = NutritionalQuestionnaireDAO.getInstance(context);
        this.odontologicalQuestionnaireDAO = OdontologicalQuestionnaireDAO.getInstance(context);
        this.haniotNetRepository = HaniotNetRepository.getInstance(context);
        this.appPreferencesHelper = AppPreferencesHelper.getInstance(context);
        this.mDisposable = new CompositeDisposable();
    }

    /**
     * Synchronize data of application
     * Send the data unsynchronized and download most recent data of server
     */
    public synchronized void synchronize() {

        if (ConnectionUtils.internetIsEnabled(this.mContext)) {
            Log.i(TAG, "synchronize: COM INTERNET");
            sendUnsynchronized(); // envia os dados não sincronizados
            downloadMostRecent(); // Baixa os mais recentes do servidor
        } else {
            Log.i(TAG, "synchronize: SEM INTERNET");
        }
    }

    /**
     * Send or try send the measurements unsynchronized
     */
    private void sendUnsynchronized() {
        sendDevices(deviceDAO.getAllNotSync());

        List<Patient> patients = patientDAO.getAllNotSync();
        // primeiro envia os pacientes não sincronizados, juntamente com os seus dados (medições e questionários) / ainda não tem o _id
        for (Patient patientLocal : patients) {
            mDisposable.add(
                    haniotNetRepository.savePatient(patientLocal)
                            .doAfterSuccess(patientServer -> { // patiente com _id, posso enviar as measurements, questionnaires
                                haniotNetRepository.associatePatientToPilotStudy(patientLocal.getPilotId(), patientServer.get_id()).subscribe();

                                if (patientLocal.getId() == appPreferencesHelper.getLastPatient().getId())
                                    appPreferencesHelper.saveLastPatient(patientServer);

                                long patientId = patientLocal.getId();
                                patientDAO.remove(patientId);
                                String patient_id = patientServer.get_id();

                                //pega as lista de dados com o id(long) do paciente enviado
                                List<Measurement> mAux = measurementDAO.getAllNotSync(patientId);
                                List<NutritionalQuestionnaire> nAux = nutritionalQuestionnaireDAO.getAllNotSync(patientId);
                                List<OdontologicalQuestionnaire> oAux = odontologicalQuestionnaireDAO.getAllNotSync(patientId);

                                sendMeasurementsSamePatient(patient_id, mAux);

                                for (NutritionalQuestionnaire n : nAux)
                                    n.setPatient_id(patient_id);
                                sendNutritionalQuestionnaire(nAux);

                                for (OdontologicalQuestionnaire o : oAux)
                                    o.setPatient_id(patient_id);
                                sendOdontologicalQuestionnaires(oAux);
                            })
                            .subscribe((patient, throwable) -> {
                            })
            );
        }
        // Segundo envia os dados dos pacientes já sincronizados / já tem o _id
        sendMeasurements(measurementDAO.getAllNotSync());
        sendNutritionalQuestionnaire(nutritionalQuestionnaireDAO.getAllNotSync());
        sendOdontologicalQuestionnaires(odontologicalQuestionnaireDAO.getAllNotSync());
        Log.i(TAG, "sendUnsynchronized: ");
    }

    /**
     * Send measurements from multiple patients
     *
     * @param measurements Measurements
     */
    private void sendMeasurements(List<Measurement> measurements) {
        for (Measurement m : measurements) {
            Log.i(TAG, "sendMeasurements: "+ m.toJson());
            if (m.getUser_id() != null)
                mDisposable.add(
                        haniotNetRepository.saveMeasurement(m)
                                .doAfterSuccess(measurement -> {
                                    Log.i(TAG, "sendMeasurements: doAfterSuccess");
                                    measurementDAO.remove(m.getId());
                                })
                                .subscribe((measurement, throwable) -> {
                                    Log.i(TAG, "sendMeasurements: "+throwable.getMessage());
                                })
                );
        }
    }

    /**
     * Send Devices
     *
     * @param devices List of Devices
     */
    private void sendDevices(List<Device> devices) {
        for (Device device : devices)
            mDisposable.add(
                    haniotNetRepository.saveDevice(device)
                            .doAfterSuccess(device1 -> deviceDAO.markAsSync(device.getId()))
                            .subscribe((device1, throwable) -> {
                            })
            );
    }

    /**
     * Send Odontological questionnaires
     *
     * @param list List of Odontological questionnaires
     */
    private void sendOdontologicalQuestionnaires(List<OdontologicalQuestionnaire> list) {
        for (OdontologicalQuestionnaire o : list)
            if (o.getPatient_id() != null)
                mDisposable.add(
                        haniotNetRepository.saveOdontologicalQuestionnaire(o)
                                .doAfterSuccess(odontologicalQuestionnaire -> odontologicalQuestionnaireDAO.remove(o.getId()))
                                .subscribe((odontologicalQuestionnaire, throwable) -> {
                                })
                );
    }

    /**
     * Send Nutritional questionnaires
     *
     * @param list List of Nutritional questionnaires
     */
    private void sendNutritionalQuestionnaire(List<NutritionalQuestionnaire> list) {
        for (NutritionalQuestionnaire n : list)
            if (n.getPatient_id() != null)
                mDisposable.add(
                        haniotNetRepository.saveNutritionalQuestionnaire(n)
                                .doAfterSuccess(nutritionalQuestionnaire -> nutritionalQuestionnaireDAO.remove(n.getId()))
                                .subscribe((nutritionalQuestionnaire, throwable) -> {
                                })
                );
    }

    /**
     * Send measurements from same patient
     *
     * @param patient_id   String
     * @param measurements List of Measurements
     */
    private void sendMeasurementsSamePatient(String patient_id, List<Measurement> measurements) {

        int count = measurements.size() / 100 + 1; // quantidade de requisições que serão feitas
        int low, high = 0;

        for (int i = 0; i < count; i++) {
            low = high;
            high = (high + 100 > measurements.size()) ? measurements.size() : high + 100;
            List<Measurement> list = measurements.subList(low, high);

            mDisposable.add(
                    haniotNetRepository.saveMeasurement(patient_id, list)
                            .subscribe(result -> {
                                List<Result.Success> success = result.getSuccess(); // medições gravadas com sucesso
                                for (Result.Success s : success)
                                    measurementDAO.markAsSync(s.getItem().getPatient_id(), s.getItem().getType(), s.getItem().getTimestamp());
                                Log.i(TAG, "sendMeasurementsSamePatient: " + result.toString());
                            }, throwable -> {
                            })
            );
        }
    }

    private void downloadMostRecent() {
        String pilotStudyId = getPilotStudyId();
        if (pilotStudyId == null || "".equals(pilotStudyId)) return;
        mDisposable.clear();

        mDisposable.add(haniotNetRepository
                .getAllPatients(pilotStudyId, "-created_at", 1, 100)
                .doAfterSuccess(patients1 -> {
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

                        mDisposable.add(
                                Single.zip(measurements, nutritionalQuestionnaires, odontologicalQuestionnaires, GetResponseList::new)
                                        .subscribe(getResponse -> {
                                            saveMeasurements(patientId, patientServer.get_id(), getResponse.measurements);
                                            saveNutritionalQuestionnaires(patientId, patientServer.get_id(), getResponse.nutritionalQuestionnaires);
                                            saveOdontologicalQuestionnaires(patientId, patientServer.get_id(), getResponse.odontologicalQuestionnaires);
                                        }, throwable -> {
                                        })
                        );
                    }
                    Log.i(TAG, "Patients download: " + patientDAO.getAllPatients(pilotStudyId, null, 1, 100).toString());
                })
                .subscribe((patients, throwable) -> {
                })
        );

        mDisposable.add(
                haniotNetRepository.getAllDevices(getUserId())
                        .doAfterSuccess(devices -> {
                            for (Device d : devices)
                                deviceDAO.save(d);
                        })
                        .subscribe((devices, throwable) -> {
                        })
        );
    }

    private void saveOdontologicalQuestionnaires(long patientId, String patient_id, List<OdontologicalQuestionnaire> odontologicalQuestionnaires) {
        if (odontologicalQuestionnaires == null) return;
        odontologicalQuestionnaireDAO.removeSyncronized(patient_id);

        for (OdontologicalQuestionnaire o : odontologicalQuestionnaires) {
            o.setPatientId(patientId);
            odontologicalQuestionnaireDAO.save(o);
        }
    }

    private void saveNutritionalQuestionnaires(long patientId, String patient_id, List<NutritionalQuestionnaire> nutritionalQuestionnaires) {
        if (nutritionalQuestionnaires == null) return;
        nutritionalQuestionnaireDAO.removeSyncronized(patient_id);

        for (NutritionalQuestionnaire n : nutritionalQuestionnaires) {
            n.setPatientId(patientId);
            nutritionalQuestionnaireDAO.save(n);
        }
    }

    private void saveMeasurements(long patientId, String patient_id, List<Measurement> measurements) {
        if (measurements == null) return;
        measurementDAO.removeSyncronized(patient_id);

        for (Measurement m : measurements) {
            m.setUserId(patientId);
            if (measurementDAO.save(m) > 0)
                Log.i(TAG, "measurement salva de: " + m.toString());
        }
    }

    /**
     * Recover the actual pilot study id
     *
     * @return String
     */
    private String getPilotStudyId() {
        if (appPreferencesHelper.getLastPilotStudy() != null) {
            return appPreferencesHelper.getLastPilotStudy().get_id();
        }
        if (appPreferencesHelper.getUserLogged() != null) {
            return appPreferencesHelper.getUserLogged().getPilotStudyIDSelected();
        }
        return null;
    }

    private String getUserId() {
        return appPreferencesHelper.getUserLogged().get_id();
    }

    /**
     * Class to response of a request zip
     */
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
}
