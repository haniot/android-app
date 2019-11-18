package br.edu.uepb.nutes.haniot.data.dao;

import android.content.Context;
import android.util.Log;

import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.odontological.OdontologicalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.objectbox.odontological.OdontologicalQuestionnaireOB;
import br.edu.uepb.nutes.haniot.data.objectbox.odontological.OdontologicalQuestionnaireOB_;
import br.edu.uepb.nutes.haniot.data.repository.Repository;
import io.objectbox.Box;
import io.objectbox.BoxStore;

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

    public List<OdontologicalQuestionnaire> getAll(String patientId, int page, int limit, String sort) {
        page--;
        List<OdontologicalQuestionnaireOB> aux = odontologicalQuestionnaireBox.query()
                .equal(OdontologicalQuestionnaireOB_.patientId, patientId)
                .orderDesc(OdontologicalQuestionnaireOB_.createdAt)
                .build()
                .find(page * limit, limit);
        return Convert.listOdontologicalQuestionnaireToModel(aux);
    }

    public void update(String patientId, String questionnaireId, String resourceName, Object object) {
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

    public void removeSyncronized() {
        Log.i(Repository.TAG, "Removendo OdontologicalQuestionnaire sincronizados");
        odontologicalQuestionnaireBox.query()
                .equal(OdontologicalQuestionnaireOB_.sync, true)
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
