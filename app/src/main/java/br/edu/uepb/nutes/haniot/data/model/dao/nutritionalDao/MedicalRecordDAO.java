package br.edu.uepb.nutes.haniot.data.model.dao.nutritionalDao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import javax.annotation.Nullable;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.model.objectbox.nutritional.MedicalRecordOB;
import br.edu.uepb.nutes.haniot.data.model.objectbox.nutritional.MedicalRecordOB_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class MedicalRecordDAO {
    public static MedicalRecordDAO instance;
    private static Box<MedicalRecordOB> medicalRecordBox;

    private MedicalRecordDAO(){}

    public static synchronized MedicalRecordDAO getInstance(@Nullable Context context){
        if (instance == null) instance = new MedicalRecordDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        medicalRecordBox = boxStore.boxFor(MedicalRecordOB.class);

        return instance;
    }

//    Search FeedingHabitsRecordOB by id
    public MedicalRecordOB getFromPatientId(@NonNull String _id){
        return medicalRecordBox.query().equal(MedicalRecordOB_.patientId,_id).build().findFirst();
    }

//    get all FeedingHabitsRecordOB on database
    public List<MedicalRecordOB> get(){
        return medicalRecordBox.query().build().find();
    }

//    save FeedingHabitsRecordOB
    public boolean save(@NonNull MedicalRecordOB FeedingHabitsRecord){
        return medicalRecordBox.put(FeedingHabitsRecord) > 0;
    }

//    update FeedingHabitsRecordOB
    public boolean update(@NonNull MedicalRecordOB medicalRecord){
        if (medicalRecord.getId() == 0){
            MedicalRecordOB medicalRecordRecordUp = getFromPatientId(medicalRecord.getPatientId());

            if (medicalRecordRecordUp == null) return false;

            medicalRecord.setId(medicalRecordRecordUp.getId());
            //if (feedingHabitsRecord.get_id() == null) feedingHabitsRecord.set_id(feedingHabitsRecordUp.get_id());
        }
        return save(medicalRecord);
    }

//    remove FeedingHabitsRecordOB
    public boolean remove (@NonNull MedicalRecordOB medicalRecord){
        return medicalRecordBox.query().equal(MedicalRecordOB_.patientId,medicalRecord.getId()).build().remove() > 0;
    }

}
