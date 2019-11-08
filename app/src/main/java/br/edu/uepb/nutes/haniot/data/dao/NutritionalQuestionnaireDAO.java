package br.edu.uepb.nutes.haniot.data.dao;

import android.content.Context;

import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.nutritional.NutritionalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.objectbox.nutritional.NutritionalQuestionnaireOB;
import br.edu.uepb.nutes.haniot.data.objectbox.nutritional.NutritionalQuestionnaireOB_;
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

    public List<NutritionalQuestionnaire> getAll(String patientId, int page, int limit, String sort) {
        page--;
        List<NutritionalQuestionnaireOB> aux = nutritionalQuestionnaireBox.query()
                .equal(NutritionalQuestionnaireOB_.patientId, patientId)
                .orderDesc(NutritionalQuestionnaireOB_.createdAt)
                .build()
                .find(page * limit, limit);
        return Convert.listNutritionalQuestionnaireToModel(aux);
    }

    public void update(String patientId, String questionnaireId, String resourceName, Object object) {
    }

    public long save(NutritionalQuestionnaire nutritionalQuestionnaire) {
        return nutritionalQuestionnaireBox.put(new NutritionalQuestionnaireOB(nutritionalQuestionnaire));
    }

    public List<NutritionalQuestionnaire> getAllNotSync() {
        List<NutritionalQuestionnaireOB> aux = nutritionalQuestionnaireBox.query()
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

    public void removeSyncronized() {
        nutritionalQuestionnaireBox.query()
                .equal(NutritionalQuestionnaireOB_.sync, true)
                .build()
                .remove();
    }
}