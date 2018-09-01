package br.edu.uepb.nutes.haniot.devices.register;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import br.edu.uepb.nutes.haniot.R;

public class ManageDeviceViewHolder extends RecyclerView.ViewHolder {

    TextView name_device;
    TextView model_device;
    ImageView image_device;

    public ManageDeviceViewHolder(View itemView) {
        super(itemView);
        this.name_device = itemView.findViewById(R.id.name_device);
        this.model_device = itemView.findViewById(R.id.model_device);
        this.image_device = itemView.findViewById(R.id.image_device);
    }

    public TextView getName_device() {
        return name_device;
    }

    public TextView getModel_device() {
        return model_device;
    }

    public ImageView getImage_device() {
        return image_device;
    }


}
