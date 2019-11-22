package br.edu.uepb.nutes.haniot.data.dao;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.Patient;
import br.edu.uepb.nutes.haniot.data.model.nutritional.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.nutritional.MedicalRecord;
import br.edu.uepb.nutes.haniot.data.model.nutritional.NutritionalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.model.nutritional.PhysicalActivityHabit;
import br.edu.uepb.nutes.haniot.data.model.nutritional.SleepHabit;
import br.edu.uepb.nutes.haniot.data.objectbox.nutritional.NutritionalQuestionnaireOB;
import br.edu.uepb.nutes.haniot.data.objectbox.nutritional.NutritionalQuestionnaireOB_;
import br.edu.uepb.nutes.haniot.data.repository.Repository;
import io.objectbox.Box;
import io.objectbox.BoxStore;

import static br.edu.uepb.nutes.haniot.data.model.type.NutritionalQuestionnaireType.FEEDING_HABITS_RECORD;
import static br.edu.uepb.nutes.haniot.data.model.type.NutritionalQuestionnaireType.MEDICAL_RECORDS;
import static br.edu.uepb.nutes.haniot.data.model.type.NutritionalQuestionnaireType.PHYSICAL_ACTIVITY_HABITS;
import static br.edu.uepb.nutes.haniot.data.model.type.NutritionalQuestionnaireType.SLEEP_HABIT;

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

    public void update(long questionnaireId, String question, Object newValue) {
        NutritionalQuestionnaireOB q = get(questionnaireId);
        Log.i(Repository.TAG, "update: questionnaire: " + q);
        Log.i(Repository.TAG, "update: question: "+ question);
        Log.i(Repository.TAG, "update: newValue: "+ newValue);

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

    public NutritionalQuestionnaireOB get(long questionnaireId) {
        return nutritionalQuestionnaireBox.query()
                .equal(NutritionalQuestionnaireOB_.id, questionnaireId)
                .build()
                .findFirst();
    }

    public long save(NutritionalQuestionnaire nutritionalQuestionnaire) {
        return nutritionalQuestionnaireBox.put(Convert.nutritionalQuestionnaire(nutritionalQuestionnaire));
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
