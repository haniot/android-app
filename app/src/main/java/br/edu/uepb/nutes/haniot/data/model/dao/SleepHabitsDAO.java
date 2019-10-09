package br.edu.uepb.nutes.haniot.data.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import javax.annotation.Nullable;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.model.objectbox.SleepHabit;
import br.edu.uepb.nutes.haniot.data.model.objectbox.SleepHabit_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class SleepHabitsDAO {
    public static SleepHabitsDAO instance;
    private static Box<SleepHabit> sleepHabitBox;

    private SleepHabitsDAO(){}

    public static synchronized SleepHabitsDAO getInstance(@Nullable Context context){
        if (instance == null) instance = new SleepHabitsDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        sleepHabitBox = boxStore.boxFor(SleepHabit.class);

        return instance;
    }

//    Search FeedingHabitsRecord by id
    public SleepHabit getFromPatientId(@NonNull String _id){
        return sleepHabitBox.query().equal(SleepHabit_.patientId,_id).build().findFirst();
    }

//    get all FeedingHabitsRecord on database
    public List<SleepHabit> get(){
        return sleepHabitBox.query().build().find();
    }

//    save FeedingHabitsRecord
    public boolean save(@NonNull SleepHabit sleepHabit){
        return sleepHabitBox.put(sleepHabit) > 0;
    }

//    update FeedingHabitsRecord
    public boolean update(@NonNull SleepHabit sleepHabit){
        if (sleepHabit.getId() == 0){
            SleepHabit sleepHabitRecordUp = getFromPatientId(sleepHabit.getPatientId());

            if (sleepHabitRecordUp == null) return false;

            sleepHabit.setId(sleepHabitRecordUp.getId());
            //if (feedingHabitsRecord.get_id() == null) feedingHabitsRecord.set_id(feedingHabitsRecordUp.get_id());
        }
        return save(sleepHabit);
    }

//    remove FeedingHabitsRecord
    public boolean remove (@NonNull SleepHabit sleepHabit){
        return sleepHabitBox.query().equal(SleepHabit_.patientId,sleepHabit.getId()).build().remove() > 0;
    }

}
