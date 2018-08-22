package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.edu.uepb.nutes.haniot.R;

import java.util.List;
import android.os.Handler;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

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
    private Context           context;
    private int     lastPosition = -1;
    private Animation           scale;


    public GridDashAdapter(List<ItemGrid> listaItens,Context context){
        this.listaItens = listaItens;
        this.context = context;
        scale = AnimationUtils.loadAnimation(context, R.anim.click);
    }

    @NonNull
    @Override
    public ViewHolderGrid onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_grid_dash, parent, false);

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
            setAnimation(holder.itemView,position);
            holder.itemView.setOnClickListener( v -> {
                holder.imageIten.startAnimation(scale);
                holder.itemGridProgressBar.setVisibility(View.VISIBLE);
                holder.itemGridProgressBar.setProgressWithAnimation(100);

                switch (item.getName()){
                    case "YUNMAI Mini 1501":

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                // do something...
                                context.startActivity(new Intent(context,BodyCompositionHDPActivity.class));

                            }
                        }, 200);
                        break;

                    case "DL8740":

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                // do something...
                                context.startActivity(new Intent(context,ThermometerActivity.class));

                            }
                        }, 200);
                        break;

                    case "OMRON BP792IT":

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                // do something...
                                context.startActivity(new Intent(context,BloodPressureHDPActivity.class));

                            }
                        }, 200);
                        break;

                    case "Polar H7":

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                // do something...
                                Intent it = new Intent(context,HeartRateActivity.class);
                                it.putExtra(HeartRateActivity.EXTRA_DEVICE_ADDRESS, "00:22:D0:BA:95:80");
                                it.putExtra(HeartRateActivity.EXTRA_DEVICE_INFORMATIONS, new String[]{"POLAR", "H7"});
                                context.startActivity(it);

                            }
                        }, 200);
                        break;

                    case "Polar H10":

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                // do something...
                                Intent intent = new Intent(context,HeartRateActivity.class);
                                intent.putExtra(HeartRateActivity.EXTRA_DEVICE_ADDRESS, "E9:50:60:1F:31:D2");
                                intent.putExtra(HeartRateActivity.EXTRA_DEVICE_INFORMATIONS, new String[]{"POLAR", "H10"});
                                context.startActivity(intent);

                            }
                        }, 200);
                        break;

                    case "MI BAND 2":

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                // do something...
                                context.startActivity(new Intent(context,SmartBandActivity.class));

                            }
                        }, 200);
                        break;

                    case "Performa Connect":

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                // do something...
                                context.startActivity(new Intent(context,GlucoseActivity.class));

                            }
                        }, 200);
                        break;

                    case "OMRON HBF-206ITH":

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                // do something...
                                context.startActivity(new Intent(context,ScaleActivity.class));

                            }
                        }, 200);
                        break;

                }
            });



        }

    }


    public void setAnimation(View view, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);

            anim.setDuration(600);
            view.startAnimation(anim);
            lastPosition = position;
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
        @BindView(R.id.itemGridProgressBar)
        CircularProgressBar itemGridProgressBar;

        public ViewHolderGrid(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }
    }

}
