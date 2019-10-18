package br.edu.uepb.nutes.haniot.data.model.dao.nutritionalDao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import javax.annotation.Nullable;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.model.objectbox.nutritional.FeedingHabitsRecordOB;
import br.edu.uepb.nutes.haniot.data.model.objectbox.nutritional.FeedingHabitsRecordOB_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class FeedingHabitsDAO {
    public static FeedingHabitsDAO instance;
    private static Box<FeedingHabitsRecordOB> feendingHabitsBox;

    private FeedingHabitsDAO(){}

    public static synchronized FeedingHabitsDAO getInstance(@Nullable Context context){
        if (instance == null) instance = new FeedingHabitsDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        feendingHabitsBox = boxStore.boxFor(FeedingHabitsRecordOB.class);

        return instance;
    }

//    Search FeedingHabitsRecordOB by id
    public FeedingHabitsRecordOB getFromPatientId(@NonNull String _id){
        return feendingHabitsBox.query().equal(FeedingHabitsRecordOB_.patientId,_id).build().findFirst();
    }

//    get all FeedingHabitsRecordOB on database
    public List<FeedingHabitsRecordOB> get(){
        return feendingHabitsBox.query().build().find();
    }

//    save FeedingHabitsRecordOB
    public boolean save(@NonNull FeedingHabitsRecordOB FeedingHabitsRecord){
        return feendingHabitsBox.put(FeedingHabitsRecord) > 0;
    }

//    update FeedingHabitsRecordOB
    public boolean update(@NonNull FeedingHabitsRecordOB feedingHabitsRecord){
        if (feedingHabitsRecord.getId() == 0){
            FeedingHabitsRecordOB feedingHabitsRecordUp = getFromPatientId(feedingHabitsRecord.getPatientId());

            if (feedingHabitsRecordUp == null) return false;

            feedingHabitsRecord.setId(feedingHabitsRecordUp.getId());
            //if (feedingHabitsRecord.get_id() == null) feedingHabitsRecord.set_id(feedingHabitsRecordUp.get_id());
        }
        return save(feedingHabitsRecord);
    }

//    remove FeedingHabitsRecordOB
    public boolean remove (@NonNull FeedingHabitsRecordOB FeedingHabitsRecord){
        return feendingHabitsBox.query().equal(FeedingHabitsRecordOB_.patientId,FeedingHabitsRecord.getId()).build().remove() > 0;
    }

}
