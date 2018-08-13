package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.content.Intent;
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

import br.edu.uepb.nutes.haniot.devices.GlucoseActivity;
import br.edu.uepb.nutes.haniot.devices.HeartRateActivity;
import br.edu.uepb.nutes.haniot.devices.ScaleActivity;
import br.edu.uepb.nutes.haniot.devices.SmartBandActivity;
import br.edu.uepb.nutes.haniot.devices.ThermometerActivity;
import br.edu.uepb.nutes.haniot.devices.hdp.BloodPressureHDPActivity;
import br.edu.uepb.nutes.haniot.devices.hdp.BodyCompositionHDPActivity;
import br.edu.uepb.nutes.haniot.model.ItemGrid;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GridDashAdapter extends RecyclerView.Adapter<GridDashAdapter.ViewHolderGrid> {

    private List<ItemGrid> listaItens;
    private Context context;

    public GridDashAdapter(List<ItemGrid> listaItens,Context context){
        this.listaItens = listaItens;
        this.context = context;
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
                        context.startActivity(new Intent(context,BodyCompositionHDPActivity.class));
                        break;
                    case "DL8740":
                        context.startActivity(new Intent(context,ThermometerActivity.class));
                        break;
                    case "OMRON BP792IT":
                        context.startActivity(new Intent(context,BloodPressureHDPActivity.class));
                        break;
                    case "Polar H7":
                        Intent it = new Intent(context,HeartRateActivity.class);
                        it.putExtra(HeartRateActivity.EXTRA_DEVICE_ADDRESS, "00:22:D0:BA:95:80");
                        it.putExtra(HeartRateActivity.EXTRA_DEVICE_INFORMATIONS, new String[]{"POLAR", "H7"});
                        context.startActivity(it);
                        break;
                    case "Polar H10":
                        Intent intent = new Intent(context,HeartRateActivity.class);
                        intent.putExtra(HeartRateActivity.EXTRA_DEVICE_ADDRESS, "E9:50:60:1F:31:D2");
                        intent.putExtra(HeartRateActivity.EXTRA_DEVICE_INFORMATIONS, new String[]{"POLAR", "H10"});
                        context.startActivity(intent);
                        break;
                    case "MI BAND 2":
                        context.startActivity(new Intent(context,SmartBandActivity.class));
                        break;
                    case "Performa Connect":
                        context.startActivity(new Intent(context,GlucoseActivity.class));
                        break;
                    case "OMRON HBF-206ITH":
                        context.startActivity(new Intent(context,ScaleActivity.class));
                        break;
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
