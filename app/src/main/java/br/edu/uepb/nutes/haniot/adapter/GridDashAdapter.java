package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import br.edu.uepb.nutes.haniot.R;

import java.util.List;

import br.edu.uepb.nutes.haniot.model.ItemGrid;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GridDashAdapter extends RecyclerView.Adapter<GridDashAdapter.ViewHolderGrid> {

    private List<ItemGrid> listaItens;

    public GridDashAdapter(List<ItemGrid> listaItens){
        this.listaItens = listaItens;
    }

    @NonNull
    @Override
    public ViewHolderGrid onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_grid_dash, parent, false);

        ViewHolderGrid viewHolderGrid = new ViewHolderGrid(view,parent.getContext());

        return viewHolderGrid;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderGrid holder, int position) {

        if(listaItens != null && listaItens.size()>0){
            ItemGrid item = listaItens.get(position);

            //this method put the border in items
            holder.layoutItemGrid.setBackgroundResource(R.drawable.border_button_grid);
            holder.imageIten.setImageDrawable(item.getIcon());
            holder.textDescription.setText(item.getDescription());
            holder.textName.setText(item.getName());

            /*this part set the listener for each item in list, the items are being differentiated
            by his names;
             */
            holder.itemView.setOnClickListener( v -> {
                switch (item.getName()){
                    case "YUNMAI Mini 1501":
                        Toast.makeText(v.getContext(),"YUNMAI!",Toast.LENGTH_SHORT).show();
                        break;
                    case "DL8740":
                        Toast.makeText(v.getContext(),"DL8740!",Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public List<ItemGrid> getListaItens(){
        return this.listaItens;
    }

    @Override
    public int getItemCount() {
        return this.listaItens.size();
    }

    public static class ViewHolderGrid extends RecyclerView.ViewHolder{

        @BindView      (R.id.imageIten)
        ImageView imageIten;
        @BindView(R.id.textDescription)
        TextView textDescription;
        @BindView       (R.id.textName)
        TextView textName;
        @BindView (R.id.layoutItemGrid)
        RelativeLayout layoutItemGrid;

        public ViewHolderGrid(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }
    }

}
