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

    private final static String TAG = "Syncronize";

    private Context mContext;
    private DeviceDAO deviceDAO;
    private PatientDAO patientDAO;
    private MeasurementDAO measurementDAO;
    private NutritionalQuestionnaireDAO nutritionalQuestionnaireDAO;
    private OdontologicalQuestionnaireDAO odontologicalQuestionnaireDAO;
    private HaniotNetRepository haniotNetRepository;
    private AppPreferencesHelper appPreferencesHelper;
    private CompositeDisposable mCompositeDisposable;

    private Synchronize(Context context) {
        this.mContext = context;
        this.deviceDAO = DeviceDAO.getInstance(context);
        this.patientDAO = PatientDAO.getInstance(context);
        this.measurementDAO = MeasurementDAO.getInstance(context);
        this.nutritionalQuestionnaireDAO = NutritionalQuestionnaireDAO.getInstance(context);
        this.odontologicalQuestionnaireDAO = OdontologicalQuestionnaireDAO.getInstance(context);
        this.haniotNetRepository = HaniotNetRepository.getInstance(context);
        this.appPreferencesHelper = AppPreferencesHelper.getInstance(context);
        this.mCompositeDisposable = new CompositeDisposable();
    }

    private void sendUnsynchronized() {
        List<Device> devices = deviceDAO.getAllNotSync();
        List<Patient> patients = patientDAO.getAllNotSync();
        List<Measurement> measurements = measurementDAO.getAllNotSync();
        List<NutritionalQuestionnaire> nutritionalQuestionnaires = nutritionalQuestionnaireDAO.getAllNotSync();
        List<OdontologicalQuestionnaire> odontologicalQuestionnaires = odontologicalQuestionnaireDAO.getAllNotSync();

        if (devices.isEmpty() && patients.isEmpty() && measurements.isEmpty() && nutritionalQuestionnaires.isEmpty() && odontologicalQuestionnaires.isEmpty()) return;

        sendDevices(devices);

        // primeiro envia os pacientes não sincronizados, juntamente com os seus dados (medições e questionários) / ainda não tem o _id
        for (Patient patientLocal : patients) {
            mCompositeDisposable.add(
                    haniotNetRepository.savePatient(patientLocal)
                            .doAfterSuccess(patientServer -> { // patiente com _id, posso enviar as measurements, questionnaires
                                haniotNetRepository.associatePatientToPilotStudy(patientLocal.getPilotId(), patientServer.get_id()).subscribe();

                                if (patientLocal.getId() == appPreferencesHelper.getLastPatient().getId()) appPreferencesHelper.saveLastPatient(patientServer);

                                long patientId = patientLocal.getId();
                                patientDAO.markAsSync(patientId);
                                String patient_id = patientServer.get_id();

                                //pegar a lista de measurements com o id(long) dele
                                List<Measurement> mAux = measurementDAO.getAllNotSync(patientId);
                                List<NutritionalQuestionnaire> nAux = nutritionalQuestionnaireDAO.getAllNotSync(patientId);
                                List<OdontologicalQuestionnaire> oAux = odontologicalQuestionnaireDAO.getAllNotSync(patientId);

                                for (Measurement m : mAux)
                                    m.setUser_id(patient_id); // seta com o _id recebido agora
                                sendMeasurements(mAux);

                                for (NutritionalQuestionnaire n : nAux)
                                    n.setPatient_id(patient_id);
                                sendNutritionalQuestionnaire(nAux);

                                for (OdontologicalQuestionnaire o : oAux)
                                    o.setPatient_id(patient_id);
                                sendOdontologicalQuestionnaires(oAux);
                            })
                            .subscribe((patient, throwable) -> {})
            );
        }
        // Segundo envia os dados dos pacientes já sincronizados / já tem o _id
        sendMeasurements(measurementDAO.getAllNotSync());
        sendNutritionalQuestionnaire(nutritionalQuestionnaireDAO.getAllNotSync());
        sendOdontologicalQuestionnaires(odontologicalQuestionnaireDAO.getAllNotSync());
        Log.i(TAG, "sendUnsynchronized: DADOS OFF ENVIADOS");
    }

    private void sendDevices(List<Device> devices) {
        for (Device device : devices)
            mCompositeDisposable.add(
                    haniotNetRepository.saveDevice(device)
                            .doAfterSuccess(device1 -> deviceDAO.markAsSync(device.getId()))
                            .subscribe((device1, throwable) -> {})
            );
    }

    private void sendOdontologicalQuestionnaires(List<OdontologicalQuestionnaire> list) {
        for (OdontologicalQuestionnaire o : list)
            if (o.getPatient_id() != null)
                mCompositeDisposable.add(
                        haniotNetRepository.saveOdontologicalQuestionnaire(o)
                                .doAfterSuccess(odontologicalQuestionnaire -> odontologicalQuestionnaireDAO.markAsSync(o.getId()))
                                .subscribe((odontologicalQuestionnaire, throwable) -> {})
                );
    }

    private void sendNutritionalQuestionnaire(List<NutritionalQuestionnaire> list) {
        for (NutritionalQuestionnaire n : list)
            if (n.getPatient_id() != null)
                mCompositeDisposable.add(
                        haniotNetRepository.saveNutritionalQuestionnaire(n)
                                .doAfterSuccess(nutritionalQuestionnaire -> nutritionalQuestionnaireDAO.markAsSync(n.getId()))
                                .subscribe((nutritionalQuestionnaire, throwable) -> {})
                );
    }

    private void sendMeasurements(List<Measurement> list) {
        for (Measurement m : list)
            if (m.getUser_id() != null)
                mCompositeDisposable.add(
                        haniotNetRepository.saveMeasurement(m)
                                .doAfterSuccess(measurement -> measurementDAO.markAsSync(m.getId()))
                                .subscribe((measurement, throwable) -> {})
                );
    }

    public synchronized void synchronize() {

        if (ConnectionUtils.internetIsEnabled(this.mContext)) {
            Log.i(TAG, "syncronize: COM INTERNET");
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
                                            .subscribe(getResponse -> {

                                                if (getResponse.measurements != null && !getResponse.measurements.isEmpty()) {
                                                    measurementDAO.removeSyncronized(patientServer);

                                                    for (Measurement m : getResponse.measurements) {
                                                        m.setUserId(patientId);
                                                        if (measurementDAO.save(m) > 0)
                                                            Log.i(TAG, "measurement salva de: " + m.toString());
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
                                            }, throwable -> {})
                            );
                        }
                        Log.i(TAG, "pacientes salvos localmente: " + patientDAO.getAllPatients(pilotStudyId, null, 1, 100).toString());
                    })
                    .subscribe((patients, throwable) -> {})
            );

            // recupera todos os dipositivos de todos os pacientes - verificar se é o paciente ou o profissional que tem
//            mComposite.add(
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
}
