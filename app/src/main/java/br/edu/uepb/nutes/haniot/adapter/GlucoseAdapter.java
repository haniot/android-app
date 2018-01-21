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

    private DecimalFormat decimalFormat;

    /**
     * Contructor.
     *
     * @param context {@link Context}
     */
    public GlucoseAdapter(Context context) {
        this.context = context;
        this.decimalFormat = new DecimalFormat(context.getResources().getString(R.string.format_number1), new DecimalFormatSymbols(Locale.US));
    }

    @Override
    public View createView(ViewGroup viewGroup, int viewType) {
        return View.inflate(context, R.layout.item_temperature, null);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new GlucoseAdapter.ViewHolder(view);
    }

    @Override
    public void showData(RecyclerView.ViewHolder holder, int position, List<Measurement> itemsList) {
        if (holder instanceof TemperatureAdapter.ViewHolder) {
            final Measurement m = itemsList.get(position);
            TemperatureAdapter.ViewHolder h = (TemperatureAdapter.ViewHolder) holder;

            h.value.setText(decimalFormat.format(m.getValue()));
            h.unit.setText(m.getUnit());
            h.dayWeek.setText(DateUtils.formatDate(m.getRegistrationDate(), "EEEE"));
            h.date.setText(DateUtils.formatDate(
                    m.getRegistrationDate(), context.getString(R.string.datetime_format))
            );

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
        ((TemperatureAdapter.ViewHolder) holder).clearAnimation();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Measurement mItem;

        @BindView(R.id.fcmax_textview)
        TextView fcMax;
        @BindView(R.id.date_heart_rate_textview)
        TextView date;
        @BindView(R.id.duration_heart_rate_textview)
        TextView duration;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return "ViewHolder{" +
                    ", fcMax=" + fcMax +
                    ", date=" + date +
                    ", duration=" + duration +
                    '}';
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Measurement item);
    }
}
