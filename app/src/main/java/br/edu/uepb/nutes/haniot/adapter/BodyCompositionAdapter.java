package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.base.BaseAdapter;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.model.MeasurementType;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter from the RecyclerView to list the body composition.
 *
 * @author Douglas Rafael <douglasrafaelcg@gmail.com>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class BodyCompositionAdapter extends BaseAdapter<Measurement> {
    private final String LOG = "BodyCompositionAdapter";
    private final Context context;

    private DecimalFormat decimalFormat;

    public BodyCompositionAdapter(Context context) {
        this.context = context;
        this.decimalFormat = new DecimalFormat(context.getResources().getString(R.string.format_number2), new DecimalFormatSymbols(Locale.US));
    }

    @Override
    public View createView(ViewGroup viewGroup, int viewType) {
        return View.inflate(context, R.layout.item_body_composition, null);
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

            h.bodyMass.setText(decimalFormat.format(m.getValue()));
            h.unitBodyMass.setText(m.getUnit());
            h.dayWeek.setText(DateUtils.formatDate(m.getRegistrationDate(), "EEEE"));
            h.date.setText(DateUtils.formatDate(m.getRegistrationDate(),
                    context.getString(R.string.datetime_format)));

            /**
             * Relations
             */
            for (Measurement parent : m.getMeasurements()) {
                if (parent.getTypeId() == MeasurementType.BODY_FAT)
                    h.bodyFat.setText(decimalFormat.format(parent.getValue()));
            }

            /**
             * OnClick Item
             */
            h.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (BodyCompositionAdapter.super.mListener != null)
                        BodyCompositionAdapter.super.mListener.onItemClick(m);
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
     * Class ViewHolder Item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        @BindView(R.id.measurement_body_mass)
        TextView bodyMass;

        @BindView(R.id.unit_body_mass_textview)
        TextView unitBodyMass;

        @BindView(R.id.measurement_body_fat)
        TextView bodyFat;

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
