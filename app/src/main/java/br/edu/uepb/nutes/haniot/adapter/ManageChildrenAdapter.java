package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
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
import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.Patient;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManageChildrenAdapter extends RecyclerView.Adapter<ManageChildrenAdapter.ManageChildrenViewHolder> implements Filterable{

    private List<Patient> itemList;
    private List<Patient> itemListFiltered;
    private Context context;
    private String searchQuerry = "";
    private Session session;

    public ManageChildrenAdapter(List<Patient> patientList, Context context){

        this.itemList = patientList;
        this.itemListFiltered = patientList;
        this.context = context;
        session = new Session(context);
    }

    @NonNull
    @Override
    public ManageChildrenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Os itens não estavam preenchendo a tela na api 19, inflando dessa forma o bug é resolvido.
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.from(parent.getContext()).inflate(R.layout.item_children,null,false);

        ManageChildrenViewHolder holder = new ManageChildrenViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ManageChildrenViewHolder holder, int position) {

        if (itemListFiltered != null && itemListFiltered.size() > 0){

            Patient patient = itemListFiltered.get(position);

//            if(position == getItemCount()-1){
//                holder.divChildren.setBackgroundColor(Color.TRANSPARENT);
//            }

            String nameText = String.valueOf(patient.getName().charAt(0));

            holder.textLetter.setText(nameText);
            holder.textId.setText(patient.get_id());
            holder.textId.setTextColor(context.getResources().getColor(R.color.colorBlackGrey));
            holder.textName.setText(patient.getName());
            holder.btnSelect.setOnClickListener( c -> {

                //Código abaixo funcional!
                Intent it = new Intent(context,MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(context.getResources().getString(R.string.id_last_child), patient.get_id());
                bundle.putString(context.getResources().getString(R.string.name_last_child), patient.getName());
                it.putExtras(bundle);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                String id = context.getResources().getString(R.string.id_last_child);
                String name = context.getResources().getString(R.string.name_last_child);
                session.putString(id, patient.get_id());
                session.putString(name, patient.getName());

                context.startActivity(it);

            });
            holder.btnDelete.setOnClickListener( d -> {

                int oldId = searchPositionByChildrenId(patient.get_id());
                if (oldId != -1) {
                    removeChild(oldId);
                    String idLastChild = context.getResources().getString(R.string.id_last_child);
                    String lastId = session.getString(idLastChild);
                    if (patient.get_id().equals(lastId)) {
                        String id = context.getResources().getString(R.string.id_last_child);
                        String name = context.getResources().getString(R.string.name_last_child);
                        session.putString(id,"");
                        session.putString(name,"");
                    }
                    if (getItemListFilteredSize() == 0){
                        Toast.makeText(context,context.getResources().getString(R.string.no_data_available),Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    //Delete child;
    private void removeChild(int position){
        if (this.itemList.size()> 0) {
            this.itemList.remove(position);
            this.itemListFiltered = this.itemList;
            getFilter().filter(this.searchQuerry);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,itemList.size());
        }
    }

    private int searchPositionByChildrenId(String _id){
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

    public static class ManageChildrenViewHolder extends RecyclerView.ViewHolder{

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

        public ManageChildrenViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

    }

}
