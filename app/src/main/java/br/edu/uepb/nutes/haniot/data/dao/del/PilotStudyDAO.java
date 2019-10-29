package br.edu.uepb.nutes.haniot.data.dao.del;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import javax.annotation.Nullable;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.objectbox.PilotStudyOB;
import br.edu.uepb.nutes.haniot.data.objectbox.PilotStudyOB_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Represents PilotStudyDAO.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
public class PilotStudyDAO {
    public static PilotStudyDAO instance;
    private static Box<PilotStudyOB> pilotBox;

    private PilotStudyDAO() {
    }

    public static synchronized PilotStudyDAO getInstance(@Nullable Context context) {
        if (instance == null) instance = new PilotStudyDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        pilotBox = boxStore.boxFor(PilotStudyOB.class);

        return instance;
    }

    public PilotStudyOB get(long id) {
        return pilotBox.query().equal(PilotStudyOB_.id, id).build().findFirst();
    }

    public PilotStudyOB get(@NonNull String _id) {
        return pilotBox.query().equal(PilotStudyOB_._id, _id).build().findFirst();
    }

    public List<PilotStudyOB> list(String userId) {
        return pilotBox.query().equal(PilotStudyOB_.userId, userId).build().find();
    }

    public boolean save(@NonNull PilotStudyOB PilotStudy) {
        return pilotBox.put(PilotStudy) > 0;
    }

    public boolean update(@NonNull PilotStudyOB PilotStudy) {
        if (PilotStudy.getId() == 0) {
            PilotStudyOB PilotStudyUp = get(PilotStudy.get_id());

            if (PilotStudyUp == null) return false;

            PilotStudy.setId(PilotStudyUp.getId());
            if (PilotStudy.get_id() == null) PilotStudy.set_id(PilotStudyUp.get_id());
        }
        return save(PilotStudy);
    }

    public void clearSelected(@NonNull String userId) {
        for (PilotStudyOB pilot : list(userId)) {
            pilot.setSelected(false);
            update(pilot);
        }
    }

    public boolean remove(@NonNull PilotStudyOB PilotStudy) {
        return pilotBox.query().equal(PilotStudyOB_.id, PilotStudy.getId()).build().remove() > 0;
    }

    public boolean remove(@NonNull String _id) {
        return pilotBox.query().equal(PilotStudyOB_._id, _id).build().remove() > 0;
    }

    public boolean removeAll(@NonNull String userId) {
        return pilotBox.query().equal(PilotStudyOB_.userId, userId).build().remove() > 0;
    }
}
