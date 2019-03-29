package br.edu.uepb.nutes.haniot.data.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import javax.annotation.Nullable;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.model.FeedingHabitsRecord;
import br.edu.uepb.nutes.haniot.data.model.FeedingHabitsRecord_;
import br.edu.uepb.nutes.haniot.data.model.PhysicalActivityHabit;
import br.edu.uepb.nutes.haniot.data.model.PhysicalActivityHabit_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class PhysicalActivityHabitsDAO {
    public static PhysicalActivityHabitsDAO instance;
    private static Box<PhysicalActivityHabit> physicalActivityHabitBox;

    private PhysicalActivityHabitsDAO(){}

    public static synchronized PhysicalActivityHabitsDAO getInstance(@Nullable Context context){
        if (instance == null) instance = new PhysicalActivityHabitsDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        physicalActivityHabitBox = boxStore.boxFor(PhysicalActivityHabit.class);

        return instance;
    }

//    Search FeedingHabitsRecord by id
    public PhysicalActivityHabit getFromPatientId(@NonNull String _id){
        return physicalActivityHabitBox.query().equal(PhysicalActivityHabit_.patientId,_id).build().findFirst();
    }

//    get all FeedingHabitsRecord on database
    public List<PhysicalActivityHabit> get(){
        return physicalActivityHabitBox.query().build().find();
    }

//    save FeedingHabitsRecord
    public boolean save(@NonNull PhysicalActivityHabit physicalActivityHabit){
        return physicalActivityHabitBox.put(physicalActivityHabit) > 0;
    }

//    update FeedingHabitsRecord
    public boolean update(@NonNull PhysicalActivityHabit physicalActivityHabit){
        if (physicalActivityHabit.getId() == 0){
            PhysicalActivityHabit physicalActivityHabitRecordUp = getFromPatientId(physicalActivityHabit.getPatientId());

            if (physicalActivityHabitRecordUp == null) return false;

            physicalActivityHabit.setId(physicalActivityHabitRecordUp.getId());
            //if (feedingHabitsRecord.get_id() == null) feedingHabitsRecord.set_id(feedingHabitsRecordUp.get_id());
        }
        return save(physicalActivityHabit);
    }

//    remove FeedingHabitsRecord
    public boolean remove (@NonNull PhysicalActivityHabit physicalActivityHabit){
        return physicalActivityHabitBox.query().equal(PhysicalActivityHabit_.patientId,physicalActivityHabit.getId()).build().remove() > 0;
    }

}
