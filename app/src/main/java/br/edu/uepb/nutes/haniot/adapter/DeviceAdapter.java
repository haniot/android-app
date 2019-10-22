package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.base.BaseAdapter;
import br.edu.uepb.nutes.haniot.data.model.Device;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter from the RecyclerView to list the available devices.
 *
 * @author Copyright (c) 2019, NUTES/UEPB
 */
public class DeviceAdapter extends BaseAdapter<Device> {
    private final String LOG = "DeviceAdapter";
    private final Context context;

    /**
     * Constructor.
     *
     * @param context {@link Context}
     */
    public DeviceAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View createView(ViewGroup viewGroup, int viewType) {
        return View.inflate(context, R.layout.item_device, null);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void showData(RecyclerView.ViewHolder holder, int position, List<Device> itemsList) {
        if (holder instanceof ViewHolder) {
            final Device device = itemsList.get(position);
            ViewHolder h = (ViewHolder) holder;

            h.name.setText(device.getName());
            h.manufacturer.setText(device.getManufacturer());

            if (device.getImg() != 0) {
                h.image.setImageDrawable(ContextCompat.getDrawable(context, device.getImg()));
            } else {
                h.image.setVisibility(View.GONE);
            }

            /**
             * OnClick Item
             */
            h.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (DeviceAdapter.super.mListener != null)
                        DeviceAdapter.super.mListener.onItemClick(device);
                    Log.w("AAA", "Cliquei");
                }
            });
        }
    }

    @Override
    public void clearAnimation(RecyclerView.ViewHolder holder) {
    }

    /**
     * Class ViewHolder for item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        @BindView(R.id.name_device)
        TextView name;

        @BindView(R.id.manufacturer_device)
        TextView manufacturer;

        @BindView(R.id.image_device)
        ImageView image;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            this.mView = view.getRootView();
        }
    }
}

