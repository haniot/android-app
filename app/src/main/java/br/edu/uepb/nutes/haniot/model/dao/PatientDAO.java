package br.edu.uepb.nutes.haniot.model.dao;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import javax.annotation.Nullable;

import br.edu.uepb.nutes.haniot.App;
import br.edu.uepb.nutes.haniot.model.Patient;
import br.edu.uepb.nutes.haniot.model.Patient_;
import io.objectbox.Box;
import io.objectbox.BoxStore;

public class PatientDAO {

    public static PatientDAO instance;
    private static Box<Patient> patientBox;

    private PatientDAO (){}

//    Singleton
    public static synchronized PatientDAO getInstance(@Nullable Context context){
        if (instance == null) instance = new PatientDAO();

        BoxStore boxStore = ((App) context.getApplicationContext()).getBoxStore();
        patientBox = boxStore.boxFor(Patient.class);

        return instance;
    }

//    Search patient by name
    public Patient get(@NonNull String name){
        return patientBox.query().equal(Patient_.firstName,name).build().findFirst();
    }

//    Search patient by id
    public Patient get(@NonNull long id){
        return patientBox.query().equal(Patient_.id,id).build().findFirst();
    }

//    Search patient by id
    public Patient getFromID(@NonNull String _id){
        return patientBox.query().equal(Patient_._id,_id).build().findFirst();
    }

//    get all patient on database
    public List<Patient> get(){
        return patientBox.query().build().find();
    }

//    save patient
    public boolean save(@NonNull Patient patient){
        return patientBox.put(patient) > 0;
    }

//    update patient
    public boolean update(@NonNull Patient patient){
        if (patient.getId() == 0){
            Patient patientUp = get(patient.getFirstName());

            if (patientUp == null) return false;

            patient.setId(patientUp.getId());
            if (patient.get_id() == null) patient.set_id(patientUp.get_id());
        }
        return save(patient);
    }

//    remove patient
    public boolean remove (@NonNull Patient patient){
        return patientBox.query().equal(Patient_.id,patient.getId()).build().remove() > 0;
    }

}
