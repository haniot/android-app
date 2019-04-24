package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.base.BaseAdapter;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter from the RecyclerView to list the heart rate.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
public class HeartRateAdapter extends BaseAdapter<Measurement> {
    private final Context context;

    /**
     * Constructor.
     *
     * @param context {@link Context}
     */
    public HeartRateAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View createView(ViewGroup viewGroup, int viewType) {
        return View.inflate(context, R.layout.item_heart_rate, null);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void showData(RecyclerView.ViewHolder holder, int position, List<Measurement> itemsList) {
        if (holder instanceof ViewHolder) {
            final Measurement m = itemsList.get(position);
            ViewHolder h = (ViewHolder) holder;

            h.value.setText(String.format(Locale.getDefault(), "%03d",
                    (int) m.getDataset().get(0).getValue())
            );
            h.unit.setText(m.getUnit());
            h.dayWeek.setText(DateUtils.formatDateISO8601(m.getDataset().get(0).getTimestamp(),
                    "EEEE"));
            h.date.setText(DateUtils.formatDateISO8601(m.getDataset().get(0).getTimestamp(),
                    context.getString(R.string.datetime_format)));
            h.imageHeart.setVisibility(View.VISIBLE);

            // onClick Item
            h.mView.setOnClickListener(v -> {
                if (super.mListener != null) super.mListener.onItemClick(m);
            });

            // call Animation function
            setAnimation(h.mView, position);
        }
    }

    @Override
    public void clearAnimation(RecyclerView.ViewHolder holder) {
        ((ViewHolder) holder).clearAnimation();
    }

    /**
     * Class ViewHolder for item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        @BindView(R.id.heart_rate_textview)
        TextView value;

        @BindView(R.id.heart_rate_unit_textview)
        TextView unit;

        @BindView(R.id.date_measurement_textview)
        TextView date;

        @BindView(R.id.day_week_measurement_textview)
        TextView dayWeek;

        @BindView(R.id.heart_imageview)
        ImageView imageHeart;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            this.mView = view.getRootView();
        }

        public void clearAnimation() {
            mView.clearAnimation();
        }
    }
}

