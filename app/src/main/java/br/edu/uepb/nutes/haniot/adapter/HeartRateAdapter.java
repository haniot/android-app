package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.model.MeasurementHeartRate;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter from the RecyclerView to list the heart rate.
 *
 * @author Douglas Rafael <douglasrafaelcg@gmail.com>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class HeartRateAdapter extends RecyclerView.Adapter<HeartRateAdapter.ViewHolder> {
    private final String LOG = "BluetoothDeviceAdapter";
    private final int EMPTY_VIEW = 10;

    private final List<MeasurementHeartRate> mValues;
    private final OnItemClickListener mListener;
    private final Context context;

    public HeartRateAdapter(List<MeasurementHeartRate> items, OnItemClickListener listener, Context context) {
        mValues = items;
        mListener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == EMPTY_VIEW) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_view, parent, false);
            new ViewHolder(view);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_heart_rate, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.mItem = mValues.get(position);
        holder.fcMax.setText(String.format("%03d", mValues.get(position).getFcMaximum()));
        holder.date.setText(DateUtils.getDatetime(mValues.get(position).getRegistrationTime(), context.getString(R.string.datetime_format)));
        holder.duration.setText(DateUtils.getDatetime(mValues.get(position).getDurationTime(), context.getString(R.string.time_format_simple)));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues == null ? 0 : mValues.size();
    }

    public void addItem(MeasurementHeartRate m, int position) {
        mValues.add(m);
        notifyItemInserted(position);
    }

    public void removeItem(MeasurementHeartRate m, int position) {
        mValues.remove(m);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public MeasurementHeartRate mItem;

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
        void onItemClick(MeasurementHeartRate item);
    }
}
