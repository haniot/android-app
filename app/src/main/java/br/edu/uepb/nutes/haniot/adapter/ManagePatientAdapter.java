package br.edu.uepb.nutes.haniot.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.MainActivity;
import br.edu.uepb.nutes.haniot.activity.PatientHistoricalActivity;
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.devices.ScaleActivity;
import br.edu.uepb.nutes.haniot.model.Patient;
import br.edu.uepb.nutes.haniot.model.dao.PatientDAO;
import br.edu.uepb.nutes.haniot.utils.Log;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManagePatientAdapter extends RecyclerView.Adapter<ManagePatientAdapter.ManagePatientViewHolder> implements Filterable{

    private List<Patient> itemList;
    private List<Patient> itemListFiltered;
    private Context context;
    private String searchQuerry = "";
    private Session session;

    public ManagePatientAdapter(List<Patient> patientList, Context context){

        this.itemList = patientList;
        this.itemListFiltered = patientList;
        this.context = context;
        session = new Session(context);
    }

    @NonNull
    @Override
    public ManagePatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Os itens não estavam preenchendo a tela na api 19, inflando dessa forma o bug é resolvido.
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.from(parent.getContext()).inflate(R.layout.item_children,null,false);

        ManagePatientViewHolder holder = new ManagePatientViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ManagePatientViewHolder holder, int position) {

        if (itemListFiltered != null && itemListFiltered.size() > 0){

            Patient patient = itemListFiltered.get(position);

            String nameText = String.valueOf(patient.getName().charAt(0));

            holder.textLetter.setText(nameText);
            holder.textId.setText(patient.get_id());
            holder.textId.setTextColor(context.getResources().getColor(R.color.colorBlackGrey));
            holder.textName.setText(patient.getName());
            holder.btnSelect.setOnClickListener( c -> {

                //Código abaixo funcional!
                Intent it = new Intent(context,MainActivity.class);
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
            holder.btnDelete.setOnClickListener( d -> {

                int oldId = searchPositionByChildrenId(patient.get_id());
                if (oldId != -1) {
                    removeItem(patient,oldId);
                    String idLastChild = context.getResources().getString(R.string.id_last_patient);
                    String lastId = session.getString(idLastChild);
                    if (patient.get_id().equals(lastId)) {
                        String id = context.getResources().getString(R.string.id_last_patient);
                        String name = context.getResources().getString(R.string.name_last_patient);
                        session.putString(id,"");
                        session.putString(name,"");
                    }
                    Snackbar snackbar = Snackbar
                            .make(holder.itemView, context.getResources().getString(R.string.patient_removed)
                                    , Snackbar.LENGTH_LONG).setAction(context.getResources()
                                    .getString(R.string.undo), view -> {
                                restoreItem(patient,position);
                            });
                    snackbar.show();
                    if (getItemListFilteredSize() == 0){
                        Toast.makeText(context,context.getResources().getString(R.string.no_data_available),Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.itemView.setOnClickListener( c -> {

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

    public void setSearchQuerry(String searchQuerry) {
        this.searchQuerry = searchQuerry;
    }

    public void restoreItem(Patient patient, int index){

        if (PatientDAO.getInstance(context).save(patient)) {
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

            notifyItemInserted(index);
        }
    }

    public void removeItem(Patient patient, int oldId){

        if (this.itemList.size()> 0) {
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

                Log.d("TESTE","Paciente "+patient.getName()+" removido!");
                this.itemList.remove(patient);
                this.itemListFiltered = this.itemList;
                getFilter().filter(this.searchQuerry);
                notifyItemRemoved(oldId);
                notifyItemRangeChanged(oldId, itemList.size());
            }
        }
    }

    public List<Patient>getData(){
        return this.itemList;
    }

    public Patient getPatient(int index){
        Log.d("TESTE","Size of listFiltered: "+itemListFiltered.size());
        Log.d("TESTE","index: "+index);

        if (itemListFiltered.size()>0 && itemListFiltered.size() >= index){
            return itemListFiltered.get(index);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return itemListFiltered == null ? 0 : itemListFiltered.size();
    }

    public int getItemListFilteredSize(){
        return this.itemListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String querry = constraint.toString();
                if (querry.isEmpty() || querry.equals("")){
                    itemListFiltered = itemList;
                }else{
                    List<Patient> filteredList = new ArrayList<>();
                    for (Patient child : itemList){

                        //O filtro é aplicado aqui
                        if (child.get_id().toLowerCase().contains(querry.toLowerCase())
                                || child.getRegisterDate().toLowerCase().equalsIgnoreCase(querry.toLowerCase())
                                || child.getName().toLowerCase().contains(querry.toLowerCase())){
                            filteredList.add(child);
                        }
                    }
                    itemListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = itemListFiltered;
                filterResults.count = itemListFiltered.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                itemListFiltered = (ArrayList<Patient>)results.values;
                notifyDataSetChanged();
            }
        };
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

        public ManagePatientViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

    }

}
