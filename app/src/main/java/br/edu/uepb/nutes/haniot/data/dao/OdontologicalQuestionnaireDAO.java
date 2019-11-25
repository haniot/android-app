package br.edu.uepb.nutes.haniot.data.dao;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.ActivityHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.odontological.FamilyCohesionRecord;
import br.edu.uepb.nutes.haniot.data.model.odontological.OdontologicalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.model.odontological.OralHealthRecord;
import br.edu.uepb.nutes.haniot.data.model.odontological.SociodemographicRecord;
import br.edu.uepb.nutes.haniot.data.objectbox.odontological.OdontologicalQuestionnaireOB;
import br.edu.uepb.nutes.haniot.data.objectbox.odontological.OdontologicalQuestionnaireOB_;
import br.edu.uepb.nutes.haniot.data.repository.Repository;
import io.objectbox.Box;
import io.objectbox.BoxStore;

import static br.edu.uepb.nutes.haniot.data.model.type.OdontologicalQuestionnaireType.FAMILY_COHESION_RECORD;
import static br.edu.uepb.nutes.haniot.data.model.type.OdontologicalQuestionnaireType.ORAL_HEALTH_RECORD;
import static br.edu.uepb.nutes.haniot.data.model.type.OdontologicalQuestionnaireType.SOCIODEMOGRAPHIC_RECORD;

public class OdontologicalQuestionnaireDAO {

    private static OdontologicalQuestionnaireDAO instance;
    private static Box<OdontologicalQuestionnaireOB> odontologicalQuestionnaireBox;

    private OdontologicalQuestionnaireDAO(Context context) {
        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        odontologicalQuestionnaireBox = boxStore.boxFor(OdontologicalQuestionnaireOB.class);
    }

    public static OdontologicalQuestionnaireDAO getInstance(Context context) {
        if (instance == null)
            instance = new OdontologicalQuestionnaireDAO(context);
        return instance;
    }

    public List<OdontologicalQuestionnaire> getAll(Patient patient, int page, int limit, String sort) {
        page--;
        List<OdontologicalQuestionnaireOB> list;

        if (patient.get_id() != null) {
            list = odontologicalQuestionnaireBox.query()
                    .equal(OdontologicalQuestionnaireOB_.patient_id, patient.get_id())
                    .orderDesc(OdontologicalQuestionnaireOB_.createdAt)
                    .build()
                    .find(page * limit, limit);
        } else {
            list = odontologicalQuestionnaireBox.query()
                    .equal(OdontologicalQuestionnaireOB_.patientId, patient.getId())
                    .orderDesc(OdontologicalQuestionnaireOB_.createdAt)
                    .build()
                    .find(page * limit, limit);
        }
        return Convert.listOdontologicalQuestionnaireToModel(list);
    }

    public void update(long questionnaireId, String question, ActivityHabitsRecord newValue) {
        OdontologicalQuestionnaireOB o = get(questionnaireId);
        if (o == null) return;

        switch (question) {
            case SOCIODEMOGRAPHIC_RECORD:
                o.setSociodemographicRecord(Convert.sociodemographicRecord((SociodemographicRecord) newValue));
                break;
            case FAMILY_COHESION_RECORD:
                o.setFamilyCohesionRecord(Convert.familyCohesionRecord((FamilyCohesionRecord) newValue));
                break;
            case ORAL_HEALTH_RECORD:
                o.setOralHealthRecord(Convert.oralHealthRecord((OralHealthRecord) newValue));
                break;
        }
        odontologicalQuestionnaireBox.put(o);
    }

    private OdontologicalQuestionnaireOB get(long id) {
        return odontologicalQuestionnaireBox.query()
                .equal(OdontologicalQuestionnaireOB_.id, id)
                .build()
                .findFirst();
    }

    public long save(OdontologicalQuestionnaire odontologicalQuestionnaire) {
        return odontologicalQuestionnaireBox.put(Convert.convertOdontologicalQuestionnaire(odontologicalQuestionnaire));
    }

    public List<OdontologicalQuestionnaire> getAllNotSync() {
        List<OdontologicalQuestionnaireOB> aux = odontologicalQuestionnaireBox.query()
                .equal(OdontologicalQuestionnaireOB_.sync, false)
                .build()
                .find();
        return Convert.listOdontologicalQuestionnaireToModel(aux);
    }

    public List<OdontologicalQuestionnaire> getAllNotSync(long id) {
        List<OdontologicalQuestionnaireOB> aux = odontologicalQuestionnaireBox.query()
                .equal(OdontologicalQuestionnaireOB_.patientId, id)
                .equal(OdontologicalQuestionnaireOB_.sync, false)
                .build()
                .find();
        return Convert.listOdontologicalQuestionnaireToModel(aux);
    }

    public void markAsSync(long id) {
        odontologicalQuestionnaireBox.query()
                .equal(OdontologicalQuestionnaireOB_.id, id)
                .build()
                .remove();
    }

    public void removeSyncronized(@NonNull Patient patient) {
        Log.i(Repository.TAG, "Removendo OdontologicalQuestionnaire sincronizados: " + patient.get_id());
        if (patient.get_id() == null) return;
        odontologicalQuestionnaireBox.query()
                .equal(OdontologicalQuestionnaireOB_.sync, true)
                .equal(OdontologicalQuestionnaireOB_.patient_id, patient.get_id())
                .build()
                .remove();
    }

    public boolean removeByPatientId(long patientId) {
        return odontologicalQuestionnaireBox.query()
                .equal(OdontologicalQuestionnaireOB_.patientId, patientId)
                .build()
                .remove() > 0;
    }
}
