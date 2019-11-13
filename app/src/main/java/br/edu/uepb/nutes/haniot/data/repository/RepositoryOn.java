package br.edu.uepb.nutes.haniot.data.repository;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;

import java.util.List;

import br.edu.uepb.nutes.haniot.data.model.Admin;
import br.edu.uepb.nutes.haniot.data.model.HealthProfessional;
import br.edu.uepb.nutes.haniot.data.model.MeasurementLastResponse;
import br.edu.uepb.nutes.haniot.data.model.PilotStudy;
import br.edu.uepb.nutes.haniot.data.model.User;
import br.edu.uepb.nutes.haniot.data.model.UserAccess;
import br.edu.uepb.nutes.haniot.data.model.nutritional.NutritionalEvaluation;
import br.edu.uepb.nutes.haniot.data.model.nutritional.NutritionalEvaluationResult;
import br.edu.uepb.nutes.haniot.data.model.nutritional.NutritionalQuestionnaire;
import br.edu.uepb.nutes.haniot.data.repository.remote.haniot.HaniotNetRepository;
import io.reactivex.Completable;
import io.reactivex.Single;

public abstract class RepositoryOn {
    public static String TAG = "REPOSITORY";

    protected HaniotNetRepository haniotNetRepository;
    protected Context mContext;

    public RepositoryOn(Context context) {
        this.mContext = context;
        this.haniotNetRepository = HaniotNetRepository.getInstance(context);
    }

    // ----------------- PILOT STUDY DAO -------------------------

    public Single<List<PilotStudy>> getAllPilotStudies() {
        Log.i(TAG, "getAllPilotStudies: ");
        return haniotNetRepository.getAllPilotStudies();
    }

    public Single<List<PilotStudy>> getAllUserPilotStudies(String userId) {
        Log.i(TAG, "getAllUserPilotStudies: ");
        return haniotNetRepository.getAllUserPilotStudies(userId);
    }

    // ---------- USER DAO ---------------

    public Completable deleteUserById(String userId) {
        Log.i(TAG, "deleteUserById: ");
        return haniotNetRepository.deleteUserById(userId);
    }

    // ------------ admin and password ------------

    public Single<Admin> updateAdmin(Admin admin) {
        Log.i(TAG, "updateAdmin: ");
        return haniotNetRepository.updateAdmin(admin);
    }

    public Single<Admin> getAdmin(String _id) {
        Log.i(TAG, "getAdmin: ");
        return haniotNetRepository.getAdmin(_id);
    }

    public Single<UserAccess> auth(String username, String password) {
        Log.i(TAG, "auth: ");
        return haniotNetRepository.auth(username, password);
    }

    public Completable changePassword(User user) {
        Log.i(TAG, "changePassword: ");
        return haniotNetRepository.changePassword(user);
    }

    public Single<Object> forgotPassword(JsonObject email) {
        Log.i(TAG, "forgotPassword: ");
        return haniotNetRepository.forgotPassword(email);
    }

    // --------------- Health Professional ---------------------------------

    public Single<HealthProfessional> updateHealthProfissional(HealthProfessional healthProfessional) {
        Log.i(TAG, "updateHealthProfissional: ");
        return haniotNetRepository.updateHealthProfissional(healthProfessional);
    }

    public Single<HealthProfessional> getHealthProfissional(String healthProfessionalId) {
        Log.i(TAG, "getHealthProfissional: ");
        return haniotNetRepository.getHealthProfissional(healthProfessionalId);
    }

    /**
     * Utilizado para avaliação nutricional
     *
     * @param patientId String
     * @return NutritionalQuestionnaire
     */
    public Single<NutritionalQuestionnaire> getLastNutritionalQuestionnaire(String patientId) {
        Log.i(TAG, "getLastNutritionalQuestionnaire: ");
        return haniotNetRepository.getLastNutritionalQuestionnaire(patientId);
    }

    /**
     * Select Last measurements of a patient
     * Necessary connection
     *
     * @param patientId String
     * @return MeasurementLasResponse
     */
    public Single<MeasurementLastResponse> getLastMeasurements(String patientId) {
        Log.i(TAG, "getLastMeasurements: ");
        return haniotNetRepository.getLastMeasurements(patientId);
    }

    public Single<NutritionalEvaluationResult> saveNutritionalEvaluation(NutritionalEvaluation nutritionalEvaluation) {
        Log.i(TAG, "saveNutritionalEvaluation: ");
        return haniotNetRepository.saveNutritionalEvaluation(nutritionalEvaluation);
    }
}
