package br.edu.uepb.nutes.haniot.devices.register;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.model.Device;

public class ManageDeviceAdapter extends RecyclerView.Adapter<ManageDeviceViewHolder> {

    List<Device> devices;

    public ManageDeviceAdapter(List<Device> devices) {
        this.devices = devices;
    }

    @NonNull
    @Override
    public ManageDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_devices, parent, false);

        return new ManageDeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageDeviceViewHolder holder, int position) {
        holder.getName_device().setText(devices.get(position).getName());
        holder.getModel_device().setText(devices.get(position).getModelNumber());
        holder.getImage_device().setImageResource(devices.get(position).getImg_device());
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }
}
