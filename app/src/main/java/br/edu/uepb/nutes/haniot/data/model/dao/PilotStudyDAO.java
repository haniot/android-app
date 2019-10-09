package br.edu.uepb.nutes.haniot.data.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import javax.annotation.Nullable;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.model.objectbox.PilotStudy;
import br.edu.uepb.nutes.haniot.data.model.objectbox.PilotStudy_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Represents PilotStudyDAO.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
public class PilotStudyDAO {
    public static PilotStudyDAO instance;
    private static Box<PilotStudy> pilotBox;

    private PilotStudyDAO() {
    }

    public static synchronized PilotStudyDAO getInstance(@Nullable Context context) {
        if (instance == null) instance = new PilotStudyDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        pilotBox = boxStore.boxFor(PilotStudy.class);

        return instance;
    }

    public PilotStudy get(long id) {
        return pilotBox.query().equal(PilotStudy_.id, id).build().findFirst();
    }

    public PilotStudy get(@NonNull String _id) {
        return pilotBox.query().equal(PilotStudy_._id, _id).build().findFirst();
    }

    public List<PilotStudy> list(String userId) {
        return pilotBox.query().equal(PilotStudy_.userId, userId).build().find();
    }

    public boolean save(@NonNull PilotStudy PilotStudy) {
        return pilotBox.put(PilotStudy) > 0;
    }

    public boolean update(@NonNull PilotStudy PilotStudy) {
        if (PilotStudy.getId() == 0) {
            PilotStudy PilotStudyUp = get(PilotStudy.get_id());

            if (PilotStudyUp == null) return false;

            PilotStudy.setId(PilotStudyUp.getId());
            if (PilotStudy.get_id() == null) PilotStudy.set_id(PilotStudyUp.get_id());
        }
        return save(PilotStudy);
    }

    public void clearSelected(@NonNull String userId) {
        for (PilotStudy pilot : list(userId)) {
            pilot.setSelected(false);
            update(pilot);
        }
    }

    public boolean remove(@NonNull PilotStudy PilotStudy) {
        return pilotBox.query().equal(PilotStudy_.id, PilotStudy.getId()).build().remove() > 0;
    }

    public boolean remove(@NonNull String _id) {
        return pilotBox.query().equal(PilotStudy_._id, _id).build().remove() > 0;
    }

    public boolean removeAll(@NonNull String userId) {
        return pilotBox.query().equal(PilotStudy_.userId, userId).build().remove() > 0;
    }
}
