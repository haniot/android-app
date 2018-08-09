package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.edu.uepb.nutes.haniot.R;

import java.util.List;

import br.edu.uepb.nutes.haniot.model.ItemManageMeasurement;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManageMeasurementAdapter extends RecyclerView.Adapter<ManageMeasurementAdapter.ManageMeasurementAdapterViewHolder> {

    private List<ItemManageMeasurement> listaItens;
    private Context context;

    public ManageMeasurementAdapter(List<ItemManageMeasurement> listaItens, Context context){
        this.listaItens = listaItens;
        this.context = context;
    }

    @NonNull
    @Override
    public ManageMeasurementAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Os itens não estavam preenchendo a tela na api 19, inflando dessa forma o bug é resolvido.
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.from(parent.getContext()).inflate(R.layout.view_manage_measurement_line,null,false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(lp);

        ManageMeasurementAdapterViewHolder viewHolderManage = new ManageMeasurementAdapterViewHolder(view,parent.getContext());

        return viewHolderManage;
    }

    public static List<ItemManageMeasurement> teste(){
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ManageMeasurementAdapterViewHolder holder, int position) {

        if (listaItens != null && listaItens.size() > 0 ){
            ItemManageMeasurement item = listaItens.get(position);
            View view = new View(this.context);
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT,0));
            view.setBackgroundColor(android.R.attr.listDivider);

            holder.iconManageMeasurement.setImageDrawable(item.getIcon().getDrawable());
            holder.textNameManageMeasurement.setText(item.getName());
            holder.switchManageMeasurement = item.getSwitchButton();
            holder.divider = view;
        }

    }

    @Override
    public int getItemCount() {
        return listaItens.size();
    }

    public static class ManageMeasurementAdapterViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.iconManageMeasurement)
        ImageView iconManageMeasurement;
        @BindView(R.id.textNameManageMeasurement)
        TextView textNameManageMeasurement;
        @BindView(R.id.switchManageMeasurement)
        SwitchCompat switchManageMeasurement;
        @BindView(R.id.divider)
        View divider;

        public ManageMeasurementAdapterViewHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
