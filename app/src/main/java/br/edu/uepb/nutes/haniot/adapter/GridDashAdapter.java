package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import br.edu.uepb.nutes.haniot.R;

import java.util.Collections;
import java.util.List;

import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import br.edu.uepb.nutes.haniot.adapter.base.OnRecyclerViewListener;
import br.edu.uepb.nutes.haniot.model.ItemGrid;
import butterknife.BindView;
import butterknife.ButterKnife;
import br.edu.uepb.nutes.haniot.adapter.base.BaseAdapter;

public class GridDashAdapter extends BaseAdapter<ItemGrid>{
    private Context context;

    public GridDashAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View createView(ViewGroup viewGroup, int viewType) {
        return View.inflate(context, R.layout.item_grid_dash, null);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void showData(RecyclerView.ViewHolder holder, int position, List<ItemGrid> itemsList) {
        if (holder instanceof ViewHolder) {
            final ItemGrid ig = itemsList.get(position);
            ViewHolder h = (ViewHolder) holder;

            h.imageIten.setImageResource(ig.getIcon());
            h.textDescription.setText(ig.getDescription());
            h.textMeasurement.setText(ig.getMeasurementValue());
            h.textMeasurementType.setText(ig.getMeasurementInitials());

            h.botAddMeasurement.setOnClickListener(v -> {
                if (GridDashAdapter.super.mListener != null){
                    GridDashAdapter.super.mListener.onMenuContextClick(h.botAddMeasurement, ig);
                }
            });

            h.mView.setOnClickListener(v -> {
                if (GridDashAdapter.super.mListener != null) {
                    GridDashAdapter.super.mListener.onItemClick(ig);
                }
            });

            setAnimation(h.mView, position);
        }
    }

    public void onItemMoved(int oldPosition, int newPosition){
        super.notifyItemMoved(oldPosition,newPosition);
    }

    @Override
    public void clearAnimation(RecyclerView.ViewHolder holder) {
        ((ViewHolder) holder).clearAnimation();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        /*@BindView(R.id.imageIten)
        ImageView imageIten;
        @BindView(R.id.textDescription)
        TextView textDescription;
        @BindView(R.id.textName)
        TextView textName;
        @BindView(R.id.layoutItemGrid)
        RelativeLayout layoutItemGrid;
        @BindView(R.id.itemGridProgressBar)
        CircularProgressBar itemGridProgressBar;*/

        @BindView(R.id.imageIten)
        ImageView imageIten;
        @BindView(R.id.textDescription)
        TextView textDescription;
        @BindView(R.id.textMeasurement)
        TextView textMeasurement;
        @BindView(R.id.textMeasurementType)
        TextView textMeasurementType;
        @BindView(R.id.botAddMeasurement)
        Button botAddMeasurement;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            this.mView = itemView.getRootView();
        }

        public void clearAnimation() {
            mView.clearAnimation();
        }
    }

}
