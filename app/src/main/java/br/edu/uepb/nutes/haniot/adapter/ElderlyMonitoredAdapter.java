package br.edu.uepb.nutes.haniot.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.List;

import br.edu.uepb.nutes.haniot.R;
import br.edu.uepb.nutes.haniot.adapter.base.BaseAdapter;
import br.edu.uepb.nutes.haniot.model.Elderly;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Adapter from the RecyclerView to list the Elderlys.
 *
 * @author Douglas Rafael <douglasrafaelcg@gmail.com>
 * @version 1.0
 * @copyright Copyright (c) 2017, NUTES UEPB
 */
public class ElderlyMonitoredAdapter extends BaseAdapter<Elderly> {
    private final String LOG = "ElderlyMonitoredAdapter";
    private final Context context;

    /**
     * Contructor.
     *
     * @param context {@link Context}
     */
    public ElderlyMonitoredAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View createView(ViewGroup viewGroup, int viewType) {
        return View.inflate(context, R.layout.item_elderly, null);
    }

    @Override
    public RecyclerView.ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void showData(RecyclerView.ViewHolder holder, int position, List<Elderly> itemsList) {
        if (holder instanceof ViewHolder) {
            final Elderly e = itemsList.get(position);
            ViewHolder h = (ViewHolder) holder;

            h.name.setText(e.getName());
            h.fallRecords.setText(String.format(context.getResources().getString(R.string.elderly_fall_register), 0));

            // textview fall risk
            h.fallRisk.setVisibility(View.VISIBLE);
            h.fallRisk.setBackgroundResource(R.drawable.rounded_corner);
            GradientDrawable drawableRisk = (GradientDrawable) h.fallRisk.getBackground();
            switch (e.getFallRisk()) {
                case 1:
                    drawableRisk.setColor(ContextCompat.getColor(context, R.color.colorLightGreen));
                    h.fallRisk.setText(context.getResources().getString(R.string.fall_risk_title_low));
                    break;
                case 2:
                    drawableRisk.setColor(ContextCompat.getColor(context, R.color.colorAmber));
                    h.fallRisk.setText(context.getResources().getString(R.string.fall_risk_title_moderate));
                    break;
                case 3:
                    drawableRisk.setColor(ContextCompat.getColor(context, R.color.colorRed));
                    h.fallRisk.setText(context.getResources().getString(R.string.fall_risk_title_high));
                    break;
                default:
                    h.fallRisk.setVisibility(View.GONE);
                    break;
            }

            /**
             * OnClick Item
             */
            h.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ElderlyMonitoredAdapter.super.mListener != null)
                        ElderlyMonitoredAdapter.super.mListener.onItemClick(e);
                }
            });

            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(e.getName());

            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .toUpperCase()
                    .endConfig()
                    .buildRoundRect(e.getName().substring(0, 1), color, 10);

            h.image.setImageDrawable(drawable);

            // call Animation function
            setAnimation(h.mView, position);
        }
    }

    @Override
    public void clearAnimation(RecyclerView.ViewHolder holder) {
        ((ViewHolder) holder).clearAnimation();
    }

    /**
     * Class ViewHolder for item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        @BindView(R.id.elderly_name_textview)
        TextView name;

        @BindView(R.id.elderly_imageView)
        ImageView image;

        @BindView(R.id.elderly_fall_records_textView)
        TextView fallRecords;

        @BindView(R.id.elderly_fall_risk_textView)
        TextView fallRisk;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

            this.mView = view.getRootView();
        }

        public void clearAnimation() {
            mView.clearAnimation();
        }
    }
}

