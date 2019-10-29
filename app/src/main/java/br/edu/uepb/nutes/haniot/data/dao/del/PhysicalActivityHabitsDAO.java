package br.edu.uepb.nutes.haniot.data.dao.del;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import javax.annotation.Nullable;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.Convert;
import br.edu.uepb.nutes.haniot.data.model.nutritional.PhysicalActivityHabit;
import br.edu.uepb.nutes.haniot.data.objectbox.nutritional.PhysicalActivityHabitOB;
import br.edu.uepb.nutes.haniot.data.objectbox.nutritional.PhysicalActivityHabitOB_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class PhysicalActivityHabitsDAO {
    public static PhysicalActivityHabitsDAO instance;
    private static Box<PhysicalActivityHabitOB> physicalActivityHabitBox;

    private PhysicalActivityHabitsDAO(){}

    public static synchronized PhysicalActivityHabitsDAO getInstance(@Nullable Context context){
        if (instance == null) instance = new PhysicalActivityHabitsDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        physicalActivityHabitBox = boxStore.boxFor(PhysicalActivityHabitOB.class);

        return instance;
    }

//    Search FeedingHabitsRecordOB by id
    public PhysicalActivityHabitOB getFromPatientId(@NonNull String _id){
        return physicalActivityHabitBox.query().equal(PhysicalActivityHabitOB_.patientId,_id).build().findFirst();
    }

//    get all FeedingHabitsRecordOB on database
    public List<PhysicalActivityHabitOB> get(){
        return physicalActivityHabitBox.query().build().find();
    }

//    save FeedingHabitsRecordOB
    public boolean save(@NonNull PhysicalActivityHabit physicalActivityHabit){
        return physicalActivityHabitBox.put(Convert.convertPhysicalActivityHabit(physicalActivityHabit)) > 0;
    }

//    update FeedingHabitsRecordOB
    public boolean update(@NonNull PhysicalActivityHabit physicalActivityHabit){
        if (physicalActivityHabit.getId() == 0){
            PhysicalActivityHabitOB physicalActivityHabitRecordUp = getFromPatientId(physicalActivityHabit.getPatientId());

            if (physicalActivityHabitRecordUp == null) return false;

            physicalActivityHabit.setId(physicalActivityHabitRecordUp.getId());
            //if (feedingHabitsRecord.get_id() == null) feedingHabitsRecord.set_id(feedingHabitsRecordUp.get_id());
        }
        return save(physicalActivityHabit);
    }

//    remove FeedingHabitsRecordOB
    public boolean remove (@NonNull PhysicalActivityHabitOB physicalActivityHabit){
        return physicalActivityHabitBox.query().equal(PhysicalActivityHabitOB_.patientId,physicalActivityHabit.getId()).build().remove() > 0;
    }

}
