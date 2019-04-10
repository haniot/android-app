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
import br.edu.uepb.nutes.haniot.data.model.MeasurementType;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter from the RecyclerView to list the Blood Pressure.
 *
 * @author Douglas Rafael <douglasrafaelcg@gmail.com>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class BloodPressureAdapter extends BaseAdapter<Measurement> {
    private final String LOG = "TemperatureAdapter";
    private final Context context;

    /**
     * Contructor.
     *
     * @param context {@link Context}
     */
    public BloodPressureAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View createView(ViewGroup viewGroup, int viewType) {
        return View.inflate(context, R.layout.item_blood_pressure, null);
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

            h.systolic.setText(String.valueOf((int) m.getValue()).concat("/"));

            // Relations
            for (Measurement parent : m.getMeasurements()) {
                if (parent.getTypeId() == MeasurementType.BLOOD_PRESSURE_DIASTOLIC)
                    h.diastolic.setText(String.valueOf((int) parent.getValue()));
                else if (parent.getTypeId() == MeasurementType.HEART_RATE)
                    h.pulse.setText(String.valueOf((int) parent.getValue()));
            }
            h.unit.setText(m.getUnit());
            h.dayWeek.setText(DateUtils.formatDate(m.getRegistrationDate(), "EEEE"));
            h.date.setText(DateUtils.formatDate(m.getRegistrationDate(),
                    context.getString(R.string.datetime_format)));

            /**
             * OnClick Item
             */
            h.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (BloodPressureAdapter.super.mListener != null)
                        BloodPressureAdapter.super.mListener.onItemClick(m);
                }
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

        @BindView(R.id.blood_pressure_sys_textview)
        TextView systolic;

        @BindView(R.id.blood_pressure_dia_textview)
        TextView diastolic;

        @BindView(R.id.blood_pressure_pulse_textView)
        TextView pulse;

        @BindView(R.id.unit_blood_pressure_textview)
        TextView unit;

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

