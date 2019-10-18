package br.edu.uepb.nutes.haniot.data.model.dao.nutritionalDao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import javax.annotation.Nullable;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.model.objectbox.nutritional.SleepHabitOB;
import br.edu.uepb.nutes.haniot.data.model.objectbox.nutritional.SleepHabitOB_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class SleepHabitsDAO {
    public static SleepHabitsDAO instance;
    private static Box<SleepHabitOB> sleepHabitBox;

    private SleepHabitsDAO(Context context) {
        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        sleepHabitBox = boxStore.boxFor(SleepHabitOB.class);
    }

    public static synchronized SleepHabitsDAO getInstance(@Nullable Context context) {
        if (instance == null)
            instance = new SleepHabitsDAO(context);
        return instance;
    }

    //    Search FeedingHabitsRecordOB by id
    public SleepHabitOB getFromPatientId(@NonNull String _id) {
        return sleepHabitBox.query().equal(SleepHabitOB_.patientId, _id).build().findFirst();
    }

    //    get all FeedingHabitsRecordOB on database
    public List<SleepHabitOB> get() {
        return sleepHabitBox.query().build().find();
    }

    //    save FeedingHabitsRecordOB
    public boolean save(@NonNull SleepHabitOB sleepHabit) {
        return sleepHabitBox.put(sleepHabit) > 0;
    }

    //    update FeedingHabitsRecordOB
    public boolean update(@NonNull SleepHabitOB sleepHabit) {
        if (sleepHabit.getId() == 0) {
            SleepHabitOB sleepHabitRecordUp = getFromPatientId(sleepHabit.getPatientId());

            if (sleepHabitRecordUp == null) return false;

            sleepHabit.setId(sleepHabitRecordUp.getId());
            //if (feedingHabitsRecord.get_id() == null) feedingHabitsRecord.set_id(feedingHabitsRecordUp.get_id());
        }
        return save(sleepHabit);
    }

    //    remove FeedingHabitsRecordOB
    public boolean remove(@NonNull SleepHabitOB sleepHabit) {
        return sleepHabitBox.query().equal(SleepHabitOB_.patientId, sleepHabit.getId()).build().remove() > 0;
    }

}
