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
import io.objectbox.Box;
import io.objectbox.BoxStore;

import static br.edu.uepb.nutes.haniot.data.model.type.OdontologicalQuestionnaireType.FAMILY_COHESION_RECORD;
import static br.edu.uepb.nutes.haniot.data.model.type.OdontologicalQuestionnaireType.ORAL_HEALTH_RECORD;
import static br.edu.uepb.nutes.haniot.data.model.type.OdontologicalQuestionnaireType.SOCIODEMOGRAPHIC_RECORD;

/**
 * Odontological Questionnaire DAO implementation
 */
public class OdontologicalQuestionnaireDAO {
    private static final String TAG = "ODONTOLOGICALDAO";

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

    /**
     * Get all questionnaires by patient
     * @param patient Patient
     * @param page Int
     * @param limit Int
     * @param sort String
     * @return List of Questionnaire
     */
    public List<OdontologicalQuestionnaire> getAll(Patient patient, int page, int limit, String sort) {
        Log.i(TAG, "getAll: ");
        List<OdontologicalQuestionnaireOB> list;

        if (patient.get_id() != null) {
            list = odontologicalQuestionnaireBox.query()
                    .equal(OdontologicalQuestionnaireOB_.patient_id, patient.get_id())
                    .orderDesc(OdontologicalQuestionnaireOB_.createdAt)
                    .build()
                    .find((page - 1) * limit, limit);
        } else {
            list = odontologicalQuestionnaireBox.query()
                    .equal(OdontologicalQuestionnaireOB_.patientId, patient.getId())
                    .orderDesc(OdontologicalQuestionnaireOB_.createdAt)
                    .build()
                    .find((page - 1) * limit, limit);
        }
        return Convert.listOdontologicalQuestionnaireToModel(list);
    }

    /**
     * Update Questionnaire
     * @param questionnaireId Long (only locale)
     * @param question String
     * @param newValue ActivityHabitsRecord
     */
    public void update(long questionnaireId, @NonNull String question, @NonNull ActivityHabitsRecord newValue) {
        Log.i(TAG, "update: ");
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

    /**
     * Get Questionnaire by id
     * @param id Long
     * @return Questionnaire or Null
     */
    private OdontologicalQuestionnaireOB get(long id) {
        return odontologicalQuestionnaireBox.query()
                .equal(OdontologicalQuestionnaireOB_.id, id)
                .build()
                .findFirst();
    }

    /**
     * Add Questionnaire to the DataBase
     * @param questionnaire Questionnaire
     * @return Long > 0 if success or 0 otherwise
     */
    public long save(@NonNull OdontologicalQuestionnaire questionnaire) {
        Log.i(TAG, "save: ");
        return odontologicalQuestionnaireBox.put(Convert.convertOdontologicalQuestionnaire(questionnaire));
    }

    public List<OdontologicalQuestionnaire> getAllNotSync() {
        Log.i(TAG, "getAllNotSync: ");
        List<OdontologicalQuestionnaireOB> aux = odontologicalQuestionnaireBox.query()
                .equal(OdontologicalQuestionnaireOB_.sync, false)
                .build()
                .find();
        return Convert.listOdontologicalQuestionnaireToModel(aux);
    }

    public List<OdontologicalQuestionnaire> getAllNotSync(long id) {
        Log.i(TAG, "getAllNotSync: ");
        List<OdontologicalQuestionnaireOB> aux = odontologicalQuestionnaireBox.query()
                .equal(OdontologicalQuestionnaireOB_.patientId, id)
                .equal(OdontologicalQuestionnaireOB_.sync, false)
                .build()
                .find();
        return Convert.listOdontologicalQuestionnaireToModel(aux);
    }

    /**
     * Remove questionnaire by id
     *
     * @param id Long
     */
    public void remove(long id) {
        Log.i(TAG, "remove: ");
        odontologicalQuestionnaireBox.query()
                .equal(OdontologicalQuestionnaireOB_.id, id)
                .build()
                .remove();
    }

    public void removeSyncronized(@NonNull String patient_id) {
        Log.i(TAG, "removeSyncronized: ");
        odontologicalQuestionnaireBox.query()
                .equal(OdontologicalQuestionnaireOB_.sync, true)
                .equal(OdontologicalQuestionnaireOB_.patient_id, patient_id)
                .build()
                .remove();
    }

    /**
     * Remove all questionnaires of a patient
     *
     * @param patientId Long
     */
    public void removeByPatientId(long patientId) {
        Log.i(TAG, "removeByPatientId: ");
        odontologicalQuestionnaireBox.query()
                .equal(OdontologicalQuestionnaireOB_.patientId, patientId)
                .build()
                .remove();
    }
}
