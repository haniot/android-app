package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.MainActivity;
import br.edu.uepb.nutes.haniot.model.Children;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManageChildrenAdapter extends RecyclerView.Adapter<ManageChildrenAdapter.ManageChildrenViewHolder>{

    private List<Children> itemList;
    private Context context;

    public ManageChildrenAdapter(List<Children> childrenList, Context context){

        this.itemList = childrenList;
        this.context = context;

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

        if (itemList != null && itemList.size() > 0){

            Children children = itemList.get(position);

            holder.textId.setText(children.get_id());
            holder.textDate.setText(children.getRegisterDate());
            holder.btnSelect.setOnClickListener( c -> {

                Toast.makeText(context,"Iniciando dashboard com o id: "+children.get_id(),Toast.LENGTH_SHORT).show();

                //Código abaixo funcional!
//                Intent it = new Intent(context,MainActivity.class);
//                it.putExtra("_id",children.get_id());
//                this.context.startActivity(it);

            });
            holder.btnDelete.setOnClickListener( d -> {

                Toast.makeText(context,"Deletando criança com o id: "+children.get_id(),Toast.LENGTH_SHORT).show();

            });

        }

    }

    @Override
    public int getItemCount() {
        return itemList.size();
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

        public ManageChildrenViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
