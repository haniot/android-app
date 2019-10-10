package br.edu.uepb.nutes.haniot.data.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import javax.annotation.Nullable;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.model.objectbox.PatientOB;
import br.edu.uepb.nutes.haniot.data.model.objectbox.PatientOB_;
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

    public PatientOB get(@NonNull String _id) {
        return patientBox.query().equal(PatientOB_._id, _id).build().findFirst();
    }

    public List<PatientOB> list(@NonNull String healthProfessionalId) {
        return patientBox.query().equal(PatientOB_.healthProfessionalId, healthProfessionalId).build().find();
    }

    public boolean save(@NonNull PatientOB patientOB) {
        return patientBox.put(patientOB) > 0;
    }

    public boolean update(@NonNull PatientOB patientOB) {
        if (patientOB.getId() == 0) {
            PatientOB patientOBUp = get(patientOB.getName());

            if (patientOBUp == null) return false;

            patientOB.setId(patientOBUp.getId());
            if (patientOB.get_id() == null) patientOB.set_id(patientOBUp.get_id());
        }
        return save(patientOB);
    }

    public boolean remove(@NonNull PatientOB patientOB) {
        return patientBox.query().equal(PatientOB_.id, patientOB.getId()).build().remove() > 0;
    }

    public boolean remove(@NonNull String _id) {
        return patientBox.query().equal(PatientOB_._id, _id).build().remove() > 0;
    }
}
