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
import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Represents PatientDAO.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
public class PatientDAO {
    private static final String TAG = "PATIENTDAO";
    private static PatientDAO instance;
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

    /**
     * Get Patient by id or _id
     *
     * @param patient Patient
     * @return Patient or null
     */
    public Patient get(@NonNull Patient patient) {
        Log.i(TAG, "get: ");
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
        return Convert.patient(aux);
    }

    /**
     * Add patient in the database
     *
     * @param patient Patient
     * @return Long > 0 if success or 0 otherwise
     */
    public long save(@NonNull Patient patient) {
        Log.i(TAG, "save: ");
        if (patient.getEmail() != null && contains(patient))
            return 0;
        return patientBox.put(Convert.patient(patient));
    }

    private boolean contains(Patient patient) {
        return (patientBox.query()
                .equal(PatientOB_.email, patient.getEmail())
                .build()
                .findFirst()) != null;
    }

    /**
     * Update data of patient
     *
     * @param patient Patient
     * @return Long > 0 if success or 0 otherwise
     */
    public long update(@NonNull Patient patient) {
        Log.i(TAG, "update: ");
        if (patient.getId() == 0)
            return 0;
        return save(patient);
    }

    /**
     * Remove patient from database
     *
     * @param id Long
     * @return True if success or False otherwise
     */
    public boolean remove(long id) {
        Log.i(TAG, "remove: ");
        return patientBox.query()
                .equal(PatientOB_.id, id)
                .build().remove() > 0;
    }

    /**
     * Get all patients by pilot study
     *
     * @param pilotStudyId String
     * @param sort         String
     * @param page         Int
     * @param limit        Int
     * @return List of Patient
     */
    public List<Patient> getAllPatients(String pilotStudyId, String sort, int page, int limit) {
        Log.i(TAG, "getAllPatients: ");
        List<PatientOB> aux = patientBox.query()
                .equal(PatientOB_.pilotId, pilotStudyId)
                .orderDesc(PatientOB_.createdAt)
                .build()
                .find((page - 1) * limit, limit);
        return Convert.listPatientsToModel(aux);
    }

    public List<Patient> getAllNotSync() {
        Log.i(TAG, "getAllNotSync: ");
        List<PatientOB> aux = patientBox.query()
                .equal(PatientOB_.sync, false)
                .build()
                .find();
        return Convert.listPatientsToModel(aux);
    }

    public boolean isSync() {
        return patientBox.query().equal(PatientOB_.sync, false).build().find().isEmpty();
    }

    public void removeSyncronized() {
        Log.i(TAG, "removeSyncronized: ");
        patientBox.query()
                .equal(PatientOB_.sync, true)
                .build()
                .remove();
    }
}
