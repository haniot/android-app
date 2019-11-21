package br.edu.uepb.nutes.haniot.data.dao;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.nutritional.NutritionalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.objectbox.nutritional.NutritionalQuestionnaireOB;
import br.edu.uepb.nutes.haniot.data.objectbox.nutritional.NutritionalQuestionnaireOB_;
import br.edu.uepb.nutes.haniot.data.repository.Repository;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class NutritionalQuestionnaireDAO {

    private static NutritionalQuestionnaireDAO instance;
    private static Box<NutritionalQuestionnaireOB> nutritionalQuestionnaireBox;

    private NutritionalQuestionnaireDAO(Context context) {
        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        nutritionalQuestionnaireBox = boxStore.boxFor(NutritionalQuestionnaireOB.class);
    }

    public static NutritionalQuestionnaireDAO getInstance(Context context) {
        if (instance == null)
            instance = new NutritionalQuestionnaireDAO(context);
        return instance;
    }

    public List<NutritionalQuestionnaire> getAll(Patient patient, int page, int limit, String sort) {
        page--;
        List<NutritionalQuestionnaireOB> aux;

        if (patient.get_id() != null) {
            aux = nutritionalQuestionnaireBox.query()
                    .equal(NutritionalQuestionnaireOB_.patient_id, patient.get_id())
                    .orderDesc(NutritionalQuestionnaireOB_.createdAt)
                    .build()
                    .find(page * limit, limit);
        } else {
            aux = nutritionalQuestionnaireBox.query()
                    .equal(NutritionalQuestionnaireOB_.patientId, patient.getId())
                    .orderDesc(NutritionalQuestionnaireOB_.createdAt)
                    .build()
                    .find(page * limit, limit);
        }
        return Convert.listNutritionalQuestionnaireToModel(aux);
    }

    public void update(long patientId, long questionnaireId, String question, Object newValue) {
        NutritionalQuestionnaireOB q = get(patientId, questionnaireId);
        Log.i(Repository.TAG, "update: questionnaire: " + q);
        Log.i(Repository.TAG, "update: question: "+ question);
        Log.i(Repository.TAG, "update: newValue: "+ newValue);
    }

    public NutritionalQuestionnaireOB get(long patient_id, long questionnaire_id) {
        return nutritionalQuestionnaireBox.query()
                .equal(NutritionalQuestionnaireOB_.id, questionnaire_id)
                .equal(NutritionalQuestionnaireOB_.patientId, patient_id)
                .build()
                .findFirst();
    }

    public long save(NutritionalQuestionnaire nutritionalQuestionnaire) {
        return nutritionalQuestionnaireBox.put(Convert.convertNutritionalQuestionnaire(nutritionalQuestionnaire));
    }

    public List<NutritionalQuestionnaire> getAllNotSync() {
        List<NutritionalQuestionnaireOB> aux = nutritionalQuestionnaireBox.query()
                .equal(NutritionalQuestionnaireOB_.sync, false)
                .build()
                .find();
        return Convert.listNutritionalQuestionnaireToModel(aux);
    }

    public List<NutritionalQuestionnaire> getAllNotSync(long id) {
        List<NutritionalQuestionnaireOB> aux = nutritionalQuestionnaireBox.query()
                .equal(NutritionalQuestionnaireOB_.patientId, id)
                .equal(NutritionalQuestionnaireOB_.sync, false)
                .build()
                .find();
        return Convert.listNutritionalQuestionnaireToModel(aux);
    }

    public void markAsSync(long id) {
        nutritionalQuestionnaireBox.query()
                .equal(NutritionalQuestionnaireOB_.id, id)
                .build()
                .remove();
    }

    public void removeSyncronized(@NonNull Patient patient) {
        Log.i(Repository.TAG, "Removendo NutritionalQuestionnaire sincronizadas: " + patient.get_id());
        if (patient.get_id() == null) return;
        nutritionalQuestionnaireBox.query()
                .equal(NutritionalQuestionnaireOB_.sync, true)
                .equal(NutritionalQuestionnaireOB_.patient_id, patient.get_id())
                .build()
                .remove();
    }

    public boolean removeByPatienId(long patientId) {
        return nutritionalQuestionnaireBox.query()
                .equal(NutritionalQuestionnaireOB_.patientId, patientId)
                .build()
                .remove() > 0;
    }
}
