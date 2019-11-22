package br.edu.uepb.nutes.haniot.data.objectbox.odontological;

import br.edu.uepb.nutes.haniot.data.objectbox.ActivityHabitsRecordOB;
import io.objectbox.annotation.Entity;

@Entity
public class FamilyCohesionRecordOB  extends ActivityHabitsRecordOB {

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

    public FamilyCohesionRecordOB() {}

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

    @Override
    public String toString() {
        return "FamilyCohesionRecordOB{" +
                super.toString() +
                "familyMutualAidFreq='" + familyMutualAidFreq + '\'' +
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
