package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.MainActivity;
import br.edu.uepb.nutes.haniot.activity.PatientHistoricalActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.Patient;
import br.edu.uepb.nutes.haniot.model.dao.PatientDAO;
import br.edu.uepb.nutes.haniot.utils.Log;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManagePatientAdapter extends RecyclerView.Adapter<ManagePatientAdapter.ManagePatientViewHolder> {

    private List<Patient> itemList;
    private List<Patient> itemListCopy = new ArrayList<>();
    private Context context;
    private String searchQuerry = "";
    private Session session;
    private String t = "TESTE";
    public final int REMOVE_TYPE_NOT_FILTERED = 1;
    public final int REMOVE_TYPE_FILTERED = 2;
    private final int EMPTY_VIEW = 77777;

    public ManagePatientAdapter(List<Patient> patientList, Context context){

        this.itemList = patientList;
        this.itemListCopy.addAll(patientList);
        this.context = context;
        session = new Session(context);
    }

    @NonNull
    @Override
    public ManagePatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Os itens não estavam preenchendo a tela na api 19, inflando dessa forma o bug é resolvido.
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view;

//        test if the list is empty and return the empty view if yes;
        if (viewType == EMPTY_VIEW){
            view = inflater.from(parent.getContext()).inflate(R.layout.patient_empty_view,parent,false);
            ManagePatientViewHolder holder = new ManagePatientViewHolder(view,"");
            return holder;
        }else{
            view = inflater.from(parent.getContext()).inflate(R.layout.item_children,null,false);
            ManagePatientViewHolder holder = new ManagePatientViewHolder(view,"notEmpty");
            return holder;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ManagePatientViewHolder holder, int position) {

        if (!(getItemViewType(position) == EMPTY_VIEW)) {

            Patient patient = itemList.get(position);

            String nameText = String.valueOf(patient.getName().charAt(0));

            holder.textLetter.setText(nameText);
            holder.textId.setText(patient.get_id());
            holder.textId.setTextColor(context.getResources().getColor(R.color.colorBlackGrey));
            holder.textName.setText(patient.getName());
            holder.btnSelect.setOnClickListener(c -> {

                //Código abaixo funcional!
                Intent it = new Intent(context, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(context.getResources().getString(R.string.id_last_patient), patient.get_id());
                bundle.putString(context.getResources().getString(R.string.name_last_patient), patient.getName());
                it.putExtras(bundle);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                String id = context.getResources().getString(R.string.id_last_patient);
                String name = context.getResources().getString(R.string.name_last_patient);
                session.putString(id, patient.get_id());
                session.putString(name, patient.getName());

                context.startActivity(it);

            });
            holder.btnDelete.setOnClickListener(d -> {

                int oldId = searchPositionByChildrenId(patient.get_id());
                if (oldId != -1) {
                    if (this.searchQuerry.isEmpty()) {
                        removeItem(patient, oldId, REMOVE_TYPE_NOT_FILTERED);
                    } else {
                        removeItem(patient, oldId, REMOVE_TYPE_FILTERED);
                    }
                    String idLastChild = context.getResources().getString(R.string.id_last_patient);
                    String lastId = session.getString(idLastChild);
                    if (patient.get_id().equals(lastId)) {
                        String id = context.getResources().getString(R.string.id_last_patient);
                        String name = context.getResources().getString(R.string.name_last_patient);
                        session.putString(id, "");
                        session.putString(name, "");
                    }
                    Snackbar snackbar = Snackbar
                            .make(holder.itemView, context.getResources().getString(R.string.patient_removed)
                                    , Snackbar.LENGTH_LONG).setAction(context.getResources()
                                    .getString(R.string.undo), view -> {
                                restoreItem(patient, position, holder.itemView);
                            });
                    snackbar.show();
                    if (itemListCopy.size() == 0) {
                        Toast.makeText(context, context.getResources().getString(R.string.no_data_available), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.itemView.setOnClickListener(c -> {

                final Intent intent = new Intent(context,PatientHistoricalActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Bundle bundle = ActivityOptionsCompat.makeScaleUpAnimation(holder.itemView,
                        0,
                        0,
                        holder.itemView.getWidth(),
                        holder.itemView.getHeight()).toBundle();
                intent.putExtra("Patient",patient);

                context.startActivity(intent,bundle);

            });

        }
    }

    public int searchPositionByChildrenId(String _id){
        for (int i =0; i<itemList.size(); i++){
            if (itemList.get(i).get_id().equals(_id)){
                return i;
            }
        }

        return -1;
    }

    public int getItemsSize(){
        return this.itemList.size();
    }

    public int searchOldPatientPosition(Patient patient){
        return itemListCopy.lastIndexOf(patient);
    }

    public void setSearchQuerry(String searchQuerry) {
        this.searchQuerry = searchQuerry;
    }

    public String getSearchQuerry() {
        return searchQuerry;
    }

    public void restoreItem(Patient patient, int index, View view){

        if (itemList.isEmpty()){
            notifyDataSetChanged();
        }
        Log.d(t,"1");
        if (PatientDAO.getInstance(context).save(patient)) {
            itemListCopy.add(index,patient);
            itemList.add(index, patient);

            String idLastPatient = context.getResources().getString(R.string.id_last_patient);
            String lastId = session.getString(idLastPatient);

            String idPatient = itemList.get(index).get_id();
            String idLastPatientDeleted = context.getResources().getString(R.string.last_patient_deleted);
            String lastPatient = session.getString(idLastPatientDeleted);

            if (idPatient.equals(lastPatient)) {
                String id = context.getResources().getString(R.string.id_last_patient);
                String name = context.getResources().getString(R.string.name_last_patient);

                session.putString(id, patient.get_id());
                session.putString(name, patient.getName());
            }
            Log.d(t,"2");
            notifyItemInserted(index);
            Log.d(t,"iniciando anim");
            Log.d(t,"3");
        }
    }

    public void removeItem(Patient patient, int oldPosition, final int updateType){

        if (this.itemListCopy.size()> 0) {
            String idPatient = patient.get_id();
            String idLastPatient = context.getResources().getString(R.string.id_last_patient);
            String lastId = session.getString(idLastPatient);

            if (PatientDAO.getInstance(context).remove(patient)) {
            String idLastPatientDeleted = context.getResources().getString(R.string.last_patient_deleted);

                if (idPatient.equals(lastId)){
                    String id = context.getResources().getString(R.string.id_last_patient);
                    String name = context.getResources().getString(R.string.name_last_patient);
                    session.putString(id,"");
                    session.putString(name,"");
                    session.putString(idLastPatientDeleted,idPatient);
                }else{
                    session.putString(idLastPatientDeleted,"");
                }

                this.itemListCopy.remove(patient);
                this.itemList.remove(patient);
                if (updateType == REMOVE_TYPE_FILTERED){
//                    notifyDataSetChanged();
                    notifyItemRemoved(oldPosition);
                    notifyItemRangeChanged(oldPosition, itemList.size());
                    Log.d(t,"tipo filtrado \n ");
                }else {
                    Log.d(t,"Posição: "+oldPosition);
                    notifyItemRemoved(oldPosition);
                    notifyItemRangeChanged(oldPosition, itemListCopy.size());
                }

            }
        }
    }

    public List<Patient>getData(){
        return this.itemList;
    }

    public Patient getPatient(int index){

        if (itemList.size()>0 && itemList.size() >= index){
            return itemList.get(index);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return itemList.size() > 0? itemList.size() : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (itemList.size() == 0) {
            return EMPTY_VIEW;
        }
        return super.getItemViewType(position);
    }

    public void filter(String text){
        if (text.isEmpty()){
            itemList.clear();
            itemList.addAll(itemListCopy);
        } else{
            ArrayList<Patient> result = new ArrayList<>();
            for (Patient patient : itemListCopy){
                if (patient.get_id().toLowerCase().contains(text.toLowerCase())
                        || patient.getRegisterDate().toLowerCase().equalsIgnoreCase(text.toLowerCase())
                        || patient.getName().toLowerCase().contains(text.toLowerCase())){

                    result.add(patient);
                    Log.d("TESTE",patient.getName());
                }
            }
            itemList.clear();
            itemList.addAll(result);
        }
        notifyDataSetChanged();
    }

    public static class ManagePatientViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.textIdNumberChildren)
        TextView textId;
        @BindView(R.id.btnSelectChildren)
        AppCompatButton btnSelect;
        @BindView(R.id.btnDeleteChild)
        AppCompatButton btnDelete;
        @BindView(R.id.textNameChildValue)
        TextView textName;
        @BindView(R.id.textLetter)
        TextView textLetter;
        @BindView(R.id.divChildren)
        View divChildren;

        public ManagePatientViewHolder(View itemView, String type) {
            super(itemView);
//            test if the view is empty, if yes, the items of patient are not binded;
            if (!type.isEmpty())
                ButterKnife.bind(this,itemView);
        }

    }

}
