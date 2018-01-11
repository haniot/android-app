package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.utils.DateUtils;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter from the RecyclerView to list the temperatures.
 *
 * @author Douglas Rafael <douglasrafaelcg@gmail.com>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class BodyCompositionAdapter extends RecyclerView.Adapter<BodyCompositionAdapter.ViewHolder> {
    private final String LOG = "BluetoothDeviceAdapter";

    private final List<Measurement> mValues;
    private final OnItemClickListener mListener;
    private final Context context;

    public BodyCompositionAdapter(List<Measurement> items, OnItemClickListener listener, Context context) {
        mValues = items;
        mListener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_body_composition, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        holder.mItem = mValues.get(position);
//
//        DecimalFormat df = new DecimalFormat(context.getResources().getString(R.string.weight_format));
//
//        holder.weight.setText(df.format(mValues.get(position).getValue()));
//        holder.bodyFat.setText(df.format(mValues.get(position).getBodyFat()));
//        holder.date.setText(DateUtils.getDatetime(mValues.get(position).getRegistrationTime(), context.getString(R.string.datetime_format)));
//        holder.unit.setText(mValues.get(position).getUnit());
//
//        holder.mView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mListener.onItemClick(holder.mItem);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mValues == null ? 0 : mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Measurement mItem;

        @BindView(R.id.measurement_weight)
        TextView weight;
        @BindView(R.id.measurement_bodyfat)
        TextView bodyFat;
        @BindView(R.id.date_weight_textview)
        TextView date;
        @BindView(R.id.unit_weight_textview)
        TextView unit;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return "ViewHolder{" +
                    "mView=" + mView +
                    ", mItem=" + mItem +
                    ", weight=" + weight +
                    ", bodyFat=" + bodyFat +
                    ", date=" + date +
                    ", unit=" + unit +
                    '}';
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Measurement item);
    }
}
