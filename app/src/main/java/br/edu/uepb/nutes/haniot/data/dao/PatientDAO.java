package br.edu.uepb.nutes.haniot.data.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.objectbox.PatientOB;
import br.edu.uepb.nutes.haniot.data.objectbox.PatientOB_;
import io.objectbox.Box;
import io.objectbox.BoxStore;
import io.reactivex.Single;

/**
 * Represents PatientDAO.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
public class PatientDAO {

    public static PatientDAO instance;
    private static Box<PatientOB> patientBox;

    private PatientDAO() {
    }

    public static synchronized PatientDAO getInstance(@Nullable Context context) {
        if (instance == null) instance = new PatientDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        patientBox = boxStore.boxFor(PatientOB.class);

        return instance;
    }

    public PatientOB get(long id) {
        return patientBox.query().equal(PatientOB_.id, id).build().findFirst();
    }

    public Patient get(@NonNull String _id) {
        PatientOB aux = patientBox.query()
                .equal(PatientOB_._id, _id)
                .build()
                .findFirst();
        return new Patient(aux);
    }

    public List<PatientOB> list(@NonNull String healthProfessionalId) {
        return patientBox.query().equal(PatientOB_.healthProfessionalId, healthProfessionalId).build().find();
    }

    public boolean save(@NonNull Patient patient) {
        return patientBox.put(new PatientOB(patient)) > 0;
    }

    public boolean update(@NonNull Patient patient) {
        if (patient.getId() == 0) {
            Patient patientOBUp = get(patient.getName());

            if (patientOBUp == null) return false;

            patient.setId(patientOBUp.getId());
            if (patient.get_id() == null) patient.set_id(patientOBUp.get_id());
        }
        return save(patient);
    }

    public boolean remove(@NonNull String _id) {
        return patientBox.query().equal(PatientOB_._id, _id).build().remove() > 0;
    }

    public List<Patient> getAllPatients(String pilotStudyId, String sort, int page, int limit) {
        List<PatientOB> aux = patientBox.query()
                                .equal(PatientOB_.pilotId, pilotStudyId)
                                .build()
                                .find();
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

    public void addAll(List<Patient> patients) {
        for (Patient patient : patients) {
            patient.setSync(true);
            patientBox.put(new PatientOB(patient));
        }
    }

    public void removeSyncronized() {
        patientBox.query()
                .equal(PatientOB_.sync, true)
                .build()
                .remove();
    }
}
