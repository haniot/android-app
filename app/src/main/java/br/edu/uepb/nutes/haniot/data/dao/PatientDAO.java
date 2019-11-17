package br.edu.uepb.nutes.haniot.data.dao;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import javax.annotation.Nullable;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.objectbox.PatientOB;
import br.edu.uepb.nutes.haniot.data.objectbox.PatientOB_;
import br.edu.uepb.nutes.haniot.data.repository.Repository;
import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Represents PatientDAO.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
public class PatientDAO {

    public static PatientDAO instance;
    private static Box<PatientOB> patientBox;

    private PatientDAO(Context context) {
        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        patientBox = boxStore.boxFor(PatientOB.class);
    }

    public static synchronized PatientDAO getInstance(@Nullable Context context) {
        if (instance == null)
            instance = new PatientDAO(context);
        return instance;
    }

    public Patient get(@NonNull Patient patient) {
        PatientOB aux;
        if (patient.get_id() != null) {
            aux = patientBox.query()
                    .equal(PatientOB_._id, patient.get_id())
                    .build()
                    .findFirst();
        } else {
            aux = patientBox.query()
                    .equal(PatientOB_.id, patient.getId())
                    .build()
                    .findFirst();
        }
        return Convert.convertPatient(aux);
    }

    public Patient getBy_id(@NonNull String _id) {
        PatientOB aux = patientBox.query()
                .equal(PatientOB_._id, _id)
                .build()
                .findFirst();
        return Convert.convertPatient(aux);
    }

    public long save(@NonNull Patient patient) {
        return patientBox.put(Convert.convertPatient(patient));
    }

    public long update(@NonNull Patient patient) {
        if (patient.getId() == 0)
            return 0;
        return save(patient);
    }

    public boolean remove(@NonNull long id) {
        return patientBox.query()
                .equal(PatientOB_.id, id)
                .build().remove() > 0;
    }

    public List<Patient> getAllPatients(String pilotStudyId, String sort, int page, int limit) {
        page--;
        List<PatientOB> aux = patientBox.query()
                .equal(PatientOB_.pilotId, pilotStudyId)
                .orderDesc(PatientOB_.createdAt)
                .build()
                .find(page * limit, limit);
        return Convert.listPatientsToModel(aux);
    }

    public List<Patient> getAllNotSync() {
        List<PatientOB> aux = patientBox.query()
                .equal(PatientOB_.sync, false)
                .build()
                .find();
        return Convert.listPatientsToModel(aux);
    }

    public void markAsSync(long id) {
        patientBox.query()
                .equal(PatientOB_.id, id)
                .build()
                .remove();
    }

    public void removeSyncronized() {
        Log.i(Repository.TAG, "Removendo Patients sincronizados");
        patientBox.query()
                .equal(PatientOB_.sync, true)
                .build()
                .remove();
    }
}
