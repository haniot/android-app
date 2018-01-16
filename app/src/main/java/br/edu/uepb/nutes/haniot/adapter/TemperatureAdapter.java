package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

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
public class TemperatureAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String LOG = "BluetoothDeviceAdapter";
    private final int TOTAL_PER_PAGE = 3;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private final List<Measurement> items;
    private final Context context;
    private final OnItemClickListener itemClckListener;
    private final OnLoadMoreListener loadMoreListener;

    private int lastVisibleItem, totalItems;
    private boolean isLoading;

    /**
     * Contructor.
     *
     * @param recyclerView {@link RecyclerView}
     * @param items List<Measurement>
     * @param itemClckListener {@link OnItemClickListener}
     * @param loadMoreListener {@link OnLoadMoreListener}
     * @param context {@link Context}
     */
    public TemperatureAdapter(RecyclerView recyclerView, List<Measurement> items, OnItemClickListener itemClckListener, OnLoadMoreListener loadMoreListener, Context context) {
        this.items = items;
        this.context = context;
        this.itemClckListener = itemClckListener;
        this.loadMoreListener = loadMoreListener;

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItems <= (lastVisibleItem + TOTAL_PER_PAGE)) {
                    if (loadMoreListener != null) {
                        loadMoreListener.onLoadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING) {
            // Loading
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_temperature, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            Measurement m = items.get(position);
            ItemViewHolder viewHolder = (ItemViewHolder) holder;

            DecimalFormat df = new DecimalFormat(
                    context.getResources().getString(R.string.temperature_format),
                    new DecimalFormatSymbols(Locale.US));

            viewHolder.value.setText(df.format(m.getValue()));
            viewHolder.unit.setText(m.getUnit());
            viewHolder.date.setText(DateUtils.getDatetime(
                    m.getRegistrationDate(),
                    context.getString(R.string.datetime_format))
            );

            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClckListener != null) itemClckListener.onItemClick(viewHolder.item);
                }
            });
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setVisibility(View.VISIBLE);
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    public void cancelLoading() {
        isLoading = false;
    }

    /**
     *
     */
    public class LoadingViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_progressbar)
        ProgressBar progressBar;

        public LoadingViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    /**
     *
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public Measurement item;

        @BindView(R.id.measurement_temperature)
        TextView value;
        @BindView(R.id.date_temperature_textview)
        TextView date;
        @BindView(R.id.unit_temperature_textview)
        TextView unit;

        public ItemViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return "ItemViewHolder{" +
                    "view=" + view +
                    ", item=" + item +
                    ", value=" + value +
                    ", date=" + date +
                    ", unit=" + unit +
                    '}';
        }
    }
}

