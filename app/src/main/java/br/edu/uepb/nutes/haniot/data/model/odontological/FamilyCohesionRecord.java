package br.edu.uepb.nutes.haniot.data.model.odontological;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import br.edu.uepb.nutes.haniot.data.objectbox.odontological.FamilyCohesionRecordOB;

public class FamilyCohesionRecord {

    @Expose(serialize = false, deserialize = false)
    private long id;

    @SerializedName("id")
    @Expose()
    private String _id;

//    @Expose(serialize = false, deserialize = false)
//    private String patientId;
//
//    @SerializedName("created_at")
//    @Expose(serialize = false)
//    private String createdAt;

    @SerializedName("family_mutual_aid_freq")
    @Expose()
    private String familyMutualAidFreq;

    @SerializedName("friendship_approval_freq")
    @Expose()
    private String friendshipApprovalFreq;

    @SerializedName("family_only_task_freq")
    @Expose()
    private String familyOnlyTaskFreq;

    @SerializedName("family_only_preference_freq")
    @Expose()
    private String familyOnlyPreferenceFreq;

    @SerializedName("free_time_together_freq")
    @Expose()
    private String freeTimeTogetherFreq;

    @SerializedName("family_proximity_perception_freq")
    @Expose()
    private String familyProximityPerceptionFreq;

    @SerializedName("all_family_tasks_freq")
    @Expose()
    private String allFamilyTasksFreq;

    @SerializedName("family_tasks_opportunity_freq")
    @Expose()
    private String familyTasksOpportunityFreq;

    @SerializedName("family_decision_support_freq")
    @Expose()
    private String familyDecisionSupportFreq;

    @SerializedName("family_union_relevance_freq")
    @Expose()
    private String familyUnionRelevanceFreq;

    @SerializedName("family_cohesion_result")
    @Expose()
    private int familyCohesionResult;

    public FamilyCohesionRecord() {
    }

    public FamilyCohesionRecord(FamilyCohesionRecordOB f) {
        this.setId(f.getId());
        this.set_Id(f.get_id());
//        this.setPatientId(f.getPatientId());
//        this.setCreatedAt(f.getCreatedAt());
        this.setFamilyMutualAidFreq(f.getFamilyMutualAidFreq());
        this.setFriendshipApprovalFreq(f.getFriendshipApprovalFreq());
        this.setFamilyOnlyTaskFreq(f.getFamilyOnlyTaskFreq());
        this.setFamilyOnlyPreferenceFreq(f.getFamilyOnlyPreferenceFreq());
        this.setFreeTimeTogetherFreq(f.getFreeTimeTogetherFreq());
        this.setFamilyProximityPerceptionFreq(f.getFamilyProximityPerceptionFreq());
        this.setAllFamilyTasksFreq(f.getAllFamilyTasksFreq());
        this.setFamilyTasksOpportunityFreq(f.getFamilyTasksOpportunityFreq());
        this.setFamilyDecisionSupportFreq(f.getFamilyDecisionSupportFreq());
        this.setFamilyUnionRelevanceFreq(f.getFamilyUnionRelevanceFreq());
        this.setFamilyCohesionResult(f.getFamilyCohesionResult());
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String get_id() {
        return _id;
    }

    public void set_Id(String _id) {
        this._id = _id;
    }

//    public String getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(String createdAt) {
//        this.createdAt = createdAt;
//    }

    public String getFamilyMutualAidFreq() {
        return familyMutualAidFreq;
    }

    public void setFamilyMutualAidFreq(String familyMutualAidFreq) {
        this.familyMutualAidFreq = familyMutualAidFreq;
    }

    public String getFriendshipApprovalFreq() {
        return friendshipApprovalFreq;
    }

    public void setFriendshipApprovalFreq(String friendshipApprovalFreq) {
        this.friendshipApprovalFreq = friendshipApprovalFreq;
    }

    public String getFamilyOnlyTaskFreq() {
        return familyOnlyTaskFreq;
    }

    public void setFamilyOnlyTaskFreq(String familyOnlyTaskFreq) {
        this.familyOnlyTaskFreq = familyOnlyTaskFreq;
    }

    public String getFamilyOnlyPreferenceFreq() {
        return familyOnlyPreferenceFreq;
    }

    public void setFamilyOnlyPreferenceFreq(String familyOnlyPreferenceFreq) {
        this.familyOnlyPreferenceFreq = familyOnlyPreferenceFreq;
    }

    public String getFreeTimeTogetherFreq() {
        return freeTimeTogetherFreq;
    }

    public void setFreeTimeTogetherFreq(String freeTimeTogetherFreq) {
        this.freeTimeTogetherFreq = freeTimeTogetherFreq;
    }

    public String getFamilyProximityPerceptionFreq() {
        return familyProximityPerceptionFreq;
    }

    public void setFamilyProximityPerceptionFreq(String familyProximityPerceptionFreq) {
        this.familyProximityPerceptionFreq = familyProximityPerceptionFreq;
    }

    public String getAllFamilyTasksFreq() {
        return allFamilyTasksFreq;
    }

    public void setAllFamilyTasksFreq(String allFamilyTasksFreq) {
        this.allFamilyTasksFreq = allFamilyTasksFreq;
    }

    public String getFamilyTasksOpportunityFreq() {
        return familyTasksOpportunityFreq;
    }

    public void setFamilyTasksOpportunityFreq(String familyTasksOpportunityFreq) {
        this.familyTasksOpportunityFreq = familyTasksOpportunityFreq;
    }

    public String getFamilyDecisionSupportFreq() {
        return familyDecisionSupportFreq;
    }

    public void setFamilyDecisionSupportFreq(String familyDecisionSupportFreq) {
        this.familyDecisionSupportFreq = familyDecisionSupportFreq;
    }

    public String getFamilyUnionRelevanceFreq() {
        return familyUnionRelevanceFreq;
    }

    public void setFamilyUnionRelevanceFreq(String familyUnionRelevanceFreq) {
        this.familyUnionRelevanceFreq = familyUnionRelevanceFreq;
    }

    public int getFamilyCohesionResult() {
        return familyCohesionResult;
    }

    public void setFamilyCohesionResult(int familyCohesionResult) {
        this.familyCohesionResult = familyCohesionResult;
    }

//    public String getPatientId() {
//        return patientId;
//    }
//
//    public void setPatientId(String patientId) {
//        this.patientId = patientId;
//    }

    /**
     * Convert object to json format.
     *
     * @return String
     */
    public String toJson() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String a = gson.toJson(this);
        Log.i("AAAAAAAAAA", a);
        return a;
    }

    @Override
    public String toString() {
        return "FamilyCohesionRecordOB{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
//                ", patientId='" + patientId + '\'' +
//                ", createdAt='" + createdAt + '\'' +
                ", familyMutualAidFreq='" + familyMutualAidFreq + '\'' +
                ", friendshipApprovalFreq='" + friendshipApprovalFreq + '\'' +
                ", familyOnlyTaskFreq='" + familyOnlyTaskFreq + '\'' +
                ", familyOnlyPreferenceFreq='" + familyOnlyPreferenceFreq + '\'' +
                ", freeTimeTogetherFreq='" + freeTimeTogetherFreq + '\'' +
                ", familyProximityPerceptionFreq='" + familyProximityPerceptionFreq + '\'' +
                ", allFamilyTasksFreq='" + allFamilyTasksFreq + '\'' +
                ", familyTasksOpportunityFreq='" + familyTasksOpportunityFreq + '\'' +
                ", familyDecisionSupportFreq='" + familyDecisionSupportFreq + '\'' +
                ", familyUnionRelevanceFreq='" + familyUnionRelevanceFreq + '\'' +
                ", familyCohesionResult=" + familyCohesionResult +
                '}';
    }
}
