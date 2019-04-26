package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.base.BaseAdapter;
import br.edu.uepb.nutes.haniot.data.model.ItemEvaluation;
import br.edu.uepb.nutes.haniot.data.model.MeasurementMonitor;
import butterknife.BindView;
import butterknife.ButterKnife;

public class EvaluationAdapter extends BaseAdapter<ItemEvaluation> {
    private Context context;

    public EvaluationAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View createView(ViewGroup viewGroup, int viewType) {
        return View.inflate(context, R.layout.item_evaluation, null);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void showData(RecyclerView.ViewHolder holder, int position, List<ItemEvaluation> itemsList) {
        if (holder instanceof ViewHolder) {
            final ItemEvaluation ig = itemsList.get(position);
            ViewHolder h = (ViewHolder) holder;

            h.imageItem.setImageResource(ig.getIcon());
            h.textDescription.setText(ig.getTitle());
            if (ig.getValueMeasurement().isEmpty()) {
                h.textMeasurement.setVisibility(View.GONE);
                h.textMeasurement.setVisibility(View.GONE);
                h.textMeasurementType.setVisibility(View.GONE);
                h.texTime.setVisibility(View.INVISIBLE);
            } else {
                h.textMeasurement.setVisibility(View.VISIBLE);
                h.textMeasurement.setVisibility(View.VISIBLE);
                h.textMeasurementType.setVisibility(View.VISIBLE);
                h.texTime.setVisibility(View.VISIBLE);
                h.textMeasurement.setText(ig.getValueMeasurement());
                h.textMeasurementType.setText(ig.getUnitMeasurement());
                h.texTime.setText(ig.getTime());
            }

            h.mView.setOnClickListener(v -> {
                if (EvaluationAdapter.super.mListener != null) {
                    EvaluationAdapter.super.mListener.onItemClick(ig);
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
        @BindView(R.id.textMeasurementType)
        TextView textMeasurementType;
        @BindView(R.id.text_time_measurement)
        TextView texTime;
        @BindView(R.id.quiz_text)
        TextView QuizText;
        @BindView(R.id.check_item)
        CheckBox checkItem;

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