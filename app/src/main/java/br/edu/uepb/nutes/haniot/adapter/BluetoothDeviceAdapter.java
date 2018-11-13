package br.edu.uepb.nutes.haniot.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter from the RecyclerView to list the devices found on the scanner.
 *
 * @author Douglas Rafael <douglasrafaelcg@gmail.com>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder> {
    private final String LOG = "BluetoothDeviceAdapter";

    private final List<BluetoothDevice> mValues;
    private final OnItemClickListener mListener;

    public BluetoothDeviceAdapter(List<BluetoothDevice> items, OnItemClickListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.i(LOG, "onCreateViewHolder()");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetoothdevice, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.i(LOG, "onBindViewHolder()");
        holder.mItem = mValues.get(position);
        holder.mName.setText(mValues.get(position).getName());
        holder.mAddress.setText(mValues.get(position).getAddress());
        holder.mState.setText(bondStateToString(mValues.get(position).getBondState()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        int size = mValues.size();

        return size;
    }

    public void addItem(BluetoothDevice device, int position) {
        mValues.add(device);
        notifyItemInserted(position);
    }

    public void removeItem(BluetoothDevice device, int position) {
        mValues.remove(device);
        notifyItemRemoved(position);
    }

    public String bondStateToString(int state) {
        String result = "";

        if (mListener instanceof Context) {
            Context c = (Context) mListener;

            switch (state) {
                case BluetoothDevice.BOND_NONE:
                    result = c.getString(R.string.device_not_bonded);
                    break;
                case BluetoothDevice.BOND_BONDING:
                    result = c.getString(R.string.device_bonding);
                    break;
                case BluetoothDevice.BOND_BONDED:
                    result = c.getString(R.string.device_bonded);
                    break;
            }
        }
        return result;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public BluetoothDevice mItem;

        @BindView(R.id.device_name)
        TextView mName;
        @BindView(R.id.device_mac)
        TextView mAddress;
        @BindView(R.id.device_state)
        TextView mState;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mName.getText() + "'" + " '" + mAddress.getText() + "'";
        }
    }

    public interface OnItemClickListener {
        void onItemClick(BluetoothDevice item);
    }

}
