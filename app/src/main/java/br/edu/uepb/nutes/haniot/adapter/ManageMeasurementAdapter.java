package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.edu.uepb.nutes.haniot.R;

import java.util.List;

import br.edu.uepb.nutes.haniot.activity.settings.Session;
import br.edu.uepb.nutes.haniot.model.ItemManageMeasurement;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ManageMeasurementAdapter extends RecyclerView.Adapter<ManageMeasurementAdapter.ManageMeasurementAdapterViewHolder> {

    public List<ItemManageMeasurement> listaItens;
    private Context context;
    private boolean bloodPressureMonitor;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public ManageMeasurementAdapter(List<ItemManageMeasurement> listaItens, Context context){
        this.listaItens = listaItens;
        this.context = context;
        this.prefs = context.getSharedPreferences("MyPref",Context.MODE_PRIVATE);
        this.editor = prefs.edit();


        bloodPressureMonitor = prefs.getBoolean("blood_pressure_monitor",false);
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

    @Override
    public void onBindViewHolder(@NonNull ManageMeasurementAdapterViewHolder holder, int position) {

        if (listaItens != null && listaItens.size() > 0 ){
            ItemManageMeasurement item = listaItens.get(position);
            View view = new View(this.context);
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT,0));
            view.setBackgroundColor(android.R.attr.listDivider);

            holder.iconManageMeasurement.setImageDrawable(item.getIcon().getDrawable());
            holder.textNameManageMeasurement.setText(item.getName());
            holder.switchManageMeasurement.setId(item.getSwitchButton().getId());
            holder.switchManageMeasurement.setChecked(item.getSwitchButton().isChecked());
            holder.switchManageMeasurement.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) (compoundButton, b) -> {
                switch (holder.switchManageMeasurement.getId()) {
                    case 1:
                        if (b) {
                            editor.putBoolean("blood_pressure_monitor", true).commit();
                        } else {
                            editor.putBoolean("blood_pressure_monitor", false).commit();
                        }
                        break;
                    case 2:
                        if(b){
                            editor.putBoolean("heart_rate_sensor_polar_h10",true).commit();
                        } else {
                            editor.putBoolean("heart_rate_sensor_polar_h10",false).commit();
                        }
                        break;
                    case 3:
                        if(b){
                            editor.putBoolean("heart_rate_sensor_polar_h7",true).commit();
                        } else {
                            editor.putBoolean("heart_rate_sensor_polar_h7",false).commit();
                        }
                        break;
                    case 4:
                        if(b){
                            editor.putBoolean("smart_band",true).commit();
                        } else {
                            editor.putBoolean("smart_band",false).commit();
                        }
                        break;
                    case 5:
                        if(b){
                            editor.putBoolean("ear_thermometer",true).commit();
                        } else {
                            editor.putBoolean("ear_thermometer",false).commit();
                        }
                        break;
                    case 6:
                        if(b){
                            editor.putBoolean("accu_check",true).commit();
                        } else {
                            editor.putBoolean("accu_check",false).commit();
                        }
                        break;
                    case 7:
                        if(b){
                            editor.putBoolean("body_composition_yunmai",true).commit();
                        } else {
                            editor.putBoolean("body_composition_yunmai",false).commit();
                        }
                        break;
                    case 8:
                        if(b){
                            editor.putBoolean("body_composition_omron",true).commit();
                        } else {
                            editor.putBoolean("body_composition_omron",false).commit();
                        }
                        break;
                }
            });
            holder.divider = view;

        }

    }

    @Override
    public int getItemCount() {
        return listaItens.size();
    }

    public static class ManageMeasurementAdapterViewHolder extends RecyclerView.ViewHolder{

        private SharedPreferences prefs;
        private SharedPreferences.Editor editor;

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
