package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.activity.settings.ManagerMeasurementsActivity;
import br.edu.uepb.nutes.haniot.adapter.base.BaseAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

public class GridItemDeviceAdapter extends BaseAdapter<ManagerMeasurementsActivity.ItemDevice> {
    private Context context;
    private SharedPreferences preferences;

    public GridItemDeviceAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View createView(ViewGroup viewGroup, int viewType) {
        this.preferences = context.getSharedPreferences("device_enabled", Context.MODE_PRIVATE);
        return View.inflate(context, R.layout.item_manager_measurement, null);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void showData(RecyclerView.ViewHolder holder, int position, List<ManagerMeasurementsActivity.ItemDevice> itemsList) {
        if (holder instanceof ViewHolder) {
            final ManagerMeasurementsActivity.ItemDevice ig = itemsList.get(position);
            ViewHolder h = (ViewHolder) holder;

            h.imageItem.setImageResource(ig.getImage());
            h.title.setText(ig.getTitle());
            h.device_name.setText(ig.getDevice());
            h.enabled.setChecked(preferences.getBoolean(ig.getKey(), false));
            h.enabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences.Editor editor = preferences.edit();
                    if (isChecked) {
                        editor.putBoolean(ig.getKey(), true);
                        editor.apply();
                    } else {
                        editor.putBoolean(ig.getKey(), false);
                        editor.apply();
                    }
                }
            });
            setAnimation(h.mView, position);
        }
    }

    @Override
    public void clearAnimation(RecyclerView.ViewHolder holder) {
        ((ViewHolder) holder).clearAnimation();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        @BindView(R.id.imageItem)
        ImageView imageItem;
        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.device_name)
        TextView device_name;
        @BindView(R.id.enabled)
        Switch enabled;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            this.mView = itemView.getRootView();
        }

        public void clearAnimation() {
            mView.clearAnimation();
        }
    }


}
