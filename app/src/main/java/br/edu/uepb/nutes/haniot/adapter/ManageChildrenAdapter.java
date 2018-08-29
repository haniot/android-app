package br.edu.uepb.nutes.haniot.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import br.edu.uepb.nutes.haniot.activity.ManageChildrenActivity;
import br.edu.uepb.nutes.haniot.model.Children;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManageChildrenAdapter extends RecyclerView.Adapter<ManageChildrenAdapter.ManageChildrenViewHolder> implements Filterable{

    private List<Children> itemList;
    private List<Children> itemListFiltered;
    private Context context;
    private String searchQuerry = "";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public ManageChildrenAdapter(List<Children> childrenList, Context context){

        this.itemList = childrenList;
        this.itemListFiltered = childrenList;
        this.context = context;
        this.prefs = context.getSharedPreferences("MyPref",Context.MODE_PRIVATE);
        this.editor = prefs.edit();
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

            Children children = itemListFiltered.get(position);
            View divider = new View(this.context);
            divider.setMinimumWidth(2);
            divider.setMinimumHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            divider.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));

            holder.textId.setText(children.get_id());
            holder.textDate.setText(children.getRegisterDate());
            holder.textName.setText(children.getName());
            holder.btnSelect.setOnClickListener( c -> {

                Toast.makeText(context,"Iniciando dashboard com o id: "+children.get_id(),Toast.LENGTH_SHORT).show();

                //Código abaixo funcional!
                Intent it = new Intent(context,MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id__last_child",children.get_id());
                bundle.putString("name__last_child",children.getName());
                it.putExtras(bundle);
                it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                editor.putString("childId",children.get_id()).commit();
                editor.putString("childName",children.getName()).commit();

                context.startActivity(it);

            });
            holder.btnDelete.setOnClickListener( d -> {

                int oldId = searchPositionByChildrenId(children.get_id());
                if (oldId != -1) {
                    removeChild(oldId);
                    if (children.get_id().equals(prefs.getString("childId","null").toString())) {
                        editor.putString("childId", "null").commit();
                        editor.putString("childName","null").commit();
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
                    List<Children> filteredList = new ArrayList<>();
                    for (Children child : itemList){

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
                itemListFiltered = (ArrayList<Children>)results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ManageChildrenViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.textIdNumberChildren)
        TextView textId;
        @BindView(R.id.textRegisterDateNumberChildren)
        TextView textDate;
        @BindView(R.id.btnSelectChildren)
        AppCompatButton btnSelect;
        @BindView(R.id.btnDeleteChild)
        AppCompatButton btnDelete;
        @BindView(R.id.textNameChildValue)
        TextView textName;

        public ManageChildrenViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

    }

}
