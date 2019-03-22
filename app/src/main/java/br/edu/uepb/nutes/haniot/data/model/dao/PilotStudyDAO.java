package br.edu.uepb.nutes.haniot.data.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import javax.annotation.Nullable;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.data.model.PilotStudy;
import br.edu.uepb.nutes.haniot.data.model.PilotStudy_;
import br.edu.uepb.nutes.haniot.data.model.PilotStudy;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class PilotStudyDAO {
    public static PilotStudyDAO instance;
    private static Box<PilotStudy> pilotBox;

    private PilotStudyDAO(){}

    public static synchronized PilotStudyDAO getInstance(@Nullable Context context){
        if (instance == null) instance = new PilotStudyDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        pilotBox = boxStore.boxFor(PilotStudy.class);

        return instance;
    }

//    Search PilotStudy by name
    public PilotStudy get(@NonNull String name){
        return pilotBox.query().equal(PilotStudy_.name,name).build().findFirst();
    }

//    Search PilotStudy by id
    public PilotStudy get(@NonNull long id){
        return pilotBox.query().equal(PilotStudy_.idDb,id).build().findFirst();
    }

//    Search PilotStudy by id
    public PilotStudy getFromID(@NonNull String _id){
        return pilotBox.query().equal(PilotStudy_._id,_id).build().findFirst();
    }

//    get all PilotStudy on database
    public List<PilotStudy> get(){
        return pilotBox.query().build().find();
    }

//    save PilotStudy
    public boolean save(@NonNull PilotStudy PilotStudy){
        return pilotBox.put(PilotStudy) > 0;
    }

//    update PilotStudy
    public boolean update(@NonNull PilotStudy PilotStudy){
        if (PilotStudy.getIdDb() == 0){
            PilotStudy PilotStudyUp = get(PilotStudy.getName());

            if (PilotStudyUp == null) return false;

            PilotStudy.setIdDb(PilotStudyUp.getIdDb());
            if (PilotStudy.get_id() == null) PilotStudy.set_id(PilotStudyUp.get_id());
        }
        return save(PilotStudy);
    }

//    remove PilotStudy
    public boolean remove (@NonNull PilotStudy PilotStudy){
        return pilotBox.query().equal(PilotStudy_.idDb,PilotStudy.getIdDb()).build().remove() > 0;
    }

}
