package br.edu.uepb.nutes.haniot.data.objectbox.odontological;

import io.objectbox.annotation.Id;

public class FamilyCohesionRecordOB {

    @Id
    private long id;

    private String _id;

    private String patientId;

    private String createdAt;

    private String familyMutualAidFreq;

    private String friendshipApprovalFreq;

    private String familyOnlyTaskFreq;

    private String familyOnlyPreferenceFreq;

    private String freeTimeTogetherFreq;

    private String familyProximityPerceptionFreq;

    private String allFamilyTasksFreq;

    private String familyTasksOpportunityFreq;

    private String familyDecisionSupportFreq;

    private String familyUnionRelevanceFreq;

    private int familyCohesionResult;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String get_Id() {
        return _id;
    }

    public void set_Id(String _id) {
        this._id = _id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

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

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Override
    public String toString() {
        return "FamilyCohesionRecordOB{" +
                "id=" + id +
                ", _id='" + _id + '\'' +
                ", patientId='" + patientId + '\'' +
                ", createdAt='" + createdAt + '\'' +
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
