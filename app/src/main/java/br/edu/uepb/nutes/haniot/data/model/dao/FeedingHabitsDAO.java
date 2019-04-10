package br.edu.uepb.nutes.haniot.data.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import javax.annotation.Nullable;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.model.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.FeedingHabitsRecord_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class FeedingHabitsDAO {
    public static FeedingHabitsDAO instance;
    private static Box<FeedingHabitsRecord> feendingHabitsBox;

    private FeedingHabitsDAO(){}

    public static synchronized FeedingHabitsDAO getInstance(@Nullable Context context){
        if (instance == null) instance = new FeedingHabitsDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        feendingHabitsBox = boxStore.boxFor(FeedingHabitsRecord.class);

        return instance;
    }

//    Search FeedingHabitsRecord by id
    public FeedingHabitsRecord getFromPatientId(@NonNull String _id){
        return feendingHabitsBox.query().equal(FeedingHabitsRecord_.patientId,_id).build().findFirst();
    }

//    get all FeedingHabitsRecord on database
    public List<FeedingHabitsRecord> get(){
        return feendingHabitsBox.query().build().find();
    }

//    save FeedingHabitsRecord
    public boolean save(@NonNull FeedingHabitsRecord FeedingHabitsRecord){
        return feendingHabitsBox.put(FeedingHabitsRecord) > 0;
    }

//    update FeedingHabitsRecord
    public boolean update(@NonNull FeedingHabitsRecord feedingHabitsRecord){
        if (feedingHabitsRecord.getId() == 0){
            FeedingHabitsRecord feedingHabitsRecordUp = getFromPatientId(feedingHabitsRecord.getPatientId());

            if (feedingHabitsRecordUp == null) return false;

            feedingHabitsRecord.setId(feedingHabitsRecordUp.getId());
            //if (feedingHabitsRecord.get_id() == null) feedingHabitsRecord.set_id(feedingHabitsRecordUp.get_id());
        }
        return save(feedingHabitsRecord);
    }

//    remove FeedingHabitsRecord
    public boolean remove (@NonNull FeedingHabitsRecord FeedingHabitsRecord){
        return feendingHabitsBox.query().equal(FeedingHabitsRecord_.patientId,FeedingHabitsRecord.getId()).build().remove() > 0;
    }

}
