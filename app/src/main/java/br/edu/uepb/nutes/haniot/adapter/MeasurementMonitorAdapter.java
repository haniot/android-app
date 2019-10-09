package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.base.BaseAdapter;
import br.edu.uepb.nutes.haniot.data.model.objectbox.MeasurementMonitor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MeasurementMonitorAdapter extends BaseAdapter<MeasurementMonitor> {
    private Context context;

    public MeasurementMonitorAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View createView(ViewGroup viewGroup, int viewType) {
        return View.inflate(context, R.layout.item_measurement_monitor, null);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void showData(RecyclerView.ViewHolder holder, int position, List<MeasurementMonitor> itemsList) {
        if (holder instanceof ViewHolder) {
            final MeasurementMonitor ig = itemsList.get(position);
            ViewHolder h = (ViewHolder) holder;

            h.imageItem.setImageResource(ig.getIcon());
            h.textDescription.setText(ig.getDescription());
            if (ig.getMeasurementValue().isEmpty()) {
                h.lastMeasurement.setText(context.getResources().getString(R.string.empty_measurement));
                h.textMeasurement.setVisibility(View.INVISIBLE);
                h.textMeasurement.setVisibility(View.INVISIBLE);
                h.textMeasurementType.setVisibility(View.INVISIBLE);
                h.texTime.setVisibility(View.INVISIBLE);
                h.timeIcon.setVisibility(View.INVISIBLE);
            } else {
                h.lastMeasurement.setText(context.getResources().getString(R.string.last_measurement));
                h.textMeasurement.setVisibility(View.VISIBLE);
                h.textMeasurement.setVisibility(View.VISIBLE);
                h.textMeasurementType.setVisibility(View.VISIBLE);
                h.texTime.setVisibility(View.VISIBLE);
                h.timeIcon.setVisibility(View.VISIBLE);
                h.textMeasurement.setText(ig.getMeasurementValue());
                h.textMeasurementType.setText(ig.getMeasurementInitials());
                h.texTime.setText(ig.getTime());
            }

            if (ig.getStatus() == 0) {
                h.status.setColorFilter(ContextCompat.getColor(context, R.color.connected),
                        PorterDuff.Mode.SRC_IN);
                h.status.setVisibility(View.VISIBLE);

            } else if (ig.getStatus() == 1) {
                h.status.setColorFilter(ContextCompat.getColor(context, R.color.disconnected),
                        PorterDuff.Mode.SRC_IN);
                h.status.setVisibility(View.VISIBLE);

            } else if (ig.getStatus() == 2) {
                h.status.setColorFilter(ContextCompat.getColor(context, R.color.noRegister),
                        PorterDuff.Mode.SRC_IN);
                h.status.setVisibility(View.VISIBLE);

            } else if (ig.getStatus() == 3) {
                h.status.setVisibility(View.INVISIBLE);
                h.progressBar.setIndeterminate(true);
            }
            h.botAddMeasurement.setOnClickListener(v -> {
                if (MeasurementMonitorAdapter.super.mListener != null) {
                    MeasurementMonitorAdapter.super.mListener.onMenuContextClick(h.botAddMeasurement, ig);
                }
            });

            h.mView.setOnClickListener(v -> {
                if (MeasurementMonitorAdapter.super.mListener != null) {
                    MeasurementMonitorAdapter.super.mListener.onItemClick(ig);
                }
            });

            setAnimation(h.mView, position);
        }
    }

    @Override
    public void clearAnimation(RecyclerView.ViewHolder holder) {
        ((ViewHolder) holder).clearAnimation();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        @BindView(R.id.imageItem)
        ImageView imageItem;
        @BindView(R.id.textDescription)
        TextView textDescription;
        @BindView(R.id.textMeasurement)
        TextView textMeasurement;
        @BindView(R.id.text_last_measurement)
        TextView lastMeasurement;
        @BindView(R.id.textMeasurementType)
        TextView textMeasurementType;
        @BindView(R.id.text_time_measurement)
        TextView texTime;
        @BindView(R.id.btn_add_measurement)
        TextView botAddMeasurement;
        @BindView(R.id.status)
        ImageView status;
        @BindView(R.id.time_measuerement)
        ImageView timeIcon;
        @BindView(R.id.receiving_data)
        ProgressBar progressBar;

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