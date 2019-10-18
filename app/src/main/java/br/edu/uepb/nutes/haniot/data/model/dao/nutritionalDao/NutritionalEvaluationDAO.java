package br.edu.uepb.nutes.haniot.data.model.dao.nutritionalDao;

import android.content.Context;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.model.objectbox.nutritional.NutritionalQuestionnaireOB;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class NutritionalEvaluationDAO {

    private static NutritionalEvaluationDAO instance;
    private static Box<NutritionalQuestionnaireOB> nutritionalEvaluationBox;

    private NutritionalEvaluationDAO(Context context) {
        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        nutritionalEvaluationBox = boxStore.boxFor(NutritionalQuestionnaireOB.class);
    }

    public NutritionalEvaluationDAO getInstance(Context context) {
        if (instance == null)
            instance = new NutritionalEvaluationDAO(context);
        return instance;
    }



}
