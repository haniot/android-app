package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.base.BaseAdapter;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter from the RecyclerView to list the SmartBand.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
public class SmartBandAdapter extends BaseAdapter<Measurement> {
    private final Context context;

    /**
     * Constructor.
     *
     * @param context {@link Context}
     */
    public SmartBandAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View createView(ViewGroup viewGroup, int viewType) {
        return View.inflate(context, R.layout.item_smartband, null);
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

            h.steps.setText(String.valueOf((int) m.getValue()));
            h.dayWeek.setText(DateUtils.convertDateTimeUTCToLocale(m.getTimestamp(), "EEEE"));
            h.date.setText(DateUtils.convertDateTimeUTCToLocale(m.getTimestamp(), context.getString(R.string.datetime_format)));

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

        @BindView(R.id.steps_textview)
        TextView steps;

        @BindView(R.id.distance_textview)
        TextView distance;

        @BindView(R.id.calories_textview)
        TextView calories;

        @BindView(R.id.date_measurement_textview)
        TextView date;

        @BindView(R.id.day_week_measurement_textview)
        TextView dayWeek;

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

