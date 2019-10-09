package br.edu.uepb.nutes.haniot.data.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import javax.annotation.Nullable;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.model.objectbox.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.objectbox.FeedingHabitsRecord_;
import br.edu.uepb.nutes.haniot.data.model.objectbox.MedicalRecord;
import br.edu.uepb.nutes.haniot.data.model.objectbox.MedicalRecord_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class MedicalRecordDAO {
    public static MedicalRecordDAO instance;
    private static Box<MedicalRecord> medicalRecordBox;

    private MedicalRecordDAO(){}

    public static synchronized MedicalRecordDAO getInstance(@Nullable Context context){
        if (instance == null) instance = new MedicalRecordDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        medicalRecordBox = boxStore.boxFor(MedicalRecord.class);

        return instance;
    }

//    Search FeedingHabitsRecord by id
    public MedicalRecord getFromPatientId(@NonNull String _id){
        return medicalRecordBox.query().equal(MedicalRecord_.patientId,_id).build().findFirst();
    }

//    get all FeedingHabitsRecord on database
    public List<MedicalRecord> get(){
        return medicalRecordBox.query().build().find();
    }

//    save FeedingHabitsRecord
    public boolean save(@NonNull MedicalRecord FeedingHabitsRecord){
        return medicalRecordBox.put(FeedingHabitsRecord) > 0;
    }

//    update FeedingHabitsRecord
    public boolean update(@NonNull MedicalRecord medicalRecord){
        if (medicalRecord.getId() == 0){
            MedicalRecord medicalRecordRecordUp = getFromPatientId(medicalRecord.getPatientId());

            if (medicalRecordRecordUp == null) return false;

            medicalRecord.setId(medicalRecordRecordUp.getId());
            //if (feedingHabitsRecord.get_id() == null) feedingHabitsRecord.set_id(feedingHabitsRecordUp.get_id());
        }
        return save(medicalRecord);
    }

//    remove FeedingHabitsRecord
    public boolean remove (@NonNull MedicalRecord medicalRecord){
        return medicalRecordBox.query().equal(MedicalRecord_.patientId,medicalRecord.getId()).build().remove() > 0;
    }

}
