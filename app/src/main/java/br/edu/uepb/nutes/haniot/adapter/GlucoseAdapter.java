package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.base.BaseAdapter;
import br.edu.uepb.nutes.haniot.data.model.ContextMeasurement;
import br.edu.uepb.nutes.haniot.data.model.ContextMeasurementType;
import br.edu.uepb.nutes.haniot.data.model.ContextMeasurementValueType;
import br.edu.uepb.nutes.haniot.data.model.Measurement;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter from the RecyclerView to list the glucose.
 *
 * @author Douglas Rafael <douglasrafaelcg@gmail.com>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class GlucoseAdapter extends BaseAdapter<Measurement> {
    private final String LOG = "BluetoothDeviceAdapter";
    private final Context context;

    /**
     * Contructor.
     *
     * @param context {@link Context}
     */
    public GlucoseAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View createView(ViewGroup viewGroup, int viewType) {
        return View.inflate(context, R.layout.item_glucose, null);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new GlucoseAdapter.ViewHolder(view);
    }

    @Override
    public void showData(RecyclerView.ViewHolder holder, int position, List<Measurement> itemsList) {
        if (holder instanceof GlucoseAdapter.ViewHolder) {
            final Measurement m = itemsList.get(position);
            GlucoseAdapter.ViewHolder h = (GlucoseAdapter.ViewHolder) holder;

            h.glucose.setText(String.valueOf((int) m.getValue()));
            h.unitGlucose.setText(m.getUnit());
            h.dayWeek.setText(DateUtils.formatDate(m.getRegistrationDate(), "EEEE"));
            h.date.setText(DateUtils.formatDate(
                    m.getRegistrationDate(), context.getString(R.string.datetime_format))
            );

            /**
             * Relations
             */
            for (ContextMeasurement c : m.getContextMeasurements()) {
                if (c.getTypeId() == ContextMeasurementType.GLUCOSE_MEAL)
                    h.glucoseMeal.setText(ContextMeasurementValueType.getString(context, c.getValueId()));
            }

            /**
             * OnClick Item
             */
            h.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (GlucoseAdapter.super.mListener != null)
                        GlucoseAdapter.super.mListener.onItemClick(m);
                }
            });

            // call Animation function
            setAnimation(h.mView, position);
        }
    }

    @Override
    public void clearAnimation(RecyclerView.ViewHolder holder) {
        ((GlucoseAdapter.ViewHolder) holder).clearAnimation();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        @BindView(R.id.glucose_textview)
        TextView glucose;

        @BindView(R.id.unit_glucose_textview)
        TextView unitGlucose;

        @BindView(R.id.glucose_meal_textview)
        TextView glucoseMeal;

        @BindView(R.id.date_measurement_textview)
        TextView date;

        @BindView(R.id.day_week_measurement_textview)
        TextView dayWeek;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }

        public void clearAnimation() {
            mView.clearAnimation();
        }
    }
}
