package br.edu.uepb.nutes.haniot.data.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import javax.annotation.Nullable;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.model.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.FeedingHabitsRecord_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class FeendingHabitsDAO {
    public static FeendingHabitsDAO instance;
    private static Box<FeedingHabitsRecord> feendingHabitsBox;

    private FeendingHabitsDAO(){}

    public static synchronized FeendingHabitsDAO getInstance(@Nullable Context context){
        if (instance == null) instance = new FeendingHabitsDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        feendingHabitsBox = boxStore.boxFor(FeedingHabitsRecord.class);

        return instance;
    }

//    Search FeedingHabitsRecord by id
//    public FeedingHabitsRecord get(@NonNull long id){
//        return feendingHabitsBox.query().equal(FeedingHabitsRecord_.idDb,id).build().findFirst();
//    }

//    Search FeedingHabitsRecord by id
    public FeedingHabitsRecord getFromID(@NonNull String _id){
        return feendingHabitsBox.query().equal(FeedingHabitsRecord_._id,_id).build().findFirst();
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
    public boolean update(@NonNull FeedingHabitsRecord FeedingHabitsRecord){
//        if (FeedingHabitsRecord.getIdDb() == 0){
//            FeedingHabitsRecord FeedingHabitsRecordUp = get(FeedingHabitsRecord.getName());
//
//            if (FeedingHabitsRecordUp == null) return false;
//
//            FeedingHabitsRecord.setIdDb(FeedingHabitsRecordUp.getIdDb());
//            if (FeedingHabitsRecord.get_id() == null) FeedingHabitsRecord.set_id(FeedingHabitsRecordUp.get_id());
//        }
        return save(FeedingHabitsRecord);
    }

//    remove FeedingHabitsRecord
//    public boolean remove (@NonNull FeedingHabitsRecord FeedingHabitsRecord){
//        return feendingHabitsBox.query().equal(FeedingHabitsRecord_.idDb,FeedingHabitsRecord.getIdDb()).build().remove() > 0;
//    }

}
