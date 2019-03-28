package br.edu.uepb.nutes.haniot.data.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import javax.annotation.Nullable;
import javax.sql.StatementEvent;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.Patient_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Represents PatientDAO.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
public class PatientDAO {

    public static PatientDAO instance;
    private static Box<Patient> patientBox;

    private PatientDAO() {
    }

    public static synchronized PatientDAO getInstance(@Nullable Context context) {
        if (instance == null) instance = new PatientDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        patientBox = boxStore.boxFor(Patient.class);

        return instance;
    }

    public Patient get(long id) {
        return patientBox.query().equal(Patient_.id, id).build().findFirst();
    }

    public Patient get(@NonNull String _id) {
        return patientBox.query().equal(Patient_._id, _id).build().findFirst();
    }

    public List<Patient> list(@NonNull String userId) {
        return patientBox.query().equal(Patient_.userId, userId).build().find();
    }

    public boolean save(@NonNull Patient patient) {
        return patientBox.put(patient) > 0;
    }

    public boolean update(@NonNull Patient patient) {
        if (patient.getId() == 0) {
            Patient patientUp = get(patient.getFirstName());

            if (patientUp == null) return false;

            patient.setId(patientUp.getId());
            if (patient.get_id() == null) patient.set_id(patientUp.get_id());
        }
        return save(patient);
    }

    public boolean remove(@NonNull Patient patient) {
        return patientBox.query().equal(Patient_.id, patient.getId()).build().remove() > 0;
    }

    public boolean remove(@NonNull String _id) {
        return patientBox.query().equal(Patient_._id, _id).build().remove() > 0;
    }
}
