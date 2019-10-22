package br.edu.uepb.nutes.haniot.data.dao.nutritionalDao;

import android.content.Context;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.objectbox.nutritional.NutritionalQuestionnaireOB;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class NutritionalQuestionnaireDAO {

    private static NutritionalQuestionnaireDAO instance;
    private static Box<NutritionalQuestionnaireOB> nutritionalQuestionnaireBox;

    private NutritionalQuestionnaireDAO(Context context) {
        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        nutritionalQuestionnaireBox = boxStore.boxFor(NutritionalQuestionnaireOB.class);
    }

    public NutritionalQuestionnaireDAO getInstance(Context context) {
        if (instance == null)
            instance = new NutritionalQuestionnaireDAO(context);
        return instance;
    }



}
