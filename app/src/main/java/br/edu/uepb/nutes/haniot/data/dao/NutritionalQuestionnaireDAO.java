package br.edu.uepb.nutes.haniot.data.dao;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.ActivityHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.nutritional.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.nutritional.MedicalRecord;
import br.edu.uepb.nutes.haniot.data.model.nutritional.NutritionalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.model.nutritional.PhysicalActivityHabit;
import br.edu.uepb.nutes.haniot.data.model.nutritional.SleepHabit;
import br.edu.uepb.nutes.haniot.data.objectbox.nutritional.NutritionalQuestionnaireOB;
import br.edu.uepb.nutes.haniot.data.objectbox.nutritional.NutritionalQuestionnaireOB_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

import static br.edu.uepb.nutes.haniot.data.model.type.NutritionalQuestionnaireType.FEEDING_HABITS_RECORD;
import static br.edu.uepb.nutes.haniot.data.model.type.NutritionalQuestionnaireType.MEDICAL_RECORDS;
import static br.edu.uepb.nutes.haniot.data.model.type.NutritionalQuestionnaireType.PHYSICAL_ACTIVITY_HABITS;
import static br.edu.uepb.nutes.haniot.data.model.type.NutritionalQuestionnaireType.SLEEP_HABIT;

/**
 * Nutritional Questionnaire DAO implementation
 */
public class NutritionalQuestionnaireDAO {
    private static final String TAG = "NUTRITIONALDAO";

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

    /**
     * Get all questionnaires by patient
     *
     * @param patient Patient
     * @param page    Int
     * @param limit   Int
     * @param sort    String
     * @return List of Questionnaire
     */
    public List<NutritionalQuestionnaire> getAll(@NonNull Patient patient, int page, int limit, String sort) {
        Log.i(TAG, "getAll: ");
        List<NutritionalQuestionnaireOB> aux;

        if (patient.get_id() != null) {
            aux = nutritionalQuestionnaireBox.query()
                    .equal(NutritionalQuestionnaireOB_.patient_id, patient.get_id())
                    .orderDesc(NutritionalQuestionnaireOB_.createdAt)
                    .build()
                    .find((page - 1) * limit, limit);
        } else {
            aux = nutritionalQuestionnaireBox.query()
                    .equal(NutritionalQuestionnaireOB_.patientId, patient.getId())
                    .orderDesc(NutritionalQuestionnaireOB_.createdAt)
                    .build()
                    .find((page - 1) * limit, limit);
        }
        return Convert.listNutritionalQuestionnaireToModel(aux);
    }

    /**
     * Update Questionnaire
     *
     * @param questionnaireId Long (only locale)
     * @param question        String
     * @param newValue        ActivityHabitsRecord
     */
    public void update(long questionnaireId, @NonNull String question, @NonNull ActivityHabitsRecord newValue) {
        Log.i(TAG, "update: ");
        NutritionalQuestionnaireOB q = get(questionnaireId);
        if (q == null) return;

        switch (question) {
            case SLEEP_HABIT:
                if (newValue instanceof SleepHabit)
                    q.setSleepHabit(Convert.sleepHabit((SleepHabit) newValue));
                break;
            case FEEDING_HABITS_RECORD:
                if (newValue instanceof FeedingHabitsRecord)
                    q.setFeedingHabitsRecord(Convert.feedingHabitsRecord((FeedingHabitsRecord) newValue));
                break;
            case MEDICAL_RECORDS:
                if (newValue instanceof MedicalRecord)
                    q.setMedicalRecord(Convert.medicalRecord((MedicalRecord) newValue));
                break;
            case PHYSICAL_ACTIVITY_HABITS:
                if (newValue instanceof PhysicalActivityHabit)
                    q.setPhysicalActivityHabit(Convert.physicalActivityHabit((PhysicalActivityHabit) newValue));
                break;
        }
        nutritionalQuestionnaireBox.put(q);
    }

    /**
     * Get Questionnaire by id
     *
     * @param id Long
     * @return Questionnaire or Null
     */
    public NutritionalQuestionnaireOB get(long id) {
        return nutritionalQuestionnaireBox.query()
                .equal(NutritionalQuestionnaireOB_.id, id)
                .build()
                .findFirst();
    }

    /**
     * Add Questionnaire to the DataBase
     *
     * @param questionnaire Questionnaire
     * @return Long > 0 if success or 0 otherwise
     */
    public long save(@NonNull NutritionalQuestionnaire questionnaire) {
        Log.i(TAG, "save: ");
        return nutritionalQuestionnaireBox.put(Convert.nutritionalQuestionnaire(questionnaire));
    }

    public List<NutritionalQuestionnaire> getAllNotSync() {
        Log.i(TAG, "getAllNotSync: ");
        List<NutritionalQuestionnaireOB> aux = nutritionalQuestionnaireBox.query()
                .equal(NutritionalQuestionnaireOB_.sync, false)
                .build()
                .find();
        return Convert.listNutritionalQuestionnaireToModel(aux);
    }

    public List<NutritionalQuestionnaire> getAllNotSync(long id) {
        Log.i(TAG, "getAllNotSync: ");
        List<NutritionalQuestionnaireOB> aux = nutritionalQuestionnaireBox.query()
                .equal(NutritionalQuestionnaireOB_.patientId, id)
                .equal(NutritionalQuestionnaireOB_.sync, false)
                .build()
                .find();
        return Convert.listNutritionalQuestionnaireToModel(aux);
    }

    /**
     * Remove questionnaire by id
     *
     * @param id Long
     */
    public void remove(long id) {
        Log.i(TAG, "remove: ");
        nutritionalQuestionnaireBox.query()
                .equal(NutritionalQuestionnaireOB_.id, id)
                .build()
                .remove();
    }

    public void removeSyncronized(@NonNull String patient_id) {
        Log.i(TAG, "removeSyncronized: ");
        nutritionalQuestionnaireBox.query()
                .equal(NutritionalQuestionnaireOB_.sync, true)
                .equal(NutritionalQuestionnaireOB_.patient_id, patient_id)
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
        nutritionalQuestionnaireBox.query()
                .equal(NutritionalQuestionnaireOB_.patientId, patientId)
                .build()
                .remove();
    }
}
