package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.model.Measurement;
import br.edu.uepb.nutes.haniot.utils.DateUtils;

/**
 * Adapter from the RecyclerView to list the temperatures.
 *
 * @author Douglas Rafael <douglasrafaelcg@gmail.com>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class TemperatureAdapter extends RecyclerView.Adapter<TemperatureAdapter.ItemViewHolder> {
    private final String LOG = "BluetoothDeviceAdapter";

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private final List<Measurement> items;
    private final Context context;

    private OnItemClickListener itemClckListener;
    private OnLoadMoreListener loadMoreListener;

    /**
     * Used to signal that callback is not required onLoadMore,
     * because data is being listed from the local database.
     */
    private boolean isDataLocal = false;

    /**
     * isLoading - to set the remote loading and complete status to fix back to back load more call
     */
    private boolean isLoading = false;

    /**
     * isMoreDataAvailable - to set whether more data from server available or not.
     * It will prevent useless load more request even after all the server data loaded.
     */
    private boolean isMoreDataAvailable = true;

    /**
     * Contructor.
     *
     * @param context {@link Context}
     * @param items   List<Measurement>
     */
    public TemperatureAdapter(Context context, List<Measurement> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public TemperatureAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new ItemViewHolder(inflater.inflate(R.layout.item_temperature, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder)
            ((ItemViewHolder) holder).bindData(items.get(position));
    }

//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        if (position >= getItemCount() - 1 && isMoreDataAvailable && !isLoading && loadMoreListener != null) {
//            isLoading = true;
//            loadMoreListener.onLoadMore();
//        }
//
//        if (getItemViewType(position) == VIEW_TYPE_ITEM && holder instanceof ItemViewHolder)
//            ((ItemViewHolder) holder).bindData(items.get(position));
//
//    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public TextView value;
        public TextView unit;
        public TextView date;

        public ItemViewHolder(View view) {
            super(view);
            this.view = view;
            value = (TextView) view.findViewById(R.id.measurement_temperature);
            unit = (TextView) view.findViewById(R.id.unit_temperature_textview);
            date = (TextView) view.findViewById(R.id.date_temperature_textview);
        }

        private void bindData(Measurement m) {
            DecimalFormat df = new DecimalFormat(
                    context.getResources().getString(R.string.temperature_format),
                    new DecimalFormatSymbols(Locale.US));

            value.setText(df.format(m.getValue()));
            unit.setText(m.getUnit());
            date.setText(DateUtils.getDatetime(
                    m.getRegistrationDate(),
                    context.getString(R.string.datetime_format))
            );

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClckListener != null) itemClckListener.onItemClick(m);
                }
            });
        }
    }

    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(View view) {
            super(view);
        }
    }

    public void setDataLocal(boolean dataLocal) {
        isDataLocal = dataLocal;
    }

    public void setMoreDataAvailable(boolean moreDataAvailable) {
        isMoreDataAvailable = moreDataAvailable;
    }

    public boolean isLoading() {
        return isLoading;
    }

    /**
     * NotifyDataSetChanged is final method so we can't override it call adapter.notifyDataChanged(); after update the list
     */
    public void notifyDataChanged() {
        notifyDataSetChanged();
        isLoading = false;
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }
}

